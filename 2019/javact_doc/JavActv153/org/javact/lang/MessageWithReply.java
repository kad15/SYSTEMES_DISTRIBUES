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
 * Message for client-server-like interaction. A reply can be requested through 
 * it. When the client explicitely demands the result, it is locked until
 * reply is got. Extended by (automatically generated) users' message classes.
 * Here, inheritance relation allows code factorization and sharing
 * (inheritance of implementation). The extending class must define handle()
 * and getReply() methods. However, getReply() can't be  specified here as
 * abstract method, because it is generated with ad hoc result type.
 * Middleware-level only.
 *
 * @version 0.5.0
 */
public abstract class MessageWithReply implements Message {
    /**
     * Client's delegate for answer recovering on which it locks. Its reference
     * is not transmitted within the message (transient).
     */
    private transient Future f;

    /** A delegate for reply recovering. Used only on server side, locally. */
    private ServerSideAnswerBack ssab;

    /**
     * Creation of a message for client-server-like interaction
     */
    protected MessageWithReply() {
    }

    /**
     * Initialization of the message Future delegate and the ServerSideAnswerBack delegate. 
     * Invoked by SendCt on client side.
     *
     * @param f the Future delegate
     * @param ssab the server side answer back delegate
     *
     * @throws JSMSendException when the message has already
     *         been sent
     */
    void init(Future f, ServerSideAnswerBack ssab) {
        if ((this.f == null) || (this.ssab == null)) {
            this.f = f;
            this.ssab = ssab;
        } else {
            throw new JSMSendException("Message "+this+" has already been sent",null);
        }
    }

    /**
     * Get the Future delegate. Not user-level.
     *
     * @return the object future
     */
    public Future getFuture() {
        return f;
    }

    /**
     * Get the AnswerBack delegate. Not user-level.
     *
     * @return the reference of the delegate
     */
    protected ServerSideAnswerBack getServerSideAnswerBack() {
        return ssab;
    }
}
