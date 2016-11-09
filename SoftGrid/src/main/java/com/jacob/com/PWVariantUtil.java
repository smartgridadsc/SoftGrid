package com.jacob.com;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Date;

/**
* Created by prageethmahendra on 20/1/2016.
*/
public class PWVariantUtil{
    private static final BigDecimal LARGEST_DECIMAL = new BigDecimal(new BigInteger("ffffffffffffffffffffffff", 16));
    private static final BigDecimal SMALLEST_DECIMAL = new BigDecimal((new BigInteger("ffffffffffffffffffffffff", 16)).negate());

    private PWVariantUtil() {
    }

    public static void populateVariant(Variant targetVariant, Object pValueObject, boolean fByRef) {
        if(pValueObject == null) {
            targetVariant.putEmpty();
        } else if(pValueObject instanceof Integer) {
            if(fByRef) {
                targetVariant.putIntRef(((Integer)pValueObject).intValue());
            } else {
                targetVariant.putInt(((Integer)pValueObject).intValue());
            }
        } else if(pValueObject instanceof Short) {
            if(fByRef) {
                targetVariant.putShortRef(((Short)pValueObject).shortValue());
            } else {
                targetVariant.putShort(((Short)pValueObject).shortValue());
            }
        } else if(pValueObject instanceof String) {
            if(fByRef) {
                targetVariant.putStringRef((String)pValueObject);
            } else {
                targetVariant.putString((String)pValueObject);
            }
        } else if(pValueObject instanceof Boolean) {
            if(fByRef) {
                targetVariant.putBooleanRef(((Boolean)pValueObject).booleanValue());
            } else {
                targetVariant.putBoolean(((Boolean)pValueObject).booleanValue());
            }
        } else if(pValueObject instanceof Double) {
            if(fByRef) {
                targetVariant.putDoubleRef(((Double)pValueObject).doubleValue());
            } else {
                targetVariant.putDouble(((Double)pValueObject).doubleValue());
            }
        } else if(pValueObject instanceof Float) {
            if(fByRef) {
                targetVariant.putFloatRef(((Float)pValueObject).floatValue());
            } else {
                targetVariant.putFloat(((Float)pValueObject).floatValue());
            }
        } else if(pValueObject instanceof BigDecimal) {
            if(fByRef) {
                targetVariant.putDecimalRef((BigDecimal)pValueObject);
            } else {
                targetVariant.putDecimal((BigDecimal)pValueObject);
            }
        } else if(pValueObject instanceof Byte) {
            if(fByRef) {
                targetVariant.putByteRef(((Byte)pValueObject).byteValue());
            } else {
                targetVariant.putByte(((Byte)pValueObject).byteValue());
            }
        } else if(pValueObject instanceof Date) {
            if(fByRef) {
                targetVariant.putDateRef((Date)pValueObject);
            } else {
                targetVariant.putDate((Date)pValueObject);
            }
        } else if(pValueObject instanceof Long) {
            if(fByRef) {
                targetVariant.putLongRef(((Long)pValueObject).longValue());
            } else {
                targetVariant.putLong(((Long)pValueObject).longValue());
            }
        } else if(pValueObject instanceof Currency) {
            if(fByRef) {
                targetVariant.putCurrencyRef((Currency)pValueObject);
            } else {
                targetVariant.putCurrency((Currency)pValueObject);
            }
        } else if(pValueObject instanceof SafeArray) {
            if(fByRef) {
                targetVariant.putSafeArrayRef((SafeArray)pValueObject);
            } else {
                targetVariant.putSafeArray((SafeArray)pValueObject);
            }
        } else if(pValueObject instanceof Dispatch) {
            if(fByRef) {
                targetVariant.putDispatchRef((Dispatch)pValueObject);
            } else {
                targetVariant.putDispatch((Dispatch)pValueObject);
            }
        } else {
            if(!(pValueObject instanceof Variant)) {
                throw new NotImplementedException("populateVariant() not implemented for " + pValueObject.getClass());
            }

            targetVariant.putVariant(pValueObject);
        }

    }

    public static Variant objectToVariant(Object objectToBeMadeIntoVariant) {
        if(objectToBeMadeIntoVariant == null) {
            return new Variant();
        } else if(objectToBeMadeIntoVariant instanceof Variant) {
            return (Variant)objectToBeMadeIntoVariant;
        } else if(!objectToBeMadeIntoVariant.getClass().isArray()) {
            Variant var8 = new Variant();
            populateVariant(var8, objectToBeMadeIntoVariant, false);
            return var8;
        } else {
            SafeArray returnVariant = null;
            int len1 = Array.getLength(objectToBeMadeIntoVariant);
            Class componentType = objectToBeMadeIntoVariant.getClass().getComponentType();
            int returnVariant1;
            int i;
            if(componentType.isArray()) {
                returnVariant1 = 0;

                Object e1;
                int j;
                for(i = 0; i < len1; ++i) {
                    e1 = Array.get(objectToBeMadeIntoVariant, i);
                    j = Array.getLength(e1);
                    if(returnVariant1 < j) {
                        returnVariant1 = j;
                    }
                }

                returnVariant = new SafeArray(12, len1, returnVariant1);

                for(i = 0; i < len1; ++i) {
                    e1 = Array.get(objectToBeMadeIntoVariant, i);

                    for(j = 0; j < Array.getLength(e1); ++j) {
                        returnVariant.setVariant(i, j, objectToVariant(Array.get(e1, j)));
                    }
                }
            } else if(Byte.TYPE.equals(componentType)) {
                byte[] var9 = (byte[])((byte[])objectToBeMadeIntoVariant);
                returnVariant = new SafeArray(17, len1);

                for(i = 0; i < len1; ++i) {
                    returnVariant.setByte(i, var9[i]);
                }
            } else if(Integer.TYPE.equals(componentType)) {
                int[] var10 = (int[])((int[])objectToBeMadeIntoVariant);
                returnVariant = new SafeArray(3, len1);

                for(i = 0; i < len1; ++i) {
                    returnVariant.setInt(i, var10[i]);
                }
            } else if(Double.TYPE.equals(componentType)) {
                double[] var11 = (double[])((double[])objectToBeMadeIntoVariant);
                returnVariant = new SafeArray(5, len1);

                for(i = 0; i < len1; ++i) {
                    returnVariant.setDouble(i, var11[i]);
                }
            } else if(Long.TYPE.equals(componentType)) {
                long[] var12 = (long[])((long[])objectToBeMadeIntoVariant);
                returnVariant = new SafeArray(20, len1);

                for(i = 0; i < len1; ++i) {
                    returnVariant.setLong(i, var12[i]);
                }
            } else {
                returnVariant = new SafeArray(12, len1);

                for(returnVariant1 = 0; returnVariant1 < len1; ++returnVariant1) {
                    returnVariant.setVariant(returnVariant1, objectToVariant(Array.get(objectToBeMadeIntoVariant, returnVariant1)));
                }
            }

            Variant var13 = new Variant();
            populateVariant(var13, returnVariant, false);
            return var13;
        }
    }

    public static Variant[] objectsToVariants(Object[] arrayOfObjectsToBeConverted) {
        if(arrayOfObjectsToBeConverted instanceof Variant[]) {
            return (Variant[])((Variant[])arrayOfObjectsToBeConverted);
        } else {
            Variant[] vArg = new Variant[arrayOfObjectsToBeConverted.length];

            for(int i = 0; i < arrayOfObjectsToBeConverted.length; ++i) {
                vArg[i] = objectToVariant(arrayOfObjectsToBeConverted[i]);
            }

            return vArg;
        }
    }

    public static Object variantToObject(Variant sourceData) {
        Object result = null;
        short type = sourceData.getvt();
        if((type & 8192) == 8192) {
            SafeArray array = null;
            type = (short)(type - 8192);
            array = sourceData.toSafeArray();
            result = array;
        } else {
            switch(type) {
                case 0:
                case 1:
                    break;
                case 2:
                    result = new Short(sourceData.getShort());
                    break;
                case 3:
                    result = new Integer(sourceData.getInt());
                    break;
                case 4:
                    result = new Float(sourceData.getFloat());
                    break;
                case 5:
                    result = new Double(sourceData.getDouble());
                    break;
                case 6:
                    result = sourceData.getCurrency();
                    break;
                case 7:
                    result = sourceData.getJavaDate();
                    break;
                case 8:
                    result = sourceData.getString();
                    break;
                case 9:
                    result = sourceData.getDispatch();
                    break;
                case 10:
                    result = new NotImplementedException("toJavaObject() Not implemented for VariantError");
                    break;
                case 11:
                    result = new Boolean(sourceData.getBoolean());
                    break;
                case 12:
                    result = new NotImplementedException("toJavaObject() Not implemented for VariantVariant without ByRef");
                    break;
                case 13:
                    result = new NotImplementedException("toJavaObject() Not implemented for VariantObject");
                    break;
                case 14:
                    result = sourceData.getDecimal();
                    break;
                case 17:
                    result = new Byte(sourceData.getByte());
                    break;
                case 20:
                    result = new Long(sourceData.getLong());
                    break;
                case 4095:
                    result = new NotImplementedException("toJavaObject() Not implemented for VariantBstrBlob/VariantTypeMask");
                    break;
                case 8192:
                    result = new NotImplementedException("toJavaObject() Not implemented for VariantArray");
                    break;
                case 16384:
                    result = new NotImplementedException("toJavaObject() Not implemented for VariantByref");
                    break;
                case 16386:
                    result = new Short(sourceData.getShortRef());
                    break;
                case 16387:
                    result = new Integer(sourceData.getIntRef());
                    break;
                case 16388:
                    result = new Float(sourceData.getFloatRef());
                    break;
                case 16389:
                    result = new Double(sourceData.getDoubleRef());
                    break;
                case 16390:
                    result = sourceData.getCurrencyRef();
                    break;
                case 16391:
                    result = sourceData.getJavaDateRef();
                    break;
                case 16392:
                    result = sourceData.getStringRef();
                    break;
                case 16393:
                    result = sourceData.getDispatchRef();
                    break;
                case 16395:
                    result = new Boolean(sourceData.getBooleanRef());
                    break;
                case 16396:
                    result = sourceData.getVariant();
                    break;
                case 16398:
                    result = sourceData.getDecimalRef();
                    break;
                case 16401:
                    result = new Byte(sourceData.getByteRef());
                    break;
                case 16404:
                    result = new Long(sourceData.getLongRef());
                    break;
                default:
                    result = new NotImplementedException("Unknown return type: " + type);
            }

            if(result instanceof JacobException) {
                throw (JacobException)result;
            }
        }

        return result;
    }

    protected static void validateDecimalScaleAndBits(BigDecimal in) {
        BigInteger allWordBigInt = in.unscaledValue();
        if(in.scale() > 28) {
            throw new IllegalArgumentException("VT_DECIMAL only supports a maximum scale of 28 and the passed in value has a scale of " + in.scale());
        } else if(in.scale() < 0) {
            throw new IllegalArgumentException("VT_DECIMAL only supports a minimum scale of 0 and the passed in value has a scale of " + in.scale());
        } else if(allWordBigInt.bitLength() > 96) {
            throw new IllegalArgumentException("VT_DECIMAL supports a maximum of 96 bits not counting scale and the number passed in has " + allWordBigInt.bitLength());
        }
    }

    protected static void validateDecimalMinMax(BigDecimal in) {
        if(in == null) {
            throw new IllegalArgumentException("null is not a supported Decimal value.");
        } else if(LARGEST_DECIMAL.compareTo(in) < 0) {
            throw new IllegalArgumentException("Value too large for VT_DECIMAL data type:" + in.toString() + " integer: " + in.toBigInteger().toString(16) + " scale: " + in.scale());
        } else if(SMALLEST_DECIMAL.compareTo(in) > 0) {
            throw new IllegalArgumentException("Value too small for VT_DECIMAL data type:" + in.toString() + " integer: " + in.toBigInteger().toString(16) + " scale: " + in.scale());
        }
    }

    public static BigDecimal roundToMSDecimal(BigDecimal sourceDecimal) {
        BigInteger sourceDecimalIntComponent = sourceDecimal.unscaledValue();
        BigDecimal destinationDecimal = new BigDecimal(sourceDecimalIntComponent, sourceDecimal.scale());
        byte roundingModel = 4;
        validateDecimalMinMax(destinationDecimal);
        BigInteger allWordBigInt = destinationDecimal.unscaledValue();
        if(allWordBigInt.bitLength() > 96) {
            destinationDecimal = destinationDecimal.round(new MathContext(29));
            if(allWordBigInt.bitLength() > 96) {
                destinationDecimal = destinationDecimal.round(new MathContext(28));
            }
        }

        if(destinationDecimal.scale() > 28) {
            destinationDecimal = destinationDecimal.setScale(28, roundingModel);
        }

        if(destinationDecimal.scale() < 0) {
            destinationDecimal = destinationDecimal.setScale(0, roundingModel);
        }

        return destinationDecimal;
    }
}
