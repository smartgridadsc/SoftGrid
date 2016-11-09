package it.illinois.adsc.ema.control.proxy.context;

import it.illinois.adsc.ema.control.proxy.client.VPBusClient;
import it.illinois.adsc.ema.control.proxy.util.DeviceType;
import it.illinois.adsc.ema.control.proxy.client.PowerProxyClient;
import it.illinois.adsc.ema.control.proxy.infor.ProxyInformation;
import it.illinois.adsc.ema.control.proxy.server.PowerProxyServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prageethmahendra on 2/2/2016.
 */
public class SimpleProxyContext implements ProxyServerContext, ProxyClientContext {
    private PowerProxyServer powerProxyServer = null;
    private PowerProxyClient powerProxyClient = null;
    private HashMap<Integer, PowerProxyClient> iedClientMap = new HashMap<Integer, PowerProxyClient>();

    // This method will register the proxyserver and keep its reference to maintain the bridge
    @Override
    public void registerProxyServer(PowerProxyServer proxyServer) {
        this.powerProxyServer = proxyServer;
    }

    @Override
    public void registerProxyClient(int iedID, PowerProxyClient proxyClient) {
        iedClientMap.put(iedID, proxyClient);
    }

    @Override
    public boolean handleControlCommand(int iedID, int qualifier, boolean state) {
        return handleControlCommand(iedID, qualifier, new Boolean(state));
    }

    @Override
    public boolean handleControlCommand(int iedID, int qualifier, Object valueObject) {
        boolean result = true;
        for (PowerProxyClient proxyClient : getClients(iedID)) {
            result = proxyClient.handleControlCommand(qualifier, valueObject) && result;
        }
        return result;
    }

    @Override
    public ProxyInformation getIntegrationData(int iedID) {
        if (iedClientMap == null || iedClientMap.isEmpty()) {
            return null;
        } else {
            // General Interrogation
            ProxyInformation proxyInformation = new ProxyInformation();
            proxyInformation.init();
            proxyInformation.setDeviceType(DeviceType.ROOT);
            if (iedID == 65534) {
                // vertual IED address
                VPBusClient vpBusClient = getVirtualClient(iedID);
                List<ProxyInformation> proxyInterrogations  = vpBusClient.getResults();
                proxyInformation.getDeviceInfor().addAll(proxyInterrogations);
                proxyInformation.setIedId(iedID);
            } else {
                System.out.println("iedClientMap = " + iedClientMap.size());
                for (PowerProxyClient proxyClient : getClients(iedID)) {
                    List<ProxyInformation> proxyInterrogations = proxyClient.interrogationRequest();
                    if (proxyInterrogations != null) {
                        for (ProxyInformation proxyInterrogation : proxyInterrogations) {
                            System.out.println("params = " + proxyInterrogation.getParameter());
                            System.out.println("value = " + proxyInterrogation.getVariant());
                        }
                        proxyInformation.getDeviceInfor().addAll(proxyInterrogations);
                        proxyInformation.setIedId(iedID);
                    }
                    else
                    {
                        System.out.println("proxyInterrogations = " + proxyInterrogations);
                    }
                }
            }
            return proxyInformation;
        }
    }

    private List<PowerProxyClient> getClients(int addresses) {
        List<PowerProxyClient> powerProxyClients = new ArrayList<PowerProxyClient>();
        if (iedClientMap != null) {
//            int count = 0;
            for (Integer ied : iedClientMap.keySet()) {
                if ( addresses != 65535) {
                    PowerProxyClient powerProxyClient = iedClientMap.get(addresses);
                    if (powerProxyClient != null) {
                        powerProxyClients.add(powerProxyClient);
                        break;
                    }
                } else  // address of all the ieds
                {
                    powerProxyClients.addAll(iedClientMap.values());
                }
//                count++;
            }
        }
        return powerProxyClients;
    }

    private VPBusClient getVirtualClient(int vertualAddress) {
        return new VPBusClient(iedClientMap);
    }
}
