package it.illinois.adsc.ema.interceptor;

import java.io.File;
import java.lang.ClassLoader;
import java.net.*;

import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

/**
 * Created by prageethmahendra on 5/5/2017.
 *
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

    public InterceptorListObject initInterceptors(){


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


            // to create linked list of InterceptorNode by setting previous and next node
            for (int i = 0; i < interceptorClasses.length; i++) {

                currentNode = new InterceptorNode();

                if (rootNode == null) {
                    rootNode = currentNode;
                }

                try {
                    Class cls = cl.loadClass(interceptorClasses[i]);
                    curObj = cls.getConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

                currentNode.setCurInterceptor((Interceptor) curObj);

                //set previous and next node of each InterceptorNode
                if(previousNode != null) {
                    currentNode.setPreviousInterceptor(previousNode);
                    previousNode.setNextInterceptor(currentNode);
                }

                previousNode = currentNode;

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // return reference to root node (first node)
        return rootNode;
    }


}
