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
* Modified by : Sébastien LERICHE
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.lang;

/**
 * *META-COMPONENT-LEVEL* Forwarder object, which replaces actor's mailbox in 
 * case of mobility. It serves as a relay in the message routing process.
 *
 * @version 0.4.0 Delta (optimisation du routage)
 */
public class Forwarder extends JavActComponent implements MailBoxCtI, Runnable {

    /** The next and prec actors in the chain of forwarders */
    Agent next;
    Agent prec;

    /**
     * Creates a new Forwarder from the following actor in the pipeline, and
     * from the object in charge of message forwarding.
     *
     * @param p the next actor in the chain of forwarders
     * @param n the object in charge of message forwarding
     */
    public Forwarder(Agent p, Agent n) {
        next = n;
        prec = p;
    }

    /**
     * This method is used to update the reference between the proxies of this
     * actor  and its current location. Use the run method (separate thread)
     */
    public void shortcut() {
        //Le shortcutting permet d'optimiser les liens des forwarder vers le
        //dernier acteur créé. Mais la mise à jour doit être faite dans un thread
        //séparé pour conserver de bonnes performances
        if (prec != null)
            new Thread(this).start();
    }

    /**
     * The thread for updating th references
     */
    public void run() {
        myControler.send(new MessageForwarder(next), prec);
    }

    /**
     * Message forwarding
     *
     * @param m the message to be forwarded
     */
    public void putMessage(Message m) {
        if (m instanceof MessageForwarder) {
            MessageForwarder mf = (MessageForwarder) m;
            next = mf.getNext();

            if (prec != null)
                myControler.send(mf, prec); // emission en chaine
        } else
            myControler.send(m, next);
    }

    /**
     * Mandatory, because of MailBoxCtI interface. Should not be invoked.
     *
     * @return null
     */
    public Message getMessage() {
        System.out.println("Forwarder.java " + this +
            " should not be invoked -method getMessage()-");
        return null;
    }

    /**
     * Mandatory, because of MailBoxCtI interface. Should not be invoked.
     *
     * @return true (always)
     */
    public boolean isEmpty() {
        return true;
    }
    
	/**
	 * Middleware-level message used to inform the Forwarders how to make shortcut 
	 * in the localization protocol. Middleware-level only.
	 *
	 * @author SL
	 * @version 0.4.1
	 */
	private class MessageForwarder implements Message {
		private Agent next = null;

		MessageForwarder(Agent next) {
			this.next = next;
		}

		Agent getNext() {
			return next;
		}

		// nécessaire car implements Message, mais ne doit pas etre invoquée
		public void handle(QuasiBehavior b) throws MessageHandleException {
			throw new MessageHandleException();
		}
	}
}
