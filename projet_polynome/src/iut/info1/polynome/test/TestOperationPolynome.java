package iut.info1.polynome.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import iut.info1.polynome.Polynome;
import iut.info1.polynome.OperationPolynome;

class TestOperationPolynome {

    private OperationPolynome op;

    // Polynômes de base pour les tests
    private Polynome pConstante5;           // P = 5
    private Polynome pConstante3;           // P = 3
    private Polynome pLinaire;              // P = X + 1
    private Polynome pLineaire2;            // P = X + 2
    private Polynome pQuadratique;          // P = 2X^2 + 3X + 1
    private Polynome pQuadratique2;         // P = X^2 + 2X + 1
    private Polynome pQuadratique3;         // P = X^2 + 2X + 3
    private Polynome pCubique;              // P = X^3 + 2X^2 + 3X + 4
    private Polynome pDiviseur;             // P = X + 1
    private Polynome pNul;                  // P = 0
    private Polynome pDeriveResultat;       // P = 4X + 3 (dérivée de 2X^2 + 3X + 1)
    private Polynome pPrimitiveResultat;    // P = 3X + 2X^2

    @BeforeEach
    void setUp() throws Exception {
        op = new OperationPolynome();

        // Polynômes constants
        pConstante5 = new Polynome(new double[]{5});
        pConstante3 = new Polynome(new double[]{3});

        // Polynômes linéaires
        pLinaire = new Polynome(new double[]{1, 1});      // X + 1
        pLineaire2 = new Polynome(new double[]{2, 1});    // X + 2

        // Polynômes quadratiques
        pQuadratique = new Polynome(new double[]{1, 3, 2});    // 2X^2 + 3X + 1
        pQuadratique2 = new Polynome(new double[]{1, 2, 1});   // X^2 + 2X + 1
        pQuadratique3 = new Polynome(new double[]{3, 2, 1});   // X^2 + 2X + 3

        // Polynôme cubique
        pCubique = new Polynome(new double[]{4, 3, 2, 1});     // X^3 + 2X^2 + 3X + 4

        // Polynôme diviseur
        pDiviseur = new Polynome(new double[]{1, 1});          // X + 1

        // Polynôme nul
        pNul = new Polynome();

        // Résultats attendus
        pDeriveResultat = new Polynome(new double[]{3, 4});         // 4X + 3
        pPrimitiveResultat = new Polynome(new double[]{0, 3, 2});   // 2X^2 + 3X
    }

    @Test
    final void testAddition() {
        // P1 + P2 = (2X^2 + 3X + 1) + (X^2 + 2X + 3) = 3X^2 + 5X + 4
        Polynome resultat = op.addition(pQuadratique, pQuadratique3);

        assertEquals(2, resultat.getDegre());
        assertEquals(4, resultat.getCoefficient(0));
        assertEquals(5, resultat.getCoefficient(1));
        assertEquals(3, resultat.getCoefficient(2));
    }

    @Test
    final void testSoustraction() {
        // (3X^2 + 5X + 4) - (X^2 + 2X + 3) = 2X^2 + 3X + 1
        Polynome p1 = new Polynome(new double[]{4, 5, 3});
        Polynome resultat = op.soustraction(p1, pQuadratique3);

        assertEquals(2, resultat.getDegre());
        assertEquals(1, resultat.getCoefficient(0));
        assertEquals(3, resultat.getCoefficient(1));
        assertEquals(2, resultat.getCoefficient(2));
    }

    @Test
    final void testMultiplicationScalaire() {
        // 2 × (2X^2 + 3X + 1) = 4X^2 + 6X + 2
        Polynome resultat = op.multiplicationScalaire(pQuadratique, 2);

        assertEquals(2, resultat.getDegre());
        assertEquals(2, resultat.getCoefficient(0));
        assertEquals(6, resultat.getCoefficient(1));
        assertEquals(4, resultat.getCoefficient(2));
    }

    @Test
    final void testMultiplicationScalaireParZero() {
        Polynome resultat = op.multiplicationScalaire(pQuadratique, 0);

        assertTrue(resultat.estNul());
    }

    @Test
    final void testMultiplication() {
        // (X + 1) × (X + 2) = X^2 + 3X + 2
        Polynome resultat = op.multiplication(pLinaire, pLineaire2);

        assertEquals(2, resultat.getDegre());
        assertEquals(2, resultat.getCoefficient(0));
        assertEquals(3, resultat.getCoefficient(1));
        assertEquals(1, resultat.getCoefficient(2));
    }

    @Test
    final void testDivision() {
        // (X^3 + 2X^2 + 3X + 4) / (X + 1) = X^2 + X + 2
        Polynome quotient = op.division(pCubique, pDiviseur);

        assertEquals(2, quotient.getDegre());
        assertEquals(2, quotient.getCoefficient(0));
        assertEquals(1, quotient.getCoefficient(1));
        assertEquals(1, quotient.getCoefficient(2));
    }

    @Test
    final void testDivisionParPolynomeNul() {
        assertThrows(IllegalArgumentException.class, () -> op.division(pQuadratique, pNul));
    }

    @Test
    final void testReste() {
        // Reste de (X^3 + 2X^2 + 3X + 4) / (X + 1) = 2
        Polynome reste = op.reste(pCubique, pDiviseur);

        assertEquals(0, reste.getDegre());
        assertEquals(2, reste.getCoefficient(0));
    }

    @Test
    final void testPgcd() {
        Polynome p1 = new Polynome(new double[]{20, -26, 4, 2});    // 2X^3 + 4X^2 - 26X + 20
        Polynome p2 = new Polynome(new double[]{-6, 1, 1});         // X^2 + X - 6
        Polynome pgcd = op.pgcd(p1, p2);

        assertEquals(1, pgcd.getDegre());
        assertEquals(1, pgcd.getCoefficient(1)); // Coefficient dominant = 1 (unitaire)
    }

    @Test
    final void testDerivee() {
        // Dérivée de (2X^2 + 3X + 1) = 4X + 3
        Polynome resultat = op.derivee(pQuadratique);

        assertEquals(1, resultat.getDegre());
        assertEquals(3, resultat.getCoefficient(0));
        assertEquals(4, resultat.getCoefficient(1));
    }

    @Test
    final void testDeriveeConstante() {
        Polynome resultat = op.derivee(pConstante5);

        assertTrue(resultat.estNul());
    }

    @Test
    final void testPrimitive() {
        // Primitive de (4X + 3) = 3X + 2X^2
        Polynome p = new Polynome(new double[]{3, 4});
        Polynome resultat = op.primitive(p);

        assertEquals(2, resultat.getDegre());
        assertEquals(0, resultat.getCoefficient(0));
        assertEquals(3, resultat.getCoefficient(1));
        assertEquals(2, resultat.getCoefficient(2));
    }

    @Test
    final void testCalculImageFonction() {
        // P(3) pour P = X^2 + 2X + 1 = 9 + 6 + 1 = 16
        double resultat = op.calculImageFonction(pQuadratique2, 3);

        assertEquals(16, resultat);
    }

    @Test
    final void testCalculImageFonctionZero() {
        // P(0) pour P = X^2 + 2X + 1 = 1
        double resultat = op.calculImageFonction(pQuadratique2, 0);

        assertEquals(1, resultat);
    }

    @Test
    final void testCalculValeurMoyenneIntervalle() {
        // Valeur moyenne de P = X sur [0, 2] = 1
        Polynome p = new Polynome(new double[]{0, 1});
        double resultat = op.calculValeurMoyenneIntervalle(p, 0, 2);

        assertEquals(1.0, resultat);
    }

    @Test
    final void testCalculValeurMoyenneBornesIdentiques() {
        Polynome p = new Polynome(new double[]{1, 2});

        assertThrows(IllegalArgumentException.class,
                     () -> op.calculValeurMoyenneIntervalle(p, 2, 2));
    }

    @Test
    final void testIntegrationPolynome() {
        // Intégrale de P = X sur [0, 2] = 2
        Polynome p = new Polynome(new double[]{0, 1});
        double resultat = op.integrationPolynome(p, 0, 2);

        assertEquals(2.0, resultat);
    }

    @Test
    final void testIntegrationPolynomeConstante() {
        // Intégrale de P = 3 sur [1, 4] = 3 × (4 - 1) = 9
        double resultat = op.integrationPolynome(pConstante3, 1, 4);

        assertEquals(9.0, resultat);
    }

}
