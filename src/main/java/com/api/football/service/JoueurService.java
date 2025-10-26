package com.api.football.service;

import com.api.football.dto.CreateJoueurRequest;
import com.api.football.dto.JoueurDto;
import com.api.football.model.Equipe;
import com.api.football.model.Joueur;
import com.api.football.repository.EquipeRepository;
import com.api.football.repository.JoueurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des joueurs.
 *
 * Ce service fournit les opérations métier pour la gestion des joueurs
 * incluant la création, la recherche avec pagination et tri, les transferts.
 *
 * @author API Football API Team
 * @version 1.0.0
 */
@Service
@Transactional
public class JoueurService {

    private static final Logger logger = LoggerFactory.getLogger(JoueurService.class);

    @Autowired
    private JoueurRepository joueurRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    /**
     * Récupère tous les joueurs avec pagination et tri.
     *
     * @param page le numéro de page (commence à 0)
     * @param size la taille de la page
     * @param sortBy le champ de tri (nom, position)
     * @param sortDirection la direction du tri (asc, desc)
     * @param equipeId filtre par équipe (optionnel)
     * @param position filtre par position (optionnel)
     * @return une page de joueurs
     */
    @Transactional(readOnly = true)
    public Page<JoueurDto> getAllJoueurs(int page, int size, String sortBy, String sortDirection,
                                         Long equipeId, String position) {
        logger.info("Récupération des joueurs - page: {}, size: {}, sortBy: {}, sortDirection: {}, equipeId: {}, position: {}",
                page, size, sortBy, sortDirection, equipeId, position);

        // Validation et normalisation des paramètres de tri
        String validSortBy = validateSortField(sortBy);
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, validSortBy));

        Page<Joueur> joueurs;

        // Application des filtres
        if (equipeId != null && position != null) {
            joueurs = joueurRepository.findByEquipeIdAndPositionContainingIgnoreCase(equipeId, position, pageable);
        } else if (equipeId != null) {
            joueurs = joueurRepository.findByEquipeId(equipeId, pageable);
        } else if (position != null) {
            joueurs = joueurRepository.findByPositionContainingIgnoreCase(position, pageable);
        } else {
            joueurs = joueurRepository.findAll(pageable);
        }

        return joueurs.map(this::convertToDto);
    }

    /**
     * Crée un nouveau joueur.
     *
     * @param request les données de création du joueur
     * @return le joueur créé
     * @throws IllegalArgumentException si l'équipe n'existe pas ou si le nom existe déjà
     */
    public JoueurDto createJoueur(CreateJoueurRequest request) {
        logger.info("Création d'un nouveau joueur: {}", request);

        // Vérification de l'unicité du nom
        if (joueurRepository.existsByNom(request.getNom())) {
            logger.warn("Tentative de création de joueur avec nom existant: {}", request.getNom());
            throw new IllegalArgumentException("Un joueur avec le nom '" + request.getNom() + "' existe déjà");
        }

        // Vérification de l'existence de l'équipe si fournie
        Equipe equipe = null;
        if (request.getEquipeId() != null) {
            equipe = equipeRepository.findById(request.getEquipeId())
                    .orElseThrow(() -> new IllegalArgumentException("Équipe avec l'ID " + request.getEquipeId() + " non trouvée"));
        }

        // Création du joueur
        Joueur joueur = new Joueur(request.getNom(), request.getPosition(), equipe);
        joueur = joueurRepository.save(joueur);
        logger.info("Joueur créé avec l'ID: {}", joueur.getId());

        return convertToDto(joueur);
    }

    /**
     * Récupère un joueur par son ID.
     *
     * @param id l'identifiant du joueur
     * @return le joueur ou Optional.empty() si non trouvé
     */
    @Transactional(readOnly = true)
    public Optional<JoueurDto> getJoueurById(Long id) {
        logger.info("Récupération du joueur avec l'ID: {}", id);
        return joueurRepository.findById(id).map(this::convertToDto);
    }

    /**
     * Transfère un joueur vers une autre équipe.
     *
     * @param joueurId l'identifiant du joueur
     * @param equipeId l'identifiant de la nouvelle équipe
     * @return le joueur transféré
     * @throws IllegalArgumentException si le joueur ou l'équipe n'existe pas
     */
    public JoueurDto transferJoueur(Long joueurId, Long equipeId) {
        logger.info("Transfert du joueur {} vers l'équipe {}", joueurId, equipeId);

        // Vérification de l'existence du joueur
        Joueur joueur = joueurRepository.findById(joueurId)
                .orElseThrow(() -> new IllegalArgumentException("Joueur avec l'ID " + joueurId + " non trouvé"));

        // Vérification de l'existence de l'équipe
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new IllegalArgumentException("Équipe avec l'ID " + equipeId + " non trouvée"));

        // Transfert du joueur
        joueur.setEquipe(equipe);
        joueur = joueurRepository.save(joueur);

        logger.info("Joueur {} transféré vers l'équipe {}", joueur.getNom(), equipe.getNom());

        return convertToDto(joueur);
    }

    /**
     * Supprime un joueur.
     *
     * @param id l'identifiant du joueur
     * @return true si le joueur a été supprimé, false s'il n'existe pas
     */
    public boolean deleteJoueur(Long id) {
        logger.info("Suppression du joueur avec l'ID: {}", id);

        if (joueurRepository.existsById(id)) {
            joueurRepository.deleteById(id);
            logger.info("Joueur avec l'ID {} supprimé avec succès", id);
            return true;
        } else {
            logger.warn("Joueur avec l'ID {} non trouvé", id);
            return false;
        }
    }

    /**
     * Récupère tous les joueurs d'une équipe.
     *
     * @param equipeId l'identifiant de l'équipe
     * @return la liste des joueurs de l'équipe
     */
    @Transactional(readOnly = true)
    public List<JoueurDto> getJoueursByEquipe(Long equipeId) {
        logger.info("Récupération des joueurs de l'équipe {}", equipeId);

        return joueurRepository.findByEquipeId(equipeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Valide et normalise le champ de tri.
     *
     * @param sortBy le champ de tri fourni
     * @return le champ de tri validé
     */
    private String validateSortField(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "nom";
        }

        String normalizedSortBy = sortBy.toLowerCase().trim();
        switch (normalizedSortBy) {
            case "nom":
            case "name":
                return "nom";
            case "position":
                return "position";
            default:
                logger.warn("Champ de tri invalide '{}', utilisation de 'nom' par défaut", sortBy);
                return "nom";
        }
    }

    /**
     * Convertit une entité Joueur en DTO.
     *
     * @param joueur l'entité à convertir
     * @return le DTO correspondant
     */
    private JoueurDto convertToDto(Joueur joueur) {
        return new JoueurDto(
                joueur.getId(),
                joueur.getNom(),
                joueur.getPosition(),
                joueur.getEquipe() != null ? joueur.getEquipe().getId() : null,
                joueur.getEquipe() != null ? joueur.getEquipe().getNom() : null
        );
    }
}
