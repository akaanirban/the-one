/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package report;

import core.DTNHost;
import core.Message;
import core.PayloadMessageListener;
import core.PayloadMessage;

import java.util.Arrays;

/**
 * Reports delivered messages
 * report:
 *  message_id creation_time deliver_time (duplicate)
 */
public class PayloadMessageReport extends Report  {
    public static final String HEADER =
            "# messages: ID, start time, end time, Payload, Tag";
    /** all message delays */

    /**
     * Constructor.
     */
    public PayloadMessageReport() {
        init();
    }

    @Override
    public void init() {
        super.init();
        write(HEADER);
    }

    public void newPayloadMessage(PayloadMessage m) {}

/*    public void PayloadmessageTransferred(PayloadMessage m, DTNHost from, DTNHost to,
                                   boolean firstDelivery) {
        if (firstDelivery) {
            write(m.getId() + " "
                    + format(m.getCreationTime()) + " "
                    + format(getSimTime()));
        } else {
            if (to.getAddress() == m.getTo().getAddress()) {
                write(m.getId() + " "
                        + format(m.getCreationTime()) + " "
                        + format(getSimTime()) + " duplicate" );
                *//**+ format(m.getPayload())  + m.getMessageTag()*//*
            }
        }
    }*/

    public void messageTransferred(PayloadMessage m, DTNHost from, DTNHost to,
                                   boolean firstDelivery) {
        if (firstDelivery) {
            write(m.getId() + " "
                    + format(m.getCreationTime()) + " "
                    + format(getSimTime()));
        } else {
            if (to.getAddress() == m.getTo().getAddress()) {
                write(m.getId() + " "
                        + format(m.getCreationTime()) + " "
                        + format(getSimTime()) + " duplicate");
            }
        }
    }

    public void done() {
        super.done();
    }

    // nothing to implement for the rest
    public void PayloadmessageDeleted(PayloadMessage m, DTNHost where, boolean dropped) {}
    public void PayloadmessageTransferAborted(PayloadMessage m, DTNHost from, DTNHost to) {}
    public void PayloadmessageTransferStarted(PayloadMessage m, DTNHost from, DTNHost to) {}

}
