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
