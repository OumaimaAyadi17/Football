package com.api.football;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application principale pour l'API de gestion de l'équipe de football de Nice.
 *
 * Cette application fournit une API REST pour gérer les équipes et les joueurs
 * avec des fonctionnalités de pagination et de tri.
 *
 * @author API Football API Team
 * @version 1.0.0
 */
@SpringBootApplication
public class FootballApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootballApplication.class, args);
	}

}
