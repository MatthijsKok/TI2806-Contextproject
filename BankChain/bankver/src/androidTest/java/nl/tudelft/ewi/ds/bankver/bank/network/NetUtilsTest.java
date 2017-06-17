package nl.tudelft.ewi.ds.bankver.bank.network;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author Jos Kuijpers
 */
public class NetUtilsTest {

    @Test
    public void testIpv4() {
        String ip = NetUtils.getIPAddress(NetUtils.IPVersion.IPV4);

        assertNotNull(ip);
    }

    @Test
    public void testIpv6() {
        String ip = NetUtils.getIPAddress(NetUtils.IPVersion.IPV6);

        assertNotNull(ip);
    }
}
