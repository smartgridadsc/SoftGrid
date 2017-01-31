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
package it.illinois.adsc.ema.pw.tools;

/**
 * Created by prageethmahendra on 14/4/2016.
 */
public class Configuration {
    private int type;
    private double mitigatinoDuration;
    private int commandCount;
    private int experimentCount;
    private double delayUpperBound;
    private int randFunction;
    private double networkDelay;
    private double delaySelectThreshold;
    public static double PN_Percent = 20;
    public static double PT_Percent = 5;

    public Configuration(int type, double mitigatinoDuration, double networkDelay, int commandCount, int experimentCount, double delayUpperBound, int randFunction, double delaySelectThreshold) {
        this.type = type;
        this.mitigatinoDuration = mitigatinoDuration;
        this.commandCount = commandCount;
        this.experimentCount = experimentCount;
        this.delayUpperBound = delayUpperBound;
        this.randFunction = randFunction;
        this.networkDelay = networkDelay;
        this.delaySelectThreshold = delaySelectThreshold;
    }

    public double getDelaySelectThreshold() {
        return delaySelectThreshold;
    }

    public void setDelaySelectThreshold(double delaySelectThreshold) {
        this.delaySelectThreshold = delaySelectThreshold;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getMitigatinoDuration() {
        return mitigatinoDuration;
    }

    public void setMitigatinoDuration(double mitigatinoDuration) {
        this.mitigatinoDuration = mitigatinoDuration;
    }

    public int getCommandCount() {
        return commandCount;
    }

    public void setCommandCount(int commandCount) {
        this.commandCount = commandCount;
    }

    public int getExperimentCount() {
        return experimentCount;
    }

    public void setExperimentCount(int experimentCount) {
        this.experimentCount = experimentCount;
    }

    public double getDelayUpperBound() {
        return delayUpperBound;
    }

    public void setDelayUpperBound(double delayUpperBound) {
        this.delayUpperBound = delayUpperBound;
    }

    public int getRandFunction() {
        return randFunction;
    }

    public void setRandFunction(int randFunction) {
        this.randFunction = randFunction;
    }

    public double getNetworkDelay() {
        return networkDelay;
    }

    public void setNetworkDelay(double networkDelay) {
        this.networkDelay = networkDelay;
    }

    @Override
    public String toString() {
//        return this.type+"_"+mitigatinoDuration + "MIT_" +commandCount +"CMD_"+experimentCount + "EXP_" + delayUpperBound + "DUB_" + randFunction +"RAND_" + networkDelay +"PN";
        return this.type+"_"+mitigatinoDuration + "MIT_" +commandCount +"CMD_"+experimentCount + "EXP_" + delayUpperBound + "DUB_" + randFunction +"RAND_"+delaySelectThreshold + "SELE_";
    }
}
