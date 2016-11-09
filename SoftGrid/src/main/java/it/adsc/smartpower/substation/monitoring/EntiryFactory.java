package it.adsc.smartpower.substation.monitoring;

import it.adsc.smartpower.substation.monitoring.ui.CCControler;
import it.adsc.smartpower.substation.monitoring.ui.IEDControler;
import it.adsc.smartpower.substation.monitoring.ui.SPMainFrame;
import it.illinois.adsc.ema.ui.ControlCenter;

/**
 * Created by prageethmahendra on 2/9/2016.
 */
public class EntiryFactory {
    public static IEDControler getIEDControler()
    {
        return SPMainFrame.getInstance();
    }
    public static CCControler getCCControler()
    {
        return ControlCenter.getInstance();
    }
}
