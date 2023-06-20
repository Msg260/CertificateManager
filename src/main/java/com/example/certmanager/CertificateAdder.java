package com.example.certmanager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;
import java.util.Scanner;

public class CertificateAdder {
    public void addCertificate(String certificatePath, String outputDirectory) {
        try {
            // Read the certificate file
            byte[] certBytes = Files.readAllBytes(Paths.get(certificatePath));
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream bis = new ByteArrayInputStream(certBytes);
            Certificate certificate = certificateFactory.generateCertificate(bis);
            bis.close();


            // Load the cacerts keystore
            final char sep = File.separatorChar;
            File dir = new File(outputDirectory);
            File file = new File(dir, "cacerts");


            InputStream localCertIn = new FileInputStream(file);
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] passphrase = "changeit".toCharArray();
            keystore.load(localCertIn, passphrase);

            if (keystore.containsAlias("myAlias")) {
                localCertIn.close();
                System.out.println("Already exists");
                return;
            }
            localCertIn.close();

            // Add the certificate to the keystore
            keystore.setCertificateEntry("myAlias", certificate);
            // Save the updated keystore to a file
            File outputDir = new File(outputDirectory);
            if (outputDir.isDirectory()) {
                File outputFile = new File(outputDir, "cacerts");
                OutputStream out = new FileOutputStream(outputFile);
                keystore.store(out, passphrase);
                out.close();
                System.out.println("Certificate added successfully. Updated cacert file: " + outputFile.getAbsolutePath());
            } else {
                System.out.println("Invalid output directory path");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
