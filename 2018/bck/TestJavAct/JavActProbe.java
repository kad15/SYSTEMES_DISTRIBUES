/*
 JavActProbe tests the presence of JVM Javact on a list of adresses
 Copyright (C) 2008-2016 Sebastien Leriche, sebastien.leriche@enac.fr

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author leriche
 * 
 */
public class JavActProbe implements Callable<String> {

	private String address;

	private int port;

	private static final int ADDRESSSTART = 2; // 1 is usually the router

	private static final int ADDRESSEND = 254; // 255 is a broadcast address

	private static final int TIMEOUT = 500;

	private static ArrayList<String> addressList = new ArrayList<String>();

	private static ExecutorService threadPool = Executors.newCachedThreadPool();

	private static ArrayList<Future<String>> futureList = new ArrayList<Future<String>>();

	public synchronized static String[] probe(int port) {
		String localIP = "127.0.0.1";
		try {
			// localIP = InetAddress.getLocalHost().getHostAddress();
			InetAddress adr = getLocalHostLANAddress();
			localIP = adr.getHostAddress();
			localIP = localIP.substring(0, localIP.lastIndexOf("."));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return probe(port, localIP);
	}

	public synchronized static String[] probe(int port, String baseIP) {
		futureList.clear();

		for (int i = ADDRESSSTART; i < ADDRESSEND; i++) {
			futureList.add(threadPool.submit(new JavActProbe(baseIP + "." + i,
					port)));
		}

		try {
			threadPool.awaitTermination(1, TimeUnit.SECONDS);
		} catch (Exception e) {
		}

		threadPool.shutdownNow();

		try {
			for (Future<String> future : futureList)
				if (future.isDone() && future.get() != null)

					addressList.add(future.get());
		} catch (Exception e) {
		}

		return addressList.toArray(new String[0]);
	}

	private JavActProbe(String address, int port) {
		this.address = address;
		this.port = port;
	}

	public String call() {
		boolean result = false;

		Socket socket = new Socket();

		try {
			socket.connect(new InetSocketAddress(address, port), TIMEOUT);
			result = socket.isConnected()
					/*&& (Naming.lookup("rmi://" + address + ":" + port
							+ "/Asteroide325") != null)*/;
			socket.close();
		} catch (Exception e) {
		}

		return (result) ? address : null;
	}

	/**
	 * Returns an <code>InetAddress</code> object encapsulating what is most
	 * likely the machine's LAN IP address.
	 * <p/>
	 * This method is intended for use as a replacement of JDK method
	 * <code>InetAddress.getLocalHost</code>, because that method is ambiguous
	 * on Linux systems. Linux systems enumerate the loopback network interface
	 * the same way as regular LAN network interfaces, but the JDK
	 * <code>InetAddress.getLocalHost</code> method does not specify the
	 * algorithm used to select the address returned under such circumstances,
	 * and will often return the loopback address, which is not valid for
	 * network communication. Details <a
	 * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037"
	 * >here</a>.
	 * <p/>
	 * This method will scan all IP addresses on all network interfaces on the
	 * host machine to determine the IP address most likely to be the machine's
	 * LAN address. If the machine has multiple IP addresses, this method will
	 * prefer a site-local IP address (e.g. 192.168.x.x or 10.10.x.x, usually
	 * IPv4) if the machine has one (and will return the first site-local
	 * address if the machine has more than one), but if the machine does not
	 * hold a site-local address, this method will return simply the first
	 * non-loopback address found (IPv4 or IPv6).
	 * <p/>
	 * If this method cannot find a non-loopback address using this selection
	 * algorithm, it will fall back to calling and returning the result of JDK
	 * method <code>InetAddress.getLocalHost</code>.
	 * <p/>
	 *
	 * @throws UnknownHostException
	 *             If the LAN address of the machine cannot be found.
	 */
	public static InetAddress getLocalHostLANAddress()
			throws UnknownHostException {
		try {
			InetAddress candidateAddress = null;
			// Iterate all NICs (network interface cards)...
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface
					.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces
						.nextElement();
				// Iterate all IP addresses assigned to each card...
				for (Enumeration<InetAddress> inetAddrs = iface
						.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs
							.nextElement();
					if (!inetAddr.isLoopbackAddress()) {

						if (inetAddr.isSiteLocalAddress()) {
							// Found non-loopback site-local address. Return it
							// immediately...
							return inetAddr;
						} else if (candidateAddress == null) {
							// Found non-loopback address, but not necessarily
							// site-local.
							// Store it as a candidate to be returned if
							// site-local address is not subsequently found...
							candidateAddress = inetAddr;
							// Note that we don't repeatedly assign non-loopback
							// non-site-local addresses as candidates,
							// only the first. For subsequent iterations,
							// candidate will be non-null.
						}
					}
				}
			}
			if (candidateAddress != null) {
				// We did not find a site-local address, but we found some other
				// non-loopback address.
				// Server might have a non-site-local address assigned to its
				// NIC (or it might be running
				// IPv6 which deprecates the "site-local" concept).
				// Return this non-loopback candidate address...
				return candidateAddress;
			}
			// At this point, we did not find a non-loopback address.
			// Fall back to returning whatever InetAddress.getLocalHost()
			// returns...
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException(
						"The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress;
		} catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException(
					"Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}

	public static void main(String[] args) {
		String[] v = probe(5099);
		for (String s : v)
			System.out.println(s);
	}
}
