package nl.tudelft.ewi.ds.bankchain.blockchain_hello;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Interface for the public API, to talk back to a blockchain.
 *
 * @author Jos Kuijpers
 */
public interface IBlockchain {

    /**
     * Get the private key for the blockchain owner.
     *
     * @return private key of type EdDSA25519
     */
    @NonNull
    PrivateKey getPrivateKey();

    /**
     * Get the public key of the blockahin owner.
     *
     * @return public key of type EdDSA25519
     */
    @NonNull
    PublicKey getPublicKey();

    /**
     * Get the public key for the user with given IBAN
     *
     * @param iban IBAM
     * @return A public key of type EdDSA25519, or null if not found
     */
    @Nullable
    PublicKey getPublicKeyForIBAN(String iban);

    /**
     * Called when given public key has been verified.
     *
     * @param publicKey Public key used for the verification
     * @param iban      IBAN of the bank account
     * @param legalName Legal name connected to the bank account
     */
    void setIbanVerified(PublicKey publicKey, String iban, String legalName);
}