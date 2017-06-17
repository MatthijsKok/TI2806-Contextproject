package nl.tudelft.ewi.ds.bankver.bank.bunq.api;

/**
 * A POJO for the often used "Token" block in Bunq responses.
 *
 * @author Jos Kuijpers
 */
public class GenericToken {
    public int id;
    public String created; // Optional
    public String updated; // Optional
    public String token;
}
