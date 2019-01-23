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
package org.javact.lang;

/**
 * Defines partially a Future object, for client-server-like interaction.
 * Extended by ClientSideAnswerBackImpl, with a synchronized method that
 * updates the field reply -setReply()-.  Middleware-level only.
 *
 * @version 0.5.0
 */
public abstract class Future {
    /** Indicates if the reply has been got or not */
    protected boolean replyOK = false;

    /** The result of the client-server-like interaction */
    protected Object reply;

    /**
     * Creates a Future Object
     */
    public Future() {
    }

    /**
     * Get the answer, possibly after waiting for it. In any case, its is
     * located on the same JVM as the client, so it is locally invoked.
     *
     * @return the result of the client-server-like interaction
     */
    public final synchronized Object getReply() {
        if (replyOK == false) {
            try {
                wait();
            } catch (InterruptedException e) {
            	throw new JSMSendException("Interrupted while waiting the answer",e);
            }
        }

        return reply;
    }
}
