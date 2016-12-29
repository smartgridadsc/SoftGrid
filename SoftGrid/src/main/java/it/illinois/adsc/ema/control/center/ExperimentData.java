/* Copyright (C) 2016 Advanced Digital Science Centre

        * This file is part of Soft-Grid.
        * For more information visit https://www.illinois.adsc.com.sg/cybersage/
        *
        * Soft-Grid is free software: you can redistribute it and/or modify
        * it under the terms of the GNU General Public License as published by
        * the Free Software Foundation, either version 3 of the License, or
        * (at your option) any later version.
        *
        * Soft-Grid is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        * GNU General Public License for more details.
        *
        * You should have received a copy of the GNU General Public License
        * along with Soft-Grid.  If not, see <http://www.gnu.org/licenses/>.

        * @author Prageeth Mahendra Gunathilaka
*/
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
