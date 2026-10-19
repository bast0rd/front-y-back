package com.andesstay.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class AuditApplication {

    public static void main(String[] args) throws IOException {
        File walletDir = new File("wallet").getCanonicalFile();

        System.setProperty(
                "oracle.net.tns_admin",
                walletDir.getAbsolutePath());

        System.out.println("Wallet: " + walletDir.getAbsolutePath());
        System.out.println("Existe: " + walletDir.exists());
        System.out.println(
                "tnsnames.ora existe: " +
                        new File(walletDir, "tnsnames.ora").exists());
        System.out.println(
                "TNS_ADMIN: " +
                        System.getProperty("oracle.net.tns_admin"));

        SpringApplication.run(AuditApplication.class, args);
    }
}