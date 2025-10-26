package com.api.football.repository;

import com.api.football.model.Joueur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des joueurs.
 *
 * Ce repository fournit les méthodes CRUD de base et des méthodes
 * personnalisées pour la recherche des joueurs.
 *
 * @author API Football API Team
 * @version 1.0.0
 */
@Repository
public interface JoueurRepository extends JpaRepository<Joueur, Long> {

    /**
     * Trouve un joueur par son nom.
     *
     * @param nom le nom du joueur
     * @return le joueur correspondant ou Optional.empty() si non trouvé
     */
    Optional<Joueur> findByNom(String nom);

    /**
     * Trouve tous les joueurs d'une équipe donnée.
     *
     * @param equipeId l'identifiant de l'équipe
     * @return la liste des joueurs de l'équipe
     */
    List<Joueur> findByEquipeId(Long equipeId);

    /**
     * Trouve tous les joueurs d'une équipe donnée avec pagination.
     *
     * @param equipeId l'identifiant de l'équipe
     * @param pageable les paramètres de pagination
     * @return une page de joueurs de l'équipe
     */
    Page<Joueur> findByEquipeId(Long equipeId, Pageable pageable);

    /**
     * Recherche des joueurs par nom contenant le terme donné.
     *
     * @param nom le terme de recherche
     * @param pageable les paramètres de pagination et tri
     * @return une page de joueurs correspondants
     */
    @Query("SELECT j FROM Joueur j WHERE LOWER(j.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    Page<Joueur> findByNomContainingIgnoreCase(@Param("nom") String nom, Pageable pageable);

    /**
     * Recherche des joueurs par position.
     *
     * @param position la position recherchée
     * @param pageable les paramètres de pagination et tri
     * @return une page de joueurs à cette position
     */
    @Query("SELECT j FROM Joueur j WHERE LOWER(j.position) LIKE LOWER(CONCAT('%', :position, '%'))")
    Page<Joueur> findByPositionContainingIgnoreCase(@Param("position") String position, Pageable pageable);

    /**
     * Compte le nombre de joueurs dans une équipe.
     *
     * @param equipeId l'identifiant de l'équipe
     * @return le nombre de joueurs dans l'équipe
     */
    long countByEquipeId(Long equipeId);

    /**
     * Vérifie si un joueur existe avec le nom donné.
     *
     * @param nom le nom à vérifier
     * @return true si le nom existe, false sinon
     */
    boolean existsByNom(String nom);

    /**
     * Recherche des joueurs par équipe et position.
     *
     * @param equipeId l'identifiant de l'équipe
     * @param position la position recherchée
     * @param pageable les paramètres de pagination et tri
     * @return une page de joueurs correspondants
     */
    @Query("SELECT j FROM Joueur j WHERE j.equipe.id = :equipeId AND LOWER(j.position) LIKE LOWER(CONCAT('%', :position, '%'))")
    Page<Joueur> findByEquipeIdAndPositionContainingIgnoreCase(@Param("equipeId") Long equipeId, @Param("position") String position, Pageable pageable);
}
