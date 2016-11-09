package it.illinois.adsc.ema.control.proxy.infor;

/**
 * Created by prageethmahendra on 2/2/2016.
 */
public class ProxyVariant {
    private String string;
    private int integer;
    private long longInteger;
    private float floatValue;
    private double doubleValue;

    public ProxyVariant() {
    }

    public ProxyVariant(String string) {
        this.string = string;
    }

    public ProxyVariant(int integer) {
        this.integer = integer;
    }

    public ProxyVariant(long longInteger) {
        this.longInteger = longInteger;
    }

    public ProxyVariant(float floatValue) {
        this.floatValue = floatValue;
    }

    public ProxyVariant(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public long getLongInteger() {
        return longInteger;
    }

    public void setLongInteger(long longInteger) {
        this.longInteger = longInteger;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }
}
