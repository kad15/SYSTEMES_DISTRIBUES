package examples.local.CCE;
import org.javact.lang.Actor;
import org.javact.util.ActorProfile;
import org.javact.util.BehaviorProfile;

public interface Cryptage extends BehaviorProfile,ActorProfile {
	public void crypte(String s, Actor client);
}
