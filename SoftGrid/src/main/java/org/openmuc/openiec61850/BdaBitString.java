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

import org.openmuc.jasn1.ber.types.BerBitString;
import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.openiec61850.internal.mms.asn1.Data;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification;

abstract public class BdaBitString extends BasicDataAttribute {

	byte[] value;
	final int maxNumBits;

	public BdaBitString(ObjectReference objectReference, Fc fc, String sAddr, int maxNumBits, boolean dchg, boolean dupd) {
		super(objectReference, fc, sAddr, dchg, dupd);
		this.maxNumBits = maxNumBits;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	@Override
	void setValueFrom(BasicDataAttribute bda) {
		byte[] srcValue = ((BdaBitString) bda).getValue();
		if (value.length != srcValue.length) {
			value = new byte[srcValue.length];
		}
		System.arraycopy(srcValue, 0, value, 0, srcValue.length);
	}

	public int getMaxNumBits() {
		return maxNumBits;
	}

	/**
	 * Initializes BIT_STRING with all zeros
	 */
	@Override
	public void setDefault() {
		value = new byte[(maxNumBits / 8 + (((maxNumBits % 8) > 0) ? 1 : 0))];
	}

	@Override
	Data getMmsDataObj() {
		return new Data(null, null, null, new BerBitString(value, maxNumBits), null, null, null, null, null, null,
				null, null);
	}

	@Override
	void setValueFromMmsDataObj(Data data) throws ServiceError {
		if (data.bit_string == null) {
			throw new ServiceError(ServiceError.TYPE_CONFLICT, "expected type: bit_string");
		}
		if (data.bit_string.numBits > maxNumBits) {
			throw new ServiceError(ServiceError.TYPE_CONFLICT, objectReference
					+ ": bit_string is bigger than maxNumBits: " + data.bit_string.numBits + ">" + maxNumBits);
		}
		value = data.bit_string.bitString;
	}

	@Override
	TypeSpecification getMmsTypeSpec() {
		return new TypeSpecification(null, null, null, new BerInteger(maxNumBits * -1), null, null, null, null, null,
				null, null, null);
	}

	@Override
	public String toString() {
		return getReference().toString() + ": " + value;
	}

}
