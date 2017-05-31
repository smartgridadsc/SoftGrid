package it.illinois.adsc.ema.interceptor;

import org.openmuc.j60870.ASdu;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by prageeth.g on 29/5/2017.
 */
// this is the flyweight factory to reduce memory leaks and reuse of objects
public class ASduThreadFactory {
    private static final int MAX_THREAD_OBJECTS = 500;
    private static Vector<ASduThread> aSduThreads = new Vector<>();

    public static ASduThread createThread() {
        synchronized (aSduThreads) {
            if (aSduThreads.isEmpty()) {
                return new ASduThread(null, null);
            } else {
                ASduThread aSduThread = aSduThreads.get(0);
                aSduThreads.remove(aSduThread);
                return aSduThread;
            }
        }
    }

    public static void returnExecutedThreads(ASduThread aSduThread) {
        if (aSduThread != null && aSduThreads.size() < MAX_THREAD_OBJECTS) {
            aSduThread.setAsdu(null);
            aSduThread.setCurrentNode(null);
            synchronized (aSduThreads) {
                aSduThreads.add(aSduThread);
            }
        }
    }
}
