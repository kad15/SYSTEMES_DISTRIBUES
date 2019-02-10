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
package org.javact.local;

import java.io.Serializable;

import org.javact.lang.Agent;
import org.javact.lang.ServerSideAnswerBack;





/**
 * Server-side objects in charge of returning a result to a client, in  case of
 * client-server-like interaction (MessageWithReply).  This class is not
 * user-level. Middleware-level only.
 *
 * @version 0.5.0
 */
public class ServerSideAnswerBackImpl implements ServerSideAnswerBack {
    private ClientSideAnswerBackLocal csab;

    /**
     * Creates a server-side delegate in charge of returning the result, via a
     * client-side delegate
     *
     * @param csab the remote client-side delegate
     */
    public ServerSideAnswerBackImpl(ClientSideAnswerBackLocal csab) {
        this.csab = csab;
    }

    /**
     * Answer back the result, via the client-side delegate.
     *
     * @param s the result (a Serializable)
     */
    public void setReply(Serializable s) {
        csab.setReply(s);
    }

    /**
     * Answer back the result, via the client-side delegate.
     *
     * @param a the result (an actor)
     */
    public void setReply(Agent a) {
        csab.setReply(a);
    }
}
