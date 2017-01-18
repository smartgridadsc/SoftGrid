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
package it.illinois.adsc.ema.softgrid.webservice.web.resources;

import it.illinois.adsc.ema.common.webservice.FileRequestCriteria;
import it.illinois.adsc.ema.softgrid.webservice.file.FileHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;

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
                .header("filename",
                        FileHandler.LAST_UPLOADED_FILE_PATH.isEmpty() ? "" :
                                new File(FileHandler.LAST_UPLOADED_FILE_PATH).getName()).build();
    }
}
