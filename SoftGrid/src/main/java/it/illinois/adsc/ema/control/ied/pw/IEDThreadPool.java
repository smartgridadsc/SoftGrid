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

