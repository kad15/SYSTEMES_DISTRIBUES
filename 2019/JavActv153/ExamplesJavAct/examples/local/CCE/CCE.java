package examples.local.CCE;
import org.javact.lang.Actor;
import org.javact.local.CreateCt;
import org.javact.local.SendCt;

class CBeh extends CryptageQuasiBehavior {
	public void crypte(String s, Actor a) {
		send(new JAMprintC(StringUtil.crypte(s)), a);
	}
}

class RBeh extends ControleQuasiBehavior {
	
	public void controle(String s, Actor a) {
		send(new JAMprintR(StringUtil.ajouteCtrl(s)), a);
	}
}

class ReceptBeh extends RecepteurQuasiBehavior {
	public void printC(String s) {
		System.out.println(StringUtil.decrypte(s));	
	}
	
	public void printR(String s) {
		System.out.println(StringUtil.verifieCtrl(s));
	}
	
	public void printCR(String s) {
		System.out.println(StringUtil.decrypte(StringUtil.verifieCtrl(s)));
	}
}

class ContBeh extends ContinuationQuasiBehavior {
	Actor C,R,client;
	
	ContBeh(Actor C, Actor R) {
		this.C=C;
		this.R=R;
	}
		
	public void crypteEtControle(String s, Actor client) {
		this.client=client;
		send(new JAMcrypte(s, ego()),C);
	}
	
	public void printC(String s) {
		send(new JAMcontrole(s, ego()), R);
	}
	
	public void printR(String s) {
		send(new JAMprintCR(s), client);
	}
	
	
}

public class CCE {
        public static void main(String args[]) {
		Actor C=CreateCt.STD.create(new CBeh());
		Actor R=CreateCt.STD.create(new RBeh());
		Actor recept=CreateCt.STD.create(new ReceptBeh());
		
		SendCt.STD.send(new JAMcrypte("Croyez-vous que cela fonctionne ?",recept), C);
		SendCt.STD.send(new JAMcontrole("Coucou les gens !",recept), R);
		
		Actor cont=CreateCt.STD.create(new ContBeh(C, R));
		SendCt.STD.send(new JAMcrypteEtControle("Cryptage et controle via la continuation !", recept),cont);
	}
}	

/*
> ~/JavActv050/bin/javactgen *.java
Found the following actor profile(s): Continuation, Controle, Cryptage, Recepteur.
Compiling files defining actor profile interfaces...
Extracting messages...
Handling the actor profile Continuation...
        This is also a behavior profile.
Handling the actor profile Controle...
        This is also a behavior profile.
Handling the actor profile Cryptage...
        This is also a behavior profile.
Handling the actor profile Recepteur...
        This is also a behavior profile.
Generating QuasiBehaviors...
Generating messages...
Compiling generated files...
> ~/JavActv050/bin/javactc *.java
> ~/JavActv050/bin/javact examples.local.CCE
Croyez-vous que cela fonctionne ?
Coucou les gens !
Cryptage et controle via la continuation !
*/
