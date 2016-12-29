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
