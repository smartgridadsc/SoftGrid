package it.illinois.adsc.ema.control.center.security;

import org.openmuc.j60870.ASdu;

/**
 * Created by prageethmahendra on 2/6/2016.
 */
public interface CommandListener {
    public void commandSent(ASdu aSdu);
}
