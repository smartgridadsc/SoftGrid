package it.adsc.smartpower.substation.monitoring.ui;


import java.awt.Color;

import javax.swing.*;

/**
 * Created by prageethmahendra on 16/5/2016.
 */
public class SMAlert extends JButton {
    protected String deviceDetails;
    protected String timestamp;
    protected String description;
    protected SMAlert previouseAlert;
    protected SMAlert nextAlert;


    public SMAlert(String deviceDetails, String timestamp, String description) {
        this.deviceDetails = deviceDetails;
        this.timestamp = timestamp;
        this.description = description;
        formatText();
    }

    protected void formatText() {
        String color = "red";
        if (description.contains("Voltage")) {
            color = "purple";
        } else if (description.contains("Branch Limit")) {
            color = "orange";
        }
        this.setText("<html><font color=\"black\" size=\"2\"><font color=\"" + color + "\" size=\"3\">" + description + "</font><br>" + deviceDetails.replaceFirst("u'", "").replace("'", "") + "<br>" + timestamp + "</font></html>");
        this.setAlignmentX(JButton.LEFT);
        this.setBackground(new Color(255,250,250));
    }

    public String getDeviceDetails() {
        return deviceDetails;
    }

    public void setDeviceDetails(String deviceDetails) {
        this.deviceDetails = deviceDetails;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SMAlert getPreviouseAlert() {
        return previouseAlert;
    }

    public void setPreviouseAlert(SMAlert previouseAlert) {
        this.previouseAlert = previouseAlert;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof SMAlert) {
            SMAlert tempSMAlert = (SMAlert) obj;
            return tempSMAlert.getDeviceDetails().equals(this.deviceDetails) &&
                    tempSMAlert.getTimestamp().equals(this.timestamp) &&
                    tempSMAlert.getDescription().equals(this.description);
        }
        return false;
    }

    public void setNextAlert(SMAlert nextAlert) {
        this.nextAlert = nextAlert;
    }

    public SMAlert getNextAlert() {
        return nextAlert;
    }

    public boolean isFrequencyViolation() {
        return description.contains("Frequency");
    }

    public boolean isBranchLimitViolation() {
        return description.contains("Branch Limit");
    }

    public boolean isVoltageViolation() {
        return description.contains("Voltage");
    }
}
