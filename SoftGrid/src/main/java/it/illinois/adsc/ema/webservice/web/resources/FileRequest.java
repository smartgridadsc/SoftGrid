package it.illinois.adsc.ema.webservice.web.resources;

import it.illinois.adsc.ema.common.webservice.FileRequestCriteria;
import it.illinois.adsc.ema.webservice.file.FileHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by prageethmahendra on 31/8/2016.
 */
@Path("request")
public class FileRequest {

    @POST
    public Response requestFile(@HeaderParam("username") String userid,
                                FileRequestCriteria fileRequestCriteria) throws Exception {
        StreamingOutput stream = FileHandler.getLogFileUploadStream(fileRequestCriteria.getLastFileName());
        System.out.println(" File requisted...! " + stream);
        return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
                .header("filename", FileHandler.LAST_UPLOADED_FILE_NAME).build();
    }
}
