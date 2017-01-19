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
package org.openmuc.j60870;

import it.illinois.adsc.ema.control.proxy.server.GatewayConListener;
import it.illinois.adsc.ema.control.proxy.server.handlers.ICommandHandler;
import org.openmuc.j60870.job.ContextState;
import org.openmuc.j60870.job.MessageHandler;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by prageethmahendra on 30/5/2016.
 */
public class IEC60870D104Receiver implements ConnectionEventListener, ICommandHandler {
    private Connection connection;
    protected int connectionId;
    private ServerSap serverSap;
    private GatewayConListener gatewayConListener;

    protected IEC60870D104Receiver(GatewayConListener gatewayConListener) {
        super();
        this.gatewayConListener = gatewayConListener;
        MessageHandler.getInstance().start(gatewayConListener);
    }

    @Override
    public void newASdu(ASdu aSdu) {
//        System.out.println(getClass().toString() + " : " + aSdu.toString().replace('\n', ','));
//        System.out.println("Handling the Received Command....\n\n");
        MessageHandler.handleMessage(aSdu, connection);
    }


    public void setConnection(Connection connection) {
        System.out.println("A Security Enabled Proxy Gateway Has Been Initiated Using TCP/IP. Will Listen For A StartDT Request.");
        try {
            this.connection = connection;
            connection.waitForStartDT(this, 5000);
        } catch (IOException e) {
            System.out.println("Connection Interrupted While Waiting For StartDT: " + e.getMessage() + ". Will quit.");
            return;
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println("Started Data Transfer On Connection Will Listen For Incoming Commands.");
    }

    @Override
    public void connectionClosed(IOException e) {
        System.out.println("Connection (" + connectionId + ") Was Closed. " + e.getMessage());
        gatewayConListener.ccConnectionClosed(this);
    }


    public void forwardToCC(ASdu aSdu) {
       MessageHandler.handleMessage(aSdu, connection, ContextState.RESPONSE);
    }

    public void stop() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection = null;
        }
    }
}


