package it.illinois.adsc.ema.control.center;

import java.io.IOException;

/**
 * Created by prageeth.g on 18/5/2017.
 */
public class StandAlonControlCenter {

    public static void main(String[] args) {
        ControlCenterClient centerClient = ControlCenterClient.getInstance(new ControlCenterContext(false, "GatewayIEDmap.xml"), "192.168.0.43:2404" );
        centerClient.startClient();
        try {
            centerClient.runCommand("interrogation " + String.valueOf(Integer.MAX_VALUE - 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
