package examples.net.rmi.MobileBrowser2;
import org.javact.util.BehaviorProfile;

public interface Browser extends BehaviorProfile {
	public void become(BrowserResult beh);
}