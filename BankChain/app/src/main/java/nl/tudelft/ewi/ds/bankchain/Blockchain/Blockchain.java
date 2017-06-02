package nl.tudelft.ewi.ds.bankchain.Blockchain;

import android.util.Base64;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.ewi.ds.bankchain.bank.Transaction;

/**
 * Created by Richard-HP on 01/06/2017.
 */

public class Blockchain {

    String location;
    Map<String, jsonBlock> blockMap;


    public Blockchain(String location) {
      this.location = location;
        blockMap = new HashMap<String,jsonBlock>();

    }

    private void toMap(String json) {
        Gson gson = new Gson();
        jsonChain chain = gson.fromJson(json, jsonChain.class);
        for (jsonBlock block : chain.blockchain) {
            blockMap.put(block.iban, block);
        }
    }
//{"blockchain":[{"IBAN":"NLABNA0123456789","name":"henk","validated":false},{"IBAN":"NLTRIO0123456789","name":"Steve","validated":true}]}

    public boolean addKey(PublicKey key, String iban, String name, boolean validated) {
        if (blockMap.containsKey(iban)) {
            return false;
        }

        blockMap.put(iban,new jsonBlock(key, iban, name, validated));
        return true;
    }

    public boolean isValidated(Transaction trans) {
        return blockMap.containsKey(trans.getCounterAccount().getIban());
    }

    ;

    public PublicKey GetPublicKeyForTransaction(Transaction trans) {
        return blockMap.get(trans.getCounterAccount().getIban()).getPublicKey();
    }

    public void save(){
        jsonChain chain = new jsonChain();
        chain.blockchain = new ArrayList<>(blockMap.values());
        Gson gson = new Gson();
        String json = gson.toJson(chain);
        System.out.println(json);
    }

     class jsonChain {
        public List<jsonBlock> blockchain;
    }

    class jsonBlock {
        public String iban;
        public String name;
        public String publicKey;
        public boolean validated;

        public jsonBlock() {
        }


        public jsonBlock(PublicKey key, String iban, String name, boolean validated) {
            this.iban = iban;
            this.name = name;
            this.validated = validated;
            setPublicKey(key);
        }

        public void setPublicKey(PublicKey key) {
           publicKey = new String(Base64.encode(key.getEncoded(), 0), StandardCharsets.UTF_8);
        }

        public PublicKey getPublicKey() {
            try {
                byte[] data = Base64.decode(publicKey, Base64.DEFAULT);

                X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
                KeyFactory fact = KeyFactory.getInstance("RSA");

                return fact.generatePublic(spec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                return null;
            }
        }
    }

}

