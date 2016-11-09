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
