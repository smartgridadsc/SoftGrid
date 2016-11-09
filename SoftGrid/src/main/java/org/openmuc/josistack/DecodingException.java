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

public final class DecodingException extends Exception {

	private static final long serialVersionUID = -4102153710148894434L;

	public DecodingException() {
		super();
	}

	public DecodingException(String s) {
		super(s);
	}

	public DecodingException(Throwable cause) {
		super(cause);
	}

	public DecodingException(String s, Throwable cause) {
		super(s, cause);
	}

}
