package com.api.football.controller;

import com.api.football.dto.CreateEquipeRequest;
import com.api.football.dto.EquipeDto;
import com.api.football.service.EquipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des équipes.
 *
 * Ce contrôleur expose les endpoints pour :
 * - Récupérer la liste des équipes avec pagination et tri
 * - Créer une nouvelle équipe avec ou sans joueurs
 *
 * @author API Football API Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/equipes")
@Tag(name = "Équipes", description = "API pour la gestion des équipes de football")
public class EquipeController {

    private static final Logger logger = LoggerFactory.getLogger(EquipeController.class);

    @Autowired
    private EquipeService equipeService;

    /**
     * Récupère la liste des équipes avec pagination et tri.
     *
     * @param page le numéro de page (commence à 0, défaut: 0)
     * @param size la taille de la page (défaut: 10)
     * @param sortBy le champ de tri (nom, acronyme, budget, défaut: nom)
     * @param sortDirection la direction du tri (asc, desc, défaut: asc)
     * @return une page d'équipes
     */
    @GetMapping
    @Operation(
            summary = "Récupère la liste des équipes",
            description = "Récupère la liste paginée des équipes avec possibilité de tri sur nom, acronyme ou budget"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des équipes récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Paramètres de requête invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Page<EquipeDto>> getAllEquipes(
            @Parameter(description = "Numéro de page (commence à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Champ de tri (nom, acronyme, budget)", example = "nom")
            @RequestParam(defaultValue = "nom") String sortBy,

            @Parameter(description = "Direction du tri (asc, desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDirection) {

        logger.info("Requête GET /api/equipes - page: {}, size: {}, sortBy: {}, sortDirection: {}",
                page, size, sortBy, sortDirection);

        try {
            // Validation des paramètres
            if (page < 0) {
                logger.warn("Numéro de page invalide: {}", page);
                return ResponseEntity.badRequest().build();
            }

            if (size <= 0 || size > 100) {
                logger.warn("Taille de page invalide: {}", size);
                return ResponseEntity.badRequest().build();
            }

            Page<EquipeDto> equipes = equipeService.getAllEquipes(page, size, sortBy, sortDirection);
            logger.info("Retour de {} équipes sur la page {}", equipes.getContent().size(), page);

            return ResponseEntity.ok(equipes);

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des équipes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crée une nouvelle équipe avec ou sans joueurs.
     *
     * @param request les données de création de l'équipe
     * @return l'équipe créée
     */
    @PostMapping
    @Operation(
            summary = "Crée une nouvelle équipe",
            description = "Crée une nouvelle équipe avec ou sans joueurs associés"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Équipe créée avec succès",
                    content = @Content(schema = @Schema(implementation = EquipeDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de requête invalides"),
            @ApiResponse(responseCode = "409", description = "Conflit - équipe avec acronyme/nom existant"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> createEquipe(@Valid @RequestBody CreateEquipeRequest request) {
        logger.info("Requête POST /api/equipes - création d'équipe: {}", request);

        try {
            EquipeDto equipeCreee = equipeService.createEquipe(request);
            logger.info("Équipe créée avec succès - ID: {}, Nom: {}", equipeCreee.getId(), equipeCreee.getNom());

            return ResponseEntity.status(HttpStatus.CREATED).body(equipeCreee);

        } catch (IllegalArgumentException e) {
            logger.warn("Erreur de validation lors de la création d'équipe: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("error", "Erreur de validation", "message", e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'équipe", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Erreur interne", "message", "Une erreur inattendue s'est produite")
            );
        }
    }

    /**
     * Ajoute un joueur à une équipe.
     *
     * @param equipeId l'identifiant de l'équipe
     * @param joueurId l'identifiant du joueur
     * @return l'équipe mise à jour
     */
    @PostMapping("/{equipeId}/joueurs/{joueurId}")
    @Operation(
            summary = "Ajoute un joueur à une équipe",
            description = "Ajoute un joueur existant à une équipe existante"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Joueur ajouté avec succès à l'équipe",
                    content = @Content(schema = @Schema(implementation = EquipeDto.class))),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
            @ApiResponse(responseCode = "404", description = "Équipe ou joueur non trouvé"),
            @ApiResponse(responseCode = "409", description = "Le joueur est déjà dans une équipe"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> ajouterJoueur(
            @Parameter(description = "ID de l'équipe", example = "1")
            @PathVariable Long equipeId,

            @Parameter(description = "ID du joueur", example = "1")
            @PathVariable Long joueurId) {

        logger.info("Requête POST /api/equipes/{}/joueurs/{} - ajout de joueur", equipeId, joueurId);

        try {
            EquipeDto equipeMiseAJour = equipeService.ajouterJoueur(equipeId, joueurId);
            logger.info("Joueur {} ajouté avec succès à l'équipe {}", joueurId, equipeId);

            return ResponseEntity.ok(equipeMiseAJour);

        } catch (IllegalArgumentException e) {
            logger.warn("Erreur lors de l'ajout du joueur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", "Erreur de validation", "message", e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout du joueur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Erreur interne", "message", "Une erreur inattendue s'est produite")
            );
        }
    }

    /**
     * Retire un joueur d'une équipe.
     *
     * @param equipeId l'identifiant de l'équipe
     * @param joueurId l'identifiant du joueur
     * @return l'équipe mise à jour
     */
    @DeleteMapping("/{equipeId}/joueurs/{joueurId}")
    @Operation(
            summary = "Retire un joueur d'une équipe",
            description = "Retire un joueur d'une équipe existante"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Joueur retiré avec succès de l'équipe",
                    content = @Content(schema = @Schema(implementation = EquipeDto.class))),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
            @ApiResponse(responseCode = "404", description = "Équipe ou joueur non trouvé"),
            @ApiResponse(responseCode = "409", description = "Le joueur n'appartient pas à cette équipe"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> retirerJoueur(
            @Parameter(description = "ID de l'équipe", example = "1")
            @PathVariable Long equipeId,

            @Parameter(description = "ID du joueur", example = "1")
            @PathVariable Long joueurId) {

        logger.info("Requête DELETE /api/equipes/{}/joueurs/{} - retrait de joueur", equipeId, joueurId);

        try {
            EquipeDto equipeMiseAJour = equipeService.retirerJoueur(equipeId, joueurId);
            logger.info("Joueur {} retiré avec succès de l'équipe {}", joueurId, equipeId);

            return ResponseEntity.ok(equipeMiseAJour);

        } catch (IllegalArgumentException e) {
            logger.warn("Erreur lors du retrait du joueur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", "Erreur de validation", "message", e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Erreur lors du retrait du joueur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Erreur interne", "message", "Une erreur inattendue s'est produite")
            );
        }
    }
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupère une équipe par son ID",
            description = "Récupère les détails d'une équipe spécifique avec ses joueurs"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Équipe trouvée",
                    content = @Content(schema = @Schema(implementation = EquipeDto.class))),
            @ApiResponse(responseCode = "404", description = "Équipe non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<EquipeDto> getEquipeById(
            @Parameter(description = "ID de l'équipe", example = "1")
            @PathVariable Long id) {

        logger.info("Requête GET /api/equipes/{}", id);

        try {
            Optional<EquipeDto> equipe = equipeService.getEquipeById(id);

            if (equipe.isPresent()) {
                logger.info("Équipe trouvée: {}", equipe.get().getNom());
                return ResponseEntity.ok(equipe.get());
            } else {
                logger.warn("Équipe non trouvée avec l'ID: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'équipe avec l'ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère une équipe par son acronyme.
     *
     * @param acronyme l'acronyme de l'équipe
     * @return l'équipe ou 404 si non trouvée
     */
    @GetMapping("/acronyme/{acronyme}")
    @Operation(
            summary = "Récupère une équipe par son acronyme",
            description = "Récupère les détails d'une équipe par son acronyme"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Équipe trouvée",
                    content = @Content(schema = @Schema(implementation = EquipeDto.class))),
            @ApiResponse(responseCode = "404", description = "Équipe non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<EquipeDto> getEquipeByAcronyme(
            @Parameter(description = "Acronyme de l'équipe", example = "OGC")
            @PathVariable String acronyme) {

        logger.info("Requête GET /api/equipes/acronyme/{}", acronyme);

        try {
            Optional<EquipeDto> equipe = equipeService.getEquipeByAcronyme(acronyme);

            if (equipe.isPresent()) {
                logger.info("Équipe trouvée: {}", equipe.get().getNom());
                return ResponseEntity.ok(equipe.get());
            } else {
                logger.warn("Équipe non trouvée avec l'acronyme: {}", acronyme);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'équipe avec l'acronyme: {}", acronyme, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
