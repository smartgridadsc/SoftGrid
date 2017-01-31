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
package it.illinois.adsc.ema.control;


import it.illinois.adsc.ema.control.center.ControlCenterClient;
import it.illinois.adsc.ema.control.conf.generator.ConfigGenerator;
import it.illinois.adsc.ema.control.ied.SmartPowerIEDServer;
import it.illinois.adsc.ema.control.ied.StatusHandler;
import it.illinois.adsc.ema.control.ied.pw.IEDServerFactory;
import it.illinois.adsc.ema.control.proxy.server.ProxyServer;
import it.illinois.adsc.ema.pw.ConfigReader;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import org.openmuc.openiec61850.ServiceError;

import java.io.*;

/**
 * Created by prageethmahendra on 3/2/2016.
 */
public class SmartPowerControler {

    public static void main(final String[] args) {
        initiate(args);
    }

    public static void initiate(String[] args) {
        StatusHandler.reset();
        try {
            StatusHandler.statusChanged("NOT_STARTED");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Initializing..!");
        if (args[0].trim().equalsIgnoreCase("-F")) {
            if ((ConfigUtil.CONFIG_PEROPERTY_FILE == null || ConfigUtil.CONFIG_PEROPERTY_FILE.trim().length() == 0) && args.length > 0 && new File(args[1]).exists()) {
                ConfigReader.getAllProperties(new File(args[1]));
            }
            if (args.length >= 4 && !args[3].equalsIgnoreCase("local")) {
                ConfigUtil.IP = args[3];
            }
            if (args.length >= 5 && !args[4].equalsIgnoreCase("local")) {
                ConfigUtil.GATEWAY_CC_PORT = Integer.parseInt(args[4].trim());
            }
            try {
                StatusHandler.statusChanged("CC_NOT_STARTED " + ConfigUtil.IP + ":" + ConfigUtil.GATEWAY_CC_PORT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Generating Config XML...");
            try {
                ConfigGenerator.generateConfigXml(ConfigUtil.SCL_PATH, ConfigUtil.CONFIG_PEROPERTY_FILE, ConfigUtil.IP);
                System.out.println("Starting server/client threads...");
                IEDServerFactory.createAndStartIEDServer(ConfigUtil.CONFIG_PEROPERTY_FILE, ConfigUtil.SERVER_TYPE, ConfigUtil.CC_CONSOLE_INTERACTIVE);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServiceError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Initialization Complete ...!");

    }

    public static void killAll() {
        IEDServerFactory.killAll();
    }

    public static void setIEDLogEventListener(LogEventListener iedLogEventListener) {
        SmartPowerIEDServer.setCommonIEDEventListener(iedLogEventListener);
    }

    public static void setPRXLogEventListener(LogEventListener iedLogEventListener) {
        ProxyServer.setPRXEventListener(iedLogEventListener);
    }

    public static ControlCenterClient getControlCenterClient() {
        return IEDServerFactory.controlCenterClient;
    }

}
