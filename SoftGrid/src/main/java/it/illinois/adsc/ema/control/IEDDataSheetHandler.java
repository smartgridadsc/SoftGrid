package it.illinois.adsc.ema.control;

import it.illinois.adsc.ema.control.ied.pw.IEDWorkerThread;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import it.illinois.adsc.ema.webservice.web.resources.FileRequest;

import java.io.*;
import java.util.List;

/**
 * Created by prageethmahendra on 21/10/2016.
 */
public class IEDDataSheetHandler {
    public static void dumpIEDDataSheet(List<IEDWorkerThread> iedWorkerThreads) {
        if (iedWorkerThreads != null) {
            try {
                File file = getDataSheetFile();
                if (file == null || !file.exists()) {
                    return;
                }
                BufferedWriter writer = null;
                FileWriter wr = null;
                try {
                    wr = new FileWriter(file);
                    writer = new BufferedWriter(wr);
                    int i = 0;
                    for (IEDWorkerThread iedWorkerThread : iedWorkerThreads) {
                        i++;
                        writer.write(iedWorkerThread.toString() + "\n");
                        writer.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                        if (wr != null) {
                            try {
                                wr.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static File getDataSheetFile() {
        File file = new File(ConfigUtil.LOG_FILE);
        String logPath = file.getAbsolutePath().replace(file.getName(), "");
        String iedDataSheetFilePath = logPath + File.separator + "IEDDataSheet.csv";
        file = new File(iedDataSheetFilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static boolean isProxyConnectionAllowed(int portNumber) {

        File file = getDataSheetFile();
        if (file == null || !file.exists()) {
            return false;
        }
        BufferedReader bufferedReader = null;
        FileReader reader = null;
        String port = String.valueOf(portNumber);

        try {
            bufferedReader = new BufferedReader(reader = new FileReader(file));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.endsWith(port)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
