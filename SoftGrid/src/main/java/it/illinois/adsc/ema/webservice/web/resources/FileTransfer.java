package it.illinois.adsc.ema.webservice.web.resources;

import it.illinois.adsc.ema.common.file.FileProcessor;
import it.illinois.adsc.ema.common.webservice.FileType;
import it.illinois.adsc.ema.common.webservice.TransferResults;
import it.illinois.adsc.ema.webservice.file.FileHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;

/**
 * Created by prageeth.g on 29/1/2016.
 */
@Path("count")
public class FileTransfer {

    @POST
    @Consumes({MediaType.APPLICATION_OCTET_STREAM})
    @Produces({MediaType.APPLICATION_JSON})
    public TransferResults transferFile(@HeaderParam("username") String userid,
                                        @HeaderParam("fileType") String fileType,
                                        InputStream attachmentInputStream) throws Exception {
        return FileProcessor.downloadFile( attachmentInputStream, FileHandler.getNewFOS(userid, fileType), fileType.equals(FileType.CASE_FILE.name()));
    }


}
