import org.javact.util.ActorProfile;
import org.javact.util.BehaviorProfile;

public interface Hello extends BehaviorProfile, ActorProfile {
	public void hello();
	public void stop();
}
