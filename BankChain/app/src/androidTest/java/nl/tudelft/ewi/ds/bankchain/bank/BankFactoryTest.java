package nl.tudelft.ewi.ds.bankchain.bank;

import org.junit.Test;

import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqBank;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Jos Kuijpers
 */
public class BankFactoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNullEnvironment() throws Exception {
        new BankFactory(null).create();
    }

    @Test
    public void testUnknoqnBankEnvironment() throws Exception {
        Bank bank;
        Environment env;

        env = new Environment();
        env.bank = "NONE";

        bank = new BankFactory(env).create();

        assertNull(bank);
    }

    @Test
    public void testBunqEnvironment() throws Exception {
        Bank bank;
        Environment env;

        env = new Environment();
        env.bank = "Bunq";
        env.url = "https://example.com/";
        env.apiKey = "";

        bank = new BankFactory(env).create();

        assertNotNull(bank);
        assertTrue(bank instanceof BunqBank);
    }
}
