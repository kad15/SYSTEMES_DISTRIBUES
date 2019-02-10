import org.javact.util.ActorProfile;
import org.javact.util.BehaviorProfile;
import org.javact.util.StandAlone; // s'execute 1 fois créé

public interface Recherche extends BehaviorProfile {
	public void become(Superviseur beh);
}
