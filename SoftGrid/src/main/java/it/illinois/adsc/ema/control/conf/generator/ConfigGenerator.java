package it.illinois.adsc.ema.control.conf.generator;

import it.illinois.adsc.ema.control.cid.*;
import it.illinois.adsc.ema.control.conf.*;
import it.illinois.adsc.ema.softgrid.common.ConfigUtil;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

/**
 * Created by prageethmahendra on 19/2/2016.
 */
public class ConfigGenerator {
    public static final String TRANSFORMER = "TRANSFORMER";
    public static final String CIRCUITE_BREAKER = "CB";
    public static final String GENERATOR = "GEN";
    public static final String LOAD = "LOAD";
    public static final String BUS = "BUS";
    public static final String SHUNT = "SHUNT";
    public static final String PW_CASE_INFOR = "PWCaseInformation";
    public static final int FIRST_PORT = 10003;
    private static Properties pwToSclMappingProperties;
    private static Properties iedTypeToFieldMappingProperties;

    public static PWModelType deserializeConfigXml(String confFileName) throws IOException {
        PWModelType pwModelType = null;
        try {
            JAXBContext jc = JAXBContext.newInstance("it.illinois.adsc.ema.control.conf");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            File file = new File(confFileName);
            if (!file.exists()) {
                file = new File("PWModel.xml");
            }
            if (file.exists()) {
                InputStream xml = new FileInputStream(file);
                JAXBElement<PWModelType> feed = unmarshaller.unmarshal(new StreamSource(xml), PWModelType.class);
                pwModelType = feed.getValue();
                xml.close();
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return pwModelType;
    }

    public static void generateConfigXml(String xmlDirName, String configFileName, Properties properties) {
        generateConfigXml(xmlDirName, configFileName, properties.getProperty("ip"));
    }

    public static void generateConfigXml(String xmlDirName, String configFileName, String ip) {
        try {
            if (!ConfigUtil.PW_TO_SCL_MAPPING.isEmpty()) {
                initProperties();
            }
            JAXBContext jc = JAXBContext.newInstance("it.illinois.adsc.ema.control.conf");
            Marshaller marshaller = jc.createMarshaller();
            File dir = new File(xmlDirName);
            if (!dir.exists()) {
                dir = new File("scl");
            }
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                PWModelType pwModelType = new PWModelType();
                ProxyNodeType proxyNodeType = new ProxyNodeType();
                System.out.println("properties.getProperty(\"ip\") = " + ip);
                proxyNodeType.setIp(ip);
                proxyNodeType.setPort("2404");
                proxyNodeType.setActive("true");
                pwModelType.getProxyNode().add(proxyNodeType);
                ControlCenterNodeType controlCenterNodeType = new ControlCenterNodeType();
                controlCenterNodeType.setIp(ip);
                controlCenterNodeType.setActive("true");
                pwModelType.setControlCenterNode(controlCenterNodeType);
                generateIntegrationTypes(proxyNodeType, files, 0, ip);
                for (File file : files) {
                    if (file.exists() && file.isDirectory()) {
                        generateIntegrationTypes(proxyNodeType, file.listFiles(), 0, ip);
                    }
                }
                int port = FIRST_PORT;
                Collections.sort(pwModelType.getProxyNode().get(0).getIedNode(), new Comparator<IedNodeType>() {
                    @Override
                    public int compare(IedNodeType o1, IedNodeType o2) {
                        return o1.getReference().compareTo(o2.getReference());
                    }
                });
                for (IedNodeType nodeType : pwModelType.getProxyNode().get(0).getIedNode()) {
                    if (port == 100 + FIRST_PORT) {
                        System.out.println();
                    }
                    nodeType.setPort(String.valueOf(port++));
                }
                OutputStream xmlout = null;
                try {
                    System.out.println(configFileName);
                    xmlout = new FileOutputStream(configFileName);
                    marshaller.marshal(pwModelType, xmlout);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (xmlout != null) {
                        try {
                            xmlout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
//          Marshaller marshaller = jc.createMarshaller();
//          marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//          marshaller.marshal(feed, System.out);
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    private static void initProperties() {
        pwToSclMappingProperties = new Properties();
        FileReader fr = null;
        try {
            fr = new FileReader(ConfigUtil.PW_TO_SCL_MAPPING);
            pwToSclMappingProperties.load(fr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        iedTypeToFieldMappingProperties = new Properties();
        fr = null;
        try {
            fr = new FileReader(ConfigUtil.IED_TYPE_TO_FIELD_MAPPING);
            iedTypeToFieldMappingProperties.load(fr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static int generateIntegrationTypes(ProxyNodeType proxyNodeTypes, File[] files, int portOffset, String ip) {
        int count = 0;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (!file.exists() || file.isDirectory()) {
                continue;
            }
            IedNodeType iedNodeType = new IedNodeType();
            iedNodeType.setActive("true");
            iedNodeType.setIp(ip);
            iedNodeType.setPort(String.valueOf(FIRST_PORT + portOffset + i));
            iedNodeType.setPWCaseFileName(file.getAbsolutePath());

            String fname = file.getName();
            String[] fnameElements = fname.split("_");
            ParametersType parametersType = new ParametersType();
            count++;
            String refPrefix = file.getName().replace(".", "");
            switch (fnameElements[0]) {
                case CIRCUITE_BREAKER:
                    iedNodeType.setDevice("Branch");
                    iedNodeType.setReference(refPrefix + "lDevice1/MMXU1.BRANCH_" + fname.split("\\.")[0]);
                    addBranchKeys(fnameElements, parametersType);
                    addBranchDatas(parametersType, CIRCUITE_BREAKER);
                    iedNodeType.setParameters(parametersType);
//                  iedNodeType.setActive("false");
                    proxyNodeTypes.getIedNode().add(iedNodeType);
                    break;
                case TRANSFORMER:
                    iedNodeType.setDevice("Transformer");
                    iedNodeType.setReference(refPrefix + "Transformer/TRNS_MMXU1.TRANS" + fname.split("\\.")[0].substring(TRANSFORMER.length()));
                    addBranchKeys(fnameElements, parametersType);
                    addBranchDatas(parametersType, CIRCUITE_BREAKER);
                    addBranchDatas(parametersType, TRANSFORMER);
                    iedNodeType.setParameters(parametersType);
//                  iedNodeType.setActive("false");
                    proxyNodeTypes.getIedNode().add(iedNodeType);
                    break;
                case GENERATOR:
//                  count++;
                    iedNodeType.setDevice("Gen");
                    iedNodeType.setReference(refPrefix + "Generator/GEN_MMXU1.GEN" + fname.split("\\.")[0].substring(GENERATOR.length()));
                    addGeneratorKeys(fnameElements, parametersType);
                    addBranchDatas(parametersType, GENERATOR);
                    iedNodeType.setParameters(parametersType);
//                  iedNodeType.setActive("false");
                    proxyNodeTypes.getIedNode().add(iedNodeType);
                    break;
                case LOAD:
                    iedNodeType.setDevice("Load");
                    iedNodeType.setReference(refPrefix + "Load/Load_MMXU1.Load" + fname.split("\\.")[0].substring(LOAD.length()));
                    addLoadKeys(fnameElements, parametersType);
                    addBranchDatas(parametersType, LOAD);
                    iedNodeType.setParameters(parametersType);
//                  iedNodeType.setActive("false");
                    proxyNodeTypes.getIedNode().add(iedNodeType);
                    break;
                case BUS:
                    iedNodeType.setDevice("Bus");
                    iedNodeType.setReference(refPrefix + "Bus/Bus_MMXU1.Bus" + fname.split("\\.")[0].substring(BUS.length()));
                    addBusKeys(fnameElements, parametersType);
                    addBranchDatas(parametersType, BUS);
                    iedNodeType.setParameters(parametersType);
//                  iedNodeType.setActive("false");
                    proxyNodeTypes.getIedNode().add(iedNodeType);
                    break;
                case SHUNT:
                    iedNodeType.setDevice("Shunt");
                    iedNodeType.setReference(refPrefix + "Shunt/Shunt_MMXU1.Shunt" + fname.split("\\.")[0].substring(SHUNT.length()));
                    addShuntKeys(fnameElements, parametersType);
                    addBranchDatas(parametersType, SHUNT);
                    iedNodeType.setParameters(parametersType);
//                  iedNodeType.setActive("false");
                    proxyNodeTypes.getIedNode().add(iedNodeType);
                    break;
                case PW_CASE_INFOR:
                    iedNodeType.setDevice("PWCaseInformation");
                    iedNodeType.setReference(refPrefix + "PWCaseInformation/PWCaseInformation_MMXU1.PWCaseInformation_");
//                  addCaseKeys(fnameElements, parametersType);
                    addBranchDatas(parametersType, PW_CASE_INFOR);
                    iedNodeType.setParameters(parametersType);
//                  iedNodeType.setActive("false");
                    proxyNodeTypes.getIedNode().add(iedNodeType);
                    break;
                default:
            }
        }
        return count + portOffset;
    }


//    private static void addTransformerDatas(ParametersType parametersType) {
//        DataType dataType = new DataType();
//        dataType.setPwname("LineTap");
//        dataType.setSclName(pwToSclMappingProperties.getProperty("LineTap"));
//        parametersType.getData().add(dataType);
//    }

    private static void addBranchDatas(ParametersType parametersType, String type) {
        DataType dataType = new DataType();
        String fieldList = iedTypeToFieldMappingProperties.getProperty(type);
        for (String field : fieldList.split(",")) {
            dataType.setPwname(field);
            dataType.setSclName(pwToSclMappingProperties.getProperty(field));
        }
        parametersType.getData().add(dataType);
    }

//    private static void addGeneratorDatas(ParametersType parametersType) {
//        DataType dataType = new DataType();
//        dataType.setPwname("GenMW");
//        dataType.setSclName(pwToSclMappingProperties.getProperty("GenMW"));
//        parametersType.getData().add(dataType);
//
//    }
//
//    private static void addLoadDatas(ParametersType parametersType) {
//        DataType dataType = new DataType();
//        dataType.setPwname("LoadMW");
//        dataType.setSclName(pwToSclMappingProperties.getProperty("LoadMW"));
//        parametersType.getData().add(dataType);
//    }
//
//
//    private static void addCaseDatas(ParametersType parametersType) {
//        DataType dataType = new DataType();
//        dataType.setPwname("OverloadRank");
//        dataType.setSclName(pwToSclMappingProperties.getProperty("OverloadRank"));
//        parametersType.getData().add(dataType);
//    }
//
//    private static void addBusDatas(ParametersType parametersType) {
//        DataType dataType = new DataType();
//        dataType.setSclName(pwToSclMappingProperties.getProperty("Frequency"));
//        dataType.setPwname("Frequency");
//        parametersType.getData().add(dataType);
//        dataType = new DataType();
//        dataType.setSclName(pwToSclMappingProperties.getProperty("BusKVVolt"));
//        dataType.setPwname("BusKVVolt");
//        parametersType.getData().add(dataType);
//    }

//    private static void addShuntDatas(ParametersType parametersType) {
//        DataType dataType = new DataType();
//        dataType.setPwname("SSNMVR");
//        dataType.setSclName(pwToSclMappingProperties.getProperty("SSNMVR"));
//        parametersType.getData().add(dataType);
//    }

    private static void addBusKeys(String[] fnameElements, ParametersType parametersType) {

        KeyType keyType = new KeyType();
        keyType.setPwname("BusNum");
        keyType.setValue(fnameElements[1].split("\\.")[0]);
        parametersType.getKey().add(keyType);

    }

    private static void addShuntKeys(String[] fnameElements, ParametersType parametersType) {

        addBusKeys(fnameElements, parametersType);
        KeyType keyType = new KeyType();
        keyType.setPwname("ShuntID");
        keyType.setValue(fnameElements[2].split("\\.")[0]);
        parametersType.getKey().add(keyType);

    }

    private static void addBranchKeys(String[] fnameElements, ParametersType parametersType) {
        addBusKeys(fnameElements, parametersType);

        KeyType keyType = new KeyType();
        keyType.setPwname("LineCircuit");
        keyType.setValue(fnameElements[3].split("\\.")[0]);
        parametersType.getKey().add(keyType);

        keyType = new KeyType();
        keyType.setPwname("BusNum:1");
        keyType.setValue(fnameElements[2]);
        parametersType.getKey().add(keyType);
    }

    private static void addGeneratorKeys(String[] fnameElements, ParametersType parametersType) {
        addBusKeys(fnameElements, parametersType);

        KeyType keyType = new KeyType();
        keyType.setPwname("GenID");
        keyType.setValue(fnameElements[2].split("\\.")[0]);
        parametersType.getKey().add(keyType);
    }

    private static void addLoadKeys(String[] fnameElements, ParametersType parametersType) {
        addBusKeys(fnameElements, parametersType);

        KeyType keyType = new KeyType();
        keyType.setPwname("LoadID");
        keyType.setValue(fnameElements[2].split("\\.")[0]);
        parametersType.getKey().add(keyType);
    }


    public static void generateCIDFile(PWModelType pwModelType) {
        String logPath = "\\log\\";
        File logFile = new File(ConfigUtil.LOG_FILE);
//        if(logFile.exists())
//        {
        logPath = logFile.getAbsolutePath().replace(logFile.getName(), "");
//        }
        File cidFile = new File(logPath + "\\substation.cid");
        File cidHeader = new File("cidHeader.xml");
        if (cidFile.exists()) {
            cidFile.delete();
        }
        try {
            cidFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuffer headerString = new StringBuffer();
        headerString.append(getCidHeader());
        for (ProxyNodeType proxyNodeType : pwModelType.getProxyNode()) {
            for (IedNodeType iedNodeType : proxyNodeType.getIedNode()) {
                File scdFile = new File(iedNodeType.getPWCaseFileName());
                /*<ConnectedAP iedName="ANIED" apName="S1">
                <Address>
                    <P type="OSI-AP-Title">1 3 9999 106</P>
                    <P type="OSI-AE-Qualifier">33</P>
                    <P type="OSI-PSEL">00 00 00 01</P>
                    <P type="OSI-SSEL">00 01</P>
                    <P type="OSI-TSEL">00 01</P>
                    <P type="IP">192.168.0.114</P>
                    <P type="PORT">10003</P>
                </Address>
            </ConnectedAP>*/
                if (scdFile.exists()) {
                    headerString.append("<ConnectedAP iedName=\"").append(scdFile.getName()).append("\" apName=\"S1\">");
                    headerString.append("<Address>" +
                            "                    <P type=\"OSI-AP-Title\">1 3 9999 106</P>" +
                            "                    <P type=\"OSI-AE-Qualifier\">33</P>" +
                            "                    <P type=\"OSI-PSEL\">00 00 00 01</P>" +
                            "                    <P type=\"OSI-SSEL\">00 01</P>" +
                            "                    <P type=\"OSI-TSEL\">00 01</P>");
                    headerString.append("<P type=\"IP\">").append(iedNodeType.getIp()).append("</P>");
                    headerString.append("<P type=\"PORT\">").append(iedNodeType.getPort()).append("</P>");
                    headerString.append("</Address> </ConnectedAP>");
                }
            }
            headerString.append("  </SubNetwork>" +
                    "    </Communication>\n");
            StringBuffer footerString = new StringBuffer("<DataTypeTemplates>");
            FileWriter fileWriter = null;
            BufferedWriter writer = null;
            try {
                fileWriter = new FileWriter(cidFile);
                writer = new BufferedWriter(fileWriter);
                writer.write(headerString.toString());
                for (IedNodeType iedNodeType : proxyNodeType.getIedNode()) {
                    File scdFile = new File(iedNodeType.getPWCaseFileName());
                    FileReader fr = null;
                    BufferedReader bufferedReader = null;
                    try {
                        fr = new FileReader(scdFile);
                        bufferedReader = new BufferedReader(fr);
                        String line = "";
                        boolean found = false;
                        boolean footer = false;
                        while ((line = bufferedReader.readLine()) != null) {
                            String arr[] = line.split(">");
                            for (String section : arr) {

                                section = section + ">";
                                if (section.trim().equals("<DataTypeTemplates>")) {
                                    continue;
                                }
                                if (section.trim().startsWith("<IED name=\"")) {
                                    found = true;
                                }

                                if (section.trim().contains("<EnumType id=\"CtlModels\">")) {
                                    found = false;
                                    footer = false;
                                    break;
                                }
                                if (footer) {
                                    footerString.append(section);
                                } else if (found) {
                                    writer.write(section);
                                }
                                if (section.trim().contains("</IED>")) {
                                    footer = true;
                                    found = false;
                                }
                            }
                        }
                        writer.write("\n");
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            fr.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                footerString.append(getCidFooter());
                writer.write(footerString + "\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private static String getCidFooter() {
        return "<EnumType id=\"CtlModels\">" +
                "            <EnumVal ord=\"0\">status-only</EnumVal>" +
                "            <EnumVal ord=\"1\">direct-with-normal-security</EnumVal>" +
                "            <EnumVal ord=\"2\">sbo-with-normal-security</EnumVal>" +
                "            <EnumVal ord=\"3\">direct-with-enhanced-security</EnumVal>" +
                "            <EnumVal ord=\"4\">sbo-with-enhanced-security</EnumVal>" +
                "        </EnumType>" +
                "        <EnumType id=\"OrCat\">" +
                "            <EnumVal ord=\"0\">not-supported</EnumVal>" +
                "            <EnumVal ord=\"1\">bay-control</EnumVal>" +
                "            <EnumVal ord=\"2\">station-control</EnumVal>" +
                "            <EnumVal ord=\"3\">remote-control</EnumVal>" +
                "            <EnumVal ord=\"4\">automatic-bay</EnumVal>" +
                "            <EnumVal ord=\"5\">automatic-station</EnumVal>" +
                "            <EnumVal ord=\"6\">automatic-remote</EnumVal>" +
                "            <EnumVal ord=\"7\">maintenance</EnumVal>" +
                "            <EnumVal ord=\"8\">process</EnumVal>" +
                "        </EnumType>" +
                "    </DataTypeTemplates>" +
                "</SCL>";
    }

    private static String getCidHeader() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?> " +
                "<SCL xsi:schemaLocation=\"http://www.iec.ch/61850/2003/SCL  SCL.xsd\" xmlns=\"http://www.iec.ch/61850/2003/SCL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> " +
                "    <Header id=\"ADSC\" toolID=\"XMLSpy\" nameStructure=\"SubstationName\"> " +
                "        <History> " +
                "            <Hitem version=\"1.0\" revision=\"0\" when=\"2016-07-27\" who=\"Prageeth\" what=\"Used for ADSC\"></Hitem> " +
                "        </History> " +
                "    </Header> " +
                "    <Substation name=\"Sub_a\"> " +
                "    </Substation> " +
                "     <Communication> " +
                "            <SubNetwork name=\"LAN1\" type=\"8-MMS/TCP\"> " +
                "                <BitRate unit=\"b/s\" multiplier=\"M\">100</BitRate>";
    }
}


