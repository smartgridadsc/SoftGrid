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

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.BerOctetString;
import org.openmuc.openiec61850.internal.mms.asn1.Data;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification.SubSeq_floating_point;

public final class BdaFloat64 extends BasicDataAttribute {

	private byte[] value = new byte[] { 11, 0, 0, 0, 0, 0, 0, 0, 0 };

	public BdaFloat64(ObjectReference objectReference, Fc fc, String sAddr, boolean dchg, boolean dupd) {
		super(objectReference, fc, sAddr, dchg, dupd);
		basicType = BdaType.FLOAT64;
		setDefault();
	}

	public void setValue(byte[] value) {
		if (value != null && value.length != 9) {
			throw new IllegalArgumentException("value does not have length 9");
		}
		this.value = value;
	}

	@Override
	void setValueFrom(BasicDataAttribute bda) {
		byte[] srcValue = ((BdaFloat64) bda).getValue();
		if (value.length != srcValue.length) {
			value = new byte[srcValue.length];
		}
		System.arraycopy(srcValue, 0, value, 0, srcValue.length);
	}

	public void setDouble(Double value) {
		this.value = ByteBuffer.allocate(1 + 8).put((byte) 11).putDouble(value).array();
	}

	public byte[] getValue() {
		return value;
	}

	public Double getDouble() {
		if (value == null) {
			return null;
		}
		return Double.longBitsToDouble(((0xffL & (value[1])) << 56) | ((0xffL & (value[2])) << 48)
				| ((0xffL & (value[3])) << 40) | ((0xffL & (value[4])) << 32) | ((0xffL & (value[5])) << 24)
				| ((0xffL & (value[6])) << 16) | ((0xffL & (value[7])) << 8) | ((0xffL & (value[8])) << 0));
	}

	@Override
	public void setDefault() {
		value = new byte[] { 11, 0, 0, 0, 0, 0, 0, 0, 0 };
	}

	@Override
	public BdaFloat64 copy() {
		BdaFloat64 copy = new BdaFloat64(objectReference, fc, sAddr, dchg, dupd);
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
		return new Data(null, null, null, null, null, null, new BerOctetString(value), null, null, null, null, null);
	}

	@Override
	void setValueFromMmsDataObj(Data data) throws ServiceError {
		if (data.floating_point == null || data.floating_point.octetString.length != 9) {
			throw new ServiceError(ServiceError.TYPE_CONFLICT,
					"expected type: floating_point as an octet string of size 9");
		}
		value = data.floating_point.octetString;
	}

	@Override
	TypeSpecification getMmsTypeSpec() {
		return new TypeSpecification(null, null, null, null, null, null, new SubSeq_floating_point(new BerInteger(64),
				new BerInteger(11)), null, null, null, null, null);
	}

	@Override
	public String toString() {
		return getReference().toString() + ": " + getDouble();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BdaFloat64) {
			return Arrays.equals(value, ((BdaFloat64) obj).getValue());
		}
		else {
			return false;
		}
	}

}
