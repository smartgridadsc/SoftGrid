package it.illinois.adsc.ema.control.ied.pw;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by prageethmahendra on 12/2/2016.
 */
public class PWModelDetails implements Comparable {
    private String sclFileName;
    private String ipAddress;
    private int portNumber;
    private String modelNodeReference;
    private String deviceName;
    private HashMap<String, String> keyValueFields;
    private HashMap<String, String> dataKeyValueFields;
    private HashMap<String, String> sclToPWMapping;

    public PWModelDetails(String sclFileName) {
        this.sclFileName = sclFileName;
    }

    public void setKeyValues(String[] keys, String[] values) {
    }

    public void addKeyField(String field, String value) {
        if (keyValueFields == null) {
            keyValueFields = new HashMap<String, String>();
        }
        keyValueFields.put(field, value);
    }

    public String getSclFileName() {
        return sclFileName;
    }

    public void setSclFileName(String sclFileName) {
        this.sclFileName = sclFileName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public void addDataField(String fieldName, String value, String sclFieldName) {
        if (dataKeyValueFields == null) {
            dataKeyValueFields = new HashMap<String, String>();
        }
        if (sclToPWMapping == null) {
            sclToPWMapping = new HashMap<String, String>();
        }
        dataKeyValueFields.put(fieldName, value);
        sclToPWMapping.put(fieldName, sclFieldName);
    }

    public int getFieldCount() {
        return dataKeyValueFields.size() + keyValueFields.size();
    }

    public String getModelNodeReference() {
        return modelNodeReference;
    }

    public void setModelNodeReference(String modelNodeReference) {
        this.modelNodeReference = modelNodeReference;
    }

    public String[] getKeyArray() {
        if (keyValueFields == null) {
            return new String[0];
        }
        String[] keyArray = new String[keyValueFields.size()];
        int keyCount = 0;
        for (String key : keyValueFields.keySet()) {
            keyArray[keyCount] = key;
            keyCount++;
        }
        return keyArray;
    }

    public String[] getDataFieldArray() {
        String[] fieldArray = new String[dataKeyValueFields.size()];
        int keyCount = 0;
        for (String key : dataKeyValueFields.keySet()) {
            fieldArray[keyCount] = key;
            keyCount++;
        }
        return fieldArray;
    }

    public String[] getValueArray() {
        String[] values = new String[(keyValueFields == null ? 0 : keyValueFields.size()) + dataKeyValueFields.size()];
        int keyCount = 0;
        if (keyValueFields != null) {
            for (String key : keyValueFields.keySet()) {
                values[keyCount] = keyValueFields.get(key);
                keyCount++;
            }
        }

        for (String otherField : dataKeyValueFields.keySet()) {
            values[keyCount] = dataKeyValueFields.get(otherField);
            keyCount++;
        }
        return values;
    }

    public HashMap<String, String> getKeyValueFields() {
        return keyValueFields;
    }

    public void setKeyValueFields(LinkedHashMap<String, String> keyValueFields) {
        this.keyValueFields = keyValueFields;
    }

    public HashMap<String, String> getDataKeyValueFields() {
        return dataKeyValueFields;
    }

    public void setDataKeyValueFields(LinkedHashMap<String, String> dataKeyValueFields) {
        this.dataKeyValueFields = dataKeyValueFields;
    }

    public HashMap<String, String> getSclToPWMapping() {
        return sclToPWMapping;
    }

    public void setSclToPWMapping(HashMap<String, String> sclToPWMapping) {
        this.sclToPWMapping = sclToPWMapping;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !(o instanceof PWModelDetails)) {
            return -1;
        } else {
            PWModelDetails pwModelDetails = (PWModelDetails) o;
            if (pwModelDetails.getModelNodeReference() == null && modelNodeReference == null) {
                return 0;
            } else if (pwModelDetails.getModelNodeReference() != null &&
                    modelNodeReference != null) {
                return modelNodeReference.compareTo(pwModelDetails.getModelNodeReference());
            } else {
                return -1;
            }

        }
    }
}
