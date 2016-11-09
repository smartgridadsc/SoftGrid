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

import java.util.List;

public class Brcb extends Rcb {

	public Brcb(ObjectReference objectReference, List<FcModelNode> children) {
		super(objectReference, Fc.BR, children);
	}

	public BdaBoolean getPurgeBuf() {
		return (BdaBoolean) children.get("PurgeBuf");
	}

	public BdaOctetString getEntryId() {
		return (BdaOctetString) children.get("EntryID");
	}

	public BdaEntryTime getTimeOfEntry() {
		return (BdaEntryTime) children.get("TimeOfEntry");
	}

	/**
	 * Gets the ResvTms attribute. This attribute is optional. Will return NULL if the attribute is not available.
	 * 
	 * @return the ResvTms attribute, null if not available.
	 */
	public BdaInt16 getResvTms() {
		return (BdaInt16) children.get("ResvTms");
	}
}
