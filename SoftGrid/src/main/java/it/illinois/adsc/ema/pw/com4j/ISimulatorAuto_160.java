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

@IID("{18047F2A-A3EF-45C6-A0F3-BB869204A445}")
public interface ISimulatorAuto_160 extends it.illinois.adsc.ema.pw.com4j.ISimulatorAuto_141 {
  // Methods:
  /**
   * @param ctgName Mandatory java.lang.String parameter.
   * @param plotAndObjFieldList Mandatory java.lang.Object parameter.
   * @param startTime Mandatory java.lang.String parameter.
   * @param endTime Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(601) //= 0x259. The runtime will prefer the VTID if present
  @VTID(44)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object tsGetContingencyResults(
    java.lang.String ctgName,
    @MarshalAs(NativeType.VARIANT) java.lang.Object plotAndObjFieldList,
    java.lang.String startTime,
    java.lang.String endTime);


  // Properties:
}
