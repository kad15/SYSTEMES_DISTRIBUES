package examples.local.CCE;
/**
 * StringUtil , TP de DESS IF (JavAct) - (c) 2004 S�bastien Leriche
 * Ne pas utiliser pour des applications r�elles
 */
public class StringUtil {
	
	private static byte[] key={26,123,72,9,17,91,48,51,97,6,24,7,33,125,109,111};
	
	/**
	 * Crypte la chaine s avec la cl� par d�faut sur 128 bits
	 */
	public static String crypte(String s) {
		byte[] buffer=s.getBytes();
		for (int i=0; i<s.length(); i++)
			buffer[i]=(byte) (buffer[i] ^ key [i % key.length]);
		return new String(buffer);	
		    
	}
	
	/**
	 * Decrypte la chaine s avec la cl� par d�faut sur 128 bits
	 */
	public static String decrypte(String s) {
	 	return crypte(s);
	}
	
	/**
	 * Ajoute un caract�re de controle � la fin de la chaine s 
	 */
	public static String ajouteCtrl(String s) {
		byte[] buffer=s.getBytes();
		byte x=26;
		for(int i=0; i<s.length(); i++)
			x=(byte) (buffer[i] ^ x);		
		
		byte [] ctrl=new byte[1];
		ctrl[0]=(byte)x;
		return s+new String(ctrl);
	}
	
	/**
	 * Verifie la conformite de la chaine s avec son code d'erreur (dernier caract�re), 
	 * renvoie null en cas de pb, sinon la chaine sans la caract�re de controle
	 */
	 public static String verifieCtrl(String s) {
		byte[] buffer=s.getBytes();
		byte x=26;
		for(int i=0; i<s.length() -1; i++)
			x=(byte) (buffer[i] ^ x);
		if (x==buffer[s.length()-1]) return s.substring(0,s.length()-1);
		else return null;
	}
	
	/* juste pour le test */
	public static void main(String args[]) {
		String s=args[0];
		System.out.println(s);
		System.out.println(crypte(s));
		System.out.println(decrypte(crypte(s)));
		System.out.println(ajouteCtrl(s));
		System.out.println(verifieCtrl(ajouteCtrl(s)));
	}
	
}
