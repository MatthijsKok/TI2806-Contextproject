package nl.tudelft.ewi.ds.bankchain.bank;

import java.util.List;

/**
 * Created by Richard-HP on 15/05/2017.
 */

public interface Party {


    /**
     * Get name of the party, if available
     *
     * @return Name, or null
     */
    String getName();

    /**
     * return user id
     * @return ID or 0
     */
    int getId();

    /**
     * if the user is linked to this api key return the list of all connected accounts
     * @return
     */
    List<Account> getAccounts();


}
