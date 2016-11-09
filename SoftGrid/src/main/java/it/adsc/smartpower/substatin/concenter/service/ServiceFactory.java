package it.adsc.smartpower.substatin.concenter.service;

/**
 * Created by prageethmahendra on 2/9/2016.
 */
public class ServiceFactory {
    public static ISoftGridService getServiceConnection() {
        return SoftGridWebService.getInstance();
    }
}
