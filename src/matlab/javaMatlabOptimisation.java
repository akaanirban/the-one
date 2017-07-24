package matlab;
import com.mathworks.engine.*;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;

import java.lang.*;
import java.awt.*;
import java.util.Arrays;


public class javaMatlabOptimisation {
    public static void main(String[] args) throws Exception{
        //System.setProperty( "java.library.path", "C:\\Program Files\\MATLAB\\R2016b\\bin\\win64" );
        //Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
        //fieldSysPath.setAccessible( true );
        //fieldSysPath.set( null, null );
        //System.out.println(System.getProperty("java.library.path"));
        //System.out.println(System.getProperty("user.dir"));
        MatlabEngine eng = MatlabEngine.startMatlab();

        StringWriter writer = new StringWriter();
        eng.eval("cd 'D:\\GitHub\\the-one\\anirban\\l1_ls_matlab'");
        double[] x0 = {1.0,0.0,1.0,0.0};
        double[] m = eng.feval("simple_example", x0);
        System.out.println("The returned value" + Arrays.toString(m));
        //        System.out.println("The returned value" + Arrays.deepToString(new double[][]{m}));
        eng.eval("A = [1,2,3;4,5,6]");
        eng.eval("B = [4,5,6;1,2,3]");
        double[][] A = eng.getVariable("A");
        System.out.println(Arrays.deepToString(A));













        //double[][] a = {{2.0 ,4.0, 6.0}, {3.0, 10, 8}};
        //ArrayList dummyList = new ArrayList()
        //dblArray = javaArray('java.lang.Double',1,10);
        //eng.cd(r'"D:\GitHub\the-one\anirban\l1_ls_matlab\simple_example.m"',nargout=0)
        //double[][] a = {{2.0 ,4.0, 6.0}, {5,6,7}};
        //double[][] roots = eng.feval("sqrt", (Object) a);
        //eng.feval("D:\\GitHub\\the-one\\anirban\\l1_ls_matlab\\simple_example.m");
        //System.out.println(roots);








        eng.close();
    }
}
