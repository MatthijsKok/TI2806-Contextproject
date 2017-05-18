package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import java.util.Currency;
import java.util.Date;

import nl.tudelft.ewi.ds.bankchain.bank.Account;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.PaymentService;

/**
 * A transaction from the Bunq bank
 *
 * @author Jos Kuijpers
 */
final class BunqTransaction implements Transaction {

    private Date date;
    private float value;
    private String description;
    private Currency currency;
    private Account counterAccount;
    private Account account;


    BunqTransaction(PaymentService.ListResponse.Payment payment, Account account) {
        date = new Date(22111990); //payment.getCreationDate(); Todo fix payment
        value = payment.amount.getValue();
        currency = payment.amount.getCurrency();
        description = payment.description;
        this.account = account;
        this.counterAccount = new BunqAccount(payment.counterParty.iban, -1, new BunqParty(payment.counterParty.name, -1));
    }


    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public Account getCounterAccount() {
        return counterAccount;
    }

    @Override
    public Account getAcount() {
        return account;
    }


    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "<Payment desc:'" + getDescription() + "'>";
    }
}
