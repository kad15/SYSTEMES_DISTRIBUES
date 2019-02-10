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

package examples.net.rmi.mobileBrowser;

import java.io.Serializable;
import java.util.Vector;

import org.javact.lang.Actor;
import org.javact.lang.GoException;
import org.javact.lang.HookInterface;
import org.javact.net.rmi.CreateCt;
import org.javact.net.rmi.SendCt;
import org.javact.util.StandAlone;

class Info implements Serializable {
	private long freeMemory;
	private String place;

	public Info(String place, long freeMemory) {
		this.place = place;
		this.freeMemory = freeMemory;
	}

	long getFreeMemory() {
		return freeMemory;
	}
	String getPlace() {
		return place;
	}
	public String toString() {
		return place + " : " + freeMemory;
	}
}

class SimpleHook implements HookInterface {
	Actor supervisor;
	SimpleHook(Actor supervisor) {
		this.supervisor = supervisor;
	}
	public void resume(GoException e) {
		// Nothing to do : the agent stays in its current place. A message is sent.
		SendCt.STD.send(new JAMtrace("Unreachable place : " + e.getPlace()), supervisor);
	}
}

/**
 * The behavior for (when activated) rmi browsing, then moving. 
 */
class OneStepBrowserBeh extends OneStepBrowserQuasiBehavior {
	Actor supervisor;
	Vector collectedInfos;
	
	class Hook implements HookInterface {
		Vector itinerary;
		String place;
		Hook(Vector itinerary, String place){
			this.itinerary = itinerary;
			this.place = place;
		}
		public void resume(GoException e) {
			String message = ego() + " : " + e.getPlace() + " is unreachable from " + myPlace()
					                      + " - Moves to " + itinerary.firstElement();
			send(new JAMtrace(message), supervisor);
			go((String) itinerary.firstElement(), new Hook(itinerary, place));
			itinerary.removeElementAt(0);
			become(new MultiStepBrowserBeh(supervisor, collectedInfos, itinerary, place));
		}
	}

    // Constructors
	OneStepBrowserBeh(Actor supervisor) {
		this.supervisor = supervisor;
		this.collectedInfos = new Vector();
	}
	OneStepBrowserBeh(Actor supervisor, Vector collectedInfos) {
		this.supervisor = supervisor;
		this.collectedInfos = collectedInfos;
	}

    // Methods
	public void browseAndJump(String place) {
		// rmi browsing first
		collectedInfos.addElement(
			new Info(myPlace(), Runtime.getRuntime().freeMemory()));
		// then, moving
		go(place, new SimpleHook(supervisor));
	}
	
	public void browseAndJump(Vector itinerary, String place) {
		// rmi browsing first
		collectedInfos.addElement(
			new Info(myPlace(), Runtime.getRuntime().freeMemory()));
		if (!itinerary.isEmpty()) {
			// then travelling
			go((String) itinerary.firstElement(), new Hook(itinerary, place));
			itinerary.removeElementAt(0);
			become(new MultiStepBrowserBeh(supervisor, collectedInfos, itinerary, place));
		} else {
			// else moving
			go(place, new SimpleHook(supervisor));
		}
	}
	public Vector getInfos() {
		Vector saveCollectedInfos = collectedInfos;
		collectedInfos = new Vector();
		return saveCollectedInfos;
	}
}

/**
 * The behavior for automatic browsing and moving on a list of places
 */
class MultiStepBrowserBeh extends MultiStepBrowserQuasiBehavior implements StandAlone {
	Actor supervisor;
	Vector collectedInfos;
	Vector itinerary;
	String place;
	
	class Hook implements HookInterface {
		Vector itinerary;
		String place;
		Hook(Vector itinerary, String place){
			this.itinerary = itinerary;
			this.place = place;
		}
		public void resume(GoException e) {
			String message = ego() + " : " + e.getPlace() + " is unreachable from " + myPlace()
										  + " - Moves to ";
      if (itinerary.size()>0) {
        message+=itinerary.firstElement();
			  send(new JAMtrace(message), supervisor);
			  go((String) itinerary.firstElement(), new Hook(itinerary, place));
			  itinerary.removeElementAt(0);
      } else {
        message+=place;
        send(new JAMtrace(message), supervisor);
        go(place); 
      }   
		}
	}

    // Constructor
	MultiStepBrowserBeh(Actor supervisor, Vector collectedInfos, Vector itinerary, String place) {
		this.supervisor = supervisor;
		this.collectedInfos = collectedInfos;
		this.itinerary = itinerary;
		this.place = place;
	}


    // Method
	public void run() {
		// rmi browsing first
		collectedInfos.addElement(
			new Info(myPlace(), Runtime.getRuntime().freeMemory()));
		if (!itinerary.isEmpty()) {
			// then travelling
			go((String) itinerary.firstElement(), new Hook(itinerary, place));
			itinerary.removeElementAt(0);
		} else {
			// else moving
			become(new OneStepBrowserBeh(supervisor, collectedInfos));
			go(place, new SimpleHook(supervisor));
		}
	}
}

class SupervisorBeh extends SupervisorQuasiBehavior {
	public void trace(String s) {
		System.out.println(s);
	}
}

public class MobileBrowser {
	public static void main(String[] args) {
		if (args.length >= 2) {
			Actor supervisor =
				CreateCt.STD.create(new SupervisorBeh());
			Actor browser =
				CreateCt.STD.create(args[0], new OneStepBrowserBeh(supervisor));

			for (int i = 1; i < args.length; i++) {
				SendCt.STD.send(new JAMbrowseAndJump(args[i]), browser);
			}
			SendCt.STD.send(new JAMbrowseAndJump(args[0]), browser);
			JSMgetInfosVector m1 = new JSMgetInfosVector();
			SendCt.STD.send(m1, browser);
      
     		Vector itinerary = new Vector();
			for (int i = 1; i < args.length; i++)
				itinerary.addElement(args[i]);
				
			SendCt.STD.send(new JAMbrowseAndJump(itinerary, args[0]), browser);
			JSMgetInfosVector m2 = new JSMgetInfosVector();
			SendCt.STD.send(m2, browser);

			System.out.println(m1.getReply());
			System.out.println(m2.getReply());
		}
	}
}
/*
~/JavActv050>bin/javactgen -p rmi.MobileBrowser  rmi/examples.net.rmi.MobileBrowser2/*.java
Found the following actor profile(s): rmi.MobileBrowser.Browser, rmi.MobileBrowser.Supervisor.
Compiling files defining actor profile interfaces...
Extracting messages...
Handling the actor profile rmi.MobileBrowser.Browser...
        Found behavior profile rmi.MobileBrowser.OneStepBrowser
        Found behavior profile rmi.MobileBrowser.MultiStepBrowser
Handling the actor profile rmi.MobileBrowser.Supervisor...
        This is also a behavior profile.
Generating QuasiBehaviors...
Generating messages...
Compiling generated files...
~/JavActv050>bin/javactc rmi/examples.net.rmi.MobileBrowser2/*.java
~/JavActv050>bin/javact -Djava.rmi.server.codebase=file:/home/leriche/JavActv050/ rmi/examples.net.rmi.MobileBrowser2/examples.net.rmi.MobileBrowser2 chambord caraibe truc
[chambord:1099 : 1471040, caraibe:1099 : 1564112, caraibe:1099 : 1346464]
[chambord:1099 : 1527192, caraibe:1099 : 2747584, chambord:1099 : 1270024]
*/
