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
 * Created by Edwin on 09-May-17.
 *
 * This class defined node in the Interceptor linked list which will be created and
 * iterated by InterceptorFactory class
 */
public class InterceptorNode implements InterceptorListObject {
    private Interceptor curInterceptor;

    private InterceptorListObject next;
    private InterceptorListObject previous;

    //Default constructor
    public InterceptorNode() {
        curInterceptor = null;
    }

    public InterceptorNode(Interceptor curInterceptor)
    {
        this.curInterceptor = curInterceptor;
    }

    public Interceptor getCurInterceptor() {
        return curInterceptor;
    }

    public void setCurInterceptor(Interceptor curInterceptor) {
        this.curInterceptor = curInterceptor;
    }

    @Override
    public InterceptorListObject getNextInterceptor() {
        return next;
    }

    @Override
    public InterceptorListObject getPreviousInterceptor() {
        return previous;
    }

    @Override
    public void setNextInterceptor(InterceptorListObject next) {
        this.next = next;
    }

    @Override
    public void setPreviousInterceptor(InterceptorListObject previous) {
        this.previous = previous;
    }

    //Calling current interceptor to process ASdu package and passed processed ASdu to next node
    public ASdu intercepts(ASdu asDu)
    {
        ASdu interceptedASdu = curInterceptor.intercept(asDu);

        if (next != null)
            interceptedASdu = next.intercepts(interceptedASdu);

        return interceptedASdu;
    }
}
