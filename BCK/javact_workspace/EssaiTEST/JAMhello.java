public class JAMhello implements org.javact.lang.Message
{
	private int signatureNumber ;


	public JAMhello()
	{
		signatureNumber = 0 ;
	}

	public final void handle(org.javact.lang.QuasiBehavior _behavior)
	{
		switch (signatureNumber)
		{
			case 0 :
				if (_behavior instanceof toto)
					((toto) _behavior).hello() ;
				else 
					throw new org.javact.lang.MessageHandleException() ;
				break ;
			default :
				throw new org.javact.lang.MessageHandleException() ;
		}
	}
}
