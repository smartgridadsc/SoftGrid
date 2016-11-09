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

public final class BdaFloat32 extends BasicDataAttribute {

	private byte[] value;

	private static TypeSpecification mmsTypeSpec = new TypeSpecification(null, null, null, null, null, null,
			new SubSeq_floating_point(new BerInteger(32), new BerInteger(8)), null, null, null, null, null);

	public BdaFloat32(ObjectReference objectReference, Fc fc, String sAddr, boolean dchg, boolean dupd) {
		super(objectReference, fc, sAddr, dchg, dupd);
		basicType = BdaType.FLOAT32;
		setDefault();
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	@Override
	void setValueFrom(BasicDataAttribute bda) {
		byte[] srcValue = ((BdaFloat32) bda).getValue();
		if (value.length != srcValue.length) {
			value = new byte[srcValue.length];
		}
		System.arraycopy(srcValue, 0, value, 0, srcValue.length);
	}

	public void setFloat(Float value) {
		this.value = ByteBuffer.allocate(1 + 4).put((byte) 8).putFloat(value).array();
	}

	public byte[] getValue() {
		return value;
	}

	public Float getFloat() {
		return Float.intBitsToFloat(((0xff & value[1]) << 24) | ((0xff & value[2]) << 16) | ((0xff & value[3]) << 8)
				| ((0xff & value[4]) << 0));
	}

	@Override
	public void setDefault() {
		value = new byte[] { 8, 0, 0, 0, 0 };
	}

	@Override
	public BdaFloat32 copy() {
		BdaFloat32 copy = new BdaFloat32(objectReference, fc, sAddr, dchg, dupd);
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
		if (value == null) {
			return null;
		}
		return new Data(null, null, null, null, null, null, new BerOctetString(value), null, null, null, null, null);
	}

	@Override
	void setValueFromMmsDataObj(Data data) throws ServiceError {
		if (data.floating_point == null || data.floating_point.octetString.length != 5) {
			throw new ServiceError(ServiceError.TYPE_CONFLICT,
					"expected type: floating_point as an octet string of size 5");
		}
		value = data.floating_point.octetString;
	}

	@Override
	TypeSpecification getMmsTypeSpec() {
		return mmsTypeSpec;
	}

	@Override
	public String toString() {
		return getReference().toString() + ": " + getFloat();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BdaFloat32) {
			return Arrays.equals(value, ((BdaFloat32) obj).getValue());
		}
		else {
			return false;
		}
	}

}
