package it.illinois.adsc.ema.control.db;

import it.illinois.adsc.ema.control.proxy.util.DeviceType;

/**
 * Created by prageethmahendra on 24/4/2017.
 */
public interface DBAPI {
    /**
     * To load the Transient data values from the database.
     * Normally transient data is available only in the database and the powerworld returns the steady state data.
     * @param objectType : type of the object. Eg. BUS, BRANCH, TRANSFORMER
     * @param paramList : Parameter Name List
     * @param values : Result/Value List
     * @return : Final result after updating the values from the database
     */
    String[] getParametersSingleElement(String objectType, String[] paramList, String[] values);
}
