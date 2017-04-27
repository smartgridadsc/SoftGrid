package it.illinois.adsc.ema.control.db;

import it.illinois.adsc.ema.control.proxy.util.DeviceType;

/**
 * Created by prageethmahendra on 24/4/2017.
 */
public class TransientController implements DBAPI {
    public static TransientController instance;

    public static TransientController getInstance() {
        if (instance == null) {
            instance = new TransientController();
        }
        return instance;
    }

    @Override
    public String[] getParametersSingleElement(String objectType, String[] paramList, String[] values) {
        if (objectType != null && paramList != null && values != null) {
            switch (objectType.toUpperCase()) {
                case "BUS":
                    int index = 0;
                    for (String param : paramList) {
                        if (param.equalsIgnoreCase("frequency")) {
                            DataObject dataObject = DBConnection.getConnection().getDataObject(Integer.parseInt(values[0].trim()),
                                    DeviceType.BUS,
                                    StateType.FREQUENCY);
                            if (dataObject != null) {
                                values[index] = dataObject.getValue();
                            }
                            break;
                        }
                        index++;
                    }
            }
        }
        return values;
    }
}
