package it.illinois.adsc.ema.control.db;

/**
 * Created by prageethmahendra on 13/4/2017.
 */
public enum DataValueType {
    DOUBLE(0), LINESTATUS(1), STRING(2), BOOL(3);

    int value = 0;

    private DataValueType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
