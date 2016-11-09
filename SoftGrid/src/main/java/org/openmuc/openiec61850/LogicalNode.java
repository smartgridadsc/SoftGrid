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
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class LogicalNode extends ModelNode {

	private final Map<Fc, Map<String, FcDataObject>> fcDataObjects = new EnumMap<Fc, Map<String, FcDataObject>>(
			Fc.class);

	private final Map<String, Urcb> urcbs = new HashMap<String, Urcb>();
	private final Map<String, Brcb> brcbs = new HashMap<String, Brcb>();

	public LogicalNode(ObjectReference objectReference, List<FcDataObject> fcDataObjects) {
		children = new LinkedHashMap<String, ModelNode>();
		for (Fc fc : Fc.values()) {
			this.fcDataObjects.put(fc, new LinkedHashMap<String, FcDataObject>());
		}

		this.objectReference = objectReference;

		for (FcDataObject fcDataObject : fcDataObjects) {
			children.put(fcDataObject.getReference().getName() + fcDataObject.fc.toString(), fcDataObject);
			this.fcDataObjects.get(fcDataObject.getFc()).put(fcDataObject.getReference().getName(), fcDataObject);
			fcDataObject.setParent(this);
			if (fcDataObject.getFc() == Fc.RP) {
				addUrcb((Urcb) fcDataObject, false);
			}
			else if (fcDataObject.getFc() == Fc.BR) {
				addBrcb((Brcb) fcDataObject);
			}
		}
	}

	@Override
	public LogicalNode copy() {

		List<FcDataObject> dataObjectsCopy = new ArrayList<FcDataObject>();
		for (ModelNode obj : children.values()) {
			dataObjectsCopy.add((FcDataObject) obj.copy());
		}

		LogicalNode copy = new LogicalNode(objectReference, dataObjectsCopy);
		return copy;
	}

	public List<FcDataObject> getChildren(Fc fc) {
		Map<String, FcDataObject> requestedDataObjectsMap = fcDataObjects.get(fc);
		if (requestedDataObjectsMap == null) {
			return null;
		}

		Collection<FcDataObject> fcChildren = requestedDataObjectsMap.values();
		if (fcChildren.size() == 0) {
			return null;
		}
		else {
			return new ArrayList<FcDataObject>(fcChildren);
		}
	}

	void addUrcb(Urcb urcb, boolean addDataSet) {
		urcbs.put(urcb.getReference().getName(), urcb);
		if (addDataSet) {
			String dataSetRef = urcb.getDatSet().getStringValue();
			if (dataSetRef != null) {
				urcb.dataSet = ((ServerModel) getParent().getParent()).getDataSet(dataSetRef.replace('$', '.'));
			}
		}
	}

	public Collection<Urcb> getUrcbs() {
		return urcbs.values();
	}

	public Urcb getUrcb(String urcbName) {
		return urcbs.get(urcbName);
	}

	void addBrcb(Brcb brcb) {
		brcbs.put(brcb.getReference().getName(), brcb);
	}

	public Brcb getBrcb(String brcbName) {
		return brcbs.get(brcbName);
	}

	public Collection<Brcb> getBrcbs() {
		return brcbs.values();
	}

	@Override
	public ModelNode getChild(String childName, Fc fc) {
		if (fc != null) {
			return fcDataObjects.get(fc).get(childName);
		}
		return null;
	}
}
