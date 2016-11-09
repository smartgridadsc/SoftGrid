package it.illinois.adsc.ema.common.webservice;

/**
 * Created by prageethmahendra on 2/9/2016.
 */
public class SecurityContext {
    private String user;
    private String pass;
    private String key;

    public SecurityContext() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
