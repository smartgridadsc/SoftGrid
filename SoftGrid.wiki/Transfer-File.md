## API Name : Transfer File
### Summery : https://github.com/smartgridadsc/smartpower/blob/master/API/TransferFile
### URL : /transferfile
### Method : POST

### Description
This is the REST API endpoint to request or download log files from the SoftGrid testbed. For authentication purpose the username and password should be provided in the http header.

Example request using Jearsy  

    `response = target.path("transferfile/").request(MediaType.APPLICATION_JSON).`
             `header("auth", DatatypeConverter.printBase64Binary("TestClient:admin123".getBytes())).`
             `header("fileType", fileType.name()).`
             `post(Entity.entity(fileInputStream, MediaType.APPLICATION_OCTET_STREAM), TransferResults.class);`
### Use Case