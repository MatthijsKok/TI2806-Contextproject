package nl.tudelft.ewi.ds.bankchain.bank;

import org.junit.Test;

import nl.tudelft.ewi.ds.bankchain.Environment;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqBank;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Jos Kuijpers
 */
public class BankFactoryTest {
    @Test
    public void testUnknoqnBankEnvironment() throws Exception {
        Bank bank;
        Environment env;

        env = new Environment();
        env.setBank("NONE");

        bank = new BankFactory(env).create();

        assertNull(bank);
    }

    @Test
    public void testBunqEnvironment() throws Exception {
        Bank bank;
        Environment env;

        env = new Environment();
        env.setBank("Bunq");
        env.setBankUrl("https://example.com/");
        env.setBankApiKey("");

        bank = new BankFactory(env).create();

        assertNotNull(bank);
        assertTrue(bank instanceof BunqBank);
    }
}
