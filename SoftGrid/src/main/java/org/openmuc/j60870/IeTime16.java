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
/*
 * Copyright 2014 Fraunhofer ISE
 *
 * This file is part of j60870.
 * For more information visit http://www.openmuc.org
 *
 * j60870 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * j60870 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with j60870.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openmuc.j60870;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Represents a two octet binary time (CP16Time2a) information element.
 * 
 * @author Stefan Feuerhahn
 * 
 */
public class IeTime16 extends InformationElement {

	private final byte[] value = new byte[2];

	public IeTime16(long timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);

		int ms = calendar.get(Calendar.MILLISECOND) + 1000 * calendar.get(Calendar.SECOND);

		value[0] = (byte) ms;
		value[1] = (byte) (ms >> 8);
	}

	public IeTime16(int timeInMs) {

		int ms = timeInMs % 60000;
		value[0] = (byte) ms;
		value[1] = (byte) (ms >> 8);
	}

	IeTime16(DataInputStream is) throws IOException {
		is.readFully(value);
	}

	@Override
	int encode(byte[] buffer, int i) {
		System.arraycopy(value, 0, buffer, i, 2);
		return 2;
	}

	public int getTimeInMs() {
		return (value[0] & 0xff) + ((value[1] & 0xff) << 8);
	}

	@Override
	public String toString() {
		return "Time16, time in ms: " + getTimeInMs();
	}
}
