package nl.tudelft.ewi.ds.bankchain.bank.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Network utilities
 *
 * @author Jos Kuijpers
 */
public final class NetUtils {
    public enum IPVersion {
        IPV4,
        IPV6
    }

    /**
     * Get the current IPv4 adress.
     *
     * @return ipv4 address or null
     */
    public static String getIPAddress() {
        return NetUtils.getIPAddress(IPVersion.IPV4);
    }

    /**
     * Get the current IP address with given version.
     * @param version version
     * @return ip or null
     */
    // http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device-from-code
    public static String getIPAddress(IPVersion version) {
        try {
            List<NetworkInterface> interfaces;

            interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface iface : interfaces) {
                List<InetAddress> adresses = Collections.list(iface.getInetAddresses());

                for (InetAddress inetAddress : adresses) {
                    if (inetAddress.isLoopbackAddress()) {
                        continue;
                    }

                    String address = inetAddress.getHostAddress();
                    boolean isIPv4 = address.indexOf(':') < 0;

                    if (version == IPVersion.IPV4 && isIPv4) {
                        return address;
                    } else if (version == IPVersion.IPV6 && !isIPv4) {
                        // Drop ip6 zone suffix
                        int delim = address.indexOf('%');

                        return delim < 0 ? address.toUpperCase(Locale.ENGLISH)
                                : address.substring(0, delim).toUpperCase(Locale.ENGLISH);
                    }
                }
            }
        } catch (SocketException e) {
            // Thrown when the android app does not have INTERNET permissions
            // Can't be tested in the same program.
            return null;
        }

        return null;
    }
}
