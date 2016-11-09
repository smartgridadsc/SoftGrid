package it.illinois.adsc.ema.pw.com4j;

import com4j.*;

@IID("{A81701A1-A0F2-42AF-9AE0-6EEDE12D8E33}")
public interface ISimulatorAuto_141 extends it.illinois.adsc.ema.pw.com4j.ISimulatorAuto_140 {
  // Methods:
  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(503) //= 0x1f7. The runtime will prefer the VTID if present
  @VTID(38)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object getSystemMetrics();


  /**
   * @param objectType Mandatory java.lang.String parameter.
   * @param paramList Mandatory java.lang.Object parameter.
   * @param filterName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(501) //= 0x1f5. The runtime will prefer the VTID if present
  @VTID(39)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object getParametersMultipleElementRect(
    java.lang.String objectType,
    @MarshalAs(NativeType.VARIANT) java.lang.Object paramList,
    java.lang.String filterName);


  /**
   * @param objType Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(502) //= 0x1f6. The runtime will prefer the VTID if present
  @VTID(40)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object getFieldListStr(
    java.lang.String objType);


  /**
   * @param objectType Mandatory java.lang.String parameter.
   * @param paramList Mandatory java.lang.Object parameter.
   * @param valueList Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(504) //= 0x1f8. The runtime will prefer the VTID if present
  @VTID(41)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object changeParametersMultipleElementRect(
    java.lang.String objectType,
    @MarshalAs(NativeType.VARIANT) java.lang.Object paramList,
    @MarshalAs(NativeType.VARIANT) java.lang.Object valueList);


  /**
   * <p>
   * Getter method for the COM property "CreateIfNotFound"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(505) //= 0x1f9. The runtime will prefer the VTID if present
  @VTID(42)
  boolean createIfNotFound();


  /**
   * <p>
   * Setter method for the COM property "CreateIfNotFound"
   * </p>
   * @param value Mandatory boolean parameter.
   */

  @DISPID(505) //= 0x1f9. The runtime will prefer the VTID if present
  @VTID(43)
  void createIfNotFound(
    boolean value);


  // Properties:
}
