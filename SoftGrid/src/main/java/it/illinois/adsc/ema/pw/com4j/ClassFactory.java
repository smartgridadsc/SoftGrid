package it.illinois.adsc.ema.pw.com4j;

import com4j.*;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
    private ClassFactory() {
    } // instanciation is not allowed

    public static it.illinois.adsc.ema.pw.com4j.ISimulatorAuto createSimulatorAuto() {
        return COM4J.createInstance(it.illinois.adsc.ema.pw.com4j.ISimulatorAuto.class, ConfigUtil.POWER_WORLD_CLSID);
    }

    public static void close() {
        COM4J.cleanUp();
    }

//  public static it.illinois.adsc.ema.pw.com4j.IPowerWorldInternal createPowerWorldInternal() {
//    return COM4J.createInstance( it.illinois.adsc.ema.pw.com4j.IPowerWorldInternal.class, "{07FB16B5-5B5C-4703-B5D5-33D874C02493}" );
//  }
}
