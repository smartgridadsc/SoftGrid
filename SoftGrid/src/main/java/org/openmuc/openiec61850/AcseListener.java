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
import java.nio.ByteBuffer;

import org.openmuc.josistack.AcseAssociation;
import org.openmuc.josistack.AcseAssociationListener;

final class AcseListener implements AcseAssociationListener {

	ServerSap serverSap;

	AcseListener(ServerSap serverSap) {
		this.serverSap = serverSap;
	}

	@Override
	public void connectionIndication(AcseAssociation acseAssociation, ByteBuffer psdu) {
		serverSap.connectionIndication(acseAssociation, psdu);
	}

	@Override
	public void serverStoppedListeningIndication(IOException e) {
		serverSap.serverStoppedListeningIndication(e);
	}

}
