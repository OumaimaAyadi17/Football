package com.api.football.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant un joueur de football.
 *
 * Cette entité contient les informations principales d'un joueur :
 * - Identifiant unique
 * - Nom du joueur
 * - Position sur le terrain
 * - Référence vers l'équipe
 *
 * @author API Football API Team
 * @version 1.0.0
 */
@Entity
@Table(name = "joueurs")
public class Joueur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du joueur est obligatoire")
    @Size(max = 100, message = "Le nom du joueur ne peut pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "La position est obligatoire")
    @Size(max = 50, message = "La position ne peut pas dépasser 50 caractères")
    @Column(name = "position", nullable = false, length = 50)
    private String position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    /**
     * Constructeur par défaut requis par JPA.
     */
    public Joueur() {
    }

    /**
     * Constructeur avec paramètres pour créer un joueur.
     *
     * @param nom le nom du joueur
     * @param position la position du joueur
     */
    public Joueur(String nom, String position) {
        this.nom = nom;
        this.position = position;
    }

    /**
     * Constructeur avec paramètres pour créer un joueur avec équipe.
     *
     * @param nom le nom du joueur
     * @param position la position du joueur
     * @param equipe l'équipe du joueur
     */
    public Joueur(String nom, String position, Equipe equipe) {
        this.nom = nom;
        this.position = position;
        this.equipe = equipe;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    @Override
    public String toString() {
        return "Joueur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", position='" + position + '\'' +
                ", equipe=" + (equipe != null ? equipe.getNom() : "Aucune") +
                '}';
    }
}
