package it.adsc.smartpower.substatin.concenter.service;

import it.illinois.adsc.ema.common.webservice.ExperimentRequest;
import it.illinois.adsc.ema.common.webservice.ExperimentResponse;
import it.illinois.adsc.ema.common.webservice.FileType;
import it.illinois.adsc.ema.common.webservice.TransferResults;

import javax.ws.rs.core.Response;

/**
 * Created by prageethmahendra on 2/9/2016.
 */
public interface ISoftGridService {
    TransferResults transferFile(String filePath, FileType fileType);

    Response requestFile(String lastFileName, FileType fileType);

    ExperimentResponse changeExperimentState(ExperimentRequest experimentRequest);
}
