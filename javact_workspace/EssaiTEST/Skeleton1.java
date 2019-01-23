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

import org.javact.lang.*;
import org.javact.local.CreateCt;
import org.javact.local.SendCt;
import org.javact.util.StandAlone;

/**
* Behaviour for the actor toto
*/
class totoBeh extends totoQuasiBehavior implements StandAlone {

	@Override
	public void  run () {
		send(new JAMstop(),ego()); //je m'envoie le message stop
		System.out.println("run run for your life");
		
	}
	
	public void hello() {
		System.out.println("on m'a créé vivant !");
	}
	
	public void stop() {
		System.out.println("je me suicide !");
		suicide();
		System.out.println("toto");
	}
	
	

}

public class Skeleton1 {

	public static void main(String[] args) {
		Agent a=CreateCt.STD.create(new totoBeh());
		SendCt.STD.send(new JAMhello(), a);
		SendCt.STD.send(new JAMstop(), a);
	}

}