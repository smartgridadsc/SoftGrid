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
            if(ConfigUtil.MULTI_IP_IED_MODE_ENABLED) {
                String[] ipParts = modelDetails.getIpAddress().split("\\.");
                // each ip part should contain 3 characters. if not "0" will be added to fill the missing characters
                for (int i = 2; i < 4; i++) {
                    while (ipParts[i].length() < 3) {
                        ipParts[i] = "0" + ipParts[i];
                    }
                }
                int iedID = Integer.parseInt("1" + ipParts[2] + ipParts[3]);
                ipParts = firstIP.split("\\.");
                for (int i = 2; i < 4; i++) {
                    while (ipParts[i].length() < 3) {
                        ipParts[i] = "0" + ipParts[i];
                    }
                }
                int startID = Integer.parseInt("1" + ipParts[2] + ipParts[3]);
                substationProxyClient = new SubstationProxyClient( iedID - startID );
            }
            else
            {
                substationProxyClient = new SubstationProxyClient( modelDetails.getPortNumber() - startPort);
            }
            substationProxyClient.init(ProxyType.NORMAL);
            substationProxyClient.startProxy(modelDetails);

            /*0,9984,1,9985,2,9986,3,9987,4,9988,5,9989,6,9990,7,9991,8,9992,9993,9994,9995,99
96,9997,9998,9909,
9910,9911,9912,9913,9914,9915,9916,9917,9918,9919,9920,9921,9922,9923,9924,9925,
9926,108999,9927,9928,109001,9929,109000,9930,109003,
9931,109002,9932,109005,9933,109004,9934,109007,9935,109006,9936,109009,9937,109
008,9938,109011,9939,109010,9940,109013,9941,109012,9942,109015,9943,
109014,9944,109017,9945,109016,9946,109019,9947,109018,9948,109021,9949,109020,9
950,109023,9951,109022,9952,109025,9953,109024,9954,109027,9955,109026,
9956,109029,9957,109028,9958,109031,9959,109030,9960,109033,9961,109032,9962,109
035,9963,109034,9964,109037,9965,109036,9966,109039,9967,109038,9968,
109041,9969,109040,9970,109043,9971,109042,9972,109045,9973,109044,9974,109047,9
975,109046,9976,109049,9977,109048,9978,9979,109050,9980,9981,9982,
9983,*/
        }
    }


    public static void killAll() {
        if (substationProxyClient != null) {
            substationProxyClient.stop();
        }
    }
}
