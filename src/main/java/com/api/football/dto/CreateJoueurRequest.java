package com.api.football.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la création d'un joueur.
 *
 * @author API Football API Team
 * @version 1.0.0
 */
public class CreateJoueurRequest {

    @NotBlank(message = "Le nom du joueur est obligatoire")
    @Size(max = 100, message = "Le nom du joueur ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "La position est obligatoire")
    @Size(max = 50, message = "La position ne peut pas dépasser 50 caractères")
    private String position;

    private Long equipeId;

    /**
     * Constructeur par défaut.
     */
    public CreateJoueurRequest() {
    }

    /**
     * Constructeur avec paramètres.
     *
     * @param nom le nom du joueur
     * @param position la position du joueur
     */
    public CreateJoueurRequest(String nom, String position) {
        this.nom = nom;
        this.position = position;
    }

    // Getters et Setters

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

    public Long getEquipeId() {
        return equipeId;
    }

    public void setEquipeId(Long equipeId) {
        this.equipeId = equipeId;
    }

    @Override
    public String toString() {
        return "CreateJoueurRequest{" +
                "nom='" + nom + '\'' +
                ", position='" + position + '\'' +
                ", equipeId=" + equipeId +
                '}';
    }
}

