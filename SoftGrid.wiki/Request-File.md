## API Name : Request File
### Summery : https://github.com/smartgridadsc/smartpower/blob/master/API/RequestFile
### URL : /requestfile
### Method : POST

### Description
This is the REST API endpoint to request or download log files from the SoftGrid testbed. For authentication purpose the username and password should be provided in the http header.

Example request using Jearsy  

    `clientResponse = target.path("requestfile/").request(MediaType.APPLICATION_JSON).`
        `header("auth", DatatypeConverter.printBase64Binary("TestClient:admin123".getBytes())).`
        `header("fileType", fileType.name()).`
        `post(Entity.entity(fileRequestCriteria, MediaType.APPLICATION_JSON), Response.class);`
### Use Case