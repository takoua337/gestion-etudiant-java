# Gestion des Étudiants — Application Java Swing + MySQL

Application de bureau développée en Java (Swing) pour la gestion académique d'une école d'ingénieurs.
Elle permet de gérer les étudiants, les cours, les notes, les bulletins et d'afficher un tableau de bord statistique.

---

## Technologies utilisées

| Technologie | Rôle |
|---|---|
| Java 17+ | Langage principal |
| Java Swing | Interface graphique desktop |
| JDBC | Connexion à la base de données |
| MySQL (XAMPP) | Stockage des données |
| mysql-connector-j | Driver JDBC officiel MySQL |

---

## Architecture du projet

```
IdeaProjects/projet/
├── lib/                          ← Driver JDBC (mysql-connector-j-9.x.x.jar)
├── src/
│   ├── model/                    ← Entités métier (POJO)
│   │   ├── Etudiant.java
│   │   ├── Note.java
│   │   └── Cours.java
│   ├── dao/                      ← Accès base de données (pattern DAO)
│   │   ├── DatabaseConnection.java   (Singleton + reconnexion auto)
│   │   ├── IEtudiantDAO.java
│   │   ├── INoteDAO.java
│   │   ├── ICoursDAO.java
│   │   ├── EtudiantDAOImpl.java
│   │   ├── NoteDAOImpl.java
│   │   └── CoursDAOImpl.java
│   ├── service/                  ← Logique métier
│   │   ├── BulletinService.java
│   │   └── DashboardService.java
│   ├── util/                     ← Classes utilitaires
│   │   └── ComboItem.java            (wrapper JComboBox id+label)
│   └── ui/                       ← Interface graphique (Swing)
│       ├── MainGUI.java              (fenêtre principale + onglets)
│       ├── PanelEtudiant.java
│       ├── PanelNote.java
│       ├── PanelCours.java
│       ├── PanelBulletin.java
│       └── PanelDashboard.java
├── schema.sql                    ← Création de la base de données
├── fake_data.sql                 ← Données de démonstration (59 étudiants)
├── run.bat                       ← Script de compilation et d'exécution
└── README.md
```

**Pattern DAO** : chaque entité a une interface (`IXxxDAO`) et une implémentation (`XxxDAOImpl`).
La couche `service` orchestre les DAOs pour les calculs complexes (dashboard, bulletin).

---

## Prérequis

1. **Java JDK 17+** installé et `java`/`javac` dans le PATH.
2. **XAMPP** avec le module MySQL démarré.
3. **mysql-connector-j-9.x.x.jar** placé dans le dossier `lib/`.
   - Téléchargement : https://dev.mysql.com/downloads/connector/j/
   - Sélectionner "Platform Independent" → fichier `.zip` → extraire le `.jar` dans `lib/`.

---

## Installation et démarrage

### Étape 1 — Créer la base de données

Ouvrir phpMyAdmin (`http://localhost/phpmyadmin`) ou le client MySQL en ligne de commande, puis exécuter :

```sql
SOURCE chemin/vers/schema.sql;
```

Cela crée la base `projet_java` et les trois tables : `etudiants`, `cours`, `notes`.

### Étape 2 — Charger les données de démonstration

```sql
SOURCE chemin/vers/fake_data.sql;
```

59 étudiants, 15 cours et ~260 notes répartis sur les filières ING1 à ING5.

### Étape 3 — Lancer l'application

Depuis un terminal **dans le dossier `IdeaProjects/projet/`** :

```bat
run.bat
```

Le script compile automatiquement tous les fichiers sources et lance `ui.MainGUI`.

### Depuis IntelliJ IDEA

1. Ouvrir le projet à la racine `gestion-etudiant-java/`.
2. Aller dans **File → Project Structure → Modules → Dependencies**.
3. Ajouter le JAR `lib/mysql-connector-j-9.x.x.jar` comme dépendance.
4. Définir `ui.MainGUI` comme classe principale.
5. Cliquer sur **Run**.

---

## Fonctionnalités détaillées

### Onglet Étudiants

- **Ajouter** un étudiant avec validation des champs (nom, prénom, filière, email, date de naissance).
- **Modifier** les informations d'un étudiant sélectionné.
- **Supprimer** (suppression logique — le flag `actif` passe à 0, les données sont conservées).
- **Recherche en temps réel** par nom, prénom, filière ou email.
- **Tri** par colonne (clic sur l'en-tête).
- **Pagination** : affichage par pages de 20 étudiants.
- **Export CSV** : exporter la liste complète vers un fichier `.csv`.

### Onglet Cours

- **CRUD complet** : Ajouter, modifier, supprimer un cours.
- Champs : intitulé, semestre (S1–S7), capacité maximale.
- La colonne **Inscrits** affiche le nombre d'étudiants ayant au moins une note dans ce cours.
- Confirmation avant suppression (les notes associées seront également supprimées par contrainte FK).

### Onglet Notes

- **Filtres dynamiques** : filtrer par étudiant et/ou par cours via des listes déroulantes.
- **Bouton Réinitialiser** pour revenir à la liste complète.
- **Saisie d'une note** : sélectionner l'étudiant et le cours dans les menus déroulants, entrer la valeur (0–20).
- **Modifier** une note sélectionnée dans le tableau.
- **Supprimer** une note avec confirmation.
- Validation : la note doit être un nombre décimal entre 0 et 20.

### Onglet Bulletin

- Entrer un **ID étudiant** pour afficher son bulletin complet.
- Tableau coloré : vert ≥ 16, blanc ≥ 10, rouge < 10.
- **Moyenne calculée automatiquement** avec indication Admis/Échec (couleur verte/rouge).
- **Export PDF** via l'imprimante système (sélectionner "Microsoft Print to PDF").

### Onglet Tableau de bord

#### Indicateurs KPI (6 cartes colorées)
| Indicateur | Description |
|---|---|
| Étudiants actifs | Nombre total d'étudiants dans la base |
| Notes saisies | Nombre total de notes enregistrées |
| Moyenne générale | Moyenne de toutes les notes |
| Taux de réussite | % d'étudiants avec moyenne ≥ 10 |
| Note maximale | Meilleure note enregistrée |
| Note minimale | Note la plus basse enregistrée |

#### Classement de la promotion
Tableau des 15 meilleurs étudiants avec leur moyenne, filière et mention.
Lignes colorées selon la mention :
- Vert foncé : Très Bien (≥ 16)
- Bleu : Bien (≥ 14)
- Jaune : Assez Bien (≥ 12)
- Blanc : Passable (≥ 10)
- Rouge : Insuffisant (< 10)

#### Statistiques par filière
Nombre d'étudiants, moyenne, nombre d'admis et d'étudiants en échec par filière.

#### Statistiques par semestre
Nombre de notes et moyenne par semestre (S1 → S7).

#### Graphiques (3 types, Java2D natif — aucune bibliothèque externe)

| Onglet | Type | Données affichées |
|---|---|---|
| Barres | Histogramme | Moyenne par filière |
| Camembert | Diagramme circulaire | Répartition des étudiants par filière |
| Courbe | Graphique linéaire | Évolution de la moyenne par semestre |

Le bouton **Rafraîchir** recharge toutes les données via un thread séparé (`SwingWorker`) pour ne pas bloquer l'interface.

---

## Points techniques notables

### Connexion base de données (Singleton + reconnexion)
```java
// DatabaseConnection.java
if (instance == null || instance.isClosed() || !instance.isValid(2)) {
    instance = DriverManager.getConnection(URL, USER, PASSWORD);
}
```
La connexion est automatiquement rétablie si MySQL redémarre (XAMPP stop/start).

### Requête filtrée dynamique (Notes)
```java
// NoteDAOImpl.java — listerAvecDetails()
SELECT n.id, CONCAT(e.prenom,' ',e.nom), c.intitule, n.valeur, n.date_saisie
FROM notes n JOIN etudiants e ON n.etudiant_id=e.id JOIN cours c ON n.cours_id=c.id
WHERE (? IS NULL OR n.etudiant_id=?) AND (? IS NULL OR n.cours_id=?)
```

### Graphique Java2D (aucune dépendance externe)
Le `BarChartPanel` dans `PanelDashboard` dessine entièrement les barres, axes et étiquettes avec `Graphics2D`.

---

## Structure de la base de données

```sql
etudiants (id, nom, prenom, filiere, email, date_naissance, actif)
cours     (id, intitule, semestre, capacite, nb_inscrits)
notes     (id, valeur, etudiant_id, cours_id, date_saisie)
           └── FK → etudiants(id) ON DELETE CASCADE
           └── FK → cours(id) ON DELETE CASCADE
```

---

## Données de démonstration

Le fichier `fake_data.sql` inclut :

| Filière | Étudiants |
|---|---|
| ING1 | 5 |
| ING2 | 5 |
| ING3-A | 8 |
| ING3-B | 7 |
| ING3-C | 6 |
| ING4-GLSI-A | 7 |
| ING4-SSIR-A | 6 |
| ING4-SDIA-A | 5 |
| ING5-GLSI-A | 5 |
| ING5-SSIR-A | 5 |
| **Total** | **59** |

15 cours couvrant les semestres S1 à S7, et ~260 notes avec des distributions réalistes (quelques excellents, majorité passables, quelques échecs).

---

## Auteur

Projet réalisé dans le cadre du module **Développement Java** — TEK-UP Ingénierie, Année Académique 2024–2025.
