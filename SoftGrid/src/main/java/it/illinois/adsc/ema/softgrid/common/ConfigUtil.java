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
package it.illinois.adsc.ema.softgrid.common;

import it.illinois.adsc.ema.pw.SoftGridComType;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by prageethmahendra on 5/8/2016.
 */
public class ConfigUtil {
    public static boolean CONFIG_INITIALISED = false;
    public static String MAIN_CONFIG_REF_FILE = "smartPowerConf";
    public static String CONFIG_FILE_PATH = "";
    public static String LOG_FILE = "";
    public static String CONFIG_FOLDER = "";
    public static String LIMIT_VIOLATION_CSV_PATH = "";
    public static String MESSAGE_MONITOR_LOG_FILE = "IEDMonitor.log";
    public static String POWER_WORLD_EXE = "";
    public static String POWER_WORLD_CLSID = "";
    public static String CONFIG_PEROPERTY_FILE = "";
    public static String SERVER_TYPE = "";
    public static String IP = "";
    public static String CASE_FILE_PATH = "..\\SoftGrid\\casefiles\\TempCaseFiles\\";
    public static String CASE_FILE_NAME = "CaseFile.PWB";
    public static String CASE_FILE_TEMP = "";
    public static String CASE_FILE_MONITOR = "";
    public static int VIRTUAL_CLOCK_CYCLE_DURATION = 20;
    public static String CLOCK_CONTINGENCY_NAME = "CONTINGENCY1";
    public static String CASE_FILE_TYPE = "PWB";
    public static String SCL_PATH = "";
    public static boolean GENERATE_SCL = false;
    public static String PW_TO_SCL_MAPPING = "";
    public static String IED_TYPE_TO_FIELD_MAPPING = "";
    public static String EXP_DATA_FILE = "";
    public static String LIMIT_VIOLATION_RECORD_FILE = "";
//  public static String PYTHON_START_BAT_FILE_PATH = "";
    public static String PYTHON_FILE_PATH = "";
    public static String PYTHON_FILE_NAME = "";
//  public static String TEMP_STATE_FILE_PATH = "";
    public static boolean CC_CONSOLE_INTERACTIVE = false;
    public static boolean PROXY_SERVER_LOCAL_API_MODE = false;
//  public static boolean MANUAL_EXPERIMENT_MODE = true;
    public static String ACM_SECURITY_PROPERTY_FILE = "";
    public static int GATEWAY_CC_PORT = -1;
    public static boolean MULTI_IP_IED_MODE_ENABLED = false;
    public static String DEFAULT_IED_PORT = "102";
    public static String TRANSIENT_MYSQL_HOST = "localhost";
    public static String TRANSIENT_MYSQL_USERNAME = "root";
    public static String TRANSIENT_MYSQL_PASSWORD = "root";
    public static String GATEWAY_IED_MAPING_FILE = "GatewayIEDmap.xml";
    public static SoftGridComType PHYSICAL_SIMMULATOR_API = SoftGridComType.COM4J_PW;

    public static boolean init() {
        CONFIG_INITIALISED = true;
        if (CASE_FILE_PATH.length() == 0) {
            JOptionPane.showMessageDialog(new JPanel(), "PowerWorld Case file not specified.");
            CONFIG_INITIALISED = false;
            System.exit(0);
            return false;
        }
        CASE_FILE_NAME = new File(CASE_FILE_NAME).getName();
        CASE_FILE_PATH = CASE_FILE_PATH.endsWith(File.separator) ? CASE_FILE_PATH : CASE_FILE_PATH + File.separator;
        String clonedSCLFile = new File(CASE_FILE_PATH + "OP_" + CASE_FILE_NAME).getAbsolutePath();
        CASE_FILE_TEMP = new File(CASE_FILE_PATH + "TEMP_" + CASE_FILE_NAME).getAbsolutePath();
        CASE_FILE_MONITOR = new File(CASE_FILE_PATH + "MONITOR_" + CASE_FILE_NAME).getAbsolutePath();
        CASE_FILE_NAME = new File(CASE_FILE_PATH + CASE_FILE_NAME).getAbsolutePath();
        if (!new File(CASE_FILE_NAME).exists()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(new JPanel(), "Unable to find the case file at \n" + new File(CASE_FILE_NAME).getAbsolutePath());
                    System.exit(0);
                }
            });
            CONFIG_INITIALISED = false;
            return false;
        }
        try {
//          Clone PWB file
            Files.copy(new File(CASE_FILE_NAME).toPath(), new File(CASE_FILE_TEMP).toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(new File(CASE_FILE_NAME).toPath(), new File(CASE_FILE_MONITOR).toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(new File(CASE_FILE_NAME).toPath(), new File(clonedSCLFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
//          Clone PWD file
            String pwdFile = CASE_FILE_NAME.substring(0, CASE_FILE_NAME.length() - 4) + ".PWD";
//          System.out.println("pwdFile = " + pwdFile);
//          String tempPwdFile = CASE_FILE_TEMP.substring(0, CASE_FILE_TEMP.length() - 4) + ".PWD";
//          String monitorPwdFile = CASE_FILE_MONITOR.substring(0, CASE_FILE_MONITOR.length() - 4) + ".PWD";
//          String clonedPwdFile = clonedSCLFile.substring(0, clonedSCLFile.length() - 4) + ".PWD";
//          Files.copy(new File(pwdFile).toPath(), new File(tempPwdFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
//          Files.copy(new File(pwdFile).toPath(), new File(monitorPwdFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
//          Files.copy(new File(pwdFile).toPath(), new File(clonedPwdFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
            CASE_FILE_NAME = clonedSCLFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
