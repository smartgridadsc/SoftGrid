package it.illinois.adsc.ema.control.center.experiments;

import org.openmuc.j60870.ASdu;

/**
 * Created by prageethmahendra on 4/7/2016.
 */
public class AsduCommand {
    private ASdu aSdu;
    private long sentTimeMillis;

    public AsduCommand(ASdu aSdu) {
        this.aSdu = aSdu;
        this.sentTimeMillis = System.nanoTime();
    }

    public ASdu getaSdu() {
        return aSdu;
    }

    public void setaSdu(ASdu aSdu) {
        this.aSdu = aSdu;
    }

    public long getSentTimeMillis() {
        return sentTimeMillis;
    }

    public void setSentTimeMillis(long sentTimeMillis) {
        this.sentTimeMillis = sentTimeMillis;
    }
}
