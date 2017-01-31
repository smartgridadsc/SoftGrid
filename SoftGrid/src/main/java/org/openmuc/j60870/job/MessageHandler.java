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
package org.openmuc.j60870.job;

import it.illinois.adsc.ema.control.proxy.server.GatewayConListener;
//import it.illinois.adsc.ema.control.proxy.server.SecurityHandler;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.AsduWorker;
import org.openmuc.j60870.Connection;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by prageethmahendra on 18/7/2016.
 */
public class MessageHandler implements Runnable {

    private static Queue<MessageContext> messageContextQueue = new ConcurrentLinkedDeque<MessageContext>();
    private static MessageHandler instance;
    private ExecutorService executor = Executors.newFixedThreadPool(200);
    private static GatewayConListener gatewayConListener;

    private MessageHandler() {
    }

    private void start() {
        // start the hanlder thread
        new Thread(this).start();
    }

    public static MessageHandler getInstance() {
        if (instance == null) {
            instance = new MessageHandler();
        }
        return instance;
    }

    public static void handleMessage(ASdu aSdu, Connection connection, ContextState contextState) {
        messageContextQueue.add(new MessageContext(connection, aSdu, gatewayConListener, contextState));
    }

    public static void handleMessage(ASdu aSdu, Connection connection) {
        handleMessage(aSdu, connection, ContextState.REQUEST);
    }

    @Override
    public void run() {

        while (true) {
            MessageContext messageContext = messageContextQueue.poll();
            if (messageContext != null) {
                switch (messageContext.getState()) {
                    case REQUEST:
//                        if (SecurityHandler.getInstance().isEnabled()) {
//                            executor.execute(new AsduWorker(messageContext, new SecureConfirmationJob(gatewayConListener)));
//                        }
                        executor.execute(new AsduWorker(messageContext, new ConfirmationJob()));
//                        executor.execute(new AsduWorker(messageContext, new TranslatorJob()));
                        break;
                    case RESPONSE:
                        executor.execute(new AsduWorker(messageContext, new ResponseJob()));
                        break;
                }
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void start(GatewayConListener gatewayConListener) {
        if (this.gatewayConListener == null) {
            this.gatewayConListener = gatewayConListener;
            start();
        }
    }

    public int getQueueSize() {
        return messageContextQueue.size();
    }
}

