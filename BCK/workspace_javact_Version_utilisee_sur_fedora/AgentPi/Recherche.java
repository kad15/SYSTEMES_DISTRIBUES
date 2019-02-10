import org.javact.util.ActorProfile;
import org.javact.util.BehaviorProfile;
import org.javact.util.StandAlone;

public interface Recherche extends BehaviorProfile, StandAlone {
	public void become(Superviseur beh);
}
