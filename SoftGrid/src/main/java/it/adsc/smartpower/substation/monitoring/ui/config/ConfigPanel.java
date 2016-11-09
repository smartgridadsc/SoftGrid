package it.adsc.smartpower.substation.monitoring.ui.config;

import com.l2fprod.common.propertysheet.AbstractProperty;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import it.adsc.smartpower.substation.monitoring.ui.SPMainFrame;
import it.illinois.adsc.ema.pw.ConfigReader;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.Properties;

/**
 * Created by prageethmahendra on 8/8/2016.
 */
public class ConfigPanel extends JPanel implements ActionListener, PropertyChangeListener {
    private static ConfigPanel instance;
    private Property[] properties;
    private PropertySheetPanel propertySheetPanel = new PropertySheetPanel();
    private JLabel filenameLabel = new JLabel("Configuration File");
    private JTextField filenameField = new JTextField();
    private JButton fileSelectorButton = new JButton("");
    private JButton fileSaveButton = new JButton("");
    private static Properties configProperties = null;

    private ConfigPanel() {
        try {
            setupGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConfigPanel getInstance() {
        if (instance == null) {
            instance = new ConfigPanel();
        }
        return instance;
    }

    private void setupGUI() throws Exception {
        this.setLayout(new GridBagLayout());
        fileSelectorButton.setPreferredSize(new Dimension(22, 22));
        fileSelectorButton.setMinimumSize(new Dimension(22, 22));
        fileSelectorButton.setMaximumSize(new Dimension(22, 22));
        fileSelectorButton.setToolTipText("Select Config File");
        fileSelectorButton.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("select-folder.png"))));
        fileSaveButton.setPreferredSize(new Dimension(22, 22));
        fileSaveButton.setMinimumSize(new Dimension(22, 22));
        fileSaveButton.setMaximumSize(new Dimension(22, 22));
        fileSaveButton.setToolTipText("Save Changes");
        fileSaveButton.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("save-icon-9.png"))));
        filenameField.setSize(new Dimension(200, 22));
        filenameField.setMinimumSize(new Dimension(200, 22));
        filenameField.setMaximumSize(new Dimension(200, 22));
        this.add(propertySheetPanel, new GridBagConstraints(0, 0, 4, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(filenameLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 12, 6, 6), 0, 0));
        this.add(filenameField, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(6, 6, 6, 0), 0, 0));
        this.add(fileSelectorButton, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 2, 6, 0), 0, 0));
        this.add(fileSaveButton, new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 2, 6, 12), 0, 0));
        propertySheetPanel.addPropertySheetChangeListener(this);
        fileSelectorButton.addActionListener(this);
        fileSaveButton.addActionListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() != null && evt.getSource() instanceof MonitorProperty) {
            MonitorProperty monitorProperty = (MonitorProperty) evt.getSource();
            configProperties.setProperty(monitorProperty.getName(), monitorProperty.getValue().toString());
            ConfigReader.updateConfigUtils(monitorProperty.getName(), monitorProperty.getValue().toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fileSelectorButton) {
            File file = new File("..\\");
            JFileChooser fc = new JFileChooser(file);
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setSize(400, 400);
            fc.showDialog(SPMainFrame.getInstance(), "config/config.properties");
            file = fc.getSelectedFile();
            if (file != null) {
                filenameField.setText(file.getAbsolutePath());
                setupConfigPanel(file);
            } else {
                System.out.println("Config File Not Selected...!");
            }
        } else if (e.getSource() == fileSaveButton) {
            if (filenameField.getText() != null && new File(filenameField.getText()).exists()) {
                BufferedWriter projectConfigWriter = null;
                FileOutputStream configOutputStream = null;
                try {
                    configOutputStream = new FileOutputStream(filenameField.getText());
                    projectConfigWriter = new BufferedWriter(new FileWriter(ConfigUtil.MAIN_CONFIG_REF_FILE));
                    projectConfigWriter.write("ConfigFilePath " + filenameField.getText().replace("\\","\\\\").replace("C:","C\\:") + "\n");
                    projectConfigWriter.write("POWER_WORLD_EXE " + ConfigUtil.POWER_WORLD_EXE + "\n");
                    projectConfigWriter.flush();
                    configProperties.store(configOutputStream, "");
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    if (projectConfigWriter != null) {
                        try {
                            projectConfigWriter.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public Property[] getProperties() {
        return properties;
    }

    public void setProperties(Property[] properties) {
        this.properties = properties;
        if (properties != null) {
            try {
                propertySheetPanel.setProperties(properties);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.getContentPane().setLayout(new BorderLayout());
        jFrame.getContentPane().add(ConfigPanel.getInstance(), BorderLayout.CENTER);
        jFrame.setSize(new Dimension(400, 400));
        jFrame.setVisible(true);
    }

    public void setupConfigPanel() {
        String configFilePath = "../SmartPower/config.properties";
        ConfigReader.getAllProperties(new File(ConfigUtil.MAIN_CONFIG_REF_FILE));
        BufferedReader bf = null;
        if (filenameField.getText() == null || filenameField.getText().isEmpty()) {
            filenameField.setText(new File(ConfigUtil.CONFIG_FILE_PATH).getAbsolutePath());
        }
        setupConfigPanel(new File(filenameField.getText()));
    }

    public void setupConfigPanel(File file) {
        configProperties = ConfigReader.getAllProperties(file);
        if (configProperties != null) {
            Property[] property = new Property[configProperties.size()];
            int count = 0;
            for (Object key : configProperties.keySet()) {
                String keyString = key.toString();
                String value = configProperties.getProperty(keyString);
                property[count] = new MonitorProperty(keyString, value);
                count++;
            }
            propertySheetPanel.setProperties(property);
        }
    }
}

class MonitorProperty extends AbstractProperty {
    private String name = "";
    private String category = "";

    public MonitorProperty(String name, String value) {
        this.setValue(value);
        this.setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getShortDescription() {
        return name;
    }

    @Override
    public Class getType() {
        return String.class;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void readFromObject(Object o) {
    }

    @Override
    public void writeToObject(Object o) {
    }
}
