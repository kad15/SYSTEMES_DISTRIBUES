package examples.net.rmi.MobileBrowser2;
import org.javact.lang.Actor;
import org.javact.lang.Controler;
import org.javact.lang.GoException;
import org.javact.lang.JavActComponent;
import org.javact.lang.MoveCtI;
import org.javact.net.rmi.SendCt;

public class NaiveMoveCt extends JavActComponent implements MoveCtI {

    public NaiveMoveCt() {
    }

    public void go(String p) {
        Actor next = null;
        NaiveForwarder f = null;

        //On the initial place, it remains only a Forwarder,  a
        //component for message forwarding.
        //Locking the controler prevents the invocation of its reveive() method;
        //then, its MailBox can be replaced by a Forwarder
        synchronized (myControler) {
            Controler oldc = myControler;

            //Creation of the following actor in the chain
            //comme on réutilise le composant Move courant, il faut sauvegarder la référence du controleur courant
            //sinon ds le cas de création locale, cette référence est modifiée
            try {
                next = myControler.create(p,
                    new Controler(myControler.getMyNextBehavior(),
                        myControler.getMyNextMailBoxCt(),
                        myControler.getMyNextBecomeCt(),
                        myControler.getMyNextCreateCt(),
                        myControler.getMyNextLifeCycleCt(),
                        myControler.getMyNextMoveCt(),
                        myControler.getMyNextSendCt(),
                        myControler.getMyMailQueue()));
            } catch (Exception e) {
            	throw new GoException(p,"Unable to go to "+p,e);
            }


            if (next != null) {
                f = new NaiveForwarder(next);
                f.setMyControler(oldc);

                //on adapte les composants du controleur pour devenir un acteur relai
                // ie boite aux lettre = forwarder et comportement = null
                oldc.setMyMailBoxCt(f);
                oldc.setMySendCt(SendCt.STD);
                oldc.setMyBehavior(null);
				oldc.setMyMoveCt(null);
				oldc.setMyBecomeCt(null);
				oldc.setMyCreateCt(null);
            }
        }
    }
}
