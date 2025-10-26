package com.api.football.controller;

import com.api.football.dto.CreateJoueurRequest;
import com.api.football.dto.JoueurDto;
import com.api.football.service.JoueurService;
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
 * Contrôleur REST pour la gestion des joueurs.
 *
 * Ce contrôleur expose les endpoints pour :
 * - Récupérer la liste des joueurs avec pagination et tri
 * - Créer un nouveau joueur
 * - Récupérer un joueur par ID
 * - Mettre à jour un joueur
 * - Supprimer un joueur
 * - Transférer un joueur entre équipes
 *
 * @author API Football API Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/joueurs")
@Tag(name = "Joueurs", description = "API pour la gestion des joueurs de football")
public class JoueurController {

    private static final Logger logger = LoggerFactory.getLogger(JoueurController.class);

    @Autowired
    private JoueurService joueurService;

    /**
     * Récupère la liste des joueurs avec pagination et tri.
     *
     * @param page le numéro de page (commence à 0, défaut: 0)
     * @param size la taille de la page (défaut: 10)
     * @param sortBy le champ de tri (nom, position, défaut: nom)
     * @param sortDirection la direction du tri (asc, desc, défaut: asc)
     * @param equipeId filtre par équipe (optionnel)
     * @param position filtre par position (optionnel)
     * @return une page de joueurs
     */
    @GetMapping
    @Operation(
            summary = "Récupère la liste des joueurs",
            description = "Récupère la liste paginée des joueurs avec possibilité de tri et filtrage"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des joueurs récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Paramètres de requête invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Page<JoueurDto>> getAllJoueurs(
            @Parameter(description = "Numéro de page (commence à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Champ de tri (nom, position)", example = "nom")
            @RequestParam(defaultValue = "nom") String sortBy,

            @Parameter(description = "Direction du tri (asc, desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDirection,

            @Parameter(description = "Filtrer par ID d'équipe", example = "1")
            @RequestParam(required = false) Long equipeId,

            @Parameter(description = "Filtrer par position", example = "Gardien")
            @RequestParam(required = false) String position) {

        logger.info("Requête GET /api/joueurs - page: {}, size: {}, sortBy: {}, sortDirection: {}, equipeId: {}, position: {}",
                page, size, sortBy, sortDirection, equipeId, position);

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

            Page<JoueurDto> joueurs = joueurService.getAllJoueurs(page, size, sortBy, sortDirection, equipeId, position);
            logger.info("Retour de {} joueurs sur la page {}", joueurs.getContent().size(), page);

            return ResponseEntity.ok(joueurs);

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des joueurs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crée un nouveau joueur.
     *
     * @param request les données de création du joueur
     * @return le joueur créé
     */
    @PostMapping
    @Operation(
            summary = "Crée un nouveau joueur",
            description = "Crée un nouveau joueur et l'assigne à une équipe"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Joueur créé avec succès",
                    content = @Content(schema = @Schema(implementation = JoueurDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de requête invalides"),
            @ApiResponse(responseCode = "404", description = "Équipe non trouvée"),
            @ApiResponse(responseCode = "409", description = "Conflit - joueur avec nom existant"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> createJoueur(@Valid @RequestBody CreateJoueurRequest request) {
        logger.info("Requête POST /api/joueurs - création de joueur: {}", request);

        try {
            JoueurDto joueurCree = joueurService.createJoueur(request);
            logger.info("Joueur créé avec succès - ID: {}, Nom: {}", joueurCree.getId(), joueurCree.getNom());

            return ResponseEntity.status(HttpStatus.CREATED).body(joueurCree);

        } catch (IllegalArgumentException e) {
            logger.warn("Erreur de validation lors de la création du joueur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("error", "Erreur de validation", "message", e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Erreur lors de la création du joueur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Erreur interne", "message", "Une erreur inattendue s'est produite")
            );
        }
    }

    /**
     * Récupère un joueur par son ID.
     *
     * @param id l'identifiant du joueur
     * @return le joueur ou 404 si non trouvé
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupère un joueur par son ID",
            description = "Récupère les détails d'un joueur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Joueur trouvé",
                    content = @Content(schema = @Schema(implementation = JoueurDto.class))),
            @ApiResponse(responseCode = "404", description = "Joueur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<JoueurDto> getJoueurById(
            @Parameter(description = "ID du joueur", example = "1")
            @PathVariable Long id) {

        logger.info("Requête GET /api/joueurs/{}", id);

        try {
            Optional<JoueurDto> joueur = joueurService.getJoueurById(id);

            if (joueur.isPresent()) {
                logger.info("Joueur trouvé: {}", joueur.get().getNom());
                return ResponseEntity.ok(joueur.get());
            } else {
                logger.warn("Joueur non trouvé avec l'ID: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du joueur avec l'ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Transfère un joueur vers une autre équipe.
     *
     * @param id l'identifiant du joueur
     * @param equipeId l'identifiant de la nouvelle équipe
     * @return le joueur transféré
     */
    @PutMapping("/{id}/transfer")
    @Operation(
            summary = "Transfère un joueur vers une autre équipe",
            description = "Transfère un joueur d'une équipe vers une autre"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Joueur transféré avec succès",
                    content = @Content(schema = @Schema(implementation = JoueurDto.class))),
            @ApiResponse(responseCode = "404", description = "Joueur ou équipe non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> transferJoueur(
            @Parameter(description = "ID du joueur", example = "1")
            @PathVariable Long id,

            @Parameter(description = "ID de la nouvelle équipe", example = "2")
            @RequestParam Long equipeId) {

        logger.info("Requête PUT /api/joueurs/{}/transfer vers équipe {}", id, equipeId);

        try {
            JoueurDto joueur = joueurService.transferJoueur(id, equipeId);
            logger.info("Joueur {} transféré vers l'équipe {}", joueur.getNom(), joueur.getEquipeNom());

            return ResponseEntity.ok(joueur);

        } catch (IllegalArgumentException e) {
            logger.warn("Erreur lors du transfert du joueur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", "Erreur de transfert", "message", e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Erreur lors du transfert du joueur avec l'ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Erreur interne", "message", "Une erreur inattendue s'est produite")
            );
        }
    }

    /**
     * Supprime un joueur.
     *
     * @param id l'identifiant du joueur
     * @return 204 No Content si succès
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprime un joueur",
            description = "Supprime un joueur de la base de données"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Joueur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Joueur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Void> deleteJoueur(
            @Parameter(description = "ID du joueur", example = "1")
            @PathVariable Long id) {

        logger.info("Requête DELETE /api/joueurs/{}", id);

        try {
            boolean deleted = joueurService.deleteJoueur(id);

            if (deleted) {
                logger.info("Joueur avec l'ID {} supprimé avec succès", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Joueur non trouvé avec l'ID: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du joueur avec l'ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
