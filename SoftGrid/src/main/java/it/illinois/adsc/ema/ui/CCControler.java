package it.adsc.smartpower.substation.monitoring.ui;

/**
 * Created by prageethmahendra on 13/9/2016.
 */
public interface CCControler {
    void startCCClient(String gatewayIP, int gatewayPort);
    void stopCCClient();

    void runCommand(String command);
    boolean isLive(String ip, int port);
}
