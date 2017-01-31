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
package it.illinois.adsc.ema.control.ied.pw;

/**
 * Created by prageethmahendra on 11/2/2016.
 */

import it.illinois.adsc.ema.control.IEDDataSheetHandler;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IEDThreadPool {

    public void execute(List<IEDWorkerThread> iedWorkerThreads) {
        IEDDataSheetHandler.dumpIEDDataSheet(iedWorkerThreads);
            ExecutorService executor = Executors.newFixedThreadPool(iedWorkerThreads.size());
            for (IEDWorkerThread iedWorkerThread : iedWorkerThreads) {
                executor.execute(iedWorkerThread);
            }
            executor.shutdown();
            System.out.println("Finished all IED threads");
        }

    }

