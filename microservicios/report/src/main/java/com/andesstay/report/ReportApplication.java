package com.andesstay.report;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReportApplication {

	public static void main(String[] args) throws IOException {
		File walletDir = new File("../wallet").getCanonicalFile();

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

		SpringApplication.run(ReportApplication.class, args);
	}

}
