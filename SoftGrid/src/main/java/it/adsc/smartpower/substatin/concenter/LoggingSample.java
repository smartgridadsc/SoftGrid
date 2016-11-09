package it.adsc.smartpower.substatin.concenter;

/**
* Created by prageethmahendra on 29/8/2016.
*/
import org.apache.log4j.Logger;
/*
* Logger for the application
*/
public class LoggingSample {
    private static Logger logger = Logger.getLogger("CyberSAGE");

    public static Logger getLogger() {

        return logger;
    }

    public static void setLogger(Logger logger) {
        LoggingSample.logger = logger;
    }
}