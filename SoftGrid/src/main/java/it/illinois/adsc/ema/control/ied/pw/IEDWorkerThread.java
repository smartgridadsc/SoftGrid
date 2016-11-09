package it.illinois.adsc.ema.control.ied.pw;


import com.jacob.com.ComThread;
import it.adsc.smartpower.substation.monitoring.ui.IEDControler;
import it.illinois.adsc.ema.control.conf.generator.ConfigGenerator;
import it.illinois.adsc.ema.control.ied.SmartPowerIEDServer;

import java.util.Map;

/**
 * Created by prageethmahendra on 11/2/2016.
 */
public class IEDWorkerThread implements Runnable, Comparable {

    private PWModelDetails pwModelDetails;
    private SmartPowerIEDServer smartPowerIEDServer = null;

    public IEDWorkerThread(PWModelDetails s) {
        this.pwModelDetails = s;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Start. in Port = " + pwModelDetails.getPortNumber());
        processCommand();
        System.out.println(Thread.currentThread().getName() + " End.");
    }

    private void processCommand() {
        smartPowerIEDServer = new SmartPowerIEDServer();
        System.out.println("pwModelDetails.getModelNodeReference() = " + pwModelDetails.getModelNodeReference());
        try {
            smartPowerIEDServer.startServer(pwModelDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SmartPowerIEDServer getSmartPowerIEDServer() {
        return smartPowerIEDServer;
    }

    @Override
    public String toString() {
        Map<String, String> keyValuePair = getKeyValuePairs();
        String iedType = keyValuePair == null ? "Virtual" : getIEDType();
        int portNumber = getPortNumber();
        String ioa = String.valueOf(portNumber - ConfigGenerator.FIRST_PORT);
        StringBuffer sb = new StringBuffer();
        sb.append(iedType).append(",").append(ioa).append(",");
        if (keyValuePair != null) {
            for (String key : keyValuePair.keySet()) {
                sb.append(key).append("=").append(keyValuePair.get(key)).append(" ");
            }
        }
        sb.append(",").append(String.valueOf(portNumber));
        return sb.toString();
    }

    public boolean isServerStarted() {
        return smartPowerIEDServer != null && smartPowerIEDServer.isServerStarted();
    }


    public String getIEDType() {
        return pwModelDetails.getDeviceName();
    }

    public int getPortNumber() {
        return pwModelDetails.getPortNumber();
    }

    public Map<String, String> getKeyValuePairs() {
        return pwModelDetails.getKeyValueFields();
    }

    public PWModelDetails getPwModelDetails() {
        return pwModelDetails;
    }

    public void setPwModelDetails(PWModelDetails pwModelDetails) {
        this.pwModelDetails = pwModelDetails;
    }

    @Override
    public int compareTo(Object o) {
        if (o != null && o instanceof IEDWorkerThread && pwModelDetails != null) {
            return this.pwModelDetails.compareTo(((IEDWorkerThread) o).getPwModelDetails());
        } else if (pwModelDetails == null && o instanceof IEDWorkerThread &&
                ((IEDWorkerThread) o).getPwModelDetails() == null) {
            return 0;
        } else {
            return -1;
        }
    }
}
