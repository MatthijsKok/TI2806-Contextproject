package nl.tudelft.ewi.ds.bank;

abstract class TransactionCounterparty {


    EType type
    String iban
    String email

    String name


    // boolean isIBANValid() { return (new IBANVerifier()).verify(this.iban)}
}
