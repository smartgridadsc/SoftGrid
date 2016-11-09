package it.illinois.adsc.ema.common.webservice;

/**
 * Created by prageethmahendra on 1/9/2016.
 */
public class ExperimentResponse {
    ExperimentRequest experimentRequest;
    ExperimentStatus experimentStatus;
//    private SecurityContext securityContext;
    private int logFileCount;

    public ExperimentResponse() {
    }

    public ExperimentRequest getExperimentRequest() {
        return experimentRequest;
    }

    public void setExperimentRequest(ExperimentRequest experimentRequest) {
        this.experimentRequest = experimentRequest;
    }

    public ExperimentStatus getExperimentStatus() {
        return experimentStatus;
    }

    public void setExperimentStatus(ExperimentStatus experimentStatus) {
        this.experimentStatus = experimentStatus;
    }



//    public SecurityContext getSecurityContext() {
//        return securityContext;
//    }
//
//    public void setSecurityContext(SecurityContext securityContext) {
//        this.securityContext = securityContext;
//    }

    public int getLogFileCount() {
        return logFileCount;
    }

    public void setLogFileCount(int logFileCount) {
        this.logFileCount = logFileCount;
    }

//    @Override
//    public String toString() {
//        return (experimentRequest != null ? experimentRequest.toString() : "") + " " + logFileCount;
//    }
}
