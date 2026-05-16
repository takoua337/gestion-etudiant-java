-- Run this in phpMyAdmin before launching the app

CREATE DATABASE IF NOT EXISTS projet_java CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE projet_java;

CREATE TABLE IF NOT EXISTS etudiants (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nom            VARCHAR(100) NOT NULL,
    prenom         VARCHAR(100) NOT NULL,
    filiere        VARCHAR(100) NOT NULL,
    email          VARCHAR(150) NOT NULL UNIQUE,
    date_naissance DATE,
    actif          TINYINT(1)   NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS cours (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    intitule    VARCHAR(200) NOT NULL,
    semestre    VARCHAR(20)  NOT NULL,
    capacite    INT          NOT NULL DEFAULT 30,
    nb_inscrits INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS notes (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    valeur      DOUBLE      NOT NULL,
    etudiant_id INT         NOT NULL,
    cours_id    INT         NOT NULL,
    date_saisie VARCHAR(20),
    FOREIGN KEY (etudiant_id) REFERENCES etudiants(id) ON DELETE CASCADE,
    FOREIGN KEY (cours_id)    REFERENCES cours(id)     ON DELETE CASCADE
);

