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
 * *META-COMPONENT-LEVEL* Specification of a Send component. Implemented by 
 * SendCt (in a package used for distribution). Implementable by a user 
 * meta-component-level class.
 *
 * @version 0.4.1
 */
public interface SendCtI extends JavActComponentInt {
    /**
     * Sending of standard messages
     *
     * @param m the message
     * @param a target actor
     *
     */
    public abstract void send(Message m, Agent a);

    /**
     * Sending of messages on which a reply can be requested (client-server-like 
     * interaction).
     *
     * @param m the message
     * @param a target actor
     *
     */
    public abstract void send(MessageWithReply m, Agent a);
}
