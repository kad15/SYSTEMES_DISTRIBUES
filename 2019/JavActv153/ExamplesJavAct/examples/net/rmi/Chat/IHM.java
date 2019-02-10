package examples.net.rmi.Chat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.javact.lang.Actor;
import org.javact.net.rmi.SendCt;

/**
 * @author Sébastien LERICHE - 11/2005
 *
 *	TP JavAct / N7
 *
 */
public class IHM implements Serializable, ActionListener{

	JFrame fenetre;
	JTextArea outTA;
	JTextArea inTA;
	ArrayList text;
	Actor ca;

	IHM () {
		text=new ArrayList();
	}

	void show(Actor a, String place) {
		ca=a;	//bind de l'acteur dont on sert d'IHM
		
		fenetre=new JFrame("JavAct'examples.net.rmi.Chat - "+place);
		Container panneau=fenetre.getContentPane();
		panneau.setLayout(new BorderLayout());

		outTA=new JTextArea(25,50);
		outTA.setLineWrap(true);
		outTA.setEditable(false);

		inTA=new JTextArea(5,50);
		inTA.setLineWrap(true);

		JButton env=new JButton("envoyer");
		env.addActionListener(this);

		JPanel panneau2=new JPanel();
		panneau2.add(new JScrollPane(inTA));
		panneau2.add(env);

		panneau.add(new JScrollPane(outTA),BorderLayout.CENTER);
		panneau.add(panneau2, BorderLayout.SOUTH);

		fenetre.pack();
		//fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fenetre.show();

		refresh();
	}

	public void actionPerformed(ActionEvent ae) {
		//ca.sendText(inTA.getText()); ne marche pas pcq on a le thread de l'IHM et pas du cycle de	vie de l'agent
		SendCt.STD.send(new JAMsendText(inTA.getText()),ca); 
	}

	void print(String msg) {
		String sep="";
		for (int i=0; i<50; i++)
			sep+="-";
		text.add(msg+"\n"+sep+"\n");
		inTA.setText("");
		refresh();
	}

	private void refresh() {
		outTA.setText("");
		Iterator iter=text.iterator();
		while (iter.hasNext())
			outTA.append((String)iter.next());
		inTA.requestFocusInWindow();
	}

	void dispose() {
		fenetre.dispose();
	}

	/*public static void main(String[] args) {	//test uniquement
		new IHM().show();
	}*/
}
