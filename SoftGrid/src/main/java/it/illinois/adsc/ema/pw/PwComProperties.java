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
