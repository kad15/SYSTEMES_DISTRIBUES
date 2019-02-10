import org.javact.util.BehaviorProfile;

public interface Superviseur extends BehaviorProfile {
	public void become(Afficheur beh);
}
