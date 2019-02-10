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
import org.javact.net.rmi.CreateCt;
import org.javact.net.rmi.SendCt;
import org.javact.util.StandAlone;

/**
* Behaviour for the actor MobileChat
*/
class MobileChatBeh extends MobileChatQuasiBehavior implements StandAlone {

	private Agent dest;
	private String text;
	private transient MobileChatSwing ihm;
	private String[] list = JavActProbe.probe(1099);
	
	@Override
	public void run() {
		System.out.println("sur "+myPlace());
		ihm = new MobileChatSwing(this, ego(), list, text);
	}
	
	public void setDest(Agent dest) {
		this.dest=dest;
	}
	
	public void move(String place, String text2) {
		text=text2;
		go(place);
	}
	
	public void send(String msg) {
		send(new JAMspeak(msg), dest);
	}
	
	public void speak(String msg) {
		ihm.receive(msg);
	}

}

public class Skeleton1 {

	public static void main(String[] args) {
		Agent a1= CreateCt.STD.create("localhost",new MobileChatBeh());
		Agent a2 = CreateCt.STD.create("localhost",new MobileChatBeh());
		SendCt.STD.send(new JAMsetDest(a2),a1);
		SendCt.STD.send(new JAMsetDest(a1),a2);
	}

}
