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

public enum Fc {

	// The following FCs are not part of this enum because they are not really
	// FCs and only defined in part 8-1:
	// RP (report), LG (log), BR (buffered report), GO, GS, MS, US

	// FCs according to IEC 61850-7-2:
	/** Status information */
	ST,
	/** Measurands - analogue values */
	MX,
	/** Setpoint */
	SP,
	/** Substitution */
	SV,
	/** Configuration */
	CF,
	/** Description */
	DC,
	/** Setting group */
	SG,
	/** Setting group editable */
	SE,
	/** Service response / Service tracking */
	SR,
	/** Operate received */
	OR,
	/** Blocking */
	BL,
	/** Extended definition */
	EX,
	/** Control, deprecated but kept here for backward compatibility */
	CO,
	/** Unbuffered Reporting */
	RP,
	/** Buffered Reporting */
	BR;

	/*
	 * * @param fc
	 * 
	 * @return
	 */

	public static Fc fromString(String fc) {
		try {
			return Fc.valueOf(fc);
		} catch (Exception e) {
			return null;
		}
	}

}
