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

import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.SclParseException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class Da extends AbstractDataAttribute {

	private Fc fc = null;
	private boolean dchg = false;
	private boolean qchg = false;
	private boolean dupd = false;

	public Da(Node xmlNode) throws SclParseException {

		super(xmlNode);

		NamedNodeMap attributes = xmlNode.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {
			Node node = attributes.item(i);
			String nodeName = node.getNodeName();

			if (nodeName.equals("fc")) {
				fc = Fc.fromString(node.getNodeValue());
				if (fc == null) {
					throw new SclParseException("Invalid Functional Constraint");
				}
			}
			else if (nodeName.equals("dchg")) {
				dchg = "true".equals(node.getNodeValue());
			}
			else if (nodeName.equals("qchg")) {
				qchg = "true".equals(node.getNodeValue());
			}
			else if (nodeName.equals("dupd")) {
				dupd = "true".equals(node.getNodeValue());
			}
		}

		if (fc == null) {
			throw new SclParseException("Required attribute \"fc\" not found!");
		}

	}

	public Fc getFc() {
		return fc;
	}

	public boolean isDchg() {
		return dchg;
	}

	public boolean isQchg() {
		return qchg;
	}

	public boolean isDupd() {
		return dupd;
	}

}
