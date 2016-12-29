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
package org.openmuc.openiec61850.server.report;

//package org.openiec61850.server.report;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Logger;
//
//public class MonitorThread extends Thread {
//
//	private static MonitorThread instance = new MonitorThread();
//	private final List<Runnable> runnables = new ArrayList<Runnable>();
//	private final static Logger logger = LoggerFactory.getLogger(MonitorThread.class);
//
//	protected MonitorThread() {
//		setName("MonitorThread");
//		start();
//	}
//
//	public static MonitorThread theThread() {
//		return instance;
//	}
//
//	@Override
//	public void run() {
//		while (true) {
//			try {
//				for (Runnable runnable : runnables) {
//					runnable.run();
//				}
//			} catch (Throwable exc) {
//				logger.error("Error in {} {}", getName(), exc.getMessage());
//			}
//			try {
//				sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public void addService(Runnable runnable) {
//		runnables.add(runnable);
//	}
//
// }
