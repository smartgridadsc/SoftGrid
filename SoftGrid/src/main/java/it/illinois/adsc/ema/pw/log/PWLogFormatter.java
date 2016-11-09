package it.illinois.adsc.ema.pw.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by prageethmahendra on 26/4/2016.
 */
public class PWLogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return formatMessage(record) +"\n";
    }
}
