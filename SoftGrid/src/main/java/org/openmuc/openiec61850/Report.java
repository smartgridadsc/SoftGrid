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

import java.util.ArrayList;
import java.util.List;

public class Report {

	private final String rptId;
	private final BdaOptFlds optFlds;
	private final Integer sqNum;
	private final Integer subSqNum;
	private final boolean moreSegmentsFollow;
	private final String dataSetRef;
	private final boolean bufOvfl;
	private final Long confRev;
	private final BdaEntryTime timeOfEntry;
	private final BdaOctetString entryId;
	private final byte[] inclusionBitString;
	private final List<BdaReasonForInclusion> reasonCodes;
	private final DataSet dataSet;

	private final List<ReportEntryData> entryData = new ArrayList<ReportEntryData>();

	public Report(String rptId, BdaOptFlds optFlds, Integer sqNum, Integer subSqNum, boolean moreSegmentsFollow,
			String dataSetRef, boolean bufOvfl, Long confRev, BdaEntryTime timeOfEntry, BdaOctetString entryId,
			byte[] inclusionBitString, List<BdaReasonForInclusion> reasonCodes, DataSet dataSet) {
		this.rptId = rptId;
		this.optFlds = optFlds;
		this.sqNum = sqNum;
		this.subSqNum = subSqNum;
		this.moreSegmentsFollow = moreSegmentsFollow;
		this.dataSetRef = dataSetRef;
		this.bufOvfl = bufOvfl;
		this.confRev = confRev;
		this.timeOfEntry = timeOfEntry;
		this.entryId = entryId;
		this.inclusionBitString = inclusionBitString;
		this.reasonCodes = reasonCodes;
		this.dataSet = dataSet;
	}

	public String getRptId() {
		return rptId;
	}

	public BdaOptFlds getOptFlds() {
		return optFlds;
	}

	/**
	 * Sequence numberThe parameter MoreSegmentsFollow indicates that more report segments with the same sequence number
	 * follow, counted up for every {@code Report} instance generated
	 * 
	 * @return the sequence number
	 */
	public Integer getSqNum() {
		return sqNum;
	}

	/**
	 * For the case of long reports that do not fit into one message, a single report shall be divided into subreports.
	 * Each segment – of one report – shall be numbered with the same sequence number and a unique SubSqNum.
	 * 
	 * @return the subsequence number
	 */
	public Integer getSubSqNum() {
		return subSqNum;
	}

	/**
	 * The parameter MoreSegmentsFollow indicates that more report segments with the same sequence number follow
	 * 
	 * @return true if more segments follow
	 */
	public boolean isMoreSegmentsFollow() {
		return moreSegmentsFollow;
	}

	public String getDataSetRef() {
		return dataSetRef;
	}

	/**
	 * The parameter BufOvfl shall indicate to the client that entries within the buffer may have been lost. The
	 * detection of possible loss of information occurs when a client requests a resynchronization to a non-existent
	 * entry or to the first entry in the queue.
	 * 
	 * @return true if buffer overflow is true
	 */
	public boolean isBufOvfl() {
		return bufOvfl;
	}

	public Long getConfRev() {
		return confRev;
	}

	/**
	 * The parameter TimeOfEntry shall specify the time when the EntryID was created
	 * 
	 * @return the time of entry
	 */
	public BdaEntryTime getTimeOfEntry() {
		return timeOfEntry;
	}

	public BdaOctetString getEntryId() {
		return entryId;
	}

	public List<ReportEntryData> getEntryData() {
		return entryData;
	}

	/**
	 * Indicator of data set members included in the report
	 * 
	 * @return the inclusion bit string as a byte array
	 */
	public byte[] getInclusionBitString() {
		return inclusionBitString;
	}

	/**
	 * Gets the reasons for inclusion
	 * 
	 * @return the reasons for inclusion
	 */
	public List<BdaReasonForInclusion> getReasonCodes() {
		return reasonCodes;
	}

	/**
	 * Gets the data set associated with this report.
	 * 
	 * @return the data set associated with this report.
	 */
	public DataSet getDataSet() {
		return dataSet;
	}

}
