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
package it.illinois.adsc.ema;

import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import it.illinois.adsc.ema.softgrid.common.IEDLogFormatter;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.logging.*;

/**
 * Created by prageethmahendra on 27/12/2016.
 */
public class IEDLoggerFactory {
    private static Logger logger;

    public static Logger getLogger()
    {
        init();
        return logger;
    }

    private static synchronized void init()
    {
        FileHandler fileTxt = null;
        Formatter formatterTxt;
        if (logger != null) {
            return;
        }
        // suppress the logging output to the console
        logger = Logger.getLogger("IED");
        logger.setLevel(Level.INFO);
        try {
            fileTxt = new FileHandler(ConfigUtil.LOG_FILE, 500000, 2);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileTxt != null) {
            logger.addHandler(fileTxt);// create a TXT formatter
        }
        formatterTxt = new IEDLogFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(new ConsoleHandler());
        // create an HTML formatter
    }
}
