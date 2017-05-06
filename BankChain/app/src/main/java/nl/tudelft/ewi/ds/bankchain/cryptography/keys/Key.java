package nl.tudelft.ewi.ds.bankchain.cryptography.keys;


import android.util.Base64;

abstract class Key {

    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public String getDataAsBase64String() {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setData(String string) {
        this.data = Base64.decode(string, Base64.NO_WRAP);
    }
}
