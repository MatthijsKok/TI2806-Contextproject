package nl.tudelft.ewi.ds.bankchain;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Jos Kuijpers
 */
public class EnvironmentTest {

    private InputStream getInputStream(String path) {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }

    @Test
    public void testLoading() throws IOException {
        InputStream in = getInputStream("res/raw/test_environment.xml");

        assertNotNull(in);

        Environment env = Environment.loadDefaults(in);

        assertNotNull(env);
        assertEquals(env.getBank(), "Bunq");
    }

    @Test
    public void testFaultyXML() {
        InputStream in = getInputStream("res/raw/test_environment_faulty.xml");
        assertNotNull(in);

        try {
            Environment env = Environment.loadDefaults(in);

            assertNull(env);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNoFile() throws IOException {
        InputStream in = getInputStream("res/raw/test_environment_none.xml");
        assertNull(in);
    }
}
