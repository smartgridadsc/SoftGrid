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
package it.illinois.adsc.ema.control.center.command;

/**
 * Created by prageethmahendra on 18/2/2016.
 */
public class Command {
    private CommandType commandType;
    private int subtationAddressSpace;
    private int objectAddress;
    private String feild;
    private String value;
    private int feildQualifier;
    private int valueType;
    private boolean select = true;
    private String IEDType;
    private int percent;

    public Command() {
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public int getSubtationAddressSpace() {
        return subtationAddressSpace;
    }

    public void setSubtationAddressSpace(int bustationAddressSpace) {
        this.subtationAddressSpace = bustationAddressSpace;
    }

    public String getFeild() {
        return feild;

    }

    public void setFeild(String feild) {
        this.feild = feild;
        switch (feild.toLowerCase())
        {
            case "linestatus": feildQualifier = 1; break;
            case "ssstatus": feildQualifier = 1; break;
            case "putratio" : feildQualifier = 2; break;
            case "loadmw" : feildQualifier = 3; break;
            default:
                feildQualifier = 0;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getFeildQualifier() {
        return feildQualifier;
    }

    public void setFeildQualifier(int feildQualifier) {
        this.feildQualifier = feildQualifier;
    }

    public int getValueType() {
        return valueType;
    }

    public int getObjectAddress() {
        return objectAddress;
    }

    public void setObjectAddress(int objectAddress) {
        this.objectAddress = objectAddress;
    }

    public void setValueType(int valueType) {
        this.valueType = valueType;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public void setIEDType(String IEDType) {
        this.IEDType = IEDType;
    }

    public String getIEDType() {
        return IEDType;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
