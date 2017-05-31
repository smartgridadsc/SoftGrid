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

        * @author Edwin Lesmana Tjiong
*/

package it.illinois.adsc.ema.interceptor;

import it.illinois.adsc.ema.control.proxy.server.ProxyServer;
import org.openmuc.j60870.ASdu;

import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Edwin on 11-May-17.
 * <p>
 * This class is container which will initialize list of interceptors defined in config file
 * and running them in the sequence defined in InterceptorFactory class. Processed ASdu package
 * will be returned to caller class
 */
public class InterceptorContainer implements ASduListener, InterceptorListener {

    private static InterceptorContainer curContainer = null;

    private static InterceptorFactory curFactory = null;

    private static ExecutorService executorService = null;

    private static InterceptorListObject currentNode = null;

    private HashSet<ASduEndpoint> aSduEndpoints = new HashSet<>();

    private InterceptorContainer() {
        //Set the configuration parameter
        curFactory = new InterceptorFactory();
        //Initialize interceptors and return the reference to first interceptor
        currentNode = curFactory.initInterceptors();

    }

    public void init() {
        ProxyServer.getInstance().addASduListeners(this);
    }

    public static InterceptorContainer getInstance() {
        if (curContainer == null) {
            curContainer = new InterceptorContainer();
            executorService = Executors.newFixedThreadPool(100);
        }
        return curContainer;
    }

    @Override
    public void interceptCompleted(ASdu aSdu, ASduThread aSduThread) {
        ASduThreadFactory.returnExecutedThreads(aSduThread);
        for (ASduEndpoint aSduEndpoint : aSduEndpoints) {
            aSduEndpoint.validForExecution(aSdu);
        }
    }

    public void addEndPoint(ASduEndpoint aSduEndpoint) {
        if (aSduEndpoint != null) {
            aSduEndpoints.add(aSduEndpoint);
        }
    }

    @Override
    public void ASduReceived(ASdu aSdu, ASduEndpoint aSduEndpoint) {
        if (aSduEndpoint != null) {
            aSduEndpoints.add(aSduEndpoint);
        }
        ASduThread aSduThread = ASduThreadFactory.createThread();
        aSduThread.setAsdu(aSdu);
        aSduThread.setCurrentNode(currentNode);
        aSduThread.addInterceptorListener(this);
        try {
            executorService.execute(aSduThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
