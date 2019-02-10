package examples.local.CCE;
import org.javact.lang.Actor;
import org.javact.util.ActorProfile;
import org.javact.util.BehaviorProfile;

public interface Controle extends BehaviorProfile,ActorProfile {
	public void controle(String s, Actor client);
}
