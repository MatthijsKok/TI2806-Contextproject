package nl.tudelft.ewi.ds.bank;

abstract class Transaction {

    // id => BunqTransaction

    created // can be null, getter only

    Float value
    Currency currency
    TransactionCounterparty counterparty
    String description
}
