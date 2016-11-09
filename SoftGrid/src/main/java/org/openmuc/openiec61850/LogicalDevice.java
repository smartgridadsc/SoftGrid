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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class LogicalDevice extends ModelNode {

	public LogicalDevice(ObjectReference objectReference, List<LogicalNode> logicalNodes) {
		children = new LinkedHashMap<String, ModelNode>((int) ((logicalNodes.size() / 0.75) + 1));
		this.objectReference = objectReference;
		for (LogicalNode logicalNode : logicalNodes) {
			children.put(logicalNode.getReference().getName(), logicalNode);
			logicalNode.setParent(this);
		}
	}

	@Override
	public LogicalDevice copy() {
		List<LogicalNode> childCopies = new ArrayList<LogicalNode>(children.size());
		for (ModelNode childNode : children.values()) {
			childCopies.add((LogicalNode) childNode.copy());
		}
		return new LogicalDevice(objectReference, childCopies);
	}

}
