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
 * *META-COMPONENT-LEVEL* Specification of a Create component. Implemented by 
 * CreateCt (in a package used for distribution). Implementable by a user 
 * meta-component-level class.
 *
 * @version 0.5.0
 */
public interface CreateCtI extends JavActComponentInt {
    
    /**
     * Actor creation from its behavior, somewhere in the network of places 
     * depending on the placement policy.
     *
     * @param b the behavior
     *
     * @return the reference of the created actor
     */
    public Actor create(QuasiBehavior b);
   

    /**
     * Actor creation from all its components. Somewhere in the network of
     * places depending on the placement policy.
     *
     * @param b the behavior
     * @param box the MailBox component
     * @param bec the Become component
     * @param lif the LifeCycle component
     * @param crt the Create component
     * @param mve the Move component
     * @param snd the Send component
     *
     * @return the reference of the created actor
     */
    public Actor create(QuasiBehavior b, MailBoxCtI box, BecomeCtI bec, CreateCtI crt,
			LifeCycleCtI lif, MoveCtI mve, SendCtI snd);

    /**
     * Actor creation from its behavior, on a given place.
     *
     * @param p the place for hosting the actor
     * @param b the behavior
     *
     * @return the reference of the created actor
     */
    public Actor create(String p, QuasiBehavior b);

    /**
     * Actor creation from a controler, on a given place. The behavior is included 
     * in the controler. Middleware-level only.
     *
     * @param p the place for hosting the actor
     * @param c the controler
     *
     * @return the reference of the created actor
     */
    public Actor create(String p, Controler c);

    /**
     * Actor creation from all its components, on a given place.
     *
     * @param p the place for hosting the actor
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
    public Actor create(String p, QuasiBehavior b, MailBoxCtI box, BecomeCtI bec,
			CreateCtI crt, LifeCycleCtI lif, MoveCtI mve, SendCtI snd);

}
