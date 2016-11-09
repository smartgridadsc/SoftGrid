package it.illinois.adsc.ema.control.center;

/**
 * Created by prageethmahendra on 2/3/2016.
 */
public class ExperimentData {
    private int expNo;
    private int percentage;
    private float loadRank;
    private float busFrequency;
    String lineStatus;

    public ExperimentData() {
    }

    public int getExpNo() {
        return expNo;
    }

    public void setExpNo(int expNo) {
        this.expNo = expNo;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public float getLoadRank() {
        return loadRank;
    }

    public void setLoadRank(float loadRank) {
        this.loadRank = loadRank;
    }

    public String getLineStatus() {
        return lineStatus;
    }

    public void setLineStatus(String lineStatus) {
        this.lineStatus = lineStatus;
    }

    public float getBusFrequency() {
        return busFrequency;
    }

    public void setBusFrequency(float busFrequency) {
        this.busFrequency = busFrequency;
    }

    @Override
    public String toString() {
        return expNo +",\t" + lineStatus+",\t" + percentage + "%,\t" + loadRank + ",\t" + busFrequency;
    }
}
