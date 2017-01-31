/* Copyright (C) 2016 Advanced Digital Science Centre

        * This file is part of Soft-Grid.
        * For more information visit https://www.illinois.adsc.com.sg/cybersage/
        *
        * Soft-Grid is free software: you can redistribute it and/or modify
        * it under the terms of the GNU General Public License as published by
        * the Free Software Foundation, either version 3 of the License, or
        * (at your option) any later version.
        *
        * Soft-Grid is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        * GNU General Public License for more details.
        *
        * You should have received a copy of the GNU General Public License
        * along with Soft-Grid.  If not, see <http://www.gnu.org/licenses/>.

        * @author Prageeth Mahendra Gunathilaka
*/
package it.illinois.adsc.ema.control.proxy.context;

import it.illinois.adsc.ema.control.proxy.client.VPBusClient;
import it.illinois.adsc.ema.control.proxy.util.DeviceType;
import it.illinois.adsc.ema.control.proxy.client.PowerProxyClient;
import it.illinois.adsc.ema.control.proxy.infor.ProxyInformation;
import it.illinois.adsc.ema.control.proxy.server.handlers.ICommandHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prageethmahendra on 2/2/2016.
 */
public class SimpleProxyContext implements ProxyServerContext, ProxyClientContext {
    private ICommandHandler icommandHandler = null;
    private PowerProxyClient powerProxyClient = null;
    private HashMap<Integer, PowerProxyClient> iedClientMap = new HashMap<Integer, PowerProxyClient>();

    // This method will register the proxyserver and keep its reference to maintain the bridge
    @Override
    public void registerCommandHandler(ICommandHandler commandHandler) {
        this.icommandHandler = commandHandler;
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
    public ProxyInformation getIntegrationData(int commonAddress) {
        if (iedClientMap == null || iedClientMap.isEmpty()) {
            return null;
        } else {
            // General Interrogation
            ProxyInformation proxyInformation = new ProxyInformation();
            proxyInformation.init();
            proxyInformation.setDeviceType(DeviceType.ROOT);
            if (commonAddress == 65534) {
                // vertual IED address
                VPBusClient vpBusClient = getVirtualClient(commonAddress);
                List<ProxyInformation> proxyInterrogations  = vpBusClient.getResults();
                proxyInformation.getDeviceInfor().addAll(proxyInterrogations);
                proxyInformation.setIedId(commonAddress);
            } else {
                System.out.println("iedClientMap = " + iedClientMap.size());
                for (PowerProxyClient proxyClient : getClients(commonAddress)) {
                    List<ProxyInformation> proxyInterrogations = proxyClient.interrogationRequest(commonAddress);
                    if (proxyInterrogations != null) {
                        for (ProxyInformation proxyInterrogation : proxyInterrogations) {
                            System.out.println("params = " + proxyInterrogation.getParameter());
                            System.out.println("value = " + proxyInterrogation.getVariant());
                        }
                        proxyInformation.getDeviceInfor().addAll(proxyInterrogations);
                        proxyInformation.setIedId(commonAddress);
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
