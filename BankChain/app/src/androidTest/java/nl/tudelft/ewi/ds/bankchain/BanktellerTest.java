package nl.tudelft.ewi.ds.bankchain;


import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Richard-HP on 23/06/2017.
 */

public class BanktellerTest {

    @Test
    public void testcreateTeller() throws IOException {
        assertNotNull(createTeller());
    }

    @Test
    public void testcheckExisting() throws IOException {
        BankTeller teller = createTeller();
        teller.addkey("steve", "NL67TRIO0786890722", "3b6a27bcceb6a42d62a3a8d02a6f0d73653215771de243a63ac048a18b59da29", true);
        assertTrue(teller.blockchain.isValidated("NL67TRIO0786890722"));

    }

    private BankTeller createTeller() throws IOException {
        InputStream in = getInputStream("res/raw/test_environment.xml");
        assertNotNull(in);
        Environment env = Environment.loadDefaults(in);
        return BankTeller.getBankTeller(getInstrumentation().getTargetContext(), env);
    }

    private InputStream getInputStream(String path) {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }
}
