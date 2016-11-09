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

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerBitString;
import org.openmuc.jasn1.ber.types.BerBoolean;
import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.BerNull;
import org.openmuc.jasn1.ber.types.string.BerVisibleString;
import org.openmuc.josistack.AcseAssociation;
import org.openmuc.josistack.ByteBufferInputStream;
import org.openmuc.josistack.ClientAcseSap;
import org.openmuc.josistack.DecodingException;
import org.openmuc.openiec61850.internal.mms.asn1.AccessResult;
import org.openmuc.openiec61850.internal.mms.asn1.ConfirmedRequestPdu;
import org.openmuc.openiec61850.internal.mms.asn1.ConfirmedResponsePdu;
import org.openmuc.openiec61850.internal.mms.asn1.ConfirmedServiceRequest;
import org.openmuc.openiec61850.internal.mms.asn1.ConfirmedServiceResponse;
import org.openmuc.openiec61850.internal.mms.asn1.Data;
import org.openmuc.openiec61850.internal.mms.asn1.DefineNamedVariableListRequest;
import org.openmuc.openiec61850.internal.mms.asn1.DeleteNamedVariableListRequest;
import org.openmuc.openiec61850.internal.mms.asn1.DeleteNamedVariableListRequest.SubSeqOf_listOfVariableListName;
import org.openmuc.openiec61850.internal.mms.asn1.DeleteNamedVariableListResponse;
import org.openmuc.openiec61850.internal.mms.asn1.GetNameListRequest;
import org.openmuc.openiec61850.internal.mms.asn1.GetNameListRequest.SubChoice_objectScope;
import org.openmuc.openiec61850.internal.mms.asn1.GetNameListResponse;
import org.openmuc.openiec61850.internal.mms.asn1.GetNamedVariableListAttributesResponse;
import org.openmuc.openiec61850.internal.mms.asn1.GetVariableAccessAttributesRequest;
import org.openmuc.openiec61850.internal.mms.asn1.InitRequestDetail;
import org.openmuc.openiec61850.internal.mms.asn1.InitiateRequestPdu;
import org.openmuc.openiec61850.internal.mms.asn1.InitiateResponsePdu;
import org.openmuc.openiec61850.internal.mms.asn1.MmsPdu;
import org.openmuc.openiec61850.internal.mms.asn1.ObjectClass;
import org.openmuc.openiec61850.internal.mms.asn1.ObjectName;
import org.openmuc.openiec61850.internal.mms.asn1.ObjectName.SubSeq_domain_specific;
import org.openmuc.openiec61850.internal.mms.asn1.ReadRequest;
import org.openmuc.openiec61850.internal.mms.asn1.ReadResponse;
import org.openmuc.openiec61850.internal.mms.asn1.RejectPdu.SubChoice_rejectReason;
import org.openmuc.openiec61850.internal.mms.asn1.ServiceError.SubChoice_errorClass;
import org.openmuc.openiec61850.internal.mms.asn1.UnconfirmedPdu;
import org.openmuc.openiec61850.internal.mms.asn1.UnconfirmedService;
import org.openmuc.openiec61850.internal.mms.asn1.VariableAccessSpecification;
import org.openmuc.openiec61850.internal.mms.asn1.VariableAccessSpecification.SubSeqOf_listOfVariable;
import org.openmuc.openiec61850.internal.mms.asn1.VariableDef;
import org.openmuc.openiec61850.internal.mms.asn1.WriteRequest;
import org.openmuc.openiec61850.internal.mms.asn1.WriteRequest.SubSeqOf_listOfData;
import org.openmuc.openiec61850.internal.mms.asn1.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an association/connection to an IEC 61850 MMS server. An instance of <code>ClientAssociation</code> is
 * obtained using <code>ClientSap</code>. An association object can be used to execute the IEC 61850 ACSI services. Note
 * that not all ACSI services have a corresponding function in this API. For example all GetDirectory and GetDefinition
 * services are covered by <code>retrieveModel()</code>. The control services can be executed by using getDataValues and
 * setDataValues on the control objects in the data model.
 */
public final class ClientAssociation {
	private final static Logger logger = LoggerFactory.getLogger(ClientAssociation.class);
	private static final BerInteger version = new BerInteger(new byte[] { (byte) 0x01, (byte) 0x01 });
	private static final BerBitString proposedParameterCbbBitString = new BerBitString(new byte[] { 0x03, 0x05,	(byte) 0xf1, 0x00 });
	private AcseAssociation acseAssociation = null;
	private final ClientReceiver clientReceiver;
	private final BlockingQueue<MmsPdu> incomingResponses = new LinkedBlockingQueue<MmsPdu>();
	private final BerByteArrayOutputStream berOStream = new BerByteArrayOutputStream(500, true);
	ServerModel serverModel;
	private int responseTimeout;
	private int invokeId = 0;
	private int negotiatedMaxPduSize;
	private ClientEventListener reportListener = null;
	private boolean closed = false;

	public final class ClientReceiver extends Thread {
		private Integer expectedResponseId;
		private final ByteBuffer pduBuffer;
		private IOException lastIOException = null;
		public ClientReceiver(int maxMmsPduSize) {
			pduBuffer = ByteBuffer.allocate(maxMmsPduSize + 400);
		}

		@Override
		public void run() {
			try {
				while (true) {

					pduBuffer.clear();
					try {
						acseAssociation.receive(pduBuffer);
					} catch (TimeoutException e) {
						logger.error("Illegal state: A timeout exception was thrown.", e);
						throw new IllegalStateException();
					} catch (DecodingException e) {
						logger.warn("Error decoding the OSI headers of the received packet", e);
						continue;
					}

					MmsPdu decodedResponsePdu = new MmsPdu();
					try {
						decodedResponsePdu.decode(new ByteBufferInputStream(pduBuffer), null);
					} catch (IOException e) {
						logger.warn("Error decoding the received MMS PDU", e);
						continue;
					}

					if (decodedResponsePdu.unconfirmedPdu != null) {
						if (decodedResponsePdu.unconfirmedPdu.unconfirmedService.informationReport.variableAccessSpecification.listOfVariable != null) {
							logger.debug("Discarding LastApplError Report");
						}
						else {
							if (reportListener != null) {
								final Report report = processReport(decodedResponsePdu);

								Thread t1 = new Thread(new Runnable() {
									@Override
									public void run() {
										reportListener.newReport(report);
									}
								});
								t1.start();
							}
							else {
								logger.debug("discarding report because no ReportListener was registered.");
							}
						}
					}
					else if (decodedResponsePdu.rejectPdu != null) {
						synchronized (incomingResponses) {
							if (expectedResponseId == null) {
								logger.warn("Discarding Reject MMS PDU because no listener for request was found.");
								continue;
							}
							else if (decodedResponsePdu.rejectPdu.originalInvokeID.val != expectedResponseId) {
								logger.warn("Discarding Reject MMS PDU because no listener with fitting invokeID was found.");
								continue;
							}
							else {
								try {
									incomingResponses.put(decodedResponsePdu);
								} catch (InterruptedException e) {
								}
							}
						}
					}
					else if (decodedResponsePdu.confirmedErrorPdu != null) {
						synchronized (incomingResponses) {
							if (expectedResponseId == null) {
								logger.warn("Discarding ConfirmedError MMS PDU because no listener for request was found.");
								continue;
							}
							else if (decodedResponsePdu.confirmedErrorPdu.invokeID.val != expectedResponseId) {
								logger.warn("Discarding ConfirmedError MMS PDU because no listener with fitting invokeID was found.");
								continue;
							}
							else {
								try {
									incomingResponses.put(decodedResponsePdu);
								} catch (InterruptedException e) {
								}
							}
						}
					}
					else {
						synchronized (incomingResponses) {
							if (expectedResponseId == null) {
								logger.warn("Discarding ConfirmedResponse MMS PDU because no listener for request was found.");
								continue;
							}
							else if (decodedResponsePdu.confirmedResponsePdu.invokeID.val != expectedResponseId) {
								logger.warn("Discarding ConfirmedResponse MMS PDU because no listener with fitting invokeID was found.");
								continue;
							}
							else {
								try {
									incomingResponses.put(decodedResponsePdu);
								} catch (InterruptedException e) {
								}
							}
						}

					}
				}
			} catch (IOException e) {
				close(e);
			} catch (Exception e) {
				close(new IOException("unexpected exception while receiving", e));
			}
		}

		public void setResponseExpected(int invokeId) {
			expectedResponseId = invokeId;
		}

		private void disconnect() {
			synchronized (this) {
				if (closed == false) {
					closed = true;
					acseAssociation.disconnect();
					lastIOException = new IOException("Connection disconnected by client");
					Thread t1 = new Thread(new Runnable() {
						@Override
						public void run() {
							reportListener.associationClosed(lastIOException);
						}
					});
					t1.start();

					try {
						incomingResponses.put(new MmsPdu(new ConfirmedRequestPdu(), null, null, null, null, null, null,
								null, null));
					} catch (InterruptedException e1) {
					}
				}
			}
		}

		private void close(IOException e) {
			synchronized (this) {
				if (closed == false) {
					closed = true;
					acseAssociation.close();
					lastIOException = e;
					Thread t1 = new Thread(new Runnable() {
						@Override
						public void run() {
							reportListener.associationClosed(lastIOException);
						}
					});
					t1.start();

					try {
						incomingResponses.put(new MmsPdu(new ConfirmedRequestPdu(), null, null, null, null, null, null,
								null, null));
					} catch (InterruptedException e1) {
					}
				}
			}
		}

		public IOException getLastIOException() {
			return lastIOException;
		}

		public MmsPdu removeExpectedResponse() {
			synchronized (incomingResponses) {
				expectedResponseId = null;
				return incomingResponses.poll();
			}
		}

	}

	ClientAssociation(InetAddress address, int port, InetAddress localAddr, int localPort,
			String authenticationParameter, ClientAcseSap acseSap, int proposedMaxMmsPduSize,
			int proposedMaxServOutstandingCalling, int proposedMaxServOutstandingCalled,
			int proposedDataStructureNestingLevel, byte[] servicesSupportedCalling, int responseTimeout,
			int messageFragmentTimeout, ClientEventListener reportListener) throws IOException {

		this.responseTimeout = responseTimeout;

		acseSap.tSap.setMessageFragmentTimeout(messageFragmentTimeout);
		acseSap.tSap.setMessageTimeout(responseTimeout);

		negotiatedMaxPduSize = proposedMaxMmsPduSize;

		this.reportListener = reportListener;

		associate(address, port, localAddr, localPort, authenticationParameter, acseSap, proposedMaxMmsPduSize,
				proposedMaxServOutstandingCalling, proposedMaxServOutstandingCalled, proposedDataStructureNestingLevel,
				servicesSupportedCalling);

		acseAssociation.setMessageTimeout(0);

		clientReceiver = new ClientReceiver(negotiatedMaxPduSize);
		clientReceiver.start();
	}

	/**
	 * Sets the response timeout. The response timeout is used whenever a request is sent to the server. The client will
	 * wait for this amount of time for the server's response before throwing a ServiceError.TIMEOUT. Responses received
	 * after the timeout will be automatically discarded.
	 *
	 * @param timeout
	 *            the response timeout in milliseconds.
	 */
	public void setResponseTimeout(int timeout) {
		responseTimeout = timeout;
	}

	/**
	 * Gets the response timeout. The response timeout is used whenever a request is sent to the server. The client will
	 * wait for this amount of time for the server's response before throwing a ServiceError.TIMEOUT. Responses received
	 * after the timeout will be automatically discarded.
	 *
	 * @return the response timeout in milliseconds.
	 */
	public int getResponseTimeout() {
		return responseTimeout;
	}

	private int getInvokeId() {
		invokeId = (invokeId + 1) % 2147483647;
		return invokeId;
	}

	private static ServiceError mmsDataAccessErrorToServiceError(BerInteger dataAccessError) {

		switch ((int) dataAccessError.val) {
		case 1:
			return new ServiceError(ServiceError.FAILED_DUE_TO_SERVER_CONSTRAINT, "MMS DataAccessError: hardware-fault");
		case 2:
			return new ServiceError(ServiceError.INSTANCE_LOCKED_BY_OTHER_CLIENT,
					"MMS DataAccessError: temporarily-unavailable");
		case 3:
			return new ServiceError(ServiceError.ACCESS_VIOLATION, "MMS DataAccessError: object-access-denied");
		case 5:
			return new ServiceError(ServiceError.PARAMETER_VALUE_INCONSISTENT, "MMS DataAccessError: invalid-address");
		case 7:
			return new ServiceError(ServiceError.TYPE_CONFLICT, "MMS DataAccessError: type-inconsistent");
		case 10:
			return new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE, "MMS DataAccessError: object-non-existent");
		case 11:
			return new ServiceError(ServiceError.PARAMETER_VALUE_INCONSISTENT,
					"MMS DataAccessError: object-value-invalid");
		default:
			return new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT, "MMS DataAccessError: "
					+ dataAccessError.val);
		}

	}

	private static void testForErrorResponse(MmsPdu mmsResponsePdu) throws ServiceError {
		if (mmsResponsePdu.confirmedErrorPdu == null) {
			return;
		}

		SubChoice_errorClass errClass = mmsResponsePdu.confirmedErrorPdu.serviceError.errorClass;
		if (errClass != null) {
			if (errClass.access != null) {
				if (errClass.access.val == 3) {
					throw new ServiceError(ServiceError.ACCESS_VIOLATION,
							"MMS confirmed error: class: \"access\", error code: \"object-access-denied\"");
				}
				else if (errClass.access.val == 2) {

					throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
							"MMS confirmed error: class: \"access\", error code: \"object-non-existent\"");
				}
			}
		}

		if (mmsResponsePdu.confirmedErrorPdu.serviceError.additionalDescription != null) {
			throw new ServiceError(ServiceError.UNKNOWN, "MMS confirmed error. Description: "
					+ mmsResponsePdu.confirmedErrorPdu.serviceError.additionalDescription.toString());
		}
		throw new ServiceError(ServiceError.UNKNOWN, "MMS confirmed error.");
	}

	private static void testForRejectResponse(MmsPdu mmsResponsePdu) throws ServiceError {
		if (mmsResponsePdu.rejectPdu == null) {
			return;
		}

		SubChoice_rejectReason rejectReason = mmsResponsePdu.rejectPdu.rejectReason;
		if (rejectReason != null) {
			if (rejectReason.pdu_error != null) {
				if (rejectReason.pdu_error.val == 1) {
					throw new ServiceError(ServiceError.PARAMETER_VALUE_INCONSISTENT,
							"MMS reject: type: \"pdu-error\", reject code: \"invalid-pdu\"");
				}
			}
		}
		throw new ServiceError(ServiceError.UNKNOWN, "MMS confirmed error.");
	}

	private static void testForInitiateErrorResponse(MmsPdu mmsResponsePdu) throws ServiceError {
		if (mmsResponsePdu.initiateErrorPdu != null) {

			SubChoice_errorClass errClass = mmsResponsePdu.initiateErrorPdu.errorClass;
			if (errClass != null) {
				if (errClass.vmd_state != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"vmd_state\" with val: " + errClass.vmd_state.val);
				}
				if (errClass.application_reference != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"application_reference\" with val: " + errClass.application_reference.val);
				}
				if (errClass.definition != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"definition\" with val: " + errClass.definition.val);
				}
				if (errClass.resource != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"resource\" with val: " + errClass.resource.val);
				}
				if (errClass.service != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"service\" with val: " + errClass.service.val);
				}
				if (errClass.service_preempt != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"service_preempt\" with val: " + errClass.service_preempt.val);
				}
				if (errClass.time_resolution != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"time_resolution\" with val: " + errClass.time_resolution.val);
				}
				if (errClass.access != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"access\" with val: " + errClass.access.val);
				}
				if (errClass.initiate != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"initiate\" with val: " + errClass.initiate.val);
				}
				if (errClass.conclude != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"conclude\" with val: " + errClass.conclude.val);
				}
				if (errClass.cancel != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"cancel\" with val: " + errClass.cancel.val);
				}
				if (errClass.file != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"file\" with val: " + errClass.file.val);
				}
				if (errClass.others != null) {
					throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
							"error class \"others\" with val: " + errClass.others.val);
				}
			}

			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT, "unknown error class");
		}
	}

	private ConfirmedServiceResponse encodeWriteReadDecode(ConfirmedServiceRequest serviceRequest) throws ServiceError,
			IOException {

		int currentInvokeId = getInvokeId();

		ConfirmedRequestPdu confirmedRequestPdu = new ConfirmedRequestPdu(new BerInteger(currentInvokeId),
				serviceRequest);
		MmsPdu requestPdu = new MmsPdu(confirmedRequestPdu, null, null, null, null, null, null, null, null);

		berOStream.reset();

		try {
			requestPdu.encode(berOStream, true);
		} catch (Exception e) {
			IOException e2 = new IOException("Error encoding MmsPdu.", e);
			clientReceiver.close(e2);
			throw e2;
		}

		clientReceiver.setResponseExpected(currentInvokeId);
		try {
			acseAssociation.send(berOStream.getByteBuffer());
		} catch (IOException e) {
			IOException e2 = new IOException("Error sending packet.", e);
			clientReceiver.close(e2);
			throw e2;
		}

		MmsPdu decodedResponsePdu = null;

		try {
			if (responseTimeout == 0) {
				decodedResponsePdu = incomingResponses.take();
			}
			else {
				decodedResponsePdu = incomingResponses.poll(responseTimeout, TimeUnit.MILLISECONDS);
			}
		} catch (InterruptedException e) {
		}

		if (decodedResponsePdu == null) {
			decodedResponsePdu = clientReceiver.removeExpectedResponse();
			if (decodedResponsePdu == null) {
				throw new ServiceError(ServiceError.TIMEOUT);
			}
		}

		if (decodedResponsePdu.confirmedRequestPdu != null) {
			incomingResponses.add(decodedResponsePdu);
			throw clientReceiver.getLastIOException();
		}

		testForInitiateErrorResponse(decodedResponsePdu);
		testForErrorResponse(decodedResponsePdu);
		testForRejectResponse(decodedResponsePdu);

		ConfirmedResponsePdu confirmedResponsePdu = decodedResponsePdu.confirmedResponsePdu;
		if (confirmedResponsePdu == null) {
			throw new IllegalStateException("Response PDU is not a confirmed response pdu");
		}

		return confirmedResponsePdu.confirmedServiceResponse;

	}

	private void associate(InetAddress address, int port, InetAddress localAddr, int localPort,
			String authenticationParameter, ClientAcseSap acseSap, int proposedMaxPduSize,
			int proposedMaxServOutstandingCalling, int proposedMaxServOutstandingCalled,
			int proposedDataStructureNestingLevel, byte[] servicesSupportedCalling) throws IOException {

		MmsPdu initiateRequestMMSpdu = constructInitRequestPdu(proposedMaxPduSize, proposedMaxServOutstandingCalling,
				proposedMaxServOutstandingCalled, proposedDataStructureNestingLevel, servicesSupportedCalling);

		BerByteArrayOutputStream berOStream = new BerByteArrayOutputStream(500, true);
		initiateRequestMMSpdu.encode(berOStream, true);

		try {
			acseAssociation = acseSap.associate(address, port, localAddr, localPort, authenticationParameter,
					berOStream.getByteBuffer());

			ByteBuffer initResponse = acseAssociation.getAssociateResponseAPdu();

			MmsPdu initiateResponseMmsPdu = new MmsPdu();

			initiateResponseMmsPdu.decode(new ByteBufferInputStream(initResponse), null);

			handleInitiateResponse(initiateResponseMmsPdu, proposedMaxPduSize, proposedMaxServOutstandingCalling,
					proposedMaxServOutstandingCalled, proposedDataStructureNestingLevel);
		} catch (IOException e) {
			if (acseAssociation != null) {
				acseAssociation.close();
			}
			throw e;
		}
	}

	private static MmsPdu constructInitRequestPdu(int proposedMaxPduSize, int proposedMaxServOutstandingCalling,
			int proposedMaxServOutstandingCalled, int proposedDataStructureNestingLevel, byte[] servicesSupportedCalling) {

		InitRequestDetail initRequestDetail = new InitRequestDetail(version, proposedParameterCbbBitString,
				new BerBitString(servicesSupportedCalling, 85));

		InitiateRequestPdu initiateRequestPdu = new InitiateRequestPdu(new BerInteger(proposedMaxPduSize),
				new BerInteger(proposedMaxServOutstandingCalling), new BerInteger(proposedMaxServOutstandingCalled),
				new BerInteger(proposedDataStructureNestingLevel), initRequestDetail);

		MmsPdu initiateRequestMMSpdu = new MmsPdu(null, null, null, null, null, initiateRequestPdu, null, null, null);

		return initiateRequestMMSpdu;
	}

	private void handleInitiateResponse(MmsPdu responsePdu, int proposedMaxPduSize,
			int proposedMaxServOutstandingCalling, int proposedMaxServOutstandingCalled,
			int proposedDataStructureNestingLevel) throws IOException {

		if (responsePdu.initiateErrorPdu != null) {
			throw new IOException("Got response error of class: " + responsePdu.initiateErrorPdu.errorClass);
		}

		if (responsePdu.initiateResponsePdu == null) {
			acseAssociation.disconnect();
			throw new IOException("Error decoding InitiateResponse Pdu");
		}

		InitiateResponsePdu initiateResponsePdu = responsePdu.initiateResponsePdu;

		if (initiateResponsePdu.localDetailCalled != null) {
			negotiatedMaxPduSize = (int) initiateResponsePdu.localDetailCalled.val;
		}

		int negotiatedMaxServOutstandingCalling = (int) initiateResponsePdu.negotiatedMaxServOutstandingCalling.val;
		int negotiatedMaxServOutstandingCalled = (int) initiateResponsePdu.negotiatedMaxServOutstandingCalled.val;

		int negotiatedDataStructureNestingLevel;
		if (initiateResponsePdu.negotiatedDataStructureNestingLevel != null) {
			negotiatedDataStructureNestingLevel = (int) initiateResponsePdu.negotiatedDataStructureNestingLevel.val;
		}
		else {
			negotiatedDataStructureNestingLevel = proposedDataStructureNestingLevel;
		}

		if (negotiatedMaxPduSize < ClientSap.MINIMUM_MMS_PDU_SIZE || negotiatedMaxPduSize > proposedMaxPduSize
				|| negotiatedMaxServOutstandingCalling > proposedMaxServOutstandingCalling
				|| negotiatedMaxServOutstandingCalling < 0
				|| negotiatedMaxServOutstandingCalled > proposedMaxServOutstandingCalled
				|| negotiatedMaxServOutstandingCalled < 0
				|| negotiatedDataStructureNestingLevel > proposedDataStructureNestingLevel
				|| negotiatedDataStructureNestingLevel < 0) {
			acseAssociation.disconnect();
			throw new IOException("Error negotiating parameters");
		}

		int version = (int) initiateResponsePdu.mmsInitResponseDetail.negotiatedVersionNumber.val;
		if (version != 1) {
			throw new IOException("Unsupported version number was negotiated.");
		}

		byte[] servicesSupported = initiateResponsePdu.mmsInitResponseDetail.servicesSupportedCalled.bitString;
		if ((servicesSupported[0] & 0x40) != 0x40) {
			throw new IOException("Obligatory services are not supported by the server.");
		}
	}

	/**
	 * Parses the given SCL File and returns the server model that is described by it. This function can be used instead
	 * of <code>retrieveModel</code> in order to get the server model that is needed to call the other ACSI services.
	 *
	 * @param sclFilePath
	 *            the path to the SCL file that is to be parsed.
	 * @return The ServerNode that is the root node of the complete server model.
	 * @throws SclParseException
	 *             if any kind of fatal error occurs in the parsing process.
	 */
	public ServerModel getModelFromSclFile(String sclFilePath) throws SclParseException {
		List<ServerSap> serverSaps = ServerSap.getSapsFromSclFile(sclFilePath);
		if (serverSaps == null || serverSaps.size() == 0) {
			throw new SclParseException("No AccessPoint found in SCL file.");
		}
		serverModel = serverSaps.get(0).serverModel;
		return serverModel;
	}

	/**
	 * Triggers all GetDirectory and GetDefinition ACSI services needed to get the complete server model. Because in MMS
	 * SubDataObjects cannot be distinguished from Constructed Data Attributes they will always be represented as
	 * Constructed Data Attributes in the returned model.
	 *
	 * @return the ServerModel that is the root node of the complete server model.
	 * @throws ServiceError
	 *             if a ServiceError occurs while calling any of the ASCI services.
	 * @throws IOException
	 *             if a fatal association error occurs. The association object will be closed and can no longer be used
	 *             after this exception is thrown.
	 */
	public ServerModel retrieveModel() throws ServiceError, IOException {

		List<String> ldNames = retrieveLogicalDevices();
		List<List<String>> lnNames = new ArrayList<List<String>>(ldNames.size());

		for (int i = 0; i < ldNames.size(); i++) {
			lnNames.add(retrieveLogicalNodeNames(ldNames.get(i)));
		}
		List<LogicalDevice> lds = new ArrayList<LogicalDevice>();
		for (int i = 0; i < ldNames.size(); i++) {
			List<LogicalNode> lns = new ArrayList<LogicalNode>();
			for (int j = 0; j < lnNames.get(i).size(); j++) {
				lns.add(retrieveDataDefinitions(new ObjectReference(ldNames.get(i) + "/" + lnNames.get(i).get(j))));
			}
			lds.add(new LogicalDevice(new ObjectReference(ldNames.get(i)), lns));
		}

		serverModel = new ServerModel(lds, null);

		updateDataSets();

		return serverModel;
	}

	private List<String> retrieveLogicalDevices() throws ServiceError, IOException {
		ConfirmedServiceRequest serviceRequest = constructGetServerDirectoryRequest();
		ConfirmedServiceResponse confirmedServiceResponse = encodeWriteReadDecode(serviceRequest);
		return decodeGetServerDirectoryResponse(confirmedServiceResponse);
	}

	private ConfirmedServiceRequest constructGetServerDirectoryRequest() {
		ObjectClass objectClass = new ObjectClass(new BerInteger(9));

		GetNameListRequest.SubChoice_objectScope objectScope = new GetNameListRequest.SubChoice_objectScope(
				new BerNull(), null, null);

		GetNameListRequest getNameListRequest = new GetNameListRequest(objectClass, objectScope, null);

		return new ConfirmedServiceRequest(getNameListRequest, null, null, null, null, null, null);

	}

	private List<String> decodeGetServerDirectoryResponse(ConfirmedServiceResponse confirmedServiceResponse)
			throws ServiceError {

		if (confirmedServiceResponse.getNameList == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"Error decoding Get Server Directory Response Pdu");
		}

		List<BerVisibleString> identifiers = confirmedServiceResponse.getNameList.listOfIdentifier.seqOf;
		ArrayList<String> objectRefs = new ArrayList<String>(); // ObjectReference[identifiers.size()];

		for (BerVisibleString identifier : identifiers) {
			objectRefs.add(identifier.toString());
		}

		return objectRefs;
	}

	private List<String> retrieveLogicalNodeNames(String ld) throws ServiceError, IOException {
		List<String> lns = new LinkedList<String>();
		String continueAfterRef = "";
		do {
			ConfirmedServiceRequest serviceRequest = constructGetDirectoryRequest(ld, continueAfterRef, true);
			ConfirmedServiceResponse confirmedServiceResponse = encodeWriteReadDecode(serviceRequest);
			continueAfterRef = decodeGetDirectoryResponse(confirmedServiceResponse, lns);

		} while (continueAfterRef != "");
		return lns;
	}

	private ConfirmedServiceRequest constructGetDirectoryRequest(String ldRef, String continueAfter,
			boolean logicalDevice) {

		ObjectClass objectClass = null;

		if (logicalDevice) {
			objectClass = new ObjectClass(new BerInteger(0));
		}
		else { // for data sets
			objectClass = new ObjectClass(new BerInteger(2));
		}

		GetNameListRequest getNameListRequest = null;

		SubChoice_objectScope objectScopeChoiceType = new SubChoice_objectScope(null, new BerVisibleString(ldRef), null);

		if (continueAfter != "") {
			getNameListRequest = new GetNameListRequest(objectClass, objectScopeChoiceType, new BerVisibleString(
					continueAfter));
		}
		else {
			getNameListRequest = new GetNameListRequest(objectClass, objectScopeChoiceType, null);
		}

		return new ConfirmedServiceRequest(getNameListRequest, null, null, null, null, null, null);

	}

	/**
	 * Decodes an MMS response which contains the structure of a LD and its LNs including names of DOs.
	 */
	private String decodeGetDirectoryResponse(ConfirmedServiceResponse confirmedServiceResponse, List<String> lns)
			throws ServiceError {

		if (confirmedServiceResponse.getNameList == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"decodeGetLDDirectoryResponse: Error decoding server response");
		}

		GetNameListResponse getNameListResponse = confirmedServiceResponse.getNameList;

		List<BerVisibleString> identifiers = getNameListResponse.listOfIdentifier.seqOf;

		if (identifiers.size() == 0) {
			throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
					"decodeGetLDDirectoryResponse: Instance not available");
		}

		BerVisibleString identifier = null;
		Iterator<BerVisibleString> it = identifiers.iterator();

		String idString;

		while (it.hasNext()) {
			identifier = it.next();
			idString = identifier.toString();

			if (idString.indexOf('$') == -1) {
				lns.add(idString);
			}
		}

		if (getNameListResponse.moreFollows != null && getNameListResponse.moreFollows.val == false) {
			return "";
		}
		else {
			return identifier.toString();
		}
	}

	private LogicalNode retrieveDataDefinitions(ObjectReference lnRef) throws ServiceError, IOException {
		ConfirmedServiceRequest serviceRequest = constructGetDataDefinitionRequest(lnRef);
		ConfirmedServiceResponse confirmedServiceResponse = encodeWriteReadDecode(serviceRequest);
		return decodeGetDataDefinitionResponse(confirmedServiceResponse, lnRef);
	}

	private ConfirmedServiceRequest constructGetDataDefinitionRequest(ObjectReference lnRef) {

		SubSeq_domain_specific domainSpec = null;

		domainSpec = new SubSeq_domain_specific(new BerVisibleString(lnRef.get(0)), new BerVisibleString(lnRef.get(1)));

		GetVariableAccessAttributesRequest getVariableAccessAttributesRequest = new GetVariableAccessAttributesRequest(
				new ObjectName(null, domainSpec, null));

		return new ConfirmedServiceRequest(null, null, null, getVariableAccessAttributesRequest, null, null, null);

	}

	private LogicalNode decodeGetDataDefinitionResponse(ConfirmedServiceResponse confirmedServiceResponse,
			ObjectReference lnRef) throws ServiceError {

		return DataDefinitionResParser.parseGetDataDefinitionResponse(confirmedServiceResponse, lnRef);
	}

	/**
	 * The implementation of the GetDataValues ACSI service. Will send an MMS read request for the given model node.
	 * After a successful return, the Basic Data Attributes of the passed model node will contain the values read. If
	 * one of the Basic Data Attributes cannot be read then none of the values will be read and a
	 * <code>ServiceError</code> will be thrown.
	 *
	 * @param modelNode
	 *            the functionally constrained model node that is to be read.
	 * @throws ServiceError
	 *             if a ServiceError is returned by the server.
	 * @throws IOException
	 *             if a fatal association error occurs. The association object will be closed and can no longer be used
	 *             after this exception is thrown.
	 */
	public void getDataValues(FcModelNode modelNode) throws ServiceError, IOException {
		ConfirmedServiceRequest serviceRequest = constructGetDataValuesRequest(modelNode);
		ConfirmedServiceResponse confirmedServiceResponse = encodeWriteReadDecode(serviceRequest);
		decodeGetDataValuesResponse(confirmedServiceResponse, modelNode);
	}

	/**
	 * Will update all data inside the model except for control variables (those that have FC=CO). Control variables are
	 * not meant to be read. Update is done by calling getDataValues on the FCDOs below the Logical Nodes.
	 *
	 * @throws ServiceError
	 *             if a ServiceError is returned by the server.
	 * @throws IOException
	 *             if a fatal association error occurs. The association object will be closed and can no longer be used
	 *             after this exception is thrown.
	 */
	public void getAllDataValues() throws ServiceError, IOException {
		for (ModelNode logicalDevice : serverModel.getChildren()) {
			for (ModelNode logicalNode : logicalDevice.getChildren()) {
				for (ModelNode dataObject : logicalNode.getChildren()) {
					FcModelNode fcdo = (FcModelNode) dataObject;
					if (fcdo.getFc() != Fc.CO) {
						getDataValues(fcdo);
					}
				}
			}
		}
	}

	private ConfirmedServiceRequest constructGetDataValuesRequest(FcModelNode modelNode) {
		List<VariableDef> listOfVariables = new ArrayList<VariableDef>(1);
		listOfVariables.add(modelNode.getMmsVariableDef());

		VariableAccessSpecification varAccessSpec = new VariableAccessSpecification(new SubSeqOf_listOfVariable(
				listOfVariables), null);

		ReadRequest readRequest = new ReadRequest(null, varAccessSpec);

		return new ConfirmedServiceRequest(null, readRequest, null, null, null, null, null);
	}

	private void decodeGetDataValuesResponse(ConfirmedServiceResponse confirmedServiceResponse, ModelNode modelNode)
			throws ServiceError {

		if (confirmedServiceResponse.read == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"Error decoding GetDataValuesReponsePdu");
		}

		List<AccessResult> listOfAccessResults = confirmedServiceResponse.read.listOfAccessResult.seqOf;

		if (listOfAccessResults.size() != 1) {
			throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE, "Multiple results received.");
		}

		AccessResult accRes = listOfAccessResults.get(0);

		if (accRes.failure != null) {
			throw mmsDataAccessErrorToServiceError(accRes.failure);
		}
		modelNode.setValueFromMmsDataObj(accRes.success);
	}

	/**
	 * The implementation of the SetDataValues ACSI service. Will send an MMS write request with the values of all Basic
	 * Data Attributes of the given model node. Will simply return if all values have been successfully written. If one
	 * of the Basic Data Attributes could not be written then a <code>ServiceError</code> will be thrown. In this case
	 * it is not possible to find out which of several Basic Data Attributes could not be written.
	 *
	 * @param modelNode
	 *            the functionally constrained model node that is to be written.
	 * @throws ServiceError
	 *             if a ServiceError is returned by the server.
	 * @throws IOException
	 *             if a fatal association error occurs. The association object will be closed and can no longer be used
	 *             after this exception is thrown.
	 */
	public void setDataValues(FcModelNode modelNode) throws ServiceError, IOException {
		ConfirmedServiceRequest serviceRequest = constructSetDataValuesRequest(modelNode);
		ConfirmedServiceResponse confirmedServiceResponse = encodeWriteReadDecode(serviceRequest);
		decodeSetDataValuesResponse(confirmedServiceResponse);
	}

	private ConfirmedServiceRequest constructSetDataValuesRequest(FcModelNode modelNode) throws ServiceError {
		List<VariableDef> listOfVariables = new ArrayList<VariableDef>(1);
		listOfVariables.add(modelNode.getMmsVariableDef());
		VariableAccessSpecification varAccessSpec = new VariableAccessSpecification(new SubSeqOf_listOfVariable(listOfVariables), null);

		List<Data> listOfData = new ArrayList<Data>(1);
		listOfData.add(modelNode.getMmsDataObj());

		WriteRequest writeRequest = new WriteRequest(varAccessSpec, new SubSeqOf_listOfData(listOfData));
		return new ConfirmedServiceRequest(null, null, writeRequest, null, null, null, null);
	}

	private void decodeSetDataValuesResponse(ConfirmedServiceResponse confirmedServiceResponse) throws ServiceError {

		WriteResponse writeResponse = confirmedServiceResponse.write;

		if (writeResponse == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"SetDataValuesResponse: improper response");
		}

		WriteResponse.SubChoice subChoice = writeResponse.seqOf.get(0);

		if (subChoice.failure != null) {
			throw mmsDataAccessErrorToServiceError(subChoice.failure);
		}
	}

	/**
	 * This function will get the definition of all persistent DataSets from the server and update the DataSets in the
	 * ServerModel that were returned by the retrieveModel() or getModelFromSclFile() functions. It will delete DataSets
	 * that have been deleted since the last update and add any new DataSets
	 *
	 * @throws ServiceError
	 *             if a ServiceError is returned by the server.
	 * @throws IOException
	 *             if a fatal association error occurs. The association object will be closed and can no longer be used
	 *             after this exception is thrown.
	 */
	public void updateDataSets() throws ServiceError, IOException {

		if (serverModel == null) {
			throw new IllegalStateException(
					"Before calling this function you have to get the ServerModel using the retrieveModel() function");
		}

		Collection<ModelNode> lds = serverModel.getChildren();

		for (ModelNode ld : lds) {
			ConfirmedServiceRequest serviceRequest = constructGetDirectoryRequest(ld.getName(), "", false);
			ConfirmedServiceResponse confirmedServiceResponse = encodeWriteReadDecode(serviceRequest);
			decodeAndRetrieveDsNamesAndDefinitions(confirmedServiceResponse, (LogicalDevice) ld);
		}
	}

	private void decodeAndRetrieveDsNamesAndDefinitions(ConfirmedServiceResponse confirmedServiceResponse,
			LogicalDevice ld) throws ServiceError, IOException {

		if (confirmedServiceResponse.getNameList == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"decodeGetDataSetResponse: Error decoding server response");
		}

		GetNameListResponse getNameListResponse = confirmedServiceResponse.getNameList;

		List<BerVisibleString> identifiers = getNameListResponse.listOfIdentifier.seqOf;

		if (identifiers.size() == 0) {
			return;
		}

		for (BerVisibleString identifier : identifiers) {
			// TODO delete DataSets that no longer exist
			getDataSetDirectory(identifier, ld);
		}

		if (getNameListResponse.moreFollows != null && getNameListResponse.moreFollows.val == true) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT);
		}
	}

	private void getDataSetDirectory(BerVisibleString dsId, LogicalDevice ld) throws ServiceError, IOException {
		ConfirmedServiceRequest serviceRequest = constructGetDataSetDirectoryRequest(dsId, ld);
		ConfirmedServiceResponse confirmedServiceResponse = encodeWriteReadDecode(serviceRequest);
		decodeGetDataSetDirectoryResponse(confirmedServiceResponse, dsId, ld);
	}

	private ConfirmedServiceRequest constructGetDataSetDirectoryRequest(BerVisibleString dsId, LogicalDevice ld)
			throws ServiceError {
		ObjectName dataSetObj = new ObjectName(null, new ObjectName.SubSeq_domain_specific(new BerVisibleString(ld
				.getName().getBytes()), dsId), null);
		return new ConfirmedServiceRequest(null, null, null, null, null, dataSetObj, null);
	}

	private void decodeGetDataSetDirectoryResponse(ConfirmedServiceResponse confirmedServiceResponse,
			BerVisibleString dsId, LogicalDevice ld) throws ServiceError {

		if (confirmedServiceResponse.getNamedVariableListAttributes == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"decodeGetDataSetDirectoryResponse: Error decoding server response");
		}

		GetNamedVariableListAttributesResponse getNamedVariableListAttResponse = confirmedServiceResponse.getNamedVariableListAttributes;
		boolean deletable = getNamedVariableListAttResponse.mmsDeletable.val;
		List<VariableDef> variables = getNamedVariableListAttResponse.listOfVariable.seqOf;

		if (variables.size() == 0) {
			throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
					"decodeGetDataSetDirectoryResponse: Instance not available");
		}

		List<FcModelNode> dsMems = new ArrayList<FcModelNode>();

		for (VariableDef variableDef : variables) {

			FcModelNode member;
			// TODO remove this try catch statement once all possible FCs are
			// supported
			// it is only there so that Functional Constraints such as GS will
			// be ignored and DataSet cotaining elements with these FCs are
			// ignored and not created.
			try {
				member = serverModel.getNodeFromVariableDef(variableDef);
			} catch (ServiceError e) {
				return;
			}
			if (member == null) {
				throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
						"decodeGetDataSetDirectoryResponse: data set memeber does not exist, you might have to call retrieveModel first");
			}
			dsMems.add(member);
		}

		String dsObjRef = ld.getName() + "/" + dsId.toString().replace('$', '.');

		DataSet dataSet = new DataSet(dsObjRef, dsMems, deletable);

		if (ld.getChild(dsId.toString().substring(0, dsId.toString().indexOf('$'))) == null) {
			throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
					"decodeGetDataSetDirectoryResponse: LN for returned DataSet is not available");
		}

		DataSet existingDs = serverModel.getDataSet(dsObjRef);
		if (existingDs == null) {
			serverModel.addDataSet(dataSet);
		}
		else if (!existingDs.isDeletable()) {
			return;
		}
		else {
			serverModel.removeDataSet(dsObjRef.toString());
			serverModel.addDataSet(dataSet);
		}

	}

	/**
	 * The client should create the data set first and add it to either the non-persistent list or to the model. Then it
	 * should call this method for creation on the server side
	 *
	 * @param dataSet
	 *            the data set to be created on the server side
	 * @throws ServiceError
	 *             if a ServiceError is returned by the server.
	 * @throws IOException
	 *             if a fatal IO error occurs. The association object will be closed and can no longer be used after
	 *             this exception is thrown.
	 */
	public void createDataSet(DataSet dataSet) throws ServiceError, IOException {
		ConfirmedServiceRequest serviceRequest = constructCreateDataSetRequest(dataSet);
		encodeWriteReadDecode(serviceRequest);
		handleCreateDataSetResponse(dataSet);
	}

	/**
	 * dsRef = either LD/LN.DataSetName (persistent) or @DataSetname (non-persistent) Names in dsMemberRef should be in
	 * the form: LD/LNName.DoName or LD/LNName.DoName.DaName
	 */
	private ConfirmedServiceRequest constructCreateDataSetRequest(DataSet dataSet) throws ServiceError {
		List<VariableDef> listOfVariable = new LinkedList<VariableDef>();

		for (FcModelNode dsMember : dataSet) {
			listOfVariable.add(dsMember.getMmsVariableDef());
		}

		DefineNamedVariableListRequest.SubSeqOf_listOfVariable seqOf = new DefineNamedVariableListRequest.SubSeqOf_listOfVariable(
				listOfVariable);
		DefineNamedVariableListRequest createDSRequest = new DefineNamedVariableListRequest(dataSet.getMmsObjectName(),
				seqOf);

		return new ConfirmedServiceRequest(null, null, null, null, createDSRequest, null, null);

	}

	private void handleCreateDataSetResponse(DataSet dataSet) throws ServiceError {
		serverModel.addDataSet(dataSet);
	}

	public void deleteDataSet(DataSet dataSet) throws ServiceError, IOException {
		ConfirmedServiceRequest serviceRequest = constructDeleteDataSetRequest(dataSet);
		ConfirmedServiceResponse confirmedServiceResponse = encodeWriteReadDecode(serviceRequest);
		decodeDeleteDataSetResponse(confirmedServiceResponse, dataSet);
	}

	private ConfirmedServiceRequest constructDeleteDataSetRequest(DataSet dataSet) throws ServiceError {
		List<ObjectName> listOfVariableListName = new ArrayList<ObjectName>(1);
		listOfVariableListName.add(dataSet.getMmsObjectName());

		DeleteNamedVariableListRequest requestDeleteDS = new DeleteNamedVariableListRequest(null,
				new SubSeqOf_listOfVariableListName(listOfVariableListName), null);

		return new ConfirmedServiceRequest(null, null, null, null, null, null, requestDeleteDS);
	}

	private void decodeDeleteDataSetResponse(ConfirmedServiceResponse confirmedServiceResponse, DataSet dataSet)
			throws ServiceError {

		if (confirmedServiceResponse.deleteNamedVariableList == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"decodeDeleteDataSetResponse: Error decoding server response");
		}

		DeleteNamedVariableListResponse deleteNamedVariableListResponse = confirmedServiceResponse.deleteNamedVariableList;

		if (deleteNamedVariableListResponse.numberDeleted.val != 1) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT, "number deleted not 1");
		}

		if (serverModel.removeDataSet(dataSet.getReferenceStr()) == null) {
			throw new ServiceError(ServiceError.UNKNOWN, "unable to delete dataset locally");
		}

	}

	/**
	 * The implementation of the GetDataSetValues ACSI service. After a successful return, the Basic Data Attributes of
	 * the data set members will contain the values read. If one of the data set members could not be read, this will be
	 * indicated in the returned list. The returned list will have the same size as the member list of the data set. For
	 * each member it will contain <code>null</code> if reading was successful and a ServiceError if reading of this
	 * member failed.
	 *
	 * @param dataSet
	 *            the DataSet that is to be read.
	 * @return a list indicating ServiceErrors that may have occurred.
	 * @throws IOException
	 *             if a fatal IO error occurs. The association object will be closed and can no longer be used after
	 *             this exception is thrown.
	 */
	public List<ServiceError> getDataSetValues(DataSet dataSet) throws IOException {

		ConfirmedServiceResponse confirmedServiceResponse;
		try {
			ConfirmedServiceRequest serviceRequest = constructGetDataSetValuesRequest(dataSet);
			confirmedServiceResponse = encodeWriteReadDecode(serviceRequest);
		} catch (ServiceError e) {
			int dataSetSize = dataSet.getMembers().size();
			List<ServiceError> serviceErrors = new ArrayList<ServiceError>(dataSetSize);
			for (int i = 0; i < dataSetSize; i++) {
				serviceErrors.add(e);
			}
			return serviceErrors;
		}
		return decodeGetDataSetValuesResponse(confirmedServiceResponse, dataSet);
	}

	private ConfirmedServiceRequest constructGetDataSetValuesRequest(DataSet dataSet) throws ServiceError {

		VariableAccessSpecification varAccSpec = new VariableAccessSpecification(null, dataSet.getMmsObjectName());
		ReadRequest getDataSetValuesRequest = new ReadRequest(new BerBoolean(true), varAccSpec);
		return new ConfirmedServiceRequest(null, getDataSetValuesRequest, null, null, null, null, null);

	}

	private List<ServiceError> decodeGetDataSetValuesResponse(ConfirmedServiceResponse confirmedServiceResponse,
			DataSet ds) {

		int dataSetSize = ds.getMembers().size();
		List<ServiceError> serviceErrors = new ArrayList<ServiceError>(dataSetSize);

		if (confirmedServiceResponse.read == null) {
			ServiceError serviceError = new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"Error decoding GetDataValuesReponsePdu");
			for (int i = 0; i < dataSetSize; i++) {
				serviceErrors.add(serviceError);
			}
			return serviceErrors;
		}

		ReadResponse readResponse = confirmedServiceResponse.read;
		List<AccessResult> listOfAccessResults = readResponse.listOfAccessResult.seqOf;

		if (listOfAccessResults.size() != ds.getMembers().size()) {
			ServiceError serviceError = new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE,
					"Number of AccessResults does not match the number of DataSet members.");
			for (int i = 0; i < dataSetSize; i++) {
				serviceErrors.add(serviceError);
			}
			return serviceErrors;
		}

		Iterator<AccessResult> accessResultIterator = listOfAccessResults.iterator();

		for (FcModelNode dsMember : ds) {
			AccessResult accessResult = accessResultIterator.next();
			if (accessResult.success != null) {
				try {
					dsMember.setValueFromMmsDataObj(accessResult.success);
				} catch (ServiceError e) {
					serviceErrors.add(e);
				}
				serviceErrors.add(null);
			}
			else {
				serviceErrors.add(mmsDataAccessErrorToServiceError(accessResult.failure));
			}
		}

		return serviceErrors;
	}

	public List<ServiceError> setDataSetValues(DataSet dataSet) throws ServiceError, IOException {
		ConfirmedServiceRequest serviceRequest = constructSetDataSetValues(dataSet);
		ConfirmedServiceResponse confirmedServiceResponse = encodeWriteReadDecode(serviceRequest);
		return decodeSetDataSetValuesResponse(confirmedServiceResponse);
	}

	private ConfirmedServiceRequest constructSetDataSetValues(DataSet dataSet) throws ServiceError {
		VariableAccessSpecification varAccessSpec = new VariableAccessSpecification(null, dataSet.getMmsObjectName());

		List<Data> listOfData = new ArrayList<Data>(dataSet.getMembers().size());

		for (ModelNode member : dataSet) {
			listOfData.add(member.getMmsDataObj());
		}

		WriteRequest writeRequest = new WriteRequest(varAccessSpec, new SubSeqOf_listOfData(listOfData));
		return new ConfirmedServiceRequest(null, null, writeRequest, null, null, null, null);
	}

	private List<ServiceError> decodeSetDataSetValuesResponse(ConfirmedServiceResponse confirmedServiceResponse)
			throws ServiceError {

		if (confirmedServiceResponse.write == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"Error decoding SetDataSetValuesReponsePdu");
		}

		WriteResponse writeResponse = confirmedServiceResponse.write;
		List<WriteResponse.SubChoice> writeResChoiceType = writeResponse.seqOf;
		List<ServiceError> serviceErrors = new ArrayList<ServiceError>(writeResChoiceType.size());

		for (WriteResponse.SubChoice accessResult : writeResChoiceType) {
			if (accessResult.success != null) {
				serviceErrors.add(null);
			}
			else {
				serviceErrors.add(mmsDataAccessErrorToServiceError(accessResult.failure));
			}
		}
		return serviceErrors;

	}

	public void getRcbValues(Rcb rcb) throws ServiceError, IOException {
		getDataValues(rcb);
	}

	public void reserveUrcb(Urcb urcb) throws ServiceError, IOException {
		BdaBoolean resvBda = urcb.getResv();
		resvBda.setValue(true);
		setDataValues(resvBda);
	}

	public void reserveBrcb(Brcb brcb, Short resvTime) throws ServiceError, IOException {
		BdaInt16 resvTmsBda = brcb.getResvTms();
		resvTmsBda.setValue(resvTime);
		setDataValues(resvTmsBda);
	}

	public void cancelUrcbReservation(Urcb urcb) throws ServiceError, IOException {
		BdaBoolean resvBda = urcb.getResv();
		resvBda.setValue(false);
		setDataValues(resvBda);
	}

	public void enableReporting(Rcb rcb) throws ServiceError, IOException {
		BdaBoolean rptEnaBda = rcb.getRptEna();
		rptEnaBda.setValue(true);
		setDataValues(rptEnaBda);
	}

	public void disableReporting(Rcb rcb) throws ServiceError, IOException {
		BdaBoolean rptEnaBda = rcb.getRptEna();
		rptEnaBda.setValue(false);
		setDataValues(rptEnaBda);
	}

	public void startGi(Rcb rcb) throws ServiceError, IOException {
		BdaBoolean rptGiBda = (BdaBoolean) rcb.getChild("GI");
		rptGiBda.setValue(true);
		setDataValues(rptGiBda);
	}

	/**
	 * Sets the selected values of the given report control block. Note that all these parameters may only be set if
	 * reporting for this report control block is not enabled and if it is not reserved by another client. The
	 * parameters PurgeBuf, EntryId are only applicable if the given rcb is of type BRCB.
	 *
	 * @param rcb
	 *            the report control block
	 * @param setRptId
	 *            whether to set the report ID
	 * @param setDatSet
	 *            whether to set the data set
	 * @param setOptFlds
	 *            whether to set the optional fields
	 * @param setBufTm
	 *            whether to set the buffer time
	 * @param setTrgOps
	 *            whether to set the trigger options
	 * @param setIntgPd
	 *            whether to set the integrity period
	 * @param setPurgeBuf
	 *            whether to set purge buffer
	 * @param setEntryId
	 *            whether to set the entry ID
	 * @return a list indicating ServiceErrors that may have occurred.
	 * @throws IOException
	 *             if a fatal IO error occurs. The association object will be closed and can no longer be used after
	 *             this exception is thrown.
	 */
	public List<ServiceError> setRcbValues(Rcb rcb, boolean setRptId, boolean setDatSet, boolean setOptFlds,
			boolean setBufTm, boolean setTrgOps, boolean setIntgPd, boolean setPurgeBuf, boolean setEntryId)
			throws IOException {

		List<FcModelNode> parametersToSet = new ArrayList<FcModelNode>(6);

		if (setRptId == true) {
			parametersToSet.add(rcb.getRptId());
		}
		if (setDatSet == true) {
			parametersToSet.add(rcb.getDatSet());
		}
		if (setOptFlds == true) {
			parametersToSet.add(rcb.getOptFlds());
		}
		if (setBufTm == true) {
			parametersToSet.add(rcb.getBufTm());
		}
		if (setTrgOps == true) {
			parametersToSet.add(rcb.getTrgOps());
		}
		if (setIntgPd == true) {
			parametersToSet.add(rcb.getIntgPd());
		}
		if (rcb instanceof Brcb) {
			Brcb brcb = (Brcb) rcb;
			if (setPurgeBuf == true) {
				parametersToSet.add(brcb.getPurgeBuf());
			}
			if (setEntryId == true) {
				parametersToSet.add(brcb.getEntryId());
			}
			// if (setResvTms == true) {
			// if (brcb.getResvTms() != null) {
			// parametersToSet.add(brcb.getResvTms());
			// }
			// }
		}

		List<ServiceError> serviceErrors = new ArrayList<ServiceError>(parametersToSet.size());

		for (FcModelNode child : parametersToSet) {
			try {
				setDataValues(child);
				serviceErrors.add(null);
			} catch (ServiceError e) {
				serviceErrors.add(e);
			}
		}

		return serviceErrors;
	}

	private Report processReport(MmsPdu mmsPdu) throws ServiceError {

		if (mmsPdu.unconfirmedPdu == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"getReport: Error decoding server response");
		}

		UnconfirmedPdu unconfirmedRes = mmsPdu.unconfirmedPdu;

		if (unconfirmedRes.unconfirmedService == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"getReport: Error decoding server response");
		}

		UnconfirmedService unconfirmedServ = unconfirmedRes.unconfirmedService;

		if (unconfirmedServ.informationReport == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"getReport: Error decoding server response");
		}

		List<AccessResult> listRes = unconfirmedServ.informationReport.listOfAccessResult.seqOf;
		int index = 0;

		if (listRes.get(index).success.visible_string == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"processReport: report does not contain RptID");
		}

		String rptId;
		BdaOptFlds optFlds;
		Integer sqNum = null;
		Integer subSqNum = null;
		boolean moreSegmentsFollow = false;
		String dataSetRef = null;
		boolean bufOvfl = false;
		Long confRev = null;
		BdaEntryTime timeOfEntry = null;
		BdaOctetString entryId = null;
		byte[] inclusionBitString;
		DataSet dataSet = null;

		rptId = listRes.get(index++).success.visible_string.toString();

		if (listRes.get(index).success.bit_string == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"processReport: report does not contain OptFlds");
		}

		optFlds = new BdaOptFlds(new ObjectReference("none"));
		optFlds.setValue(listRes.get(index++).success.bit_string.bitString);

		if (optFlds.isSequenceNumber()) {
			sqNum = (int) listRes.get(index++).success.unsigned.val;
		}

		if (optFlds.isReportTimestamp()) {
			timeOfEntry = new BdaEntryTime(new ObjectReference("none"), null, "", false, false);
			timeOfEntry.setValueFromMmsDataObj(listRes.get(index++).success);
		}

		if (optFlds.isDataSetName()) {
			dataSetRef = (listRes.get(index++).success.visible_string.toString());
		}

		if (optFlds.isBufferOverflow()) {
			bufOvfl = (listRes.get(index++).success.boolean_.val);
		}

		if (optFlds.isEntryId()) {
			entryId = new BdaOctetString(new ObjectReference("none"), null, "", 8, false, false);
			entryId.setValue(listRes.get(index++).success.octet_string.octetString);
		}

		if (optFlds.isConfigRevision()) {
			confRev = listRes.get(index++).success.unsigned.val;
		}

		if (optFlds.isSegmentation()) {
			subSqNum = (int) listRes.get(index++).success.unsigned.val;
			moreSegmentsFollow = listRes.get(index++).success.boolean_.val;
		}

		inclusionBitString = listRes.get(index++).success.bit_string.bitString;

		if (optFlds.isDataReference()) {
			// this is just to move the index to the right place
			// The next part will process the changes to the values
			// without the dataRefs
			for (int i = 0; i < inclusionBitString.length * 8; i++) {
				if ((inclusionBitString[i / 8] & (1 << (7 - i % 8))) == (1 << (7 - i % 8))) {
					index++;
				}
			}
		}

		if (dataSetRef == null) {
			for (Urcb urcb : serverModel.getUrcbs()) {
				if ((urcb.getRptId() != null && urcb.getRptId().toString().equals(rptId))
						|| urcb.getReference().toString().equals(rptId)) {
					dataSetRef = urcb.getDatSet().toString();
					break;
				}
			}
		}

		if (dataSetRef == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"unable to find URCB that matches the given RptID in the report.");
		}

		// updating of data set copy - original stays the same
		dataSet = serverModel.getDataSet(dataSetRef.replace('$', '.')).copy();
		int shiftNum = 0;
		for (ModelNode child : dataSet.getMembers()) {
			if ((inclusionBitString[shiftNum / 8] & (1 << (7 - shiftNum % 8))) == (1 << (7 - shiftNum % 8))) {

				AccessResult accessRes = listRes.get(index++);
				child.setValueFromMmsDataObj(accessRes.success);
			}
			shiftNum++;
		}

		List<BdaReasonForInclusion> reasonCodes = null;
		if (optFlds.isReasonForInclusion()) {
			reasonCodes = new ArrayList<BdaReasonForInclusion>(dataSet.getMembers().size());
			for (int i = 0; i < dataSet.getMembers().size(); i++) {

				if ((inclusionBitString[i / 8] & (1 << (7 - i % 8))) == (1 << (7 - i % 8))) {

					BdaReasonForInclusion reasonForInclusion = new BdaReasonForInclusion(null);

					reasonCodes.add(reasonForInclusion);

					byte[] reason = listRes.get(index++).success.bit_string.bitString;

					reasonForInclusion.setValue(reason);

				}

			}
		}

		return new Report(rptId, optFlds, sqNum, subSqNum, moreSegmentsFollow, dataSetRef, bufOvfl, confRev,
				timeOfEntry, entryId, inclusionBitString, reasonCodes, dataSet);

	}

	/**
	 * Performs the Select ACSI Service of the control model on the given controllable Data Object (DO). By selecting a
	 * controllable DO you can reserve it for exclusive control/operation. This service is only applicable if the
	 * ctlModel Data Attribute is set to "sbo-with-normal-security" (2).
	 *
	 * The selection is canceled in one of the following events:
	 * <ul>
	 * <li>The "Cancel" ACSI service is issued.</li>
	 * <li>The sboTimemout (select before operate timeout) runs out. If the given controlDataObject contains a
	 * sboTimeout Data Attribute it is possible to change the timeout after which the selection/reservation is
	 * automatically canceled by the server. Otherwise the timeout is a local issue of the server.</li>
	 * <li>The connection to the server is closed.</li>
	 * <li>An operate service failed because of some error</li>
	 * <li>The sboClass is set to "operate-once" then the selection is also canceled after a successful operate service.
	 * </li>
	 * </ul>
	 *
	 * @param controlDataObject
	 *            needs to be a controllable Data Object that contains a Data Attribute named "SBO".
	 * @return false if the selection/reservation was not successful (because it is already selected by another client).
	 *         Otherwise true is returned.
	 * @throws ServiceError
	 *             if a ServiceError is returned by the server.
	 * @throws IOException
	 *             if a fatal IO error occurs. The association object will be closed and can no longer be used after
	 *             this exception is thrown.
	 */
	public boolean select(FcModelNode controlDataObject) throws ServiceError, IOException {
		BdaVisibleString sbo;
		try {
			sbo = (BdaVisibleString) controlDataObject.getChild("SBO");
		} catch (Exception e) {
			throw new IllegalArgumentException("ModelNode needs to conain a child node named SBO in order to select");
		}

		getDataValues(sbo);

		if (sbo.getValue().length == 0) {
			return false;
		}
		return true;

	}

	/**
	 * Executes the Operate ACSI Service on the given controllable Data Object (DO). The following subnodes of the given
	 * control DO should be set according your needs before calling this function. (Note that you can probably leave
	 * most attributes with their default value):
	 * <ul>
	 * <li>Oper.ctlVal - has to be set to actual control value that is to be written using the operate service.</li>
	 * <li>Oper.operTm (type: BdaTimestamp) - is an optional sub data attribute of Oper (thus it may not exist). If it
	 * exists it can be used to set the timestamp when the operation shall be performed by the server. Thus the server
	 * will delay execution of the operate command until the given date is reached. Can be set to an empty byte array
	 * (new byte[0]) or null so that the server executes the operate command immediately. This is also the default.</li>
	 * <li>Oper.check (type: BdaCheck) is used to tell the server whether to perform the synchrocheck and
	 * interlockcheck. By default they are turned off.</li>
	 * <li>Oper.orign - contains the two data attributes orCat (origin category, type: BdaInt8) and orIdent (origin
	 * identifier, type BdaOctetString). Origin is optionally reflected in the status Data Attribute controlDO.origin.
	 * By reading this data attribute other clients can see who executed the last operate command. The default value for
	 * orCat is 0 ("not-supported") and the default value for orIdent is ""(the empty string).</li>
	 * <li>Oper.Test (BdaBoolean) - if true this operate command is sent for test purposes only. Default is false.</li>
	 * </ul>
	 *
	 * All other operate parameters are automatically handled by this function.
	 *
	 * @param controlDataObject
	 *            needs to be a controllable Data Object that contains a Data Attribute named "Oper".
	 * @throws ServiceError
	 *             if a ServiceError is returned by the server
	 * @throws IOException
	 *             if a fatal IO error occurs. The association object will be closed and can no longer be used after
	 *             this exception is thrown.
	 */
	public void operate(FcModelNode controlDataObject) throws ServiceError, IOException {
		ConstructedDataAttribute oper;
		try {
			oper = (ConstructedDataAttribute) controlDataObject.getChild("Oper");
		} catch (Exception e) {
			throw new IllegalArgumentException("ModelNode needs to conain a child node named \"Oper\".");
		}

		((BdaInt8U) oper.getChild("ctlNum")).setValue((short) 1);
		((BdaTimestamp) oper.getChild("T")).setDate(new Date(System.currentTimeMillis()));

		setDataValues(oper);
	}

	/**
	 * Will close the connection simply by closing the TCP socket.
	 */
	public void close() {
		clientReceiver.close(new IOException("Connection closed by client"));
	}

	/**
	 * Will send a disconnect request first and then close the TCP socket.
	 */
	public void disconnect() {
		clientReceiver.disconnect();
	}

}
