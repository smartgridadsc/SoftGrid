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
