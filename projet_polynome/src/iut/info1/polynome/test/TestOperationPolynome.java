/*
 * TestOperationPolynome                                           15/05/26
 * IUT de Rodez, pas de copyright ni copyleft
 *
 * Plan de test établi selon la méthode boîte noire :
 *   - Partitionnement en classes d'équivalence
 *   - Analyse aux valeurs limites
 * Aucune connaissance de l'implémentation interne n'est utilisée.
 */

package iut.info1.polynome.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import iut.info1.polynome.OperationPolynome;
import iut.info1.polynome.Polynome;

/**
 * Tests boîte noire de la classe {@link OperationPolynome}.
 *
 * Chaque méthode publique est testée à travers ses classes d'équivalence :
 *   - Cas nominaux  : entrées valides représentatives
 *   - Cas limites   : valeurs aux frontières (nul, degré 0, scalaire=0…)
 *   - Cas d'erreur  : entrées invalides attendant une IllegalArgumentException
 *
 * Fixture (initialisée dans setUp()) :
 *   op            — instance d'OperationPolynome
 *   pNul          — polynôme nul P = 0
 *   pConstante3   — P = 3
 *   pConstante5   — P = 5
 *   pLineaire     — P = X + 1
 *   pLineaire2    — P = X + 2
 *   pQuadratique  — P = 2X² + 3X + 1
 *   pQuadratique2 — P = X² + 2X + 1
 *   pCubique      — P = X³ + 2X² + 3X + 4
 *
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
class TestOperationPolynome {

    /* ------------------------------------------------------------------ */
    /*  Fixture                                                             */
    /* ------------------------------------------------------------------ */

    private OperationPolynome op;

    private Polynome pNul;          // P = 0
    private Polynome pConstante3;   // P = 3
    private Polynome pConstante5;   // P = 5
    private Polynome pLineaire;     // P = X + 1     coeffs = {1, 1}
    private Polynome pLineaire2;    // P = X + 2     coeffs = {2, 1}
    private Polynome pQuadratique;  // P = 2X² + 3X + 1  coeffs = {1, 3, 2}
    private Polynome pQuadratique2; // P = X² + 2X + 1   coeffs = {1, 2, 1}
    private Polynome pCubique;      // P = X³ + 2X² + 3X + 4  coeffs = {4, 3, 2, 1}

    @BeforeEach
    void setUp() throws Exception {
        op            = new OperationPolynome();
        pNul          = new Polynome();
        pConstante3   = new Polynome(new double[]{3});
        pConstante5   = new Polynome(new double[]{5});
        pLineaire     = new Polynome(new double[]{1, 1});
        pLineaire2    = new Polynome(new double[]{2, 1});
        pQuadratique  = new Polynome(new double[]{1, 3, 2});
        pQuadratique2 = new Polynome(new double[]{1, 2, 1});
        pCubique      = new Polynome(new double[]{4, 3, 2, 1});
    }

    /* ================================================================== */
    /*  8.2.1  addition                                                     */
    /* ================================================================== */

    /**
     * CE1 — Même degré, coefficients positifs.
     * (2X² + 3X + 1) + (X² + 2X + 3) = 3X² + 5X + 4
     */
    @Test
    void testAddition_MemeDegreCoefficientsPositifs() {
        Polynome p2 = new Polynome(new double[]{3, 2, 1}); // X² + 2X + 3
        Polynome res = op.addition(pQuadratique, p2);

        assertEquals(2, res.getDegre());
        assertEquals(4.0, res.getCoefficient(0), 1e-9);
        assertEquals(5.0, res.getCoefficient(1), 1e-9);
        assertEquals(3.0, res.getCoefficient(2), 1e-9);
    }

    /**
     * CE2 — Degrés différents : deg(P1) > deg(P2).
     * (X³ + 2X² + 3X + 4) + (X + 1) = X³ + 2X² + 4X + 5
     */
    @Test
    void testAddition_DegresDifferents() {
        Polynome res = op.addition(pCubique, pLineaire);

        assertEquals(3, res.getDegre());
        assertEquals(5.0, res.getCoefficient(0), 1e-9);
        assertEquals(4.0, res.getCoefficient(1), 1e-9);
        assertEquals(2.0, res.getCoefficient(2), 1e-9);
        assertEquals(1.0, res.getCoefficient(3), 1e-9);
    }

    /**
     * CE3 — Addition avec le polynôme nul (élément neutre).
     * (X + 1) + 0 = X + 1
     */
    @Test
    void testAddition_AvecPolynomeNul() {
        Polynome res = op.addition(pLineaire, pNul);

        assertEquals(1, res.getDegre());
        assertEquals(1.0, res.getCoefficient(0), 1e-9);
        assertEquals(1.0, res.getCoefficient(1), 1e-9);
    }

    /**
     * CE4 — Les deux polynômes sont nuls.
     * 0 + 0 = 0
     */
    @Test
    void testAddition_DeuxPolynomesNuls() {
        Polynome res = op.addition(pNul, pNul);

        assertTrue(res.estNul());
    }

    /**
     * CE5 — Les termes dominants se compensent (degré réduit).
     * (X² + 1) + (-X² + 2X) = 2X + 1  (degré 1, pas 2)
     */
    @Test
    void testAddition_CompensationTermeDominant() {
        Polynome p1 = new Polynome(new double[]{1, 0, 1});   //  X² + 1
        Polynome p2 = new Polynome(new double[]{0, 2, -1});  // -X² + 2X
        Polynome res = op.addition(p1, p2);

        assertEquals(1, res.getDegre());
        assertEquals(1.0, res.getCoefficient(0), 1e-9);
        assertEquals(2.0, res.getCoefficient(1), 1e-9);
    }

    /* ================================================================== */
    /*  8.2.2  soustraction                                                 */
    /* ================================================================== */

    /**
     * CE1 — Même degré, résultat positif.
     * (3X² + 5X + 4) - (X² + 2X + 3) = 2X² + 3X + 1
     */
    @Test
    void testSoustraction_MemeDegreResultatPositif() {
        Polynome p1 = new Polynome(new double[]{4, 5, 3}); // 3X² + 5X + 4
        Polynome p2 = new Polynome(new double[]{3, 2, 1}); // X²  + 2X + 3
        Polynome res = op.soustraction(p1, p2);

        assertEquals(2, res.getDegre());
        assertEquals(1.0, res.getCoefficient(0), 1e-9);
        assertEquals(3.0, res.getCoefficient(1), 1e-9);
        assertEquals(2.0, res.getCoefficient(2), 1e-9);
    }

    /**
     * CE2 — Degrés différents : deg(P1) > deg(P2).
     * (X³ + 2X² + 3X + 4) - (X + 1) = X³ + 2X² + 2X + 3
     */
    @Test
    void testSoustraction_DegresDifferents() {
        Polynome res = op.soustraction(pCubique, pLineaire);

        assertEquals(3, res.getDegre());
        assertEquals(3.0, res.getCoefficient(0), 1e-9);
        assertEquals(2.0, res.getCoefficient(1), 1e-9);
        assertEquals(2.0, res.getCoefficient(2), 1e-9);
        assertEquals(1.0, res.getCoefficient(3), 1e-9);
    }

    /**
     * CE3 — Soustraction du polynôme nul (identité).
     * (X + 1) - 0 = X + 1
     */
    @Test
    void testSoustraction_DuPolynomeNul() {
        Polynome res = op.soustraction(pLineaire, pNul);

        assertEquals(1, res.getDegre());
        assertEquals(1.0, res.getCoefficient(0), 1e-9);
        assertEquals(1.0, res.getCoefficient(1), 1e-9);
    }

    /**
     * CE4 — P1 = P2 => résultat = polynôme nul.
     * (X² + 1) - (X² + 1) = 0
     */
    @Test
    void testSoustraction_PolynomeParLuiMeme() {
        Polynome res = op.soustraction(pQuadratique2, pQuadratique2);

        assertTrue(res.estNul());
    }

    /* ================================================================== */
    /*  8.2.3  multiplicationScalaire                                       */
    /* ================================================================== */

    /**
     * CE1 — Scalaire entier > 1.
     * 2 × (2X² + 3X + 1) = 4X² + 6X + 2
     */
    @Test
    void testMultiplicationScalaire_EntierPositif() {
        Polynome res = op.multiplicationScalaire(pQuadratique, 2.0);

        assertEquals(2, res.getDegre());
        assertEquals(2.0, res.getCoefficient(0), 1e-9);
        assertEquals(6.0, res.getCoefficient(1), 1e-9);
        assertEquals(4.0, res.getCoefficient(2), 1e-9);
    }

    /**
     * CE2 — Scalaire négatif.
     * -1 × (X + 3) = -X - 3
     */
    @Test
    void testMultiplicationScalaire_ScalaireNegatif() {
        Polynome p = new Polynome(new double[]{3, 1}); // X + 3
        Polynome res = op.multiplicationScalaire(p, -1.0);

        assertEquals(1, res.getDegre());
        assertEquals(-3.0, res.getCoefficient(0), 1e-9);
        assertEquals(-1.0, res.getCoefficient(1), 1e-9);
    }

    /**
     * CE3 — Scalaire = 0 => résultat = polynôme nul.
     */
    @Test
    void testMultiplicationScalaire_ParZero() {
        Polynome res = op.multiplicationScalaire(pQuadratique, 0.0);

        assertTrue(res.estNul());
    }

    /**
     * CE4 — Scalaire = 1 (identité).
     * 1 × (X + 1) = X + 1
     */
    @Test
    void testMultiplicationScalaire_ParUn() {
        Polynome res = op.multiplicationScalaire(pLineaire, 1.0);

        assertEquals(1, res.getDegre());
        assertEquals(1.0, res.getCoefficient(0), 1e-9);
        assertEquals(1.0, res.getCoefficient(1), 1e-9);
    }

    /**
     * CE5 — Polynôme nul * scalaire quelconque => polynôme nul.
     */
    @Test
    void testMultiplicationScalaire_PolynomeNul() {
        Polynome res = op.multiplicationScalaire(pNul, 5.0);

        assertTrue(res.estNul());
    }

    /* ================================================================== */
    /*  8.2.4  multiplication                                               */
    /* ================================================================== */

    /**
     * CE1 — Produit de deux polynômes linéaires.
     * (X + 1)(X + 2) = X² + 3X + 2
     */
    @Test
    void testMultiplication_DeuxLineaires() {
        Polynome res = op.multiplication(pLineaire, pLineaire2);

        assertEquals(2, res.getDegre());
        assertEquals(2.0, res.getCoefficient(0), 1e-9);
        assertEquals(3.0, res.getCoefficient(1), 1e-9);
        assertEquals(1.0, res.getCoefficient(2), 1e-9);
    }

    /**
     * CE2 — Linéaire × quadratique.
     * (X + 1)(X² + 1) = X³ + X² + X + 1
     */
    @Test
    void testMultiplication_LineaireParQuadratique() {
        Polynome p2 = new Polynome(new double[]{1, 0, 1}); // X² + 1
        Polynome res = op.multiplication(pLineaire, p2);

        assertEquals(3, res.getDegre());
        assertEquals(1.0, res.getCoefficient(0), 1e-9);
        assertEquals(1.0, res.getCoefficient(1), 1e-9);
        assertEquals(1.0, res.getCoefficient(2), 1e-9);
        assertEquals(1.0, res.getCoefficient(3), 1e-9);
    }

    /**
     * CE3 — Constante × polynôme.
     * 3 × (X² + X + 1) = 3X² + 3X + 3
     */
    @Test
    void testMultiplication_ConstanteParPolynome() {
        Polynome p2 = new Polynome(new double[]{1, 1, 1}); // X² + X + 1
        Polynome res = op.multiplication(pConstante3, p2);

        assertEquals(2, res.getDegre());
        assertEquals(3.0, res.getCoefficient(0), 1e-9);
        assertEquals(3.0, res.getCoefficient(1), 1e-9);
        assertEquals(3.0, res.getCoefficient(2), 1e-9);
    }

    /**
     * CE4 — Un des polynômes est nul => résultat nul.
     * 0 × (X + 1) = 0
     */
    @Test
    void testMultiplication_ParPolynomeNul() {
        Polynome res = op.multiplication(pNul, pLineaire);

        assertTrue(res.estNul());
    }

    /**
     * CE5 — Les deux polynômes sont nuls.
     * 0 × 0 = 0
     */
    @Test
    void testMultiplication_DeuxPolynomesNuls() {
        Polynome res = op.multiplication(pNul, pNul);

        assertTrue(res.estNul());
    }

    /* ================================================================== */
    /*  8.2.5  division                                                     */
    /* ================================================================== */

    /**
     * CE1 — Division euclidienne exacte (reste nul).
     * (X³ + 2X² + 3X + 4) / (X + 1) = X² + X + 2
     */
    @Test
    void testDivision_DivisionExacte() {
        Polynome res = op.division(pCubique, pLineaire);

        assertEquals(2, res.getDegre());
        assertEquals(2.0, res.getCoefficient(0), 1e-9);
        assertEquals(1.0, res.getCoefficient(1), 1e-9);
        assertEquals(1.0, res.getCoefficient(2), 1e-9);
    }

    /**
     * CE2 — Division avec reste non nul.
     * (X² + 1) / (X + 1) : quotient = X - 1
     */
    @Test
    void testDivision_AvecReste() {
        Polynome p1 = new Polynome(new double[]{1, 0, 1}); // X² + 1
        Polynome res = op.division(p1, pLineaire);

        assertEquals(1, res.getDegre());
        assertEquals(-1.0, res.getCoefficient(0), 1e-9);
        assertEquals(1.0,  res.getCoefficient(1), 1e-9);
    }

    /**
     * CE3 — deg(P1) < deg(P2) => quotient = polynôme nul.
     * (X + 1) / (X² + 1) = Q nul, R = X + 1
     */
    @Test
    void testDivision_DegreeInferieur() {
        Polynome diviseur = new Polynome(new double[]{1, 0, 1}); // X² + 1
        Polynome res = op.division(pLineaire, diviseur);

        assertTrue(res.estNul());
    }

    /**
     * CE4 — Division par un polynôme constant non nul.
     * (2X² + 4) / 2 = X² + 2
     */
    @Test
    void testDivision_ParConstante() {
        Polynome p1 = new Polynome(new double[]{4, 0, 2}); // 2X² + 4
        Polynome res = op.division(p1, new Polynome(new double[]{2}));

        assertEquals(2, res.getDegre());
        assertEquals(2.0, res.getCoefficient(0), 1e-9);
        assertEquals(0.0, res.getCoefficient(1), 1e-9);
        assertEquals(1.0, res.getCoefficient(2), 1e-9);
    }

    /**
     * CE5 — Erreur : diviseur = polynôme nul.
     * Doit lever IllegalArgumentException.
     */
    @Test
    void testDivision_ParPolynomeNul_LanceException() {
        assertThrows(IllegalArgumentException.class,
                     () -> op.division(pQuadratique, pNul));
    }

    /* ================================================================== */
    /*  8.2.6  reste                                                        */
    /* ================================================================== */

    /**
     * CE1 — Division exacte : reste = polynôme nul constant valant 2.
     * reste((X³ + 2X² + 3X + 4), (X + 1)) = 2
     */
    @Test
    void testReste_DivisionExacte() {
        Polynome res = op.reste(pCubique, pLineaire);

        assertEquals(0, res.getDegre());
        assertEquals(2.0, res.getCoefficient(0), 1e-9);
    }

    /**
     * CE2 — Reste non nul.
     * reste((X² + 1), (X + 1)) = 2
     */
    @Test
    void testReste_NonNul() {
        Polynome p1 = new Polynome(new double[]{1, 0, 1}); // X² + 1
        Polynome res = op.reste(p1, pLineaire);

        assertEquals(0, res.getDegre());
        assertEquals(2.0, res.getCoefficient(0), 1e-9);
    }

    /**
     * CE3 — deg(P1) < deg(P2) => reste = P1 entier.
     * reste((X + 1), (X² + 1)) = X + 1
     */
    @Test
    void testReste_DegreeInferieur() {
        Polynome diviseur = new Polynome(new double[]{1, 0, 1}); // X² + 1
        Polynome res = op.reste(pLineaire, diviseur);

        assertEquals(1, res.getDegre());
        assertEquals(1.0, res.getCoefficient(0), 1e-9);
        assertEquals(1.0, res.getCoefficient(1), 1e-9);
    }

    /**
     * CE4 — Erreur : diviseur = polynôme nul.
     * Doit lever IllegalArgumentException.
     */
    @Test
    void testReste_ParPolynomeNul_LanceException() {
        assertThrows(IllegalArgumentException.class,
                     () -> op.reste(pQuadratique, pNul));
    }

    /* ================================================================== */
    /*  8.2.7  pgcd                                                         */
    /* ================================================================== */

    /**
     * CE1 — PGCD non trivial (degré 1).
     * pgcd(2X³ + 4X² - 26X + 20, X² + X - 6) = X - 2
     */
    @Test
    void testPgcd_DegreeUn() {
        Polynome a = new Polynome(new double[]{20, -26, 4, 2}); // 2X³ + 4X² - 26X + 20
        Polynome b = new Polynome(new double[]{-6, 1, 1});      // X²  + X  - 6
        Polynome res = op.pgcd(a, b);

        assertEquals(1, res.getDegre());
        // Résultat normalisé : coefficient dominant = 1
        assertEquals(1.0, res.getCoefficient(1), 1e-6);
    }

    /**
     * CE2 — Polynômes premiers entre eux (PGCD = constante).
     * pgcd(X² + 1, X + 1) = 1
     */
    @Test
    void testPgcd_PolynomesPremiers() {
        Polynome a = new Polynome(new double[]{1, 0, 1}); // X² + 1
        Polynome res = op.pgcd(a, pLineaire);

        assertEquals(0, res.getDegre());
        assertEquals(1.0, res.getCoefficient(0), 1e-6);
    }

    /**
     * CE3 — b = polynôme nul => pgcd = a normalisé.
     * pgcd(2X + 4, 0) = X + 2 (unitaire)
     */
    @Test
    void testPgcd_DeuxiemePolynomeNul() {
        Polynome a = new Polynome(new double[]{4, 2}); // 2X + 4
        Polynome res = op.pgcd(a, pNul);

        assertEquals(1, res.getDegre());
        assertEquals(1.0, res.getCoefficient(1), 1e-6); // coefficient dominant = 1
    }

    /**
     * CE4 — a = b => pgcd = a normalisé.
     * pgcd(X + 1, X + 1) = X + 1
     */
    @Test
    void testPgcd_PolynomesIdentiques() {
        Polynome res = op.pgcd(pLineaire, pLineaire);

        assertEquals(1, res.getDegre());
        assertEquals(1.0, res.getCoefficient(1), 1e-6);
    }

    /* ================================================================== */
    /*  8.2.8  derivee                                                      */
    /* ================================================================== */

    /**
     * CE1 — Dérivée d'un polynôme quadratique.
     * (2X² + 3X + 1)' = 4X + 3
     */
    @Test
    void testDerivee_Quadratique() {
        Polynome res = op.derivee(pQuadratique);

        assertEquals(1, res.getDegre());
        assertEquals(3.0, res.getCoefficient(0), 1e-9);
        assertEquals(4.0, res.getCoefficient(1), 1e-9);
    }

    /**
     * CE2 — Dérivée d'un polynôme cubique.
     * (X³ + 2X² + 3X + 4)' = 3X² + 4X + 3
     */
    @Test
    void testDerivee_Cubique() {
        Polynome res = op.derivee(pCubique);

        assertEquals(2, res.getDegre());
        assertEquals(3.0, res.getCoefficient(0), 1e-9);
        assertEquals(4.0, res.getCoefficient(1), 1e-9);
        assertEquals(3.0, res.getCoefficient(2), 1e-9);
    }

    /**
     * CE3 — Dérivée d'un polynôme linéaire => constante.
     * (X + 1)' = 1
     */
    @Test
    void testDerivee_Lineaire() {
        Polynome res = op.derivee(pLineaire);

        assertEquals(0, res.getDegre());
        assertEquals(1.0, res.getCoefficient(0), 1e-9);
    }

    /**
     * CE4 — Dérivée d'une constante => polynôme nul.
     * (5)' = 0
     */
    @Test
    void testDerivee_Constante() {
        Polynome res = op.derivee(pConstante5);

        assertTrue(res.estNul());
    }

    /**
     * CE5 — Dérivée du polynôme nul => polynôme nul.
     * (0)' = 0
     */
    @Test
    void testDerivee_PolynomeNul() {
        Polynome res = op.derivee(pNul);

        assertTrue(res.estNul());
    }

    /* ================================================================== */
    /*  8.2.9  primitive                                                    */
    /* ================================================================== */

    /**
     * CE1 — Primitive d'un linéaire.
     * ∫(4X + 3) = 2X² + 3X   (coeff[0] = 0)
     */
    @Test
    void testPrimitive_Lineaire() {
        Polynome p = new Polynome(new double[]{3, 4}); // 4X + 3
        Polynome res = op.primitive(p);

        assertEquals(2, res.getDegre());
        assertEquals(0.0, res.getCoefficient(0), 1e-9);
        assertEquals(3.0, res.getCoefficient(1), 1e-9);
        assertEquals(2.0, res.getCoefficient(2), 1e-9);
    }

    /**
     * CE2 — Primitive d'un polynôme quadratique.
     * ∫(3X² + 2X + 1) = X³ + X² + X
     */
    @Test
    void testPrimitive_Quadratique() {
        Polynome p = new Polynome(new double[]{1, 2, 3}); // 3X² + 2X + 1
        Polynome res = op.primitive(p);

        assertEquals(3, res.getDegre());
        assertEquals(0.0, res.getCoefficient(0), 1e-9);
        assertEquals(1.0, res.getCoefficient(1), 1e-9);
        assertEquals(1.0, res.getCoefficient(2), 1e-9);
        assertEquals(1.0, res.getCoefficient(3), 1e-9);
    }

    /**
     * CE3 — Primitive d'une constante.
     * ∫(5) = 5X   =>  coeffs = {0, 5}
     */
    @Test
    void testPrimitive_Constante() {
        Polynome res = op.primitive(pConstante5);

        assertEquals(1, res.getDegre());
        assertEquals(0.0, res.getCoefficient(0), 1e-9);
        assertEquals(5.0, res.getCoefficient(1), 1e-9);
    }

    /**
     * CE4 — Primitive du polynôme nul => polynôme nul.
     * ∫(0) = 0
     */
    @Test
    void testPrimitive_PolynomeNul() {
        Polynome res = op.primitive(pNul);

        assertTrue(res.estNul());
    }

    /* ================================================================== */
    /*  8.2.10  calculImageFonction                                         */
    /* ================================================================== */

    /**
     * CE1 — Évaluation en x > 0.
     * P = X² + 2X + 1,  P(3) = 9 + 6 + 1 = 16
     */
    @Test
    void testCalculImageFonction_XPositif() {
        double res = op.calculImageFonction(pQuadratique2, 3.0);
        assertEquals(16.0, res, 1e-9);
    }

    /**
     * CE2 — Évaluation en x = 0 => terme constant.
     * P = X² + 2X + 1,  P(0) = 1
     */
    @Test
    void testCalculImageFonction_XZero() {
        double res = op.calculImageFonction(pQuadratique2, 0.0);
        assertEquals(1.0, res, 1e-9);
    }

    /**
     * CE3 — Évaluation en x < 0.
     * P = X² + 2X + 1,  P(-1) = 1 - 2 + 1 = 0
     */
    @Test
    void testCalculImageFonction_XNegatif() {
        double res = op.calculImageFonction(pQuadratique2, -1.0);
        assertEquals(0.0, res, 1e-9);
    }

    /**
     * CE4 — Polynôme nul : P(x) = 0 pour tout x.
     */
    @Test
    void testCalculImageFonction_PolynomeNul() {
        double res = op.calculImageFonction(pNul, 99.0);
        assertEquals(0.0, res, 1e-9);
    }

    /**
     * CE5 — Polynôme constant.
     * P = 5,  P(42) = 5
     */
    @Test
    void testCalculImageFonction_Constant() {
        double res = op.calculImageFonction(pConstante5, 42.0);
        assertEquals(5.0, res, 1e-9);
    }

    /* ================================================================== */
    /*  8.2.11  integrationPolynome                                         */
    /* ================================================================== */

    /**
     * CE1 — Intégrale de X sur [0, 2].
     * ∫₀² X dx = [X²/2]₀² = 2.0
     */
    @Test
    void testIntegrationPolynome_LineareSurIntervalle() {
        Polynome p = new Polynome(new double[]{0, 1}); // X
        double res = op.integrationPolynome(p, 0.0, 2.0);
        assertEquals(2.0, res, 1e-9);
    }

    /**
     * CE2 — Intégrale d'une constante sur [1, 4].
     * ∫₁⁴ 3 dx = 3 × (4 - 1) = 9.0
     */
    @Test
    void testIntegrationPolynome_Constante() {
        double res = op.integrationPolynome(pConstante3, 1.0, 4.0);
        assertEquals(9.0, res, 1e-9);
    }

    /**
     * CE3 — Intégrale avec a > b => valeur négative.
     * ∫₂⁰ X dx = -2.0
     */
    @Test
    void testIntegrationPolynome_BornesInversees() {
        Polynome p = new Polynome(new double[]{0, 1}); // X
        double res = op.integrationPolynome(p, 2.0, 0.0);
        assertEquals(-2.0, res, 1e-9);
    }

    /**
     * CE4 — Intégrale sur intervalle symétrique d'une fonction impaire.
     * ∫₋₁¹ X dx = 0.0
     */
    @Test
    void testIntegrationPolynome_IntervalleSymetrique() {
        Polynome p = new Polynome(new double[]{0, 1}); // X
        double res = op.integrationPolynome(p, -1.0, 1.0);
        assertEquals(0.0, res, 1e-9);
    }

    /**
     * CE5 — Intégrale du polynôme nul.
     * ∫₀¹⁰ 0 dx = 0.0
     */
    @Test
    void testIntegrationPolynome_PolynomeNul() {
        double res = op.integrationPolynome(pNul, 0.0, 10.0);
        assertEquals(0.0, res, 1e-9);
    }

    /* ================================================================== */
    /*  8.2.12  calculValeurMoyenneIntervalle                               */
    /* ================================================================== */

    /**
     * CE1 — Valeur moyenne de X sur [0, 2].
     * (1/2) × ∫₀² X dx = (1/2) × 2 = 1.0
     */
    @Test
    void testCalculValeurMoyenne_XSur0_2() {
        Polynome p = new Polynome(new double[]{0, 1}); // X
        double res = op.calculValeurMoyenneIntervalle(p, 0.0, 2.0);
        assertEquals(1.0, res, 1e-9);
    }

    /**
     * CE2 — Valeur moyenne de X² sur [0, 3].
     * (1/3) × ∫₀³ X² dx = (1/3) × [X³/3]₀³ = (1/3) × 9 = 3.0
     */
    @Test
    void testCalculValeurMoyenne_X2Sur0_3() {
        Polynome p = new Polynome(new double[]{0, 0, 1}); // X²
        double res = op.calculValeurMoyenneIntervalle(p, 0.0, 3.0);
        assertEquals(3.0, res, 1e-9);
    }

    /**
     * CE3 — Polynôme nul : valeur moyenne = 0 sur tout intervalle.
     */
    @Test
    void testCalculValeurMoyenne_PolynomeNul() {
        double res = op.calculValeurMoyenneIntervalle(pNul, 0.0, 5.0);
        assertEquals(0.0, res, 1e-9);
    }

    /**
     * CE4 — Erreur : bornes identiques (a == b) => division par zéro.
     * Doit lever IllegalArgumentException.
     */
    @Test
    void testCalculValeurMoyenne_BornesIdentiques_LanceException() {
        assertThrows(IllegalArgumentException.class,
                     () -> op.calculValeurMoyenneIntervalle(pLineaire, 2.0, 2.0));
    }

}