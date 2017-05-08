package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;

import nl.tudelft.ewi.ds.bankchain.bank.Session;

public final class BunqSession extends Session {
    /*
    private key
    public key

    counter

    server public key
    client-auth
    device server id
    device IP
    session id
    session token = client auth
    token date = Response[1].Token.created
    */

    private KeyPair clientKeyPair;
    private SignHelper signHelper;
    private PublicKey serverPublicKey;

    public BunqSession() {
    }

    public void openSession() {


        // Create a keypair for the client
        this.clientKeyPair = this.createClientKeyPair();


        // Send public key to server
        // DO POST to /installation
        // Receive public server key

        this.serverPublicKey = null;

        this.signHelper = new SignHelper(this.clientKeyPair, this.serverPublicKey);

        
    }

    public SignHelper getSignHelper() {
        return this.signHelper;
    }

    /**
     * Create a new keypair
     *
     * @return keypair or null
     */
    private KeyPair createClientKeyPair() {
        SecureRandom random = null;
        KeyPair pair;

        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            random = SecureRandom.getInstance("SHA1PRNG");

            // Bunq requires key size of 2048 bits
            keygen.initialize(2048, random);

            pair = keygen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

            return null;
        }

        return pair;
    }

    @Override
    public boolean isValid() {
        return false;
    }
}

/*

Create:
- create client public and private keypair (store)
- create number: 1
- send request to /installation with client pub key
  - store installationId (Response[0].id.id)
  - store token (Response.[1].Token.token) = Client-Authentication header dor [device and session]
  - store server pub key (Response[2].ServerPublicKey)

- send request to /device-server (signed)
-- with, in json, secret (API key)
-- store own IP for reference check
-- description (Random thing)
-- Client-Authentication header
  - store id of DeviceServer (is  header), (Response[0].id.id)

- send request to /session-server (signed)
- provide Client-Authentication header
- provide Client-Signature header
- JSON: secret (API key)
 - store session id (Response[0].id.id) (to end session)
 - store session token (Response[1].Token.token) = Client-Authentication

DONE


Signing

SHA256 hash, as base64 in X-Bunq-Client-Signature req, and X-Bunq-Server-Signature resp

requests:
  - for requests only: method in capitals, request endpoint url incl query string. "POST /v1/user"
  - for response only: response code
  - \n
  - headers, sorted alphabetically by key, key and value separated by ": ", only Cache-Control, User-Agent and X-Bunq-. Separate using \n
  - two \n
  - request or response body


sign using private key (of public key sent to server in first step)

verify server signature using public key of server

RSA keypair must be 2048 bits and adhere to PKCS #8.


Create keypair

Use Signature

initVerify(public) of server

initSign(privatekey) of client
initSign(private, secure)

method: SHA256withRSA

Signature s = Signature.getInstance("SHA256withRSA")
s.initSign(privateKey) // of client
s.update(dataToSign bytes)
bytes = s.sign()


s.initVerify(publicKey) // of server
s.verify(byte[] signature)

 */
