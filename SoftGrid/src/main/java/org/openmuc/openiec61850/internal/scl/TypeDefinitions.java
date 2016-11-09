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
package org.openmuc.openiec61850.internal.scl;

import java.util.ArrayList;

public final class TypeDefinitions {

	private final ArrayList<LnType> lnodeTypes = new ArrayList<LnType>();
	private final ArrayList<DoType> doTypes = new ArrayList<DoType>();
	private final ArrayList<DaType> daTypes = new ArrayList<DaType>();
	private final ArrayList<EnumType> enumTypes = new ArrayList<EnumType>();

	public TypeDefinitions() {
	}

	public void putLNodeType(LnType lnodeType) {
		lnodeTypes.add(lnodeType);
	}

	public void putDOType(DoType doType) {
		doTypes.add(doType);
	}

	public void putDAType(DaType daType) {
		daTypes.add(daType);
	}

	public void putEnumType(EnumType enumType) {
		enumTypes.add(enumType);
	}

	public DaType getDaType(String daType) {
		for (DaType datype : daTypes) {
			if (datype.id.equals(daType)) {
				return datype;
			}
		}

		return null;
	}

	public DoType getDOType(String doType) {
		for (DoType dotype : doTypes) {
			if (dotype.id.equals(doType)) {
				return dotype;
			}
		}

		return null;
	}

	public LnType getLNodeType(String lnType) {

		for (LnType ntype : lnodeTypes) {
			if (ntype.id.equals(lnType)) {
				return ntype;
			}
		}

		return null;
	}

	public EnumType getEnumType(String enumTypeRef) {
		for (EnumType enumType : enumTypes) {
			if (enumType.id.equals(enumTypeRef)) {
				return enumType;
			}
		}

		return null;
	}

}
