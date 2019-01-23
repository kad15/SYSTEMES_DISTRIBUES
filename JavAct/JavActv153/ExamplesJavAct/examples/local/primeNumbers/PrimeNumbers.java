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

package examples.local.primeNumbers;

import org.javact.lang.Actor;
import org.javact.local.CreateCt;
import org.javact.local.SendCt;

/**
 * Behavior of an intermediate actor in the pipeline
 */
class IntermediateBeh extends IntermediateSieveQuasiBehavior {
    protected int prime;
    protected Actor next;

    public IntermediateBeh(int i, Actor a) {
        super();
        prime = i;
        next = a;
    }

    public void sift(int i) {
        if ((i % prime) != 0) send(new JAMsift(i), next);
    }
    public void die() {
        send(new JAMdie(), next);
        suicide();
    }
}

/**
 * Behavior of the last actor of the pipeline
 */
class LastBeh extends LastSieveQuasiBehavior {
    protected int prime;

    public LastBeh(int i) {
        super();
        prime = i;
        System.out.println("*** " + prime + " is a prime number ***");
    }

    public void sift(int i) {
        if ((i % prime) != 0) {
            Actor next = create(new LastBeh(i));
            become(new IntermediateBeh(prime, next));
        }
    }
    public void die() {
        suicide();
    }
}

/**
 * Computes the first N prime numbers with the Erathostene Sieves algorithm
 */
public class PrimeNumbers {
	public static void main(String[] args) {
		if (args.length != 0) {
			int valint = Integer.parseInt(args[0]);
			if (valint > 2) {
				Actor two = CreateCt.STD.create(new LastBeh(2));
				for (int i = 3; valint >= i; i++)
					SendCt.STD.send(new JAMsift(i), two);
				// Message transmission is assumed to be regular
				SendCt.STD.send(new JAMdie(), two); // Pipeline destruction
			} else
				System.out.println("Give an int > 2");
		} else
			System.out.println("Syntax : java PrimeNumbers *int>2*");
	}
}

