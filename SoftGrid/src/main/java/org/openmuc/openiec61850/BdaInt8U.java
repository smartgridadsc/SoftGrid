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

import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.openiec61850.internal.mms.asn1.Data;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification;

public final class BdaInt8U extends BasicDataAttribute {

	private short value;

	public BdaInt8U(ObjectReference objectReference, Fc fc, String sAddr, boolean dchg, boolean dupd) {
		super(objectReference, fc, sAddr, dchg, dupd);
		basicType = BdaType.INT8U;
		setDefault();
	}

	public void setValue(short value) {
		this.value = value;
	}

	@Override
	void setValueFrom(BasicDataAttribute bda) {
		value = ((BdaInt8U) bda).getValue();
	}

	public short getValue() {
		return value;
	}

	@Override
	public void setDefault() {
		value = 0;
	}

	@Override
	public BdaInt8U copy() {
		BdaInt8U copy = new BdaInt8U(objectReference, fc, sAddr, dchg, dupd);
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
		return new Data(null, null, null, null, null, new BerInteger(value), null, null, null, null, null, null);
	}

	@Override
	void setValueFromMmsDataObj(Data data) throws ServiceError {
		if (data.unsigned == null) {
			throw new ServiceError(ServiceError.TYPE_CONFLICT, "expected type: unsigned");
		}
		value = (short) data.unsigned.val;
	}

	@Override
	TypeSpecification getMmsTypeSpec() {
		return new TypeSpecification(null, null, null, null, null, new BerInteger(8), null, null, null, null, null,
				null);
	}

	@Override
	public String toString() {
		return getReference().toString() + ": " + value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BdaInt8U) {
			return value == ((BdaInt8U) obj).getValue();
		}
		else {
			return false;
		}
	}

}
