package com.api.football.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une équipe de football.
 *
 * Cette entité contient les informations principales d'une équipe :
 * - Identifiant unique
 * - Nom de l'équipe
 * - Acronyme
 * - Budget de l'équipe
 * - Liste des joueurs
 */
@Entity
@Table(name = "equipes")
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de l'équipe est obligatoire")
    @Size(max = 100, message = "Le nom de l'équipe ne peut pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "L'acronyme est obligatoire")
    @Size(max = 10, message = "L'acronyme ne peut pas dépasser 10 caractères")
    @Column(name = "acronyme", nullable = false, length = 10, unique = true)
    private String acronyme;

    @NotNull(message = "Le budget est obligatoire")
    @PositiveOrZero(message = "Le budget doit être positif ou nul")
    @Column(name = "budget", nullable = false, precision = 15, scale = 2)
    private BigDecimal budget;

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Joueur> joueurs = new ArrayList<>();

    /**
     * Constructeur par défaut requis par JPA.
     */
    public Equipe() {
    }

    /**
     * Constructeur avec paramètres pour créer une équipe.
     *
     * @param nom le nom de l'équipe
     * @param acronyme l'acronyme de l'équipe
     * @param budget le budget de l'équipe
     */
    public Equipe(String nom, String acronyme, BigDecimal budget) {
        this.nom = nom;
        this.acronyme = acronyme;
        this.budget = budget;
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAcronyme() {
        return acronyme;
    }

    public void setAcronyme(String acronyme) {
        this.acronyme = acronyme;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    public void setJoueurs(List<Joueur> joueurs) {
        this.joueurs = joueurs;
    }


    @Override
    public String toString() {
        return "Equipe{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", acronyme='" + acronyme + '\'' +
                ", budget=" + budget +
                ", nombreJoueurs=" + (joueurs != null ? joueurs.size() : 0) +
                '}';
    }
}
