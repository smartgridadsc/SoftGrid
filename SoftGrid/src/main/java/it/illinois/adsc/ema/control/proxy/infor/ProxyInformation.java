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
import it.illinois.adsc.ema.control.proxy.util.ParameterType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prageethmahendra on 2/2/2016.
 */
public class ProxyInformation {
    private DeviceType deviceType;
    private ParameterType paramType;
    private String parameter;
    private ProxyVariant variant;
    private int iedId;
    private List<ProxyInformation> deviceInfor;

    public ProxyInformation() {
        init();
    }

    public void init() {
        deviceInfor = new ArrayList<ProxyInformation>();
        deviceType = DeviceType.CIRCUITE_BREACKER;
        iedId = -1;
    }

    public ParameterType getParamType() {
        return paramType;
    }

    public void setParamType(ParameterType paramType) {
        this.paramType = paramType;
    }

    public ProxyVariant getVariant() {
        return variant;
    }

    public void setVariant(ProxyVariant variant) {
        this.variant = variant;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public List<ProxyInformation> getDeviceInfor() {
        return deviceInfor;
    }

    public void setDeviceInfor(List<ProxyInformation> deviceInfor) {
        this.deviceInfor = deviceInfor;
    }

    public int getIedId() {
        return iedId;
    }

    public void setIedId(int iedId) {
        this.iedId = iedId;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
