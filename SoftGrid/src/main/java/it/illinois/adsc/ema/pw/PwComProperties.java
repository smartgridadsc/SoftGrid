//package it.ilinois.adsc.ema.pw;
//
//import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
//
///**
// * Created by prageethmahendra on 9/6/2016.
// */
//public class PwComProperties {
////    public static String CASE_FILE_TEMP_PATH = new File("..\\SmartPower\\casefiles\\Temp_GSO_37Bus_dm.PWB").getAbsolutePath();
////    public static String CASE_FILE_MONITOR_PATH = new File("..\\SmartPower\\casefiles\\O_Temp_GSO_37Bus_dm.pwb").getAbsolutePath();
////    public static String CASE_FILE_NAME = new File("..\\SmartPower\\casefiles\\GSO_37Bus_dm.PWB").getAbsolutePath();
//    public static final String CASE_FILE_TYPE = "PWB";
//    public static int virtualClockDurationMilis = 20;
//
//    public static void init() {
////  37 bus
////        String mainPath = "..\\SmartPower\\casefiles\\37Bus\\";
////        CASE_FILE_TEMP_PATH = new File(mainPath + "Temp_GSO_37Bus_dm.PWB").getAbsolutePath();
////        CASE_FILE_MONITOR_PATH = new File(mainPath + "O_Temp_GSO_37Bus_dm.pwb").getAbsolutePath();
////        CASE_FILE_NAME = new File(mainPath + "GSO_37Bus_dm.PWB").getAbsolutePath();
////  2000 bus
////        String mainPath = "..\\SmartPower\\casefiles\\2000Bus\\";
////        CASE_FILE_TEMP_PATH = new File(mainPath + "Texas2000_June2016-Temp.pwb").getAbsolutePath();
////        CASE_FILE_MONITOR_PATH = new File(mainPath + "Texas2000_June2016-Monitor.pwb").getAbsolutePath();
////        CASE_FILE_NAME = new File(mainPath + "Texas2000_June2016.pwb").getAbsolutePath();
////  118 bus
////        String mainPath = "..\\SmartPower\\casefiles\\118Bus\\";
////        CASE_FILE_TEMP_PATH = new File(mainPath + "IEEE 118 Bus.pwb").getAbsolutePath();
////        CASE_FILE_MONITOR_PATH = new File(mainPath + "IEEE 118 Bus.pwb").getAbsolutePath();
////        CASE_FILE_NAME = new File(mainPath + "IEEE 118 Bus.pwb").getAbsolutePath();
//
////          150 bus
////        String mainPath = "..\\SmartPower\\casefiles\\150Bus\\";
////        CASE_FILE_TEMP_PATH = new File(mainPath + "uiuc-150bus-Temp.pwb").getAbsolutePath();
////        CASE_FILE_MONITOR_PATH = new File(mainPath + "uiuc-150bus-Monitor.pwb").getAbsolutePath();
////        CASE_FILE_NAME = new File(mainPath + "uiuc-150bus.pwb").getAbsolutePath();
//
////        try {
////            Files.copy(new File(ConfigUtil.CASE_FILE_NAME).toPath(), new File(PwComProperties.CASE_FILE_TEMP_PATH).toPath(), StandardCopyOption.REPLACE_EXISTING);
////            PwComProperties.CASE_FILE_NAME = PwComProperties.CASE_FILE_TEMP_PATH;
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }
//}
