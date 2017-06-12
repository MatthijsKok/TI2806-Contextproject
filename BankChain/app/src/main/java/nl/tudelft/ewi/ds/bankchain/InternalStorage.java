package nl.tudelft.ewi.ds.bankchain;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Isha Dijcks
 */

public class InternalStorage {

    public static List<VerifiedAccount> retrieveCompletedVerifications(Context context) {
        List<VerifiedAccount> completedVerificationsList = new ArrayList<>();

        String filename = "CompletedVerifications.txt";
        String verifiedAccounts = readFromInternalStorage(filename, context);

        if (verifiedAccounts.equals("")) {
            return completedVerificationsList;
        }
        String[] lines = verifiedAccounts.split("\\r?\\n");


        for (int i = lines.length - 1; i >= 0; i--) {
            completedVerificationsList.add(parseVerifiedAccount(lines[i]));
        }
        return completedVerificationsList;
    }

    public static void appendVerification(Context context, String string) {
        String filename = "CompletedVerifications.txt";
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_APPEND);
            fos.write(string.getBytes(Charset.forName("UTF-8")));
            fos.close();

        } catch (Exception ignored) {
            Log.d("Internal Storage", "appendVerification: exception");
        }

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
        } catch (Exception ignored) {
            Log.d("Internal Storage", "readFromInternalStorage: exception");
        }
        return temp;
    }
}
