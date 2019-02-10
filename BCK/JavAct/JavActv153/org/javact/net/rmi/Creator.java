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
 * Contact: javact@irit.fr
 * ###########################################################################
 */
package org.javact.net.rmi;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.util.Date;

import org.javact.lang.Actor;
import org.javact.lang.BecomeCt;
import org.javact.lang.BecomeCtI;
import org.javact.lang.Controler;
import org.javact.lang.CreateCtI;
import org.javact.lang.LifeCycleCt;
import org.javact.lang.LifeCycleCtI;
import org.javact.lang.MailBoxCt;
import org.javact.lang.MailBoxCtI;
import org.javact.lang.MoveCtI;
import org.javact.lang.QuasiBehavior;
import org.javact.lang.SendCtI;


/**
 * Creator est la classe implantant les services offerts par l'interface
 * CreatorInt, c'est à dire la création locale d'acteurs résultant d'une
 * demande, qui peut être distante (RMI). On lancera cette classe en batch sur
 * chaque UC via le shell jrmi.
 * 
 * @author Patrick MORALES (version 2.x, 2001), modified by TER 2002
 *         "TENEBRION", modified by Sébastien LERICHE (4.0 Delta - 4.1) modified
 *         by Vincent MERIOCHAUD (vincent.meriochaud@free.fr) (marked by a "//
 *         New") modified by SL (0.5.2) adding some debug infos at startup,
 *         updating to 0.5.2 modified by SL cleaning code (-debug) + updating to 1.5.3
 * @version 1.5.3
 */
public class Creator extends UnicastRemoteObject implements CreatorInt {
	// Champs
	private static final String version = "JavAct v1.5.3";

	/** Hostname de l'UC hébergeant le créateur. */
	private static String maPlace;

	/**
	 * Port d'écoute utilisé par le service d'enregistrement. S'il n'est pas
	 * donné via la ligne de commande, JAVA l'initialisera à 1099 par défaut
	 * port in [1099..65535]
	 */
	private static String port = "";

	/**
	 * Construit un objet distant créateur, qui s'auto référence dans le
	 * registre de noms local (rmiregistry).
	 * 
	 * @param _port
	 *            le numero de port sur lequel ecoute le Creator
	 * 
	 * @throws RemoteException
	 *             exception RMI
	 */
	public Creator(int _port) throws RemoteException {
		super();

		// pour exporter le numero de port ds la JVM, recupere dans myPlace()
		port = ":" + String.valueOf(_port);
		System.setProperty("JAVACT_PORT", port);

		// voici l'indispensable gestionnaire de sécurité
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());

		try {
			maPlace = InetAddress.getLocalHost().getHostName() + port;
		} catch (java.net.UnknownHostException e) {
			System.out.println("Exception -> ");
			e.printStackTrace();
		}

		// nommage de l'objet dans le registre local
		try {
			LocateRegistry.createRegistry(_port);
			Naming.rebind("rmi://" + maPlace + "/CreateurDeJavAct", this);
		} catch (MalformedURLException e) {
			System.out.println("Exception -> ");
			e.printStackTrace();
		} catch (ExportException e) {
			System.out.println("(Creator) Unable to launch/rebind the registry...\nDo you already have a JVM/RMI project running ? Cause is : "+ e.getMessage());
			System.exit(-1);
		} catch (RemoteException e) {
			System.out.println("Exception -> ");
			e.printStackTrace();
		}
	}

	// METHODES
	private void exportActor(ReceiveCt a) {
		try {
			UnicastRemoteObject.exportObject(a);
		} catch (RemoteException e) {
			System.out.println("(Creator) Exception exporting ReceiveCt -> ");
			e.printStackTrace();
		}
	}

	/**
	 * Création effective d'un acteur local suite à une demande qui peut être
	 * distante.
	 * 
	 * @param beh
	 *            Le comportement à associer à l'acteur.
	 * 
	 * @return la référence du nouvel acteur créé localement.
	 * 
	 * @throws RemoteException
	 *             exception RMI
	 */
	public final Actor createLocal(QuasiBehavior beh) throws RemoteException {
		Actor actor = new ReceiveCt();

		new Controler(beh, new MailBoxCt(), new BecomeCt(), new CreateCt(),
				new LifeCycleCt(), new MoveCt(), new SendCt(), actor);
		exportActor((ReceiveCt) actor);
		return actor;
	}

	/**
	 * Création effective d'un acteur local suite à une demande qui peut être
	 * distante.
	 * 
	 * @param c
	 *            the controler
	 * @return La référence (Actor) de l'acteur créé.
	 * 
	 * @throws RemoteException
	 *             exception RMI
	 */
	public final Actor createLocal(Controler c) throws RemoteException {
		Actor actor = new ReceiveCt();
		c.changeToNextForceReInit(actor);
		exportActor((ReceiveCt) actor);
		return actor;
	}

	/**
	 * Création effective d'un acteur local suite à une demande qui peut être
	 * distante.
	 * 
	 * @param beh
	 *            Le comportement associé à l'acteur
	 * @param box
	 *            Le composant boite à lettre de l'acteur.
	 * @param bec
	 *            Le composant become de l'acteur.
	 * @param crt
	 *            Le composant create de l'acteur.
	 * @param lif
	 *            Le composant cycle de vie de l'acteur.
	 * @param mve
	 *            Le composant move de l'acteur.
	 * @param snd
	 *            Le composant send de l'acteur.
	 * 
	 * @return La référence (Actor) de l'acteur créé.
	 * 
	 * @throws RemoteException
	 *             exception RMI
	 */
	public final Actor createLocal(QuasiBehavior beh, MailBoxCtI box,
			BecomeCtI bec, CreateCtI crt, LifeCycleCtI lif, MoveCtI mve,
			SendCtI snd) throws RemoteException {
		Actor actor = new ReceiveCt();
		new Controler(beh, box, bec, crt, lif, mve, snd, actor);
		exportActor((ReceiveCt) actor);

		return actor;
	}

	/**
	 * Classe princiale du système d'accueil. Son rôle est d'attendre une
	 * demande de création locale d'acteur. Elle restera en attente
	 * perpétuellement et sera tuée de l'extérieur, car elle n'a aucun moyen de
	 * savoir si on aura encore besoin d'elle !
	 * 
	 * @param args
	 *            arguments facultatifs du main = localisation absolue du
	 *            fichier de places (places.txt, répertoire courant par défaut) +
	 *            numéro de port (1099 À 65535), 1099 par défaut
	 */
	public static void main(String[] args) {
		int port_int = 1099;
		String arg_domain = null, arg_port = null;
		
		switch (args.length) {
		case 0:
			arg_port = "1099";
			break;
		case 1:
			arg_port = args[0];
			break;
		case 2:
			arg_domain = args[0];
			arg_port = args[1];
			break;
		default:
			System.out.println("(Creator) Wrong number of arguments !");
			System.exit(-2);
		}

		// argument fichier de places, par défaut places.txt dans le répertoire
		// de lancement du creator
		if (arg_domain == null)
			arg_domain = "places.txt";
		System.setProperty("EXECUTION_DOMAIN", arg_domain);

		// infos sur le places.txt
		File fPlaces = new File(arg_domain);
		System.out.print("(Creator) EXECUTION_DOMAIN="
				+ fPlaces.getAbsolutePath());
		if (fPlaces.canRead())
			System.out.println(" [ok, loaded]");
		else
			System.out.println(" [warning: not found !]");

		// infos sur le security manager
		String policy = System.getProperty("java.security.policy");
		if (policy == null) {
			policy="awfullPolicy"; //politique par défaut (grant all)
			System.setProperty("java.security.policy",policy);
		}
		File fPolicy = new File(policy);
		System.out.print("(Creator) security policy="
				+ fPolicy.getAbsolutePath());
		if (fPolicy.canRead())
			System.out.println(" [ok, found]");
		else {
			System.out.println(" [warning: not found !]");
			System.out.println("You must use the -Djava.security.policy option with a correct argument.");
			System.exit(-1);
		}

		// argument port, 1099 par défaut
		if ((Integer.parseInt(arg_port) >= 1099)
				&& (Integer.parseInt(arg_port) <= 65535))
			port_int = Integer.parseInt(arg_port);
		else {
			// le port permet de simuler 1 reseau d'UC sur une seule
			System.out
					.println("(Creator) Wrong port number : 1099 <= port <= 65535");
			System.exit(-2);
		}

		try {
			new Creator(port_int);
		} catch (RemoteException e) {
			System.out.println("Exception -> ");
			e.printStackTrace();
			System.exit(-1);
		}

		while (true) {
			System.out.println(version + " [" + maPlace + "] - "
					+ DateFormat.getTimeInstance().format(new Date()) + "\n");

			try {
				Thread.sleep(5 * 60 * 1000); // millisecondes
			} catch (InterruptedException er) {
			}
		}
		// while
	}
	// main
}
