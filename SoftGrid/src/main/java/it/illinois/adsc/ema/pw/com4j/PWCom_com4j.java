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
package it.illinois.adsc.ema.pw.com4j;

import com.jacob.com.PWVariantUtil;
import it.illinois.adsc.ema.pw.ied.pwcom.PWComAPI;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

import java.util.List;

/**
 * Created by prageethmahendra on 9/6/2016.
 */
public class PWCom_com4j implements PWComAPI {
    private ISimulatorAuto simulatorAuto = null;
    private Thread contingencyThread = null;
    private Thread savingThread = null;
    private static PWCom_com4j instance = null;
    volatile static boolean caseOpened = false;
    volatile static String lock = "LOCK";

    private PWCom_com4j() {
        init();
    }

    public static PWCom_com4j getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new PWCom_com4j();
            }
        }
        return instance;
    }

    private void init() {
        if (!ConfigUtil.CONFIG_INITIALISED) {
            ConfigUtil.init();
        }
        simulatorAuto = ClassFactory.createSimulatorAuto();
    }

    public void startClock() {
        //  Clock Thread
        contingencyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Entermode(RUN)" + runScriptCommand("Entermode(CONTINGENCY);"));// SolvePrimalLP;"));// SolveFullSCOPF(OPF);"));
                while (true) {
                    synchronized (this) {
                        try {
                            runScriptCommand("TSRunUntilSpecifiedTime(" + ConfigUtil.CLOCK_CONTINGENCY_NAME + ")");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(ConfigUtil.VIRTUAL_CLOCK_CYCLE_DURATION);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //  Monitoring Thread
        savingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        saveState();
                        saveCase(ConfigUtil.CASE_FILE_MONITOR, ConfigUtil.CASE_FILE_NAME, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        contingencyThread.start();
        savingThread.start();
    }

//  public static void main(String[] args) {
//      ISimulatorAuto simulatorAuto = ClassFactory.createSimulatorAuto();
//      Object object = simulatorAuto.openCase("C:\\EMA\\Demo\\smartpower\\SmartPower\\casefiles\\GSO_37Bus_dm.PWB");
//      ClassFactory.createSimulatorAuto().runScriptCommand("Entermode(CONTINGENCY);");
//      System.out.println("object = " + PWVariantUtil.objectToVariant(object).toString());
//      String[] params = {"BusNum", "BusNum:1","LineCircuit","LineStatus"};
//      String[] values = {"1", "31","1",""};
//      object = simulatorAuto.getParametersSingleElement("Branch", params, values);
//      System.out.println("object = " + PWVariantUtil.objectToVariant(object).toString());
//  }

    @Override
    public com.jacob.com.Variant changeParameters(String objectType, com.jacob.com.Variant paramList, com.jacob.com.Variant values) {
        return PWVariantUtil.objectToVariant(simulatorAuto.changeParameters(objectType, paramList, values));
    }

    @Override
    public String changeParametersSingleElement(String objectType, String[] paramList, String[] valueList) {
        synchronized (simulatorAuto) {
            if(paramList.length == valueList.length)
            {
                return convertToStrArray(((Object[]) simulatorAuto.changeParametersSingleElement(objectType, paramList, valueList)))[0];
            }
            else
            {
                return null;
            }
        }
    }

    @Override
    public com.jacob.com.Variant changeParametersMultipleElement(String objectType, String[] paramList, List<String[]> valueList) {
        return PWVariantUtil.objectToVariant(simulatorAuto.changeParametersMultipleElement(objectType, paramList, valueList));
    }

    @Override
    public com.jacob.com.Variant changeParametersMultipleElementFlatInput(String objectType, String[] paramList, String[] valueList) {
        return PWVariantUtil.objectToVariant(simulatorAuto.changeParametersMultipleElementFlatInput(objectType, paramList, (int) (valueList.length / paramList.length), valueList));
    }

    @Override
    public Object closeCase() {
        caseOpened = false;
        return simulatorAuto.closeCase();
    }

    @Override
    public com.jacob.com.Variant getCaseHeader() {
        return PWVariantUtil.objectToVariant(simulatorAuto.getCaseHeader(ConfigUtil.CASE_FILE_NAME));
    }

    @Override
    public com.jacob.com.Variant getFieldList(String objectType) {
        return PWVariantUtil.objectToVariant(simulatorAuto.getFieldList(objectType));
    }

    @Override
    public String[] getParametersSingleElement(String objectType, String[] paramList, String[] values) {
        synchronized (simulatorAuto) {
            try {
                Object[] result = (Object[]) simulatorAuto.getParametersSingleElement(objectType, paramList, values);
                if (result != null && result.length > 1) {
                    return convertToStrArray((Object[]) result[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String[] convertToStrArray(Object[] objects) {
        if (objects == null) {
            return new String[0];
        }
        String[] strings = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                strings[i] = "";
            } else {
                strings[i] = objects[i].toString();
            }
        }
        return strings;
    }

    @Override
    public com.jacob.com.Variant getParametersMultipleElement(String objectType, String[] paramList, String filterName) {
        return PWVariantUtil.objectToVariant(simulatorAuto.getParametersMultipleElement(objectType, paramList, filterName));
    }

    @Override
    public com.jacob.com.Variant getParametersMultipleElementFlatOutput(String objectType, String[] paramList, String filterName) {
        return PWVariantUtil.objectToVariant(simulatorAuto.getParametersMultipleElement(objectType, paramList, filterName));
    }

    @Override
    public Object getSpecificFieldList(String objectType, String[] fieldList) {
        return PWVariantUtil.objectToVariant(simulatorAuto.getSpecificFieldList(objectType, fieldList));
    }

    @Override
    public com.jacob.com.Variant getSpecificFieldMaxNum(String objectType, String fieldName) {
        return PWVariantUtil.objectToVariant(simulatorAuto.getSpecificFieldMaxNum(objectType, fieldName));
    }

    @Override
    public Object listOfDevices(String objectType, String filter) {
        return PWVariantUtil.objectToVariant(simulatorAuto.listOfDevices(objectType, filter));
    }

    @Override
    public com.jacob.com.Variant listOfDevicesAsVariantStrings(String objectType, String filter) {
        return PWVariantUtil.objectToVariant(simulatorAuto.listOfDevicesAsVariantStrings(objectType, filter));
    }

    @Override
    public Object listOfDevicesFlatOutput(String objectType, String filter) {
        return PWVariantUtil.objectToVariant(simulatorAuto.listOfDevicesFlatOutput(objectType, filter));
    }

    @Override
    public com.jacob.com.Variant loadState() {
        return PWVariantUtil.objectToVariant(simulatorAuto.loadState());
    }

    public synchronized Object reopenCase(String fileName) {
        synchronized (simulatorAuto) {
            Object obj = simulatorAuto.openCase(fileName);
            if (!caseOpened) {
                caseOpened = true;
                startClock();
            }
            return obj;
        }
    }
    @Override
    public synchronized Object openCase(String fileName) {
        return openCase(fileName, true);
    }

    public synchronized Object openCase(String fileName, boolean startClock) {
        synchronized (simulatorAuto) {
            if (!caseOpened) {
                Object obj = simulatorAuto.openCase(fileName);
                if (!caseOpened) {
                    caseOpened = true;
                    if(startClock)
                    {
                        startClock();
                    }
                }
                return obj;
            }
            return "";
        }
    }

    @Override
    public com.jacob.com.Variant openCaseType(String fileName, String fileType, String[] options) {
        return PWVariantUtil.objectToVariant(simulatorAuto.openCaseType(fileName, fileType, options));
    }

    @Override
    public com.jacob.com.Variant processAuxFile(String fileName) {
        return PWVariantUtil.objectToVariant(simulatorAuto.processAuxFile(fileName));
    }

    @Override
    public com.jacob.com.Variant loadContingencies(String fileName) {
        return null;
    }

    @Override
    public Object runScriptCommand(String statements) {
        synchronized (simulatorAuto) {
            if (simulatorAuto != null) {
                return simulatorAuto.runScriptCommand(statements);
            } else {
                return null;
            }
        }
    }

    @Override
    public Object saveCase(String fileName, String fileType, boolean overwrite) {
        synchronized (simulatorAuto) {
            return simulatorAuto.saveCase(fileName, fileType, overwrite);
        }
    }

    @Override
    public Object saveState() {
        synchronized (simulatorAuto) {
            return simulatorAuto.saveState();
        }
    }

    @Override
    public com.jacob.com.Variant sendToExcel(String objectType, String filterName, String[] fieldList) {
        return PWVariantUtil.objectToVariant(simulatorAuto.sendToExcel(objectType, filterName, fieldList));
    }

    @Override
    public com.jacob.com.Variant writeAuxFile(String fileName, String filterName, String objectType, String eString, boolean toAppend, String[] fieldList) {
        return PWVariantUtil.objectToVariant(simulatorAuto.writeAuxFile(fileName, filterName, objectType, toAppend, fieldList));
    }

    public boolean isCaseOpened() {
        return caseOpened;
    }

    @Override
    public void stop() {
        if(contingencyThread != null)
        {
            contingencyThread.stop();
        }
        if(savingThread != null)
        {
            savingThread.stop();
        }
        if(caseOpened)
        {
            simulatorAuto.closeCase();
        }
        caseOpened = false;
        simulatorAuto = null;
        ClassFactory.close();
        instance = null;
        try {
            finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        PWCom_com4j.getInstance().openCase(ConfigUtil.CASE_FILE_NAME);
//        PWCom_com4j.getInstance().openCase(ConfigUtil.CASE_FILE_NAME);
////        String[] param = {"BusNum", "BusNum:1", "LineCircuit", "LineStatus"};
////        String[] values = {"1", "31", "1", ""};
////        System.out.println(PWCom_com4j.getInstance().getParametersSingleElement("Branch", param, values));
////        System.out.println(PWCom_com4j.getInstance().getParametersSingleElement("Branch", param, values));
////        System.out.println(PWCom_com4j.getInstance().getParametersSingleElement("Branch", param, values));
////        System.out.println(PWCom_com4j.getInstance().getParametersSingleElement("Branch", param, values));
//        String[] param = {"BusNum", "BusKVVolt", "Frequency"};
//        int count = 0;
//        while (true) {
//            count++;
//            String[] values = {count + "", "", ""};
//            try {
//                for (String bus : PWCom_com4j.getInstance().getParametersSingleElement("Bus", param, values)) {
//                    System.out.print("bus = " + bus);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                count = 0;
//                continue;
//            }
//            System.out.println("");
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        ConfigUtil.POWER_WORLD_CLSID = "{0BDBD63F-C4A1-4226-9546-8964CED2C29B}";
        getInstance().openCase("C:\\EMA\\Demo\\smartpower\\SmartPower\\casefiles\\37Bus\\GSO_37Bus_dm.PWB");
        System.out.println("getInstance().getFeildValues() = " + getInstance().getFeildValues("Branch"));
    }

    public String getFeildValues(String deviceType) {
        try {
//            com.jacob.com.Variant variant = PWVariantUtil.objectToVariant(simulatorAuto.openCase(caseFile));
//            if (PWVariantUtil.variantToObject(variant).toString().trim().isEmpty()) {

            switch (deviceType) {
                case "CB":
                    deviceType = "Branch";
                    break;
                case "BUS":
                    deviceType = "Bus";
                    break;
                case "TRANSFORMER":
                    deviceType = "Transformer";
                    break;
                case "LOAD":
                    deviceType = "Load";
                    break;
                case "SHUNT":
                    deviceType = "Shunt";
                    break;
                case "GEN":
                    deviceType = "Gen";
                    break;
            }
            System.out.println("Loading metadata..." + deviceType);
            try {
                Object[] devices = (Object[]) simulatorAuto.listOfDevicesFlatOutput(deviceType, "");
                if (devices != null) {
                    System.out.println("Successfully loaded meta data");
                } else {
                    System.out.println("Error in loading meta data");
                    return null;
                }
                if (devices == null || devices.length <= 3 || devices[0] == null || devices[0].toString().trim().length() > 0) {
                    // error occurred
                    return null;
                } else if (devices.length > 3) {
                    int deviceCount = Integer.parseInt(devices[1].toString().trim());
                    int fieldCount = Integer.parseInt(devices[2].toString().trim());
                    System.out.println("Number of Devices : " + deviceCount);
                    System.out.println("Number of Fields Per Object : " + fieldCount);
                    String field = "";
                    for (int i = 0; i < deviceCount; i++) {
                        field = field + "\n";
                        for (int j = 0; j < fieldCount; j++) {
                            field = field + " " + devices[3 + i * fieldCount + j].toString().trim();
                        }
                    }
                    System.out.println(field);
                    return field.trim();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return null;
//            } else {
//                return null;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (simulatorAuto != null) {
                try {
                    simulatorAuto.closeCase();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
