/*
* ###########################################################################
* JavAct: A Java(TM) library for distributed and mobile actor-based computing
* Copyright (C) 2001-2004 I.R.I.T./C.N.R.S.-I.N.P.T.-U.P.S.
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
* Initial developer(s): The I.A.M. Team (I.R.I.T.)
* Contributor(s): The I.A.M. Team (I.R.I.T.)
* Contact: javact@irit.fr
* ###########################################################################
*/

package examples.net.rmi.fastMover;

import org.javact.lang.Actor;
import org.javact.lang.QuasiBehavior;
import org.javact.net.rmi.CreateCt;
import org.javact.net.rmi.DecideOn;
import org.javact.util.StandAlone;


class MoveBeh extends QuasiBehavior implements StandAlone {
    int i = 0;

    /**
     * Demo for the auto-activation mecanism (method run and StandAlone)
     */
    public void run() {
        String nextPlace;
        i++;
        System.out.println("[" + i + "] Je suis sur : " + myPlace());
        System.out.println("Je fais semblant de travailler...");

        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }

        nextPlace = DecideOn.randomPlace();
        System.out.println("Je repars vers " + nextPlace + " !");
        go(nextPlace);
    }
}


class FastMover {
    
    public static void main(String[] args) {
        Actor myMover = CreateCt.STD.create(new MoveBeh());
    }
}
