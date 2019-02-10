package examples.net.rmi.Chat;

import org.javact.lang.Actor;
import org.javact.util.ActorProfile;
import org.javact.util.BehaviorProfile;

public interface ChatAgent extends BehaviorProfile,ActorProfile {
	public void printText(String msg);
	public void sendText(String msg);	//il vaudrait mieux extraire cette m�thode pour ne pas permettre l'envoi de msg depuis l'ext�rieur...
	public void setCorresp(Actor c);
}
