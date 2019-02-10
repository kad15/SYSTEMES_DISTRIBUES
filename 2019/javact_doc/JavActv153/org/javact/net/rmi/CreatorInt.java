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

package org.javact.net.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.javact.lang.Actor;
import org.javact.lang.BecomeCtI;
import org.javact.lang.Controler;
import org.javact.lang.CreateCtI;
import org.javact.lang.LifeCycleCtI;
import org.javact.lang.MailBoxCtI;
import org.javact.lang.MoveCtI;
import org.javact.lang.QuasiBehavior;
import org.javact.lang.SendCtI;





/**
 * CreatorInt.java CreatorInt est l'interface définissant la méthode RMI de
 * création d'acteur distant.
 *
 * @author Patrick MORALES (version 2.x, 2001), modified
 * @version 0.4.0
 */
public interface CreatorInt extends Remote {
    /**
     * méthode de création locale
     *
     * @param beh Le comportement associé à l'acteur
     * @param box Le composant boite à lettre de l'acteur.
     *
     * @return La référence (RefActor) de l'acteur créé.
     *
     * @throws RemoteException exception RMI
     */
    public Actor createLocal(QuasiBehavior beh) throws RemoteException;

    /**
     * méthode de création locale
     *
     * @param beh Le comportement associé à l'acteur
     * @param box Le composant boite à lettre de l'acteur.
     * @param bec Le composant become de l'acteur.
     * @param crt Le composant create de l'acteur.
     * @param mve Le composant move de l'acteur.
     * @param snd Le composant send de l'acteur.
     *
     * @return La référence (Actor) de l'acteur créé.
     *
     * @throws RemoteException exception RMI
     */
    public Actor createLocal(QuasiBehavior beh, MailBoxCtI box, BecomeCtI bec,
        CreateCtI crt, LifeCycleCtI lif, MoveCtI mve, SendCtI snd) throws RemoteException;

    /**
     * Création effective d'un acteur local suite à une demande qui peut être
     * distante.
     *
     * @param c the controler
     * @return La référence (Actor) de l'acteur créé.
     *
     * @throws RemoteException exception RMI
     */
    public Actor createLocal(Controler c)
        throws RemoteException;
}
