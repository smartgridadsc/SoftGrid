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

public final class BdaOptFlds extends BdaBitString {

	public BdaOptFlds(ObjectReference objectReference) {
		super(objectReference, Fc.RP, null, 10, false, false);
		basicType = BdaType.OPTFLDS;
		setDefault();
	}

	@Override
	public void setDefault() {
		/* default of buffer overflow is true by default in IEC 61850-6 sec. 9.3.8 */
		value = new byte[] { 0x02, 0x00 };
	}

	@Override
	public BdaOptFlds copy() {
		BdaOptFlds copy = new BdaOptFlds(objectReference);
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

	public boolean isSequenceNumber() {
		return (value[0] & 0x40) == 0x40;
	}

	public boolean isReportTimestamp() {
		return (value[0] & 0x20) == 0x20;
	}

	public boolean isReasonForInclusion() {
		return (value[0] & 0x10) == 0x10;
	}

	/**
	 * Will the data set reference (not just the name) be included in the report.
	 * 
	 * @return true if the data set reference (not just the name) will be included in the report
	 */
	public boolean isDataSetName() {
		return (value[0] & 0x08) == 0x08;
	}

	public boolean isDataReference() {
		return (value[0] & 0x04) == 0x04;
	}

	public boolean isBufferOverflow() {
		return (value[0] & 0x02) == 0x02;
	}

	public boolean isEntryId() {
		return (value[0] & 0x01) == 0x01;
	}

	public boolean isConfigRevision() {
		return (value[1] & 0x80) == 0x80;
	}

	public boolean isSegmentation() {
		return (value[1] & 0x40) == 0x40;
	}

	public void setSequenceNumber(boolean sequenceNumber) {
		if (sequenceNumber) {
			value[0] = (byte) (value[0] | 0x40);
		}
		else {
			value[0] = (byte) (value[0] & 0xbf);
		}
	}

	public void setReportTimestamp(boolean reportTimestamp) {
		if (reportTimestamp) {
			value[0] = (byte) (value[0] | 0x20);
		}
		else {
			value[0] = (byte) (value[0] & 0x2f);
		}
	}

	public void setReasonForInclusion(boolean reasonForInclusion) {
		if (reasonForInclusion) {
			value[0] = (byte) (value[0] | 0x10);
		}
		else {
			value[0] = (byte) (value[0] & 0xef);
		}
	}

	public void setDataSetName(boolean dataSetName) {
		if (dataSetName) {
			value[0] = (byte) (value[0] | 0x08);
		}
		else {
			value[0] = (byte) (value[0] & 0xf7);
		}
	}

	public void setDataReference(boolean dataReference) {
		if (dataReference) {
			value[0] = (byte) (value[0] | 0x04);
		}
		else {
			value[0] = (byte) (value[0] & 0xfb);
		}
	}

	public void setBufferOverflow(boolean bufferOverflow) {
		if (bufferOverflow) {
			value[0] = (byte) (value[0] | 0x02);
		}
		else {
			value[0] = (byte) (value[0] & 0xfd);
		}
	}

	public void setEntryId(boolean entryId) {
		if (entryId) {
			value[0] = (byte) (value[0] | 0x01);
		}
		else {
			value[0] = (byte) (value[0] & 0xfe);
		}
	}

	public void setConfigRevision(boolean configRevision) {
		if (configRevision) {
			value[1] = (byte) (value[1] | 0x80);
		}
		else {
			value[1] = (byte) (value[1] & 0x7f);
		}
	}

	public void setSegmentation(boolean segmentation) {
		if (segmentation) {
			value[1] = (byte) (value[1] | 0x40);
		}
		else {
			value[1] = (byte) (value[1] & 0xbf);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BdaOptFlds) {
			return Arrays.equals(value, ((BdaOptFlds) obj).getValue());
		}
		else {
			return false;
		}
	}

}
