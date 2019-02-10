import org.javact.util.BehaviorProfile;
import org.javact.util.StandAlone;

public interface Superviseur extends BehaviorProfile, StandAlone {
	public void become(Afficheur beh);
}
