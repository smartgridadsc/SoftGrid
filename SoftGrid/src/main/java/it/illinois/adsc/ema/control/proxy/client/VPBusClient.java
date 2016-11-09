package it.illinois.adsc.ema.control.proxy.client;

import it.illinois.adsc.ema.control.proxy.infor.ProxyInformation;
import it.illinois.adsc.ema.control.proxy.infor.ProxyVariant;
import it.illinois.adsc.ema.control.proxy.util.ProxyClientUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prageethmahendra on 2/3/2016.
 */
public class VPBusClient {
    List<PowerProxyClient> powerProxyClients = new ArrayList<PowerProxyClient>();
    private static float baseline_frequency = 60;

    public VPBusClient(HashMap<Integer, PowerProxyClient> proxyClientHashMap) {
        for (PowerProxyClient powerProxyClient : proxyClientHashMap.values()) {
            if (powerProxyClient.isConnectedToBus()) {
                powerProxyClients.add(powerProxyClient);
            }
        }
    }

    public List<ProxyInformation> getResults() {
        List<ProxyInformation> proxyInformation = new ArrayList<ProxyInformation>();
        int violationCount = 0;
        for (PowerProxyClient powerProxyClient : powerProxyClients) {
            List<ProxyInformation> subInfor = powerProxyClient.interrogationRequest();
            for (ProxyInformation information : subInfor) {
                if (information.getParameter() != null && information.getParameter().equalsIgnoreCase("frequency")) {
                    float value = Float.parseFloat(information.getVariant().getString());
                    if (value > (baseline_frequency + 0.5) || value < (baseline_frequency - 0.5)) {
                        violationCount++;
                    }
                    System.out.println("Bus Frequency = " + value + " " + powerProxyClient.getIedID());
                }
                System.out.println(information);
            }
        }
        ProxyInformation virtualProxyInfor = new ProxyInformation();
        virtualProxyInfor.setParameter("violation");
        virtualProxyInfor.setParamType(ProxyClientUtil.getObjectVariableType(virtualProxyInfor.getParameter()));
        virtualProxyInfor.setVariant(new ProxyVariant());
        virtualProxyInfor.getVariant().setString(String.valueOf(violationCount));
        virtualProxyInfor.setDeviceType(ProxyClientUtil.getDeviceType(null));
        proxyInformation.add(virtualProxyInfor);
        return proxyInformation;
    }
}
