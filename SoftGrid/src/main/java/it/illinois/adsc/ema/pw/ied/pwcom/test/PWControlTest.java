//package it.ilinois.adsc.ema.pw.ied.pwcom.test;
//
//import com.jacob.com.PWVariantUtil;
//import com.jacob.com.Variant;
//import it.illinois.adsc.ema.pw.ied.pwcom.PWCom;
//
///**
// * Created by prageethmahendra on 22/1/2016.
// */
//public class PWControlTest {
//
//    private void setCircuitStatus(boolean open) {
//        System.out.println("Start Circuit Breaker Command");
//        String lineStatus = "Open";
//
//        if (open) {
//            lineStatus = "Open";
//        } else {
//            lineStatus = "Close";
//        }
//        String[] params = {"BusNum", "BusNum:1", "LineCircuit", "LineStatus"};
//        String[] values = {"1", "2", "1", lineStatus};
//        String[] query = {"1", "2", "1", ""};
//        // check the current status
//        Variant v = PWCom.getInstance().getParametersSingleElement("Branch", params, query);
//        if (!PWVariantUtil.variantToObject(v).toString().contains(lineStatus)) {
//            PWCom.getInstance().changeParametersSingleElement("Branch", params, values);
//            v = PWCom.getInstance().getParametersSingleElement("Branch", params, query);
//            if (PWVariantUtil.variantToObject(v).toString().contains(lineStatus)) {
//                System.out.println("Circuit Breaker Command Successfully Executed. Current Status : " + lineStatus);
//            }
//        }
//        else
//        {
//            System.out.println("Command Discarded : Requested Circuit Breaker Status Already Updated.");
//        }
//
//        System.out.println("End  Circuit Breaker Command\"");
//    }
//
//    private void testGenerator() {
//        System.out.println("Start Generator interrogation Command");
//        String[] params = {"BusNum", "GenID", "GenMVRMin", "Frequency:1"};
//        String[] values = {"31", "1", "",""};
//        // check the current status
//        Variant v = PWCom.getInstance().getParametersSingleElement("Gen", params, values);
//
//        System.out.println("End  Circuit Breaker Command\"");
//    }
//
//
//    private void testLoad() {
//        System.out.println("Start Load Interrogation Command");
//        String[] params = {"BusNum", "LoadID", "LoadMW", };
//        String[] values = {"5", "1", ""};
//        String[] paramsBranch = {"BusNum", "BusNum:1", "LineCircuit","LineAmp:1" };
//        String[] valuesBranch = {"5", "44", "1",""};
//        // check the current status
//        Variant v = PWCom.getInstance().getParametersSingleElement("Load", params, values);
//        System.out.println("v = " + v);
//        v = PWCom.getInstance().getParametersSingleElement("Branch", paramsBranch, valuesBranch);
//        System.out.println("v = " + v);
//        values[2] = "10";
//        PWCom.getInstance().changeParametersSingleElement("Load", params, values);
//        values[2] = "";
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        v = PWCom.getInstance().getParametersSingleElement("Load", params, values);
//        System.out.println("v = " + v);
//        v = PWCom.getInstance().getParametersSingleElement("Branch", paramsBranch, valuesBranch);
//        System.out.println("v = " + v);
//        v  = PWCom.getInstance().runScriptCommand("TSRunUntilSpecifiedTime(CONTINGENCY1);");
//        System.out.println("v = " + v);
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        v = PWCom.getInstance().getParametersSingleElement("Branch", paramsBranch, valuesBranch);
//        System.out.println("v = " + v);
//        System.out.println("End  Circuit Breaker Command\"");
//    }
//
//    private void testBus() {
//        System.out.println("Start Bus interrogation Command");
//        String[] params = {"BusNum", "Frequency"};
//        String[] values = {"17", ""};
//        // check the current status
//        Variant v = PWCom.getInstance().getParametersSingleElement("Bus", params, values);
//
//        System.out.println("End  Bus \"");
//    }
//
//
//    private void testScriptTransient() {
//        System.out.println("Start Script Transient Command");
//        // check the current status
//        Variant v = PWCom.getInstance().runScriptCommand("Bus");
//
//        System.out.println("End  Script \"");
//    }
//
//    private void testCaseInfor() {
//        System.out.println("Start Script Transient Command");
//        String[] loadparams = {"BusNum", "LoadID", "LoadMW", };
//        String[] loadvalues = {"5", "1", ""};
//        // check the current status
//        String[] values = {""};
//        String[] params = {"OverloadRank"};
//        Variant v = PWCom.getInstance().getParametersSingleElement("PWCaseInformation", params, values);
//        System.out.println("v = " + v);
//        v = PWCom.getInstance().getParametersSingleElement("Load", loadparams, loadvalues);
//        System.out.println("v = " + v);
//       loadvalues[2] = "10000";
//        PWCom.getInstance().changeParametersSingleElement("Load", loadparams, loadvalues);
//        loadvalues[2] = "";
//        v = PWCom.getInstance().getParametersSingleElement("Load", loadparams, loadvalues);
//        System.out.println("v = " + v);
//        v  = PWCom.getInstance().runScriptCommand("TSRunUntilSpecifiedTime(CONTINGENCY1);");
//        System.out.println("v = " + v);
//        v  = PWCom.getInstance().runScriptCommand("TSRunUntilSpecifiedTime(CONTINGENCY1);");
//        System.out.println("v = " + v);
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        v = PWCom.getInstance().getParametersSingleElement("PWCaseInformation", params, values);
//        System.out.println("v = " + v);
//
//
//        System.out.println("End  Script \"");
//    }
//
//
//    public static void main(String[] args) {
////        PWComTest pwComTest = new PWComTest();
////        pwComTest.openCaseTest();
////        PWControlTest circuitBreakerTest = new PWControlTest();
////      circuitBreakerTest.setCircuitStatus(true);
////        circuitBreakerTest.testGenerator();
////        circuitBreakerTest.testCaseInfor();
////        circuitBreakerTest.testLoad();
////        circuitBreakerTest.testBus();
////        pwComTest.saveStateTest();
////        pwComTest.closeCaseTest();
//        System.out.println(123&65535);
//    }
//}
