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
import java.util.Timer;
import java.util.TimerTask;

import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.string.BerVisibleString;
import org.openmuc.openiec61850.internal.mms.asn1.AlternateAccess;
import org.openmuc.openiec61850.internal.mms.asn1.AlternateAccess.SubChoice.SubSeq_selectAlternateAccess;
import org.openmuc.openiec61850.internal.mms.asn1.AlternateAccess.SubChoice.SubSeq_selectAlternateAccess.SubChoice_accessSelection;
import org.openmuc.openiec61850.internal.mms.asn1.ObjectName;
import org.openmuc.openiec61850.internal.mms.asn1.VariableDef;
import org.openmuc.openiec61850.internal.mms.asn1.VariableSpecification;

public abstract class FcModelNode extends ModelNode {

	private VariableDef variableDef = null;
	Fc fc;
	private ServerAssociation selected = null;
	private TimerTask task = null;

	public Fc getFc() {
		return fc;
	}

	@Override
	public String toString() {
		return getReference().toString() + " [" + fc + "]";
	}

	boolean select(ServerAssociation association, Timer timer) {
		if (selected != null) {
			if (selected != association) {
				return false;
			}
		}
		else {
			selected = association;
			association.selects.add(this);
		}

		ModelNode sboTimeoutNode = association.serverModel.findModelNode(objectReference, Fc.CF).getChild("sboTimeout");

		if (sboTimeoutNode == null) {
			return true;
		}

		long sboTimeout = ((BdaInt32U) sboTimeoutNode).getValue();

		if (sboTimeout == 0) {
			return true;
		}

		class SelectResetTask extends TimerTask {
			ServerAssociation association;

			SelectResetTask(ServerAssociation association) {
				this.association = association;
			}

			@Override
			public void run() {
				synchronized (association.serverModel) {
					if (task == this) {
						task = null;
						deselectAndRemove(association);
					}
				}
			}
		}

		if (task != null) {
			task.cancel();
		}

		task = new SelectResetTask(association);
		timer.schedule(task, sboTimeout);

		return true;

	}

	void deselectAndRemove(ServerAssociation association) {
		selected = null;
		if (task != null) {
			task.cancel();
			task = null;
		}
		association.selects.remove(this);
	}

	void deselect() {
		selected = null;
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	boolean isSelected() {
		if (selected == null) {
			return false;
		}
		return true;
	}

	boolean isSelectedBy(ServerAssociation association) {
		if (selected == association) {
			return true;
		}
		return false;
	}

	VariableDef getMmsVariableDef() {

		if (variableDef != null) {
			return variableDef;
		}

		AlternateAccess alternateAccess = null;

		StringBuilder preArrayIndexItemId = new StringBuilder(objectReference.get(1));
		preArrayIndexItemId.append("$");
		preArrayIndexItemId.append(fc);

		int arrayIndexPosition = objectReference.getArrayIndexPosition();
		if (arrayIndexPosition != -1) {

			for (int i = 2; i < arrayIndexPosition; i++) {
				preArrayIndexItemId.append("$");
				preArrayIndexItemId.append(objectReference.get(i));
			}

			List<AlternateAccess.SubChoice> subSeqOfAlternateAccess = new ArrayList<AlternateAccess.SubChoice>();
			BerInteger indexBerInteger = new BerInteger(Integer.parseInt(objectReference.get(arrayIndexPosition)));

			if (arrayIndexPosition < (objectReference.size() - 1)) {
				// this reference points to a subnode of an array element

				StringBuilder postArrayIndexItemId = new StringBuilder(objectReference.get(arrayIndexPosition + 1));

				for (int i = (arrayIndexPosition + 2); i < objectReference.size(); i++) {
					postArrayIndexItemId.append("$");
					postArrayIndexItemId.append(objectReference.get(i));
				}

				// component name is stored in an AlternateAccess
				List<AlternateAccess.SubChoice> subSeqOf = new ArrayList<AlternateAccess.SubChoice>();
				subSeqOf.add(new AlternateAccess.SubChoice(null, new BerVisibleString(postArrayIndexItemId.toString()
						.getBytes()), null, null, null, null));
				AlternateAccess subArrayEle = new AlternateAccess(subSeqOf);

				SubChoice_accessSelection accSel = new SubChoice_accessSelection(null, indexBerInteger, null, null);
				SubSeq_selectAlternateAccess selectAltAcc = new SubSeq_selectAlternateAccess(accSel, subArrayEle);

				subSeqOfAlternateAccess.add(new AlternateAccess.SubChoice(selectAltAcc, null, null, null, null, null));

			}
			else {
				subSeqOfAlternateAccess
						.add(new AlternateAccess.SubChoice(null, null, indexBerInteger, null, null, null));
			}

			alternateAccess = new AlternateAccess(subSeqOfAlternateAccess);

		}
		else {

			for (int i = 2; i < objectReference.size(); i++) {
				preArrayIndexItemId.append("$");
				preArrayIndexItemId.append(objectReference.get(i));
			}
		}

		ObjectName objectName = new ObjectName(null, new ObjectName.SubSeq_domain_specific(new BerVisibleString(
				objectReference.get(0).getBytes()), new BerVisibleString(preArrayIndexItemId.toString().getBytes())),
				null);

		VariableSpecification varSpec = new VariableSpecification(objectName);

		variableDef = new VariableDef(varSpec, alternateAccess);
		return variableDef;
	}

}
