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
* Initial developer(s): Sébastien LERICHE / The I.A.M. Team (I.R.I.T.) - SMAC Team (IRIT) since 2007
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.lang;

import java.io.Serializable;
import java.util.Vector;

/**
 * The controler of an actor. It contains the links to the (delegate)
 * components. It's used as a connector. Middleware-level only.
 *
 * @author SL
 * @version 0.5.0
 */
public final class Controler implements Serializable {
	
	// myActor=reference de l'acteur propriétaire du controler
	// ainsi que référence du composant receive (transient car devant rester là où il a été créé)
	private transient Agent myActor = null;
	private MailBoxCtI myMailBoxCt;
	private MailBoxCtI nextMB;
	private BecomeCtI myBecomeCt;
	private BecomeCtI nextBC;
	private CreateCtI myCreateCt;
	private CreateCtI nextCR;
	private MoveCtI myMoveCt;
	private MoveCtI nextMV;
	private SendCtI mySendCt;
	private SendCtI nextSD;
	private LifeCycleCtI myLifeCycleCt;
	private LifeCycleCtI nextLC;
	private QuasiBehavior myBehavior;
	private QuasiBehavior myNextBehavior;
	private transient HookInterface behaviorHook = null;

	//vaut null tt qu'on a pas utilisé on(nextplace)
	//et après un déplacement
	private transient String myNextPlace = null;

	//sorti de la BAL pour pouvoir faire des chgts de cpts BAL ss perdre des msgs
	/** Vector containing the messages not used yet */
	private Vector mailQueue = new Vector();

	//public à cause des accès depuis les paquetages local / rmi
	/**
	 * Builds an actor by linking all its components to the controler, and
	 * starts its life cycle.
	 *
	 * @param beh the behavior
	 * @param box the MailBox component
	 * @param bec the Become component
	 * @param crt the Create component
	 * @param lif the LifeCycle component
	 * @param mve the Move component
	 * @param snd the Send component
	 * @param a the Receive component - the true reference of the actor
	 */
	public Controler(QuasiBehavior beh, MailBoxCtI box, BecomeCtI bec, CreateCtI crt,
		LifeCycleCtI lif, MoveCtI mve, SendCtI snd, Agent a) {

		myActor = a;
		//initialisation du composant receive, qui devrait être non null
		((JavActComponent) a).setMyControler(this);

		myNextPlace = null;

	   	setMyBehavior(beh);
		setMyMailBoxCt(box);
		setMyBecomeCt(bec);
		setMyCreateCt(crt);
		setMyLifeCycleCt(lif);
		setMyMoveCt(mve);
		setMySendCt(snd);

		//lancement du cycle de vie si beh!=null (sinon c'est un forwarder)
		if (myBehavior != null)
			myLifeCycleCt.start();
	}

	/**
	 * Builds an actor by linking all its next components to the controler.
	 * Used in the mobility mechanism (MoveCt). After that the method
	 * <code>changeToNextForceReInit()</code> is invoked.
	 *
	 * @param nbeh the next behavior
	 * @param nbox the next MailBox component
	 * @param nbec the next Become component
	 * @param ncrt the next Create component
	 * @param nlif the next LifeCycle component
	 * @param nmve the next Move component
	 * @param nsnd the next Send component
	 * @param mq the mail queue with the messages not read
	 */
	public Controler(QuasiBehavior nbeh, MailBoxCtI nbox, BecomeCtI nbec,CreateCtI ncrt,
		LifeCycleCtI nlif, MoveCtI nmve, SendCtI nsnd, Vector mq) {
		myNextBehavior = nbeh;
		nextMB = nbox;
		nextBC = nbec;
		nextCR = ncrt;
		nextLC = nlif;
		nextMV = nmve;
		nextSD = nsnd;
		mailQueue = mq;
	}
	
	/**
	 * Used by the life cycle to change the update the components. Triggers
	 * mobility if needed.
	 */
	public void changeToNext() {
		if (myNextPlace != null) {
			//si on a utilisé la primitive on, on se déplace simplement.
			//le changement de composants est effectué sur le site d'arrivée
			myMoveCt.go(myNextPlace); 
			return;
		}

		setMyBehavior(myNextBehavior);
		setMyMailBoxCt(nextMB);
		setMyBecomeCt(nextBC);
		setMyCreateCt(nextCR);
		setMyMoveCt(nextMV);
		setMySendCt(nextSD);
		setMyLifeCycleCt(nextLC);
	}

	/**
	 * After mobility, this method updates controler's references in
	 * components, invokes <code>changeToNext()</code>, and re-starts the life
	 * cycle.
	 *
	 * @param a the reference of the actor, its receive component
	 */
	public void changeToNextForceReInit(Agent a) {
		myActor = a;
		((JavActComponentInt) a).setMyControler(this);

		myNextPlace = null;
		
		//changement des composants, sans déplacement puisque myNextPlace==null
		changeToNext();

		//on relance le cycle de vie
		myLifeCycleCt.start();
	}

	//------------------------------------------------------------------------------
	//encapsulation des méthodes d'accès aux composants
	// public car doivent etre accessibles meme aux composants ecrits en dehors de
	// org.javact.lang
	//Send

	/**
	 * Sends a message <code>m</code> to <code>a</code>with the send component.
	 *
	 * @param m the message (JAM) to send
	 * @param a the receiver
	 */
	public void send(Message m, Agent a) {
		mySendCt.send(m, a);
	}

	/**
	 * Sends a message <code>m</code> to <code>a</code>with the send component.
	 *
	 * @param m the message (JSM) to send
	 * @param a the receiver
	 */
	public void send(MessageWithReply m, Agent a) {
		mySendCt.send(m, a);
	}


	/**
	 * To get a message from the mailbox. This method blocks and wait until a
	 * message can be proceeded.
	 * 
	 * <p>
	 * Should be used only by the LifeCycleCt
	 * </p>
	 *
	 * @return a message from the mailbox
	 */
	protected synchronized Message getMessage() {
		while (myMailBoxCt.isEmpty())
			try { wait(); } catch (Exception e) {}
		return myMailBoxCt.getMessage();
	}

	/**
	 * This method is called when the actor receives a message. If so, the
	 * waiting  thread (if it exists) is notified.
	 *
	 * @param m the received message
	 */
	public synchronized void receive(Message m) {
		myMailBoxCt.putMessage(m);
		notify();
	}

	/**
	 * To change the behavior (for the next message).
	 *
	 * @param b behavior
	 */
	public void become(QuasiBehavior b) {
		myBecomeCt.become(b);
	}

	/**
	 * To kill itself
	 */
	public void suicide() {
		myBecomeCt.suicide();
	}

	/**
	 * Creates an actor from a behavior.
	 *
	 * @param b the future behavior
	 *
	 * @return the reference of the new actor
	 */
	public Actor create(QuasiBehavior b) {
		return myCreateCt.create(b);
	}

	/**
	 * Creates an actor on a given place from a behavior.
	 *
	 * @param p the place where the actor will be created
	 * @param b behavior
	 *
	 * @return the reference of the new actor
	 */
	public Actor create(String p, QuasiBehavior b) {
		return myCreateCt.create(p, b);
	}

	/**
	 * Creates an actor on a given place with the specified components.
	 *
	 * @param p place where the actor will be created
	 * @param b behavior
	 * @param box the MailBox component
	 * @param bec the Become component
	 * @param crt the Create component
	 * @param lif the LifeCycle component
	 * @param mve the Move component
	 * @param snd the Send component
	 *
	 * @return the reference of the new actor
	 */
	public Actor create(
		String p,
		QuasiBehavior b,
		MailBoxCtI box,
		BecomeCtI bec,
		CreateCtI crt,
		LifeCycleCtI lif,
		MoveCtI mve,
		SendCtI snd) {
		return myCreateCt.create(p, b, box, bec, crt, lif, mve, snd);
	}

	/**
	 * Actor creation from a controler, on a given place. The behavior is
	 * included  in the controler. Middleware-level only.
	 *
	 * @param p the place where the actor will be created
	 * @param c the controler
	 *
	 * @return the reference of the new actor
	 */
	public Actor create(String p, Controler c) {
		return myCreateCt.create(p, c);
	}

	/**
	 * To set the next place of the actor (for the next message).
	 *
	 * @param p the place where the actor will go
	 */
	public void go(String p) {
		myNextPlace = p; //on diffère le déplacement
	}


	/**
	 * To set the next place of the actor (for the next message).
	 * Warning ! This methods never fails, the code beyond it will ever be executed.
	 * The mobility is done when the execution of the behavior is terminated, and the hook is called at this moment if an error occurs 
	 * 
	 * @param p the place where the actor will go
	 * @param h method resume() of the hook is called if if the mobility process fails
	 */
	public void go(String p, HookInterface h) {
		go(p);
		behaviorHook=h;
	}

	/**
	 * User level adaptation of the mailbox component
	 *
	 * @param box the mailbox component used with the next message
	 */
	public void with(MailBoxCtI box) {
		nextMB = box;

		if (nextMB != null)
			nextMB.setMyControler(this);
	}

	/**
	 * User level adaptation of the become component
	 *
	 * @param bec the become component used with the next message
	 */
	public void with(BecomeCtI bec) {
		myBecomeCt.with(bec);
	}

	/**
	 * User level adaptation of the create component
	 *
	 * @param crt the create component used with the next message
	 */
	public void with(CreateCtI crt) {
		myBecomeCt.with(crt);
	}

	/**
	 * User level adaptation of the move component
	 *
	 * @param mve the move component used with the next message
	 */
	public void with(MoveCtI mve) {
		myBecomeCt.with(mve);
	}

	/**
	 * User level adaptation of the send component
	 *
	 * @param snd the send component used with the next message
	 */
	public void with(SendCtI snd) {
		myBecomeCt.with(snd);
	}

	/**
	 * User level adaptation of the life cycle component
	 *
	 * @param lif the life cycle component used with the next message
	 */
	public void with(LifeCycleCtI lif) {
		myBecomeCt.with(lif);
	}

	//------------------------------------------------------------------------------

	/**
	 * To get the reference of the actor owning this controler.
	 *
	 * @return the actor
	 */
	public Agent getMyActor() {
		return myActor;
	}

	/**
	      *
	 * @return the Become component
	 */
	public BecomeCtI getMyBecomeCt() {
		return myBecomeCt;
	}

	/**
     *
	 * @return the current QuasiBehavior
	 */
	public QuasiBehavior getMyBehavior() {
		return myBehavior;
	}

	/**
     *
	 * @return the Create component
	 */
	public CreateCtI getMyCreateCt() {
		return myCreateCt;
	}

	/**
     *
	 * @return the MailBox component
	 */
	public MailBoxCtI getMyMailBoxCt() {
		return myMailBoxCt;
	}

	/**
     *
	 * @return the vector used to store the message for the mailbox
	 */
	public Vector getMyMailQueue() {
		return mailQueue;
	}

	/**
     *
	 * @return the Move component
	 */
	public MoveCtI getMyMoveCt() {
		return myMoveCt;
	}

	/**
     *
	 * @return the Send component
	 */
	public SendCtI getMySendCt() {
		return mySendCt;
	}

	/**
     *
	 * @return the LifeCycle component
	 */
	public LifeCycleCtI getMyLifeCycleCt() {
		return myLifeCycleCt;
	}

	//----------------------- get pour les nextXXXCt ------------------------

	/**
     *
	 * @return the next QuasiBehavior of the actor
	 */
	public QuasiBehavior getMyNextBehavior() {
		return myNextBehavior;
	}

	/**
     *
	 * @return the next MailBox component of this actor
	 */
	public MailBoxCtI getMyNextMailBoxCt() {
		return nextMB;
	}

	/**
     *
	 * @return the next Become component of this actor
	 */
	public BecomeCtI getMyNextBecomeCt() {
		return nextBC;
	}

	/**
     *
	 * @return the next Create component of this actor
	 */
	public CreateCtI getMyNextCreateCt() {
		return nextCR;
	}

	/**
     *
	 * @return the next Move component of this actor
	 */
	public MoveCtI getMyNextMoveCt() {
		return nextMV;
	}

	/**
     *
	 * @return the next Send component of this actor
	 */
	public SendCtI getMyNextSendCt() {
		return nextSD;
	}

	/**
     *
	 * @return the next LifeCycle component of this actor
	 */
	public LifeCycleCtI getMyNextLifeCycleCt() {
		return nextLC;
	}

	/**
     *
	 * @return the next place of this actor
	 */
	public String getMyNextPlace() {
		return myNextPlace;
	}
	
	public HookInterface getMyHook() {
		return behaviorHook;
	}

	//-------------------------------------------------------------------------------------------------------------------

	/* REM : les composants peuvent etre changés après avoir appelé la métode terminate()
	 * Les nouveaux sont ensuite initialisés avec la référence du controleur (this)
	 */

	/**
     *
	 * @param bec the new Become component
	 */
	public void setMyBecomeCt(BecomeCtI bec) {
		if (myBecomeCt != null && myBecomeCt != bec)
			myBecomeCt.terminate();

		myBecomeCt = bec;

		if (myBecomeCt != null)
			myBecomeCt.setMyControler(this);

		nextBC = myBecomeCt;
	}

	/**
     *
	 * @param crt the new Create component
	 */
	public void setMyCreateCt(CreateCtI crt) {
		if (myCreateCt != null && myCreateCt != crt)
			myCreateCt.terminate();

		myCreateCt = crt;

		if (myCreateCt != null)
			myCreateCt.setMyControler(this);

		nextCR = myCreateCt;
	}

	/**
     *
	 * @param lif the new LifeCycle component
	 */
	public void setMyLifeCycleCt(LifeCycleCtI lif) {
		if (myLifeCycleCt != null && myLifeCycleCt != lif)
			myLifeCycleCt.terminate();

		myLifeCycleCt = lif;

		if (myLifeCycleCt != null)
			myLifeCycleCt.setMyControler(this);

		nextLC = myLifeCycleCt;
	}

	/**
	 * This method is called when the actor receives a message. If so, the
	 * waiting  thread (if it exists) is notified.      When changing the
	 * mailbox, the waiting thread -inside getMessage()- (if it  exists) is
	 * notified, because the isEmpty() semantic could have changed
	 *
	 * @param mail the new MailBox component
	 */
	public synchronized void setMyMailBoxCt(MailBoxCtI mail) {
		if (myMailBoxCt != null && myMailBoxCt != mail)
			myMailBoxCt.terminate();

		myMailBoxCt = mail;

		if (myMailBoxCt != null) {
			myMailBoxCt.setMyControler(this);
			notify();
		}

		nextMB = myMailBoxCt;
	}

	/**
     *
	 * @param mve the new Move component
	 */
	public void setMyMoveCt(MoveCtI mve) {
		if (myMoveCt != null && myMoveCt != mve)
			myMoveCt.terminate();

		myMoveCt = mve;

		if (myMoveCt != null)
			myMoveCt.setMyControler(this);

		nextMV = myMoveCt;
	}

	/**
     *
	 * @param snd the new Send component
	 */
	public void setMySendCt(SendCtI snd) {
		if (mySendCt != null && mySendCt != snd)
			mySendCt.terminate();

		mySendCt = snd;

		if (mySendCt != null)
			mySendCt.setMyControler(this);

		nextSD = mySendCt;
	}

	/**
	 * Changes the current QuasiBehavior
	 *
	 * @param beh the behavior
	 */
	public void setMyBehavior(QuasiBehavior beh) {
		myBehavior = beh;

		if (myBehavior != null)
			myBehavior.setMyControler(this);

		myNextBehavior = beh;
	}

//	----------------------- set XXX next components ----------------------------

	/**
	 * Change the next QuasiBehavior
	 *
	 * @param nextBeh the next behavior
	 */
	public void setMyNextBehavior(QuasiBehavior nextBeh) {
		myNextBehavior = nextBeh;

		if (myNextBehavior != null)
			myNextBehavior.setMyControler(this);
	}
	
	/**
	 * Adaptation of the mailbox component
	 *
	 * @param box the mailbox component used with the next message
	 */
	public void setMyNextMailBoxCt(MailBoxCtI box) {
		nextMB = box;
	}

	/**
	 * Adaptation of the become component
	 *
	 * @param bec the become component used with the next message
	 */
	public void setMyNextBecomeCt(BecomeCtI bec) {
		nextBC = bec;
	}

	/**
	 * Adaptation of the create component
	 *
	 * @param crt the create component used with the next message
	 */
	public void setMyNextCreateCt(CreateCtI crt) {
		nextCR = crt;
	}

	/**
	 * Adaptation of the move component
	 *
	 * @param mve the move component used with the next message
	 */
	public void setMyNextMoveCt(MoveCtI mve) {
		nextMV = mve;
	}

	/**
	 * Adaptation of the send component
	 *
	 * @param snd the send component used with the next message
	 */
	public void setMyNextSendCt(SendCtI snd) {
		nextSD = snd;
	}

	/**
	 * Adaptation of the life cycle component
	 *
	 * @param lif the life cycle component used with the next message
	 */
	public void setMyNextLifeCycleCt(LifeCycleCtI lif) {
		nextLC = lif;
	}
}
