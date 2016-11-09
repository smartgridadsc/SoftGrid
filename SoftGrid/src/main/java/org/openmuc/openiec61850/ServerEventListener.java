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

import java.util.List;

public interface ServerEventListener {

	/**
	 * The write callback function is called if one of more basic data attributes are written using either the
	 * setDataValue, setDataSetValues or control services. If the complete write process was successful write returns
	 * either an empty list or null. If an error occurs writing one or more attributes then a list shall be returned
	 * that is of equal size as the list of basic data attributes. The returned list's element shall be null if writing
	 * the corresponding BDA was successful and a service error otherwise.
	 * 
	 * @param bdas
	 *            the list of basic data attributes that are to be set.
	 * @return a list of service errors indicating errors writing the corresponding basic data attributes.
	 */
	public List<ServiceError> write(List<BasicDataAttribute> bdas);

	void serverStoppedListening(ServerSap serverSAP);

}
