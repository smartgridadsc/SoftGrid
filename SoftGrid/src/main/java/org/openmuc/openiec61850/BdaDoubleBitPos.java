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

public final class BdaDoubleBitPos extends BdaBitString {

	public enum DoubleBitPos {
		INTERMEDIATE_STATE(0), OFF(1), ON(2), BAD_STATE(3);
		private final int value;

		private DoubleBitPos(int value) {
			this.value = value;
		}

		public int getIntValue() {
			return value;
		}
	}

	public BdaDoubleBitPos(ObjectReference objectReference, Fc fc, String sAddr, boolean dchg, boolean dupd) {
		super(objectReference, fc, sAddr, 2, dchg, dupd);
		basicType = BdaType.DOUBLE_BIT_POS;
		setDefault();
	}

	/**
	 * Sets the value to DoubleBitPos.OFF
	 */
	@Override
	public void setDefault() {
		value = new byte[] { 0x40 };
	}

	@Override
	public BdaDoubleBitPos copy() {
		BdaDoubleBitPos copy = new BdaDoubleBitPos(objectReference, fc, sAddr, dchg, dupd);
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

	public DoubleBitPos getDoubleBitPos() {

		if ((value[0] & 0xC0) == 0xC0) {
			return DoubleBitPos.BAD_STATE;
		}

		if ((value[0] & 0x80) == 0x80) {
			return DoubleBitPos.ON;
		}

		if ((value[0] & 0x40) == 0x40) {
			return DoubleBitPos.OFF;
		}

		return DoubleBitPos.INTERMEDIATE_STATE;
	}

	public void setDoubleBitPos(DoubleBitPos doubleBitPos) {
		if (doubleBitPos == DoubleBitPos.BAD_STATE) {
			value[0] = (byte) 0xC0;
		}
		else if (doubleBitPos == DoubleBitPos.ON) {
			value[0] = (byte) 0x80;
		}
		else if (doubleBitPos == DoubleBitPos.OFF) {
			value[0] = (byte) 0x40;
		}
		else {
			value[0] = (byte) 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BdaDoubleBitPos) {
			return Arrays.equals(value, ((BdaDoubleBitPos) obj).getValue());
		}
		else {
			return false;
		}
	}

}
