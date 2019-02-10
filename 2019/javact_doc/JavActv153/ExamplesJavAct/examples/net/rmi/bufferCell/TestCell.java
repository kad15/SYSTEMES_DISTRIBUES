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

package examples.net.rmi.bufferCell;

import org.javact.lang.Actor;
import org.javact.net.rmi.CreateCt;
import org.javact.net.rmi.SendCt;


class EmptyCellBeh extends EmptyQuasiBehavior
{
    EmptyCellBeh() { }

    public void put(int i)
    {
	System.out.println("Empty cell " + ego()
		+ " receives put(" + i + ") request");
	become(new FullCellBeh(i));
    }
}

class FullCellBeh extends FullQuasiBehavior
{
    int i;
    Empty empty;

    FullCellBeh(int i)
    {
	this.i = i;
	empty = new EmptyCellBeh();
    }

    public int get()
    {
	System.out.println("Full cell " + ego()
		+ " receives getInteger request; will return " + i);
	become(empty);
	return i;
    }

    public void get(Actor a)
    {
	System.out.println("Full cell " + ego()
		+ " receives get request; will return " + i);
	send(new JAMreply(i), a);
	become(empty);
    }
}

class ClientBeh extends ClientQuasiBehavior
{
    ClientBeh() { }

    public void reply(int i)
    {
	System.out.println("Client " + ego() + " receives " + i);
    }
}


/**
 * A program demo for communication of messages with reply. Before running, 3
 * places must be launched : localhost, localhost:1100 and localhost:1101
 */
public class TestCell
{
    public static void main(String[] args)
    {
        Actor cell1 = CreateCt.STD.create("localhost:1100", new EmptyCellBeh());
        SendCt.STD.send(new JAMput(2003), cell1);

        JSMgetint mget1 = new JSMgetint();
        SendCt.STD.send(mget1, cell1);

        Actor cell2 = CreateCt.STD.create("localhost:1101",
                new FullCellBeh(1789));
        JSMgetint mget2 = new JSMgetint();
        SendCt.STD.send(mget2, cell2);

        Actor customer = CreateCt.STD.create(new ClientBeh());

        System.out.println("cell1 contains " + mget1.getReply());
        System.out.println("cell2 contains " + mget2.getReply());

        SendCt.STD.send(new JAMput(mget2.getReply()), cell1);
        SendCt.STD.send(new JAMput(1515), cell1); // can't be processed
        SendCt.STD.send(new JAMget(customer), cell1);
    }
}


/*
caraibe% /home/arcangel/J40/JavActv40/bin/javact -Djava.rmi.server.codebase="file:/home/arcangel/J40/JavActv40/examples/net/rmi/BufferCell/" -DEXECUTION_DOMAIN=/home/arcangel/J40/JavActv40/places.txt TestCell
*/
