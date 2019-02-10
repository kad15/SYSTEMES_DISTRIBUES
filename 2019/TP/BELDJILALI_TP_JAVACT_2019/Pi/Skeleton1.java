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

import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;

import org.javact.net.rmi.CreateCt;
import org.javact.util.StandAlone;

/**
 * Behaviour for the actor Recherche
 */
class RechercheBeh extends RechercheQuasiBehavior implements StandAlone {

	String[] v; // v : places list
	int i; // place index
	String firstMachine = null;
	String bestMachine = null;
	double minLoadCPU = 1;

	public RechercheBeh() {
		v = JavActProbe.probe(2015, "10.3.49");
		i = 0;
	}

	@Override
	public void run() {
		System.out.println("Current place: " + myPlace());

		if (firstMachine == null) {
			firstMachine = myPlace();
		}

		if (i == v.length - 1) {
			become(new SuperviseurBeh(firstMachine));
			go(bestMachine);
		} else {
			OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
			double loadCPU = bean.getSystemLoadAverage();
			System.out.println(loadCPU);
			if (loadCPU < minLoadCPU) {
				minLoadCPU = loadCPU;
				bestMachine = myPlace();
			}
			
			
			
			go(v[i++] + ":2015");
		}
	}
}

/**
 * Behaviour for the actor Superviseur
 */
class SuperviseurBeh extends SuperviseurQuasiBehavior {

	private String firstPlace;

	public SuperviseurBeh(String firstPlace) {
		this.firstPlace = firstPlace;
	}

	public void run() {
		System.out.println("Computing on  " + myPlace());
		BigDecimal result = new Pi(10000).call();
		become(new AfficheurBeh(result));
		go(firstPlace);
	}

}

/**
 * Behaviour for the actor Afficheur
 */
class AfficheurBeh extends AfficheurQuasiBehavior implements StandAlone {
	private BigDecimal result;

	public AfficheurBeh(BigDecimal result) {
		this.result = result;
	}

	@Override
	public void run() {
		System.out.println("Home sweet home! I am back " + myPlace());
		System.out.println(result);
	}
}

public class Skeleton1 {
	public static void main(String[] args) {
		CreateCt.STD.create("localhost:2015", new RechercheBeh());
	}
}
