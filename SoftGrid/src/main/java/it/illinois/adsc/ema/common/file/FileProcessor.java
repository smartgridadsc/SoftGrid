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
package it.illinois.adsc.ema.common.file;

import it.illinois.adsc.ema.common.webservice.TransferResults;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import it.illinois.adsc.ema.webservice.file.FileHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by prageethmahendra on 2/10/2016.
 * <p>
 * /**
 * This class is the main operation controller class
 * Created by prageeth_2 on 1/31/2016.
 */
public class FileProcessor {

    public static TransferResults downloadFile(InputStream inputStream, OutputStream outputStream) {
        return downloadFile(inputStream, outputStream, false);
    }

    /**
     * this methbod move input stream to the output stream and close the streams
     *
     * @param inputStream
     * @param outputStream
     * @return Process results which include maximum, minimum and median words
     */
    public static TransferResults downloadFile(InputStream inputStream, OutputStream outputStream, boolean isCaseFile) {
        TransferResults counterResults = new TransferResults();
        if (outputStream == null || inputStream == null) {
            counterResults.setSuccess(false);
            return counterResults;
        }
        try {
            byte[] buffer = new byte[1024];
            int noOfBytes = 0;
            System.out.println("Copying file using streams");
            // read bytes from source file and write to destination file
            while ((noOfBytes = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, noOfBytes);
            }
            counterResults.setSuccess(true);
        } catch (IOException e) {
            e.printStackTrace();
            counterResults.setSuccess(false);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (isCaseFile) {
            // copy the case file to the predefine location
            ConfigUtil.CASE_FILE_NAME = new File(ConfigUtil.CASE_FILE_NAME).getName();
            ConfigUtil.CASE_FILE_PATH = ConfigUtil.CASE_FILE_PATH.endsWith(File.separator) ?
                    ConfigUtil.CASE_FILE_PATH : ConfigUtil.CASE_FILE_PATH + File.separator;
            String caseFile = ConfigUtil.CASE_FILE_PATH + File.separator + ConfigUtil.CASE_FILE_NAME;
            try {
                Files.copy(new File(FileHandler.LAST_DOWNLOADED_FILE).toPath(), new File(caseFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("Unable to copy the case file from \n" + FileHandler.LAST_DOWNLOADED_FILE_TYPE +
                        "\nTo\n" + caseFile);
            }
        }
        return counterResults;
    }

    public static void main(String[] args) {
        try {
            FileInputStream fileInputStream = new FileInputStream("C:\\EMA\\Demo\\smartpower\\SmartPower\\log\\CCLog.log.0");
            FileOutputStream fileOutputStream = new FileOutputStream("C:\\EMA\\Demo\\smartpower\\SmartPower\\log\\temp");
            downloadFile(fileInputStream, fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
