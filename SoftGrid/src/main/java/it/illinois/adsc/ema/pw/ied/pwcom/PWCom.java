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
package it.illinois.adsc.ema.pw.ied.pwcom;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.*;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;
import com4j.*;
import it.illinois.adsc.ema.pw.PWComFactory;
import it.illinois.adsc.ema.pw.ied.pwcom.PWComAPI;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import sun.nio.ch.FileLockImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.FileLock;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
* Created by prageethmahendra on 20/1/2016.
*/
@Deprecated
public class PWCom extends ActiveXComponent implements PWComAPI {
    public static final String PROGRAME_ID = "pwrworld.SimulatorAuto";
    private static PWCom instance;
    private boolean caseOpen = false;
    private Thread contingencyThread = null;
    private Thread savingThread = null;

    private PWCom(String programId) {
        super(programId);
    }

    public static synchronized PWCom getInstance() {
        if (instance == null) {
            init();
            instance = new PWCom(PROGRAME_ID);
        }
        return instance;
    }

    public void startClock() {
//  Clock Thread
        contingencyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Entermode(RUN)" + runScriptCommand("Entermode(CONTINGENCY);"));// SolvePrimalLP;"));// SolveFullSCOPF(OPF);"));
                while (true) {
                    synchronized (this) {
                        runScriptCommand("TSRunUntilSpecifiedTime(CONTINGENCY1);");
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
                    saveState();
                    saveCase(ConfigUtil.CASE_FILE_MONITOR, ConfigUtil.CASE_FILE_TYPE, true);
                }
            }
        });


        contingencyThread.start();
//        savingThread.start();
    }

    private List<String[][]> getCommandPack() {
        String[][] branches = {{"1", "31", "1"}, {"3", "40", "1"}, {"3", "41", "1"}, {"5", "18", "1"}, {"5", "44", "1"}, {"10", "13", "1"}, {"10", "19", "1"}, {"12", "17", "1"}, {"12", "18", "1"}, {"12", "27", "1"}, {"13", "55", "1"}, {"14", "34", "1"}, {"14", "t44", "1"}, {"15", "16", "1"}, {"15", "24", "1"}, {"15", "54", "1"}, {"15", "54", "1"}, {"15", "54", "1"}, {"16", "27", "1"}, {"17", "19", "1"}, {"18", "37", "1"}, {"18", "37", "1"}, {"20", "34", "1"}, {"20", "48", "1"}, {"20", "50", "1"}, {"21", "48", "1"}, {"21", "48", "1"}, {"24", "44", "1"}, {"31", "28", "1"}, {"32", "29", "1"}, {"29", "41", "1"}, {"56", "29", "1"}, {"30", "32", "1"}, {"30", "41", "1"}, {"31", "38", "1"}, {"33", "50", "1"}, {"35", "39", "1"}, {"35", "56", "1"}, {"39", "40", "1"}, {"39", "47", "1"}, {"47", "53", "1"}, {"48", "54", "1"}, {"54", "55", "1"}};
        List<String[][]> paramPacks = new ArrayList<String[][]>();
        String[] keys = {"BusNum", "BusNum:1", "LineCircuit", "LineStatus"};

        for (String[] busNumber : branches) {
            String[][] pramPack = new String[4][4];
            pramPack[0] = keys;
            pramPack[1][0] = busNumber[0];
            pramPack[1][1] = busNumber[1];
            pramPack[1][2] = busNumber[2];
            pramPack[1][3] = "Open";
            paramPacks.add(pramPack);
        }

        for (String[] busNumber : branches) {
            String[][] pramPack = new String[4][4];
            pramPack[0] = keys;
            pramPack[1][0] = busNumber[0];
            pramPack[1][1] = busNumber[1];
            pramPack[1][2] = busNumber[2];
            pramPack[1][3] = "Close";
            paramPacks.add(pramPack);
        }

        return paramPacks;
    }

    private List<String[][]> getAllBusParamPack() {
        List<String[][]> paramPacks = new ArrayList<String[][]>();
        String[] busNumbers = {"1", "3", "5", "10", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "24", "27", "28", "29", "30", "31", "32", "33", "34", "35", "37", "38", "39", "40", "41", "44", "47", "48", "50", "53", "54", "55", "56"};
        String[] keys = {"BusNum", "Frequency", "BusKVVolt"};

        for (String busNumber : busNumbers) {
            if (Integer.parseInt(busNumber) == 41) {
                String[][] pramPack = new String[3][3];
                pramPack[0] = keys;
                pramPack[1][0] = busNumber;
                pramPack[1][1] = "";
                pramPack[1][2] = "";
                paramPacks.add(pramPack);
            }
        }

        return paramPacks;
    }

    public Variant closeCase() {
        return this.invoke("CloseCase");
    }

    /*
       CtgName : String The contingency to obtain results from. Only one contingency be obtained at a time.
ObjFieldList: VariantA variant array of strings which may contain plots, subplots, or individual object/field pairs specifying the result variables to obtain.
StartTime : String The time in seconds in the simulation to begin retrieving results. If not specified, the start time of the simulation is used.
StopTime : String The time in seconds in the simulation to stop retrieving results. If not specified, the end time of the simulation is used.
       * **/
    public Variant TSGetContingencyResults(String ctgName, String[] objFieldList, String startTime, String endTime) {
        Variant variants = PWVariantUtil.objectToVariant(objFieldList);
        Variant result = Dispatch.call(this, "TSGetContingencyResults", ctgName, variants, startTime, endTime);
//        result = this.invoke("TSGetContingencyResults", variants, startTime, endTime);
        System.out.println("result = " + result.toSafeArray().toVariantArray()[0]);
        if (result.toSafeArray().toVariantArray()[0].toString().trim().isEmpty()) {
            int count = 0;
            for (Variant variant : result.toSafeArray().toVariantArray()) {
                count++;
                if (count == 1) {
                    continue;
                }
                System.out.println("Dimension  = " + count);
                if (variant.toSafeArray() != null) {
                    for (String value : variant.toSafeArray().toStringArray()) {
                        System.out.println("value = " + value);
                    }
                } else {
                    System.out.println("variant = " + variant);
                }
            }
        }
        return result;
    }

    @Override
    public Variant loadState() {
        Variant result = Dispatch.call(this, "LoadState");
        System.out.println("LoadState result = " + result);
        return result;
    }

    @Override
    public Variant saveState() {
        Variant result = Dispatch.call(this, "saveState");
        System.out.println("saveState result = " + result);
        return result;
    }

    @Override
    public Variant saveCase(String fileName, String fileType, boolean overwrite) {
        Variant result = Dispatch.call(this, "SaveCase", fileName, fileType, overwrite);
        System.out.println("SaveCase result = " + result);
        return result;
    }

    @Override
    public synchronized Variant openCase(String fileName) {
        Variant variant = new Variant(" ");
        if (!caseOpen) {
            caseOpen = true;
            variant = this.invoke("OpenCase", fileName);
            startClock();
        }
        return variant;
    }

    @Override
    public Variant openCaseType(String fileName, String fileType, String[] options) {
        Variant result = Dispatch.call(this, "OpenCaseType", fileName, fileType, PWVariantUtil.objectToVariant(options));
        System.out.println("OpenCaseType result = " + result);
        return result;
    }

    @Override
    public Variant getCaseHeader() {
        Variant variant = Dispatch.call(this, "GetCaseHeader", ConfigUtil.CASE_FILE_NAME);
        System.out.println("GetCaseHeader variant = " + variant);
        return variant;
    }

    @Override
    public Variant getFieldList(String objectType) {
        Variant variant = this.invoke("GetFieldList", objectType);
        System.out.println("GetFieldList variant = " + variant);
        return variant;
    }

//    @Override
//    public Variant getParametersSingleElement(String objectType, String[] paramList, String[] valueList) {
//        // todo
//        Variant paramVariant = PWVariantUtil.objectToVariant(paramList);
//        Variant valueVariant = PWVariantUtil.objectToVariant(valueList);
//        Variant result = Dispatch.call(this, "GetParametersSingleElement", objectType, paramVariant, valueVariant);
////        if (objectType.equalsIgnoreCase("PWCASEINFORMATION")) {
////            System.out.println(objectType + " IED result = " + result);
////        }
//        return result;
//    }


    @Override
    public String[] getParametersSingleElement(String objectType, String[] paramList, String[] values) {
        return new String[0];
    }

    @Override
    public Variant getParametersMultipleElement(String objectType, String[] paramList, String filterNames) {
        Variant paramVariant = PWVariantUtil.objectToVariant(paramList);
        Variant result = Dispatch.call(this, "GetParametersMultipleElement", objectType, paramVariant, filterNames);
        System.out.println("GetParametersMultipleElement result = " + result);
        return result;
    }

    @Override
    public Variant getParametersMultipleElementFlatOutput(String objectType, String[] paramList, String filterNames) {
        Variant paramVariant = PWVariantUtil.objectToVariant(paramList);
        Variant result = Dispatch.call(this, "GetParametersMultipleElementFlatOutput", objectType, paramVariant, filterNames);
        System.out.println("GetParametersMultipleElementFlatOutput result = " + result);
        return result;
    }

    @Override
    public Object getSpecificFieldList(String objectType, String[] feildList) {
        Variant fieldList = PWVariantUtil.objectToVariant(feildList);
        Object result = Dispatch.call(this, "GetSpecificFieldList", objectType, fieldList);
//        System.out.println("result = " + result);
        return result;
    }

    @Override
    public Variant getSpecificFieldMaxNum(String objectType, String fieldName) {
        Variant result = Dispatch.call(this, "GetSpecificFieldMaxNum", objectType, fieldName);
        System.out.println("GetSpecificFieldMaxNum result = " + result);
        return result;
    }

    @Override
    public Object listOfDevices(String objectType, String filter) {
        Object result = Dispatch.call(this, "ListOfDevicesAsVariantStrings", objectType, filter);
//      System.out.println("result = " + Dispatch.call(this, "ListOfDevices", objectType, filter));
        return result;
    }

    @Override
    public Variant listOfDevicesAsVariantStrings(String objectType, String filter) {
        Variant result = Dispatch.call(this, "ListOfDevicesAsVariantStrings", objectType, filter);
        System.out.println("ListOfDevicesAsVariantStrings result = " + result);
        return result;
    }

    @Override
    public Object listOfDevicesFlatOutput(String objectType, String filter) {
        Object result = Dispatch.call(this, "ListOfDevicesAsVariantStrings", objectType, filter);
        System.out.println("ListOfDevicesAsVariantStrings result = " + result);
        return result;
    }

    @Deprecated
    @Override
    public Variant changeParameters(String objectType, Variant paramList, Variant values) {
        return null;
    }

    @Override
    public String changeParametersSingleElement(String objectType, String[] paramList, String[] valueList) {
        Variant paramVariant = PWVariantUtil.objectToVariant(paramList);
        Variant valueVariant = PWVariantUtil.objectToVariant(valueList);
        try {
            Variant result = Dispatch.call(this, "changeParametersSingleElement", objectType, paramVariant, valueVariant);
            System.out.println("changeParametersSingleElement result = " + result);
            return PWVariantUtil.variantToObject(result).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Variant changeParametersMultipleElement(String objectType, String[] paramList, List<String[]> valueList) {
        Variant paramVariant = PWVariantUtil.objectToVariant(paramList);
        Variant[] variants = new Variant[valueList.size()];
        int i = 0;
        for (String[] value : valueList) {
            variants[i] = PWVariantUtil.objectToVariant(value);
        }
        Variant result = Dispatch.call(this, "changeParametersMultipleElement", objectType, paramVariant, variants);
        System.out.println("changeParametersMultipleElement result = " + result);
        return result;
    }

    @Override
    public Variant changeParametersMultipleElementFlatInput(String objectType, String[] paramList, String[] valueList) {
        Variant paramVariant = PWVariantUtil.objectToVariant(paramList);
        Variant result = Dispatch.call(this, "changeParametersMultipleElementFlatInput", objectType, paramVariant, (int) (valueList.length / paramList.length), PWVariantUtil.objectToVariant(valueList));
        System.out.println("changeParametersMultipleElementFlatInput result = " + result);
        return result;
    }

    @Override
    public Variant runScriptCommand(String statements) {
        Variant result = Dispatch.call(this, "RunScriptCommand", statements);
        return result;
    }

    @Override
    public Variant processAuxFile(String fileName) {
        Variant result = Dispatch.call(this, "ProcessAuxFile", fileName);
        System.out.println("ProcessAuxFile result = " + result);
        return result;
    }

    @Override
    public Variant loadContingencies(String fileName) {
        Variant result = PWVariantUtil.objectToVariant("");
        result = Dispatch.call(this, "LoadContingencies", fileName, result, true);
        System.out.println("LoadContingencies result = " + result);
        return result;
    }

    @Override
    public Variant sendToExcel(String objectType, String filterName, String[] fieldList) {
        Variant fieldListVarient = null;
        if (fieldList == null || fieldList.length == 0) {
            fieldListVarient = PWVariantUtil.objectToVariant("ALL");
        } else {
            fieldListVarient = PWVariantUtil.objectToVariant(fieldList);
        }
        Variant result = Dispatch.call(this, "SendToExcel", objectType, filterName, fieldListVarient);
        System.out.println("SendToExcel result = " + result);
        return result;
    }


    @Override
    public Variant writeAuxFile(String fileName, String filterName, String objectType, String eString, boolean toAppend, String[] fieldList) {
        Variant fieldListVarient = null;
        if (fieldList == null || fieldList.length == 0) {
            fieldListVarient = PWVariantUtil.objectToVariant("ALL");
        } else {
            fieldListVarient = PWVariantUtil.objectToVariant(fieldList);
        }
        Variant result = Dispatch.call(this, "WriteAuxFile", fileName, filterName, objectType, toAppend, fieldListVarient);
        System.out.println("WriteAuxFile result = " + result);
        return result;
    }

    public Variant createEvent(String ctgName, String eventTime, String WhoAmI, String EventString) {
        String objectType = "TSCONTINGENCYELEMENT";
//      String[] fieldList = {"TSCTGName", "TSTimeInSeconds", "WhoAmI", "TSEventString"};
//      String[] data = {ctgName, eventTime, WhoAmI, EventString};
//      String script = "CreateData(" + objectType + ",[\"TSCTGName\",\"TSTimeInSeconds\",\"WhoAmI\",\"TSEventString\"],[\"" + ctgName + "\",\"" + eventTime + "\",\"" + WhoAmI + "\",\"" + EventString +"\"]);";
//      "DATA (TSCONTINGENCYELEMENT, [TSCTGName,TSTimeInSeconds,WhoAmI,TSEventString,Enabled,FilterName],AUXDEF,YES)"
        String script = "CreateData(TSCONTINGENCYELEMENT,[TSCTGName,TSTimeInSeconds,WhoAmI,TSEventString,Enabled,FilterName ],[\"CONTINGENCY1\",3.0,\"Branch '1' '31' '1'\",\"OPEN BOTH\", \"CHECK\", \"\"]);";
        String script2 = "CreateData(TSCONTINGENCYELEMENT,[TSCTGName,TSTimeInSeconds,WhoAmI,TSEventString,Enabled,FilterName ],[\"CONTINGENCY1\",1.0,\"Branch '1' '31' '1'\",\"OPEN BOTH\", \"CHECK\", \"\"]);";
        String script3 = "CreateData(TSCONTINGENCYELEMENT,[TSCTGName,TSTimeInSeconds,WhoAmI,TSEventString,Enabled,FilterName ],[\"CONTINGENCY1\",2.0,\"Branch '1' '31' '1'\",\"OPEN BOTH\", \"CHECK\", \"\"]);";
        String script4 = "CreateData(TSCONTINGENCYELEMENT,[TSCTGName,TSTimeInSeconds,WhoAmI,TSEventString,Enabled,FilterName ],[\"CONTINGENCY1\",10.0,\"Branch '1' '31' '1'\",\"OPEN BOTH\", \"CHECK\", \"\"]);";
        openCase(ConfigUtil.CASE_FILE_NAME);
        runScriptCommand(script2);
        runScriptCommand(script3);
        runScriptCommand(script4);
        return runScriptCommand(script);
    }

    public void deleteEvent() {
        runScriptCommand("Delete(TSCONTINGENCYELEMENT)");
    }

    private static void init() {
        /**
         * `System.getProperty("os.arch")`
         * It'll tell us on which platform Java Program is executing. Based on that we'll load respective DLL file.
         * Placed under same folder of program file(.java/.class).
         */
        ConfigUtil.init();
        String libFileName = System.getProperty("os.arch").equals("amd64") ? "jcob.dll" : "jacob-1.17-x86.dll";
        try {
            /* Read DLL file*/
            File libFile = new File(libFileName);
            System.out.println("libFile.getAbsolutePath() = " + libFile.getAbsolutePath());
            File temporaryDll = File.createTempFile("jacob", ".dll");
            /**
             * `System.setProperty(LibraryLoader.JACOB_DLL_PATH, temporaryDll.getAbsolutePath());`
             * Set System property same like setting java home path in system.
             *
             * `LibraryLoader.loadJacobLibrary();`
             * Load JACOB library in current System.
             */
            System.setProperty(LibraryLoader.JACOB_DLL_PATH, libFile.getAbsolutePath());
            LibraryLoader.loadJacobLibrary();

            /**
             * Create ActiveXComponent using CLSID. You can also use program id here.
             * Next line(commented line/compProgramID) shows you how you can create ActiveXComponent using ProgramID.
             */
//            ActiveXComponent compCLSID = new ActiveXComponent("clsid:{00024500-0000-0000-C000-000000000046}");
//            ActiveXComponent compCLSID = new ActiveXComponent("pwrworld.SimulatorAuto");
            System.out.println("The Library been loaded, and an activeX component been created");

            /**
             * This is function/method of Microsoft Excel to use it with COM bridge.
             * Excel methods and its use can be found on
             * http://msdn.microsoft.com/en-us/library/bb179167(v=office.12).aspx
             *
             * Understand code:
             * 1. Make Excel visible
             * 2. Get workbook of excel object.
             * 3. Open 1test.xls1 file in excel
             */
            /* Temporary file will be removed after terminating-closing-ending the application-program */
            temporaryDll.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        contingencyThread.stop();
        savingThread.stop();
        instance = null;
        finalize();
    }

    @Override
    public boolean isCaseOpened() {
        return true;
    }
}
