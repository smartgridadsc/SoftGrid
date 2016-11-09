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
package org.openmuc.openiec61850.internal.scl;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.openiec61850.SclParseException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractDataAttribute extends AbstractElement {

	// attributes not needed: valKind

	private String sAddr = null; /* optional - short address */
	private String bType = null; /* mandatory - basic type */

	private String type = null; /* conditional - if bType = "Enum" or "Struct" */
	private int count = 0; /* optional - number of array elements */
	private List<Value> values = null;

	public AbstractDataAttribute(Node xmlNode) throws SclParseException {
		super(xmlNode);

		NamedNodeMap attributes = xmlNode.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {
			Node node = attributes.item(i);
			String nodeName = node.getNodeName();

			if (nodeName.equals("type")) {
				type = node.getNodeValue();
			}
			else if (nodeName.equals("sAddr")) {
				sAddr = node.getNodeValue();
			}
			else if (nodeName.equals("bType")) {
				bType = node.getNodeValue();
			}
			else if (nodeName.equals("count")) {
				count = Integer.parseInt(node.getNodeValue());
			}
		}

		if (bType == null) {
			throw new SclParseException("Required attribute \"bType\" not found!");
		}

		/* Parse Val elements */
		NodeList elements = xmlNode.getChildNodes();

		values = new ArrayList<Value>();

		for (int i = 0; i < elements.getLength(); i++) {
			Node node = elements.item(i);

			if (node.getNodeName().equals("Val")) {
				// TODO
				throw new SclParseException("AbstractDataAttribute(): Val not implemented!");
				// this.sdos.add(new SDO(node));
			}
		}

	}

	public String getsAddr() {
		return sAddr;
	}

	public String getbType() {
		return bType;
	}

	public String getType() {
		return type;
	}

	public int getCount() {
		return count;
	}

	public List<Value> getValues() {
		return values;
	}

}
