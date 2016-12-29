/* Copyright (C) 2016 Advanced Digital Science Centre

        * This file is part of Soft-Grid.
        * For more information visit https://www.illinois.adsc.com.sg/cybersage/
        *
        * Soft-Grid is free software: you can redistribute it and/or modify
        * it under the terms of the GNU General Public License as published by
        * the Free Software Foundation, either version 3 of the License, or
        * (at your option) any later version.
        *
        * Soft-Grid is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        * GNU General Public License for more details.
        *
        * You should have received a copy of the GNU General Public License
        * along with Soft-Grid.  If not, see <http://www.gnu.org/licenses/>.

        * @author Prageeth Mahendra Gunathilaka
*/
package it.illinois.adsc.ema.control.ied;

import it.illinois.adsc.ema.control.ied.pw.PWModelDetails;
import org.openmuc.openiec61850.Fc;

import java.io.File;
import java.util.HashMap;

/**
 * Created by prageeth.g on 22/12/2016.
 */
public class IEDServerFactory {
    public static SmartPowerIEDServer getIedServer(PWModelDetails pwModelDetails) {
        if (pwModelDetails == null) {
            return null;
        }
        HashMap<String, Fc> stringFcHashMap = new HashMap<>();
        IEDType iedType = IEDUtils.getIEDType(pwModelDetails.getDeviceName());
        String reference = new File(pwModelDetails.getSclFileName()).getName().replace(".", "");
        switch (iedType) {
            case BRANCH:
                reference += "LD1/CSWI1.";
                stringFcHashMap.put(reference + "Pos.stVal", Fc.ST);
                stringFcHashMap.put(reference + "Pos.Oper.ctlVal", Fc.CO);
                break;
            case BUS:
                reference += "LD1/MMXU1.pwMv.";
                stringFcHashMap.put(reference + "frequency.f", Fc.MX);
                stringFcHashMap.put(reference + "BusKVVolt.f", Fc.MX);
                break;
            case GENERATOR:
                reference += "LD1/";
                stringFcHashMap.put(reference + "ZGEN1.GnCtl.genMW", Fc.CO);
                stringFcHashMap.put(reference + "CSWI1.Pos.Oper.ctlVal", Fc.CO);
                stringFcHashMap.put(reference + "CSWI1.Pos.stVal", Fc.ST);
                break;
            case LOAD:
                reference += "LD1/";
                stringFcHashMap.put(reference + "CSWI1.Pos.stVal", Fc.ST);
                stringFcHashMap.put(reference + "MMXU1.pwMv.loadMW.f", Fc.MX);
                stringFcHashMap.put(reference + "CSWI1.Pos.Oper.ctlVal", Fc.CO);
                break;
            case SHUNT:
                reference += "LD1/";
                stringFcHashMap.put(reference + "ARCO1.Oper.setMag.fl", Fc.SP);
                stringFcHashMap.put(reference + "ARCO1.TapChg.Oper.setMag.f", Fc.SP);
                break;
            case TRANSFRMER:
                reference += "LD1/";
                stringFcHashMap.put(reference + "CSWI1.Pos.stVal", Fc.ST);
                stringFcHashMap.put(reference + "TVTR1.Rat.setMag.f", Fc.SP);
                stringFcHashMap.put(reference + "CSWI1.Pos.Oper.ctlVal", Fc.CO);
                break;
            case VIRTUAL:
                reference += "LD1/";
                stringFcHashMap.put(reference + "MMXU1.pwMv.overloadRank", Fc.MX);
                break;
        }
        return new SmartPowerIEDServer(stringFcHashMap);
    }
}
