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
/* Copyright (C) 2016 Advanced Digital Science Centre

        * This file is part of Soft-Grid.
        * For more information visit https://www.illinois.adsc.com.sg/cybersage/
        *
        * Soft-Grid is free software: you can redistribute it and/or modify
        * it under the terms of the GNU General Public License as published by
        * the Free Software Foundation, either version 3 of the License, or
        * (at your option) any later version.
        *
        * Soft-Grid is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        * GNU General Public License for more details.
        *
        * You should have received a copy of the GNU General Public License
        * along with Soft-Grid.  If not, see <http://www.gnu.org/licenses/>.

        * @author Prageeth Mahendra Gunathilaka
*/
package org.openmuc.openiec61850;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

import it.illinois.adsc.ema.softgrid.common.IEDMessageCounter;
import it.illinois.adsc.ema.softgrid.common.ied.data.ParameterGenerator;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerBitString;
import org.openmuc.jasn1.ber.types.BerBoolean;
import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.BerNull;
import org.openmuc.jasn1.ber.types.string.BerVisibleString;
import org.openmuc.josistack.AcseAssociation;
import org.openmuc.josistack.ByteBufferInputStream;
import org.openmuc.josistack.DecodingException;
import org.openmuc.openiec61850.internal.mms.asn1.AccessResult;
import org.openmuc.openiec61850.internal.mms.asn1.ConfirmedErrorPdu;
import org.openmuc.openiec61850.internal.mms.asn1.ConfirmedRequestPdu;
import org.openmuc.openiec61850.internal.mms.asn1.ConfirmedResponsePdu;
import org.openmuc.openiec61850.internal.mms.asn1.ConfirmedServiceRequest;
import org.openmuc.openiec61850.internal.mms.asn1.ConfirmedServiceResponse;
import org.openmuc.openiec61850.internal.mms.asn1.Data;
import org.openmuc.openiec61850.internal.mms.asn1.DefineNamedVariableListRequest;
import org.openmuc.openiec61850.internal.mms.asn1.DeleteNamedVariableListRequest;
import org.openmuc.openiec61850.internal.mms.asn1.DeleteNamedVariableListResponse;
import org.openmuc.openiec61850.internal.mms.asn1.GetNameListRequest;
import org.openmuc.openiec61850.internal.mms.asn1.GetNameListResponse;
import org.openmuc.openiec61850.internal.mms.asn1.GetNameListResponse.SubSeqOf_listOfIdentifier;
import org.openmuc.openiec61850.internal.mms.asn1.GetNamedVariableListAttributesResponse;
import org.openmuc.openiec61850.internal.mms.asn1.GetNamedVariableListAttributesResponse.SubSeqOf_listOfVariable;
import org.openmuc.openiec61850.internal.mms.asn1.GetVariableAccessAttributesRequest;
import org.openmuc.openiec61850.internal.mms.asn1.GetVariableAccessAttributesResponse;
import org.openmuc.openiec61850.internal.mms.asn1.InitResponseDetail;
import org.openmuc.openiec61850.internal.mms.asn1.InitiateRequestPdu;
import org.openmuc.openiec61850.internal.mms.asn1.InitiateResponsePdu;
import org.openmuc.openiec61850.internal.mms.asn1.MmsPdu;
import org.openmuc.openiec61850.internal.mms.asn1.ObjectName;
import org.openmuc.openiec61850.internal.mms.asn1.ObjectName.SubSeq_domain_specific;
import org.openmuc.openiec61850.internal.mms.asn1.ReadRequest;
import org.openmuc.openiec61850.internal.mms.asn1.ReadResponse;
import org.openmuc.openiec61850.internal.mms.asn1.ReadResponse.SubSeqOf_listOfAccessResult;
import org.openmuc.openiec61850.internal.mms.asn1.ServiceError.SubChoice_errorClass;
import org.openmuc.openiec61850.internal.mms.asn1.StructComponent;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification.SubSeq_structure;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification.SubSeq_structure.SubSeqOf_components;
import org.openmuc.openiec61850.internal.mms.asn1.VariableAccessSpecification;
import org.openmuc.openiec61850.internal.mms.asn1.VariableDef;
import org.openmuc.openiec61850.internal.mms.asn1.WriteRequest;
import org.openmuc.openiec61850.internal.mms.asn1.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ServerAssociation {
    private AssociationDataMode associationDataMode = AssociationDataMode.INTEGRATED_DATA;
    private final static Logger logger = LoggerFactory.getLogger(ServerAssociation.class);

    private final static WriteResponse.SubChoice writeSuccess = new WriteResponse.SubChoice(null, new BerNull());

    private AcseAssociation acseAssociation = null;

    private int negotiatedMaxPduSize;
    private ByteBuffer pduBuffer;
    private final ServerSap serverSap;
    final ServerModel serverModel;

    private final BerByteArrayOutputStream berOStream = new BerByteArrayOutputStream(500, true);
    private boolean insertRef;
    private String continueAfter;

    ScheduledExecutorService executor = null;

    HashMap<String, DataSet> nonPersistentDataSets = new HashMap<String, DataSet>();

    List<FcModelNode> selects = new ArrayList<FcModelNode>();
    List<Urcb> rsvdURCBs = new ArrayList<Urcb>();

    private static String[] mmsFcs = {"MX", "ST", "CO", "CF", "DC", "SP", "SG", "RP", "LG", "BR", "GO", "GS", "SV",
            "SE", "EX", "SR", "OR", "BL"};

    public ServerAssociation(ServerSap serverSap) {
        this.serverSap = serverSap;
        serverModel = serverSap.serverModel;
        executor = Executors.newScheduledThreadPool(2);
    }

    public void handleNewAssociation(AcseAssociation acseAssociation, ByteBuffer associationRequest) {

        this.acseAssociation = acseAssociation;

        try {
            associate(acseAssociation, associationRequest);
        } catch (IOException e) {
            logger.warn("Error during association build up", e);
            return;
        }

        handleConnection();

    }

    private void associate(AcseAssociation acseAssociation, ByteBuffer associationRequest) throws IOException {

        MmsPdu mmsPdu = new MmsPdu();

        mmsPdu.decode(new ByteBufferInputStream(associationRequest), null);

        MmsPdu initiateResponseMmsPdu = constructAssociationResponsePdu(mmsPdu.initiateRequestPdu);

        initiateResponseMmsPdu.encode(berOStream, true);

        acseAssociation.accept(berOStream.getByteBuffer());

    }

    private MmsPdu constructAssociationResponsePdu(InitiateRequestPdu associationRequestMMSpdu) {

        negotiatedMaxPduSize = serverSap.getMaxMmsPduSize();

        if (associationRequestMMSpdu.localDetailCalling != null) {
            int proposedMaxMmsPduSize = (int) associationRequestMMSpdu.localDetailCalling.val;
            if (negotiatedMaxPduSize > proposedMaxMmsPduSize && proposedMaxMmsPduSize >= ServerSap.MINIMUM_MMS_PDU_SIZE) {
                negotiatedMaxPduSize = proposedMaxMmsPduSize;
            }
        }

        int negotiatedMaxServOutstandingCalling = serverSap.getProposedMaxServOutstandingCalling();
        int proposedMaxServOutstandingCalling = (int) associationRequestMMSpdu.proposedMaxServOutstandingCalling.val;

        if (negotiatedMaxServOutstandingCalling > proposedMaxServOutstandingCalling
                && proposedMaxServOutstandingCalling > 0) {
            negotiatedMaxServOutstandingCalling = proposedMaxServOutstandingCalling;
        }

        int negotiatedMaxServOutstandingCalled = serverSap.getProposedMaxServOutstandingCalled();
        int proposedMaxServOutstandingCalled = (int) associationRequestMMSpdu.proposedMaxServOutstandingCalled.val;

        if (negotiatedMaxServOutstandingCalled > proposedMaxServOutstandingCalled
                && proposedMaxServOutstandingCalled > 0) {
            negotiatedMaxServOutstandingCalled = proposedMaxServOutstandingCalled;
        }

        int negotiatedDataStructureNestingLevel = serverSap.getProposedDataStructureNestingLevel();

        if (associationRequestMMSpdu.proposedDataStructureNestingLevel != null) {
            int proposedDataStructureNestingLevel = (int) associationRequestMMSpdu.proposedDataStructureNestingLevel.val;
            if (negotiatedDataStructureNestingLevel > proposedDataStructureNestingLevel) {
                negotiatedDataStructureNestingLevel = proposedDataStructureNestingLevel;
            }
        }

        pduBuffer = ByteBuffer.allocate(negotiatedMaxPduSize + 500);

        byte[] negotiatedParameterCbbBitString = serverSap.cbbBitString;

        byte[] servicesSupportedCalledBitString = serverSap.servicesSupportedCalled;

        InitResponseDetail initRespDetail = new InitResponseDetail(new BerInteger(1), new BerBitString(
                negotiatedParameterCbbBitString, negotiatedParameterCbbBitString.length * 8 - 5), new BerBitString(
                servicesSupportedCalledBitString, servicesSupportedCalledBitString.length * 8 - 3));

        InitiateResponsePdu initRespPdu = new InitiateResponsePdu(new BerInteger(negotiatedMaxPduSize), new BerInteger(
                negotiatedMaxServOutstandingCalling), new BerInteger(negotiatedMaxServOutstandingCalled),
                new BerInteger(negotiatedDataStructureNestingLevel), initRespDetail);

        MmsPdu initiateResponseMMSpdu = new MmsPdu(null, null, null, null, null, null, initRespPdu, null, null);

        return initiateResponseMMSpdu;
    }

    private void handleConnection() {

        while (true) {

            MmsPdu mmsRequestPdu = listenForMmsRequest(acseAssociation);
            if (mmsRequestPdu == null) {
                return;
            }

            ConfirmedRequestPdu confirmedRequestPdu = mmsRequestPdu.confirmedRequestPdu;
            // Do not have to check whether confirmedRequestPdu is null because that was already done by
            // listenForMmsRequest()

            if (confirmedRequestPdu.invokeID == null) {
                // cannot respond with ServiceError because no InvokeID was received
                logger.warn("Got unexpected MMS PDU or no invokeID");
                continue;
            }
            int invokeId = (int) confirmedRequestPdu.invokeID.val;

            try {
                if (confirmedRequestPdu.confirmedServiceRequest == null) {
                    throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
                            "Got an invalid MMS packet: confirmedServiceRequest empty");
                }
                ConfirmedServiceRequest confirmedServiceRequest = confirmedRequestPdu.confirmedServiceRequest;
                ConfirmedServiceResponse confirmedServiceResponse = null;
                if (confirmedServiceRequest.getNameList != null) {
                    GetNameListRequest getNameListRequest = confirmedServiceRequest.getNameList;
                    GetNameListResponse response = null;
                    if (getNameListRequest.objectClass.basicObjectClass == null) {
                        throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
                                "Got an invalid MMS packet: ObjectClass was not selected in GetNameList request");
                    }
                    long basicObjectClass = getNameListRequest.objectClass.basicObjectClass.val;
                    if (basicObjectClass == 9) {
                        logger.debug("Got a GetServerDirectory (MMS GetNameList[DOMAIN]) request");
                        response = handleGetServerDirectoryRequest(getNameListRequest);
                    } else if (basicObjectClass == 0) {
                        logger.debug("Got a Get{LD|LN}Directory (MMS GetNameList[NAMED_VARIABLE]) request");
                        response = handleGetDirectoryRequest(getNameListRequest);
                    } else if (basicObjectClass == 2) {
                        logger.debug("Got a GetLogicalNodeDirectory[DataSet] (MMS GetNameList[NAMED_VARIABLE_LIST]) request");
                        response = handleGetDataSetNamesRequest(getNameListRequest);
                    }
                    confirmedServiceResponse = new ConfirmedServiceResponse(response, null, null, null, null, null, null);
                } else if (confirmedServiceRequest.getVariableAccessAttributes != null) {
                    logger.debug("Got a GetDataDirectory/GetDataDefinition (MMS GetVariableAccessAttributes) request");
                    GetVariableAccessAttributesResponse response = handleGetVariableAccessAttributesRequest(

                            confirmedServiceRequest.getVariableAccessAttributes);
                    confirmedServiceResponse = new ConfirmedServiceResponse(null, null, null, response, null, null, null);
                } else if (confirmedServiceRequest.read != null) {
                    // GetDataValues, GetDataSetValues, GetBRCBValues and GetURCBValues map to this
                    long start = System.nanoTime();
                    ReadResponse response = handleGetDataValuesRequest(confirmedServiceRequest.read);
                    IEDMessageCounter.logMessageReceived(associationDataMode.name(), System.nanoTime() - start);
                    confirmedServiceResponse = new ConfirmedServiceResponse(null, response, null, null, null, null, null);
                } else if (confirmedServiceRequest.write != null) {
//					logger.debug("Got a Write request");
                    long start = System.nanoTime();
                    WriteResponse response = handleSetDataValuesRequest(confirmedServiceRequest.write);
                    IEDMessageCounter.logMessageReceived(associationDataMode.name(), System.nanoTime() - start);
                    confirmedServiceResponse = new ConfirmedServiceResponse(null, null, response, null, null, null, null);
                }
                // for Data Sets
                else if (confirmedServiceRequest.defineNamedVariableList != null) {
                    logger.debug("Got a CreateDataSet request");
                    BerNull response = handleCreateDataSetRequest(confirmedServiceRequest.defineNamedVariableList);
                    confirmedServiceResponse = new ConfirmedServiceResponse(null, null, null, null, response, null, null);
                } else if (confirmedServiceRequest.getNamedVariableListAttributes != null) {
                    logger.debug("Got a GetDataSetDirectory request");
                    GetNamedVariableListAttributesResponse response = handleGetDataSetDirectoryRequest(confirmedServiceRequest.getNamedVariableListAttributes);
                    confirmedServiceResponse = new ConfirmedServiceResponse(null, null, null, null, null, response, null);
                } else if (confirmedServiceRequest.deleteNamedVariableList != null) {
                    logger.debug("Got a DeleteDataSet request");
                    DeleteNamedVariableListResponse response = handleDeleteDataSetRequest(confirmedServiceRequest.deleteNamedVariableList);
                    confirmedServiceResponse = new ConfirmedServiceResponse(null, null, null, null, null, null, response);
                } else {
                    throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT, "invalid MMS packet: unknown request type.");
                }

                ConfirmedResponsePdu confirmedResponsePdu = new ConfirmedResponsePdu(confirmedRequestPdu.invokeID, confirmedServiceResponse);
                MmsPdu mmsResponsePdu = new MmsPdu(null, confirmedResponsePdu, null, null, null, null, null, null, null);
                if (!sendAnMmsPdu(mmsResponsePdu)) {
                    return;
                }
            } catch (ServiceError e) {
                logger.warn(e.getMessage());
                if (!sendAnMmsPdu(createServiceErrorResponse(e, invokeId))) {
                    return;
                }
            }
        }
    }

    void cleanUpConnection() {
        synchronized (serverModel) {
            for (FcModelNode selectedCdo : selects) {
                selectedCdo.deselect();
            }
            for (Urcb rsvdUrcb : rsvdURCBs) {
                synchronized (rsvdUrcb) {
                    if (rsvdUrcb.enabled) {
                        rsvdUrcb.disable();
                    }
                    rsvdUrcb.reserved = null;
                    rsvdUrcb.getResv().setValue(false);
                }
            }
        }
    }

    boolean sendAnMmsPdu(MmsPdu mmsResponsePdu) {

        synchronized (berOStream) {
            berOStream.reset();
            try {
                mmsResponsePdu.encode(berOStream, false);
            } catch (IOException e1) {
                logger.error("IOException while encoding MMS PDU. Closing association.", e1);
                return false;
            }
            try {
                acseAssociation.send(berOStream.getByteBuffer());
            } catch (IOException e) {
                logger.warn("IOException while sending MMS PDU. Closing association.", e);
                return false;
            }
        }
        return true;
    }

    private MmsPdu listenForMmsRequest(AcseAssociation acseAssociation) {

        while (true) {
            MmsPdu mmsRequestPdu = null;
            pduBuffer.clear();
            try {
                acseAssociation.receive(pduBuffer);
            } catch (EOFException e) {
                logger.debug("Connection was closed by client.");
                return null;
            } catch (SocketTimeoutException e) {
                logger.warn("Message fragment timeout occured while receiving request. Closing association.", e);
                return null;
            } catch (IOException e) {
                logger.warn("IOException at lower layers while listening for incoming request. Closing association.", e);
                return null;
            } catch (DecodingException e) {
                logger.error("Error decoding request at OSI layers.", e);
                continue;
            } catch (TimeoutException e) {
                logger.error(
                        "Illegal state: message timeout while receiving request though this timeout should 0 and never be thrown",
                        e);
                return null;
            }
            mmsRequestPdu = new MmsPdu();

            try {
                mmsRequestPdu.decode(new ByteBufferInputStream(pduBuffer), null);
            } catch (IOException e) {
                logger.warn("IOException decoding received MMS request PDU.", e);
                continue;
            }

            if (mmsRequestPdu.confirmedRequestPdu == null) {
                if (mmsRequestPdu.conclude_RequestPDU != null) {
                    logger.debug("Got Conclude request, will close connection");
                    return null;
                } else {
                    logger.warn("Got unexpected MMS PDU, will ignore it");
                    continue;
                }
            }

            return mmsRequestPdu;
        }
    }

    private MmsPdu createServiceErrorResponse(ServiceError e, int invokeId) {

        SubChoice_errorClass errClass = null;

        switch (e.getErrorCode()) {

            case ServiceError.NO_ERROR:

                break;
            case ServiceError.INSTANCE_NOT_AVAILABLE:
                errClass = new SubChoice_errorClass(null, null, null, null, null, null, null, new BerInteger(
                        e.getErrorCode()), null, null, null, null, null);
                break;
            case ServiceError.INSTANCE_IN_USE:
                errClass = new SubChoice_errorClass(null, null, new BerInteger(e.getErrorCode()), null, null, null, null,
                        null, null, null, null, null, null);
                break;
            case ServiceError.ACCESS_VIOLATION:
                errClass = new SubChoice_errorClass(null, null, null, null, null, null, null, new BerInteger(
                        e.getErrorCode()), null, null, null, null, null);
                break;
            case ServiceError.ACCESS_NOT_ALLOWED_IN_CURRENT_STATE:
                errClass = new SubChoice_errorClass(null, null, null, null, null, null, null, null, null, null, null, null,
                        new BerInteger(e.getErrorCode()));
                break;
            case ServiceError.INSTANCE_LOCKED_BY_OTHER_CLIENT:
                errClass = new SubChoice_errorClass(null, null, null, null, null, null, null, null, null, null, null,
                        new BerInteger(2), null);
                break;
            case ServiceError.TYPE_CONFLICT:
                errClass = new SubChoice_errorClass(null, null, null, null, null, null, null, null, null, null, null,
                        new BerInteger(4), null);
                break;
            default:
                errClass = new SubChoice_errorClass(null, null, null, null, null, null, null, null, null, null, null, null,
                        new BerInteger(e.getErrorCode()));
        }
        org.openmuc.openiec61850.internal.mms.asn1.ServiceError asn1ServiceError = null;

        asn1ServiceError = new org.openmuc.openiec61850.internal.mms.asn1.ServiceError(errClass, null,
                new BerVisibleString(e.getMessage()));

        MmsPdu mmsPdu = new MmsPdu(null, null, new ConfirmedErrorPdu(new BerInteger(invokeId), null, asn1ServiceError),
                null, null, null, null, null, null);

        return mmsPdu;
    }

    private GetNameListResponse handleGetServerDirectoryRequest(GetNameListRequest getNameListRequest)
            throws ServiceError {

        Vector<BerVisibleString> identifiers = new Vector<BerVisibleString>();
        BerVisibleString identifier = null;

        for (ModelNode ld : serverModel) {
            identifier = new BerVisibleString(ld.getName());
            identifiers.add(identifier);
        }

        GetNameListResponse getNameListResponse = new GetNameListResponse(new SubSeqOf_listOfIdentifier(identifiers),
                new BerBoolean(false));

        return getNameListResponse;
    }

    private GetNameListResponse handleGetDirectoryRequest(GetNameListRequest getNameListRequest) throws ServiceError {

        // the ObjectScope can be vmdSpecific,domainSpecific, or aaSpecific. vmdSpecific and aaSpecific are not part of
        // 61850-8-1 but are used by some IEC 61850 clients anyways. This stack will return an empty list on vmdSpecific
        // and aaSpecific requests.
        if (getNameListRequest.objectScope.aaSpecific != null || getNameListRequest.objectScope.vmdSpecific != null) {
            SubSeqOf_listOfIdentifier listOfIden = new SubSeqOf_listOfIdentifier(new Vector<BerVisibleString>());
            GetNameListResponse getNameListResponse = new GetNameListResponse(listOfIden, new BerBoolean(false));
            return getNameListResponse;
        }

        String mmsDomainId = getNameListRequest.objectScope.domainSpecific.toString();

        ModelNode logicalDeviceMn = serverModel.getChild(mmsDomainId);

        if (logicalDeviceMn == null) {
            throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE,
                    "Got an invalid MMS request: given Domain name in GetNameList request is not a Logical Device name");
        }

        LogicalDevice logicalDevice = (LogicalDevice) logicalDeviceMn;

        insertRef = true;

        if (getNameListRequest.continueAfter != null) {
            continueAfter = getNameListRequest.continueAfter.toString();
            insertRef = false;
        }

        List<String> mmsReferences = new LinkedList<String>();

        for (ModelNode logicalNodeMn : logicalDevice) {
            LogicalNode logicalNode = (LogicalNode) logicalNodeMn;
            mmsReferences.add(logicalNode.getName());

            for (String mmsFC : mmsFcs) {
                Fc fc = Fc.fromString(mmsFC);
                if (fc != null) {

                    List<FcDataObject> fcDataObjects = logicalNode.getChildren(fc);
                    if (fcDataObjects != null) {
                        mmsReferences.add(logicalNode.getName() + "$" + mmsFC);
                        for (FcDataObject dataObject : fcDataObjects) {
                            insertMmsRef(dataObject, mmsReferences, logicalNode.getName() + "$" + mmsFC);
                        }
                    }

                }
            }
        }

        Vector<BerVisibleString> identifiers = new Vector<BerVisibleString>();

        int identifierSize = 0;
        boolean moreFollows = false;
        for (String mmsReference : mmsReferences) {
            if (insertRef == true) {
                if (identifierSize > negotiatedMaxPduSize - 200) {
                    moreFollows = true;
                    logger.debug(" ->maxMMSPduSize of " + negotiatedMaxPduSize + " Bytes reached");
                    break;
                }

                BerVisibleString identifier = null;

                identifier = new BerVisibleString(mmsReference);

                identifiers.add(identifier);
                identifierSize += mmsReference.length() + 2;
            } else {
                if (mmsReference.equals(continueAfter)) {
                    insertRef = true;
                }
            }
        }

        SubSeqOf_listOfIdentifier listOfIden = new SubSeqOf_listOfIdentifier(identifiers);

        return new GetNameListResponse(listOfIden, new BerBoolean(moreFollows));
    }

    private static void insertMmsRef(ModelNode node, List<String> mmsRefs, String parentRef) {
        String ref = parentRef + '$' + node.getName();
        mmsRefs.add(ref);
        if (!(node instanceof Array)) {
            for (ModelNode childNode : node) {
                insertMmsRef(childNode, mmsRefs, ref);
            }
        }
    }

    /**
     * GetVariableAccessAttributes (GetDataDefinition/GetDataDirectory) can be called with different kinds of
     * references. Examples: 1. DGEN1 2. DGEN1$CF 3. DGEN1$CF$GnBlk
     */
    private GetVariableAccessAttributesResponse handleGetVariableAccessAttributesRequest(
            GetVariableAccessAttributesRequest getVariableAccessAttributesRequest) throws ServiceError {
        if (getVariableAccessAttributesRequest.name == null) {
            throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
                    "Got an invalid MMS packet: name is not selected in GetVariableAccessAttributesRequest");
        }

        SubSeq_domain_specific domainSpecific = getVariableAccessAttributesRequest.name.domain_specific;

        if (domainSpecific == null) {
            throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
                    "Got an invalid MMS packet: Domain specific is not selected in GetVariableAccessAttributesRequest");
        }

        ModelNode modelNode = serverModel.getChild(domainSpecific.domainId.toString());

        if (modelNode == null) {
            throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
                    "GetVariableAccessAttributes (GetDataDefinition): no object with domainId "
                            + getVariableAccessAttributesRequest.name.domain_specific.domainId + " and ItemID "
                            + getVariableAccessAttributesRequest.name.domain_specific.itemId + " was found.");
        }

        String itemIdString = domainSpecific.itemId.toString();

        int index1 = itemIdString.indexOf('$');

        LogicalNode logicalNode = null;

        if (index1 != -1) {
            logicalNode = (LogicalNode) modelNode.getChild(itemIdString.substring(0, index1));
            if (logicalNode == null) {
                throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
                        "GetVariableAccessAttributes (GetDataDefinition): no object with domainId "
                                + getVariableAccessAttributesRequest.name.domain_specific.domainId + " and ItemID "
                                + getVariableAccessAttributesRequest.name.domain_specific.itemId + " was found.");
            }
            int index2 = itemIdString.indexOf('$', index1 + 2);
            if (index2 != -1) {
                Fc fc = Fc.fromString(itemIdString.substring(index1 + 1, index2));
                if (fc == null) {
                    throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
                            "GetVariableAccessAttributes (GetDataDefinition): no object with domainId "
                                    + getVariableAccessAttributesRequest.name.domain_specific.domainId + " and ItemID "
                                    + getVariableAccessAttributesRequest.name.domain_specific.itemId + " was found.");
                }
                index1 = itemIdString.indexOf('$', index2 + 2);
                ModelNode subNode;
                if (index1 == -1) {
                    subNode = logicalNode.getChild(itemIdString.substring(index2 + 1), fc);
                    if (subNode == null) {
                        throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
                                "GetVariableAccessAttributes (GetDataDefinition): no object with domainId "
                                        + getVariableAccessAttributesRequest.name.domain_specific.domainId
                                        + " and ItemID "
                                        + getVariableAccessAttributesRequest.name.domain_specific.itemId
                                        + " was found.");
                    }
                } else {
                    subNode = logicalNode.getChild(itemIdString.substring(index2 + 1, index1), fc);
                    if (subNode == null) {
                        throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
                                "GetVariableAccessAttributes (GetDataDefinition): no object with domainId "
                                        + getVariableAccessAttributesRequest.name.domain_specific.domainId
                                        + " and ItemID "
                                        + getVariableAccessAttributesRequest.name.domain_specific.itemId
                                        + " was found.");
                    }
                    index2 = itemIdString.indexOf('$', index1 + 2);
                    while (index2 != -1) {
                        subNode = subNode.getChild(itemIdString.substring(index1 + 1, index2));
                        if (subNode == null) {
                            throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
                                    "GetVariableAccessAttributes (GetDataDefinition): no object with domainId "
                                            + getVariableAccessAttributesRequest.name.domain_specific.domainId
                                            + " and ItemID "
                                            + getVariableAccessAttributesRequest.name.domain_specific.itemId
                                            + " was found.");
                        }
                        index1 = index2;
                        index2 = itemIdString.indexOf('$', index1 + 2);
                    }
                    subNode = subNode.getChild(itemIdString.substring(index1 + 1));
                    if (subNode == null) {
                        throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
                                "GetVariableAccessAttributes (GetDataDefinition): no object with domainId "
                                        + getVariableAccessAttributesRequest.name.domain_specific.domainId
                                        + " and ItemID "
                                        + getVariableAccessAttributesRequest.name.domain_specific.itemId
                                        + " was found.");
                    }
                }
                return new GetVariableAccessAttributesResponse(new BerBoolean(false), subNode.getMmsTypeSpec());
            } else {
                Fc fc = Fc.fromString(itemIdString.substring(index1 + 1));

                if (fc == null) {
                    throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
                            "GetVariableAccessAttributes (GetDataDefinition): no object with domainId "
                                    + getVariableAccessAttributesRequest.name.domain_specific.domainId + " and ItemID "
                                    + getVariableAccessAttributesRequest.name.domain_specific.itemId + " was found.");
                }

                List<FcDataObject> fcDataObjects = logicalNode.getChildren(fc);

                if (fcDataObjects == null || fcDataObjects.size() == 0) {
                    throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
                            "GetVariableAccessAttributes (GetDataDefinition): no object with domainId "
                                    + getVariableAccessAttributesRequest.name.domain_specific.domainId + " and ItemID "
                                    + getVariableAccessAttributesRequest.name.domain_specific.itemId + " was found.");
                }
                List<StructComponent> doStructComponents = new LinkedList<StructComponent>();

                for (ModelNode child : fcDataObjects) {
                    doStructComponents.add(new StructComponent(new BerVisibleString(child.getName().getBytes()), child
                            .getMmsTypeSpec()));
                }

                SubSeqOf_components comp = new SubSeqOf_components(doStructComponents);
                SubSeq_structure struct = new SubSeq_structure(null, comp);

                return new GetVariableAccessAttributesResponse(new BerBoolean(false), new TypeSpecification(null,
                        struct, null, null, null, null, null, null, null, null, null, null));

            }
        }

        logicalNode = (LogicalNode) modelNode.getChild(itemIdString);
        if (logicalNode == null) {
            throw new ServiceError(ServiceError.INSTANCE_NOT_AVAILABLE,
                    "GetVariableAccessAttributes (GetDataDefinition): no object with domainId "
                            + getVariableAccessAttributesRequest.name.domain_specific.domainId + " and ItemID "
                            + getVariableAccessAttributesRequest.name.domain_specific.itemId + " was found.");
        }

        List<StructComponent> structComponents = new LinkedList<StructComponent>();

        for (String mmsFc : mmsFcs) {
            Fc fc = Fc.fromString(mmsFc);
            if (fc != null) {

                Collection<FcDataObject> fcDataObjects = logicalNode.getChildren(fc);
                if (fcDataObjects == null) {
                    continue;
                }

                List<StructComponent> doStructComponents = new LinkedList<StructComponent>();

                for (ModelNode child : fcDataObjects) {
                    doStructComponents.add(new StructComponent(new BerVisibleString(child.getName().getBytes()), child
                            .getMmsTypeSpec()));
                }

                SubSeqOf_components comp = new SubSeqOf_components(doStructComponents);
                SubSeq_structure struct = new SubSeq_structure(null, comp);

                TypeSpecification fcTypeSpec = new TypeSpecification(null, struct, null, null, null, null, null, null,
                        null, null, null, null);

                StructComponent structCom = null;

                structCom = new StructComponent(new BerVisibleString(mmsFc), fcTypeSpec);

                structComponents.add(structCom);

            }
        }

        SubSeqOf_components comp = new SubSeqOf_components(structComponents);
        SubSeq_structure struct = new SubSeq_structure(null, comp);

        TypeSpecification typeSpec = new TypeSpecification(null, struct, null, null, null, null, null, null, null,
                null, null, null);

        return new GetVariableAccessAttributesResponse(new BerBoolean(false), typeSpec);

    }

    private ReadResponse handleGetDataValuesRequest(ReadRequest mmsReadRequest) throws ServiceError {

        VariableAccessSpecification variableAccessSpecification = mmsReadRequest.variableAccessSpecification;

        if (mmsReadRequest.specificationWithResult == null || mmsReadRequest.specificationWithResult.val == false) {

            if (variableAccessSpecification.listOfVariable == null) {
                throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
                        "handleGetDataValuesRequest: Got an invalid MMS packet");
            }

            List<VariableDef> listOfVariable = variableAccessSpecification.listOfVariable.seqOf;

            if (listOfVariable.size() < 1) {
                throw new ServiceError(ServiceError.PARAMETER_VALUE_INCONSISTENT,
                        "handleGetDataValuesRequest: less than one variableAccessSpecification is not allowed");
            }

            List<AccessResult> listOfAccessResult = new ArrayList<AccessResult>(listOfVariable.size());

            synchronized (serverModel) {
                for (VariableDef variableDef : listOfVariable) {

                    FcModelNode modelNode = serverModel.getNodeFromVariableDef(variableDef);

                    if (modelNode == null) {
                        logger.debug("Got a GetDataValues request for a non existent model node.");
                        // 10 indicates error "object-non-existent"
                        listOfAccessResult.add(new AccessResult(new BerInteger(10L), null));
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Got a GetDataValues request for node: " + modelNode);
                            if (!(modelNode instanceof BasicDataAttribute)) {
                                for (BasicDataAttribute bda : modelNode.getBasicDataAttributes()) {
                                    logger.debug("sub BDA is:" + bda);
                                }
                            }
                        }
                        listOfAccessResult.add(getReadResult(modelNode));
                    }
                }
            }
            return new ReadResponse(null, new SubSeqOf_listOfAccessResult(listOfAccessResult));
        } else {
            logger.debug("Got a GetDataSetValues request.");

            String dataSetReference = convertToDataSetReference(variableAccessSpecification.variableListName);

            if (dataSetReference == null) {
                throw new ServiceError(ServiceError.PARAMETER_VALUE_INCONSISTENT,
                        "handleGetDataSetValuesRequest: DataSet name incorrect");
            }

            List<AccessResult> listOfAccessResult;

            if (dataSetReference.startsWith("@")) {
                DataSet dataSet = nonPersistentDataSets.get(dataSetReference);
                if (dataSet == null) {
                    throw new ServiceError(ServiceError.PARAMETER_VALUE_INCONSISTENT,
                            "handleGetDataSetValuesRequest: a DataSet with the given reference does not exist");
                }
                listOfAccessResult = new ArrayList<AccessResult>(dataSet.getMembers().size());

                for (FcModelNode dsMember : dataSet) {
                    listOfAccessResult.add(getReadResult(dsMember));
                }
            } else {
                synchronized (serverModel) {
                    DataSet dataSet = serverModel.getDataSet(dataSetReference);

                    if (dataSet == null) {
                        throw new ServiceError(ServiceError.PARAMETER_VALUE_INCONSISTENT,
                                "handleGetDataSetValuesRequest: a DataSet with the given reference does not exist");
                    }

                    listOfAccessResult = new ArrayList<AccessResult>(dataSet.getMembers().size());

                    for (FcModelNode dsMember : dataSet) {
                        listOfAccessResult.add(getReadResult(dsMember));
                    }
                }
            }
            return new ReadResponse(null, new SubSeqOf_listOfAccessResult(listOfAccessResult));

        }

    }

    private AccessResult getReadResult(FcModelNode modelNode) {

        if (modelNode.getFc() == Fc.CO && modelNode.getName().equals("SBO")) {
            // if (modelNode.getName().equals("SBO")) {
            FcModelNode cdcParent = (FcModelNode) modelNode.getParent();
            ModelNode ctlModelNode = serverModel.findModelNode(cdcParent.getReference(), Fc.CF).getChild("ctlModel");
            if (ctlModelNode == null || !(ctlModelNode instanceof BdaInt8) || ((BdaInt8) ctlModelNode).getValue() != 2) {
                logger.warn("Selecting controle DO fails because ctlModel is not set to \"sbo-with-normal-security\"");
                // 3 indicates error "object_access_denied"
                return new AccessResult(new BerInteger(3L), null);
            }
            if (!cdcParent.select(this, serverSap.timer)) {
                return new AccessResult(null, new Data(null, null, null, null, null, null, null, null,
                        new BerVisibleString(""), null, null, null));
            }
            return new AccessResult(null, new Data(null, null, null, null, null, null, null, null,
                    new BerVisibleString("success"), null, null, null));
            // }
            // else {
            // logger.warn("A client tried to read a control variable other than SBO. This is not allowed.");
            // // 3 indicates error "object_access_denied"
            // return new AccessResult(new BerInteger(3L), null);
            // }

        }

        Data data = modelNode.getMmsDataObj();

        if (data == null) {
            // 11 indicates error "object_value_invalid"
            return new AccessResult(new BerInteger(11L), null);
        }

        return new AccessResult(null, data);

    }

    private WriteResponse handleSetDataValuesRequest(WriteRequest mmsWriteRequest) throws ServiceError {

        VariableAccessSpecification variableAccessSpecification = mmsWriteRequest.variableAccessSpecification;

        List<Data> listOfData = mmsWriteRequest.listOfData.seqOf;

        List<WriteResponse.SubChoice> mmsResponseValues = new ArrayList<WriteResponse.SubChoice>(listOfData.size());

        if (variableAccessSpecification.listOfVariable != null) {
            logger.debug("Got a SetDataValues request.");

            List<VariableDef> listOfVariable = variableAccessSpecification.listOfVariable.seqOf;

            if (listOfVariable.size() < 1 || listOfData.size() < 1 || listOfVariable.size() != listOfData.size()) {
                throw new ServiceError(
                        ServiceError.PARAMETER_VALUE_INCONSISTENT,
                        "handleSetDataValuesRequest: less than one variableAccessSpecification or data element is not allowed, or listOfData ne listOfVar");
            }
            // Create an iterator for the Modified Data Items
            Iterator<Data> mmsDataIterator = listOfData.iterator();

            List<BasicDataAttribute> totalBdasToBeWritten = new ArrayList<BasicDataAttribute>();
            int[] numBdas = new int[listOfData.size()];

            int i = -1;
            synchronized (serverModel) {
                for (VariableDef variableDef : listOfVariable) {
                    i++;
                    Data mmsData = mmsDataIterator.next();
                    // Current status of the modified data
                    FcModelNode modelNode = serverModel.getNodeFromVariableDef(variableDef);

                    if (modelNode == null) {
                        // 10 indicates error "object-non-existent"
                        mmsResponseValues.add(new WriteResponse.SubChoice(new BerInteger(10L), null));
                    } else {
                        switch (associationDataMode) {
                            case INTEGRATED_DATA:
                                // commit the data into the integrated device
                                ParameterGenerator parameterGenerator = acseAssociation.getParameterGenerator();
                                if (parameterGenerator != null && mmsData != null && mmsData.visible_string != null) {
                                    try {
                                        String fieldName = variableDef.variableSpecification.name.domain_specific.itemId.toString().split("\\$")[3];
                                        logger.info("Write Request PW Field Name = " + fieldName);
                                        parameterGenerator.writePWParameters(fieldName, mmsData.visible_string.toString());
                                        getFirstWriteResults(mmsResponseValues, totalBdasToBeWritten, numBdas, i, modelNode, mmsData);
                                        break;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            case SELF_MANAGED_DATA:
                                // commit the data modification
                                getFirstWriteResults(mmsResponseValues, totalBdasToBeWritten, numBdas, i, modelNode, mmsData);
                                break;
                            default:
                                mmsResponseValues.add(new WriteResponse.SubChoice(new BerInteger(10L), null));
                        }
                    }
                }
                writeAndFillMissingWriteResults(mmsResponseValues, totalBdasToBeWritten, numBdas);
            }
        } else if (variableAccessSpecification.variableListName != null) {
            logger.debug("Got a SetDataSetValues request.");
            String dataSetRef = convertToDataSetReference(variableAccessSpecification.variableListName);
            // TODO handle non-persisten DataSets too
            DataSet dataSet = serverModel.getDataSet(dataSetRef);
            Iterator<Data> mmsDataIterator = listOfData.iterator();
            List<BasicDataAttribute> totalBdasToBeWritten = new ArrayList<BasicDataAttribute>();
            int[] numBdas = new int[listOfData.size()];
            int i = -1;
            synchronized (serverModel) {
                for (FcModelNode dataSetMember : dataSet) {
                    i++;
                    Data mmsData = mmsDataIterator.next();
                    getFirstWriteResults(mmsResponseValues, totalBdasToBeWritten, numBdas, i, dataSetMember, mmsData);
                }
                writeAndFillMissingWriteResults(mmsResponseValues, totalBdasToBeWritten, numBdas);
            }
        } else {
            throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE,
                    "handleSetDataValuesRequest: invalid MMS request");
        }
        return new WriteResponse(mmsResponseValues);
    }

    private void writeAndFillMissingWriteResults(List<WriteResponse.SubChoice> mmsResponseValues,
                                                 List<BasicDataAttribute> totalBdasToBeWritten, int[] numBdas) {
        int i;
        if (totalBdasToBeWritten.size() != 0) {
            List<ServiceError> serviceErrors = serverSap.serverEventListener.write(totalBdasToBeWritten);
            ListIterator<WriteResponse.SubChoice> mmsResponseIterator = mmsResponseValues.listIterator();
            if (serviceErrors == null || serviceErrors.size() != totalBdasToBeWritten.size()) {
                while (mmsResponseIterator.hasNext()) {
                    if (mmsResponseIterator.next() == null) {
                        mmsResponseIterator.set(writeSuccess);
                    }
                }
                for (BasicDataAttribute bda : totalBdasToBeWritten) {
                    bda.mirror.setValueFrom(bda);
                }
            } else {
                i = -1;
                Iterator<ServiceError> serviceErrorIterator = serviceErrors.iterator();
                Iterator<BasicDataAttribute> bdaToBeWrittenIterator = totalBdasToBeWritten.iterator();
                while (mmsResponseIterator.hasNext()) {
                    i++;
                    if (mmsResponseIterator.next() == null) {
                        for (int j = 0; j < numBdas[i]; j++) {
                            ServiceError serviceError = serviceErrorIterator.next();
                            BasicDataAttribute bda = bdaToBeWrittenIterator.next();
                            if (serviceError != null) {
                                mmsResponseIterator.set(new WriteResponse.SubChoice(new BerInteger(
                                        serviceErrorToMmsError(serviceError)), null));
                            } else {
                                bda.mirror.setValueFrom(bda);
                            }
                        }
                    }
                }
            }
        }
    }

    private void getFirstWriteResults(List<WriteResponse.SubChoice> mmsResponseValues,
                                      List<BasicDataAttribute> totalBdasToBeWritten, int[] numBdas, int i, FcModelNode dataSetMember, Data mmsData) {
        WriteResponse.SubChoice writeResult = getWriteResult(dataSetMember, mmsData);
        if (writeResult == null) {
            FcModelNode fcModelNodeCopy = (FcModelNode) dataSetMember.copy();
            try {
                fcModelNodeCopy.setValueFromMmsDataObj(mmsData);
            } catch (ServiceError e) {
                logger.warn("SetDataValues failed because of data missmatch.", e);
                mmsResponseValues.add(new WriteResponse.SubChoice(new BerInteger(serviceErrorToMmsError(e)), null));
                return;
            }

            if (fcModelNodeCopy.fc == Fc.CO) {
//              TODO timeactivate operate
                fcModelNodeCopy = (BasicDataAttribute) fcModelNodeCopy.getChild("ctlVal");
//              TODO write origin and ctlNum if they exist
            }
            List<BasicDataAttribute> bdas = fcModelNodeCopy.getBasicDataAttributes();
            totalBdasToBeWritten.addAll(bdas);
            numBdas[i] = bdas.size();
            mmsResponseValues.add(null);
        } else {
            mmsResponseValues.add(writeResult);
        }
    }

    private WriteResponse.SubChoice getWriteResult(FcModelNode modelNode, Data mmsData) {

        Fc fc = modelNode.getFc();
        if (fc == Fc.ST || fc == Fc.MX || fc == Fc.OR || fc == Fc.EX) {
            // 3 indicates error "object_access_denied"
            return new WriteResponse.SubChoice(new BerInteger(3L), null);
        }

        if (fc == Fc.CO) {
            String nodeName = modelNode.getName();

            if (nodeName.equals("Oper")) {
                FcModelNode cdcParent = (FcModelNode) modelNode.getParent();
                ModelNode ctlModelNode = serverModel.findModelNode(cdcParent.getReference(), Fc.CF)
                        .getChild("ctlModel");
                if (ctlModelNode == null || !(ctlModelNode instanceof BdaInt8)) {
                    logger.warn("Operatring controle DO failed because ctlModel is not set.");
                    // 3 indicates error "object_access_denied"
                    return new WriteResponse.SubChoice(new BerInteger(3L), null);
                }

                int ctlModel = ((BdaInt8) ctlModelNode).getValue();

				/* Direct control with normal security (direct-operate) */
                if (ctlModel == 1) {
                    return null;
                }
                /* SBO control with normal security (operate-once or operate-many) */
                else if (ctlModel == 2) {
                    if (cdcParent.isSelectedBy(this)) {
                        return null;
                    } else {
                        // 3 indicates error "object_access_denied"
                        return new WriteResponse.SubChoice(new BerInteger(3L), null);
                    }

                } else {
                    logger.warn("SetDataValues failed because of unsupported ctlModel: " + ctlModel);
                    // 9 indicates error "object_access_unsupported"
                    return new WriteResponse.SubChoice(new BerInteger(9L), null);

                }
            } else {
                logger.warn("SetDataValues failed because of the operation is not allowed yet: " + modelNode.getName());
                // 9 indicates error "object_access_unsupported"
                return new WriteResponse.SubChoice(new BerInteger(9L), null);
            }
        } else if (fc == Fc.RP) {

            if (modelNode instanceof Rcb) {
                // 3 indicates error "object_access_denied"
                return new WriteResponse.SubChoice(new BerInteger(3L), null);
            }

            FcModelNode fcModelNodeCopy = (FcModelNode) modelNode.copy();

            try {
                fcModelNodeCopy.setValueFromMmsDataObj(mmsData);
            } catch (ServiceError e1) {
                return new WriteResponse.SubChoice(new BerInteger(serviceErrorToMmsError(e1)), null);
            }

            Urcb urcb = (Urcb) modelNode.getParent();

            String nodeName = modelNode.getName();

            synchronized (urcb) {
                if (nodeName.equals("RptEna")) {
                    BdaBoolean rptEnaNode = (BdaBoolean) fcModelNodeCopy;
                    if (rptEnaNode.getValue()) {
                        if (urcb.dataSet == null) {
                            logger.info("client tried to enable RCB even though there is no configured data set");
                            // 3 indicates error "object_access_denied"
                            return new WriteResponse.SubChoice(new BerInteger(3L), null);
                        }
                        if (urcb.reserved == null) {
                            urcb.reserved = this;
                            urcb.enable();
                            rsvdURCBs.add(urcb);
                            return writeSuccess;
                        } else if (urcb.reserved == this) {
                            urcb.enable();
                            return writeSuccess;
                        } else {
                            // 3 indicates error "object_access_denied"
                            return new WriteResponse.SubChoice(new BerInteger(3L), null);
                        }

                    } else {
                        // disable reporting
                        if (urcb.reserved == this) {
                            urcb.disable();
                            return writeSuccess;
                        } else {
                            // 3 indicates error "object_access_denied"
                            return new WriteResponse.SubChoice(new BerInteger(3L), null);
                        }
                    }
                } else if (nodeName.equals("Resv")) {
                    BdaBoolean rptResvNode = (BdaBoolean) fcModelNodeCopy;
                    if (rptResvNode.getValue()) {

                        if (urcb.reserved == null) {
                            urcb.reserved = this;
                            urcb.getResv().setValue(true);
                            return writeSuccess;
                        } else if (urcb.reserved == this) {
                            return writeSuccess;
                        } else {
                            // 3 indicates error "object_access_denied"
                            return new WriteResponse.SubChoice(new BerInteger(3L), null);
                        }
                    } else {
                        if (urcb.reserved == this) {
                            urcb.reserved = null;
                            urcb.getResv().setValue(false);
                            rsvdURCBs.remove(urcb);
                            return writeSuccess;
                        } else {
                            // 3 indicates error "object_access_denied"
                            return new WriteResponse.SubChoice(new BerInteger(3L), null);
                        }
                    }

                } else if (nodeName.equals("DatSet")) {
                    if ((urcb.reserved == null || urcb.reserved == this) && !urcb.enabled) {
                        String dataSetRef = ((BdaVisibleString) fcModelNodeCopy).getStringValue();
                        if (dataSetRef.isEmpty()) {
                            urcb.dataSet = null;
                            ((BasicDataAttribute) modelNode).setValueFrom((BasicDataAttribute) fcModelNodeCopy);
                            return writeSuccess;

                        } else {
                            DataSet dataSet = serverModel.getDataSet(dataSetRef);
                            if (dataSet != null) {
                                urcb.dataSet = dataSet;
                                ((BasicDataAttribute) modelNode).setValueFrom((BasicDataAttribute) fcModelNodeCopy);
                                return writeSuccess;
                            } else {
                                logger.info("Client tried to set dataSetReference of URCB to non existant data set.");
                                // 3 indicates error "object_access_denied"
                                return new WriteResponse.SubChoice(new BerInteger(3L), null);
                            }
                        }
                    } else {
                        logger.info("Client tried to write RCB parameter even though URCB is reserved by other client or already enabled.");
                        // 3 indicates error "object_access_denied"
                        return new WriteResponse.SubChoice(new BerInteger(3L), null);
                    }
                } else if (nodeName.equals("OptFlds")) {
                    if ((urcb.reserved == null || urcb.reserved == this) && !urcb.enabled) {
                        if (!((BdaOptFlds) modelNode).isBufferOverflow()
                                && !((BdaOptFlds) modelNode).isConfigRevision()
                                && !((BdaOptFlds) modelNode).isDataReference() && !((BdaOptFlds) modelNode).isEntryId()) {
                            ((BasicDataAttribute) modelNode).setValueFrom((BasicDataAttribute) fcModelNodeCopy);
                            return writeSuccess;
                        } else {
                            logger.info("Client tried to write OptFlds with usupported field set to true.");
                            // 3 indicates error "object_access_denied"
                            return new WriteResponse.SubChoice(new BerInteger(3L), null);
                        }
                    } else {
                        logger.info("Client tried to write RCB parameter even though URCB is reserved by other client or already enabled.");
                        // 3 indicates error "object_access_denied"
                        return new WriteResponse.SubChoice(new BerInteger(3L), null);
                    }

                } else if (nodeName.equals("GI")) {

                    if ((urcb.reserved == this) && urcb.enabled
                            && ((BdaTriggerConditions) urcb.getChild("TrgOps")).isGeneralInterrogation()) {
                        urcb.generalInterrogation();
                        return writeSuccess;
                    } else {
                        logger.info("Client tried to initiate a general interrogation even though URCB is not enabled by this client or general interrogation is not enabled in the trigger options.");
                        // 3 indicates error "object_access_denied"
                        return new WriteResponse.SubChoice(new BerInteger(3L), null);
                    }

                } else if (nodeName.equals("RptID") || nodeName.equals("BufTm") || nodeName.equals("TrgOps")
                        || nodeName.equals("IntgPd")) {
                    if ((urcb.reserved == null || urcb.reserved == this) && !urcb.enabled) {
                        ((BasicDataAttribute) modelNode).setValueFrom((BasicDataAttribute) fcModelNodeCopy);
                        return writeSuccess;
                    } else {
                        // 3 indicates error "object_access_denied"
                        return new WriteResponse.SubChoice(new BerInteger(3L), null);
                    }

                } else {
                    // nodes sqnum, ConfRev, and owner may not be read
                    // 3 indicates error "object_access_denied"
                    return new WriteResponse.SubChoice(new BerInteger(3L), null);
                }
            }

        } else {

            return null;
        }

    }

    // private WriteResponse.SubChoice operate(FcModelNode modelNode, Data mmsData) {
    // FcModelNode fcModelNodeCopy = (FcModelNode) modelNode.copy();
    // try {
    // fcModelNodeCopy.setValueFromMmsDataObj(mmsData);
    // } catch (ServiceError e) {
    // logger.warn("SetDataValues failed because of data missmatch.", e);
    // return new WriteResponse.SubChoice(new BerInteger(serviceErrorToMmsError(e)), null);
    // }
    //
    // // TODO timeactivate operate
    //
    // BasicDataAttribute ctlValBda = (BasicDataAttribute) fcModelNodeCopy.getChild("ctlVal");
    // List<BasicDataAttribute> bdas = new ArrayList<BasicDataAttribute>(1);
    // bdas.add(ctlValBda);
    // List<ServiceError> serviceErrors;
    // try {
    // serviceErrors = serverSap.serverEventListener.write(bdas);
    // } catch (ServiceError e) {
    // return new WriteResponse.SubChoice(new BerInteger(serviceErrorToMmsError(e)), null);
    // }
    // if (serviceErrors != null && serviceErrors.size() == bdas.size() && serviceErrors.get(1) != null) {
    // return new WriteResponse.SubChoice(new BerInteger(serviceErrorToMmsError(serviceErrors.get(1))), null);
    // }
    //
    // ctlValBda.mirror.setValueFrom(ctlValBda);
    // // TODO write origin and ctlNum if they exist
    //
    // return writeSuccess;
    // }

    private int serviceErrorToMmsError(ServiceError e) {

        switch (e.getErrorCode()) {
            case ServiceError.FAILED_DUE_TO_SERVER_CONSTRAINT:
                return 1;
            case ServiceError.INSTANCE_LOCKED_BY_OTHER_CLIENT:
                return 2;
            case ServiceError.ACCESS_VIOLATION:
                return 3;
            case ServiceError.TYPE_CONFLICT:
                return 7;
            case ServiceError.INSTANCE_NOT_AVAILABLE:
                return 10;
            case ServiceError.PARAMETER_VALUE_INCONSISTENT:
                return 11;
            default:
                return 9;
        }
    }

    private GetNameListResponse handleGetDataSetNamesRequest(GetNameListRequest getNameListRequest) throws ServiceError {

        BerVisibleString domainSpecific = getNameListRequest.objectScope.domainSpecific;

        List<String> dsList = null;
        if (domainSpecific == null) {
            dsList = new ArrayList<String>(nonPersistentDataSets.size());
            for (String dataSet : nonPersistentDataSets.keySet()) {
                dsList.add(dataSet);
            }
        } else {
            dsList = serverModel.getDataSetNames(domainSpecific.toString());
        }

        insertRef = true;
        if (getNameListRequest.continueAfter != null) {
            continueAfter = getNameListRequest.continueAfter.toString();
            insertRef = false;
        }

        Vector<BerVisibleString> identifiers = new Vector<BerVisibleString>();

        int identifierSize = 0;
        boolean moreFollows = false;

        if (dsList != null) {
            for (String dsRef : dsList) {
                if (insertRef == true) {
                    if (identifierSize > negotiatedMaxPduSize - 200) {
                        moreFollows = true;
                        logger.info("maxMMSPduSize reached");
                        break;
                    }
                    identifiers.add(new BerVisibleString(dsRef.getBytes()));
                    identifierSize += dsRef.length() + 2;
                } else {
                    if (dsRef.equals(continueAfter)) {
                        insertRef = true;
                    }
                }
            }
        }

        SubSeqOf_listOfIdentifier listOf = new SubSeqOf_listOfIdentifier(identifiers);

        return new GetNameListResponse(listOf, new BerBoolean(moreFollows));
    }

    private GetNamedVariableListAttributesResponse handleGetDataSetDirectoryRequest(ObjectName mmsGetNamedVarListAttReq)
            throws ServiceError {

        String dataSetReference = convertToDataSetReference(mmsGetNamedVarListAttReq);

        DataSet dataSet;

        if (dataSetReference.startsWith("@")) {
            dataSet = nonPersistentDataSets.get(dataSetReference);
        } else {
            dataSet = serverModel.getDataSet(dataSetReference);
        }

        if (dataSet == null) {
            throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE,
                    "DataSet with that reference is does not exist.");
        }

        List<VariableDef> listOfVariable = new ArrayList<VariableDef>();

        for (FcModelNode member : dataSet) {
            listOfVariable.add(member.getMmsVariableDef());
        }
        return new GetNamedVariableListAttributesResponse(new BerBoolean(dataSet.isDeletable()),
                new SubSeqOf_listOfVariable(listOfVariable));
    }

    private static String convertToDataSetReference(ObjectName mmsObjectName) {
        if (mmsObjectName.domain_specific != null) {
            return mmsObjectName.domain_specific.domainId.toString() + "/"
                    + mmsObjectName.domain_specific.itemId.toString().replace('$', '.');
        } else if (mmsObjectName.aa_specific != null) {
            // format is "@DataSetName"
            return mmsObjectName.aa_specific.toString();
        }
        return null;
    }

    private BerNull handleCreateDataSetRequest(DefineNamedVariableListRequest mmsDefineNamedVariableListRequest)
            throws ServiceError {
        String dataSetReference = convertToDataSetReference(mmsDefineNamedVariableListRequest.variableListName);
        if (dataSetReference == null) {
            throw new ServiceError(ServiceError.PARAMETER_VALUE_INCONSISTENT,
                    "handleCreateDataSetRequest: invalid MMS request (No DataSet Name Specified)");
        }

        List<VariableDef> nameList = mmsDefineNamedVariableListRequest.listOfVariable.seqOf;

        List<FcModelNode> dataSetMembers = new ArrayList<FcModelNode>(nameList.size());

        for (VariableDef variableDef : nameList) {
            dataSetMembers.add(serverModel.getNodeFromVariableDef(variableDef));
        }

        DataSet dataSet = new DataSet(dataSetReference, dataSetMembers, true);

        if (dataSetReference.startsWith("@")) {
            if (nonPersistentDataSets.containsKey(dataSetReference)) {
                throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE,
                        "data set with that name exists already");
            }
            nonPersistentDataSets.put(dataSetReference, dataSet);
        } else {
            serverModel.addDataSet(dataSet);
        }

        return new BerNull();
    }

    private DeleteNamedVariableListResponse handleDeleteDataSetRequest(
            DeleteNamedVariableListRequest mmsDelNamVarListReq) throws ServiceError {
        String dataSetReference = convertToDataSetReference(mmsDelNamVarListReq.listOfVariableListName.seqOf.get(0));

        if (dataSetReference.startsWith("@")) {
            if (nonPersistentDataSets.remove(dataSetReference) == null) {
                return new DeleteNamedVariableListResponse(new BerInteger(0), new BerInteger(0));
            } else {
                return new DeleteNamedVariableListResponse(new BerInteger(1), new BerInteger(1));
            }
        } else {
            synchronized (serverModel) {
                if (serverModel.removeDataSet(dataSetReference) == null) {
                    if (serverModel.getDataSet(dataSetReference) == null) {
                        // DataSet with the name does not exist.
                        return new DeleteNamedVariableListResponse(new BerInteger(0), new BerInteger(0));
                    } else {
                        // DataSet exists but is not deletable
                        return new DeleteNamedVariableListResponse(new BerInteger(1), new BerInteger(0));
                    }
                } else {
                    return new DeleteNamedVariableListResponse(new BerInteger(1), new BerInteger(1));
                }
            }
        }
    }

    void close() {
        cleanUpConnection();
        if (acseAssociation != null) {
            acseAssociation.disconnect();
        }
    }

}
