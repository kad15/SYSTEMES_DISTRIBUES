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
* Modified by : S.L : version 4.2
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.local;

import org.javact.lang.JavActComponent;
import org.javact.lang.Message;


/**
 * The receive component of an actor. Implements LocalActor (consequently, Actor).
 * This component can't be adapted. Middleware-level only.
 *
 * @version 0.5.0
 */
public class ReceiveCt extends JavActComponent implements LocalActor {
    
    /**
     * Reception of standard messages, and storage in the MailBox.
     *
     * @param m the message
     */
    public final void receive(Message m) {
        myControler.receive(m);
    }
}
