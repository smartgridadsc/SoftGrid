//package it.ilinois.adsc.ema.pw;
//
//import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
//
///**
// * Created by prageethmahendra on 5/8/2016.
// */
//public class ConfigUtil {
//    public static boolean CONFIG_INITIALISED = false;
//    public static String MAIN_CONFIG_REF_FILE = "smartPowerConf";
//    public static String CONFIG_FILE_PATH = "";
//    public static String LOG_FILE = "";
//    public static String CONFIG_FOLDER = "";
//    public static String LIMIT_VIOLATION_CSV_PATH = "";
//    public static String MESSAGE_MONITOR_LOG_FILE = "IEDMonitor.log";
//    public static String POWER_WORLD_EXE = "";
//    public static String POWER_WORLD_CLSID = "";
//    public static String CONFIG_PEROPERTY_FILE = "";
//    public static String SERVER_TYPE = "";
//    public static String IP = "";
//    public static String CASE_FILE_PATH = "";
//    public static String CASE_FILE_NAME = "";
//    public static String CASE_FILE_TEMP = "";
//    public static String CASE_FILE_MONITOR = "";
//    public static int VIRTUAL_CLOCK_CYCLE_DURATION = 20;
//    public static String CLOCK_CONTINGENCY_NAME = "CONTINGENCY1";
//    public static String CASE_FILE_TYPE = "PWB";
//    public static String SCL_PATH = "";
//    public static boolean GENERATE_SCL = false;
//    public static String PW_TO_SCL_MAPPING = "";
//    public static String IED_TYPE_TO_FIELD_MAPPING = "";
//    public static String EXP_DATA_FILE = "";
//    public static String LIMIT_VIOLATION_RECORD_FILE = "";
//    public static String PYTHON_START_BAT_FILE_PATH = "";
//    public static String PYTHON_FILE_PATH = "";
//    public static String PYTHON_FILE_NAME = "";
//    public static String TEMP_STATE_FILE_PATH = "";
//    public static boolean CC_CONSOLE_INTERACTIVE = false;
//    public static boolean PROXY_SERVER_LOCAL_API_MODE = false;
//    public static boolean MANUAL_EXPERIMENT_MODE = false;
//    public static String ACM_SECURITY_PROPERTY_FILE = "";
//
//    public static void init() {
//        CONFIG_INITIALISED = true;
//        if (CASE_FILE_PATH.length() == 0) {
//            return;
//        }
//        CASE_FILE_NAME = new File(CASE_FILE_NAME).getName();
//        CASE_FILE_PATH = CASE_FILE_PATH.endsWith(File.separator) ? CASE_FILE_PATH : CASE_FILE_PATH + File.separator;
//        String clonedSCLFile = new File(CASE_FILE_PATH + "OP_" + CASE_FILE_NAME).getAbsolutePath();
//        CASE_FILE_TEMP = new File(CASE_FILE_PATH + "TEMP_" + CASE_FILE_NAME).getAbsolutePath();
//        CASE_FILE_MONITOR = new File(CASE_FILE_PATH + "MONITOR_" + CASE_FILE_NAME).getAbsolutePath();
//        CASE_FILE_NAME = new File(CASE_FILE_PATH + CASE_FILE_NAME).getAbsolutePath();
//
//        try {
//            //Clone PWB file
//            Files.copy(new File(CASE_FILE_NAME).toPath(), new File(CASE_FILE_TEMP).toPath(), StandardCopyOption.REPLACE_EXISTING);
//            Files.copy(new File(CASE_FILE_NAME).toPath(), new File(CASE_FILE_MONITOR).toPath(), StandardCopyOption.REPLACE_EXISTING);
//            Files.copy(new File(CASE_FILE_NAME).toPath(), new File(clonedSCLFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
//
//
//            // Clone PWD file
//            String pwdFile = CASE_FILE_NAME.substring(0, CASE_FILE_NAME.length() - 4) + ".PWD";
//            System.out.println("pwdFile = " + pwdFile);
//            String tempPwdFile = CASE_FILE_TEMP.substring(0, CASE_FILE_TEMP.length() - 4) + ".PWD";
//            String monitorPwdFile = CASE_FILE_MONITOR.substring(0, CASE_FILE_MONITOR.length() - 4) + ".PWD";
//            String clonedPwdFile = clonedSCLFile.substring(0, clonedSCLFile.length() - 4) + ".PWD";
//            Files.copy(new File(pwdFile).toPath(), new File(tempPwdFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
//            Files.copy(new File(pwdFile).toPath(), new File(monitorPwdFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
//            Files.copy(new File(pwdFile).toPath(), new File(clonedPwdFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
//
//            CASE_FILE_NAME = clonedSCLFile;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
