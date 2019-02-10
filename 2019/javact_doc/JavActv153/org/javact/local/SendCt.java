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

import org.javact.lang.Agent;
import org.javact.lang.FutureInitializer;
import org.javact.lang.Message;
import org.javact.lang.MessageWithReply;
import org.javact.lang.SendCtI;
import org.javact.lang.SendException;


/**
 * META-COMPONENT-LEVEL (excepted STD). Standard Send component.
 *
 * @version 0.5.0
 */
public class SendCt extends FutureInitializer implements SendCtI {
    /** Standard default Send component ( singleton ) */
    public static final SendCt STD = new SendCt();

    /**
     * Creates a new SendCt object.
     */
    public SendCt() {
    }

    /**
     * Sending of standard messages
     *
     * @param m the message
     * @param target the target actor
     *
     * @throws SendException if m or target is null, or if an error occured while sending the message
     */
    public void send(Message m, Agent target) {
        if (target == null)
            throw new SendException("Target of send() is null", null);

        if (m == null)
            throw new SendException("Message of send() is null", null);

        ((LocalActor) target).receive(m);
    }

    /**
     * Sending of messages for which a reply can be requested. The catched
     * exception corresponds to a message sent several times.
     *
     * @param m the message
     * @param target the target actor
     *
     * @throws SendException if m or target is null, or if an error occured while sending the message
     */
    public void send(MessageWithReply m, Agent target) {
        if (target == null)
            throw new SendException("Target of send() is null", null);

        if (m == null)
            throw new SendException("Message of send() is null", null);

        //creation of the client-side delegate
        ClientSideAnswerBackImpl csab = new ClientSideAnswerBackImpl();

        //creation of the server-side delegate
        ServerSideAnswerBackImpl ssab = new ServerSideAnswerBackImpl(csab);

        //Initialization of the Future in the MessageWithReply object
        //casb is viewed as a Future, and ssab as a ServerSideAnswerBack
        initMWR(m, csab, ssab);

        //message is sent there
        ((LocalActor) target).receive(m);
    }
}
