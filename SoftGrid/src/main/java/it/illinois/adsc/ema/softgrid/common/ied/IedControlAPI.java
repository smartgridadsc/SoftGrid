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
package it.illinois.adsc.ema.softgrid.common.ied;

import java.util.List;

/**
* Created by prageethmahendra on 21/1/2016.
*/
public interface IedControlAPI {
    public String changeParametersSingleElement(String objectType, String[] paramList, String[] valueList);
    public String changeParametersMultipleElement(String objectType, String[] paramList, List<String[]> valueList);
    public String changeParametersMultipleElementFlatInput(String objectType, String[] paramList, String[] valueList);
    public String closeCase();
    public String getCaseHeader();
    public String getFieldList(String objectType);
    public String[] getParametersSingleElement(String objectType, String[] paramList, String[] values);
    public String getParametersMultipleElement(String objectType, String[] paramList, String filterName);
    public String getParametersMultipleElementFlatOutput(String objectType, String[] paramList, String filterName);
    public String getSpecificFieldMaxNum(String objectType, String fieldName);
    public String listOfDevicesAsVariantStrings(String objectType, String filter);
    public String loadState();
    public String openCase();
    public String openCaseType(String[] options);
    public String processAuxFile(String aux_file_name);
    public String runScriptCommand(String statements);
    public String saveCase();
    public String saveState();
    public String sendToExcel(String objectType, String filterName, String[] fieldList);
    public String writeAuxFile(String filterName, String objectType, String eString, boolean toAppend, String[] fieldList, String aux_file_name);

    void stop();
    public boolean isCaseOpened();
}
