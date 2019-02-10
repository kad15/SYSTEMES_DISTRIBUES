import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;

import org.javact.net.rmi.CreateCt;
import org.javact.util.StandAlone;

/**
 * Behaviour for the actor Recherche
 */
class RechercheBeh extends RechercheQuasiBehavior implements StandAlone {

	String[] itineraire;
	int position;
	String firstPlace=null;
	String bestPlace=null;
	double bestLoad=1;

	public RechercheBeh() {
		itineraire = JavActProbe.probe(2000);
		position = 0;
	}

	@Override
	public void run() {
		System.out.println("Je suis sur " + myPlace());
		
		if (firstPlace==null) {
			firstPlace=myPlace();
		}
		
		/* cas d'arret */
		if (position == itineraire.length - 1) {
			become(new SuperviseurBeh(firstPlace));
			go(bestPlace);
		} else {
			/* cas de base */
			OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
			double load = bean.getSystemLoadAverage();
			System.out.println(load);
			if (load<bestLoad) {
				bestLoad=load;
				bestPlace=myPlace();
			}
			go(itineraire[position++] + ":2000");
		}
	}
}

/**
* Behaviour for the actor Superviseur
*/
class SuperviseurBeh extends SuperviseurQuasiBehavior {

	private String firstPlace;
	
	public SuperviseurBeh(String firstPlace) {
		this.firstPlace=firstPlace;
	}

	public void run() {
		System.out.println("Je fais mon calcul sur la machine "+myPlace());
		BigDecimal result=new Pi(10000).call();
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
		this.result=result;
	}

	@Override
	public void run() {
		System.out.println("Je suis rentré à la maison ! "+ myPlace());
		System.out.println(result);
	}
}

public class Skeleton1 {
	public static void main(String[] args) {
		CreateCt.STD.create("localhost:2000", new RechercheBeh());
	}
}
