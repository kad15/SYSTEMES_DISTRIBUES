public class JAMsendText implements org.javact.lang.Message
{
	private int signatureNumber ;

	private java.lang.String sig0attr0 ;

	public JAMsendText(java.lang.String _p0)
	{
		signatureNumber = 0 ;
		sig0attr0 = _p0 ;
	}

	public final void handle(org.javact.lang.QuasiBehavior _behavior)
	{
		switch (signatureNumber)
		{
			case 0 :
				if (_behavior instanceof ChatAgent)
					((ChatAgent) _behavior).sendText(sig0attr0) ;
				else 
					throw new org.javact.lang.MessageHandleException() ;
				break ;
			default :
				throw new org.javact.lang.MessageHandleException() ;
		}
	}
}
