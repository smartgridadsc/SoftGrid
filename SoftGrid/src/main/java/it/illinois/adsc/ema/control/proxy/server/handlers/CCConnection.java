package it.illinois.adsc.ema.control.proxy.server.handlers;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.IEC60870104ConnectionWrapper;

import java.io.IOException;

/**
 * Created by prageethmahendra on 16/1/2017.
 */
public class CCConnection {
    private IEC60870104ConnectionWrapper iec60870104ConnectionWrapper;
    private Connection connection;

    public CCConnection(IEC60870104ConnectionWrapper iec60870104ConnectionWrapper, Connection connection) {
        this.iec60870104ConnectionWrapper = iec60870104ConnectionWrapper;
        this.connection = connection;
    }

    public void sendPacket(ASdu asduToCC) {
        try {
            if (connection == null) {
                iec60870104ConnectionWrapper.send(asduToCC);
            } else {
                connection.send(asduToCC);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
