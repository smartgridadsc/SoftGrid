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
//package it.adsc.smartpower.substation.monitoring.ui.message;
//
//import com.alee.extended.tree.AsyncTreeDataUpdater;
//import com.mysql.jdbc.Connection;
//import org.openmuc.j60870.ASdu;
//
//import java.io.*;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by prageethmahendra on 22/7/2016.
// */
//public class MessageProcessor {
//    File logFile = new File("C:\\EMA\\Demo\\smartpower\\SmartPower\\sample log\\Traslation-2015-1104.txt");
//
//    public static void main(String[] args) {
//
//
//        MessageProcessor messageProcessor = new MessageProcessor();
//        messageProcessor.generateObjects();
//    }
//
//    private void generateObjects() {
//        Connection conn = null;
//
//        try {
//            // The newInstance() call is a work around for some
//            // broken Java implementations
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
//        } catch (Exception ex) {
//            // handle the error
//        }
//        try {
//            conn = (com.mysql.jdbc.Connection) DriverManager.getConnection("jdbc:mysql://localhost/sp_log?user=root&password=");
//            // Do something with the Connection
//        } catch (SQLException ex) {
//            // handle any errors
//            System.out.println("SQLException: " + ex.getMessage());
//            System.out.println("SQLState: " + ex.getSQLState());
//            System.out.println("VendorError: " + ex.getErrorCode());
//        }
//
//
//        int count = 0;
//        List<String> address = new ArrayList<String>();
//        if (logFile.exists()) {
//            try {
//                BufferedReader reader = new BufferedReader(new FileReader(logFile));
//                String str = "";
//                try {
//                    while ((str = reader.readLine()) != null) {
////                        if (str.contains("->") && !str.contains("TESTFR") && !str.contains("S_FORMAT") && !str.contains("START")) {
////                            System.out.println("str = " + str);
////                            Request request = new Request();
////                            request.setComString(str, count);
////
//////                            request.save(conn);
////                            System.out.println("request = " + request);
////                            count++;
////                        }
////                        if (str.contains("<-") && !str.contains("TESTFR") && !str.contains("S_FORMAT") && !str.contains("START")) {
////                            System.out.println("str = " + str);
////                            Response response = new Response();
////                            response.setComString(str, count);
//////                            response.save(conn);
////                            System.out.println("response = " + response);
////                            count++;
////                        }
////                        if(str.contains("Originator address:"))
////                        {
////                            String[] tokens = str.split(" ");
////                            if(!address.contains(tokens[3]))
////                            address.add(tokens[3]);
////                            if(!address.contains(tokens[6]))
////                            address.add(tokens[6]);
////                        }
//                        if(str.contains("IOA"))
//                        {
//                            String[] tokens = str.split(" ");
//                            if(!address.contains(tokens[2]))
//                                address.add(tokens[2]);
//                        }
//                        count++;
//
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            System.out.println("address.size() = " + address.size());
//            for (String addres : address) {
//                System.out.println("addres = " + addres);
//            }
//        } else {
//            System.out.println("File Not exists...!");
//        }
//        try {
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
