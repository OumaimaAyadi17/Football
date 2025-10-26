package com.api.football.repository;

import com.api.football.model.Equipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour la gestion des équipes.
 *
 * Ce repository fournit les méthodes CRUD de base et des méthodes
 * personnalisées pour la recherche et la pagination des équipes.
 *
 * @author API Football API Team
 * @version 1.0.0
 */
@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {

    /**
     * Trouve une équipe par son acronyme.
     *
     * @param acronyme l'acronyme de l'équipe
     * @return l'équipe correspondante ou Optional.empty() si non trouvée
     */
    Optional<Equipe> findByAcronyme(String acronyme);

    /**
     * Trouve une équipe par son nom.
     *
     * @param nom le nom de l'équipe
     * @return l'équipe correspondante ou Optional.empty() si non trouvée
     */
    Optional<Equipe> findByNom(String nom);

    /**
     * Vérifie si une équipe existe avec l'acronyme donné.
     *
     * @param acronyme l'acronyme à vérifier
     * @return true si l'acronyme existe, false sinon
     */
    boolean existsByAcronyme(String acronyme);

    /**
     * Vérifie si une équipe existe avec le nom donné.
     *
     * @param nom le nom à vérifier
     * @return true si le nom existe, false sinon
     */
    boolean existsByNom(String nom);

    /**
     * Recherche des équipes avec pagination et tri.
     *
     * @param pageable les paramètres de pagination et tri
     * @return une page d'équipes
     */
    @Query("SELECT e FROM Equipe e LEFT JOIN FETCH e.joueurs")
    Page<Equipe> findAllWithJoueurs(Pageable pageable);

    /**
     * Recherche des équipes par nom contenant le terme donné.
     *
     * @param nom le terme de recherche
     * @param pageable les paramètres de pagination et tri
     * @return une page d'équipes correspondantes
     */
    @Query("SELECT e FROM Equipe e LEFT JOIN FETCH e.joueurs WHERE LOWER(e.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    Page<Equipe> findByNomContainingIgnoreCase(@Param("nom") String nom, Pageable pageable);

    /**
     * Recherche des équipes par acronyme contenant le terme donné.
     *
     * @param acronyme le terme de recherche
     * @param pageable les paramètres de pagination et tri
     * @return une page d'équipes correspondantes
     */
    @Query("SELECT e FROM Equipe e LEFT JOIN FETCH e.joueurs WHERE LOWER(e.acronyme) LIKE LOWER(CONCAT('%', :acronyme, '%'))")
    Page<Equipe> findByAcronymeContainingIgnoreCase(@Param("acronyme") String acronyme, Pageable pageable);
}
