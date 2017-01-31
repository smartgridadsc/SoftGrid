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

@IID("{DA8C289F-1C6D-41A4-B6D9-0BC9CEED4441}")
public interface ISimulatorAuto_170 extends it.illinois.adsc.ema.pw.com4j.ISimulatorAuto_160 {
  // Methods:
  /**
   * @param objectType Mandatory java.lang.String parameter.
   * @param fieldList Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(701) //= 0x2bd. The runtime will prefer the VTID if present
  @VTID(45)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object getSpecificFieldList(
    java.lang.String objectType,
    @MarshalAs(NativeType.VARIANT) java.lang.Object fieldList);


  /**
   * @param objectType Mandatory java.lang.String parameter.
   * @param variableName Mandatory java.lang.String parameter.
   * @return  Returns a value of type int
   */

  @DISPID(702) //= 0x2be. The runtime will prefer the VTID if present
  @VTID(46)
  int getSpecificFieldMaxNum(
    java.lang.String objectType,
    java.lang.String variableName);


  // Properties:
}
