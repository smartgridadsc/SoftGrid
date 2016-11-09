/*
 * Copyright 2011-14 Fraunhofer ISE, energy & meteo Systems GmbH and other contributors
 *
 * This file is part of OpenIEC61850.
 * For more information visit http://www.openmuc.org
 *
 * OpenIEC61850 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * OpenIEC61850 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIEC61850.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openmuc.openiec61850;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.openmuc.openiec61850.internal.scl.AbstractDataAttribute;
import org.openmuc.openiec61850.internal.scl.Bda;
import org.openmuc.openiec61850.internal.scl.Da;
import org.openmuc.openiec61850.internal.scl.DaType;
import org.openmuc.openiec61850.internal.scl.Do;
import org.openmuc.openiec61850.internal.scl.DoType;
import org.openmuc.openiec61850.internal.scl.EnumType;
import org.openmuc.openiec61850.internal.scl.EnumVal;
import org.openmuc.openiec61850.internal.scl.LnSubDef;
import org.openmuc.openiec61850.internal.scl.LnType;
import org.openmuc.openiec61850.internal.scl.Sdo;
import org.openmuc.openiec61850.internal.scl.TypeDefinitions;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class SclParser {

	private TypeDefinitions typeDefinitions;
	private final Map<String, DataSet> dataSetsMap = new HashMap<String, DataSet>();

	private Document doc;
	private String iedName;
	private List<ServerSap> serverSaps = null;
	private boolean useResvTmsAttributes = false;

	private final List<LnSubDef> dataSetDefs = new ArrayList<LnSubDef>();

	public List<ServerSap> getServerSaps() {
		return serverSaps;
	}

	public void parse(String icdFile) throws SclParseException {
		try {
			parse(new FileInputStream(icdFile));
		} catch (FileNotFoundException e) {
			throw new SclParseException(e);
		}
	}

	public void parse(InputStream icdFileStream) throws SclParseException {

		typeDefinitions = new TypeDefinitions();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);

		try {
			doc = factory.newDocumentBuilder().parse(icdFileStream);
		} catch (Exception e) {
			throw new SclParseException(e);
		}

		Node rootNode = doc.getDocumentElement();

		if (!rootNode.getNodeName().equals("SCL")) {
			throw new SclParseException("Root node in SCL file is not of type \"SCL\"");
		}

		readTypeDefinitions();

		NodeList iedList = doc.getElementsByTagName("IED");
		if (iedList.getLength() == 0) {
			throw new SclParseException("No IED section found!");
		}

		Node nameAttribute = iedList.item(0).getAttributes().getNamedItem("name");

		iedName = nameAttribute.getNodeValue();
		if ((iedName == null) || (iedName.length() == 0)) {
			throw new SclParseException("IED must have a name!");
		}

		NodeList iedElements = iedList.item(0).getChildNodes();

		serverSaps = new ArrayList<ServerSap>(iedElements.getLength());
		for (int i = 0; i < iedElements.getLength(); i++) {
			Node element = iedElements.item(i);
			String nodeName = element.getNodeName();
			if (nodeName.equals("AccessPoint")) {
				serverSaps.add(createAccessPoint(element));
			}
			else if (nodeName.equals("Services")) {
				NodeList servicesElements = element.getChildNodes();
				for (int j = 0; j < servicesElements.getLength(); j++) {
					if (servicesElements.item(i).getNodeName().equals("ReportSettings")) {
						Node resvTmsAttribute = servicesElements.item(i).getAttributes().getNamedItem("resvTms");
						if (resvTmsAttribute != null) {
							useResvTmsAttributes = resvTmsAttribute.getNodeValue().equalsIgnoreCase("true");
						}
					}
				}
			}
		}

	}

	private void readTypeDefinitions() throws SclParseException {

		NodeList dttSections = doc.getElementsByTagName("DataTypeTemplates");

		if (dttSections.getLength() != 1) {
			throw new SclParseException("Only one DataTypeSection allowed");
		}

		Node dtt = dttSections.item(0);

		NodeList dataTypes = dtt.getChildNodes();

		for (int i = 0; i < dataTypes.getLength(); i++) {
			Node element = dataTypes.item(i);

			String nodeName = element.getNodeName();

			if (nodeName.equals("LNodeType")) {
				typeDefinitions.putLNodeType(new LnType(element));
			}
			else if (nodeName.equals("DOType")) {
				typeDefinitions.putDOType(new DoType(element));
			}
			else if (nodeName.equals("DAType")) {
				typeDefinitions.putDAType(new DaType(element));
			}
			else if (nodeName.equals("EnumType")) {
				typeDefinitions.putEnumType(new EnumType(element));
			}
		}
	}

	private ServerSap createAccessPoint(Node iedServer) throws SclParseException {
		ServerSap serverSap = null;

		NodeList elements = iedServer.getChildNodes();

		for (int i = 0; i < elements.getLength(); i++) {
			Node element = elements.item(i);

			if (element.getNodeName().equals("Server")) {

				ServerModel server = createServerModel(element);

				Node namedItem = iedServer.getAttributes().getNamedItem("name");
				if (namedItem == null) {
					throw new SclParseException("AccessPoint has no name attribute!");
				}
				String name = namedItem.getNodeValue();
				serverSap = new ServerSap(102, 0, null, server, name, null);

				break;
			}
		}

		if (serverSap == null) {
			throw new SclParseException("AccessPoint has no server!");
		}

		return serverSap;
	}

	private ServerModel createServerModel(Node serverXMLNode) throws SclParseException {

		NodeList elements = serverXMLNode.getChildNodes();
		List<LogicalDevice> logicalDevices = new ArrayList<LogicalDevice>(elements.getLength());

		for (int i = 0; i < elements.getLength(); i++) {
			Node element = elements.item(i);

			if (element.getNodeName().equals("LDevice")) {
				logicalDevices.add(createNewLDevice(element));
			}
		}

		ServerModel serverModel = new ServerModel(logicalDevices, null);

		for (LnSubDef dataSetDef : dataSetDefs) {
			DataSet dataSet = createDataSet(serverModel, dataSetDef.logicalNode, dataSetDef.defXmlNode);
			dataSetsMap.put(dataSet.getReferenceStr(), dataSet);
		}

		serverModel.addDataSets(dataSetsMap.values());

		return serverModel;

	}

	private LogicalDevice createNewLDevice(Node ldXmlNode) throws SclParseException {

		String inst = null;
		String ldName = null;

		NamedNodeMap attributes = ldXmlNode.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {
			Node node = attributes.item(i);
			String nodeName = node.getNodeName();

			if (nodeName.equals("inst")) {
				inst = node.getNodeValue();
			}
			else if (nodeName.equals("ldName")) {
				ldName = node.getNodeValue();
			}
		}

		if (inst == null) {
			throw new SclParseException("Required attribute \"inst\" not found!");
		}

		NodeList elements = ldXmlNode.getChildNodes();
		List<LogicalNode> logicalNodes = new ArrayList<LogicalNode>();

		String ref;
		if ((ldName != null) && (ldName.length() != 0)) {
			ref = ldName;
		}
		else {
			ref = iedName + inst;
		}

		for (int i = 0; i < elements.getLength(); i++) {
			Node element = elements.item(i);

			if (element.getNodeName().equals("LN") || element.getNodeName().equals("LN0")) {
				logicalNodes.add(createNewLogicalNode(element, ref));
			}
		}

		LogicalDevice lDevice = new LogicalDevice(new ObjectReference(ref), logicalNodes);

		return lDevice;
	}

	private LogicalNode createNewLogicalNode(Node lnXmlNode, String parentRef) throws SclParseException {

		// attributes not needed: desc

		String inst = null;
		String lnClass = null;
		String lnType = null;
		String prefix = "";

		NamedNodeMap attributes = lnXmlNode.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {
			Node node = attributes.item(i);
			String nodeName = node.getNodeName();

			if (nodeName.equals("inst")) {
				inst = node.getNodeValue();
			}
			else if (nodeName.equals("lnType")) {
				lnType = node.getNodeValue();
			}
			else if (nodeName.equals("lnClass")) {
				lnClass = node.getNodeValue();
			}
			else if (nodeName.equals("prefix")) {
				prefix = node.getNodeValue();
			}
		}

		if (inst == null) {
			throw new SclParseException("Required attribute \"inst\" not found!");
		}
		if (lnType == null) {
			throw new SclParseException("Required attribute \"lnType\" not found!");
		}
		if (lnClass == null) {
			throw new SclParseException("Required attribute \"lnClass\" not found!");
		}

		String ref = parentRef + '/' + prefix + lnClass + inst;

		LnType lnTypeDef = typeDefinitions.getLNodeType(lnType);

		List<FcDataObject> dataObjects = new ArrayList<FcDataObject>();

		if (lnTypeDef == null) {
			throw new SclParseException("LNType " + lnType + " not defined!");
		}
		for (Do dobject : lnTypeDef.dos) {

			// look for DOI node with the name of the DO
			Node doiNodeFound = null;
			for (int i = 0; i < lnXmlNode.getChildNodes().getLength(); i++) {
				Node childNode = lnXmlNode.getChildNodes().item(i);
				if ("DOI".equals(childNode.getNodeName())) {

					NamedNodeMap doiAttributes = childNode.getAttributes();
					Node nameAttribute = doiAttributes.getNamedItem("name");
					if (nameAttribute != null && nameAttribute.getNodeValue().equals(dobject.getName())) {
						doiNodeFound = childNode;
					}
				}
			}

			dataObjects.addAll(createFcDataObjects(dobject.getName(), ref, dobject.getType(), doiNodeFound));

		}

		// TODO inserted RCBs is this correct?

		// look for ReportControl
		for (int i = 0; i < lnXmlNode.getChildNodes().getLength(); i++) {
			Node childNode = lnXmlNode.getChildNodes().item(i);
			if ("ReportControl".equals(childNode.getNodeName())) {
				dataObjects.add(createReportControl(childNode, ref));
			}
		}

		LogicalNode lNode = new LogicalNode(new ObjectReference(ref), dataObjects);

		// look for DataSet definitions
		for (int i = 0; i < lnXmlNode.getChildNodes().getLength(); i++) {
			Node childNode = lnXmlNode.getChildNodes().item(i);
			if ("DataSet".equals(childNode.getNodeName())) {
				dataSetDefs.add(new LnSubDef(childNode, lNode));
			}
		}
		return lNode;
	}

	private DataSet createDataSet(ServerModel serverModel, LogicalNode lNode, Node dsXmlNode) throws SclParseException {

		Node nameAttribute = dsXmlNode.getAttributes().getNamedItem("name");
		if (nameAttribute == null) {
			throw new SclParseException("DataSet must have a name");
		}

		String name = nameAttribute.getNodeValue();

		List<FcModelNode> dsMembers = new ArrayList<FcModelNode>();

		for (int i = 0; i < dsXmlNode.getChildNodes().getLength(); i++) {
			Node fcdaXmlNode = dsXmlNode.getChildNodes().item(i);
			if ("FCDA".equals(fcdaXmlNode.getNodeName())) {

				// For the definition of FCDA see Table 22 part6 ed2

				String ldInst = null;
				String prefix = "";
				String lnClass = null;
				String lnInst = "";
				String doName = "";
				String daName = "";
				Fc fc = null;

				NamedNodeMap attributes = fcdaXmlNode.getAttributes();

				for (int j = 0; j < attributes.getLength(); j++) {
					Node node = attributes.item(j);
					String nodeName = node.getNodeName();

					if (nodeName.equals("ldInst")) {
						ldInst = node.getNodeValue();
					}
					else if (nodeName.equals("lnInst")) {
						lnInst = node.getNodeValue();
					}
					else if (nodeName.equals("lnClass")) {
						lnClass = node.getNodeValue();
					}
					else if (nodeName.equals("prefix")) {
						prefix = node.getNodeValue();
					}
					else if (nodeName.equals("doName")) {
						doName = node.getNodeValue();
					}
					else if (nodeName.equals("daName")) {
						if (!node.getNodeValue().isEmpty()) {
							daName = "." + node.getNodeValue();
						}
					}
					else if (nodeName.equals("fc")) {
						fc = Fc.fromString(node.getNodeValue());
						if (fc == null) {
							throw new SclParseException("FCDA contains invalid FC: " + node.getNodeValue());
						}
					}

				}

				if (ldInst == null) {
					throw new SclParseException("Required attribute \"ldInst\" not found in FCDA: " + nameAttribute
							+ "!");
				}

				if (lnClass == null) {
					throw new SclParseException("Required attribute \"lnClass\" not found in FCDA!");
				}
				if (fc == null) {
					throw new SclParseException("Required attribute \"fc\" not found in FCDA!");
				}
				if (doName != "") {

					String objectReference = iedName + ldInst + "/" + prefix + lnClass + lnInst + "." + doName + daName;

					ModelNode fcdaNode = serverModel.findModelNode(objectReference, fc);

					if (fcdaNode == null) {
						throw new SclParseException("Specified FCDA: " + objectReference + " in DataSet: "
								+ nameAttribute + " not found in Model.");
					}
					dsMembers.add((FcModelNode) fcdaNode);
				}
				else {
					String objectReference = iedName + ldInst + "/" + prefix + lnClass + lnInst;
					ModelNode logicalNode = serverModel.findModelNode(objectReference, null);
					if (logicalNode == null) {
						throw new SclParseException("Specified FCDA: " + objectReference + " in DataSet: "
								+ nameAttribute + " not found in Model.");
					}
					List<FcDataObject> fcDataObjects = ((LogicalNode) logicalNode).getChildren(fc);
					for (FcDataObject dataObj : fcDataObjects) {
						dsMembers.add(dataObj);
					}

				}

			}

		}

		DataSet dataSet = new DataSet(lNode.getReference().toString() + '.' + name, dsMembers, false);
		return dataSet;
	}

	private Rcb createReportControl(Node xmlNode, String parentRef) throws SclParseException {

		Fc fc = Fc.RP;
		NamedNodeMap rcbNodeAttributes = xmlNode.getAttributes();
		Node attribute = rcbNodeAttributes.getNamedItem("buffered");
		if (attribute != null && attribute.getNodeValue().equalsIgnoreCase("true")) {
			fc = Fc.BR;
		}

		Node nameAttribute = rcbNodeAttributes.getNamedItem("name");
		if (nameAttribute == null) {
			throw new SclParseException("Report Control Block has no name attribute.");
		}

		ObjectReference reportObjRef = new ObjectReference(parentRef + "." + nameAttribute.getNodeValue());

		BdaTriggerConditions trigOps = new BdaTriggerConditions(new ObjectReference(reportObjRef + ".TrgOps"));
		BdaOptFlds optFields = new BdaOptFlds(new ObjectReference(reportObjRef + ".OptFlds"));
		for (int i = 0; i < xmlNode.getChildNodes().getLength(); i++) {
			Node childNode = xmlNode.getChildNodes().item(i);
			if (childNode.getNodeName().equals("TrgOps")) {

				NamedNodeMap attributes = childNode.getAttributes();

				if (attributes != null) {
					for (int j = 0; j < attributes.getLength(); j++) {
						Node node = attributes.item(j);
						String nodeName = node.getNodeName();

						if (nodeName.equals("dchg")) {
							trigOps.setDataChange(node.getNodeValue().equalsIgnoreCase("true"));
						}
						else if (nodeName.equals("qchg")) {
							trigOps.setQualityChange(node.getNodeValue().equalsIgnoreCase("true"));

						}
						else if (nodeName.equals("dupd")) {
							trigOps.setDataUpdate(node.getNodeValue().equalsIgnoreCase("true"));

						}
						else if (nodeName.equals("period")) {
							trigOps.setIntegrity(node.getNodeValue().equalsIgnoreCase("true"));

						}
						else if (nodeName.equals("gi")) {
							trigOps.setGeneralInterrogation(node.getNodeValue().equalsIgnoreCase("true"));
						}
					}
				}
			}
			else if (childNode.getNodeName().equals("OptFields")) {

				NamedNodeMap attributes = childNode.getAttributes();

				if (attributes != null) {
					for (int j = 0; j < attributes.getLength(); j++) {

						Node node = attributes.item(j);
						String nodeName = node.getNodeName();

						if (nodeName.equals("seqNum")) {
							optFields.setSequenceNumber(node.getNodeValue().equalsIgnoreCase("true"));
						}
						else if (nodeName.equals("timeStamp")) {
							optFields.setReportTimestamp(node.getNodeValue().equalsIgnoreCase("true"));

						}
						else if (nodeName.equals("reasonCode")) {
							optFields.setReasonForInclusion(node.getNodeValue().equalsIgnoreCase("true"));

						}
						else if (nodeName.equals("dataSet")) {
							optFields.setDataSetName(node.getNodeValue().equalsIgnoreCase("true"));

						}
						// not supported for now
						// else if (nodeName.equals("dataRef")) {
						// optFields.setDataReference(node.getNodeValue().equals("true"));
						//
						// }
						else if (nodeName.equals("bufOvfl")) {
							optFields.setBufferOverflow(node.getNodeValue().equalsIgnoreCase("true"));

						}
						else if (nodeName.equals("entryID")) {
							optFields.setEntryId(node.getNodeValue().equalsIgnoreCase("true"));
						}
						// not supported for now:
						// else if (nodeName.equals("configRef")) {
						// optFields.setConfigRevision(node.getNodeValue().equals("true"));
						// }
					}
				}
			}
			// TODO is this setting needed?
			// else if (childNode.getNodeName().equals("RptEnabled")) {
			// Node rptEnabledMaxAttr = xmlNode.getAttributes().getNamedItem("max");
			// if (rptEnabledMaxAttr != null) {
			// Integer.parseInt(rptEnabledMaxAttr.getNodeValue());
			// }
			// }
		}

		if (fc == Fc.RP) {
			optFields.setEntryId(false);
			optFields.setBufferOverflow(false);
		}

		List<FcModelNode> children = new ArrayList<FcModelNode>();

		BdaVisibleString rptId = new BdaVisibleString(new ObjectReference(reportObjRef.toString() + ".RptID"), fc, "",
				129, false, false);
		attribute = rcbNodeAttributes.getNamedItem("rptID");
		if (attribute != null) {
			rptId.setValue(attribute.getNodeValue().getBytes());
		}
		else {
			rptId.setValue(reportObjRef.toString());
		}

		children.add(rptId);

		children.add(new BdaBoolean(new ObjectReference(reportObjRef.toString() + ".RptEna"), fc, "", false, false));

		if (fc == Fc.RP) {
			children.add(new BdaBoolean(new ObjectReference(reportObjRef.toString() + ".Resv"), fc, "", false, false));
		}

		BdaVisibleString datSet = new BdaVisibleString(new ObjectReference(reportObjRef.toString() + ".DatSet"), fc,
				"", 129, false, false);

		attribute = xmlNode.getAttributes().getNamedItem("datSet");
		if (attribute != null) {
			String nodeValue = attribute.getNodeValue();
			String dataSetName = parentRef + "$" + nodeValue;
			datSet.setValue(dataSetName.getBytes());
		}
		children.add(datSet);

		BdaInt32U confRef = new BdaInt32U(new ObjectReference(reportObjRef.toString() + ".ConfRev"), fc, "", false,
				false);
		attribute = xmlNode.getAttributes().getNamedItem("confRev");
		if (attribute == null) {
			throw new SclParseException("Report Control Block does not contain mandatory attribute confRev");
		}
		confRef.setValue(Long.parseLong(attribute.getNodeValue()));
		children.add(confRef);

		children.add(optFields);

		BdaInt32U bufTm = new BdaInt32U(new ObjectReference(reportObjRef.toString() + ".BufTm"), fc, "", false, false);
		attribute = xmlNode.getAttributes().getNamedItem("bufTime");
		if (attribute != null) {
			bufTm.setValue(Long.parseLong(attribute.getNodeValue()));
		}
		children.add(bufTm);

		children.add(new BdaInt8U(new ObjectReference(reportObjRef.toString() + ".SqNum"), fc, "", false, false));

		children.add(trigOps);

		BdaInt32U intgPd = new BdaInt32U(new ObjectReference(reportObjRef.toString() + ".IntgPd"), fc, "", false, false);
		attribute = xmlNode.getAttributes().getNamedItem("intgPd");
		if (attribute != null) {
			intgPd.setValue(Long.parseLong(attribute.getNodeValue()));
		}
		children.add(intgPd);

		children.add(new BdaBoolean(new ObjectReference(reportObjRef.toString() + ".GI"), fc, "", false, false));

		Rcb rcb = null;

		if (fc == Fc.BR) {

			children.add(new BdaBoolean(new ObjectReference(reportObjRef.toString() + ".PurgeBuf"), fc, "", false,
					false));

			children.add(new BdaOctetString(new ObjectReference(reportObjRef.toString() + ".EntryID"), fc, "", 8,
					false, false));

			children.add(new BdaEntryTime(new ObjectReference(reportObjRef.toString() + ".TimeOfEntry"), fc, "", false,
					false));

			if (useResvTmsAttributes) {
				children.add(new BdaInt16(new ObjectReference(reportObjRef.toString() + ".ResvTms"), fc, "", false,
						false));
			}

			rcb = new Brcb(reportObjRef, children);

		}
		else {

			rcb = new Urcb(reportObjRef, children);

		}
		// ignoring owner because it cannot be specified in SCL file
		return rcb;

	}

	// private LogControlBlock createLogControlBlock(LogicalNode lnode,
	// Node xmlNode) throws SclParseException {
	// // LogControlBlock lcb = new LogControlBlock();
	// // String name = getAttribute(xmlNode, "name",
	// // "LogControl must have a name");
	// // lcb.setName(name);
	// // lcb.setLogRef(new ObjectReference(lnode.getReference() + "." +
	// // name));
	// // String nodeValue = getAttribute(xmlNode, "datSet",
	// // "LogControl must have attribute datSet");
	// // DataSet dataSet = lnode.getDataSet(nodeValue);
	// // if (dataSet == null) {
	// // throw new SclParseException("DataSet " + nodeValue
	// // + " does not exist for LogControl");
	// // }
	// // lcb.setDataSet(dataSet);
	// // lcb.setIntgPd(getLongAttribute(xmlNode, "intgPd", null));
	// // lcb.setLogEna(getBooleanAttribute(xmlNode, "logEna"));
	// // for (int i = 0; i < xmlNode.getChildNodes().getLength(); i++) {
	// // Node childNode = xmlNode.getChildNodes().item(i);
	// // if ("TrgOps".equals(childNode.getNodeName())) {
	// // lcb.setTrgOps(createTriggerConditions(childNode));
	// // }
	// // }
	// return null;
	// }

	private List<FcDataObject> createFcDataObjects(String name, String parentRef, String doTypeID, Node doiNode)
			throws SclParseException {

		DoType doType = typeDefinitions.getDOType(doTypeID);

		if (doType == null) {
			throw new SclParseException("DO type " + doTypeID + " not defined!");
		}

		String ref = parentRef + '.' + name;

		List<ModelNode> childNodes = new ArrayList<ModelNode>();

		for (Da dattr : doType.das) {

			// look for DAI node with the name of the DA
			Node iNodeFound = findINode(doiNode, dattr.getName());

			if (dattr.getCount() >= 1) {
				childNodes.add(createArrayOfDataAttributes(ref + '.' + dattr.getName(), dattr, iNodeFound));
			}
			else {
				childNodes.add(createDataAttribute(ref + '.' + dattr.getName(), dattr.getFc(), dattr, iNodeFound,
						false, false, false));
			}

		}

		for (Sdo sdo : doType.sdos) {

			// parsing Arrays of SubDataObjects is ignored for now because no SCL file was found to test against. The
			// only DO that contains an Array of SDOs is Harmonic Value (HMV). The Kalkitech SCL Manager handles the
			// array of SDOs in HMV as an array of DAs.

			Node iNodeFound = findINode(doiNode, sdo.getName());

			childNodes.addAll(createFcDataObjects(sdo.getName(), ref, sdo.getType(), iNodeFound));

		}

		Map<Fc, List<FcModelNode>> subFCDataMap = new LinkedHashMap<Fc, List<FcModelNode>>();

		for (Fc fc : Fc.values()) {
			subFCDataMap.put(fc, new LinkedList<FcModelNode>());
		}

		for (ModelNode childNode : childNodes) {
			subFCDataMap.get(((FcModelNode) childNode).getFc()).add((FcModelNode) childNode);
		}

		List<FcDataObject> fcDataObjects = new LinkedList<FcDataObject>();
		ObjectReference objectReference = new ObjectReference(ref);

		for (Fc fc : Fc.values()) {
			if (subFCDataMap.get(fc).size() > 0) {
				fcDataObjects.add(new FcDataObject(objectReference, fc, subFCDataMap.get(fc)));
			}
		}

		return fcDataObjects;
	}

	private Node findINode(Node iNode, String dattrName) {

		if (iNode == null) {
			return null;
		}

		for (int i = 0; i < iNode.getChildNodes().getLength(); i++) {
			Node childNode = iNode.getChildNodes().item(i);
			if (childNode.getAttributes() != null) {
				Node nameAttribute = childNode.getAttributes().getNamedItem("name");
				if (nameAttribute != null && nameAttribute.getNodeValue().equals(dattrName)) {
					return childNode;
				}
			}
		}
		return null;
	}

	private Array createArrayOfDataAttributes(String ref, Da dataAttribute, Node iXmlNode) throws SclParseException {

		Fc fc = dataAttribute.getFc();
		int size = dataAttribute.getCount();

		List<FcModelNode> arrayItems = new ArrayList<FcModelNode>();
		for (int i = 0; i < size; i++) {
			// TODO go down the iXmlNode using the ix attribute?
			arrayItems.add(createDataAttribute(ref + '(' + i + ')', fc, dataAttribute, iXmlNode,
					dataAttribute.isDchg(), dataAttribute.isDupd(), dataAttribute.isQchg()));
		}

		return new Array(new ObjectReference(ref), fc, arrayItems);
	}

	/**
	 * returns a ConstructedDataAttribute or BasicDataAttribute
	 */
	private FcModelNode createDataAttribute(String ref, Fc fc, AbstractDataAttribute dattr, Node iXmlNode,
			boolean dchg, boolean dupd, boolean qchg) throws SclParseException {

		if (dattr instanceof Da) {
			Da dataAttribute = (Da) dattr;
			dchg = dataAttribute.isDchg();
			dupd = dataAttribute.isDupd();
			qchg = dataAttribute.isQchg();
		}

		String bType = dattr.getbType();

		if (!bType.equals("Struct")) {

			String val = null;
			String sAddr = null;
			if (iXmlNode != null) {
				NamedNodeMap attributeMap = iXmlNode.getAttributes();
				Node sAddrAttribute = attributeMap.getNamedItem("sAddr");
				if (sAddrAttribute != null) {
					sAddr = sAddrAttribute.getNodeValue();
				}

				NodeList elements = iXmlNode.getChildNodes();
				for (int i = 0; i < elements.getLength(); i++) {
					Node node = elements.item(i);
					if (node.getNodeName().equals("Val")) {
						val = node.getTextContent();
					}
				}
			}

			// BasicDataAttribute bda;

			if (bType.equals("BOOLEAN")) {
				BdaBoolean bda = new BdaBoolean(new ObjectReference(ref), fc, sAddr, dchg, dupd);
				if (val != null) {
					if (val.equalsIgnoreCase("true") || val.equals("1")) {
						bda.setValue(new Boolean(true));
					}
					else if (val.equalsIgnoreCase("false") || val.equals("0")) {
						bda.setValue(new Boolean(false));
					}
					else {
						throw new SclParseException("invalid boolean configured value: " + val);
					}
				}
				return bda;
			}
			else if (bType.equals("INT8")) {
				BdaInt8 bda = new BdaInt8(new ObjectReference(ref), fc, sAddr, dchg, dupd);
				if (val != null) {
					try {
						bda.setValue(Byte.parseByte(val));
					} catch (NumberFormatException e) {
						throw new SclParseException("invalid INT8 configured value: " + val);
					}
				}
				return bda;
			}
			else if (bType.equals("INT16")) {
				BdaInt16 bda = new BdaInt16(new ObjectReference(ref), fc, sAddr, dchg, dupd);
				if (val != null) {
					try {
						bda.setValue(Short.parseShort(val));
					} catch (NumberFormatException e) {
						throw new SclParseException("invalid INT16 configured value: " + val);
					}
				}
				return bda;
			}
			else if (bType.equals("INT32")) {
				BdaInt32 bda = new BdaInt32(new ObjectReference(ref), fc, sAddr, dchg, dupd);
				if (val != null) {
					try {
						bda.setValue(Integer.parseInt(val));
					} catch (NumberFormatException e) {
						throw new SclParseException("invalid INT32 configured value: " + val);
					}
				}
				return bda;
			}
			else if (bType.equals("INT64")) {
				BdaInt64 bda = new BdaInt64(new ObjectReference(ref), fc, sAddr, dchg, dupd);
				if (val != null) {
					try {
						bda.setValue(Long.parseLong(val));
					} catch (NumberFormatException e) {
						throw new SclParseException("invalid INT64 configured value: " + val);
					}
				}
				return bda;
			}
			else if (bType.equals("INT8U")) {
				BdaInt8U bda = new BdaInt8U(new ObjectReference(ref), fc, sAddr, dchg, dupd);
				if (val != null) {
					try {
						bda.setValue(Short.parseShort(val));
					} catch (NumberFormatException e) {
						throw new SclParseException("invalid INT8U configured value: " + val);
					}
				}
				return bda;
			}
			else if (bType.equals("INT16U")) {
				BdaInt16U bda = new BdaInt16U(new ObjectReference(ref), fc, sAddr, dchg, dupd);
				if (val != null) {
					try {
						bda.setValue(Integer.parseInt(val));
					} catch (NumberFormatException e) {
						throw new SclParseException("invalid INT16U configured value: " + val);
					}
				}
				return bda;
			}
			else if (bType.equals("INT32U")) {
				BdaInt32U bda = new BdaInt32U(new ObjectReference(ref), fc, sAddr, dchg, dupd);
				if (val != null) {
					try {
						bda.setValue(Long.parseLong(val));
					} catch (NumberFormatException e) {
						throw new SclParseException("invalid INT32U configured value: " + val);
					}
				}
				return bda;
			}
			else if (bType.equals("FLOAT32")) {
				BdaFloat32 bda = new BdaFloat32(new ObjectReference(ref), fc, sAddr, dchg, dupd);
				if (val != null) {
					try {
						bda.setFloat(Float.parseFloat(val));
					} catch (NumberFormatException e) {
						throw new SclParseException("invalid FLOAT32 configured value: " + val);
					}
				}
				return bda;
			}
			else if (bType.equals("FLOAT64")) {
				BdaFloat64 bda = new BdaFloat64(new ObjectReference(ref), fc, sAddr, dchg, dupd);
				if (val != null) {
					try {
						bda.setDouble(Double.parseDouble(val));
					} catch (NumberFormatException e) {
						throw new SclParseException("invalid FLOAT64 configured value: " + val);
					}
				}
				return bda;
			}
			else if (bType.startsWith("VisString")) {
				BdaVisibleString bda = new BdaVisibleString(new ObjectReference(ref), fc, sAddr, Integer.parseInt(dattr
						.getbType().substring(9)), dchg, dupd);
				if (val != null) {
					bda.setValue(val.getBytes());
				}
				return bda;
			}
			else if (bType.startsWith("Unicode")) {
				BdaUnicodeString bda = new BdaUnicodeString(new ObjectReference(ref), fc, sAddr, Integer.parseInt(dattr
						.getbType().substring(7)), dchg, dupd);
				if (val != null) {
					bda.setValue(val.getBytes());
				}
				return bda;
			}
			else if (bType.startsWith("Octet")) {
				BdaOctetString bda = new BdaOctetString(new ObjectReference(ref), fc, sAddr, Integer.parseInt(dattr
						.getbType().substring(5)), dchg, dupd);
				if (val != null) {
					// TODO
					throw new SclParseException("parsing configured value for octet string is not supported yet.");
				}
				return bda;
			}
			else if (bType.equals("Quality")) {
				return new BdaQuality(new ObjectReference(ref), fc, sAddr, qchg);
			}
			else if (bType.equals("Check")) {
				return new BdaCheck(new ObjectReference(ref));
			}
			else if (bType.equals("Dbpos")) {
				return new BdaDoubleBitPos(new ObjectReference(ref), fc, sAddr, dchg, dupd);
			}
			else if (bType.equals("Tcmd")) {
				return new BdaTapCommand(new ObjectReference(ref), fc, sAddr, dchg, dupd);
			}
			else if (bType.equals("Timestamp")) {
				BdaTimestamp bda = new BdaTimestamp(new ObjectReference(ref), fc, sAddr, dchg, dupd);
				if (val != null) {
					// TODO
					throw new SclParseException("parsing configured value for TIMESTAMP is not supported yet.");
				}
				return bda;
			}
			else if (bType.equals("Enum")) {
				String type = dattr.getType();
				if (type == null) {
					throw new SclParseException("The exact type of the enumeration is not set.");
				}
				EnumType enumType = typeDefinitions.getEnumType(type);

				if (enumType == null) {
					throw new SclParseException("Definition of enum type: " + type + " not found.");
				}

				if (enumType.max > 127 || enumType.min < -128) {
					BdaInt16 bda = new BdaInt16(new ObjectReference(ref), fc, sAddr, dchg, dupd);
					if (val != null) {
						for (EnumVal enumVal : enumType.getValues()) {
							if (val.equals(enumVal.getId())) {
								bda.setValue(new Short((short) enumVal.getOrd()));
								return bda;
							}
						}
						throw new SclParseException("unknown enum value: " + val);
					}
					return bda;
				}
				else {
					BdaInt8 bda = new BdaInt8(new ObjectReference(ref), fc, sAddr, dchg, dupd);
					if (val != null) {
						for (EnumVal enumVal : enumType.getValues()) {
							if (val.equals(enumVal.getId())) {
								bda.setValue(new Byte((byte) enumVal.getOrd()));
								return bda;
							}
						}
						throw new SclParseException("unknown enum value: " + val);
					}
					return bda;
				}
			}
			else {
				throw new SclParseException("Invalid bType: " + bType);
			}

		}
		else {
			DaType datype = typeDefinitions.getDaType(dattr.getType());

			if (datype == null) {
				throw new SclParseException("DAType " + dattr.getbType() + " not declared!");
			}

			List<FcModelNode> subDataAttributes = new ArrayList<FcModelNode>();
			for (Bda bda : datype.bdas) {

				Node iNodeFound = findINode(iXmlNode, dattr.getName());

				subDataAttributes.add(createDataAttribute(ref + '.' + bda.getName(), fc, bda, iNodeFound, dchg, dupd,
						qchg));
			}
			return new ConstructedDataAttribute(new ObjectReference(ref), fc, subDataAttributes);
		}
	}
}
