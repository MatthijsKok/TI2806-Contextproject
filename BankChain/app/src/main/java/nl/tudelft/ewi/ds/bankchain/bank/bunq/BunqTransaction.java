package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import java.util.Currency;
import java.util.Date;
import java.util.List;

import java8.util.concurrent.CompletableFuture;
import nl.tudelft.ewi.ds.bankchain.bank.Account;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.PaymentService;

/**
 * A transaction from the Bunq bank
 *
 * @author Jos Kuijpers
 */
public class BunqTransaction implements Transaction {


    private int id;
    private Date date;
    private float value;
    private String description;
    private Currency currency;
    private Account counterAccount;
    private Account account;


    public BunqTransaction(PaymentService.ListResponse.Payment payment, Account account) {
        date = new Date(22111990); //payment.getCreationDate(); Todo fix payment
        value = payment.amount.getValue();
        currency = payment.amount.getCurrency();
        description = payment.description;
        this.account = account;
        this.id = payment.id;
        this.counterAccount = new BunqAccount(payment.counterParty.iban, -1, new BunqParty(payment.counterParty.name, -1));
    }

    /**
     * used to create an outgoing trasnaction
     * @param amount should be negative
     * @param account source of the money
     * @param counterAccount recepient
     * @param currency
     * @param description
     */
    public BunqTransaction(float amount, Account account, Account counterAccount,Currency currency, String description){
        date = getDate();
        id = -1;
        this.currency = currency;
        value = amount;
        this.account = account;
        this.counterAccount = counterAccount;
        this.description = description;
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
    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    @Override
    public String toString() {
        return "<Payment desc:'" + getDescription() + "'>";
    }

    public PaymentService.PostRequest convertToRequest(){
        PaymentService.PostRequest p = new PaymentService.PostRequest();
        PaymentService.Amount a = new PaymentService.Amount();
        PaymentService.Counterparty_alias c = new PaymentService.Counterparty_alias();
        c.name = counterAccount.getParty().getName();
        c.value = counterAccount.getIban();
        a.currency = "EUR";
        a.value = Float.toString(value);
        p.description = description;
        return p;
    }
}
