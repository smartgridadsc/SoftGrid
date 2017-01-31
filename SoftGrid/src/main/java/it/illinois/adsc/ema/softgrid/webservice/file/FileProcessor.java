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
//package it.ilinois.adsc.ema.webservice.file;
//
//
//import it.illinois.adsc.ema.common.webservice.TransferResults;
////import it.illinois.adsc.ema.webservice.web.resources.security.SensitiveWordHandler;
//
//import java.io.*;
//import java.util.*;
//
///**
// * This class is the main operation controller class
// * Created by prageeth_2 on 1/31/2016.
// */
//public class FileProcessor {
////    private static final String SPLIT_STRING = "[{}().,;:\"!\t\r\n-=+/\\*$#%^|]";
//
//    /**
//     * This method gets the clientId and input stream obtained from the client request.
//     *
//     * @param clientId    ClientID or the username of the client
//     * @param inputStream inputstream of the long sentence
//     * @return Process results which include maximum, minimum and median words
//     */
//    public TransferResults downloadFile(String clientId, InputStream inputStream, String fileType) {
//        FileOutputStream fos = FileHandler.getNewFOS(clientId, fileType);
//        TransferResults counterResults = new TransferResults();
//        if (fos == null) {
//            counterResults.setSuccess(false);
//            return counterResults;
//        }
//        String str;
//        byte[] data = new byte[8];
//        try {
//            byte[] buffer = new byte[1024];
//            int noOfBytes = 0;
//            System.out.println("Copying file using streams");
//            // read bytes from source file and write to destination file
//            while ((noOfBytes = inputStream.read(buffer)) != -1) {
//                fos.write(buffer, 0, noOfBytes);
//            }
//            counterResults.setSuccess(true);
//        } catch (IOException e) {
//            e.printStackTrace();
//            counterResults.setSuccess(false);
//        } finally {
//            FileHandler.close(fos);
//            FileHandler.close(inputStream);
//        }
//        return counterResults;
//    }
//}
