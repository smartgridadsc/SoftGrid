package it.illinois.adsc.ema.common.webservice;

/**
 * Created by prageethmahendra on 31/8/2016.
 */

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
public class TransferResults {
    private boolean success;

    public TransferResults() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
