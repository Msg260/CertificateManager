package com.example.certmanager;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class HelloApplication extends Application {
    private String cacertPath = "";

    Stage window;
    Scene sceneMain, scene1, scene2, scene3, scene5;
    Button button1, button2, button3, button4, button5;
    KeystoreHolder keystoreHolder;

    @Override
    public void start(Stage primaryStage) throws IOException {

        window = primaryStage;
        window.setTitle("Certificate Manager");



        //defining Main scene and buttons

        button1 = new Button("1-) Add Certificate");
        button2 = new Button("2-) List All Certificates");
        button3 = new Button("3-) Delete a certificate");
        button4 = new Button("4-) Exit");
        button5 = new Button("5-) Settings");

        VBox layout = new VBox(20);
        layout.getChildren().addAll(button1, button2, button3, button4, button5);
        Scene sceneMain = new Scene(layout, 300,400);
        window.setScene(sceneMain);
        window.show();



        //defining Main scene button actions

        button1.setOnAction(e -> window.setScene(scene1));
        button2.setOnAction(e ->{
            //make a list of labels from keystore
            keystoreHolder = new KeystoreHolder(cacertPath+"\\cacerts", "changeit");
            KeyStore keystore = keystoreHolder.getKeystore();
            Enumeration<String> aliases = null;
            try {
                aliases = keystore.aliases();
            } catch (KeyStoreException ex) {
                throw new RuntimeException(ex);
            }
            List<Label> labelList = new ArrayList<>();

            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate certificate = null;
                try {
                    certificate = keystore.getCertificate(alias);
                } catch (KeyStoreException ex) {
                    throw new RuntimeException(ex);
                }

                labelList.add(new Label("Alias: " + alias + "\n" + "Certificate: " + certificate.toString() + "\n----------------------------------"));
            }
            // add all labels to layout2
            VBox layout2 = new VBox();
            layout2.setSpacing(10);
            layout2.getChildren().add(new BackButton(sceneMain));
            for (Label label : labelList) {
                layout2.getChildren().add(label);
            }
            //for the scrolling feature
            ScrollPane layout2WithScrolling = new ScrollPane(layout2);
            layout2WithScrolling.setPrefSize(700, 700);
            scene2 = new Scene(layout2WithScrolling, 700, 700);

            window.setScene(scene2);
        } );
        button3.setOnAction(e ->{
            //make a list of labels from keystore
            keystoreHolder = new KeystoreHolder(cacertPath+"\\cacerts", "changeit");
            KeyStore keystore = keystoreHolder.getKeystore();
            Enumeration<String> aliases = null;
            try {
                aliases = keystore.aliases();
            } catch (KeyStoreException ex) {
                throw new RuntimeException(ex);
            }
            VBox layout3 = new VBox();
            layout3.setSpacing(10);
            layout3.getChildren().add(new BackButton(sceneMain));

            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate certificate = null;
                try {
                    certificate = keystore.getCertificate(alias);
                } catch (KeyStoreException ex) {
                    throw new RuntimeException(ex);
                }
                Button button = new Button("Alias: " + alias + "\n" + "Certificate: " + certificate.toString() + "\n----------------------------------");
                button.setOnAction(event -> {
                    keystoreHolder.removeCertificate(alias);
                    KeyStore keystoreNew = keystoreHolder.getKeystore();

                    File outputDir = new File(cacertPath);
                    if (outputDir.isDirectory()) {
                        File outputFile = new File(outputDir, "cacerts");
                        OutputStream out = null;
                        try {
                            out = new FileOutputStream(outputFile);
                        } catch (FileNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                        char[] passphrase = "changeit".toCharArray();

                        try {
                            keystore.store(out, passphrase);
                        } catch (KeyStoreException ex) {
                            throw new RuntimeException(ex);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        } catch (NoSuchAlgorithmException ex) {
                            throw new RuntimeException(ex);
                        } catch (CertificateException ex) {
                            throw new RuntimeException(ex);
                        }
                        try {
                            out.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        System.out.println("Certificate DELETED successfully. Updated cacert file: " + outputFile.getAbsolutePath());
                    } else {
                        System.out.println("Invalid output directory path");
                    }
                    window.setScene(sceneMain);
                });
                layout3.getChildren().add(button);
            }
            //for the scrolling feature
            ScrollPane layout2WithScrolling = new ScrollPane(layout3);
            layout2WithScrolling.setPrefSize(700, 700);
            scene3 = new Scene(layout2WithScrolling, 700, 700);

            window.setScene(scene3);
        } );
        button4.setOnAction(event -> window.close());

        button5.setOnAction(e -> window.setScene(scene5));






        //scene1
        CertificateAdder certificateAdder = new CertificateAdder();

        TextField inputField = new TextField();
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            String newCertPath = inputField.getText();
            System.out.println("User input: " + newCertPath);
            certificateAdder.addCertificate(newCertPath, cacertPath);
            window.setScene(sceneMain);
        });

        VBox layout1 = new VBox(10);
        Label label1 = new Label("Input 1:");
        layout1.getChildren().addAll(label1, inputField, submitButton, new BackButton(sceneMain));
        scene1 = new Scene(layout1, 1000, 200);

        //scene5

        TextField inputField5 = new TextField();
        Button submitCacertButton = new Button("Submit");
        submitCacertButton.setOnAction(event -> {
            cacertPath = inputField5.getText();
            System.out.println("cacaert path: " + cacertPath);
            window.setScene((sceneMain));
            // You can perform any desired action with the user input here
        });

        VBox layout5 = new VBox(10);
        Label label5 = new Label("Enter the directory path for cacert file: ");
        layout5.getChildren().addAll(label5, inputField5, submitCacertButton, new BackButton(sceneMain));
        scene5 = new Scene(layout5, 1000, 200);

    }

    // BackButton class in order to use Back button in severel scenes
    private class BackButton extends Button {
        public BackButton(Scene targetScene) {
            setText("Back");
            setOnAction(event -> window.setScene(targetScene));
        }
    }

    public static void main(String[] args) {
        launch();
    }
}

