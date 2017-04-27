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
    // todo replace this with the property definitino
    public static SoftGridIEDServer getIedServer(PWModelDetails pwModelDetails) {
        if (pwModelDetails == null) {
            return null;
        }
        HashMap<String, Fc> stringFcHashMap = new HashMap<>();
        IEDType iedType = IEDUtils.getIEDType(pwModelDetails.getDeviceName());
        String reference = new File(pwModelDetails.getSclFileName()).getName().replace(".", "");
        switch (iedType) {
            case BRANCH:
                reference += "LD1/";
                stringFcHashMap.put(reference + "CSWI1.Pos.stVal", Fc.ST);
                stringFcHashMap.put(reference + "CSWI1.Pos.Oper.ctlVal", Fc.CO);
                break;
            case BUS:
                reference += "LD1/";
                stringFcHashMap.put(reference + "MMXU1.pwMv.frequency.f", Fc.MX);
                stringFcHashMap.put(reference + "MMXU1.pwMv.BusKVVolt.f", Fc.MX);
                break;
            case GENERATOR:
                reference += "LD1/";
                stringFcHashMap.put(reference + "MMXU1.pwMv.genMW.f", Fc.MX);
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
                stringFcHashMap.put(reference + "CSWI1.Pos.stVal", Fc.ST);
                stringFcHashMap.put(reference + "CSWI1.Pos.Oper.ctlVal", Fc.CO);
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
        return new SoftGridIEDServer(stringFcHashMap);
    }
}
