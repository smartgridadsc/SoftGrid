package it.illinois.adsc.ema.common.webservice;

/**
 * Created by prageethmahendra on 1/9/2016.
 */
public class ExperimentRequest {
    private ExperimentType experimentType;
    private String entity;
    private String serverName;
    private String command;
    private String gatewayIP;
    private int gatewayPort;

    public ExperimentRequest() {
        gatewayPort = 2404;
    }

    public ExperimentType getExperimentType() {
        return experimentType;
    }

    public void setExperimentType(ExperimentType experimentType) {
        this.experimentType = experimentType;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setGatewayIP(String gatewayIP) {
        this.gatewayIP = gatewayIP;
    }

    public String getGatewayIP() {
        return gatewayIP;
    }

    public int getGatewayPort() {
        return gatewayPort;
    }

    public void setGatewayPort(int gatewayPort) {
        this.gatewayPort = gatewayPort;
    }
}
