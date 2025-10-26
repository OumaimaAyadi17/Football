package com.api.football.service;

import com.api.football.dto.CreateEquipeRequest;
import com.api.football.dto.EquipeDto;
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
 * Service pour la gestion des équipes.
 *
 * Ce service fournit les opérations métier pour la gestion des équipes
 * incluant la création, la recherche avec pagination et tri.
 *
 * @author API Football API Team
 * @version 1.0.0
 */
@Service
@Transactional
public class EquipeService {

    private static final Logger logger = LoggerFactory.getLogger(EquipeService.class);

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private JoueurRepository joueurRepository;

    /**
     * Récupère toutes les équipes avec pagination et tri.
     *
     * @param page le numéro de page (commence à 0)
     * @param size la taille de la page
     * @param sortBy le champ de tri (nom, acronyme, budget)
     * @param sortDirection la direction du tri (asc, desc)
     * @return une page d'équipes
     */
    @Transactional(readOnly = true)
    public Page<EquipeDto> getAllEquipes(int page, int size, String sortBy, String sortDirection) {
        logger.info("Récupération des équipes - page: {}, size: {}, sortBy: {}, sortDirection: {}",
                page, size, sortBy, sortDirection);

        // Validation et normalisation des paramètres de tri
        String validSortBy = validateSortField(sortBy);
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, validSortBy));

        Page<Equipe> equipes = equipeRepository.findAllWithJoueurs(pageable);

        return equipes.map(this::convertToDto);
    }

    /**
     * Crée une nouvelle équipe avec ou sans joueurs.
     *
     * @param request les données de création de l'équipe
     * @return l'équipe créée
     * @throws IllegalArgumentException si l'acronyme existe déjà
     */
    public EquipeDto createEquipe(CreateEquipeRequest request) {
        logger.info("Création d'une nouvelle équipe: {}", request);

        // Vérification de l'unicité de l'acronyme
        if (equipeRepository.existsByAcronyme(request.getAcronyme())) {
            logger.warn("Tentative de création d'équipe avec acronyme existant: {}", request.getAcronyme());
            throw new IllegalArgumentException("Une équipe avec l'acronyme '" + request.getAcronyme() + "' existe déjà");
        }

        // Vérification de l'unicité du nom
        if (equipeRepository.existsByNom(request.getNom())) {
            logger.warn("Tentative de création d'équipe avec nom existant: {}", request.getNom());
            throw new IllegalArgumentException("Une équipe avec le nom '" + request.getNom() + "' existe déjà");
        }

        // Création de l'équipe
        Equipe equipe = new Equipe(request.getNom(), request.getAcronyme(), request.getBudget());
        equipe = equipeRepository.save(equipe);
        logger.info("Équipe créée avec l'ID: {}", equipe.getId());

        // Ajout des joueurs si fournis
        if (request.getJoueurs() != null && !request.getJoueurs().isEmpty()) {
            for (var joueurRequest : request.getJoueurs()) {
                Joueur joueur = new Joueur(joueurRequest.getNom(), joueurRequest.getPosition(), equipe);
                joueurRepository.save(joueur);
                logger.info("Joueur '{}' ajouté à l'équipe '{}'", joueur.getNom(), equipe.getNom());
            }
        }

        return convertToDto(equipe);
    }

    /**
     * Récupère une équipe par son ID.
     *
     * @param id l'identifiant de l'équipe
     * @return l'équipe ou Optional.empty() si non trouvée
     */
    @Transactional(readOnly = true)
    public Optional<EquipeDto> getEquipeById(Long id) {
        logger.info("Récupération de l'équipe avec l'ID: {}", id);
        return equipeRepository.findById(id).map(this::convertToDto);
    }

    /**
     * Récupère une équipe par son acronyme.
     *
     * @param acronyme l'acronyme de l'équipe
     * @return l'équipe ou Optional.empty() si non trouvée
     */
    @Transactional(readOnly = true)
    public Optional<EquipeDto> getEquipeByAcronyme(String acronyme) {
        logger.info("Récupération de l'équipe avec l'acronyme: {}", acronyme);
        return equipeRepository.findByAcronyme(acronyme).map(this::convertToDto);
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
            case "acronyme":
            case "acronym":
                return "acronyme";
            case "budget":
                return "budget";
            default:
                logger.warn("Champ de tri invalide '{}', utilisation de 'nom' par défaut", sortBy);
                return "nom";
        }
    }

    /**
     * Convertit une entité Equipe en DTO.
     *
     * @param equipe l'entité à convertir
     * @return le DTO correspondant
     */
    private EquipeDto convertToDto(Equipe equipe) {
        List<JoueurDto> joueursDto = equipe.getJoueurs().stream()
                .map(joueur -> new JoueurDto(
                        joueur.getId(),
                        joueur.getNom(),
                        joueur.getPosition(),
                        joueur.getEquipe() != null ? joueur.getEquipe().getId() : null,
                        joueur.getEquipe() != null ? joueur.getEquipe().getNom() : null
                ))
                .collect(Collectors.toList());

        return new EquipeDto(
                equipe.getId(),
                equipe.getNom(),
                equipe.getAcronyme(),
                equipe.getBudget(),
                joueursDto
        );
    }

    /**
     * Ajoute un joueur à une équipe.
     *
     * @param equipeId l'identifiant de l'équipe
     * @param joueurId l'identifiant du joueur
     * @return l'équipe mise à jour
     * @throws IllegalArgumentException si l'équipe ou le joueur n'existe pas
     */
    @Transactional
    public EquipeDto ajouterJoueur(Long equipeId, Long joueurId) {
        logger.info("Ajout du joueur {} à l'équipe {}", joueurId, equipeId);

        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new IllegalArgumentException("Équipe non trouvée avec l'ID: " + equipeId));

        Joueur joueur = joueurRepository.findById(joueurId)
                .orElseThrow(() -> new IllegalArgumentException("Joueur non trouvé avec l'ID: " + joueurId));

        // Vérifier si le joueur n'est pas déjà dans une équipe
        if (joueur.getEquipe() != null) {
            throw new IllegalArgumentException("Le joueur est déjà dans une équipe");
        }

        // Ajouter le joueur à l'équipe
        equipe.getJoueurs().add(joueur);
        joueur.setEquipe(equipe);

        // Sauvegarder les modifications
        Equipe savedEquipe = equipeRepository.save(equipe);
        joueurRepository.save(joueur);

        logger.info("Joueur {} ajouté avec succès à l'équipe {}", joueurId, equipeId);
        return convertToDto(savedEquipe);
    }

    /**
     * Retire un joueur d'une équipe.
     *
     * @param equipeId l'identifiant de l'équipe
     * @param joueurId l'identifiant du joueur
     * @return l'équipe mise à jour
     * @throws IllegalArgumentException si l'équipe ou le joueur n'existe pas
     */
    @Transactional
    public EquipeDto retirerJoueur(Long equipeId, Long joueurId) {
        logger.info("Retrait du joueur {} de l'équipe {}", joueurId, equipeId);

        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new IllegalArgumentException("Équipe non trouvée avec l'ID: " + equipeId));

        Joueur joueur = joueurRepository.findById(joueurId)
                .orElseThrow(() -> new IllegalArgumentException("Joueur non trouvé avec l'ID: " + joueurId));

        // Vérifier si le joueur appartient bien à cette équipe
        if (!equipe.getJoueurs().contains(joueur)) {
            throw new IllegalArgumentException("Le joueur n'appartient pas à cette équipe");
        }

        // Retirer le joueur de l'équipe
        equipe.getJoueurs().remove(joueur);
        joueur.setEquipe(null);

        // Sauvegarder les modifications
        Equipe savedEquipe = equipeRepository.save(equipe);
        joueurRepository.save(joueur);

        logger.info("Joueur {} retiré avec succès de l'équipe {}", joueurId, equipeId);
        return convertToDto(savedEquipe);
    }
}
