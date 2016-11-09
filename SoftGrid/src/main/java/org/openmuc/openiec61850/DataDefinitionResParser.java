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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openmuc.openiec61850.internal.mms.asn1.ConfirmedServiceResponse;
import org.openmuc.openiec61850.internal.mms.asn1.GetVariableAccessAttributesResponse;
import org.openmuc.openiec61850.internal.mms.asn1.StructComponent;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification;
import org.openmuc.openiec61850.internal.mms.asn1.TypeSpecification.SubSeq_structure.SubSeqOf_components;

final class DataDefinitionResParser {

	static LogicalNode parseGetDataDefinitionResponse(ConfirmedServiceResponse confirmedServiceResponse,
			ObjectReference lnRef) throws ServiceError {

		if (confirmedServiceResponse.getVariableAccessAttributes == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"decodeGetDataDefinitionResponse: Error decoding GetDataDefinitionResponsePdu");
		}

		GetVariableAccessAttributesResponse varAccAttrs = confirmedServiceResponse.getVariableAccessAttributes;
		TypeSpecification typeSpec = varAccAttrs.typeSpecification;
		if (typeSpec.structure == null) {
			throw new ServiceError(ServiceError.FAILED_DUE_TO_COMMUNICATIONS_CONSTRAINT,
					"decodeGetDataDefinitionResponse: Error decoding GetDataDefinitionResponsePdu");
		}

		SubSeqOf_components structure = typeSpec.structure.components;

		List<FcDataObject> fcDataObjects = new LinkedList<FcDataObject>();

		Fc fc;
		for (StructComponent fcComponent : structure.seqOf) {
			if (fcComponent.componentName == null) {
				throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE,
						"Error decoding GetDataDefinitionResponsePdu");
			}

			if (fcComponent.componentType.structure == null) {
				throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE,
						"Error decoding GetDataDefinitionResponsePdu");
			}

			String fcString = fcComponent.componentName.toString();
			if (fcString.equals("LG") || fcString.equals("GO") || fcString.equals("GS") || fcString.equals("MS")
					|| fcString.equals("US")) {
				continue;
			}

			fc = Fc.fromString(fcComponent.componentName.toString());
			SubSeqOf_components subStructure = fcComponent.componentType.structure.components;

			fcDataObjects.addAll(getFcDataObjectsFromSubStructure(lnRef, fc, subStructure));

		}

		LogicalNode ln = new LogicalNode(lnRef, fcDataObjects);

		return ln;

	}

	private static List<FcDataObject> getFcDataObjectsFromSubStructure(ObjectReference lnRef, Fc fc,
			SubSeqOf_components structure) throws ServiceError {

		List<StructComponent> structComponents = structure.seqOf;
		List<FcDataObject> dataObjects = new ArrayList<FcDataObject>(structComponents.size());

		for (StructComponent doComp : structComponents) {
			if (doComp.componentName == null) {
				throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE,
						"Error decoding GetDataDefinitionResponsePdu");
			}
			if (doComp.componentType.structure == null) {
				throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE,
						"Error decoding GetDataDefinitionResponsePdu");
			}

			ObjectReference doRef = new ObjectReference(lnRef + "." + doComp.componentName.toString());
			List<FcModelNode> children = getDoSubModelNodesFromSubStructure(doRef, fc,
					doComp.componentType.structure.components, false);
			if (fc == Fc.RP) {
				dataObjects.add(new Urcb(doRef, children));
			}
			else if (fc == Fc.BR) {
				dataObjects.add(new Brcb(doRef, children));
			}
			else {
				dataObjects.add(new FcDataObject(doRef, fc, children));
			}

		}

		return dataObjects;

	}

	private static List<FcModelNode> getDoSubModelNodesFromSubStructure(ObjectReference parentRef, Fc fc,
			SubSeqOf_components structure, boolean parentWasArray) throws ServiceError {

		Collection<StructComponent> structComponents = structure.seqOf;
		List<FcModelNode> dataObjects = new ArrayList<FcModelNode>(structComponents.size());

		for (StructComponent component : structComponents) {
			if (component.componentName == null) {
				throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE,
						"Error decoding GetDataDefinitionResponsePdu");
			}

			String childName = component.componentName.toString();
			ObjectReference childReference;
			if (!parentWasArray) {
				childReference = new ObjectReference(parentRef + "." + childName);
			}
			else {
				childReference = new ObjectReference(parentRef + childName);
			}
			dataObjects.add(getModelNodesFromTypeSpecification(childReference, fc, component.componentType, false));

		}
		return dataObjects;
	}

	private static FcModelNode getModelNodesFromTypeSpecification(ObjectReference ref, Fc fc,
			TypeSpecification mmsTypeSpec, boolean parentWasArray) throws ServiceError {

		if (mmsTypeSpec.array != null) {

			int numArrayElements = (int) mmsTypeSpec.array.numberOfElements.val;
			List<FcModelNode> arrayChildren = new ArrayList<FcModelNode>(numArrayElements);
			for (int i = 0; i < numArrayElements; i++) {
				arrayChildren.add(getModelNodesFromTypeSpecification(
						new ObjectReference(ref + "(" + Integer.toString(i) + ")"), fc, mmsTypeSpec.array.elementType,
						true));
			}

			return new Array(ref, fc, arrayChildren);

		}

		if (mmsTypeSpec.structure != null) {
			List<FcModelNode> children = getDoSubModelNodesFromSubStructure(ref, fc, mmsTypeSpec.structure.components,
					parentWasArray);
			return (new ConstructedDataAttribute(ref, fc, children));
		}

		// it is a single element
		BasicDataAttribute bt = convertMmsBasicTypeSpec(ref, fc, mmsTypeSpec);
		if (bt == null) {
			throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE,
					"decodeGetDataDefinitionResponse: Unknown data type received " + ref);
		}
		return (bt);

	}

	private static BasicDataAttribute convertMmsBasicTypeSpec(ObjectReference ref, Fc fc, TypeSpecification mmsTypeSpec)
			throws ServiceError {

		if (mmsTypeSpec.boolean_ != null) {
			return new BdaBoolean(ref, fc, null, false, false);
		}
		if (mmsTypeSpec.bit_string != null) {
			int bitStringMaxLength = Math.abs((int) mmsTypeSpec.bit_string.val);

			if (bitStringMaxLength == 13) {
				return new BdaQuality(ref, fc, null, false);
			}
			else if (bitStringMaxLength == 10) {
				return new BdaOptFlds(ref);
			}
			else if (bitStringMaxLength == 6) {
				return new BdaTriggerConditions(ref);
			}
			else if (bitStringMaxLength == 2) {
				if (fc == Fc.CO) {
					// if name == ctlVal
					if (ref.getName().charAt(1) == 't') {
						return new BdaTapCommand(ref, fc, null, false, false);
					}
					// name == Check
					else {
						return new BdaCheck(ref);
					}
				}
				else {
					return new BdaDoubleBitPos(ref, fc, null, false, false);
				}
			}
			return null;
		}
		else if (mmsTypeSpec.integer != null) {
			switch ((int) mmsTypeSpec.integer.val) {
			case 8:
				return new BdaInt8(ref, fc, null, false, false);
			case 16:
				return new BdaInt16(ref, fc, null, false, false);
			case 32:
				return new BdaInt32(ref, fc, null, false, false);
			case 64:
				return new BdaInt64(ref, fc, null, false, false);
			}
		}
		else if (mmsTypeSpec.unsigned != null) {
			switch ((int) mmsTypeSpec.unsigned.val) {
			case 8:
				return new BdaInt8U(ref, fc, null, false, false);
			case 16:
				return new BdaInt16U(ref, fc, null, false, false);
			case 32:
				return new BdaInt32U(ref, fc, null, false, false);
			}
		}
		else if (mmsTypeSpec.floating_point != null) {
			int floatSize = (int) mmsTypeSpec.floating_point.format_width.val;
			if (floatSize == 32) {
				return new BdaFloat32(ref, fc, null, false, false);
			}
			else if (floatSize == 64) {
				return new BdaFloat64(ref, fc, null, false, false);
			}
			throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE, "FLOAT of size: " + floatSize
					+ " is not supported.");
		}
		else if (mmsTypeSpec.octet_string != null) {
			int stringSize = (int) mmsTypeSpec.octet_string.val;
			if (stringSize > 255 || stringSize < -255) {
				throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE, "OCTET_STRING of size: "
						+ stringSize + " is not supported.");
			}
			return new BdaOctetString(ref, fc, null, Math.abs(stringSize), false, false);

		}
		else if (mmsTypeSpec.visible_string != null) {
			int stringSize = (int) mmsTypeSpec.visible_string.val;
			if (stringSize > 255 || stringSize < -255) {
				throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE, "VISIBLE_STRING of size: "
						+ stringSize + " is not supported.");
			}
			return new BdaVisibleString(ref, fc, null, Math.abs(stringSize), false, false);
		}
		else if (mmsTypeSpec.mms_string != null) {
			int stringSize = (int) mmsTypeSpec.mms_string.val;
			if (stringSize > 255 || stringSize < -255) {
				throw new ServiceError(ServiceError.PARAMETER_VALUE_INAPPROPRIATE, "UNICODE_STRING of size: "
						+ stringSize + " is not supported.");
			}
			return new BdaUnicodeString(ref, fc, null, Math.abs(stringSize), false, false);
		}
		else if (mmsTypeSpec.utc_time != null) {
			return new BdaTimestamp(ref, fc, null, false, false);
		}
		else if (mmsTypeSpec.binary_time != null) {
			return new BdaEntryTime(ref, fc, null, false, false);
		}
		return null;
	}
}
