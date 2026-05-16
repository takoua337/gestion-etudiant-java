-- =============================================================
--  fake_data.sql  –  Données de démonstration
--  Base : projet_java    (exécuter après schema.sql)
-- =============================================================

USE projet_java;

-- Vider les tables dans le bon ordre (contraintes FK)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE notes;
TRUNCATE TABLE cours;
TRUNCATE TABLE etudiants;
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================
--  COURS  (15 matières sur S1 → S7)  — IDs fixes
-- =============================================================
INSERT INTO cours (id, intitule, semestre, capacite, nb_inscrits) VALUES
(1,  'Algorithmique et Structures de Données', 'S1', 60, 0),
(2,  'Programmation Orientée Objet (Java)',    'S2', 60, 0),
(3,  'Bases de Données Relationnelles',        'S2', 55, 0),
(4,  'Systèmes d''Exploitation',               'S3', 50, 0),
(5,  'Réseaux Informatiques',                  'S3', 50, 0),
(6,  'Développement Web Full-Stack',           'S4', 45, 0),
(7,  'Intelligence Artificielle',              'S4', 40, 0),
(8,  'Génie Logiciel',                         'S5', 40, 0),
(9,  'Sécurité des Systèmes d''Information',   'S5', 35, 0),
(10, 'Machine Learning et Big Data',           'S6', 35, 0),
(11, 'Cloud Computing et DevOps',              'S6', 30, 0),
(12, 'Cryptographie Appliquée',               'S6', 30, 0),
(13, 'Projet de Fin d''Études (PFE)',          'S7', 25, 0),
(14, 'Architecture des Microservices',         'S7', 25, 0),
(15, 'Mathématiques pour l''IA',              'S5', 40, 0);

-- =============================================================
--  ÉTUDIANTS
--  Filières : ING1, ING2, ING3-A, ING3-B, ING3-C,
--             ING4-GLSI-A, ING4-SSIR-A, ING4-SDIA-A,
--             ING5-GLSI-A, ING5-SSIR-A
-- =============================================================
INSERT INTO etudiants (nom, prenom, filiere, email, date_naissance, actif) VALUES
-- ING1 (5 étudiants)
('Trabelsi',   'Aymen',    'ING1', 'aymen.trabelsi@tek-up.tn',   '2006-03-12', 1),
('Chouchane',  'Rania',    'ING1', 'rania.chouchane@tek-up.tn',  '2006-07-24', 1),
('Mbarek',     'Hamza',    'ING1', 'hamza.mbarek@tek-up.tn',     '2006-01-05', 1),
('Zarrouk',    'Salma',    'ING1', 'salma.zarrouk@tek-up.tn',    '2006-09-18', 1),
('Khelifi',    'Seifeddine','ING1','seif.khelifi@tek-up.tn',     '2005-11-30', 1),

-- ING2 (5 étudiants)
('Belhaj',     'Mariem',   'ING2', 'mariem.belhaj@tek-up.tn',    '2005-04-02', 1),
('Gharbi',     'Oussama',  'ING2', 'oussama.gharbi@tek-up.tn',   '2005-08-15', 1),
('Jebali',     'Nour',     'ING2', 'nour.jebali@tek-up.tn',      '2005-02-27', 1),
('Souissi',    'Malek',    'ING2', 'malek.souissi@tek-up.tn',    '2004-12-10', 1),
('Ferchichi',  'Ines',     'ING2', 'ines.ferchichi@tek-up.tn',   '2005-06-22', 1),

-- ING3-A (8 étudiants)
('Ben Ali',    'Mohamed',  'ING3-A','med.benali@tek-up.tn',      '2004-05-11', 1),
('Karoui',     'Amira',    'ING3-A','amira.karoui@tek-up.tn',    '2004-09-03', 1),
('Dridi',      'Yassine',  'ING3-A','yassine.dridi@tek-up.tn',   '2003-11-17', 1),
('Messaoudi',  'Fatma',    'ING3-A','fatma.messaoudi@tek-up.tn', '2004-02-08', 1),
('Haddad',     'Samy',     'ING3-A','samy.haddad@tek-up.tn',     '2003-07-29', 1),
('Oueslati',   'Chaima',   'ING3-A','chaima.oueslati@tek-up.tn', '2004-04-14', 1),
('Rekik',      'Adam',     'ING3-A','adam.rekik@tek-up.tn',      '2003-10-06', 1),
('Meddeb',     'Lina',     'ING3-A','lina.meddeb@tek-up.tn',     '2004-01-25', 1),

-- ING3-B (7 étudiants)
('Ayari',      'Khalil',   'ING3-B','khalil.ayari@tek-up.tn',    '2004-06-19', 1),
('Hamdi',      'Yasmine',  'ING3-B','yasmine.hamdi@tek-up.tn',   '2003-08-30', 1),
('Zouari',     'Rami',     'ING3-B','rami.zouari@tek-up.tn',     '2004-03-07', 1),
('Nasri',      'Donia',    'ING3-B','donia.nasri@tek-up.tn',     '2004-11-21', 1),
('Boukthir',   'Achraf',   'ING3-B','achraf.boukthir@tek-up.tn', '2003-05-04', 1),
('Tounsi',     'Sara',     'ING3-B','sara.tounsi@tek-up.tn',     '2004-07-16', 1),
('Chaabane',   'Bilel',    'ING3-B','bilel.chaabane@tek-up.tn',  '2003-12-09', 1),

-- ING3-C (6 étudiants)
('Elloumi',    'Ghassen',  'ING3-C','ghassen.elloumi@tek-up.tn', '2004-02-28', 1),
('Boughanmi',  'Maroua',   'ING3-C','maroua.b@tek-up.tn',        '2003-09-12', 1),
('Sfar',       'Aziz',     'ING3-C','aziz.sfar@tek-up.tn',       '2004-08-01', 1),
('Triki',      'Hana',     'ING3-C','hana.triki@tek-up.tn',      '2003-04-23', 1),
('Lahmar',     'Fares',    'ING3-C','fares.lahmar@tek-up.tn',    '2004-06-05', 1),
('Bennasr',    'Olfa',     'ING3-C','olfa.bennasr@tek-up.tn',    '2003-10-17', 1),

-- ING4-GLSI-A (7 étudiants)
('Abdallah',   'Nadia',    'ING4-GLSI-A','nadia.abdallah@tek-up.tn','2003-03-14', 1),
('Belhadj',    'Tarek',    'ING4-GLSI-A','tarek.belhadj@tek-up.tn', '2002-11-26', 1),
('Cherni',     'Imen',     'ING4-GLSI-A','imen.cherni@tek-up.tn',   '2003-07-08', 1),
('Dhouib',     'Slim',     'ING4-GLSI-A','slim.dhouib@tek-up.tn',   '2002-09-20', 1),
('Elouni',     'Rahma',    'ING4-GLSI-A','rahma.elouni@tek-up.tn',  '2003-01-02', 1),
('Fourati',    'Mehdi',    'ING4-GLSI-A','mehdi.fourati@tek-up.tn', '2002-12-15', 1),
('Guedri',     'Nesrine',  'ING4-GLSI-A','nesrine.guedri@tek-up.tn','2003-05-27', 1),

-- ING4-SSIR-A (6 étudiants)
('Hamrouni',   'Skander',  'ING4-SSIR-A','skander.hamrouni@tek-up.tn','2002-08-09', 1),
('Jarray',     'Meriem',   'ING4-SSIR-A','meriem.jarray@tek-up.tn',  '2003-02-21', 1),
('Khemiri',    'Amine',    'ING4-SSIR-A','amine.khemiri@tek-up.tn',  '2002-10-03', 1),
('Loukil',     'Sabrine',  'ING4-SSIR-A','sabrine.loukil@tek-up.tn', '2002-06-15', 1),
('Mabrouk',    'Zied',     'ING4-SSIR-A','zied.mabrouk@tek-up.tn',   '2003-04-07', 1),
('Nasr',       'Jihene',   'ING4-SSIR-A','jihene.nasr@tek-up.tn',    '2002-11-19', 1),

-- ING4-SDIA-A (5 étudiants)
('Omri',       'Hazem',    'ING4-SDIA-A','hazem.omri@tek-up.tn',     '2002-07-01', 1),
('Poulain',    'Arij',     'ING4-SDIA-A','arij.poulain@tek-up.tn',   '2003-03-13', 1),
('Quaili',     'Bassem',   'ING4-SDIA-A','bassem.quaili@tek-up.tn',  '2002-09-25', 1),
('Rajhi',      'Sana',     'ING4-SDIA-A','sana.rajhi@tek-up.tn',     '2002-12-07', 1),
('Saad',       'Ilyes',    'ING4-SDIA-A','ilyes.saad@tek-up.tn',     '2003-01-19', 1),

-- ING5-GLSI-A (5 étudiants)
('Tabboubi',   'Walid',    'ING5-GLSI-A','walid.tabboubi@tek-up.tn', '2001-06-11', 1),
('Umairi',     'Chiraz',   'ING5-GLSI-A','chiraz.umairi@tek-up.tn',  '2002-02-23', 1),
('Vali',       'Houssem',  'ING5-GLSI-A','houssem.vali@tek-up.tn',   '2001-10-05', 1),
('Wali',       'Asma',     'ING5-GLSI-A','asma.wali@tek-up.tn',      '2001-08-17', 1),
('Yahiaoui',   'Fedi',     'ING5-GLSI-A','fedi.yahiaoui@tek-up.tn',  '2002-04-29', 1),

-- ING5-SSIR-A (5 étudiants)
('Zayani',     'Mouna',    'ING5-SSIR-A','mouna.zayani@tek-up.tn',   '2001-07-13', 1),
('Ammar',      'Wassim',   'ING5-SSIR-A','wassim.ammar@tek-up.tn',   '2002-01-25', 1),
('Baccouche',  'Rim',      'ING5-SSIR-A','rim.baccouche@tek-up.tn',  '2001-11-07', 1),
('Chebbi',     'Nabil',    'ING5-SSIR-A','nabil.chebbi@tek-up.tn',   '2002-05-19', 1),
('Dhaoui',     'Hajer',    'ING5-SSIR-A','hajer.dhaoui@tek-up.tn',   '2001-09-01', 1);

-- =============================================================
--  NOTES  – distributions réalistes par filière/niveau
--  Cours utilisés par semestre (IDs approximatifs 1-15)
-- =============================================================

-- Helper view for student IDs (not stored, just to clarify mapping)
-- ING1 : IDs 1-5, ING2 : 6-10, ING3-A : 11-18, ING3-B : 19-25,
-- ING3-C : 26-31, ING4-GLSI-A : 32-38, ING4-SSIR-A : 39-44,
-- ING4-SDIA-A : 45-49, ING5-GLSI-A : 50-54, ING5-SSIR-A : 55-59

-- ING1 – cours S1/S2
INSERT INTO notes (valeur, etudiant_id, cours_id, date_saisie) VALUES
(14.50, 1, 1, '2025-01-15'), (12.00, 1, 2, '2025-06-10'),
(16.00, 2, 1, '2025-01-15'), (15.50, 2, 2, '2025-06-10'),
(8.00,  3, 1, '2025-01-15'), (9.50,  3, 2, '2025-06-10'),
(11.00, 4, 1, '2025-01-15'), (13.00, 4, 2, '2025-06-10'),
(17.50, 5, 1, '2025-01-15'), (16.00, 5, 2, '2025-06-10');

-- ING2 – cours S1/S2/S3
INSERT INTO notes (valeur, etudiant_id, cours_id, date_saisie) VALUES
(13.00, 6, 1, '2024-01-20'),  (15.00, 6, 2, '2024-06-15'),  (12.50, 6, 3, '2024-06-20'),
(10.50, 7, 1, '2024-01-20'),  (11.00, 7, 2, '2024-06-15'),  (9.00,  7, 3, '2024-06-20'),
(18.00, 8, 1, '2024-01-20'),  (17.50, 8, 2, '2024-06-15'),  (16.00, 8, 3, '2024-06-20'),
(14.00, 9, 1, '2024-01-20'),  (13.50, 9, 2, '2024-06-15'),  (14.50, 9, 3, '2024-06-20'),
(7.50, 10, 1, '2024-01-20'),  (8.00, 10, 2, '2024-06-15'),  (9.50, 10, 3, '2024-06-20');

-- ING3-A – cours S3/S4/S5
INSERT INTO notes (valeur, etudiant_id, cours_id, date_saisie) VALUES
(15.00, 11, 4, '2023-01-18'), (14.00, 11, 5, '2023-01-22'), (16.50, 11, 6, '2023-06-12'), (13.00, 11, 7, '2023-06-16'),
(12.50, 12, 4, '2023-01-18'), (11.00, 12, 5, '2023-01-22'), (13.00, 12, 6, '2023-06-12'), (10.50, 12, 7, '2023-06-16'),
(9.00,  13, 4, '2023-01-18'), (8.50,  13, 5, '2023-01-22'), (10.00, 13, 6, '2023-06-12'), (9.50,  13, 7, '2023-06-16'),
(17.00, 14, 4, '2023-01-18'), (16.50, 14, 5, '2023-01-22'), (18.00, 14, 6, '2023-06-12'), (15.50, 14, 7, '2023-06-16'),
(11.00, 15, 4, '2023-01-18'), (10.00, 15, 5, '2023-01-22'), (12.00, 15, 6, '2023-06-12'), (11.50, 15, 7, '2023-06-16'),
(14.00, 16, 4, '2023-01-18'), (13.50, 16, 5, '2023-01-22'), (15.00, 16, 6, '2023-06-12'), (14.00, 16, 7, '2023-06-16'),
(6.50,  17, 4, '2023-01-18'), (7.00,  17, 5, '2023-01-22'), (8.00,  17, 6, '2023-06-12'), (7.50,  17, 7, '2023-06-16'),
(13.50, 18, 4, '2023-01-18'), (14.00, 18, 5, '2023-01-22'), (13.50, 18, 6, '2023-06-12'), (12.00, 18, 7, '2023-06-16');

-- ING3-B – cours S3/S4/S5
INSERT INTO notes (valeur, etudiant_id, cours_id, date_saisie) VALUES
(16.00, 19, 4, '2023-01-18'), (15.00, 19, 5, '2023-01-22'), (14.50, 19, 6, '2023-06-12'), (16.00, 19, 8, '2023-06-17'),
(12.00, 20, 4, '2023-01-18'), (11.50, 20, 5, '2023-01-22'), (13.00, 20, 6, '2023-06-12'), (12.50, 20, 8, '2023-06-17'),
(10.50, 21, 4, '2023-01-18'), (9.50,  21, 5, '2023-01-22'), (11.00, 21, 6, '2023-06-12'), (10.00, 21, 8, '2023-06-17'),
(8.00,  22, 4, '2023-01-18'), (9.00,  22, 5, '2023-01-22'), (8.50,  22, 6, '2023-06-12'), (9.00,  22, 8, '2023-06-17'),
(14.50, 23, 4, '2023-01-18'), (13.50, 23, 5, '2023-01-22'), (15.00, 23, 6, '2023-06-12'), (14.00, 23, 8, '2023-06-17'),
(17.50, 24, 4, '2023-01-18'), (18.00, 24, 5, '2023-01-22'), (17.00, 24, 6, '2023-06-12'), (16.50, 24, 8, '2023-06-17'),
(11.50, 25, 4, '2023-01-18'), (12.00, 25, 5, '2023-01-22'), (11.00, 25, 6, '2023-06-12'), (12.00, 25, 8, '2023-06-17');

-- ING3-C – cours S3/S4/S5
INSERT INTO notes (valeur, etudiant_id, cours_id, date_saisie) VALUES
(13.00, 26, 4, '2023-01-18'), (14.50, 26, 5, '2023-01-22'), (12.50, 26, 7, '2023-06-12'),
(15.50, 27, 4, '2023-01-18'), (16.00, 27, 5, '2023-01-22'), (15.00, 27, 7, '2023-06-12'),
(9.00,  28, 4, '2023-01-18'), (8.50,  28, 5, '2023-01-22'), (9.50,  28, 7, '2023-06-12'),
(11.50, 29, 4, '2023-01-18'), (12.00, 29, 5, '2023-01-22'), (11.00, 29, 7, '2023-06-12'),
(16.50, 30, 4, '2023-01-18'), (15.50, 30, 5, '2023-01-22'), (17.00, 30, 7, '2023-06-12'),
(10.00, 31, 4, '2023-01-18'), (10.50, 31, 5, '2023-01-22'), (9.00,  31, 7, '2023-06-12');

-- ING4-GLSI-A – cours S5/S6
INSERT INTO notes (valeur, etudiant_id, cours_id, date_saisie) VALUES
(17.00, 32, 8, '2022-01-14'), (16.50, 32, 10, '2022-06-08'), (15.00, 32, 11, '2022-06-09'),
(13.50, 33, 8, '2022-01-14'), (14.00, 33, 10, '2022-06-08'), (13.00, 33, 11, '2022-06-09'),
(11.00, 34, 8, '2022-01-14'), (10.50, 34, 10, '2022-06-08'), (12.00, 34, 11, '2022-06-09'),
(18.00, 35, 8, '2022-01-14'), (17.50, 35, 10, '2022-06-08'), (18.50, 35, 11, '2022-06-09'),
(14.50, 36, 8, '2022-01-14'), (15.00, 36, 10, '2022-06-08'), (14.00, 36, 11, '2022-06-09'),
(9.50,  37, 8, '2022-01-14'), (9.00,  37, 10, '2022-06-08'), (10.00, 37, 11, '2022-06-09'),
(12.50, 38, 8, '2022-01-14'), (13.00, 38, 10, '2022-06-08'), (12.00, 38, 11, '2022-06-09');

-- ING4-SSIR-A – cours S5/S6
INSERT INTO notes (valeur, etudiant_id, cours_id, date_saisie) VALUES
(15.50, 39, 9, '2022-01-14'),  (14.00, 39, 12, '2022-06-08'), (16.00, 39, 15, '2022-06-10'),
(12.00, 40, 9, '2022-01-14'),  (11.50, 40, 12, '2022-06-08'), (13.00, 40, 15, '2022-06-10'),
(17.50, 41, 9, '2022-01-14'),  (18.00, 41, 12, '2022-06-08'), (17.00, 41, 15, '2022-06-10'),
(10.00, 42, 9, '2022-01-14'),  (9.50,  42, 12, '2022-06-08'), (11.00, 42, 15, '2022-06-10'),
(14.00, 43, 9, '2022-01-14'),  (13.50, 43, 12, '2022-06-08'), (14.50, 43, 15, '2022-06-10'),
(8.50,  44, 9, '2022-01-14'),  (7.00,  44, 12, '2022-06-08'), (9.00,  44, 15, '2022-06-10');

-- ING4-SDIA-A – cours S5/S6
INSERT INTO notes (valeur, etudiant_id, cours_id, date_saisie) VALUES
(16.00, 45, 7,  '2022-01-14'), (15.50, 45, 10, '2022-06-08'), (17.00, 45, 15, '2022-06-10'),
(13.00, 46, 7,  '2022-01-14'), (12.50, 46, 10, '2022-06-08'), (14.00, 46, 15, '2022-06-10'),
(10.50, 47, 7,  '2022-01-14'), (11.00, 47, 10, '2022-06-08'), (10.00, 47, 15, '2022-06-10'),
(18.50, 48, 7,  '2022-01-14'), (19.00, 48, 10, '2022-06-08'), (18.00, 48, 15, '2022-06-10'),
(14.50, 49, 7,  '2022-01-14'), (15.00, 49, 10, '2022-06-08'), (13.50, 49, 15, '2022-06-10');

-- ING5-GLSI-A – cours S7
INSERT INTO notes (valeur, etudiant_id, cours_id, date_saisie) VALUES
(18.00, 50, 13, '2021-06-05'), (17.50, 50, 14, '2021-06-06'),
(15.00, 51, 13, '2021-06-05'), (16.00, 51, 14, '2021-06-06'),
(13.00, 52, 13, '2021-06-05'), (12.50, 52, 14, '2021-06-06'),
(11.00, 53, 13, '2021-06-05'), (10.50, 53, 14, '2021-06-06'),
(16.50, 54, 13, '2021-06-05'), (17.00, 54, 14, '2021-06-06');

-- ING5-SSIR-A – cours S7
INSERT INTO notes (valeur, etudiant_id, cours_id, date_saisie) VALUES
(14.50, 55, 13, '2021-06-05'), (15.00, 55, 14, '2021-06-06'), (16.00, 55, 12, '2021-01-10'),
(17.00, 56, 13, '2021-06-05'), (16.50, 56, 14, '2021-06-06'), (18.00, 56, 12, '2021-01-10'),
(12.00, 57, 13, '2021-06-05'), (11.50, 57, 14, '2021-06-06'), (13.00, 57, 12, '2021-01-10'),
(9.50,  58, 13, '2021-06-05'), (10.00, 58, 14, '2021-06-06'), (8.50,  58, 12, '2021-01-10'),
(15.50, 59, 13, '2021-06-05'), (14.00, 59, 14, '2021-06-06'), (16.50, 59, 12, '2021-01-10');

-- =============================================================
--  Mise à jour du compteur nb_inscrits
-- =============================================================
UPDATE cours c
SET nb_inscrits = (
    SELECT COUNT(DISTINCT etudiant_id) FROM notes WHERE cours_id = c.id
);

SELECT 'Données insérées avec succès !' AS statut;
SELECT COUNT(*) AS nb_etudiants FROM etudiants;
SELECT COUNT(*) AS nb_cours     FROM cours;
SELECT COUNT(*) AS nb_notes     FROM notes;
