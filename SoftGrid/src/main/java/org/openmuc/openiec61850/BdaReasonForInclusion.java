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

public final class BdaReasonForInclusion extends BdaBitString {

	public BdaReasonForInclusion(ObjectReference objectReference) {
		super(objectReference, null, null, 7, false, false);
		basicType = BdaType.REASON_FOR_INCLUSION;
		setDefault();
	}

	@Override
	public BdaReasonForInclusion copy() {
		BdaReasonForInclusion copy = new BdaReasonForInclusion(objectReference);
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

	public boolean isDataChange() {
		return (value[0] & 0x40) == 0x40;
	}

	public boolean isQualityChange() {
		return (value[0] & 0x20) == 0x20;
	}

	public boolean isDataUpdate() {
		return (value[0] & 0x10) == 0x10;
	}

	public boolean isIntegrity() {
		return (value[0] & 0x08) == 0x08;
	}

	public boolean isGeneralInterrogation() {
		return (value[0] & 0x04) == 0x04;
	}

	public boolean isApplicationTrigger() {
		return (value[0] & 0x02) == 0x02;
	}

	public void setDataChange(boolean dataChange) {
		if (dataChange) {
			value[0] = (byte) (value[0] | 0x40);
		}
		else {
			value[0] = (byte) (value[0] & 0xbf);
		}
	}

	public void setQualityChange(boolean qualityChange) {
		if (qualityChange) {
			value[0] = (byte) (value[0] | 0x20);
		}
		else {
			value[0] = (byte) (value[0] & 0xdf);
		}
	}

	public void setDataUpdate(boolean dataUpdate) {
		if (dataUpdate) {
			value[0] = (byte) (value[0] | 0x10);
		}
		else {
			value[0] = (byte) (value[0] & 0xef);
		}
	}

	public void setIntegrity(boolean integrity) {
		if (integrity) {
			value[0] = (byte) (value[0] | 0x08);
		}
		else {
			value[0] = (byte) (value[0] & 0xf7);
		}
	}

	public void setGeneralInterrogation(boolean generalInterrogation) {
		if (generalInterrogation) {
			value[0] = (byte) (value[0] | 0x04);
		}
		else {
			value[0] = (byte) (value[0] & 0xfb);
		}
	}

	public void setApplicationTrigger(boolean applicationTrigger) {
		if (applicationTrigger) {
			value[0] = (byte) (value[0] | 0x02);
		}
		else {
			value[0] = (byte) (value[0] & 0xfd);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BdaReasonForInclusion) {
			return Arrays.equals(value, ((BdaReasonForInclusion) obj).getValue());
		}
		else {
			return false;
		}
	}

}
