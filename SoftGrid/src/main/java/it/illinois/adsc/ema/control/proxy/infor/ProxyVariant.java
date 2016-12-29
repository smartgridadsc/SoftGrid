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
