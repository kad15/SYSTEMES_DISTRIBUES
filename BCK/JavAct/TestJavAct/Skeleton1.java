
import org.javact.lang.*;
import org.javact.net.rmi.CreateCt;
import org.javact.util.StandAlone;

/**
 * Behaviour for the actor Plop
 */
class PlopBeh extends PlopQuasiBehavior implements StandAlone {

	private String[] domaine;
	int position;

	public PlopBeh() {
		position = 0;
		domaine = JavActProbe.probe(1099);
		System.out.println("Le domaine contient " + domaine.length + " places");
	}

	public void run() {
		System.out.println("Coucou " + myPlace());
		if (position < domaine.length) {
			go(domaine[position++]);
		} else {
			System.out.println("Mission suicide achevée !");
			suicide();
		}
	}
}

public class Skeleton1 {

	public static void main(String[] args) {
		Agent a = CreateCt.STD.create(new PlopBeh());
	}

}
