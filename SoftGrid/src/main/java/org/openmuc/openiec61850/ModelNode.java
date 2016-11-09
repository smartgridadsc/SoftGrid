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
import java.util.Map;

import org.openmuc.jasn1.ber.types.string.BerVisibleString;
import org.openmuc.openiec61850.internal.mms.asn1.Data;
import org.openmuc.openiec61850.internal.mms.asn1.StructComponent;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification.SubSeq_structure;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification.SubSeq_structure.SubSeqOf_components;

public abstract class ModelNode implements Iterable<ModelNode> {

	protected ObjectReference objectReference;
	protected Map<String, ModelNode> children;
	ModelNode parent;

	/**
	 * Returns a copy of model node with all of its children. Creates new BasicDataAttribute values but reuses
	 * ObjectReferences, FunctionalConstraints.
	 *
	 * @return a copy of model node with all of its children.
	 */
	public abstract ModelNode copy();

	/**
	 * Returns the child node with the given name. Will always return null if called on a logical node because a logical
	 * node need the functional constraint to uniquely identify a child. For logical nodes use
	 * <code>getChild(String name, Fc fc)</code> instead.
	 *
	 * @param name
	 *            the name of the requested child node
	 * @return the child node with the given name.
	 */
	public ModelNode getChild(String name) {
		return getChild(name, null);
	}

	/**
	 * Returns the child node with the given name and functional constraint. The fc is ignored if this function is
	 * called on any model node other than logical node.
	 *
	 * @param name
	 *            the name of the requested child node
	 * @param fc
	 *            the functional constraint of the requested child node
	 * @return the child node with the given name and functional constrain
	 */
	public ModelNode getChild(String name, Fc fc) {
		return children.get(name);
	}

	@SuppressWarnings("unchecked")
	public Collection<ModelNode> getChildren() {
		if (children == null) {
			return null;
		}
		return (Collection<ModelNode>) ((Collection<?>) children.values());
	}

	protected Iterator<Iterator<? extends ModelNode>> getIterators() {
		List<Iterator<? extends ModelNode>> iterators = new ArrayList<Iterator<? extends ModelNode>>();
		if (children != null) {
			iterators.add(children.values().iterator());
		}
		return iterators.iterator();
	}

	public Map<String, ModelNode> getChildrenMap() {
		return this.children;
	}

	/**
	 * Returns the reference of the model node.
	 *
	 * @return the reference of the model node.
	 */
	public ObjectReference getReference() {
		return objectReference;
	}

	/**
	 * Returns the name of the model node.
	 *
	 * @return the name of the model node.
	 */
	public String getName() {
		return objectReference.getName();
	}

	@Override
	public Iterator<ModelNode> iterator() {
		return children.values().iterator();
	}

	/**
	 * Returns a list of all leaf nodes (basic data attributes) contained in the subtree of this model node.
	 *
	 * @return a list of all leaf nodes (basic data attributes) contained in the subtree of this model node.
	 */
	public List<BasicDataAttribute> getBasicDataAttributes() {
		List<BasicDataAttribute> subBasicDataAttributes = new LinkedList<BasicDataAttribute>();
		for (ModelNode child : children.values()) {
			subBasicDataAttributes.addAll(child.getBasicDataAttributes());
		}
		return subBasicDataAttributes;
	}

	@Override
	public String toString() {
		return getReference().toString();
	}

	void setParent(ModelNode parent) {
		this.parent = parent;
	}

	/**
	 * Returns the parent node of this node.
	 *
	 * @return the parent node of this node.
	 */
	public ModelNode getParent() {
		return parent;
	}

	Data getMmsDataObj() {
		return null;
	}

	void setValueFromMmsDataObj(Data data) throws ServiceError {
	}

	TypeSpecification getMmsTypeSpec() {
		List<StructComponent> structComponents = new LinkedList<StructComponent>();
		for (ModelNode child : children.values()) {
			structComponents.add(new StructComponent(new BerVisibleString(child.getName().getBytes()), child
					.getMmsTypeSpec()));
		}
		SubSeqOf_components componentsSequenceType = new SubSeqOf_components(structComponents);
		SubSeq_structure structure = new SubSeq_structure(null, componentsSequenceType);

		return new TypeSpecification(null, structure, null, null, null, null, null, null, null, null, null, null);
	}

}
