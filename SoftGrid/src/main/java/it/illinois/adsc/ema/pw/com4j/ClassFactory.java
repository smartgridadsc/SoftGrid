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
