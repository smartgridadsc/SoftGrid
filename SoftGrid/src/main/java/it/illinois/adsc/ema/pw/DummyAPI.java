package it.illinois.adsc.ema.pw;

import com.jacob.com.Variant;
import it.illinois.adsc.ema.pw.ied.pwcom.PWComAPI;

import java.util.List;
import java.util.Random;

/**
 * Created by prageethmahendra on 26/4/2017.
 */
public class DummyAPI implements PWComAPI {

    private static DummyAPI instance;

    public static DummyAPI getInstance() {
        if(instance == null)
        {
            instance = new DummyAPI();
        }
        return instance;
    }

    @Override
    public Variant changeParameters(String objectType, Variant paramList, Variant values) {
        return null;
    }

    @Override
    public String changeParametersSingleElement(String objectType, String[] paramList, String[] valueList) {
        return null;
    }

    @Override
    public Variant changeParametersMultipleElement(String objectType, String[] paramList, List<String[]> valueList) {
        return null;
    }

    @Override
    public Variant changeParametersMultipleElementFlatInput(String objectType, String[] paramList, String[] valueList) {
        return null;
    }

    @Override
    public Object closeCase() {
        return null;
    }

    @Override
    public Variant getCaseHeader() {
        return null;
    }

    @Override
    public Variant getFieldList(String objectType) {
        return null;
    }

    @Override
    public String[] getParametersSingleElement(String objectType, String[] paramList, String[] values) {
        Random random = new Random(System.currentTimeMillis());
        if (paramList != null && values != null) {
            int index = 0;
            for (String s : values) {
                if (s.length() == 0) {
                    values[index] = String.valueOf(random.nextInt() % 100);
                }
                index++;
            }
        }
        return values;
    }

    @Override
    public Variant getParametersMultipleElement(String objectType, String[] paramList, String filterName) {
        return null;
    }

    @Override
    public Variant getParametersMultipleElementFlatOutput(String objectType, String[] paramList, String filterName) {
        return null;
    }

    @Override
    public Object getSpecificFieldList(String objectType, String[] fieldList) {
        return null;
    }

    @Override
    public Variant getSpecificFieldMaxNum(String objectType, String fieldName) {
        return null;
    }

    @Override
    public Object listOfDevices(String objectType, String filter) {
        return null;
    }

    @Override
    public Variant listOfDevicesAsVariantStrings(String objectType, String filter) {
        return null;
    }

    @Override
    public Object listOfDevicesFlatOutput(String objectType, String filter) {
        return null;
    }

    @Override
    public Variant loadState() {
        return null;
    }

    @Override
    public Object openCase(String fileName) {
        return "";
    }

    @Override
    public Variant openCaseType(String fileName, String fileType, String[] options) {
        return null;
    }

    @Override
    public Variant processAuxFile(String fileName) {
        return null;
    }

    @Override
    public Variant loadContingencies(String fileName) {
        return null;
    }

    @Override
    public Object runScriptCommand(String statements) {
        return null;
    }

    @Override
    public Object saveCase(String fileName, String fileType, boolean overwrite) {
        return null;
    }

    @Override
    public Object saveState() {
        return null;
    }

    @Override
    public Variant sendToExcel(String objectType, String filterName, String[] fieldList) {
        return null;
    }

    @Override
    public Variant writeAuxFile(String fileName, String filterName, String objectType, String eString, boolean toAppend, String[] fieldList) {
        return null;
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isCaseOpened() {
        return false;
    }
}
