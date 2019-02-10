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
* Modified by : Sébastien LERICHE (0.4.1)
* Contact: javact@irit.fr
* ###########################################################################
*/
package org.javact.net.rmi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;


/**
 * DecideOn est la classe qui détermine la place ou sera créé un acteur. Une
 * place représente un hostname, éventuellement un hostname:port.
 * Actuellement, le choix est fait de façon aléatoire parmi une liste de noms.
 * Cette classe ne doit pas être instanciée : elle fournit seulement une
 * méthode de classe.
 * Middleware-level only.
 *
 * @author Patrick MORALES (version 2.x, 2001)
 *         modified by : Sébastien LERICHE
 * @version 0.5.0
 */
public class DecideOn {
    // Champs

    public static final Vector VEC = new Vector();

    // private static boolean premiereInvoc = true;
    // Constructeurs

    /**
     * Creates a new DecideOn object.
     */
    public DecideOn() {
        System.out.println("WARNING : do not instanciate this class !");
    }

    // Methodes

    /**
     * Actuellement, le choix de la place est fait de façon aléatoire parmi une
     * liste de noms, contenue dans le fichier $HOME/JavAct/places.txt : sa
     * lecture à chaque invocation de la méthode, permet de gérer
     * dynamiquement l'implantation de nouveaux acteurs en fonction de
     * l'évolution du réseau. C'est un fichier ASCII avec une place par ligne,
     * #pour un commentaire.
     * Si le paramètre EXECUTION_DOMAIN n'est pas positionné, ou en cas d'erreur 
     * de lecture du fichier, on retourne localhost:1099.
     *
     * @return la chaîne représentant la place sélectionnée.
     */
    public static final String randomPlace() {
		String propExecDmn = null;
        Random rand = new Random();

        if ((propExecDmn=System.getProperty("EXECUTION_DOMAIN")) != null)
        try {
            BufferedReader in = new BufferedReader(new FileReader(propExecDmn));
            String s;
            int index, indexSpace, indexTab;

            while ((s = in.readLine()) != null) {
                s = s.trim(); //enlève les ' ' superflus

                if ((s.length() != 0) && (!s.startsWith("#"))) {
                    // élimine les lignes vides ou commencant par #
                	indexSpace = s.indexOf(" ");
    				indexTab = s.indexOf("\t");
    				if (indexSpace == -1){
    					index = indexTab;
    				} else if (indexTab == -1) {
    					index = indexSpace;
    				} else {
    					index = Math.min(indexTab, indexSpace);
    				}
    				if (index != -1) { // index always != 0 because of the s.trim()
    					s = s.substring(0,index);
    				}
                	
                	
                    // si le port n a pas ete fourni dans le fichier places.txt,
                    // on rajoute le port par defaut :1099
                    if (s.indexOf(":") == -1) {
                    	s += ":1099";
                    }
                    
                    VEC.addElement(s);
                }
            }

            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("Fichier " +
                System.getProperty("EXECUTION_DOMAIN") +
                ": ouverture impossible -> ");
				e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Exception -> ");
            e.printStackTrace();
        }

        String place = null;

        if (!VEC.isEmpty())
            place = (String) VEC.elementAt(rand.nextInt(VEC.size()));
        else
            // si fichier vide on prend le hostname local, port par défaut
            place = "localhost:1099";

        return place;
    }
}
