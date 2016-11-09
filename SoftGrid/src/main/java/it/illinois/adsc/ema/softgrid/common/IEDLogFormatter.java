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

package it.illinois.adsc.ema.softgrid.common;

import java.security.Timestamp;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by prageethmahendra on 26/4/2016.
 */
public class IEDLogFormatter extends java.util.logging.Formatter {
    private boolean noPrefix = false;

    public IEDLogFormatter() {
    }

    public IEDLogFormatter(boolean noPrefix) {
        this.noPrefix = noPrefix;
    }

    @Override
    public String format(LogRecord record) {
        if (noPrefix) {
            return formatMessage(record) + "\n";
        }
        return "Data:" + System.currentTimeMillis() + ":" + formatMessage(record) + "\n";
    }
}
