import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.javact.lang.Agent;
import org.javact.net.rmi.SendCt;

/**
 *
 * @author leriche - @ENAC - 2017
 */
public class MobileChatSwing {

	private JTextArea ta;
	private Agent myAgent;

	/**
	 * @param beh
	 *            le comportement de l'agent mobile
	 * @param list
	 *            le domaine de places accessibles
	 * @param text
	 *            l'état du texte
	 */
	public MobileChatSwing(MobileChatBeh beh, Agent myAgent, String[] list, String text) {
		this.myAgent = myAgent;
		JFrame frame = new JFrame("Mobile Chat @ ENAC");

		frame.setSize(300, 400);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		JPanel panel = new JPanel();
		frame.setContentPane(panel);

		ta = new JTextArea(text);
		ta.setEditable(false);

		JTextField field = new JTextField();

		JComboBox<String> box = new JComboBox<>();
		if (list != null) {
			for (String s : list) {
				box.addItem(s);
			}
		}

		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(ta);
		panel.add(field);
		panel.add(box);

		field.addActionListener((ActionListener) -> {
			ta.insert("\nMoi : " + field.getText(), ta.getText().length());
			if (beh!=null) {
				beh.send(field.getText());
			} else {
				System.err.println("Behavior not set (yet?)");
			}
			field.setText("");
		});

		box.addActionListener((ActionListener) -> {
			JOptionPane.showMessageDialog(panel, "Je vais me déplacer sur " + box.getSelectedItem().toString());
			SendCt.STD.send(new JAMmove(box.getSelectedItem().toString(), ta.getText()), myAgent);
			frame.dispose();
		});

		frame.setVisible(true);
	}

	/**
	 * 
	 * @param msg
	 *            le message à ajouter dans le textarea
	 */
	public void receive(String msg) {
		ta.insert("\n" + msg, ta.getText().length());
	}

	/**
	 * pour tester en statique seulement
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String[] l={"localhost"};
		new MobileChatSwing(null, null, l, null);
	}

}
