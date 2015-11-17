package report;

import core.Coord;
import core.DTNHost;
import core.Settings;
import core.SettingsError;
import core.SimScenario;

import java.util.ArrayList;
import java.util.List;

/**
 * Sampling report that counts the number of nodes in grid over the
 * simulation area. Output format is: G_x G_y count_1, count_2, ...
 * Where G_x and G_y are the coordinates of the grid square [0, 1, ...] and
 * count_n is the count during the nth sample.
 *
 * @author teemuk
 */
public class NodeDensityReport
extends SamplingReport {
	//========================================================================//
	// Settings
	//========================================================================//
	/** Number of divisions along the x-axis ({@value}). */
	public static final String X_COUNT_SETTING = "xCount";
	/** Number of divisions along the y-axis ({@value}). */
	public static final String Y_COUNT_SETTING = "yCount";
	/** Boolean setting to output gnuplot file ({@value}). */
	public static final String GNUPLOT_SETTING = "outputGnuplot";

	/** Default number of divisions along the x-axis ({@value}). */
	public static final int DEFAULT_X_COUNT = 10;
	/** Default number of divisions along the y-axis ({@value}). */
	public static final int DEFAULT_Y_COUNT = 10;
	/** Default value for outputting gnuplot instead of raw data ({@value}). */
	public static final boolean DEFAULT_GNUPLOT_SETTING = false;
	//========================================================================//


	//========================================================================//
	// Instance vars
	//========================================================================//
	private final int horizontalCount;
	private final int verticalCount;
	private final double divisionWidth;
	private final double divisionHeight;
	private final boolean gnuplot;
	private final String runName;
	private final List<int[][]> samples;
	//========================================================================//


	//========================================================================//
	// Constructor
	//========================================================================//
	public NodeDensityReport() {
		super();

		final Settings settings = super.getSettings();

		this.gnuplot = settings.getBoolean(GNUPLOT_SETTING,
				DEFAULT_GNUPLOT_SETTING);

		// Set up the sampling grid
		this.horizontalCount
				= settings.getInt(X_COUNT_SETTING, DEFAULT_X_COUNT);
		this.verticalCount
				= settings.getInt(Y_COUNT_SETTING, DEFAULT_Y_COUNT);

		if (this.horizontalCount <= 0 || this.verticalCount <= 0) {
			throw new SettingsError("Settings '" + X_COUNT_SETTING + "' and '"
					+ Y_COUNT_SETTING + "' must be positive. Found "
					+ this.horizontalCount + ", " + this.verticalCount + ".");
		}

		final SimScenario scenario = SimScenario.getInstance();
		final int worldWidth = scenario.getWorldSizeX();
		final int worldHeight = scenario.getWorldSizeY();

		this.divisionWidth = 1.0 * worldWidth / this.horizontalCount;
		this.divisionHeight = 1.0 * worldHeight / this.verticalCount;

		final double duration = scenario.getEndTime();
		final int sampleCount = (int) (duration / super.interval + 1);

		this.samples = new ArrayList<>(sampleCount);

		this.runName = scenario.getName();
	}
	//========================================================================//


	//========================================================================//
	// SamplingReport
	//========================================================================//
	@Override
	protected void sample(final List<DTNHost> hosts) {
		final int[][] sample
				= new int[this.horizontalCount][this.verticalCount];

		for (final DTNHost host : hosts) {
			final Coord location = host.getLocation();
			final int xBucket = (int) (location.getX() / this.divisionWidth);
			final int yBucket = (int) (location.getY() / this.divisionHeight);
			sample[xBucket][yBucket]++;
		}

		this.samples.add(sample);
	}
	//========================================================================//


	//========================================================================//
	// Report
	//========================================================================//
	@Override
	public void done() {

		if (this.gnuplot) {
			this.outputGnuplotPrefix();
		}

		final double[][] averages = getAverageDensities(this.samples,
				this.horizontalCount, this.verticalCount);

		for (int g_x = 0; g_x < this.horizontalCount; g_x++) {
			for (int g_y = 0; g_y < this.verticalCount; g_y++) {
				String line = "" + g_x + " " + g_y + " "
						+ averages[g_x][g_y];
				for (final int[][] sample : this.samples) {
					line += " " + sample[g_x][g_y];
				}
				super.write(line);
			}
		}

		if (this.gnuplot) {
			this.outputGnuplotSuffix(this.samples.size());
		}

		super.done();
	}
	//========================================================================//


	//========================================================================//
	// Private
	//========================================================================//
	private static double[][] getAverageDensities(
			final List<int[][]> samples,
			final int horizontalCount,
			final int verticalCount) {
		final double[][] averages = new double[horizontalCount][verticalCount];

		// Get total count
		for (final int[][] sample : samples) {
			for (int g_x = 0; g_x < horizontalCount; g_x++) {
				for (int g_y = 0; g_y < verticalCount; g_y++) {
					averages[g_x][g_y] += sample[g_x][g_y];
				}
			}
		}

		// Calculate average
		final int count = samples.size();
		for (int g_x = 0; g_x < horizontalCount; g_x++) {
			for (int g_y = 0; g_y < verticalCount; g_y++) {
				averages[g_x][g_y] /= count;
			}
		}

		return averages;
	}
	//========================================================================//


	//========================================================================//
	// Private - Gnuplot
	//========================================================================//
	private void outputGnuplotPrefix() {
		super.write("set terminal png");
		super.write("$data << EOD");
	}

	private void outputGnuplotSuffix(final int sampleCount) {
		super.write("EOD");
		super.write("set output sprintf('" + this.runName
				+ "-avrg.png')");
		super.write("plot '$data' using 2:1:3 with image");
		super.write("do for [ii=4:" + (3 + sampleCount) + "] {");
		super.write("set output sprintf('" + this.runName
						+ "%04.0f.png',ii-3)");
		super.write("plot '$data' using 2:1:ii with image");
		super.write("}");
	}
	//========================================================================//
}
