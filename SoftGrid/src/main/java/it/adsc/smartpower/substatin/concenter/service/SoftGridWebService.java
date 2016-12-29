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
package it.adsc.smartpower.substatin.concenter.service;

import it.adsc.smartpower.substatin.concenter.ControlCenterWindow;
import it.illinois.adsc.ema.common.webservice.*;
import it.illinois.adsc.ema.operation.SoftGridMain;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.swing.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.io.*;

/**
 * Created by prageethmahendra on 2/9/2016.
 */
public class SoftGridWebService implements ISoftGridService {

    private static SoftGridWebService instance = null;
    private int chuckedSize = 10240;
    private static WebTarget target;
    private JerseyClient client;

    private SoftGridWebService() {
        init();
    }

    protected static SoftGridWebService getInstance() {
        if (instance == null) {
            instance = new SoftGridWebService();
        }
        return instance;
    }

    private void init() {
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                .nonPreemptive()
                .credentials("user", "password")
                .build();

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(feature);
        client = (JerseyClient) ClientBuilder.newClient(clientConfig);
        // set the stream connection chucked size to 10KB
        client.getConfiguration().property(ClientProperties.CHUNKED_ENCODING_SIZE, chuckedSize);
        target = client.target(ServiceConfig.BASE_URI);
        System.out.println("Web sevice connected...!");
        client.property(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
    }

    public TransferResults transferFile(String filePath, FileType fileType) {
        FileInputStream fileInputStream = null;
        TransferResults response = null;
        try {
            File file = new File(filePath);
            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                return null;
            }
            response = target.path("count/").request(MediaType.APPLICATION_JSON).
                    header("auth", DatatypeConverter.printBase64Binary("TestClient:admin123".getBytes())).
                    header("fileType", fileType.name()).
                    post(Entity.entity(fileInputStream, MediaType.APPLICATION_OCTET_STREAM), TransferResults.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    public Response requestFile(String lastFileName, FileType fileType) {

        Response clientResponse = null;
        try {
            FileRequestCriteria fileRequestCriteria = new FileRequestCriteria();
            fileRequestCriteria.setLastFileName(lastFileName);
            clientResponse = target.path("request/").request(MediaType.APPLICATION_JSON).
                    header("auth", DatatypeConverter.printBase64Binary("TestClient:admin123".getBytes())).
                    header("fileType", fileType.name()).
            post(Entity.entity(fileRequestCriteria, MediaType.APPLICATION_JSON), Response.class);

            if (clientResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                System.out.println("Downloading " + clientResponse.getHeaders().get("filename") + " ...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return clientResponse;
    }

    public synchronized ExperimentResponse changeExperimentState(ExperimentRequest experimentRequest) {
//        transferFile("C:\\EMA\\Demo\\smartpower\\SoftGridService\\test.txt");
//        requestFile("C:\\EMA\\Demo\\smartpower\\SoftGridService\\test.txt");
        ExperimentResponse experimentResponse = null;
        try {
            experimentResponse = target.path("experiment/").request(MediaType.APPLICATION_JSON).
                    header("auth", DatatypeConverter.printBase64Binary("TestClient:admin123".getBytes())).
                    post(Entity.entity(experimentRequest, MediaType.APPLICATION_JSON), ExperimentResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return experimentResponse;
    }
}
