package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import java.util.Currency;
import java.util.Date;

import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.TransactionCounterparty;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.PaymentService;

/**
 * A transaction from the Bunq bank
 *
 * @author Jos Kuijpers
 */
final class BunqTransaction extends Transaction {
    private PaymentService.ListResponse.Payment payment;

    BunqTransaction(PaymentService.ListResponse.Payment payment) {
        this.payment = payment;
    }

    @Override
    // TODO: implement
    public Direction getDirection() {
        return null;
    }

    @Override
    public Date getDate() {
        return payment.getCreationDate();
    }

    @Override
    public Float getValue() {
        return payment.amount.getValue();
    }

    @Override
    public Currency getCurrency() {
        return payment.amount.getCurrency();
    }

    @Override
    // TODO: implement
    public TransactionCounterparty getCounterparty() {
        return null;
    }

    @Override
    public String getDescription() {
        return payment.description;
    }

    @Override
    public String toString() {
        return "<Payment desc:'" + getDescription() + "'>";
    }
}
