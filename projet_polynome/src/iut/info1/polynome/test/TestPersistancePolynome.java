/*
 * TestPersistancePolynome                                          27/05/26
 * IUT de Rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import iut.info1.polynome.Polynome;
import iut.info1.polynome.outils.PersistancePolynome;

/**
 * Classe de validation unitaire de la classe {@link PersistancePolynome}.
 * Tests réalisés en boîte noire : seul le comportement décrit dans la
 * Javadoc et les spécifications est vérifié, sans hypothèse sur 
 * l'implémentation interne.
 *
 * Plan de test :
 * sauvegarder() :
 *   - Cas nominal : liste non vide, fichier créé et rechargeable.
 *   - Liste vide : fichier créé mais sans données de polynômes.
 *   - Cas d'erreur : liste null -> IllegalArgumentException.
 *
 * sauvegarderParRacines() :
 *   - Cas nominal : tableaux cohérents, fichier créé et rechargeable.
 *   - Cas d'erreur : tableaux null -> IllegalArgumentException.
 *   - Cas d'erreur : tableaux de tailles différentes -> IllegalArgumentException.
 *
 * ajouterPolynome() :
 *   - Cas nominal : ajout dans un fichier existant, contenu cumulé.
 *   - Ajout dans un fichier inexistant : le fichier est créé automatiquement.
 *
 * charger() :
 *   - Cas nominal format COEFF : lecture et reconstruction correctes.
 *   - Cas nominal format RACINES : lecture et reconstruction correctes.
 *   - Fichier mixte COEFF + RACINES : tous les polynômes sont chargés.
 *   - Lignes commentaires et vides ignorées : pas d'erreur, 
 *     pas de données parasites.
 *   - Cas d'erreur : fichier inexistant -> IOException.
 *   - Cas d'erreur : format inconnu (ni COEFF ni RACINES) -> IOException.
 *   - Cas d'erreur : valeur numérique invalide -> IOException.
 *
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
class TestPersistancePolynome {

    /** Fichier temporaire utilisé par chaque test, supprimé après. */
    private Path fichierTemp;

    private Polynome pQuadratique;   // 2X^2 + 3X + 1
    private Polynome pLinaire;       // X + 1
    private Polynome pConstante;     // 5
    private Polynome pNul;           // 0

    @BeforeEach
    void setUp() throws Exception {
        fichierTemp = Files.createTempFile("test_persistance_", ".txt");

        pQuadratique = new Polynome(new double[]{1, 3, 2});
        pLinaire     = new Polynome(new double[]{1, 1});
        pConstante   = new Polynome(new double[]{5});
        pNul         = new Polynome();
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(fichierTemp);
    }

    /**
     * Couverture : liste non vide -> fichier créé,
     * polynômes récupérables par charger().
     */
    @Test
    final void testSauvegarderNominal() throws Exception {
        List<Polynome> liste = new ArrayList<>();
        liste.add(pQuadratique);
        liste.add(pLinaire);

        PersistancePolynome.sauvegarder(liste, fichierTemp.toString());

        List<Polynome> charges = PersistancePolynome.charger(fichierTemp.toString());
        assertEquals(2, charges.size(),
                "Valeur attendue : 2 | Valeur obtenue : " + charges.size());

        // Vérification du premier polynôme rechargé (2X^2 + 3X + 1)
        assertEquals(2, charges.get(0).getDegre(), 
                "Valeur attendue : 2 | Valeur obtenue : " 
                + charges.get(0).getDegre());
        assertEquals(2.0, charges.get(0).getCoefficient(0), 1e-9,
                "Valeur attendue (a0) : 2.0 | Valeur obtenue : " 
                + charges.get(0).getCoefficient(0));
        assertEquals(3.0, charges.get(0).getCoefficient(1), 1e-9,
                "Valeur attendue (a1) : 3.0 | Valeur obtenue : "
                + charges.get(0).getCoefficient(1));
        assertEquals(1.0, charges.get(0).getCoefficient(2), 1e-9,
                "Valeur attendue (a2) : 1.0 | Valeur obtenue : " 
                + charges.get(0).getCoefficient(2));

        // Vérification du second polynôme rechargé (X + 1)
        assertEquals(1, charges.get(1).getDegre(),
                "Valeur attendue : 1 | Valeur obtenue : " 
                + charges.get(1).getDegre());
        assertEquals(1.0, charges.get(1).getCoefficient(0), 1e-9,
                "Valeur attendue (a0) : 1.0 | Valeur obtenue : " 
                + charges.get(1).getCoefficient(0));
    }

    /**
     * Couverture : liste vide -> fichier créé mais aucune donnée de polynôme.
     */
    @Test
    final void testSauvegarderListeVide() throws Exception {
        List<Polynome> listeVide = new ArrayList<>();

        PersistancePolynome.sauvegarder(listeVide, fichierTemp.toString());

        assertTrue(Files.exists(fichierTemp),
                "Le fichier doit exister même si la liste est vide.");

        List<Polynome> charges = 
        		PersistancePolynome.charger(fichierTemp.toString());
        assertEquals(0, charges.size(),
                "Valeur attendue : 0 | Valeur obtenue : " + charges.size());
    }

    /**
     * Couverture : liste null -> IllegalArgumentException.
     */
    @Test
    final void testSauvegarderListeNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            PersistancePolynome.sauvegarder(null, fichierTemp.toString());
        }, "Une exception doit être levée si la liste est null.");
    }

    /**
     * Couverture : tableaux cohérents -> fichier créé et rechargeable.
     * P(X) = 2 * (X - 1)^2 * (X + 3)
     */
    @Test
    final void testSauvegarderParRacinesNominal() throws Exception {
        double[][] racines        = {{1.0, -3.0}};
        int[][]    ordres         = {{2, 1}};
        double[]   coeffsDominants = {2.0};

        PersistancePolynome.sauvegarderParRacines(
                racines, ordres, coeffsDominants, fichierTemp.toString());

        assertTrue(Files.exists(fichierTemp),
                "Le fichier doit exister après la sauvegarde par racines.");

        List<Polynome> charges = 
        		PersistancePolynome.charger(fichierTemp.toString());
        assertEquals(1, charges.size(),
                "Valeur attendue : 1 | Valeur obtenue : " + charges.size());
        assertEquals(3, charges.get(0).getDegre(),
                "Valeur attendue : 3 | Valeur obtenue : " 
                + charges.get(0).getDegre());
    }

    /**
     * Couverture : au moins un tableau null -> IllegalArgumentException.
     */
    @Test
    final void testSauvegarderParRacinesNull() {
        double[][] racines        = {{1.0}};
        int[][]    ordres         = {{1}};
        double[]   coeffsDominants = {1.0};

        assertThrows(IllegalArgumentException.class, () -> {
            PersistancePolynome.sauvegarderParRacines(
                    null, ordres, coeffsDominants, fichierTemp.toString());
        }, "Exception attendue si racinesListe est null.");

        assertThrows(IllegalArgumentException.class, () -> {
            PersistancePolynome.sauvegarderParRacines(
                    racines, null, coeffsDominants, fichierTemp.toString());
        }, "Exception attendue si ordresListe est null.");

        assertThrows(IllegalArgumentException.class, () -> {
            PersistancePolynome.sauvegarderParRacines(
                    racines, ordres, null, fichierTemp.toString());
        }, "Exception attendue si coeffsDominants est null.");
    }

    /**
     * Couverture : tableaux de tailles différentes -> IllegalArgumentException.
     */
    @Test
    final void testSauvegarderParRacineTaillesDifferentes() {
        double[][] racines        = {{1.0}, {2.0}};   // taille 2
        int[][]    ordres         = {{1}};             // taille 1
        double[]   coeffsDominants = {1.0, 2.0};       // taille 2

        assertThrows(IllegalArgumentException.class, () -> {
            PersistancePolynome.sauvegarderParRacines(
                    racines, ordres, coeffsDominants, fichierTemp.toString());
        }, "Exception attendue si les tableaux ont des tailles incohérentes.");
    }


    /**
     * Couverture : ajout dans un fichier existant ->contenu cumulé correctement
     */
    @Test
    final void testAjouterPolynomeNominal() throws Exception {
        // On sauvegarde d'abord un premier polynôme
        List<Polynome> liste = new ArrayList<>();
        liste.add(pLinaire);
        PersistancePolynome.sauvegarder(liste, fichierTemp.toString());

        // Puis on en ajoute un second
        PersistancePolynome.ajouterPolynome(pConstante, fichierTemp.toString());

        List<Polynome> charges = 
        		PersistancePolynome.charger(fichierTemp.toString());
        assertEquals(2, charges.size(),
                "Valeur attendue : 2 polynômes | Valeur obtenue : " 
                + charges.size());

        // Le second ajouté est la constante 5
        assertEquals(0, charges.get(1).getDegre(),
                "Valeur attendue : 0 | Valeur obtenue : " 
                + charges.get(1).getDegre());
        assertEquals(5.0, charges.get(1).getCoefficient(0), 1e-9,
                "Valeur attendue (a0) : 5.0 | Valeur obtenue : " 
                + charges.get(1).getCoefficient(0));
    }

    /**
     * Couverture : ajout dans un fichier inexistant 
     * -> fichier créé automatiquement.
     */
    @Test
    final void testAjouterPolynomeFichierInexistant() throws Exception {
        Path nouveauFichier = fichierTemp.resolveSibling("nouveau_test.txt");
        try {
            PersistancePolynome.ajouterPolynome(pQuadratique,
            		                            nouveauFichier.toString());

            assertTrue(Files.exists(nouveauFichier),
                    "Le fichier doit être créé s'il n'existait pas.");

            List<Polynome> charges = 
            		PersistancePolynome.charger(nouveauFichier.toString());
            assertEquals(1, charges.size(),
                    "Valeur attendue : 1 | Valeur obtenue : " + charges.size());
        } finally {
            Files.deleteIfExists(nouveauFichier);
        }
    }


    /**
     * Couverture : format COEFF -> coefficients reconstruits correctement.
     */
    @Test
    final void testChargerFormatCoeff() throws Exception {
        Files.writeString(fichierTemp, "COEFF 1.0 0.0 -4.0\n");

        List<Polynome> charges = 
        		PersistancePolynome.charger(fichierTemp.toString());
        assertEquals(1, charges.size(),
                "Valeur attendue : 1 | Valeur obtenue : " + charges.size());
        assertEquals(2, charges.get(0).getDegre(),
                "Valeur attendue : 2 | Valeur obtenue : " 
                 + charges.get(0).getDegre());
        assertEquals(1.0, charges.get(0).getCoefficient(0), 1e-9,
                "Valeur attendue (a0) : 1.0 | Valeur obtenue : " 
                 + charges.get(0).getCoefficient(0));
        assertEquals(0.0, charges.get(0).getCoefficient(1), 1e-9,
                "Valeur attendue (a1) : 0.0 | Valeur obtenue : " 
                 + charges.get(0).getCoefficient(1));
        assertEquals(-4.0, charges.get(0).getCoefficient(2), 1e-9,
                "Valeur attendue (a2) : -4.0 | Valeur obtenue : " 
                 + charges.get(0).getCoefficient(2));
    }

    /**
     * Couverture : format RACINES -> polynôme reconstruit correctement.
     */
    @Test
    final void testChargerFormatRacines() throws Exception {
        // RACINES 2.0 1.0:2 -3.0:1  ->  2(X-1)^2(X+3), degré 3
        Files.writeString(fichierTemp, "RACINES 2.0 1.0:2 -3.0:1\n");

        List<Polynome> charges = 
        		PersistancePolynome.charger(fichierTemp.toString());
        assertEquals(1, charges.size(),
                "Valeur attendue : 1 | Valeur obtenue : " 
                + charges.size());
        assertEquals(3, charges.get(0).getDegre(),
                "Valeur attendue : 3 | Valeur obtenue : "
                 + charges.get(0).getDegre());
    }

    /**
     * Couverture : fichier mixte COEFF + RACINES -> tous les polynômes chargés.
     */
    @Test
    final void testChargerFichierMixte() throws Exception {
        String contenu = "COEFF 1.0 1.0\n"
                       + "RACINES 1.0 2.0:1\n"
                       + "COEFF 5.0\n";
        Files.writeString(fichierTemp, contenu);

        List<Polynome> charges = 
        		PersistancePolynome.charger(fichierTemp.toString());
        assertEquals(3, charges.size(),
                "Valeur attendue : 3 | Valeur obtenue : " 
                + charges.size());
    }

    /**
     * Couverture : lignes vides et commentaires ignorées 
     * -> aucune erreur, aucune donnée parasite.
     */
    @Test
    final void testChargerIgnoreCommentairesEtLignesVides() throws Exception {
        String contenu = "# Ceci est un commentaire\n"
                       + "\n"
                       + "   \n"
                       + "# Un autre commentaire\n"
                       + "COEFF 1.0 1.0\n"
                       + "# Fin\n";
        Files.writeString(fichierTemp, contenu);

        List<Polynome> charges = 
        		PersistancePolynome.charger(fichierTemp.toString());
        assertEquals(1, charges.size(),
                "Valeur attendue : 1 | Valeur obtenue : " + charges.size());
    }

    /**
     * Couverture : fichier inexistant -> IOException.
     */
    @Test
    final void testChargerFichierInexistant() {
        assertThrows(IOException.class, () -> {
            PersistancePolynome.charger("/chemin/qui/nexiste/pas/fichier.txt");
        }, "Une IOException doit être levée si le fichier est introuvable.");
    }

    /**
     * Couverture : format inconnu (ni COEFF ni RACINES) -> IOException.
     */
    @Test
    final void testChargerFormatInconnu() throws Exception {
        Files.writeString(fichierTemp, "INCONNU 1.0 2.0 3.0\n");

        assertThrows(IOException.class, () -> {
            PersistancePolynome.charger(fichierTemp.toString());
        }, "Une IOException doit être levée si le format de la ligne est inconnu.");
    }

    /**
     * Couverture : valeur numérique invalide dans une ligne COEFF 
     * -> IOException.
     */
    @Test
    final void testChargerValeurNonNumerique() throws Exception {
        Files.writeString(fichierTemp, "COEFF 1.0 abc 3.0\n");

        assertThrows(IOException.class, () -> {
            PersistancePolynome.charger(fichierTemp.toString());
        }, "Une IOException doit être levée si une valeur numérique est invalide.");
    }
}