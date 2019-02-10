import org.javact.lang.Agent;
import org.javact.util.ActorProfile;
import org.javact.util.BehaviorProfile;

public interface MobileChat extends BehaviorProfile, ActorProfile{
	public void speak(String m);
	public void setDest(Agent a);
	public void move(String p, String text);
}
