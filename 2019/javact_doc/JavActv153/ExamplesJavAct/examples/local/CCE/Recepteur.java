package examples.local.CCE;
import org.javact.util.ActorProfile;
import org.javact.util.BehaviorProfile;

public interface Recepteur extends BehaviorProfile,ActorProfile {
	public void printC(String s);
	public void printR(String s);
	public void printCR(String s);
}
