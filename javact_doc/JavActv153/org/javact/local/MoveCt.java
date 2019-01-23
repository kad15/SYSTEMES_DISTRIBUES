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
* Modified by : Sébastien LERICHE (JavAct 4.0 Delta)
* Modified by : S.L (4.1)
* Modified by SL - passage dans local (place logique + utilisation du SendCt.STD)
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.local;

import org.javact.lang.Agent;
import org.javact.lang.Controler;
import org.javact.lang.Forwarder;
import org.javact.lang.JavActComponent;
import org.javact.lang.MoveCtI;


/**
 * *META-COMPONENT-LEVEL* The standard local Move component.
 *
 * @version 1.5.3
 */
public class MoveCt extends JavActComponent implements MoveCtI {
    //reference de l'acteur précédant dans le chainage des forwarders
    private Agent prec;

    /**
     * Creates a new MoveCt object.
     */
    public MoveCt() {
        prec = null; //null au départ (bout de chaine)
    }

    /**
     * Ability of changing the hosting place. The original actor becomes a
     * relay towards the mobile actor
     *
     * @param p the new place
     */
    public void go(String p) {
        Agent a = myControler.getMyActor();
        Agent next = null;
        Forwarder f = null;
		
        //On the initial place, it remains only a Forwarder and a
        //component for message forwarding
        //Locking myControler prevents the invocation of its reveive() method;
        //then, its MailBox can be replaced by a Forwarder
        synchronized (myControler) {
            Agent auxPrec = prec;
            prec = a; //précédant pour l'acteur créé = acteur courant
            Controler oldc=myControler;

            //Creation of the following actor in the chain
            //comme on réutilise le composant Move courant, il faut sauvegarder la référence du controleur courant
       		//sinon ds le cas de création locale, cette référence est modifiée 
            next = myControler.create(p,
                    new Controler(myControler.getMyNextBehavior(),
                        myControler.getMyNextMailBoxCt(),
                        myControler.getMyNextBecomeCt(),
                        myControler.getMyNextCreateCt(),
                        myControler.getMyNextLifeCycleCt(),
                        myControler.getMyNextMoveCt(),
                        myControler.getMyNextSendCt(),
                        myControler.getMyMailQueue()));

            if (next != null) {
                f = new Forwarder(auxPrec, next);
                //on adapte les composants du controleur pour devenir un acteur relai
                // ie boite aux lettre = forwarder et comportement = null 
                oldc.setMyMailBoxCt(f);
                oldc.setMySendCt(SendCt.STD);
                oldc.setMyBehavior(null);
                oldc.setMyMoveCt(null);
				oldc.setMyBecomeCt(null);
				oldc.setMyCreateCt(null);
            }
        }

        //en dehors du synchronized pour éviter les interblocages
        if (f != null)
            f.shortcut();
    }
}
