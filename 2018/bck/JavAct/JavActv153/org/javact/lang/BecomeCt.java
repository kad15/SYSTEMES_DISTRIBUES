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
* Modified by : S.L -> version 4.2 (chgt dynamique de cpts)
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.lang;

/**
 * *META-COMPONENT-LEVEL* The standard Become component
 *
 * @version 0.5.0
 */
public class BecomeCt extends JavActComponent implements BecomeCtI {
    /**
     * QuasiBehavior changing (setting the behavior for the next message)
     *
     * @param b its new behavior
     */
    public void become(QuasiBehavior b) {
        myControler.setMyNextBehavior(b);
    }

    /**
     * Adaptation of the mailbox component
     *
     * @param box the mailbox component used with the next message
     */
    public void with(MailBoxCtI box) {
        myControler.setMyNextMailBoxCt(box);
    }

    /**
     * Adaptation of the become component
     *
     * @param bec the become component used with the next message
     */
    public void with(BecomeCtI bec) {
		myControler.setMyNextBecomeCt(bec);
    }

    /**
     * Adaptation of the create component
     *
     * @param crt the create component used with the next message
     */
    public void with(CreateCtI crt) {
		myControler.setMyNextCreateCt(crt);
    }

    /**
     * Adaptation of the move component
     *
     * @param mve the move component used with the next message
     */
    public void with(MoveCtI mve) {
		myControler.setMyNextMoveCt(mve);
    }

    /**
     * Adaptation of the send component
     *
     * @param snd the send component used with the next message
     */
    public void with(SendCtI snd) {
		myControler.setMyNextSendCt(snd);
    }

    /**
     * Adaptation of the life cycle component
     *
     * @param lif the life cycle component used with the next message
     */
    public void with(LifeCycleCtI lif) {
		myControler.setMyNextLifeCycleCt(lif);
    }

    /**
     * Actor suicide (stopped after current message has been processed)
     */
    public void suicide() {
        myControler.setMyNextBehavior(null);
    }
}
