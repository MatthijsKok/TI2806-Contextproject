package nl.tudelft.ewi.ds.bankchain.Blockchain;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import net.i2p.crypto.eddsa.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.ewi.ds.bankchain.bank.IBANVerifier;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.cryptography.ED25519;
import okhttp3.internal.Util;

/**
 * Created by Richard-HP on 01/06/2017.
 */

public class Blockchain implements IBlockchain {

    String filename;
    Map<String, jsonBlock> blockMap;
    Context context;


    public Blockchain(String filename, Context context, boolean openFile) {
        blockMap = new HashMap<String, jsonBlock>();
        this.filename = filename;
        this.context = context;
        if (openFile) {
            openFile();
        }
    }

    public void openFile() {
        Gson gson = new Gson();
        try {
            FileInputStream fis = null;
            fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            jsonChain chain = gson.fromJson(bufferedReader.readLine(), jsonChain.class);
            for (jsonBlock block : chain.blockchain) {
                blockMap.put(block.iban, block);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addKey(PublicKey key, String iban, String name, boolean validated) {
        if (blockMap.containsKey(iban)) {
            return false;
        }
        blockMap.put(iban, new jsonBlock(key, iban, name, validated));
        return true;
    }

    public boolean isValidated(Transaction trans) {
        return blockMap.containsKey(trans.getCounterAccount().getIban());
    }

    public String toString() {
        jsonChain chain = new jsonChain();
        chain.blockchain = new ArrayList<>(blockMap.values());
        Gson gson = new Gson();
        String json = gson.toJson(chain);
        return json;
    }

    public void save() {
        jsonChain chain = new jsonChain();
        chain.blockchain = new ArrayList<>(blockMap.values());
        Gson gson = new Gson();
        String json = gson.toJson(chain);
        System.out.println(json);

        String FILENAME = "hello_file";
        String string = "hello world!";


        File path = context.getFilesDir();
        Log.d("starting write", "Writing0");
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("LOCATION1", path.getPath());
    }

    @NonNull
    @Override
    // not implemented in this version
    public PrivateKey getPrivateKey() {
        return null;
    }

    @NonNull
    @Override
    // not implemented in this version
    public PublicKey getPublicKey() {
        return null;
    }

    @Nullable
    @Override
    public PublicKey getPublicKeyForIBAN(String iban) {
        jsonBlock block= blockMap.get(iban);
        PublicKey k = block.getPublicKey();
        return  k;
    }

    @Override
    public void setIbanVerified(PublicKey publicKey, String iban, String legalName) {
        addKey(publicKey, iban, legalName, true);
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
            publicKey = Utils.bytesToHex(key.getEncoded());
            key.toString();
        }

        public PublicKey getPublicKey() {
           return ED25519.getPublicKey(publicKey);
        }
    }

}

