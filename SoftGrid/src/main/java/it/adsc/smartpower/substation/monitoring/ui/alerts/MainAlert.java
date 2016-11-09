package it.adsc.smartpower.substation.monitoring.ui.alerts;

import it.adsc.smartpower.substation.monitoring.ui.SMAlert;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prageethmahendra on 27/5/2016.
 */
public class MainAlert extends SMAlert {
    List<SMAlert> fqAlertList = new ArrayList<SMAlert>();

    public MainAlert(String deviceDetails, String timestamp, String description) {
        super(deviceDetails, timestamp, description);
    }

    public void addFqAlert(SMAlert smAlert) {
        fqAlertList.add(smAlert);
    }

    protected void formatText() {
        String color = "red";
        if (description.contains("Voltage")) {
            color = "purple";
        } else if (description.contains("Branch Limit")) {
            color = "orange";
        }
        if (fqAlertList != null && fqAlertList.size() > 0) {
            this.setText("<html><font color=\"black\" size=\"2\"><font color=\"" + color + "\" size=\"3\"> Current Violation : "
                    + "<b>" + fqAlertList.get(fqAlertList.size() - 1) +
                    "</font><br> violation count" +
                    fqAlertList.size() + "<br>" +
                    timestamp + "</font></html>");
        } else {
            this.setText("");
        }
        this.setAlignmentX(JButton.LEFT);
        this.setBackground(new Color(255, 250, 250));
    }
}
