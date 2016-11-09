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

/**
 * The listener sc for receiving incoming reports and association closed events. A listener is registered through
 * the {@link ClientSap#associate(java.net.InetAddress, int, String, ClientEventListener) associate} method.
 *
 * @author Stefan Feuerhahn
 *
 */
public interface ClientEventListener {

	/**
	 * Invoked when a new report arrives. Note that the implementation of this method needs to be thread safe as it can
	 * be called in parallel if a new report arrives while an old one is still being processed.
	 *
	 * @param report
	 *            the report that arrived.
	 */
	public void newReport(Report report);

	/**
	 * Invoked when an IOException occurred for the association. An IOException implies that the ClientAssociation that
	 * feeds this listener was automatically closed and can no longer be used to receive reports.
	 *
	 * @param e
	 *            the exception that occured.
	 */
	public void associationClosed(IOException e);

}
