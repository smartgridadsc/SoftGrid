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
package it.illinois.adsc.ema.pw;

import it.illinois.adsc.ema.pw.com4j.SoftGridCom_com4J;
import it.illinois.adsc.ema.pw.ied.pwcom.SoftGridCom;
import it.illinois.adsc.ema.pw.ied.pwcom.SoftGridComAPI;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

/**
 * Created by prageethmahendra on 9/6/2016.
 */
public class SoftGridComFactory {

    public static SoftGridComAPI getSingletonPWComInstance() {
        return getSingletonPWComInstance(ConfigUtil.PHYSICAL_SIMMULATOR_API);
    }

    private static SoftGridComAPI getSingletonPWComInstance(SoftGridComType pwcomType) {
        switch (pwcomType) {
            case COM4J_PW:
                return SoftGridCom_com4J.getInstance();
            case JACOB:
                return SoftGridCom.getInstance();
            case DUMMY:
                return DummyAPI.getInstance();
            default:
                return null;
        }
    }
}
