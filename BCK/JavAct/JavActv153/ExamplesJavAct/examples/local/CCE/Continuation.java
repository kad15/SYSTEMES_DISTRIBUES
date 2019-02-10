package examples.local.CCE;
import org.javact.lang.Actor;
import org.javact.util.ActorProfile;
import org.javact.util.BehaviorProfile;

public interface Continuation extends BehaviorProfile,ActorProfile {
	public void crypteEtControle(String s, Actor client) ;
	public void printC(String s);
	public void printR(String s);
}
