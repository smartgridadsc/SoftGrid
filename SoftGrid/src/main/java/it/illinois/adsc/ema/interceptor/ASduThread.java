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

        * @author Edwin Lesmana Tjiong
*/

package it.illinois.adsc.ema.interceptor;

import org.openmuc.j60870.ASdu;

import java.util.concurrent.Callable;

/**
 * Created by Edwin on 15-May-17.
 */
public class ASduThread implements Callable {
    ASdu curASdu;

    InterceptorListObject curNode;

    public ASduThread(ASdu curASdu, InterceptorListObject curNode){
        this.curASdu = curASdu;
        this.curNode = curNode;
    }


    @Override
    public ASdu call() throws Exception {

        //Run interceptor on the root, the root will call next interceptor recursively
        if (curNode != null) {
            curASdu = curNode.intercepts(curASdu);
        }

        return curASdu;
    }
}
