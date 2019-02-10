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
* Modified by : S.L -> version 4.2 (QAI->Life Cycle)
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.lang;

import org.javact.util.StandAlone;

/**
 * *META-COMPONENT-LEVEL* The standard LifeCycle component.
 *
 * @version 0.5.2
 */
public class LifeCycleCt
	extends JavActComponent
	implements LifeCycleCtI, Runnable {

	/**
	 * Launches the life cycle
	 */
	public void start() {
		// run a thread on this object
		new Thread(this).start();
	}

	/**
	 * Implementation of a standard life-cycle.
	 */
	public void run() { //public because of javac
		/**
		 * Il faut sauver la r�f�rence du contr�leur au moment de l'ex�cution de cette 
		 * m�thode, car la r�f�rence peut �tre chang�e par changeToNext() dans le cas 
		 * de mobilit� sur le m�me site, avec r�utilisation de ce composant 
		 * (et donc sa mise � jour du champ myControler)
		 */
		Controler myC = myControler;

		//autoactivation while behavior implements StandAlone (method void run())
		//note : on a previous update, I (SL) changed the 'if' statement for a 'while'
		//I don't remember why, but this surely introduce a big bug for StandAlone behaviors.
		//so... the 'if' is back !
		if ((myC.getMyBehavior()!=null) && (myC.getMyBehavior() instanceof StandAlone)) {
			((StandAlone) myControler.getMyBehavior()).run();
			changeToNext();
		}

		//actor's main loop
		while (myC.getMyBehavior() != null) {
			Message m = myControler.getMessage();

			try { //take a message in the MailBox, and run
				m.handle(myControler.getMyBehavior());
			} catch (MessageHandleException e) {
				System.out.println("Exception->LifeCycleCt.run : " + e);
				System.out.println("Actor " + myControler.getMyActor()
						+ " which behavior is " + myControler.getMyBehavior()
						+ " is unable to process "+ m + " => message given up.");
			}

			changeToNext();
		    	
			Thread.yield();
		}

		//here, the actor becomes a passive object (its thread dies)
		//which receive method remains, in case of suicide or after a move.
	}
	
	/**
	 * changeToNext tels the controler to update all its components and 
	 * to change the behavior and location of the actor if needed
	 * we check if an exception occurs while moving, to give back the control to the behavior
	 */
	private void changeToNext() {
		boolean error;
		do {
			error=false;
			try {myControler.changeToNext(); 		//next behavior, components and place 
			} catch (GoException e) {
				System.out.println("(LifeCycleCt) Unable to move to place : "+e.getPlace());
				myControler.go(null);							//move cancelled to avoid an infinite loop
				if (myControler.getMyHook() != null) {
					myControler.getMyHook().resume(e);	//hook within the Behavior to treat a goException
					error=true;
				}
				if (!error) e.printStackTrace();
			}
		} while (error);
	}
}
