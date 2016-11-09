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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class EnumType extends AbstractType {

	public List<EnumVal> values;
	public int max = 0;
	public int min = 0;

	public EnumType(Node xmlNode) throws SclParseException {
		super(xmlNode);

		NodeList elements = xmlNode.getChildNodes();

		values = new ArrayList<EnumVal>();

		for (int i = 0; i < elements.getLength(); i++) {
			Node node = elements.item(i);

			if (node.getNodeName().equals("EnumVal")) {
				EnumVal val = new EnumVal(node);
				if (val.getOrd() < min) {
					min = val.getOrd();
				}
				else if (val.getOrd() > max) {
					max = val.getOrd();
				}
				values.add(val);
			}
		}

	}

	public List<EnumVal> getValues() {
		return values;
	}

}
