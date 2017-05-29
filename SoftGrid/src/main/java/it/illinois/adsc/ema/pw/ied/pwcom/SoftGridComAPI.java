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
package it.illinois.adsc.ema.pw.ied.pwcom;

import com.jacob.com.Variant;

import java.util.List;

/**
 * Created by prageethmahendra on 20/1/2016.
 */
public interface SoftGridComAPI {
    @Deprecated
    public Variant changeParameters(String objectType, Variant paramList, Variant values);
    public String changeParametersSingleElement(String objectType, String[] paramList, String[] valueList);
    public Variant changeParametersMultipleElement(String objectType, String[] paramList, List<String[]> valueList);
    public Variant changeParametersMultipleElementFlatInput(String objectType, String[] paramList, String[] valueList);
    public Object closeCase();
    public Variant getCaseHeader();
    public Variant getFieldList(String objectType);
    public String[] getParametersSingleElement(String objectType, String[] paramList, String[] values);
    public Variant getParametersMultipleElement(String objectType, String[] paramList, String filterName);
    public Variant getParametersMultipleElementFlatOutput(String objectType, String[] paramList, String filterName);
//    @Deprecated
//    public Variant getParameters();
    public Object getSpecificFieldList(String objectType, String[] fieldList);
    public Variant getSpecificFieldMaxNum(String objectType, String fieldName);
    public Object listOfDevices(String objectType, String filter);
    public Variant listOfDevicesAsVariantStrings(String objectType, String filter);
    public Object listOfDevicesFlatOutput(String objectType, String filter);
    public Variant loadState();
    public Object openCase(String fileName);
    public Variant openCaseType(String fileName, String fileType, String[] options);
    public Variant processAuxFile(String fileName);
    public Variant loadContingencies(String fileName);
    public Object runScriptCommand(String statements);
    public Object saveCase(String fileName, String fileType, boolean overwrite);
    public Object saveState();
    public Variant sendToExcel(String objectType, String filterName, String[] fieldList);
    public Variant writeAuxFile(String fileName, String filterName, String objectType, String eString, boolean toAppend, String[] fieldList);

    void stop();

    boolean isCaseOpened();
}
