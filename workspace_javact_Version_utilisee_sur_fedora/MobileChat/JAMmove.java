public class JAMmove implements org.javact.lang.Message
{
	private int signatureNumber ;

	private java.lang.String sig0attr0 ;
	private java.lang.String sig0attr1 ;

	public JAMmove(java.lang.String _p0, java.lang.String _p1)
	{
		signatureNumber = 0 ;
		sig0attr0 = _p0 ;
		sig0attr1 = _p1 ;
	}

	public final void handle(org.javact.lang.QuasiBehavior _behavior)
	{
		switch (signatureNumber)
		{
			case 0 :
				if (_behavior instanceof MobileChat)
					((MobileChat) _behavior).move(sig0attr0, sig0attr1) ;
				else 
					throw new org.javact.lang.MessageHandleException() ;
				break ;
			default :
				throw new org.javact.lang.MessageHandleException() ;
		}
	}
}
