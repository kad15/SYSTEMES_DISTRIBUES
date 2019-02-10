import org.javact.lang.QuasiBehavior;

public abstract class RechercheQuasiBehavior extends QuasiBehavior implements Recherche
{
	public void become(Superviseur b)
	{
		try
		{ becomeAny((QuasiBehavior) b); }
		catch (RuntimeException e)
		{ throw new org.javact.lang.BecomeException(e);}
	}

}
