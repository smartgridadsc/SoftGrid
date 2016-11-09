package org.openmuc.j60870;

import com.sun.corba.se.spi.activation.Server;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.APdu;
import org.openmuc.j60870.ClientSap;
import org.openmuc.j60870.Connection;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by prageethmahendra on 13/6/2016.
 */
public class CONObject {
    ASdu aSdu;
    public CONObject(DataInputStream inputStream, Socket clientSocket) {
        try {
            APdu aPdu = new APdu(inputStream, new ConnectionSettings());
            aSdu = aPdu.getASdu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ASdu getaSdu() {
        return aSdu;
    }

    public void setaSdu(ASdu aSdu) {
        this.aSdu = aSdu;
    }
}
