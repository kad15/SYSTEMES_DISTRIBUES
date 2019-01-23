/*
* ###########################################################################
* JavAct: A Java(TM) library for distributed and mobile actor-based computing
* Copyright (C) 2001-2007 I.R.I.T./C.N.R.S.-I.N.P.T.-U.P.S.
*
* This library is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation; either version 2.1 of the License, or any
* later version.
*
* This library is distributed in the hope that it will be useful, but
* WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
* or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
* License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this library; if not, write to the Free Software Foundation,
* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
*
* Initial developer(s): The I.A.M. Team (I.R.I.T.) - SMAC Team (IRIT) since 2007
* Contributor(s): The I.A.M. Team (I.R.I.T.) - SMAC Team (IRIT) since 2007
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.net.rmi;

import java.rmi.RemoteException;

import org.javact.lang.Future;




/**
 * Remote client-side object, which is in charge of recovering the answer  from
 * a server-side object, and updating the reply. Its reference is
 * network-level (global), and not passed with the message (transient), but
 * separately. Middleware-level only.
 *
 * @version 0.5.0
 */
public class ClientSideAnswerBackImpl extends Future
    implements ClientSideAnswerBackNet {

    /**
     * Creates a Remote ClientSideAnswerBackImpl object
     */
    ClientSideAnswerBackImpl() {
    }

    /**
     * Sets the reply. Invoked by the server-side delegate. As it can be
     * located on different JVM from the client one, setReply() can be
     * remotely invoked.
     *
     * @param r the result of the client-server-like interaction
     *
     * @throws RemoteException
     */
    public final synchronized void setReply(Object r) throws RemoteException {
        reply = r;
        replyOK = true;
        notify();
    }
}
