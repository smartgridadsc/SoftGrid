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
