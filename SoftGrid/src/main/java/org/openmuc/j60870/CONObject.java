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
package org.openmuc.j60870;

import com.sun.corba.se.spi.activation.Server;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.APdu;
import org.openmuc.j60870.ClientSap;
import org.openmuc.j60870.Connection;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by prageethmahendra on 13/6/2016.
 */
public class CONObject {
    ASdu aSdu;
    public CONObject(DataInputStream inputStream, Socket clientSocket) {
        try {
            APdu aPdu = new APdu(inputStream, new ConnectionSettings());
            aSdu = aPdu.getASdu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ASdu getaSdu() {
        return aSdu;
    }

    public void setaSdu(ASdu aSdu) {
        this.aSdu = aSdu;
    }
}
