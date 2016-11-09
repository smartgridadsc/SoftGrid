//package it.ilinois.adsc.ema.pw.ied.data;
//
//import com.jacob.com.Variant;
//import it.illinois.adsc.ema.pw.ied.IedControlAPI;
//
//import java.util.HashMap;
//import java.util.Objects;
//import java.util.StringTokenizer;
//
///**
// * Created by prageethmahendra on 29/1/2016.
// */
//public class ParameterGenerator {
//
//    // Device Name
//    String deviceObjectName;
//    // key Names
//    private String[] keyParameters;
//    // Other Variable Names
//    private String[] valueParameters;
//    // All key and non key Values
//    private String[] persistedValues;
//    private HashMap<String, String> sclKeyToPWKeyMap = new HashMap<>();
//    // API controler COM sc for Power World
//    private IedControlAPI controlAPI;
//
//    public ParameterGenerator() {
//
//    }
//
//    public String[][] writePWParameters(String openMucKey, String openMUCValue) throws Exception {
//        if (openMucKey == null ||
//                openMUCValue == null ||
//                !isValid()
//                ) {
//            throw new Exception("Invalid IDE Prameter State.");
//        }
//        String pwKey = sclKeyToPWKeyMap.get(openMucKey);
//        if (pwKey != null) {
//            int keyIndex = -1;
//            for (int i = 0; i < valueParameters.length; i++) {
//                if (valueParameters[i] != null) {
//                    keyIndex = i + keyParameters.length;
//                }
//            }
//            String[][] paramPack = getParamPack();
//            if (keyIndex >= 0) {
//                persistedValues[keyIndex] = openMUCValue;
//                String variant = writeDataValues(paramPack);
//                if (variant != null && variant.trim().isEmpty()) {
//                    return paramPack;
//                }
//            }
//            throw new Exception("Error occured during changeParamegters for Single Element" + deviceObjectName +
//                    "\n Parameters : " + paramPack[0].toString() + "\n values " + paramPack[1].toString());
//        } else {
//            throw new Exception("Invalid IED parameter name : " + openMucKey + " or value : " + openMUCValue);
//        }
//    }
//
//    public String[] loadDataValues(String[][] paramPack) {
//        synchronized (controlAPI) {
//            String[] results = controlAPI.getParametersSingleElement(deviceObjectName, paramPack[0], paramPack[1]);
////            return getTokenizedElements(results, paramPack[0].length);
//            return results;
//        }
//    }
//
//    public String writeDataValues(String[][] paramPack) {
//        return controlAPI.changeParametersSingleElement(deviceObjectName, paramPack[0], paramPack[1]);
//    }
//
//    public String[][] getParamPack() {
//        String[] params = new String[keyParameters.length + valueParameters.length];
//        int keyIndex = -1;
//        String[][] paramPack = new String[2][valueParameters.length];
//        for (int i = 0; i < keyParameters.length; i++) {
//            params[i] = keyParameters[i];
//        }
//        for (int i = 0; i < valueParameters.length; i++) {
//            params[i + keyParameters.length] = sclKeyToPWKeyMap.get(valueParameters[i]);
//        }
//        paramPack[0] = params;
//        paramPack[1] = persistedValues;
//        return paramPack;
//    }
//
//    public static synchronized String[] getTokenizedElements(String str, final int elementCount) {
//        String[] elements = new String[elementCount];
//        int count = 0;
//        str = str.substring(2, str.length() - 1).trim();
//        StringTokenizer stringTokenizer = new StringTokenizer(str, " ");
//        while (stringTokenizer.hasMoreElements()) {
//            if (elements.length == count) {
//                break;
//            }
//            elements[count] = stringTokenizer.nextToken();
//            count++;
//        }
//        return elements;
//    }
//
//    public String getDeviceObjectName() {
//        return deviceObjectName;
//    }
//
//    public void setDeviceObjectName(String deviceObjectName) {
//        this.deviceObjectName = deviceObjectName;
//    }
//
//    private boolean isValid() {
//        return (keyParameters.length + valueParameters.length) == persistedValues.length;
//    }
//
//    public String[] getKeyParameters() {
//        return keyParameters;
//    }
//
//    public void setKeyParameters(String[] keyParameters) {
//        this.keyParameters = keyParameters;
//    }
//
//    public String[] getValueParameters() {
//        return valueParameters;
//    }
//
//    public void setValueParameters(String[] valueParameters) {
//        this.valueParameters = valueParameters;
//    }
//
//    public String[] getPersistedValues() {
//        return persistedValues;
//    }
//
//    public void setPersistedValues(String[] persistedValues) {
//        this.persistedValues = persistedValues;
//    }
//
//    public HashMap<String, String> getSclKeyToPWKeyMap() {
//        return sclKeyToPWKeyMap;
//    }
//
//    public void setSclKeyToPWKeyMap(HashMap<String, String> sclKeyToPWKeyMap) {
//        this.sclKeyToPWKeyMap = sclKeyToPWKeyMap;
//    }
//
//    public void setControlAPI(IedControlAPI controlAPI) {
//        this.controlAPI = controlAPI;
//    }
//}
