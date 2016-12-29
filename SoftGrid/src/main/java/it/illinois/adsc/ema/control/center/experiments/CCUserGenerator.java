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
//package it.illinois.adsc.ema.control.center.experiments;
//
//import it.illinois.adsc.ema.control.center.ControlCenterClient;
//import it.illinois.adsc.ema.control.center.ControlCenterContext;
//import it.illinois.adsc.ema.control.ied.pw.IEDServerFactory;
//import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
//import org.openmuc.openiec61850.ServiceError;
//
//import java.io.IOException;
//import java.util.Random;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Created by prageethmahendra on 22/6/2016.
// */
//public class CCUserGenerator {
//    private static ExecutorService executor = Executors.newFixedThreadPool(10);
//    private static Timer uploadCheckerTimer = null;
//    private static int count = 0;
//
//
//    public static void main(String[] args) {
//        if(uploadCheckerTimer != null)
//        {
//            return;
//        }
//        if (IEDServerFactory.proxyIpPorts == null) {
//            try {
//                //Initilize the proxy map
//                IEDServerFactory.createAndStartIEDServer(ConfigUtil.CONFIG_PEROPERTY_FILE, "NULL", ConfigUtil.CC_CONSOLE_INTERACTIVE);
//            } catch (ServiceError serviceError) {
//                serviceError.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        Thread genThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
////                while (true) {
////                    ControlCenterClient controlCenterClient = new ControlCenterClient(ControlCenterClient.getInstance(null).getConnection(), new ControlCenterContext(true, SmartPowerControler.CONFIG_FILE), false);
////                    executor.readyToExecute(controlCenterClient);
////                    try {
////                        Thread.sleep(20000);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
//
//                uploadCheckerTimer = new Timer(true);
//                uploadCheckerTimer.schedule(
//                        new TimerTask() {
//                            public void run() {
//                                if (count < (ControlCenterClient.manualExperimentMode ? 140 : 0)) {
//                                    Random random = new Random();
//                                    try {
//                                        Thread.sleep((count == 2 ? 1000 : 0) + Math.abs(random.nextLong() % 1000));
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                    System.out.println("New Client Created...!");
//                                    ControlCenterClient controlCenterClient = new ControlCenterClient(ControlCenterClient.getInstance(null).getConnection(), new ControlCenterContext(true, ConfigUtil.CONFIG_PEROPERTY_FILE), false);
//                                    executor.execute(controlCenterClient);
//                                    count++;
//                                }
//                            }
//                        }, 1000, 3000);
////                }
//            }
//        });
//        genThread.start();
//    }
//}
