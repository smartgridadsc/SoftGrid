package it.illinois.adsc.ema.pw.com4j;

import com4j.*;

@IID("{9A11A403-9569-4E97-AA33-017B7F2747E8}")
public interface IPowerWorldInternal extends Com4jObject {
  // Methods:
  /**
   * @param param1 Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */
  @DISPID(201) //= 0xc9. The runtime will prefer the VTID if present
  @VTID(7)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object internal1(
    @MarshalAs(NativeType.VARIANT) java.lang.Object param1);


  /**
   * @return  Returns a value of type java.lang.Object
   */
  @DISPID(202) //= 0xca. The runtime will prefer the VTID if present
  @VTID(8)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object internal2();


  /**
   * @return  Returns a value of type java.lang.Object
   */
  @DISPID(203) //= 0xcb. The runtime will prefer the VTID if present
  @VTID(9)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object internal3();


  /**
   * @return  Returns a value of type java.lang.Object
   */
  @DISPID(204) //= 0xcc. The runtime will prefer the VTID if present
  @VTID(10)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object internal4();
  // Properties:
}
