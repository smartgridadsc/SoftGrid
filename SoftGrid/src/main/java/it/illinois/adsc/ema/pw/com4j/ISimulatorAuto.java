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
