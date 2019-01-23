package org.javact.plugin.tools;
// TODO comment
public class Place {
	
	private String host;
	private int port;
	private boolean debug;
	
	
	public boolean isDebug() {
		return debug;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public Place(String host, int port, boolean debug) {
		this.host = host;
		this.port = port;
		this.debug = debug;
	}
	
	public boolean equals(Place place) {
		return host.equals(place.getHost()) && (port == place.getPort()); 
	}
	
	public String getName(){
		return host + ":" + port;
	}
	
	public String toString() {
		return host + ":" + port + (debug?" -d":"");
	}
	

}
