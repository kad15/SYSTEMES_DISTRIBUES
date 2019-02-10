import org.javact.lang.QuasiBehavior;

public abstract class SuperviseurQuasiBehavior extends QuasiBehavior implements Superviseur
{
	public void become(Afficheur b)
	{
		try
		{ becomeAny((QuasiBehavior) b); }
		catch (RuntimeException e)
		{ throw new org.javact.lang.BecomeException(e);}
	}

}
