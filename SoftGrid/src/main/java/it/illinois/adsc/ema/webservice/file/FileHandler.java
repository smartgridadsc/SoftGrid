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
package it.illinois.adsc.ema.webservice.file;

import it.illinois.adsc.ema.common.webservice.FileType;
import it.illinois.adsc.ema.pw.ConfigReader;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileHandler {
    private static String LOG_ARCHIVE_PATH = "archive";
    public static String LAST_DOWNLOADED_FILE = "";
    public static String LAST_DOWNLOADED_FILE_TYPE = "";
    public static String LAST_UPLOADED_FILE_PATH = "";
    public static String LAST_UPLOADED_FILE_NAME = "";
    private static boolean propertiesLoaded = false;

    /**
     * This method generates a new file name and a bufferdwriter
     *
     * @return
     */
    public static BufferedWriter getNewFileWriter(String clientId, String fileType) {
        if (fileType == null || fileType.trim().isEmpty()) {
            return null;
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(getNewFOS(clientId, fileType)));
        return bufferedWriter;
    }

    /**
     * This method closes the buffered writer
     *
     * @param bufferedWriter
     */
    public static void close(BufferedWriter bufferedWriter) {
        if (bufferedWriter != null) {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * This method generates a BufferedReader for a given input stream
     *
     * @param attachmentInputStream
     * @return
     */
    public static BufferedReader getNewStreamReader(InputStream attachmentInputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(attachmentInputStream));
        return bufferedReader;
    }

    /**
     * Thid method closes the buffered reader
     *
     * @param bufferedReader
     */
    public static void close(BufferedReader bufferedReader) {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // todo Instead of sending one file at a time. the File request api function should be modified to send
    //todo ziped multiple files at once. Therefore below methods are added.

    public static StreamingOutput getLogFileUploadStream(String lastFileName) {

        List<File> files = new ArrayList<>();
        File logFolder = getLogFilePath();
        if (logFolder.exists()) {
            for (File file : logFolder.listFiles()) {
                files.add(file);
            }
        }
//        files.add(getAuxFilePath());
//        files.add(getSclFilePath());
        boolean invalid = false;
        for (File file : files) {
            if (invalid || LAST_UPLOADED_FILE_PATH == null || LAST_UPLOADED_FILE_PATH.isEmpty()) {
                LAST_UPLOADED_FILE_PATH = file.getAbsolutePath();
                invalid = false;
                break;
            } else if (file.getAbsolutePath().equalsIgnoreCase(LAST_UPLOADED_FILE_PATH)) {
                LAST_UPLOADED_FILE_PATH = "";
                invalid = true;
            }
        }

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(
                            new FileReader(LAST_UPLOADED_FILE_PATH));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        writer.write(line + "\n");
                    }
                } catch (IOException e) {
                    System.out.println("File iin use...!" + LAST_UPLOADED_FILE_PATH);
                    writer.write("File In use...!");
                } finally {
                        reader.close();

                }
                writer.flush();
            }
        };
        return !invalid ? stream : null;
    }

    //    return null;
//}
//
    private static File getSclFilePath() {
        File logFilePath = new File(".\\scl\\");
        if (!logFilePath.exists()) {
            loadProperties();
            File logFile = new File(ConfigUtil.LOG_FILE);
            if (!logFile.exists()) {
                logFile = new File(ConfigUtil.LOG_FILE + ".0");
            }
            if (logFile.exists()) {
                logFilePath = new File(logFile.getAbsolutePath().replace(logFile.getName(), ""));
            }
        }
        return logFilePath;
    }

    private static File getLogFilePath() {
        File logFilePath = new File(".\\folder\\");
        if (!logFilePath.exists()) {
            loadProperties();
            File logFile = new File(ConfigUtil.LOG_FILE);
            if (!logFile.exists()) {
                logFile = new File(ConfigUtil.LOG_FILE + ".0");
            }
            if (logFile.exists()) {
                logFilePath = new File(logFile.getAbsolutePath().replace(logFile.getName(), ""));
            }
        }
        return logFilePath;
    }

    private static File getAuxFilePath() {
        File logFilePath = new File(".\\auxFile\\");
        if (!logFilePath.exists()) {
            loadProperties();
            File logFile = new File(ConfigUtil.LIMIT_VIOLATION_CSV_PATH);
            if (logFile.exists()) {
                logFilePath = new File(logFile.getAbsolutePath().replace(logFile.getName(), ""));
            }
        }
        return logFilePath;
    }

    private static void loadProperties() {
        if (!propertiesLoaded) {
            ConfigReader.getAllProperties(new File(ConfigUtil.MAIN_CONFIG_REF_FILE));
            ConfigReader.getAllProperties(new File(ConfigUtil.CONFIG_FILE_PATH));
        }
    }

    public static FileOutputStream getNewFOS(String clientId, String fileType) {
        StringBuilder sb = new StringBuilder();
        File archiveDir = new File(LOG_ARCHIVE_PATH);
        if (!archiveDir.exists()) {
            try {
                archiveDir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LOG_ARCHIVE_PATH = archiveDir.getAbsolutePath();
        boolean isCaseFile = fileType.trim().equalsIgnoreCase(FileType.CASE_FILE.name());
        LAST_DOWNLOADED_FILE_TYPE = fileType.trim();

        sb.append(LOG_ARCHIVE_PATH).append(File.separator).append(clientId).append("_").append(System.nanoTime()).append(isCaseFile ? ".PWB" : ".txt");
        LAST_DOWNLOADED_FILE = sb.toString();
        try {
            return new FileOutputStream(LAST_DOWNLOADED_FILE, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(OutputStream fos) {
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(InputStream fis) {
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static StreamingOutput getLogZipFile(String to) throws IOException {
        String from = ConfigUtil.CONFIG_FILE_PATH;
        File f = new File(from);
        boolean directory = f.isDirectory(); // Is it a file or directory?
        if (directory)
            to = from + ".zip"; // use a .zip suffix
        else
            to = from + ".gz"; // or a .gz suffix
        File toFile = new File(to);
        if (toFile.exists()) { // Make sure not to overwrite
            toFile.delete();
        }

        // Finally, call one of the methods defined above to do the work.
        if (directory) {
            zipDirectory(from, to);
        } else {
            gzipFile(from, to);
        }
        return null;
    }

    /**
     * Zip the contents of the directory, and save it in the zipfile
     */
    private static void zipDirectory(String dir, String zipfile) throws IOException,
            IllegalArgumentException {
        // Check that the directory is a directory, and get its contents
        File d = new File(dir);
        if (!d.isDirectory())
            throw new IllegalArgumentException("Compress: not a directory:  " + dir);
        String[] entries = d.list();
        byte[] buffer = new byte[4096]; // Create a buffer for copying
        int bytes_read;

        // Create a stream to compress data and write it to the zipfile
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));

        // Loop through all entries in the directory
        for (int i = 0; i < entries.length; i++) {
            File f = new File(d, entries[i]);
            if (f.isDirectory())
                continue; // Don't zip sub-directories
            FileInputStream in = new FileInputStream(f); // Stream to read file
            ZipEntry entry = new ZipEntry(f.getPath()); // Make a ZipEntry
            out.putNextEntry(entry); // Store entry
            while ((bytes_read = in.read(buffer)) != -1)
                // Copy bytes
                out.write(buffer, 0, bytes_read);
            in.close(); // Close input stream
        }
        // When we're done with the whole loop, close the output stream
        out.close();
    }

    /**
     * Gzip the contents of the from file and save in the to file.
     */
    public static void gzipFile(String from, String to) throws IOException {
        // Create stream to read from the from file
        FileInputStream in = new FileInputStream(from);
        // Create stream to compress data and write it to the to file.
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(to));
        // Copy bytes from one stream to the other
        byte[] buffer = new byte[4096];
        int bytes_read;
        while ((bytes_read = in.read(buffer)) != -1)
            out.write(buffer, 0, bytes_read);
        // And close the streams
        in.close();
        out.close();
    }

}
