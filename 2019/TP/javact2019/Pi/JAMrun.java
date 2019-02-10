public class JAMrun implements org.javact.lang.Message
{
	private int signatureNumber ;


	public JAMrun()
	{
		signatureNumber = 0 ;
	}

	public final void handle(org.javact.lang.QuasiBehavior _behavior)
	{
		switch (signatureNumber)
		{
			case 0 :
				if (_behavior instanceof Superviseur)
					((Superviseur) _behavior).run() ;
				else 
					throw new org.javact.lang.MessageHandleException() ;
				break ;
			default :
				throw new org.javact.lang.MessageHandleException() ;
		}
	}
}
