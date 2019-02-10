public class JAMsetDest implements org.javact.lang.Message
{
	private int signatureNumber ;

	private org.javact.lang.Agent sig0attr0 ;

	public JAMsetDest(org.javact.lang.Agent _p0)
	{
		signatureNumber = 0 ;
		sig0attr0 = _p0 ;
	}

	public final void handle(org.javact.lang.QuasiBehavior _behavior)
	{
		switch (signatureNumber)
		{
			case 0 :
				if (_behavior instanceof MobileChat)
					((MobileChat) _behavior).setDest(sig0attr0) ;
				else 
					throw new org.javact.lang.MessageHandleException() ;
				break ;
			default :
				throw new org.javact.lang.MessageHandleException() ;
		}
	}
}
