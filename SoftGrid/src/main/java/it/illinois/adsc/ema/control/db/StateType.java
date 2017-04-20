package it.illinois.adsc.ema.control.db;

/**
 * Created by prageethmahendra on 13/4/2017.
 */
public enum StateType {
    FREQUENCY(0);

    int value = 0;

    private StateType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
