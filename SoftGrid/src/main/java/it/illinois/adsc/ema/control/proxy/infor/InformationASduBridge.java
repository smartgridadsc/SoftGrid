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
package it.illinois.adsc.ema.control.proxy.infor;

import it.illinois.adsc.ema.control.proxy.util.DeviceType;
import org.openmuc.j60870.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prageethmahendra on 2/2/2016.
 */
public class InformationASduBridge {

    /**
     * This method translate the proxy data into the ProxyData Standard Information Object Set
     * @param proxyInformation
     * @return
     */
    public static List<InformationObject> getInformationObject(ProxyInformation proxyInformation) {
        List<InformationObject> informationObjects = new ArrayList<InformationObject>();
        if (proxyInformation != null) {
            if (proxyInformation.getDeviceType() != DeviceType.ROOT) {

                if (proxyInformation.getVariant().getString() != null) {
                    InformationElement informationElement = null;
                    String result = proxyInformation.getVariant().getString().trim();
                    switch (proxyInformation.getParamType()) {
                        case BRANCH_LINE_STATUS:
                            informationElement = new IeScaledValue(result.isEmpty()? -1 : proxyInformation.getVariant().getString().equals("Open") ? 1 : 0);
                            break;
                        case FLOAT_VALUE:
                            informationElement = new IeShortFloat(result.isEmpty()? 0:Float.parseFloat(proxyInformation.getVariant().getString()));
                            break;
                        default:
                            informationElement = null;
                            break;
                    }
                    InformationElement[][] informationElements = {{informationElement, new IeQuality(true, true, true, true, true)}};
                    informationObjects.add(new InformationObject(proxyInformation.getIedId(), informationElements));
                } else {
                    InformationElement[][] informationElements = {{new IeStatusAndStatusChanges(proxyInformation.getVariant().getInteger()), new IeQuality(true, true, true, true, true)}};
                    informationObjects.add(new InformationObject(proxyInformation.getIedId(), informationElements));
                }

                return informationObjects;
            } else {
                if (proxyInformation.getDeviceInfor() != null) {
                    for (ProxyInformation information : proxyInformation.getDeviceInfor()) {
                        informationObjects.addAll(getInformationObject(information));
                    }
                    return informationObjects;
                }
            }
        }
        // no data found
        informationObjects.add(new InformationObject(1, new InformationElement[][]{{new IeStatusAndStatusChanges(-1), new IeQuality(false, false, false, false, false)}}));
        return informationObjects;
    }

    public static TypeId getASDUTypeID(ProxyInformation proxyInformation)
    {
        switch (proxyInformation.getParamType())
        {
            case FLOAT_VALUE: return TypeId.M_ME_NC_1;
            default:return TypeId.M_ME_NB_1;
        }
    }
}
