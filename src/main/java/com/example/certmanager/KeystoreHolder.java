package com.example.certmanager;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;

public class KeystoreHolder {
    private KeyStore keystore;

    public KeystoreHolder(String keystorePath, String keystorePassword) {
        try {
            // Load the keystore
            FileInputStream fis = new FileInputStream(keystorePath);
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(fis, keystorePassword.toCharArray());
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public KeyStore getKeystore() {
        return keystore;
    }

    public void removeCertificate(String alias) {
        try {
            keystore.deleteEntry(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }
}
