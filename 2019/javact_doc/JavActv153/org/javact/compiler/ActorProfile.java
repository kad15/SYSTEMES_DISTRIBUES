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
package org.javact.compiler;

import java.util.LinkedList;
import java.util.List;

/**
 * Manipulating actor profiles. Given a <code>Class</code> object, a new
 * instance of this class will be created containing all of the behavior
 * profiles implemented by this interface.
 *
 * @author Vincent Hennebert
 * @version 0.5.0
 */
class ActorProfile
{
    /** List of the behavior profiles implemented by this actor profile. */
    private List behavProfList = new LinkedList();

    /**
     * Creates a new instance of <code>ActorProfile</code> corresponding to the
     * given <code>Class</code> object.
     *
     * @param actorProfClass the interface declaring an actor profile.
     *
     * @throws NotABehaviorProfileException in two case:
     * <ul>
     *     <li>when this interface also is a behavior profile but does not
     *     extend the {@link org.javact.util.BehaviorProfile} interface;
     *     <li>when this interface contains bad designed behavior profiles (see
     *     {@link BehaviorProfile}).
     * </ul>
     * @throws BecomeException when contained behavior profiles badly define
     * <code>become</code> methods.
     */
    ActorProfile(Class actorProfClass) throws ActorProfileException,
    NotABehaviorProfileException, BecomeException
    {
	Class[] extendedInterfTab = actorProfClass.getInterfaces();

	if ( (extendedInterfTab.length == 2) &&
		(
		    ((extendedInterfTab[0] == Keywords.ROLE_INTERFACE) &&
		    (extendedInterfTab[1] == Keywords.BEHAVIOR_INTERFACE))
		|| 
		    ((extendedInterfTab[0] == Keywords.BEHAVIOR_INTERFACE) &&
		    (extendedInterfTab[1] == Keywords.ROLE_INTERFACE))
		)
	   )
	{
	    /* It is an actor that has only one behavior. The same interface is
	     * used to declare both the behavior and the actor profiles
	     */
	    BehaviorProfile behavProfile = new BehaviorProfile(actorProfClass);
	    behavProfList.add(behavProfile);
	    if (Options.VERBOSE)
	    {
		System.out.println("\tThis is also a behavior profile.");
	    }
	}
	else
	{
	    /* In all other cases this is just a marking interface, and
	     * behavior profiles are declared in other interfaces. It should
	     * not declare methods and not inherit BehaviorProfile
	     */
	    if (actorProfClass.getDeclaredMethods().length > 0)
	    {
		throw new ActorProfileException(actorProfClass
			+ " inherits behavior profiles,"
			+ " so it cannot itself declare messages.");
	    }

	    /* One of the implemented interfaces... */
	    Class behavProfClass;
	    /* ... and its corresponding BehaviorProfile instance */
	    BehaviorProfile behavProfile;

	    for (int i = 0; i < extendedInterfTab.length; i++)
	    {
		behavProfClass = extendedInterfTab[i];

		/* Either this interface is a behavior interface, or it is
		 * the ActorProfile interface itself that is extended, or it
		 * is the BehaviorProfile interface that must not be parsed */
		if (behavProfClass != Keywords.ROLE_INTERFACE)
		{
		    if (behavProfClass == Keywords.BEHAVIOR_INTERFACE)
		    {
			/* The actor profile should not extend BehaviorProfile.
			 * Display a warning
			 */
			if (Options.VERBOSE)
			{
			    System.err.println("WARNING: " + actorProfClass
				    + " should not extend the "
				    + Keywords.BEHAVIOR + " interface.");
			}
		    }
		    else
		    {
			behavProfile =
			    BehaviorProfile.getBehaviorProfile(behavProfClass);
			behavProfList.add(behavProfile);

			if (Options.VERBOSE)
			{
			    System.out.println( "\tFound behavior profile "
				    + behavProfClass.getName());
			}
		    }
		}
	    }// endfor
	    /* This actor profile should inherit at least one behavior profile
	     */
	    if ((behavProfList.isEmpty()) && (Options.VERBOSE))
	    {
		System.err.println("WARNING: " + actorProfClass
			+ " is an actor profile but does not extend"
			+ " any behavior profile.");
	    }
// The code below does not work; if the automaton is inconsistent then the
// forgotten behavior profile is kept in the request list of BehaviorProfile.
// We should control that the request list is empty when all actor profiles
// have been parsed. Too complex and not expected to be in the design model.
//	    else
//	    {
//		/* Check that the automaton is consistent: the parameters of
//		 * the become methods in behavior profiles must be inherited by
//		 * this actor profile
//		 */
//		Collection futureBehaviors = new LinkedList();
//		Iterator behavIter = behavProfList.iterator();
//		BehaviorProfile behavProf;
//		/* We know that behavProfList is not empty */
//		do
//		{
//		    behavProf = (BehaviorProfile)behavIter.next();
//		    System.out.println("interface: "+behavProf);
//		    futureBehaviors.addAll(behavProf.getFutureBehaviorsList());
//		    List list = behavProf.getFutureBehaviorsList();
//		    Iterator iter = list.iterator();
//		    while (iter.hasNext())
//		    {
//			System.out.println(iter.next());
//		    }
//		}
//		while (behavIter.hasNext());
//
//		if (!behavProfList.containsAll(futureBehaviors))
//		{
//		    throw new ActorProfileException("Inconsistent automaton: " 
//			    + actorProfClass.getName()
//			    + "'s set of inherited behavior profiles does not"
//			    + " contain the parameters of all become methods");
//		}
//	    }
	}//endif actor profile is a behavior profile
    }
}
