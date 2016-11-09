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

import org.openmuc.openiec61850.SclParseException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class AbstractElement {

	private String name = null;
	private String desc = null;

	public AbstractElement(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

	public AbstractElement(Node xmlNode) throws SclParseException {
		NamedNodeMap attributes = xmlNode.getAttributes();

		Node node = attributes.getNamedItem("name");
		if (node == null) {
			throw new SclParseException("Required attribute \"name\" not found!");
		}
		name = node.getNodeValue();

		node = attributes.getNamedItem("desc");
		if (node != null) {
			desc = node.getNodeValue();
		}

	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}
}
