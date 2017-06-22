package nl.tudelft.ewi.ds.bankchain.Blockchain;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

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

    String filename;
    Map<String, jsonBlock> blockMap;
    Context context;


    public Blockchain(String filename, Context context,boolean openFile) {
        blockMap = new HashMap<String, jsonBlock>();
        this.filename = filename;
        this.context = context;
        if(openFile){
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
        Log.d("RESULT", toString());
    }
//{"blockchain":[{"IBAN":"NLABNA0123456789","name":"henk","validated":false},{"IBAN":"NLTRIO0123456789","name":"Steve","validated":true}]}

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


    public PublicKey GetPublicKeyForTransaction(Transaction trans) {
        return blockMap.get(trans.getCounterAccount().getIban()).getPublicKey();
    }

    private void open() {
        File block = new File(context.getFilesDir() + "/" + filename);
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
                KeyFactory fact = KeyFactory.getInstance("ed25519");

                return fact.generatePublic(spec);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                return null;
            }
        }
    }

}

