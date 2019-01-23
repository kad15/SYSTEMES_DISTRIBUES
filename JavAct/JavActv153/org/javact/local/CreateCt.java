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

import org.javact.lang.Actor;
import org.javact.lang.BecomeCt;
import org.javact.lang.BecomeCtI;
import org.javact.lang.Controler;
import org.javact.lang.CreateCtI;
import org.javact.lang.JavActComponent;
import org.javact.lang.LifeCycleCt;
import org.javact.lang.LifeCycleCtI;
import org.javact.lang.MailBoxCt;
import org.javact.lang.MailBoxCtI;
import org.javact.lang.MoveCtI;
import org.javact.lang.QuasiBehavior;
import org.javact.lang.SendCtI;


/**
 * *META-COMPONENT-LEVEL* (excepted STD). Standard Create component.
 *
 * @version 0.4.1
 */
public class CreateCt extends JavActComponent implements CreateCtI {

    /** 
     * Standard default Create component
     */
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
     * @return the reference of the created actor
     */
    public Actor create(QuasiBehavior b) {
        Actor a = new ReceiveCt();
        new Controler(b, new MailBoxCt(), new BecomeCt(), new CreateCt(),
            new LifeCycleCt(), new MoveCt(), new SendCt(), a);

        return a;
    }

    /**
     * Actor creation from all its components.
     *
     * @param b the behavior
     * @param box the MailBox component
     * @param bec the Become component
     * @param crt the Create component
     * @param lif the LifeCycle component
     * @param mve the Move component
     * @param snd the Send component
     *
     * @return the reference of the created actor
     */
    public Actor create(QuasiBehavior b, MailBoxCtI box, BecomeCtI bec,
        CreateCtI crt, LifeCycleCtI lif, MoveCtI mve, SendCtI snd) {
        Actor a = new ReceiveCt();
        new Controler(b, box, bec, crt, lif, mve, snd, a);

        return a;
    }

    /**
     * Actor creation from its behavior. Creation is local to the JVM (the
     * given place is not considered). In this package, this method  exists
     * only for compatibility of programs with packages for true distribution.
     *
     * @param p a place (not taken in account)
     * @param b the behavior
     *
     * @return the reference of the created actor
     */
    public Actor create(String p, QuasiBehavior b) {
        return create(b);
    }

    /**
     * Actor creation from its controler. The behavior is included in the controler.
     * Middleware-level only (used in MoveCt).
     * Creation is local to the JVM (the given place is not considered).
     * In this package, this method exists only for compatibility of programs with 
     * packages for true distribution.
     *
     * @param p a place (not taken in account)
     * @param c the controler
     *
     * @return the reference of the created actor
     */
    public Actor create(String p, Controler c) {
        Actor a = new ReceiveCt();
        c.changeToNextForceReInit(a);
        return a;
    }

    /**
     * Actor creation from all its components, with a place. Creation is local
     * to the JVM (the given place is not considered). In this package, this
     * method exists only for compatibility of programs with packages for true
     * distribution.
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
     */
    public Actor create(String p, QuasiBehavior b, MailBoxCtI box, BecomeCtI bec,
        CreateCtI crt, LifeCycleCtI lif, MoveCtI mve, SendCtI snd) {
        return create(p, b, box, bec, crt, lif, mve, snd);
    }
}
