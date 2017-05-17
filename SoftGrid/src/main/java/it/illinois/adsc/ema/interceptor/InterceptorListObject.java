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

/**
 * Created by prageethmahendra on 5/5/2017.
 *
 * This class is interface for individual interceptor object in the linked list.
 * Developer need to implement this interface to create node
 */
public interface InterceptorListObject {
    InterceptorListObject getNextInterceptor();
    InterceptorListObject getPreviousInterceptor();

    void setNextInterceptor(InterceptorListObject next);
    void setPreviousInterceptor(InterceptorListObject prev);

    ASdu intercepts(ASdu aSdu);
}
