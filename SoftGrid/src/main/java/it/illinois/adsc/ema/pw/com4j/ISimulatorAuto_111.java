package it.illinois.adsc.ema.pw.com4j;

import com4j.*;

@IID("{D8A9DDE4-5F61-41D1-A78A-9655ED402682}")
public interface ISimulatorAuto_111 extends it.illinois.adsc.ema.pw.com4j.ISimulatorAuto_110 {
  // Methods:
  /**
   * @param tObjectType Mandatory java.lang.String parameter.
   * @param paramList Mandatory java.lang.Object parameter.
   * @param valueList Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(301) //= 0x12d. The runtime will prefer the VTID if present
  @VTID(34)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object changeParametersMultipleElement(
    java.lang.String tObjectType,
    @MarshalAs(NativeType.VARIANT) java.lang.Object paramList,
    @MarshalAs(NativeType.VARIANT) java.lang.Object valueList);


  /**
   * @param tObjectType Mandatory java.lang.String parameter.
   * @param paramList Mandatory java.lang.Object parameter.
   * @param noOfObjects Mandatory int parameter.
   * @param valueList Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(302) //= 0x12e. The runtime will prefer the VTID if present
  @VTID(35)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object changeParametersMultipleElementFlatInput(
    java.lang.String tObjectType,
    @MarshalAs(NativeType.VARIANT) java.lang.Object paramList,
    int noOfObjects,
    @MarshalAs(NativeType.VARIANT) java.lang.Object valueList);


  /**
   * @param tObjectType Mandatory java.lang.String parameter.
   * @param paramList Mandatory java.lang.Object parameter.
   * @param valueList Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(303) //= 0x12f. The runtime will prefer the VTID if present
  @VTID(36)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object changeParametersSingleElement(
    java.lang.String tObjectType,
    @MarshalAs(NativeType.VARIANT) java.lang.Object paramList,
    @MarshalAs(NativeType.VARIANT) java.lang.Object valueList);


  // Properties:
}
