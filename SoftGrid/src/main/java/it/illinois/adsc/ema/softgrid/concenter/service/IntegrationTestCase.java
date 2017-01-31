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
package it.illinois.adsc.ema.softgrid.concenter.service;

import it.illinois.adsc.ema.common.webservice.ExperimentRequest;
import it.illinois.adsc.ema.common.webservice.ExperimentResponse;
import it.illinois.adsc.ema.common.webservice.TransferResults;
import it.illinois.adsc.ema.common.webservice.ServiceConfig;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.io.*;

/**
 * Created by prageeth.g on 29/1/2016.
 */
public class IntegrationTestCase {

    private static String TEXT_FILE_PATH = "..\\SoftGridService\\test.txt";
    private static WebTarget target;

    public static void main(String[] args) {
        int chuckedSize = 10240;
        // process the programe parameters
        if (args != null) {
            // identify the sentence file
            if (args.length >= 1 && args[0] != null && args[0].trim().length() > 0) {
                TEXT_FILE_PATH = args[0];
            } else if (args.length >= 2 && args[1] != null && args[1].trim().length() > 0) {
                // identify the file stream chunck size
                try {
                    chuckedSize = Integer.parseInt(args[1].trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                .nonPreemptive()
                .credentials("user", "password")
                .build();

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(feature);
        JerseyClient client = (JerseyClient) ClientBuilder.newClient(clientConfig);
        // set the stream connection chucked size to 10KB
        client.getConfiguration().property(ClientProperties.CHUNKED_ENCODING_SIZE, chuckedSize);
        target = client.target(ServiceConfig.BASE_URI);
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(new File(TEXT_FILE_PATH));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        TransferResults response = null;
        response = target.path("count/").request(MediaType.APPLICATION_JSON).
                header("auth", DatatypeConverter.printBase64Binary("TestClient:admin123".getBytes())).
                header("encriptedWords", "false").
                post(Entity.entity(fileInputStream, MediaType.APPLICATION_OCTET_STREAM), TransferResults.class);

        System.out.println(response);

        client.property(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
        OutputStream fileOutputStream = null;
        fileInputStream = null;
        try {
            fileOutputStream = new FileOutputStream("ttest.txt");
            Response clientResponse = target.path("request/").request(MediaType.APPLICATION_JSON).
                    header("auth", DatatypeConverter.printBase64Binary("TestClient:admin123".getBytes())).get();
            if(clientResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                InputStream res = (InputStream) clientResponse.getEntity();
                writeFile(res, fileOutputStream);
                System.out.println("response1 = " + res);

                ExperimentResponse experimentResponse = target.path("experiment/").request(MediaType.APPLICATION_JSON).
                        header("auth", DatatypeConverter.printBase64Binary("TestClient:admin123".getBytes())).
                        post(Entity.entity(new ExperimentRequest(), MediaType.APPLICATION_JSON), ExperimentResponse.class);
                System.out.println("experimentResponse = " + experimentResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(InputStream fileInputStream, OutputStream outputStream) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            fileInputStream.close();
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
        }
    }
}
