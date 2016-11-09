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
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmuc.jasn1.ber.types.string.BerVisibleString;
import org.openmuc.openiec61850.internal.mms.asn1.ObjectName;

public final class DataSet implements Iterable<FcModelNode> {

	private final String dataSetReference;
	private final List<FcModelNode> members;
	private final Map<Fc, Map<String, FcModelNode>> membersMap = new EnumMap<Fc, Map<String, FcModelNode>>(Fc.class);
	private final boolean deletable;
	private ObjectName mmsObjectName = null;

	public DataSet(String dataSetReference, List<FcModelNode> members) {
		this(dataSetReference, members, true);
	}

	public DataSet(String dataSetReference, List<FcModelNode> members, boolean deletable) {
		if (!dataSetReference.startsWith("@") && dataSetReference.indexOf('/') == -1) {
			throw new IllegalArgumentException("DataSet reference " + dataSetReference
					+ " is invalid. Must either start with @ or contain a slash.");
		}
		this.members = new LinkedList<FcModelNode>();
		this.dataSetReference = dataSetReference;
		this.deletable = deletable;

		for (Fc myfc : Fc.values()) {
			membersMap.put(myfc, new LinkedHashMap<String, FcModelNode>());
		}

		for (FcModelNode member : members) {
			this.members.add(member);
			membersMap.get(member.getFc()).put(member.getReference().toString(), member);
		}
	}

	public String getReferenceStr() {
		return dataSetReference;
	}

	public DataSet copy() {
		List<FcModelNode> membersCopy = new ArrayList<FcModelNode>(members.size());
		for (FcModelNode node : members) {
			membersCopy.add((FcModelNode) node.copy());
		}
		return new DataSet(dataSetReference, membersCopy, deletable);
	}

	public FcModelNode getMember(ObjectReference memberReference, Fc fc) {
		if (fc != null) {
			return membersMap.get(fc).get(memberReference.toString());
		}
		for (FcModelNode member : members) {
			if (member.getReference().toString().equals(memberReference.toString())) {
				return member;
			}
		}
		return null;
	}

	public FcModelNode getMember(int index) {
		return members.get(index);
	}

	public List<FcModelNode> getMembers() {
		return members;
	}

	/**
	 * Those DataSets defined in the SCL file are not deletable. All other DataSets are deletable. Note that no
	 * Reports/Logs may be using the DataSet otherwise it cannot be deleted (but this function will still return true).
	 * 
	 * @return true if deletable
	 */
	public boolean isDeletable() {
		return deletable;
	}

	@Override
	public Iterator<FcModelNode> iterator() {
		return members.iterator();
	}

	public List<BasicDataAttribute> getBasicDataAttributes() {
		List<BasicDataAttribute> subBasicDataAttributes = new LinkedList<BasicDataAttribute>();
		for (ModelNode member : members) {
			subBasicDataAttributes.addAll(member.getBasicDataAttributes());
		}
		return subBasicDataAttributes;
	}

	ObjectName getMmsObjectName() {

		if (mmsObjectName != null) {
			return mmsObjectName;
		}

		if (dataSetReference.charAt(0) == '@') {
			mmsObjectName = new ObjectName(null, null, new BerVisibleString(dataSetReference.getBytes()));
			return mmsObjectName;
		}

		int slash = dataSetReference.indexOf('/');
		String domainID = dataSetReference.substring(0, slash);
		String itemID = dataSetReference.substring(slash + 1, dataSetReference.length()).replace('.', '$');

		ObjectName.SubSeq_domain_specific value = null;

		value = new ObjectName.SubSeq_domain_specific(new BerVisibleString(domainID), new BerVisibleString(itemID));

		mmsObjectName = new ObjectName(null, value, null);
		return mmsObjectName;

	}

	@Override
	public String toString() {
		return dataSetReference;
	}

}
