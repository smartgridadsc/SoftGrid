/*
 * Copyright 2011-14 Fraunhofer ISE
 *
 * This file is part of jOSIStack.
 * For more information visit http://www.openmuc.org
 *
 * jOSIStack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * jOSIStack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jOSIStack.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openmuc.josistack;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import it.illinois.adsc.ema.softgrid.common.ied.data.ParameterGenerator;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerAnyNoDecode;
import org.openmuc.jasn1.ber.types.BerBitString;
import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.BerObjectIdentifier;
import org.openmuc.jasn1.ber.types.BerOctetString;
import org.openmuc.jasn1.ber.types.string.BerGraphicString;
import org.openmuc.josistack.internal.acse.asn1.AARE_apdu;
import org.openmuc.josistack.internal.acse.asn1.AARQ_apdu;
import org.openmuc.josistack.internal.acse.asn1.ACSE_apdu;
import org.openmuc.josistack.internal.acse.asn1.AE_qualifier;
import org.openmuc.josistack.internal.acse.asn1.AP_title;
import org.openmuc.josistack.internal.acse.asn1.Associate_source_diagnostic;
import org.openmuc.josistack.internal.acse.asn1.Association_information;
import org.openmuc.josistack.internal.acse.asn1.Authentication_value;
import org.openmuc.josistack.internal.acse.asn1.Myexternal;
import org.openmuc.josistack.internal.presentation.asn1.CPA_PPDU;
import org.openmuc.josistack.internal.presentation.asn1.CP_type;
import org.openmuc.josistack.internal.presentation.asn1.Context_list;
import org.openmuc.josistack.internal.presentation.asn1.Fully_encoded_data;
import org.openmuc.josistack.internal.presentation.asn1.Mode_selector;
import org.openmuc.josistack.internal.presentation.asn1.PDV_list;
import org.openmuc.josistack.internal.presentation.asn1.Result_list;
import org.openmuc.josistack.internal.presentation.asn1.User_data;
import org.openmuc.jositransport.ClientTSap;
import org.openmuc.jositransport.TConnection;

public final class AcseAssociation {

	// private final static Logger logger = LoggerFactory.getLogger(AcseAssociation.class);

	private boolean connected = false;
	private TConnection tConnection;
	private ByteBuffer associateResponseAPDU = null;
	private Long currentCallingSessionSelector = null;
	private final BerOctetString pSelLocalBerOctetString;

	private static final Context_list context_list = new Context_list(new byte[] { (byte) 0x23, (byte) 0x30,
			(byte) 0x0f, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0x06, (byte) 0x04, (byte) 0x52, (byte) 0x01,
			(byte) 0x00, (byte) 0x01, (byte) 0x30, (byte) 0x04, (byte) 0x06, (byte) 0x02, (byte) 0x51, (byte) 0x01,
			(byte) 0x30, (byte) 0x10, (byte) 0x02, (byte) 0x01, (byte) 0x03, (byte) 0x06, (byte) 0x05, (byte) 0x28,
			(byte) 0xca, (byte) 0x22, (byte) 0x02, (byte) 0x01, (byte) 0x30, (byte) 0x04, (byte) 0x06, (byte) 0x02,
			(byte) 0x51, (byte) 0x01 });

	private static final BerInteger acsePresentationContextId = new BerInteger(new byte[] { (byte) 0x01, (byte) 0x01 });
	private static final Mode_selector normalModeSelector = new Mode_selector(new BerInteger(1));

	private static final Result_list presentationResultList = new Result_list(new byte[] { (byte) 0x12, (byte) 0x30,
			(byte) 0x07, (byte) 0x80, (byte) 0x01, (byte) 0x00, (byte) 0x81, (byte) 0x02, (byte) 0x51, (byte) 0x01,
			(byte) 0x30, (byte) 0x07, (byte) 0x80, (byte) 0x01, (byte) 0x00, (byte) 0x81, (byte) 0x02, (byte) 0x51,
			(byte) 0x01 });

	private static final BerInteger aareAccepted = new BerInteger(new byte[] { (byte) 0x01, (byte) 0x00 });

	private static final Associate_source_diagnostic associateSourceDiagnostic = new Associate_source_diagnostic(
			new byte[] { (byte) 0xa1, (byte) 0x03, (byte) 0x02, (byte) 0x01, (byte) 0x00 });

	// is always equal to 1.0.9506.2.3 (MMS)
	private static final BerObjectIdentifier application_context_name = new BerObjectIdentifier(new byte[] {
			(byte) 0x05, (byte) 0x28, (byte) 0xca, (byte) 0x22, (byte) 0x02, (byte) 0x03 });

	private static final BerObjectIdentifier directReference = new BerObjectIdentifier(new byte[] { (byte) 0x02,
			(byte) 0x51, (byte) 0x01 });
	private static final BerInteger indirectReference = new BerInteger(new byte[] { (byte) 0x01, (byte) 0x03 });

	private static final BerObjectIdentifier default_mechanism_name = new BerObjectIdentifier(new byte[] { 0x03, 0x52,
			0x03, 0x01 });
	private ParameterGenerator parameterGenerator;

	AcseAssociation(TConnection tConnection, byte[] pSelLocal) {
		this.tConnection = tConnection;
		pSelLocalBerOctetString = new BerOctetString(pSelLocal);
	}

	/**
	 * A server that got an Association Request Indication may use this function to accept the association.
	 * 
	 * @param payload
	 *            the payload to send with the accept message
	 * @throws IOException
	 *             if an error occures accepting the association
	 */
	public void accept(ByteBuffer payload) throws IOException {

		int payloadLength = payload.limit() - payload.position();

		Myexternal.SubChoice_encoding encoding = new Myexternal.SubChoice_encoding(new BerAnyNoDecode(payloadLength),
				null, null);

		Myexternal myExternal = new Myexternal(directReference, indirectReference, encoding);

		List<Myexternal> externalList = new ArrayList<Myexternal>(1);
		externalList.add(myExternal);

		Association_information userInformation = new Association_information(externalList);

		AARE_apdu aare = new AARE_apdu(null, application_context_name, aareAccepted, associateSourceDiagnostic, null,
				null, null, null, null, null, null, null, null, userInformation);

		ACSE_apdu acse = new ACSE_apdu(null, aare, null, null);

		BerByteArrayOutputStream berOStream = new BerByteArrayOutputStream(100, true);
		acse.encode(berOStream, true);
		int acseHeaderLength = berOStream.buffer.length - (berOStream.index + 1);

		User_data userData = getPresentationUserDataField(acseHeaderLength + payloadLength);
		CPA_PPDU.SubSeq_normal_mode_parameters normalModeParameters = new CPA_PPDU.SubSeq_normal_mode_parameters(null,
				pSelLocalBerOctetString, presentationResultList, null, null, userData);

		CPA_PPDU cpaPPdu = new CPA_PPDU(normalModeSelector, normalModeParameters);

		cpaPPdu.encode(berOStream, true);

		List<byte[]> ssduList = new LinkedList<byte[]>();
		List<Integer> ssduOffsets = new LinkedList<Integer>();
		List<Integer> ssduLengths = new LinkedList<Integer>();

		ssduList.add(berOStream.buffer);
		ssduOffsets.add(berOStream.index + 1);
		ssduLengths.add(berOStream.buffer.length - (berOStream.index + 1));

		ssduList.add(payload.array());
		ssduOffsets.add(payload.arrayOffset() + payload.position());
		ssduLengths.add(payloadLength);

		writeSessionAccept(ssduList, ssduOffsets, ssduLengths);

		connected = true;
	}

	private void writeSessionAccept(List<byte[]> ssdu, List<Integer> ssduOffsets, List<Integer> ssduLengths)
			throws IOException {
		byte[] sduAcceptHeader = new byte[20];
		int idx = 0;

		int ssduLength = 0;
		for (int ssduElementLength : ssduLengths) {
			ssduLength += ssduElementLength;
		}

		// write ISO 8327-1 Header
		// SPDU Type: ACCEPT (14)
		sduAcceptHeader[idx++] = 0x0e;
		// Length: length of session user data + 22 ( header data after length
		// field )
		sduAcceptHeader[idx++] = (byte) ((ssduLength + 18) & 0xff);

		// -- start Connect Accept Item
		// Parameter type: Connect Accept Item (5)
		sduAcceptHeader[idx++] = 0x05;
		// Parameter length
		sduAcceptHeader[idx++] = 0x06;

		// Protocol options:
		// Parameter Type: Protocol Options (19)
		sduAcceptHeader[idx++] = 0x13;
		// Parameter length
		sduAcceptHeader[idx++] = 0x01;
		// flags: (.... ...0 = Able to receive extended concatenated SPDU:
		// False)
		sduAcceptHeader[idx++] = 0x00;

		// Version number:
		// Parameter type: Version Number (22)
		sduAcceptHeader[idx++] = 0x16;
		// Parameter length
		sduAcceptHeader[idx++] = 0x01;
		// flags: (.... ..1. = Protocol Version 2: True)
		sduAcceptHeader[idx++] = 0x02;
		// -- end Connect Accept Item

		// Session Requirement
		// Parameter type: Session Requirement (20)
		sduAcceptHeader[idx++] = 0x14;
		// Parameter length
		sduAcceptHeader[idx++] = 0x02;
		// flags: (.... .... .... ..1. = Duplex functional unit: True)
		sduAcceptHeader[idx++] = 0x00;
		sduAcceptHeader[idx++] = 0x02;

		// Called Session Selector
		// Parameter type: Called Session Selector (52)
		sduAcceptHeader[idx++] = 0x34;
		// Parameter length
		sduAcceptHeader[idx++] = 0x02;
		// Called Session Selector
		sduAcceptHeader[idx++] = 0x00;
		sduAcceptHeader[idx++] = 0x01;

		// Session user data
		// Parameter type: Session user data (193)
		sduAcceptHeader[idx++] = (byte) 0xc1;

		// Parameter length
		sduAcceptHeader[idx++] = (byte) ssduLength;

		ssdu.add(0, sduAcceptHeader);
		ssduOffsets.add(0, 0);
		ssduLengths.add(0, sduAcceptHeader.length);

		tConnection.send(ssdu, ssduOffsets, ssduLengths);

	}

	public ByteBuffer getAssociateResponseAPdu() {
		ByteBuffer returnBuffer = associateResponseAPDU;
		associateResponseAPDU = null;
		return returnBuffer;
	}

	/**
	 * Starts an Application Association by sending an association request and waiting for an association accept message
	 * 
	 * @param payload
	 *            payload that can be sent with the association request
	 * @param port
	 * @param address
	 * @param tSAP
	 * @param aeQualifierCalling
	 * @param aeQualifierCalled
	 * @param apTitleCalling
	 * @param apTitleCalled
	 * @throws IOException
	 */
	void startAssociation(ByteBuffer payload, InetAddress address, int port, InetAddress localAddr, int localPort,
			String authenticationParameter, byte[] sSelRemote, byte[] sSelLocal, byte[] pSelRemote, ClientTSap tSAP,
			int[] apTitleCalled, int[] apTitleCalling, int aeQualifierCalled, int aeQualifierCalling)
			throws IOException {
		if (connected == true) {
			throw new IOException();
		}

		int payloadLength = payload.limit() - payload.position();

		AP_title called_ap_title = new AP_title(new BerObjectIdentifier(apTitleCalled));
		AP_title calling_ap_title = new AP_title(new BerObjectIdentifier(apTitleCalling));

		AE_qualifier called_ae_qualifier = new AE_qualifier(new BerInteger(aeQualifierCalled));
		AE_qualifier calling_ae_qualifier = new AE_qualifier(new BerInteger(aeQualifierCalling));

		Myexternal.SubChoice_encoding encoding = new Myexternal.SubChoice_encoding(new BerAnyNoDecode(payloadLength),
				null, null);

		Myexternal myExternal = new Myexternal(directReference, indirectReference, encoding);

		List<Myexternal> externalList = new ArrayList<Myexternal>(1);
		externalList.add(myExternal);

		Association_information userInformation = new Association_information(externalList);

		BerBitString sender_acse_requirements = null;
		BerObjectIdentifier mechanism_name = null;
		Authentication_value authentication_value = null;
		if (authenticationParameter != null) {
			sender_acse_requirements = new BerBitString(new byte[] { (byte) 0x02, (byte) 0x07, (byte) 0x80 });
			mechanism_name = default_mechanism_name;
			authentication_value = new Authentication_value(new BerGraphicString(authenticationParameter.getBytes()),
					null, null);
		}

		AARQ_apdu aarq = new AARQ_apdu(null, application_context_name, called_ap_title, called_ae_qualifier, null,
				null, calling_ap_title, calling_ae_qualifier, null, null, sender_acse_requirements, mechanism_name,
				authentication_value, null, null, userInformation);
		ACSE_apdu acse = new ACSE_apdu(aarq, null, null, null);

		BerByteArrayOutputStream berOStream = new BerByteArrayOutputStream(200, true);
		acse.encode(berOStream, true);
		int acseHeaderLength = berOStream.buffer.length - (berOStream.index + 1);

		User_data userData = getPresentationUserDataField(acseHeaderLength + payloadLength);

		CP_type.SubSeq_normal_mode_parameters normalModeParameter = new CP_type.SubSeq_normal_mode_parameters(null,
				pSelLocalBerOctetString, new BerOctetString(pSelRemote), context_list, null, null, null, userData);

		CP_type cpType = new CP_type(normalModeSelector, normalModeParameter);

		cpType.encode(berOStream, true);

		List<byte[]> ssduList = new LinkedList<byte[]>();
		List<Integer> ssduOffsets = new LinkedList<Integer>();
		List<Integer> ssduLengths = new LinkedList<Integer>();

		ssduList.add(berOStream.buffer);
		ssduOffsets.add(berOStream.index + 1);
		ssduLengths.add(berOStream.buffer.length - (berOStream.index + 1));

		ssduList.add(payload.array());
		ssduOffsets.add(payload.arrayOffset() + payload.position());
		ssduLengths.add(payloadLength);

		ByteBuffer res = null;
		res = startSConnection(ssduList, ssduOffsets, ssduLengths, address, port, localAddr, localPort, tSAP,
				sSelRemote, sSelLocal);

		associateResponseAPDU = decodePConResponse(res);

	}

	private static ByteBuffer decodePConResponse(ByteBuffer ppdu) throws IOException {

		CPA_PPDU cpa_ppdu = new CPA_PPDU();
		InputStream iStream = new ByteBufferInputStream(ppdu);
		cpa_ppdu.decode(iStream, true);

		ACSE_apdu acseApdu = new ACSE_apdu();
		acseApdu.decode(iStream, null);

		return ppdu;

	}

	private static User_data getPresentationUserDataField(int userDataLength) {
		PDV_list.SubChoice_presentation_data_values presDataValues = new PDV_list.SubChoice_presentation_data_values(
				new BerAnyNoDecode(userDataLength), null, null);
		PDV_list pdvList = new PDV_list(null, acsePresentationContextId, presDataValues);
		List<PDV_list> pdvListList = new ArrayList<PDV_list>(1);
		pdvListList.add(pdvList);

		Fully_encoded_data fullyEncodedData = new Fully_encoded_data(pdvListList);

		User_data userData = new User_data(null, fullyEncodedData);
		return userData;
	}

	/**
	 * Starts a session layer connection, sends a CONNECT (CN), waits for a ACCEPT (AC) and throws an IOException if not
	 * successful
	 * 
	 * @throws IOException
	 */
	private ByteBuffer startSConnection(List<byte[]> ssduList, List<Integer> ssduOffsets, List<Integer> ssduLengths,
			InetAddress address, int port, InetAddress localAddr, int localPort, ClientTSap tSAP, byte[] sSelRemote,
			byte[] sSelLocal) throws IOException {
		if (connected == true) {
			throw new IOException();
		}

		byte[] spduHeader = new byte[24];
		int idx = 0;
		// byte[] res = null;

		int ssduLength = 0;
		for (int ssduElementLength : ssduLengths) {
			ssduLength += ssduElementLength;
		}

		// write ISO 8327-1 Header
		// SPDU Type: CONNECT (13)
		spduHeader[idx++] = 0x0d;
		// Length: length of session user data + 22 ( header data after
		// length field )
		spduHeader[idx++] = (byte) ((ssduLength + 22) & 0xff);

		// -- start Connect Accept Item
		// Parameter type: Connect Accept Item (5)
		spduHeader[idx++] = 0x05;
		// Parameter length
		spduHeader[idx++] = 0x06;

		// Protocol options:
		// Parameter Type: Protocol Options (19)
		spduHeader[idx++] = 0x13;
		// Parameter length
		spduHeader[idx++] = 0x01;
		// flags: (.... ...0 = Able to receive extended concatenated SPDU:
		// False)
		spduHeader[idx++] = 0x00;

		// Version number:
		// Parameter type: Version Number (22)
		spduHeader[idx++] = 0x16;
		// Parameter length
		spduHeader[idx++] = 0x01;
		// flags: (.... ..1. = Protocol Version 2: True)
		spduHeader[idx++] = 0x02;
		// -- end Connect Accept Item

		// Session Requirement
		// Parameter type: Session Requirement (20)
		spduHeader[idx++] = 0x14;
		// Parameter length
		spduHeader[idx++] = 0x02;
		// flags: (.... .... .... ..1. = Duplex functional unit: True)
		spduHeader[idx++] = 0x00;
		spduHeader[idx++] = 0x02;

		// Calling Session Selector
		// Parameter type: Calling Session Selector (51)
		spduHeader[idx++] = 0x33;
		// Parameter length
		spduHeader[idx++] = 0x02;
		// Calling Session Selector
		spduHeader[idx++] = sSelRemote[0];
		spduHeader[idx++] = sSelRemote[1];

		// Called Session Selector
		// Parameter type: Called Session Selector (52)
		spduHeader[idx++] = 0x34;
		// Parameter length
		spduHeader[idx++] = 0x02;
		// Called Session Selector
		spduHeader[idx++] = sSelLocal[0];
		spduHeader[idx++] = sSelLocal[1];

		// Session user data
		// Parameter type: Session user data (193)
		spduHeader[idx++] = (byte) 0xc1;
		// Parameter length
		spduHeader[idx++] = (byte) (ssduLength & 0xff);
		// write session user data

		ssduList.add(0, spduHeader);
		ssduOffsets.add(0, 0);
		ssduLengths.add(0, spduHeader.length);

		tConnection = tSAP.connectTo(address, port, localAddr, localPort);

		tConnection.send(ssduList, ssduOffsets, ssduLengths);

		// TODO how much should be allocated here?
		ByteBuffer pduBuffer = ByteBuffer.allocate(500);

		try {
			tConnection.receive(pduBuffer);
		} catch (TimeoutException e) {
			throw new IOException("ResponseTimeout waiting for connection response.", e);
		}
		idx = 0;

		// read ISO 8327-1 Header
		// SPDU Type: ACCEPT (14)
		byte spduType = pduBuffer.get();
		if (spduType != 0x0e) {
			throw new IOException("ISO 8327-1 header wrong SPDU type, expected ACCEPT (14), got "
					+ getSPDUTypeString(spduType) + " (" + spduType + ")");
		}
		pduBuffer.get(); // skip length byte

		parameter_loop: while (true) {
			// read parameter type
			int parameterType = pduBuffer.get() & 0xff;
			// read parameter length
			int parameterLength = pduBuffer.get() & 0xff;

			switch (parameterType) {
			// Connect Accept Item (5)
			case 0x05:
				int bytesToRead = parameterLength;
				while (bytesToRead > 0) {
					// read parameter type
					int ca_parameterType = pduBuffer.get();
					// read parameter length
					// int ca_parameterLength = res[idx++];
					pduBuffer.get();

					bytesToRead -= 2;

					switch (ca_parameterType & 0xff) {
					// Protocol Options (19)
					case 0x13:
						// flags: .... ...0 = Able to receive extended
						// concatenated SPDU: False
						byte protocolOptions = pduBuffer.get();
						if (protocolOptions != 0x00) {
							throw new IOException("SPDU Connect Accept Item/Protocol Options is " + protocolOptions
									+ ", expected 0");
						}

						bytesToRead--;
						break;
					// Version Number
					case 0x16:
						// flags .... ..1. = Protocol Version 2: True
						byte versionNumber = pduBuffer.get();
						if (versionNumber != 0x02) {
							throw new IOException("SPDU Connect Accept Item/Version Number is " + versionNumber
									+ ", expected 2");
						}

						bytesToRead--;
						break;
					default:
						throw new IOException("SPDU Connect Accept Item: parameter not implemented: "
								+ ca_parameterType);
					}
				}
				break;
			// Session Requirement (20)
			case 0x14:
				// flags: (.... .... .... ..1. = Duplex functional unit: True)
				long sessionRequirement = extractInteger(pduBuffer, parameterLength);
				if (sessionRequirement != 0x02) {
					throw new IOException("SPDU header parameter 'Session Requirement (20)' is " + sessionRequirement
							+ ", expected 2");

				}
				break;
			// Calling Session Selector (51)
			case 0x33:
				long css = extractInteger(pduBuffer, parameterLength);
				if (css != 0x01) {
					throw new IOException("SPDU header parameter 'Calling Session Selector (51)' is " + css
							+ ", expected 1");

				}
				break;
			// Called Session Selector (52)
			case 0x34:
				long calledSessionSelector = extractInteger(pduBuffer, parameterLength);
				if (calledSessionSelector != 0x01) {
					throw new IOException("SPDU header parameter 'Called Session Selector (52)' is "
							+ calledSessionSelector + ", expected 1");
				}
				break;
			// Session user data (193)
			case 0xc1:
				break parameter_loop;
			default:
				throw new IOException("SPDU header parameter type " + parameterType + " not implemented");
			}
		}

		// got correct ACCEPT (AC) from the server

		connected = true;

		return pduBuffer;
	}

	public void send(ByteBuffer payload) throws IOException {

		PDV_list pdv_list = new PDV_list(null, new BerInteger(3l), new PDV_list.SubChoice_presentation_data_values(
				new BerAnyNoDecode(payload.limit() - payload.position()), null, null));
		List<PDV_list> pdv_list_list = new ArrayList<PDV_list>();
		pdv_list_list.add(pdv_list);
		Fully_encoded_data fully_encoded_data = new Fully_encoded_data(pdv_list_list);
		User_data user_data = new User_data(null, fully_encoded_data);

		BerByteArrayOutputStream berOStream = new BerByteArrayOutputStream(200, true);
		user_data.encode(berOStream, true);

		List<byte[]> ssduList = new ArrayList<byte[]>();
		List<Integer> ssduOffsets = new LinkedList<Integer>();
		List<Integer> ssduLengths = new LinkedList<Integer>();

		ssduList.add(berOStream.buffer);
		ssduOffsets.add(berOStream.index + 1);
		ssduLengths.add(berOStream.buffer.length - (berOStream.index + 1));

		ssduList.add(payload.array());
		ssduOffsets.add(payload.arrayOffset() + payload.position());
		ssduLengths.add(payload.limit() - payload.position());

		sendSessionLayer(ssduList, ssduOffsets, ssduLengths);
	}

	private void sendSessionLayer(List<byte[]> ssduList, List<Integer> ssduOffsets, List<Integer> ssduLengths)
			throws IOException {

		byte[] spduHeader = new byte[4];
		// --write iso 8327-1 Header--
		// write SPDU Type: give tokens PDU
		spduHeader[0] = 0x01;
		// length 0
		spduHeader[1] = 0;
		// write SPDU Type: DATA TRANSFER (DT)
		spduHeader[2] = 0x01;
		// length 0
		spduHeader[3] = 0;

		ssduList.add(0, spduHeader);
		ssduOffsets.add(0, 0);
		ssduLengths.add(0, spduHeader.length);
		tConnection.send(ssduList, ssduOffsets, ssduLengths);

	}

	/**
	 * Listens for a new PDU and writes it into the given buffer. Decodes all ACSE and lower layer headers. The
	 * resulting buffer's position points to the beginning of the ACSE SDU. The limit will point to the byte after the
	 * last byte of the ACSE SDU.
	 * 
	 * @param pduBuffer
	 *            buffer to write the received pdu into
	 * @throws DecodingException
	 *             if a decoding error occurs
	 * @throws IOException
	 *             if a non recoverable error occurs. Afterwards the association should be closed by the user
	 * @throws TimeoutException
	 *             if a timeout occurs
	 */
	public void receive(ByteBuffer pduBuffer) throws DecodingException, IOException, TimeoutException {
		if (connected == false) {
			throw new IllegalStateException("ACSE Association not connected");
		}
		tConnection.receive(pduBuffer);

		int firstByte = pduBuffer.get();

		if (firstByte == 25) {
			// got an ABORT SPDU
			throw new EOFException("Received an ABORT SPDU");
		}

		// -- read ISO 8327-1 header
		// SPDU type: Give tokens PDU (1)
		if (firstByte != 0x01) {
			throw new DecodingException("SPDU header syntax errror: first SPDU type not 1");
		}
		// length
		if (pduBuffer.get() != 0) {
			throw new DecodingException("SPDU header syntax errror: first SPDU type length not 0");
		}
		// SPDU Type: DATA TRANSFER (DT) SPDU (1)
		if (pduBuffer.get() != 0x01) {
			throw new DecodingException("SPDU header syntax errror: second SPDU type not 1");
		}
		// length
		if (pduBuffer.get() != 0) {
			throw new DecodingException("SPDU header syntax errror: second SPDU type length not 0");
		}

		// decode PPDU header
		User_data user_data = new User_data();

		try {
			user_data.decode(new ByteBufferInputStream(pduBuffer), null);
		} catch (IOException e) {
			throw new DecodingException("error decoding PPDU header", e);
		}

	}

	/**
	 * Disconnects by sending a disconnect request at the Transport Layer and then closing the socket.
	 */
	public void disconnect() {
		connected = false;
		if (tConnection != null) {
			tConnection.disconnect();
		}
	}

	/**
	 * Closes the connection simply by closing the socket.
	 */
	public void close() {
		connected = false;
		if (tConnection != null) {
			tConnection.close();
		}
	}

	private long extractInteger(ByteBuffer buffer, int size) throws IOException {
		switch (size) {
		case 1:
			return buffer.get();
		case 2:
			return buffer.getShort();
		case 4:
			return buffer.getInt();
		case 8:
			return buffer.getLong();
		default:
			throw new IOException("invalid length for reading numeric value");
		}
	}

	void listenForCn(ByteBuffer pduBuffer) throws IOException, TimeoutException {
		if (connected == true) {
			throw new IllegalStateException("ACSE Association is already connected");
		}
		int parameter;
		int parameterLength;

		tConnection.receive(pduBuffer);
		// start reading ISO 8327-1 header
		// SPDU Type: CONNECT (CN) SPDU (13)
		byte spduType = pduBuffer.get();
		if (spduType != 0x0d) {
			throw new IOException("ISO 8327-1 header wrong SPDU type, expected CONNECT (13), got "
					+ getSPDUTypeString(spduType) + " (" + spduType + ")");
		}
		pduBuffer.get(); // skip lenght byte

		parameter_loop: while (true) {
			// read parameter code
			parameter = pduBuffer.get() & 0xff;
			// read parameter length
			parameterLength = pduBuffer.get() & 0xff;
			switch (parameter) {
			// Connect Accept Item (5)
			case 0x05:
				int bytesToRead = parameterLength;
				while (bytesToRead > 0) {
					// read parameter type
					int ca_parameterType = pduBuffer.get();
					// read parameter length
					pduBuffer.get();

					bytesToRead -= 2;

					switch (ca_parameterType & 0xff) {
					// Protocol Options (19)
					case 0x13:
						// flags: .... ...0 = Able to receive extended
						// concatenated SPDU: False
						byte protocolOptions = pduBuffer.get();
						if (protocolOptions != 0x00) {
							throw new IOException("SPDU Connect Accept Item/Protocol Options is " + protocolOptions
									+ ", expected 0");
						}

						bytesToRead--;
						break;
					// Version Number
					case 0x16:
						// flags .... ..1. = Protocol Version 2: True
						byte versionNumber = pduBuffer.get();
						if (versionNumber != 0x02) {
							throw new IOException("SPDU Connect Accept Item/Version Number is " + versionNumber
									+ ", expected 2");
						}

						bytesToRead--;
						break;
					default:
						throw new IOException("SPDU Connect Accept Item: parameter not implemented: "
								+ ca_parameterType);
					}
				}
				break;
			// Session Requirement (20)
			case 0x14:
				// flags: (.... .... .... ..1. = Duplex functional unit: True)
				long sessionRequirement = extractInteger(pduBuffer, parameterLength);
				if (sessionRequirement != 0x02) {
					throw new IOException("SPDU header parameter 'Session Requirement (20)' is " + sessionRequirement
							+ ", expected 2");
				}
				break;
			// Calling Session Selector (51)
			case 0x33:
				currentCallingSessionSelector = extractInteger(pduBuffer, parameterLength);
				break;
			// Called Session Selector (52)
			case 0x34:
				long calledSessionSelector = extractInteger(pduBuffer, parameterLength);
				if (calledSessionSelector != 0x01) {
					throw new IOException("SPDU header parameter 'Called Session Selector (52)' is "
							+ calledSessionSelector + ", expected 1");
				}
				break;
			// Session user data (193)
			case 0xc1:
				break parameter_loop;
			default:
				throw new IOException("SPDU header parameter type " + parameter + " not implemented");
			}

		}

		CP_type cpType = new CP_type();
		ByteBufferInputStream iStream = new ByteBufferInputStream(pduBuffer);
		cpType.decode(iStream, true);

		ACSE_apdu acseApdu = new ACSE_apdu();
		acseApdu.decode(iStream, null);
	}

	public int getMessageTimeout() {
		return tConnection.getMessageTimeout();
	}

	public void setMessageTimeout(int i) {
		tConnection.setMessageTimeout(i);
	}

	public static String getSPDUTypeString(byte spduType) {
		switch (spduType) {
		case 0:
			return "EXCEPTION REPORT (ER)";
		case 1:
			return "DATA TRANSFER (DT)";
		case 2:
			return "PLEASE TOKENS (PT)";
		case 5:
			return "EXPEDITED (EX)";
		case 7:
			return "PREPARE (PR)";
		case 8:
			return "NOT FINISHED (NF)";
		case 9:
			return "FINISH (FN)";
		case 10:
			return "DISCONNECT (DN)";
		case 12:
			return "REFUSE (RF)";
		case 13:
			return "CONNECT (CN)";
		case 14:
			return "ACCEPT (AC)";
		case 15:
			return "CONNECT DATA OVERFLOW (CDO)";
		case 16:
			return "OVERFLOW ACCEPT (OA)";
		case 21:
			return "GIVE TOKENS CONFIRM (GTC)";
		case 22:
			return "GIVE TOKENS ACK (GTA)";
		case 25:
			return "ABORT (AB)";
		case 26:
			return "ABORT ACCEPT (AA)";
		case 29:
			return "ACTIVITY RESUME (AR)";
		case 33:
			return "TYPED DATA (TD)";
		case 34:
			return "RESYNCHRONIZE ACK (RA)";
		case 41:
			return "MAJOR SYNC POINT (MAP)";
		case 42:
			return "MAJOR SYNC ACK (MAA)";
		case 45:
			return "ACTIVITY START (AS)";
		case 48:
			return "EXCEPTION DATA (ED)";
		case 49:
			return "MINOR SYNC POINT (MIP)";
		case 50:
			return "MINOR SYNC ACK (MIA)";
		case 53:
			return "RESYNCHRONIZE (RS)";
		case 57:
			return "ACTIVITY DISCARD (AD)";
		case 58:
			return "ACTIVITY DISCARD ACK (ADA)";
		case 61:
			return "CAPABILITY DATA (CD)";
		case 62:
			return "CAPABILITY DATA ACK (CDA)";
		case 64:
			return "UNIT DATA (UD)";
		default:
			return "<unknown SPDU type>";
		}
	}

	public void setParameterGenerator(ParameterGenerator parameterGenerator) {
		this.parameterGenerator = parameterGenerator;
	}

	public ParameterGenerator getParameterGenerator() {
		return parameterGenerator;
	}
}
