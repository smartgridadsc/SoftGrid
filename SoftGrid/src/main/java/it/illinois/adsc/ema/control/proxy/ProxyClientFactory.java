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
package it.illinois.adsc.ema.control.proxy;

import it.illinois.adsc.ema.control.IEDDataSheetHandler;
import it.illinois.adsc.ema.control.ied.pw.PWModelDetails;
import it.illinois.adsc.ema.control.proxy.client.SubstationProxyClient;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import org.openmuc.openiec61850.ServiceError;

import java.io.IOException;

/**
 * Created by prageethmahendra on 16/2/2016.
 */
public class ProxyClientFactory {
    private static int proxyClientAddressCount = 1;
    private static SubstationProxyClient substationProxyClient = null;
    // todo this is a hardcoden port number
    private static int startPort = 10003;

    public static void startNormalProxy(PWModelDetails modelDetails, String firstIP) throws ServiceError, IOException {
        if (IEDDataSheetHandler.isProxyConnectionAllowed(modelDetails.getPortNumber())) {
            String[] ipParts = modelDetails.getIpAddress().split("\\.");
            int iedID = Integer.parseInt(ipParts[2] + ipParts[3]);
            ipParts = firstIP.split("\\.");
            int startID = Integer.parseInt(ipParts[2] + ipParts[3]);
            substationProxyClient = new SubstationProxyClient(ConfigUtil.MULTI_IP_IED_MODE_ENABLED ?
                    iedID - startID : modelDetails.getPortNumber() - startPort);
            substationProxyClient.init(ProxyType.NORMAL);
            substationProxyClient.startProxy(modelDetails);
        }
    }


    public static void killAll() {
        if (substationProxyClient != null) {
            substationProxyClient.stop();
        }
    }
}
