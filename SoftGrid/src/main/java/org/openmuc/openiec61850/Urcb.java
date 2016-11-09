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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openmuc.jasn1.ber.types.BerBitString;
import org.openmuc.jasn1.ber.types.string.BerVisibleString;
import org.openmuc.openiec61850.internal.mms.asn1.AccessResult;
import org.openmuc.openiec61850.internal.mms.asn1.Data;
import org.openmuc.openiec61850.internal.mms.asn1.InformationReport;
import org.openmuc.openiec61850.internal.mms.asn1.MmsPdu;
import org.openmuc.openiec61850.internal.mms.asn1.ObjectName;
import org.openmuc.openiec61850.internal.mms.asn1.UnconfirmedPdu;
import org.openmuc.openiec61850.internal.mms.asn1.UnconfirmedService;
import org.openmuc.openiec61850.internal.mms.asn1.VariableAccessSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Urcb extends Rcb {

	private final static Logger logger = LoggerFactory.getLogger(ServerSap.class);

	ServerAssociation reserved = null;
	boolean enabled = false;
	private Timer integrityTimer;
	// private ScheduledFuture<?> integrityFuture = null;
	private ScheduledFuture<?> bufTmFuture = null;
	final HashMap<FcModelNode, BdaReasonForInclusion> membersToBeReported = new LinkedHashMap<FcModelNode, BdaReasonForInclusion>();

	public Urcb(ObjectReference objectReference, List<FcModelNode> children) {
		super(objectReference, Fc.RP, children);
	}

	/**
	 * Reserve URCB - The attribute Resv (if set to TRUE) shall indicate that the URCB is currently exclusively reserved
	 * for the client that has set the value to TRUE. Other clients shall not be allowed to set any attribute of that
	 * URCB.
	 * 
	 * @return the Resv child
	 */
	public BdaBoolean getResv() {
		return (BdaBoolean) children.get("Resv");
	}

	void enable() {

		for (FcModelNode dataSetMember : dataSet) {
			for (BasicDataAttribute bda : dataSetMember.getBasicDataAttributes()) {
				if (bda.dchg) {
					if (getTrgOps().isDataChange()) {
						synchronized (bda.chgRcbs) {
							bda.chgRcbs.add(this);
						}
					}
				}
				else if (bda.qchg) {
					if (getTrgOps().isQualityChange()) {
						synchronized (bda.chgRcbs) {
							bda.chgRcbs.add(this);
						}
					}
				}
				if (bda.dupd) {
					if (getTrgOps().isDataUpdate()) {
						synchronized (bda.dupdRcbs) {
							bda.dupdRcbs.add(this);
						}
					}
				}
			}
		}

		if (getTrgOps().isIntegrity() && !(getIntgPd().getValue() < 10l)) {
			integrityTimer = new Timer();

			integrityTimer.schedule(new TimerTask() {
				// integrityFuture = reserved.executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					synchronized (Urcb.this) {
						if (!enabled) {
							return;
						}
						reserved.sendAnMmsPdu(getMmsReport(true, false));
					}
				}
				// }, getIntgPd().getValue(), getIntgPd().getValue(), TimeUnit.MILLISECONDS);
			}, getIntgPd().getValue(), getIntgPd().getValue());

		}

		enabled = true;

	}

	void disable() {

		for (FcModelNode dataSetMember : dataSet) {
			for (BasicDataAttribute bda : dataSetMember.getBasicDataAttributes()) {
				if (bda.dchg) {
					if (getTrgOps().isDataChange()) {
						synchronized (bda.chgRcbs) {
							bda.chgRcbs.remove(this);
						}
					}
				}
				else if (bda.qchg) {
					if (getTrgOps().isQualityChange()) {
						synchronized (bda.chgRcbs) {
							bda.chgRcbs.remove(this);
						}
					}
				}
				if (bda.dupd) {
					if (getTrgOps().isDataUpdate()) {
						synchronized (bda.dupdRcbs) {
							bda.dupdRcbs.remove(this);
						}
					}
				}
			}
		}

		// if (integrityFuture != null) {
		// integrityFuture.cancel(false);
		// }
		if (integrityTimer != null) {
			integrityTimer.cancel();
		}

		enabled = false;

	}

	void generalInterrogation() {
		reserved.executor.execute(new Runnable() {
			@Override
			public void run() {
				synchronized (Urcb.this) {
					if (!enabled) {
						return;
					}
					reserved.sendAnMmsPdu(getMmsReport(false, true));
				}
			}
		});
	}

	protected MmsPdu getMmsReport(boolean integrity, boolean gi) {

		List<AccessResult> listOfAccessResult = new ArrayList<AccessResult>();

		listOfAccessResult.add(new AccessResult(null, getRptId().getMmsDataObj()));
		listOfAccessResult.add(new AccessResult(null, getOptFlds().getMmsDataObj()));

		if (getOptFlds().isSequenceNumber()) {
			listOfAccessResult.add(new AccessResult(null, getSqNum().getMmsDataObj()));
		}
		getSqNum().setValue((short) (getSqNum().getValue() + 1));

		if (getOptFlds().isReportTimestamp()) {
			BdaEntryTime entryTime = new BdaEntryTime(null, null, null, false, false);
			entryTime.setTimestamp(System.currentTimeMillis());
			listOfAccessResult.add(new AccessResult(null, entryTime.getMmsDataObj()));
		}

		if (getOptFlds().isDataSetName()) {
			listOfAccessResult.add(new AccessResult(null, getDatSet().getMmsDataObj()));
		}

		if (getOptFlds().isConfigRevision()) {
			listOfAccessResult.add(new AccessResult(null, getConfRev().getMmsDataObj()));
		}

		// segmentation not supported

		List<FcModelNode> dataSetMembers = dataSet.getMembers();
		int dataSetSize = dataSetMembers.size();

		// inclusion bitstring
		byte[] inclusionStringArray = new byte[(dataSetSize - 1) / 8 + 1];

		if (integrity || gi) {

			for (int i = 0; i < dataSetSize; i++) {
				inclusionStringArray[i / 8] |= 1 << (7 - i % 8);
			}
			BerBitString inclusionString = new BerBitString(inclusionStringArray, dataSetSize);
			listOfAccessResult.add(new AccessResult(null, new Data(null, null, null, inclusionString, null, null, null,
					null, null, null, null, null)));

			// data reference sending not supported for now

			for (FcModelNode dataSetMember : dataSetMembers) {
				listOfAccessResult.add(new AccessResult(null, dataSetMember.getMmsDataObj()));
			}

			BdaReasonForInclusion reasonForInclusion = new BdaReasonForInclusion(null);
			if (integrity) {
				reasonForInclusion.setIntegrity(true);
			}
			else {
				reasonForInclusion.setGeneralInterrogation(true);
			}

			if (getOptFlds().isReasonForInclusion()) {
				for (int i = 0; i < dataSetMembers.size(); i++) {
					listOfAccessResult.add(new AccessResult(null, reasonForInclusion.getMmsDataObj()));
				}
			}

		}
		else {

			int index = 0;
			for (FcModelNode dataSetMember : dataSet) {
				if (membersToBeReported.get(dataSetMember) != null) {
					inclusionStringArray[index / 8] |= 1 << (7 - index % 8);
				}
				index++;
			}
			BerBitString inclusionString = new BerBitString(inclusionStringArray, dataSetSize);
			listOfAccessResult.add(new AccessResult(null, new Data(null, null, null, inclusionString, null, null, null,
					null, null, null, null, null)));

			// data reference sending not supported for now

			for (FcModelNode dataSetMember : dataSetMembers) {
				if (membersToBeReported.get(dataSetMember) != null) {
					listOfAccessResult.add(new AccessResult(null, dataSetMember.getMmsDataObj()));
				}
			}

			if (getOptFlds().isReasonForInclusion()) {
				for (FcModelNode dataSetMember : dataSetMembers) {
					BdaReasonForInclusion reasonForInclusion = membersToBeReported.get(dataSetMember);
					if (reasonForInclusion != null) {
						listOfAccessResult.add(new AccessResult(null, reasonForInclusion.getMmsDataObj()));
					}
				}
			}

			membersToBeReported.clear();
			bufTmFuture = null;

		}

		VariableAccessSpecification varAccSpec = new VariableAccessSpecification(null, new ObjectName(
				new BerVisibleString("RPT"), null, null));

		InformationReport infoReport = new InformationReport(varAccSpec,
				new InformationReport.SubSeqOf_listOfAccessResult(listOfAccessResult));

		return new MmsPdu(null, null, null, new UnconfirmedPdu(new UnconfirmedService(infoReport)), null, null, null,
				null, null);

	}

	@Override
	public FcDataObject copy() {
		List<FcModelNode> childCopies = new ArrayList<FcModelNode>(children.size());
		for (ModelNode childNode : children.values()) {
			childCopies.add((FcModelNode) childNode.copy());
		}
		Urcb urcb = new Urcb(objectReference, childCopies);
		urcb.dataSet = dataSet;
		return urcb;
	}

	void report(BasicDataAttribute bda, boolean dchg, boolean qchg, boolean dupd) {

		synchronized (this) {

			if (!enabled) {
				return;
			}

			FcModelNode memberFound = null;
			FcModelNode fcModelNode = bda;
			while (memberFound == null) {
				for (FcModelNode member : dataSet) {
					if (member == fcModelNode) {
						memberFound = fcModelNode;
						break;
					}
				}
				if (memberFound != null) {
					break;
				}
				if (!(fcModelNode.parent instanceof FcModelNode)) {
					logger.error("Unable to report Basic Data Attribute because it is not part of the referenced data set: "
							+ bda.getReference());
					return;
				}
				fcModelNode = (FcModelNode) fcModelNode.parent;
			}

			BdaReasonForInclusion reasonForInclusion = membersToBeReported.get(fcModelNode);
			if (reasonForInclusion == null) {
				reasonForInclusion = new BdaReasonForInclusion(null);
				membersToBeReported.put(fcModelNode, reasonForInclusion);
			}

			if (dchg) {
				reasonForInclusion.setDataChange(true);
			}
			if (dupd) {
				reasonForInclusion.setDataUpdate(true);
			}
			else if (qchg) {
				reasonForInclusion.setQualityChange(true);
			}

			// if bufTmFuture is not null then it is already scheduled and will send the combined report
			if (bufTmFuture == null) {
				bufTmFuture = reserved.executor.schedule(new Runnable() {
					@Override
					public void run() {
						synchronized (Urcb.this) {
							if (!enabled) {
								return;
							}
							reserved.sendAnMmsPdu(getMmsReport(false, false));
						}
					}
				}, getBufTm().getValue(), TimeUnit.MILLISECONDS);
			}

		}

	}
}
