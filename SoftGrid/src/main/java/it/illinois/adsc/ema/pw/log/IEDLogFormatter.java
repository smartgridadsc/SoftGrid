//package it.ilinois.adsc.ema.pw.log;
//
//import java.security.Timestamp;
//import java.util.logging.Formatter;
//import java.util.logging.LogRecord;
//
///**
// * Created by prageethmahendra on 26/4/2016.
// */
//public class IEDLogFormatter extends java.util.logging.Formatter {
//    private boolean noPrefix = false;
//
//    public IEDLogFormatter() {
//    }
//
//    public IEDLogFormatter(boolean noPrefix) {
//        this.noPrefix = noPrefix;
//    }
//
//    @Override
//    public String format(LogRecord record) {
//        if (noPrefix) {
//            return formatMessage(record) + "\n";
//        }
//        return "Data:" + System.currentTimeMillis() + ":" + formatMessage(record) + "\n";
//    }
//}
