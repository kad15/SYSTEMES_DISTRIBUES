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
* Modified by : S.L -> version 4.1
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.net.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;

import org.javact.lang.Actor;
import org.javact.lang.BecomeCtI;
import org.javact.lang.Controler;
import org.javact.lang.CreateCtI;
import org.javact.lang.CreateException;
import org.javact.lang.JavActComponent;
import org.javact.lang.LifeCycleCtI;
import org.javact.lang.MailBoxCtI;
import org.javact.lang.MoveCtI;
import org.javact.lang.QuasiBehavior;
import org.javact.lang.SendCtI;





/**
 * *META-COMPONENT-LEVEL* (excepted STD). Standard Create component
 *
 * @author Patrick MORALES (version 2.x), modified by TER 2002 "TENEBRION"
 * @version 0.5.0
 */
public class CreateCt extends JavActComponent implements CreateCtI {
    /** Standard default Create component */
    public static final CreateCt STD = new CreateCt();

    /**
     * Creates a new CreateCt object.
     */
    public CreateCt() {
    }

    /**
     * Actor creation from a behavior
     *
     * @param b the behavior
     *
     * @return the network-level reference of the created actor
     */
    public Actor create(QuasiBehavior b) {
        // choose a place, and then create on it
        return create(DecideOn.randomPlace(), b);
    }

    /**
     * Actor creation from all its components.
     *
     * @param b the behavior
     * @param box the MailBox component
     * @param bec the Become component
     * @param crt the Create component
     * @param lif the Life Cycle component
     * @param mve the Move component
     * @param snd the Send component
     *
     * @return the network-level reference of the created actor
     */
    public Actor create(QuasiBehavior b, MailBoxCtI box, BecomeCtI bec,
        CreateCtI crt, LifeCycleCtI lif, MoveCtI mve, SendCtI snd) {
        // choose a place, and then create on it
        return create(DecideOn.randomPlace(), b, box, bec, crt, lif, mve, snd);
    }

   private CreatorInt lookup(String p) {
		CreatorInt leCreateur = null;
		 //	The reference of the Creator of the given place is dynamically 
		 // recovered via RMI. It's not efficient, but if we use a static table of references
		 //that would not allow dynamic addition and removal of places.
		 try {
			 leCreateur = (CreatorInt) Naming.lookup("rmi://" + p +"/CreateurDeJavAct");
		 } catch (Exception e) {
			 throw new CreateException("Unable to lookup the place " + p, e);
		 }
		return leCreateur;
   }
   
    /**
     * Actor creation from its behavior, on a given place.
     *
     * @param p a place (not taken in account)
     * @param b the behavior
     *
     * @return the network-level reference of the created actor
     *
     * @throws CreateException if the place is unreachable, or the remote operation failed
     */
    public Actor create(String p, QuasiBehavior b) {

        CreatorInt leCreateur = lookup(p);
        Actor refDisAct = null;

        try {
            refDisAct = leCreateur.createLocal(b);
        } catch (RemoteException e) {
            throw new CreateException("Unable to create the actor on " + p, e);
        }

        return refDisAct;
    }

    /**
     * Actor creation from all its components, on a given place.
     *
     * @param p a place (not taken in account)
     * @param b the behavior
     * @param box the MailBox component
     * @param bec the Become component
     * @param crt the Create component
     * @param lif the Life Cycle component
     * @param mve the Move component
     * @param snd the Send component
     *
     * @return the reference of the created actor
     *
     * @throws CreateException if the place is unreachable, or the remote operation failed
     */
    public Actor create(String p, QuasiBehavior b, MailBoxCtI box, BecomeCtI bec,
        CreateCtI crt, LifeCycleCtI lif, MoveCtI mve, SendCtI snd) {
       
        CreatorInt leCreateur = lookup(p);
        Actor refDisAct = null;

        try {
            refDisAct = leCreateur.createLocal(b, box, bec, crt, lif, mve, snd);
        } catch (RemoteException e) {
            throw new CreateException("Unable to create the actor on " + p, e);
        }

        return refDisAct;
    }

    /**
     * Actor creation from its controler, on a given place. The behavior is
     * included in the controler. Middleware-level only (used in MoveCt).
     *
     * @param p a place
     * @param c the controler
     *
     * @return the reference of the created actor
     *
     * @throws CreateException if the place is unreachable, or the remote operation failed
     */
    public Actor create(String p, Controler c) {

        CreatorInt leCreateur = lookup(p);
        Actor refDisAct = null;

        try {
            refDisAct = leCreateur.createLocal(c);
        } catch (RemoteException e) {
            throw new CreateException("Unable to create the actor on " + p, e);
        }

        return refDisAct;
    }
}
