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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.openiec61850.internal.mms.asn1.Data;
import org.openmuc.openiec61850.internal.mms.asn1.DataSequence;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification.SubSeq_array;

/**
 * An Array can contain up to n instances of one and the same DataObject, ConstructedDataAttribute, or
 * BasicDataAttribute. The children of the array have the name that equals the index in the array (e.g. "0","1" etc.)
 * 
 * @author Stefan Feuerhahn
 *
 */
public final class Array extends FcModelNode {

	private final List<ModelNode> items;

	/**
	 * Creates an array object.
	 * 
	 * @param objectReference
	 *            the reference of the array
	 * @param fc
	 *            the functional constraint of the array
	 * @param children
	 *            the children of the array
	 */
	public Array(ObjectReference objectReference, Fc fc, List<FcModelNode> children) {
		this.objectReference = objectReference;
		this.fc = fc;
		items = new ArrayList<ModelNode>(children.size());
		for (ModelNode child : children) {
			items.add(child);
			child.setParent(this);
		}
	}

	@Override
	public Collection<ModelNode> getChildren() {
		return new ArrayList<ModelNode>(items);
	}

	@Override
	public Iterator<ModelNode> iterator() {
		return items.iterator();
	}

	@Override
	public ModelNode getChild(String childName, Fc fc) {
		return items.get(Integer.parseInt(childName));
	}

	public ModelNode getChild(int index) {
		return items.get(index);
	}

	@Override
	public ModelNode copy() {
		List<FcModelNode> itemsCopy = new ArrayList<FcModelNode>(items.size());
		for (ModelNode item : items) {
			itemsCopy.add((FcModelNode) item.copy());
		}
		return new Array(objectReference, fc, itemsCopy);
	}

	@Override
	public List<BasicDataAttribute> getBasicDataAttributes() {
		List<BasicDataAttribute> subBasicDataAttributes = new LinkedList<BasicDataAttribute>();
		for (ModelNode item : items) {
			subBasicDataAttributes.addAll(item.getBasicDataAttributes());
		}
		return subBasicDataAttributes;
	}

	public int size() {
		return items.size();
	}

	@Override
	Data getMmsDataObj() {
		ArrayList<Data> seq = new ArrayList<Data>(items.size());
		for (ModelNode modelNode : items) {
			Data mmsArrayItem = modelNode.getMmsDataObj();
			if (mmsArrayItem == null) {
				throw new IllegalArgumentException("Unable to convert Child: " + modelNode.objectReference
						+ " to MMS Data Object.");
			}
			seq.add(mmsArrayItem);
		}

		if (seq.size() == 0) {
			throw new IllegalArgumentException("Converting ModelNode: " + objectReference
					+ " to MMS Data Object resulted in Sequence of size zero.");
		}

		return new Data(new DataSequence(seq), null, null, null, null, null, null, null, null, null, null, null);

	}

	@Override
	void setValueFromMmsDataObj(Data data) throws ServiceError {
		if (data.array.seqOf == null) {
			throw new ServiceError(ServiceError.TYPE_CONFLICT, "expected type: structure");
		}
		if (data.array.seqOf.size() != items.size()) {
			throw new ServiceError(ServiceError.TYPE_CONFLICT, "expected type: structure with " + children.size()
					+ " elements");
		}

		Iterator<Data> iterator = data.array.seqOf.iterator();
		for (ModelNode child : items) {
			child.setValueFromMmsDataObj(iterator.next());
		}
	}

	@Override
	TypeSpecification getMmsTypeSpec() {
		return new TypeSpecification(
				new SubSeq_array(null, new BerInteger(items.size()), items.get(0).getMmsTypeSpec()), null, null, null,
				null, null, null, null, null, null, null, null);
	}
}
