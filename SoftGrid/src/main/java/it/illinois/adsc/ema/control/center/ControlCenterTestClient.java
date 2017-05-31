package it.illinois.adsc.ema.control.center;

import it.illinois.adsc.ema.softgrid.concenter.ui.ControlCenter;

import java.io.IOException;

/**
 * Created by prageethmahendra on 30/5/2017.
 */
public class ControlCenterTestClient {
    public static void main(String[] args) {
        ControlCenterClient client = ControlCenterClient.getInstance(new ControlCenterContext(false, ""), "192.168.0.177:2404");
        client.startClient();
    }
}
