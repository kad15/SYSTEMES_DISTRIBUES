package examples.net.rmi.MobileBrowser2;
import org.javact.lang.Actor;
import org.javact.lang.JavActComponent;
import org.javact.lang.MailBoxCtI;
import org.javact.lang.Message;

/**
 * *META-COMPONENT-LEVEL* NaiveForwarder object, which replaces actor's mailbox in
 * case of mobility. It serves as a relay in the message routing process.
 *
 */
public class NaiveForwarder extends JavActComponent implements MailBoxCtI {
	Actor next;

    public NaiveForwarder(Actor n) {
        next = n;
    }


    /**
     * Message forwarding
     */
    public void putMessage(Message m) {
		System.out.println("Transmission d'un message :["+m+"] vers : "+next);
		myControler.send(m, next);
    }

    /**
     * Mandatory, because of MailBoxCtI interface. Should not be invoked.
     */
    public Message getMessage() {
        System.out.println("Forwarder.java " + this +
            " should not be invoked -method getMessage()-");
        return null;
    }

    /**
     * Mandatory, because of MailBoxCtI interface. Should not be invoked.
     */
    public boolean isEmpty() {
        return true;
    }

}
