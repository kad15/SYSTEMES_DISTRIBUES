package examples.net.rmi.MobileBrowser2;
import java.util.Iterator;
import java.util.Vector;

import org.javact.net.rmi.CreateCt;
import org.javact.util.StandAlone;

class BrowserBeh extends BrowserQuasiBehavior implements StandAlone {
	String origine=null;
	Vector chemin=null;
	Vector result=new Vector();

	BrowserBeh(Vector chemin) {
		this.chemin=chemin;
	}

	public void run() {
		if (origine==null) origine=myPlace();
		
		System.out.println("Je suis sur : "+myPlace()+" - Il reste encore : "+chemin.size()+" places.");
		result.add(myPlace()+" : "+Runtime.getRuntime().freeMemory());
		
		//if (chemin.size()<=2) with(new NaiveMoveCt());
		
		if (chemin.size()==0) {
			become(new BrowserResultBeh(result));
			go(origine);
		} else {
			go((String)chemin.firstElement());
			chemin.remove(0);
		}
	}
}

//**********************************************

class BrowserResultBeh extends BrowserResultQuasiBehavior implements StandAlone {
	Vector result;

	BrowserResultBeh(Vector r) {
		result=r;
	}

	public void run() {
		System.out.println("Résultats :");
		Iterator iter=result.iterator();
		while (iter.hasNext()) {
			System.out.println((String)iter.next());
		}
	}
}

//**********************************************

public class MobileBrowser {
	public static void main(String args[]) {
		Vector v=new Vector();
		v.add("trimix:2000");
		v.add("trimix:2001");
		CreateCt.STD.create("trimix",new BrowserBeh(v));
	}
}

/*
Je suis sur : trimix:1099 - Il reste encore : 2 places.
Résultats :
trimix:1099 : 1239680
trimix:2000 : 1276968
trimix:2001 : 1307128
*/
