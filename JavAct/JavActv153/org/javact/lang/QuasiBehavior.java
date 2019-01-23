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
* Modified by SL (new keywords)
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.lang;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Actors' primary behavior, with the standard available methods.
 * Extended by automatically-generated abstract users' base-level classes 
 * defining become(). Can be directly implemented by users' base-level classes
 * (using becomeAny() only).
 *
 * @version 0.5.0
 */
public abstract class QuasiBehavior implements Serializable {

	/** Reference of the owner actor */
	private transient Agent refToMyEgo;
	private transient Controler myControler = null;
	
	/**
	 * Creates a new QuasiBehavior.
	 */
	public QuasiBehavior() {
	}

	/**
	 * Allows the reference to the owner Controler to be set in the behavior. 
	 *
	 * @param c the owner Controler
	 */
	final void setMyControler(Controler c) {
		myControler = c;
		refToMyEgo = myControler.getMyActor();
	}

	/**
	 * QuasiBehavior changing ability
	 *
	 * @param b the behavior for the next message
	 */
	protected final void becomeAny(QuasiBehavior b) {
		myControler.become(b);
	}

	/** 
	* To change the next mailbox component
	* 
	* @param box the mailbox component used with the next message
	* 
	*/ 
	protected final void with(MailBoxCtI box) {
		myControler.with(box);
	}

	/**
	 * To change the next become component
	 *
	 * @param bec the become component used with the next message
	 */
	protected final void with(BecomeCtI bec) {
		myControler.with(bec);
	}

	/**
	 * To change the next create component
	 *
	 * @param crt the create component used with the next message
	 */
	protected final void with(CreateCtI crt) {
		myControler.with(crt);
	}

	/**
	 * To change the next move component
	 *
	 * @param mve the move component used with the next message
	 */
	protected final void with(MoveCtI mve) {
		myControler.with(mve);
	}

	/**
	 * To change the next send component
	 *
	 * @param snd the send component used with the next message
	 */
	protected final void with(SendCtI snd) {
		myControler.with(snd);
	}

	/**
	 * To change the next life cycle component
	 *
	 * @param lif the life cycle component used with the next message
	 */
	protected final void with(LifeCycleCtI lif) {
		myControler.with(lif);
	}

	/**
	 * Actor creation ability (from its behavior)
	 *
	 * @param b the initial behavior
	 *
	 * @return the created actor
	 */
	protected final Actor create(QuasiBehavior b) {
		return myControler.create(b);
	}

	/**
	 * Actor creation ability  (from a specific location and a behavior)
	 *
	 * @param p the place for hosting the actor
	 * @param b the initial behavior
	 *
	 * @return the created actor
	 */
	protected final Actor create(String p, QuasiBehavior b) {
		return myControler.create(p, b);
	}

	/**
	 * Actor creation ability (from a specific location and a set of components)
	 *
	 * @param p the place for hosting the actor
	 * @param b the initial behavior
	 * @param box the MailBox Component
	 * @param bec the Become Component
	 * @param crt the Create Component
	 * @param lif the Life Cycle Component
	 * @param mve the Move Component
	 * @param snd the Send Component
	 *
	 * @return the created actor
	 */
	protected final Actor create(
		String p,
		QuasiBehavior b,
		MailBoxCtI box,
		BecomeCtI bec,
		CreateCtI crt,
		LifeCycleCtI lif,
		MoveCtI mve,
		SendCtI snd) {
		return myControler.create(p, b, box, bec, crt, lif, mve, snd);
	}

	/**
	 * Message sending ability
	 *
	 * @param m the message
	 * @param a the target actor
	 */
	protected final void send(Message m, Agent a) {
		myControler.send(m, a);
	}

	/**
	 * Message sending ability, with a requested reply
	 * This method is mandatory because java resolves overloading methods 
	 * with a static computation of the argument, otherwise we won't get to the right send() method.
	 * @param mwr the message
	 * @param a the target actor
	 */
	protected final void send(MessageWithReply mwr, Agent a) {
		myControler.send(mwr, a);
	}

	/**
	 * Actor suicide ability. The actor suicides after the current message has
	 * been processed
	 */
	protected final void suicide() {
		myControler.suicide();
	}

	/**
	 * Actor's proactive mobility. Next message is processed on the new place.
	 *
	 * @param p the next place
	 */
	protected final void go(String p) {
		myControler.go(p);
	}
	
	/**
	* To set the next place of the actor (for the next message).
	* Warning ! This methods never fails, the code beyond it will ever be executed.
	* The mobility is done when the execution of the behavior is terminated, and the hook is called at this moment if an error occurs 
	* 
	* @param p the place where the actor will go
	* @param h method resume() of the hook is called if if the mobility process fails
	*/
	protected final void go(String p, HookInterface h) {
		myControler.go(p, h);
	}

	/**
	 * Get the reference of the owner actor
	 *
	 * @return the reference of the owner actor
	 */
	protected final Agent ego() {
		return refToMyEgo;
	}

	/**
	 * Get the place whereon the owner actor is located.  The port is included
	 * since v4.1
	 *
	 * @return the place (an URL as a String)
	 */
	protected final String myPlace() {
		String aux = null;
		String port;

		try {
			aux = InetAddress.getLocalHost().getHostName();
			port = System.getProperty("JAVACT_PORT");

			if (port != null)
				aux += port;
		} catch (UnknownHostException e) {
			System.out.println("Exception -> ");
			e.printStackTrace();
		}

		return aux;
	}
	
	/**
	 * 
	 * @author leriche
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */
}
