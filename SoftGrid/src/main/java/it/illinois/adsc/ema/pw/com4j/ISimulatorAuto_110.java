package it.illinois.adsc.ema.pw.com4j;

import com4j.*;

@IID("{C9F82170-B29F-4B81-B437-55C3ECF62955}")
public interface ISimulatorAuto_110 extends Com4jObject {
  // Methods:
  /**
   * @param fileName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1610743808) //= 0x60020000. The runtime will prefer the VTID if present
  @VTID(7)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object openCase(
    java.lang.String fileName);


  /**
   * @param tObjType Mandatory java.lang.String parameter.
   * @param filterName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1610743809) //= 0x60020001. The runtime will prefer the VTID if present
  @VTID(8)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object listOfDevices(
    java.lang.String tObjType,
    java.lang.String filterName);


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(9)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object closeCase();


  /**
   * <p>
   * Getter method for the COM property "ProgVisible"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(201) //= 0xc9. The runtime will prefer the VTID if present
  @VTID(10)
  boolean progVisible();


  /**
   * <p>
   * Setter method for the COM property "ProgVisible"
   * </p>
   * @param value Mandatory boolean parameter.
   */

  @DISPID(201) //= 0xc9. The runtime will prefer the VTID if present
  @VTID(11)
  void progVisible(
    boolean value);


  /**
   * @param tObjType Mandatory java.lang.String parameter.
   * @param filterName Mandatory java.lang.String parameter.
   * @param tfieldList Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(12)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object sendToExcel(
    java.lang.String tObjType,
    java.lang.String filterName,
    @MarshalAs(NativeType.VARIANT) java.lang.Object tfieldList);


  /**
   * @param tObjectType Mandatory java.lang.String parameter.
   * @param paramList Mandatory java.lang.Object parameter.
   * @param values Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(13)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object changeParameters(
    java.lang.String tObjectType,
    @MarshalAs(NativeType.VARIANT) java.lang.Object paramList,
    @MarshalAs(NativeType.VARIANT) java.lang.Object values);


  /**
   * @param fileName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(14)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object processAuxFile(
    java.lang.String fileName);


  /**
   * @param tObjectType Mandatory java.lang.String parameter.
   * @param paramList Mandatory java.lang.Object parameter.
   * @param valueList Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(13) //= 0xd. The runtime will prefer the VTID if present
  @VTID(15)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object getParameters(
    java.lang.String tObjectType,
    @MarshalAs(NativeType.VARIANT) java.lang.Object paramList,
    @MarshalAs(NativeType.VARIANT) java.lang.Object valueList);


  /**
   * @param fileName Mandatory java.lang.String parameter.
   * @param filterName Mandatory java.lang.String parameter.
   * @param tObjType Mandatory java.lang.String parameter.
   * @param tAppend Mandatory boolean parameter.
   * @param tfieldList Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(16)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object writeAuxFile(
    java.lang.String fileName,
    java.lang.String filterName,
    java.lang.String tObjType,
    boolean tAppend,
    @MarshalAs(NativeType.VARIANT) java.lang.Object tfieldList);


  /**
   * @param fileName Mandatory java.lang.String parameter.
   * @param fileType Mandatory java.lang.Object parameter.
   * @param overwrite Mandatory boolean parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(17) //= 0x11. The runtime will prefer the VTID if present
  @VTID(17)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object saveCase(
    java.lang.String fileName,
    @MarshalAs(NativeType.VARIANT) java.lang.Object fileType,
    boolean overwrite);


  /**
   * <p>
   * Getter method for the COM property "ExcelApp"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(202) //= 0xca. The runtime will prefer the VTID if present
  @VTID(18)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object excelApp();


  /**
   * @param cmndString Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(21) //= 0x15. The runtime will prefer the VTID if present
  @VTID(19)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object runScriptCommand(
    java.lang.String cmndString);


  /**
   * @param tObjType Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(203) //= 0xcb. The runtime will prefer the VTID if present
  @VTID(20)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object getFieldList(
    java.lang.String tObjType);


  /**
   * <p>
   * Getter method for the COM property "ProcessID"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(204) //= 0xcc. The runtime will prefer the VTID if present
  @VTID(21)
  int processID();


  /**
   * <p>
   * Getter method for the COM property "CurrentDir"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(205) //= 0xcd. The runtime will prefer the VTID if present
  @VTID(22)
  java.lang.String currentDir();


  /**
   * <p>
   * Setter method for the COM property "CurrentDir"
   * </p>
   * @param value Mandatory java.lang.String parameter.
   */

  @DISPID(205) //= 0xcd. The runtime will prefer the VTID if present
  @VTID(23)
  void currentDir(
    java.lang.String value);


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(206) //= 0xce. The runtime will prefer the VTID if present
  @VTID(24)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object saveState();


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(207) //= 0xcf. The runtime will prefer the VTID if present
  @VTID(25)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object loadState();


  /**
   * <p>
   * Getter method for the COM property "ThreadPriority"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(209) //= 0xd1. The runtime will prefer the VTID if present
  @VTID(26)
  java.lang.String threadPriority();


  /**
   * <p>
   * Setter method for the COM property "ThreadPriority"
   * </p>
   * @param value Mandatory java.lang.String parameter.
   */

  @DISPID(209) //= 0xd1. The runtime will prefer the VTID if present
  @VTID(27)
  void threadPriority(
    java.lang.String value);


  /**
   * @param tObjectType Mandatory java.lang.String parameter.
   * @param paramList Mandatory java.lang.Object parameter.
   * @param valueList Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(208) //= 0xd0. The runtime will prefer the VTID if present
  @VTID(28)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object getParametersSingleElement(
    java.lang.String tObjectType,
    @MarshalAs(NativeType.VARIANT) java.lang.Object paramList,
    @MarshalAs(NativeType.VARIANT) java.lang.Object valueList);


  /**
   * @param tObjectType Mandatory java.lang.String parameter.
   * @param paramList Mandatory java.lang.Object parameter.
   * @param filterName Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(210) //= 0xd2. The runtime will prefer the VTID if present
  @VTID(29)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object getParametersMultipleElement(
    java.lang.String tObjectType,
    @MarshalAs(NativeType.VARIANT) java.lang.Object paramList,
    @MarshalAs(NativeType.VARIANT) java.lang.Object filterName);


  /**
   * @param tObjType Mandatory java.lang.String parameter.
   * @param filterName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(211) //= 0xd3. The runtime will prefer the VTID if present
  @VTID(30)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object listOfDevicesAsVariantStrings(
    java.lang.String tObjType,
    java.lang.String filterName);


  /**
   * @param tObjType Mandatory java.lang.String parameter.
   * @param filterName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(212) //= 0xd4. The runtime will prefer the VTID if present
  @VTID(31)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object listOfDevicesFlatOutput(
    java.lang.String tObjType,
    java.lang.String filterName);


  /**
   * @param tObjectType Mandatory java.lang.String parameter.
   * @param paramList Mandatory java.lang.Object parameter.
   * @param filterName Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(213) //= 0xd5. The runtime will prefer the VTID if present
  @VTID(32)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object getParametersMultipleElementFlatOutput(
    java.lang.String tObjectType,
    @MarshalAs(NativeType.VARIANT) java.lang.Object paramList,
    @MarshalAs(NativeType.VARIANT) java.lang.Object filterName);


  /**
   * <p>
   * Getter method for the COM property "RequestBuildDate"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(214) //= 0xd6. The runtime will prefer the VTID if present
  @VTID(33)
  int requestBuildDate();


  // Properties:
}
