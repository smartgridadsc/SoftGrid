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

import java.util.Arrays;

import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.BerOctetString;
import org.openmuc.openiec61850.internal.mms.asn1.Data;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification;

public final class BdaOctetString extends BasicDataAttribute {

	private byte[] value;
	private final int maxLength;

	public BdaOctetString(ObjectReference objectReference, Fc fc, String sAddr, int maxLength, boolean dchg,
			boolean dupd) {
		super(objectReference, fc, sAddr, dchg, dupd);
		basicType = BdaType.OCTET_STRING;
		this.maxLength = maxLength;
		setDefault();
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		if (value != null && value.length > maxLength) {
			throw new IllegalArgumentException("OCTET_STRING value size exceeds maxLength of " + maxLength);
		}
		this.value = value;
	}

	@Override
	void setValueFrom(BasicDataAttribute bda) {
		byte[] srcValue = ((BdaOctetString) bda).getValue();
		if (value.length != srcValue.length) {
			value = new byte[srcValue.length];
		}
		System.arraycopy(srcValue, 0, value, 0, srcValue.length);
	}

	public int getMaxLength() {
		return maxLength;
	}

	@Override
	public void setDefault() {
		value = new byte[0];
	}

	@Override
	public BdaOctetString copy() {
		BdaOctetString copy = new BdaOctetString(objectReference, fc, sAddr, maxLength, dchg, dupd);
		byte[] valueCopy = new byte[value.length];
		System.arraycopy(value, 0, valueCopy, 0, value.length);
		copy.setValue(valueCopy);
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
		return new Data(null, null, null, null, null, null, null, new BerOctetString(value), null, null, null, null);
	}

	@Override
	void setValueFromMmsDataObj(Data data) throws ServiceError {
		if (data.octet_string == null) {
			throw new ServiceError(ServiceError.TYPE_CONFLICT, "expected type: octet_string");
		}
		value = data.octet_string.octetString;
	}

	@Override
	TypeSpecification getMmsTypeSpec() {
		return new TypeSpecification(null, null, null, null, null, null, null, new BerInteger(maxLength * -1), null,
				null, null, null);
	}

	@Override
	public String toString() {
		return getReference().toString() + ": " + Arrays.toString(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BdaOctetString) {
			return Arrays.equals(value, ((BdaOctetString) obj).getValue());
		}
		else {
			return false;
		}
	}

}
