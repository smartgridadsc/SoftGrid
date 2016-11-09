package org.openmuc.j60870;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by prageethmahendra on 17/6/2016.
 */
public class IEC60870104ConnectionWrapper {

    ConnectionEventListener eventListener;

    IEC60870104ConnectionWrapper(ConnectionEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void send(ASdu aSdu) throws IOException {
        eventListener.newASdu(aSdu);
    }
}
