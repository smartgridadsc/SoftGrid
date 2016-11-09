//package it.ilinois.adsc.ema.pw.ied.pwcom.test;
//
//import com.jacob.com.Variant;
//import it.illinois.adsc.ema.pw.PwComProperties;
//import it.illinois.adsc.ema.pw.ied.pwcom.PWCom;
//import junit.framework.TestCase;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//
///**
// * Created by prageethmahendra on 20/1/2016.
// */
//public class PWComTest extends TestCase {
//
//    @Before
//    public void init() {
//        assertNotNull(PWCom.getInstance());
//    }
//
//    @Test
//    public void openCaseTest() {
//        System.out.println("Test Open Case");
//        Variant v = PWCom.getInstance().openCase(PwComProperties.CASE_FILE_NAME);
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void openCaseTypeTest() {
//        System.out.println("Test Open Case");
//        String[] strings = {""};
//        Variant v = PWCom.getInstance().openCaseType(PwComProperties.CASE_FILE_NAME, PwComProperties.CASE_FILE_TYPE, strings);
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void getCaseHeader() {
//        System.out.println("Test Get Case Header");
//        Variant v = PWCom.getInstance().getCaseHeader();
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void getParametersSingleElementTest() {
//        System.out.println("Test Parameter Single Element");
//        String[] params = {"BusNum"};
//        String[] values = {"1"};
//        Variant v = PWCom.getInstance().getParametersSingleElement("Bus", params, values);
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void changeParametersSingleElementTest() {
//        System.out.println("Test changeParametersSingleElement");
//        String[] params = {"BusNum", "BusPUVolt"};
//        String[] values = {"1", "10.0"};
//        Variant v = PWCom.getInstance().changeParametersSingleElement("Bus", params, values);
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void changeParametersMultipleElement() {
//        System.out.println("Test changeParametersSingleElement");
//        String[] params = {"BusNum", "BusPUVolt"};
//        String[] values = {"1", "10.0"};
//        ArrayList valueList = new ArrayList();
//        valueList.add(values);
//        Variant v = PWCom.getInstance().changeParametersMultipleElement("Bus", params, valueList);
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void changeParametersMultipleElementFlatInput() {
//        System.out.println("Test changeParametersMultipleElementFlatInput");
//        String[] params = {"BusNum", "BusPUVolt"};
//        String[] values = {"1", "10.0"};
//        Variant v = PWCom.getInstance().changeParametersMultipleElementFlatInput("Bus", params, values);
//        System.out.println("SUCCESS");
//    }
//
//
//    @Test
//    public void getParametersMultipleElementTest() {
//        System.out.println("Test getParametersMultipleElementTest");
//        String[] params = {"BusNum"};
//        Variant v = PWCom.getInstance().getParametersMultipleElement("Bus", params, "");
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void getParametersMultipleElementFlatOutputTest() {
//        System.out.println("Test getParametersMultipleElementFlatOutput");
//        String[] params = {"BusNum"};
//        Variant v = PWCom.getInstance().getParametersMultipleElementFlatOutput("Bus", params, "");
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void getSpecificFieldListTest() {
//        System.out.println("Test getSpecificFieldList");
//        String[] feildList = {"BusNum"};
//        Object v = PWCom.getInstance().getSpecificFieldList("Bus", feildList);
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void getSpecificFieldMaxNumTest() {
//        System.out.println("Test getSpecificFieldMaxNum");
//        Object v = PWCom.getInstance().getSpecificFieldMaxNum("Bus", "BusNum");
//        System.out.println("SUCCESS");
//    }
//
//
//    @Test
//    public void listOfDevicesTest() {
//        System.out.println("Test listOfDevices via listOfDevicesAsVariantStrings");
//        Object v = PWCom.getInstance().listOfDevices("Bus", "");
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void listOfDevicesFlatOutput() {
//        System.out.println("Test listOfDevicesFlatOutput");
//        Object v = PWCom.getInstance().listOfDevicesFlatOutput("Bus", "");
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void listOfDevicesAsVariantStringsTest() {
//        System.out.println("Test listOfDevicesAsVariantStrings");
//        Variant v = PWCom.getInstance().listOfDevicesAsVariantStrings("Bus", "");
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void getFieldListTest() {
//        System.out.println("Test Get Field List");
//        Variant v = PWCom.getInstance().getFieldList("Bus");
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void saveStateTest() {
//        System.out.println("Test saveState");
//        Variant v = PWCom.getInstance().saveState();
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void loadStateTest() {
//        System.out.println("Test loadState");
//        Variant v = PWCom.getInstance().loadState();
//        System.out.println("SUCCESS");
//    }
//
//    private void saveCaseTest() {
//        System.out.println("Test saveStateTest");
//        Variant v = PWCom.getInstance().saveCase(PwComProperties.CASE_FILE_NAME, PwComProperties.CASE_FILE_TYPE, true);
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void runScriptCommandTest() {
//        System.out.println("Test runScriptCommand");
//        PWCom.getInstance().runScriptCommand("My Statement");
//        System.out.println("SUCCESS");
//    }
//
//
//    @Test
//    public void closeCaseTest() {
//        System.out.println("Test Close Case");
//        PWCom.getInstance().closeCase();
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void processAuxFileTest() {
//        System.out.println("Test processAuxFile");
//        PWCom.getInstance().processAuxFile("C:\\EMA\\New\\smartpower\\SmartPower\\COPY_CONTINGENCY1.aux");
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void sendToExcelTest() {
//        System.out.println("Test sendToExcel");
//        PWCom.getInstance().sendToExcel("Bus", "", null);
//        System.out.println("SUCCESS");
//    }
//
//    @Test
//    public void writeAuxFileTest() {
//        System.out.println("Test writeAuxFile");
//        String[] field = {""};
//        PWCom.getInstance().writeAuxFile("C:\\EMA\\New\\smartpower\\SmartPower\\COPY_CONTINGENCY1.aux", "", "Bus", "Field", true, field);
//        System.out.println("SUCCESS");
//    }
//
//    @After
//    public void exit() {
//        System.exit(0);
//    }
//
//    public static void main(String[] args) {
//        PWComTest pwComTest = new PWComTest();
//        pwComTest.init();
//        pwComTest.openCaseTest();
////        pwComTest.openCaseTypeTest();
////        pwComTest.getCaseHeader();
////        pwComTest.getFieldListTest();
////        pwComTest.getParametersSingleElementTest();
////        pwComTest.getParametersMultipleElementTest();
////        pwComTest.getParametersMultipleElementFlatOutputTest();
////        pwComTest.getSpecificFieldListTest();
////        pwComTest.getSpecificFieldMaxNumTest();
////        pwComTest.processAuxFileTest();
////        pwComTest.listOfDevicesTest();
////        pwComTest.listOfDevicesAsVariantStringsTest();
////        pwComTest.saveCaseTest();
////        pwComTest.saveStateTest();
////        pwComTest.loadStateTest();
//        pwComTest.getParametersSingleElementTest();
////        pwComTest.changeParametersMultipleElement();
////        pwComTest.changeParametersMultipleElementFlatInput();
////        pwComTest.getParametersMultipleElementTest();
////        pwComTest.runScriptCommandTest();
////        pwComTest.listOfDevicesFlatOutput();
////        pwComTest.saveCaseTest();
////        pwComTest.sendToExcelTest();
////        pwComTest.writeAuxFileTest();
//        pwComTest.closeCaseTest();
//        pwComTest.exit();
//    }
//}
