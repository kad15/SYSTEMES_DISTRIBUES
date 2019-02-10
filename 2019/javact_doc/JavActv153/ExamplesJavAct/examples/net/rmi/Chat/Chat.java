package examples.net.rmi.Chat;

import org.javact.lang.Actor;
import org.javact.net.rmi.CreateCt;
import org.javact.net.rmi.SendCt;
import org.javact.util.StandAlone;

class ChatAgentBeh extends ChatAgentQuasiBehavior implements ChatAgent, StandAlone {

	IHM monIHM;
	Actor corresp;

	ChatAgentBeh() {
		monIHM=new IHM();
	}

	public void run() {
		//System.out.println("Je suis sur : "+myPlace());
		monIHM.show((Actor) ego(),myPlace());
	}

	public void setCorresp(Actor c) {
		corresp=c;
	}

	public void printText(String msg) {
		monIHM.print(msg);
	}

	public void sendText(String msg) {
		if (msg.indexOf("#QUIT")==0) { suicide(); monIHM.dispose(); }
		else if (msg.indexOf("#MOVE")==0) {
			monIHM.dispose(); 
			go(msg.substring(6)); 
		}
		else {
			monIHM.print(msg);
			send(new JAMprintText(ego()+":"+msg),corresp);
		}
	}
}

public class Chat {
	public static void main(String[] args) {
		Actor CA1=CreateCt.STD.create("localhost:1099",new ChatAgentBeh());
		Actor CA2=CreateCt.STD.create("localhost:1100",new ChatAgentBeh());
		SendCt.STD.send(new JAMsetCorresp(CA2),CA1);
		SendCt.STD.send(new JAMsetCorresp(CA1),CA2);
	}
}
