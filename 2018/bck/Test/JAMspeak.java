public class JAMspeak implements org.javact.lang.Message
{
	private int signatureNumber ;


	public JAMspeak()
	{
		signatureNumber = 0 ;
	}

	public final void handle(org.javact.lang.QuasiBehavior _behavior)
	{
		switch (signatureNumber)
		{
			case 0 :
				if (_behavior instanceof Tourisk)
					((Tourisk) _behavior).speak() ;
				else 
					throw new org.javact.lang.MessageHandleException() ;
				break ;
			default :
				throw new org.javact.lang.MessageHandleException() ;
		}
	}
}
