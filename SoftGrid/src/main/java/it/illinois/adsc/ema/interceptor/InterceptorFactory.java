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

import java.io.File;
import java.lang.ClassLoader;
import java.net.*;

import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import org.eclipse.persistence.dynamic.DynamicClassLoader;

/**
 * Created by prageethmahendra on 5/5/2017.
 * <p>
 * This class will create linked list of InterceptorListObject by loading the interceptor class
 * put under specified configuration path under sequence defined by its global array list
 */
public class InterceptorFactory {

    //The root folder to put all interceptor classes which implemented Interceptor interface
    static String configPath = null;

    //List of interceptor class names to construct sequence of InterceptorNode
    static String[] interceptorClasses = null;

    public InterceptorFactory() {
        try {
            configPath = ConfigUtil.INTERCEPTOR_ROOT;
            interceptorClasses = new String[ConfigUtil.INTERCEPTOR_CLASSES.length];

            for (int i = 0; i < ConfigUtil.INTERCEPTOR_CLASSES.length; i++) {

                if (ConfigUtil.INTERCEPTOR_PACKAGE != "")
                    interceptorClasses[i] = ConfigUtil.INTERCEPTOR_PACKAGE + "." + ConfigUtil.INTERCEPTOR_CLASSES[i];
                else
                    interceptorClasses[i] = ConfigUtil.INTERCEPTOR_CLASSES[i];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InterceptorListObject initInterceptors() {


        // Create a File object on the root of the directory containing the class file
        File file = new File(configPath);

        Object curObj;
        InterceptorNode rootNode = null;
        InterceptorNode currentNode = rootNode;
        InterceptorNode previousNode = null;

        try {
            // Convert File to a URL
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            ClassLoader cl = new URLClassLoader(urls);
            LoggingInterceptor2 defaultInterceptor = new LoggingInterceptor2();
            currentNode = new InterceptorNode();
            rootNode = currentNode;
            currentNode.setCurInterceptor(defaultInterceptor);
            previousNode = currentNode;

            // to create linked list of InterceptorNode by setting previous and next node
            for (int i = 0; i < interceptorClasses.length; i++) {
                if (interceptorClasses[i].isEmpty() || interceptorClasses[i].endsWith(".")) {
                    continue;
                }
                try {
                    Class cls = cl.loadClass(interceptorClasses[i]);
                    curObj = cls.getConstructor().newInstance();
                    currentNode = new InterceptorNode();
                    if (rootNode == null) {
                        rootNode = currentNode;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                currentNode.setCurInterceptor((Interceptor) curObj);
                //set previous and next node of each InterceptorNode
                 currentNode.setPreviousInterceptor(previousNode);
                 previousNode.setNextInterceptor(currentNode);
                 previousNode = currentNode;
            }
            if (previousNode == null) {
                currentNode = new InterceptorNode();
                previousNode = currentNode;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // return reference to root node (first node)
        return rootNode;
    }
}
