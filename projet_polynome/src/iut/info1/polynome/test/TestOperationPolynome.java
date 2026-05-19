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
        Polynome resultat = op.addition(pQuadratique, pQuadratique3);

        assertEquals(2, resultat.getDegre(), "Le degré doit être = 2, reçu: " + resultat.getDegre());
        assertEquals(4, resultat.getCoefficient(0), "Le coefficient de X^0 doit être = 4, reçu: " + resultat.getCoefficient(0));
        assertEquals(5, resultat.getCoefficient(1), "Le coefficient de X^1 doit être = 5, reçu: " + resultat.getCoefficient(1));
        assertEquals(3, resultat.getCoefficient(2), "Le coefficient de X^2 doit être = 3, reçu: " + resultat.getCoefficient(2));
    }

    @Test
    final void testSoustraction() {
        Polynome p1 = new Polynome(new double[]{4, 5, 3});
        Polynome resultat = op.soustraction(p1, pQuadratique3);

        assertEquals(2, resultat.getDegre(), "Le degré doit être = 2, reçu: " + resultat.getDegre());
        assertEquals(1, resultat.getCoefficient(0), "Le coefficient de X^0 doit être = 1, reçu: " + resultat.getCoefficient(0));
        assertEquals(3, resultat.getCoefficient(1), "Le coefficient de X^1 doit être = 3, reçu: " + resultat.getCoefficient(1));
        assertEquals(2, resultat.getCoefficient(2), "Le coefficient de X^2 doit être = 2, reçu: " + resultat.getCoefficient(2));
    }

    @Test
    final void testMultiplicationScalaire() {
        Polynome resultat = op.multiplicationScalaire(pQuadratique, 2);

        assertEquals(2, resultat.getDegre(), "Le degré doit être = 2, reçu: " + resultat.getDegre());
        assertEquals(2, resultat.getCoefficient(0), "Le coefficient de X^0 doit être = 2, reçu: " + resultat.getCoefficient(0));
        assertEquals(6, resultat.getCoefficient(1), "Le coefficient de X^1 doit être = 6, reçu: " + resultat.getCoefficient(1));
        assertEquals(4, resultat.getCoefficient(2), "Le coefficient de X^2 doit être = 4, reçu: " + resultat.getCoefficient(2));
    }

    @Test
    final void testMultiplicationScalaireParZero() {
        Polynome resultat = op.multiplicationScalaire(pQuadratique, 0);

        assertTrue(resultat.estNul(), "Le résultat doit être nul après multiplication par 0");
    }

    @Test
    final void testMultiplication() {
        Polynome resultat = op.multiplication(pLinaire, pLineaire2);

        assertEquals(2, resultat.getDegre(), "Le degré doit être = 2, reçu: " + resultat.getDegre());
        assertEquals(2, resultat.getCoefficient(0), "Le coefficient de X^0 doit être = 2, reçu: " + resultat.getCoefficient(0));
        assertEquals(3, resultat.getCoefficient(1), "Le coefficient de X^1 doit être = 3, reçu: " + resultat.getCoefficient(1));
        assertEquals(1, resultat.getCoefficient(2), "Le coefficient de X^2 doit être = 1, reçu: " + resultat.getCoefficient(2));
    }

    @Test
    final void testDivision() {
        Polynome quotient = op.division(pCubique, pDiviseur);

        assertEquals(2, quotient.getDegre(), "Le degré doit être = 2, reçu: " + quotient.getDegre());
        assertEquals(2, quotient.getCoefficient(0), "Le coefficient de X^0 doit être = 2, reçu: " + quotient.getCoefficient(0));
        assertEquals(1, quotient.getCoefficient(1), "Le coefficient de X^1 doit être = 1, reçu: " + quotient.getCoefficient(1));
        assertEquals(1, quotient.getCoefficient(2), "Le coefficient de X^2 doit être = 1, reçu: " + quotient.getCoefficient(2));
    }

    @Test
    final void testDivisionParPolynomeNul() {
        assertThrows(IllegalArgumentException.class, () -> op.division(pQuadratique, pNul),
                     "Une exception doit être levée lors de la division par le polynôme nul");
    }

    @Test
    final void testReste() {
        Polynome reste = op.reste(pCubique, pDiviseur);

        assertEquals(0, reste.getDegre(), "Le degré doit être = 0, reçu: " + reste.getDegre());
        assertEquals(2, reste.getCoefficient(0), "Le coefficient de X^0 doit être = 2, reçu: " + reste.getCoefficient(0));
    }

    @Test
    final void testPgcd() {
        Polynome p1 = new Polynome(new double[]{20, -26, 4, 2});
        Polynome p2 = new Polynome(new double[]{-6, 1, 1});
        Polynome pgcd = op.pgcd(p1, p2);

        assertEquals(1, pgcd.getDegre(), "Le degré doit être = 1, reçu: " + pgcd.getDegre());
        assertEquals(1, pgcd.getCoefficient(1), "Le coefficient dominant doit être = 1 (unitaire), reçu: " + pgcd.getCoefficient(1));
    }

    @Test
    final void testDerivee() {
        Polynome resultat = op.derivee(pQuadratique);

        assertEquals(1, resultat.getDegre(), "Le degré doit être = 1, reçu: " + resultat.getDegre());
        assertEquals(3, resultat.getCoefficient(0), "Le coefficient de X^0 doit être = 3, reçu: " + resultat.getCoefficient(0));
        assertEquals(4, resultat.getCoefficient(1), "Le coefficient de X^1 doit être = 4, reçu: " + resultat.getCoefficient(1));
    }

    @Test
    final void testDeriveeConstante() {
        Polynome resultat = op.derivee(pConstante5);

        assertTrue(resultat.estNul(), "La dérivée d'une constante doit être nulle");
    }

    @Test
    final void testPrimitive() {
        Polynome p = new Polynome(new double[]{3, 4});
        Polynome resultat = op.primitive(p);

        assertEquals(2, resultat.getDegre(), "Le degré doit être = 2, reçu: " + resultat.getDegre());
        assertEquals(0, resultat.getCoefficient(0), "Le coefficient de X^0 doit être = 0, reçu: " + resultat.getCoefficient(0));
        assertEquals(3, resultat.getCoefficient(1), "Le coefficient de X^1 doit être = 3, reçu: " + resultat.getCoefficient(1));
        assertEquals(2, resultat.getCoefficient(2), "Le coefficient de X^2 doit être = 2, reçu: " + resultat.getCoefficient(2));
    }

    @Test
    final void testCalculImageFonction() {
        double resultat = op.calculImageFonction(pQuadratique2, 3);

        assertEquals(16, resultat, "L'image doit être = 16, reçu: " + resultat);
    }

    @Test
    final void testCalculImageFonctionZero() {
        double resultat = op.calculImageFonction(pQuadratique2, 0);

        assertEquals(1, resultat, "L'image doit être = 1, reçu: " + resultat);
    }

    @Test
    final void testCalculValeurMoyenneIntervalle() {
        Polynome p = new Polynome(new double[]{0, 1});
        double resultat = op.calculValeurMoyenneIntervalle(p, 0, 2);

        assertEquals(1.0, resultat, "La valeur moyenne doit être = 1.0, reçu: " + resultat);
    }

    @Test
    final void testCalculValeurMoyenneBornesIdentiques() {
        Polynome p = new Polynome(new double[]{1, 2});

        assertThrows(IllegalArgumentException.class,
                     () -> op.calculValeurMoyenneIntervalle(p, 2, 2),
                     "Une exception doit être levée quand les bornes sont identiques");
    }

    @Test
    final void testIntegrationPolynome() {
        Polynome p = new Polynome(new double[]{0, 1});
        double resultat = op.integrationPolynome(p, 0, 2);

        assertEquals(2.0, resultat, "L'intégrale doit être = 2.0, reçu: " + resultat);
    }

    @Test
    final void testIntegrationPolynomeConstante() {
        double resultat = op.integrationPolynome(pConstante3, 1, 4);

        assertEquals(9.0, resultat, "L'intégrale doit être = 9.0, reçu: " + resultat);
    }

}
