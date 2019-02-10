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
* Modified by : Sébastien LERICHE (4.1, deplacement du Vector mailQueue -> Controler)
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.lang;

import java.util.NoSuchElementException;
import java.util.Vector;


/**
 * *META-COMPONENT-LEVEL* The standard MailBox component.
 *
 * @version 0.4.1
 */
public class MailBoxCt extends JavActComponent implements MailBoxCtI {
    /** The message container */
    private transient Vector mailQueue = null; //reference de la BAL du controleur

    /**
     * Creates a new MailBoxCt object.
     */
    public MailBoxCt() {
    }

    //on ne connait le controleur qu'apres appel de cette methode
    //ce n'est qu'a partir de la qu'on peut recuperer la mailQueue
    //on suppose que tant que le controleur n'est pas changé le lien reste valide
    public void setMyControler(Controler c) {
        super.setMyControler(c);
        mailQueue = myControler.getMyMailQueue();
    }

    /**
     * Inserts a message at the end of the queue.
     *
     * @param m the Message
     */
    public void putMessage(Message m) {
        mailQueue.addElement(m);
    }

    /**
     * Returns the first message in the queue and removes it from the queue.
     * Can't be invoked concurrently (only the owner actor invokes it) Can't
     * be invoked when the queue is empty
     *
     * @return the first Message in the queue
     */
    public Message getMessage() {
        Message theMessage = null;

        try {
            theMessage = (Message) mailQueue.firstElement();
        } catch (NoSuchElementException e) {
            System.out.println("Exception -> " + e);
            e.printStackTrace();
        }

        try {
            mailQueue.removeElementAt(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception -> " + e);
            e.printStackTrace();
        }

        return theMessage;
    }

    /**
     * Control if the queue is empty or not.
     *
     * @return true if a message is present in the mailQueue
     */
    public boolean isEmpty() {
        return mailQueue.isEmpty();
    }
}
