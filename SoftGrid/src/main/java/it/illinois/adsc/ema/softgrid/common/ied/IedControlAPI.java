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
