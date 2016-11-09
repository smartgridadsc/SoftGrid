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

import org.openmuc.jasn1.ber.types.BerBoolean;
import org.openmuc.jasn1.ber.types.BerNull;
import org.openmuc.openiec61850.internal.mms.asn1.Data;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification;

public final class BdaBoolean extends BasicDataAttribute {

	private boolean value;

	public BdaBoolean(ObjectReference objectReference, Fc fc, String sAddr, boolean dchg, boolean dupd) {
		super(objectReference, fc, sAddr, dchg, dupd);
		basicType = BdaType.BOOLEAN;
		setDefault();
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	void setValueFrom(BasicDataAttribute bda) {
		value = ((BdaBoolean) bda).getValue();
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public void setDefault() {
		value = false;
	}

	@Override
	public BdaBoolean copy() {
		BdaBoolean copy = new BdaBoolean(objectReference, fc, sAddr, dchg, dupd);
		copy.setValue(value);
		if (mirror == null) {
			copy.mirror = this;
		}
		else {
			copy.mirror = mirror;
		}
		return copy;
	}

	@Override
	Data getMmsDataObj() {
		return new Data(null, null, new BerBoolean(value), null, null, null, null, null, null, null, null, null);
	}

	@Override
	void setValueFromMmsDataObj(Data data) throws ServiceError {
		if (data.boolean_ == null) {
			throw new ServiceError(ServiceError.TYPE_CONFLICT, "expected type: boolean");
		}
		value = data.boolean_.val;
	}

	@Override
	TypeSpecification getMmsTypeSpec() {
		return new TypeSpecification(null, null, new BerNull(), null, null, null, null, null, null, null, null, null);
	}

	@Override
	public String toString() {
		return getReference().toString() + ": " + value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BdaBoolean) {
			return value == ((BdaBoolean) obj).getValue();
		}
		else {
			return false;
		}
	}

}
