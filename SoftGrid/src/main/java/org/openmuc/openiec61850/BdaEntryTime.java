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

import org.openmuc.jasn1.ber.types.BerBoolean;
import org.openmuc.jasn1.ber.types.BerOctetString;
import org.openmuc.openiec61850.internal.mms.asn1.Data;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification;

/**
 * 
 * BdaEntryTime stores time in terms of days and ms since 1984.
 * 
 * @author Stefan Feuerhahn
 * 
 */
public final class BdaEntryTime extends BasicDataAttribute {

	private byte[] value;

	public BdaEntryTime(ObjectReference objectReference, Fc fc, String sAddr, boolean dchg, boolean dupd) {
		super(objectReference, fc, sAddr, dchg, dupd);
		basicType = BdaType.ENTRY_TIME;
		setDefault();
	}

	/**
	 * Set the value of this object to the given timestamp, where timestamp is the number of ms since epoch 1970-01-01
	 * 00:00:00 UTC. Note that timestamps before 1984 are not valid as they cannot be stored.
	 * 
	 * @param timestamp
	 *            the number of ms since epoch 1970-01-01
	 */
	public void setTimestamp(long timestamp) {
		long msSince1984 = timestamp - 441763200000l;
		int days = (int) (msSince1984 / 86400000);
		int ms = (int) (msSince1984 % 86400000);
		value = new byte[] { (byte) (ms >> 24), (byte) (ms >> 16), (byte) (ms >> 8), (byte) (ms), (byte) (days >> 8),
				(byte) days };
	}

	public long getTimestampValue() {
		if (value.length != 6) {
			return -1;
		}
		return (value[0] << 24) + (value[1] << 16) + (value[2] << 8) + value[3] + ((value[4] << 8) + value[5])
				* 86400000l;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	@Override
	void setValueFrom(BasicDataAttribute bda) {
		byte[] srcValue = ((BdaEntryTime) bda).getValue();
		if (value.length != srcValue.length) {
			value = new byte[srcValue.length];
		}
		System.arraycopy(srcValue, 0, value, 0, srcValue.length);
	}

	public byte[] getValue() {
		return value;
	}

	/**
	 * Sets EntryTime to byte[6] with all zeros
	 */
	@Override
	public void setDefault() {
		value = new byte[6];
	}

	@Override
	public BdaEntryTime copy() {
		BdaEntryTime copy = new BdaEntryTime(objectReference, fc, sAddr, dchg, dupd);
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
		return new Data(null, null, null, null, null, null, null, null, null, new BerOctetString(value), null, null);
	}

	@Override
	void setValueFromMmsDataObj(Data data) throws ServiceError {
		if (data.binary_time == null) {
			throw new ServiceError(ServiceError.TYPE_CONFLICT, "expected type: binary_time/EntryTime");
		}
		value = data.binary_time.octetString;
	}

	@Override
	TypeSpecification getMmsTypeSpec() {
		return new TypeSpecification(null, null, null, null, null, null, null, null, null, new BerBoolean(true), null,
				null);
	}

	@Override
	public String toString() {
		return getReference().toString() + ": " + value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BdaEntryTime) {
			return Arrays.equals(value, ((BdaEntryTime) obj).getValue());
		}
		else {
			return false;
		}
	}

}
