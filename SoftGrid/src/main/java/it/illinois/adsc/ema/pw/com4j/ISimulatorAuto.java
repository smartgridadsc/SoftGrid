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
import org.jvnet.hk2.annotations.Optional;

@IID("{603625FB-ED49-4E9F-A69E-42B55E7CC781}")
public interface ISimulatorAuto extends it.illinois.adsc.ema.pw.com4j.ISimulatorAuto_170 {
  // Methods:
  /**
   * @param fileName Mandatory java.lang.String parameter.
   * @param fileType Mandatory java.lang.String parameter.
   * @param options Optional parameter. Default value is com4j.Variant.getMissing()
   * @return  Returns a value of type java.lang.Object
   */
  @DISPID(801) //= 0x321. The runtime will prefer the VTID if present
  @VTID(47)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object openCaseType(
    java.lang.String fileName,
    java.lang.String fileType,
    @Optional @MarshalAs(NativeType.VARIANT) java.lang.Object options);


  // Properties:
}
