package nl.tudelft.ewi.ds.bankchain;

import android.content.Context;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Isha Dijcks
 */

public class InternalStorage {

    public static List<VerifiedAccount> retrieveCompletedVerifications(Context context) {
        List<VerifiedAccount> completedVerificationsList = new ArrayList<>();

        String FILENAME = "CompletedVerifications.txt";
        context.deleteFile(FILENAME);
        String string = "23576523:NL253645532b:Nu\n3456789:NL978634:Gister\n";


        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_APPEND);
            fos.write(string.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String verifiedAccounts = readFromInternalStorage(FILENAME, context);
        String lines[] = verifiedAccounts.split("\\r?\\n");

        for (int i = lines.length-1; i >= 0; i--) {
            completedVerificationsList.add(parseVerifiedAccount(lines[i]));
        }
        return completedVerificationsList;
    }

    public static VerifiedAccount parseVerifiedAccount(String line) {
        String[] items = line.split(":");
        String publicKey = items[0];
        String iban = items[1];
        String date = items[2];
        return new VerifiedAccount(publicKey, iban, date);
    }

    public static String readFromInternalStorage(String filename, Context context) {
        int c;
        String temp = "";

        try {
            FileInputStream fin = context.openFileInput(filename);
            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }
}
