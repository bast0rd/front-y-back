package com.andesstay.reservations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;

@SpringBootApplication
public class ReservationsApplication {

    public static void main(String[] args) {
        File walletDir = new File("../wallet");
        System.setProperty("oracle.net.tns_admin", walletDir.getAbsolutePath());

        SpringApplication.run(ReservationsApplication.class, args);
    }
}