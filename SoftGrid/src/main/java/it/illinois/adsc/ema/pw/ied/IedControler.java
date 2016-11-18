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

package it.illinois.adsc.ema.pw.ied;

import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import it.illinois.adsc.ema.pw.PWComFactory;
import it.illinois.adsc.ema.pw.ied.pwcom.PWComAPI;
import it.illinois.adsc.ema.pw.log.PWLogFormatter;
import it.illinois.adsc.ema.softgrid.common.ied.IedControlAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.*;

/**
 * Created by prageethmahendra on 21/1/2016.
 */
public class IedControler implements IedControlAPI {

    private static IedControler instance;
    private static Logger logger = null;
    private static HashMap<Long, List<String>> logDataMap = new HashMap<Long, List<String>>();
    public static List<String> LOG_DATA = new ArrayList<String>();
    private PWComAPI pwComBridge;
    private static long START_TIME = System.currentTimeMillis();
    public static boolean RESET_TIME = false;
    boolean closed = false;

    private IedControler() {
        super();
    }


    public void init() {
        if(pwComBridge == null) {
            pwComBridge = PWComFactory.getSingletonPWComInstance();
            initLogger();
            closed = false;
        }
    }


    public void stop() {
        closed = true;

        pwComBridge.stop();
        instance = null;
    }

    @Override
    public boolean isCaseOpened() {
        return pwComBridge.isCaseOpened();
    }

    private void log(String objectType, String[] paramList, String[] valueList) {
        synchronized (this) {
            if (RESET_TIME) {
                START_TIME = System.currentTimeMillis();
                logDataMap.put(START_TIME, LOG_DATA = new ArrayList<String>());
                RESET_TIME = false;
            }
        }
        long time = System.currentTimeMillis() - START_TIME;
        double seconds = ((double) time) / 1000.00;
        StringBuffer sb = new StringBuffer("\"OPEN_ALL\" " + seconds);
        sb.append(" \"").append(objectType);
        String[] keys = getKeyList(objectType);
        for (int i = 0; i < keys.length; i++) {
            for (int j = 0; j < paramList.length; j++)
                if (keys[i].equals(paramList[j])) {
                    sb.append(" '").append(valueList[j]).append("'");
                }
        }
        sb.append("\" \"");
        if (paramList[keys.length].equalsIgnoreCase("linestatus")) {
            sb.append("OPEN BOTH\" ");
        }
        sb.append("\"CHECK\" \"\"");
        String command = sb.toString();
        LOG_DATA.add(command);
        logger.severe(command);
    }

    private String[] getKeyList(String objectType) {
        switch (objectType.toLowerCase()) {
            case "branch":
            case "transformer":
                String[] BranchKeys = {"BusNum", "BusNum:1", "LineCircuit"};
                return BranchKeys;
            case "bus":
                String[] BusKeys = {"BusNum"};
                return BusKeys;
            case "generator":
                String[] generatorKeys = {"BusNum", "GenID"};
                return generatorKeys;
            case "load":
                String[] loadKeys = {"BusNum", "LoadID"};
                return loadKeys;
            case "shunt":
                String[] shuntKeys = {"BusNum", "ShuntID"};
                return shuntKeys;
            default:
                return null;
        }
    }

    @Override
    public String changeParametersSingleElement(String objectType, String[] paramList, String[] valueList) {
        if (ConfigUtil.MANUAL_EXPERIMENT_MODE) {
            log(objectType, paramList, valueList);
        }
        if (!closed) {
            return pwComBridge.changeParametersSingleElement(objectType, paramList, valueList).toString();
        }
        return null;
    }

    @Override
    public String changeParametersMultipleElement(String objectType, String[] paramList, List<String[]> valueList) {
        return pwComBridge.changeParametersMultipleElement(objectType, paramList, valueList).toString();
    }

    @Override
    public String changeParametersMultipleElementFlatInput(String objectType, String[] paramList, String[] valueList) {
        return pwComBridge.changeParametersMultipleElementFlatInput(objectType, paramList, valueList).toString();
    }

    @Override
    public String closeCase() {
        return pwComBridge.closeCase().toString();
    }

    @Override
    public String getCaseHeader() {
        return pwComBridge.getCaseHeader().toString();
    }

    @Override
    public String getFieldList(String objectType) {
        return pwComBridge.getFieldList(objectType).toString();
    }

    @Override
    public String[] getParametersSingleElement(String objectType, String[] paramList, String[] values) {
        String[] result = null;
        if (!closed) {
            result = pwComBridge.getParametersSingleElement(objectType, paramList, values);
            return result;
//            if (variant != null) {
//                Object value = PWVariantUtil.variantToObject(variant);
//                variant.safeRelease();
//                if (value != null) {
//                    return value.toString();
//                }
//            }

//            return null;
        } else {
//            return "Closed...!";
            String[] closed ={"Closed"};
            return closed;
        }
    }


    @Override
    public String getParametersMultipleElement(String objectType, String[] paramList, String filterName) {
        return pwComBridge.getParametersMultipleElement(objectType, paramList, filterName).toString();
    }

    @Override
    public String getParametersMultipleElementFlatOutput(String objectType, String[] paramList, String filterName) {
        return pwComBridge.getParametersMultipleElementFlatOutput(objectType, paramList, filterName).toString();
    }

    @Override
    public String getSpecificFieldMaxNum(String objectType, String fieldName) {
        return pwComBridge.getSpecificFieldMaxNum(objectType, fieldName).toString();
    }

    @Override
    public String listOfDevicesAsVariantStrings(String objectType, String filter) {
        return pwComBridge.listOfDevicesAsVariantStrings(objectType, filter).toString();
    }

    @Override
    public String loadState() {
        return pwComBridge.loadState().toString();
    }

    @Override
    public String openCase() {
        return pwComBridge.openCase(ConfigUtil.CASE_FILE_NAME).toString();
    }

    @Override
    public String openCaseType(String[] options) {
        return pwComBridge.openCaseType(ConfigUtil.CASE_FILE_NAME, ConfigUtil.CASE_FILE_TYPE, options).toString();
    }

    @Override
    public String processAuxFile(String AUX_FILE_NAME) {
        return pwComBridge.processAuxFile(AUX_FILE_NAME).toString();
    }

    @Override
    public String runScriptCommand(String statements) {
        return pwComBridge.runScriptCommand(statements).toString();
    }

    @Override
    public String saveCase() {
        return pwComBridge.saveCase(ConfigUtil.CASE_FILE_TYPE, ConfigUtil.CASE_FILE_TYPE, true).toString();
    }

    @Override
    public String saveState() {
        return null;
    }

    @Override
    public String sendToExcel(String objectType, String filterName, String[] fieldList) {
        return pwComBridge.sendToExcel(objectType, filterName, fieldList).toString();
    }

    @Override
    public String writeAuxFile(String filterName, String objectType, String eString, boolean toAppend, String[] fieldList, String AUX_FILE_NAME) {
        return pwComBridge.writeAuxFile(AUX_FILE_NAME, filterName, objectType, eString, toAppend, fieldList).toString();
    }

    private static synchronized void initLogger() {
        FileHandler fileTxt = null;
        Formatter formatterTxt;
        if (logger != null) {
            return;
        }
        // suppress the logging output to the console
        logger = Logger.getLogger("PW");
        logger.setLevel(Level.INFO);
        try {
            fileTxt = new FileHandler(ConfigUtil.LOG_FILE +"PW");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileTxt != null) {
            // create a TXT formatter
            logger.addHandler(fileTxt);
        }
        formatterTxt = new PWLogFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(new ConsoleHandler());
        AUXGenerator.startThread();
        // create an HTML formatter
    }

    public static IedControler getInstance() {
        if(instance == null)
        {
            instance = new IedControler();
        }
        return instance;
    }
}
