package iut.info1.polynome.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import iut.info1.polynome.Polynome;
import iut.info1.polynome.OperationPolynome;

/**
 * Classe de validation unitaire de la classe link OperationPolynome}.
 * Plan de test (Couverture des méthodes selon la section 7.4) :
 * Opérations arithmétiques de base :
 *  addition() :
 *      Même degré, degrés différents, avec un polynôme nul,
 *      avec deux polynômes nuls, cas de compensation (annulation de termes).
 * 
 *  soustraction() : 
 *     Même degré, degrés différents, avec un polynôme nul,
 *     annulation totale (P - P = 0).
 *  multiplicationScalaire()} : 
 *     Facteur supérieur à 1, facteur négatif, facteur égal à 1, 
 *     sur un polynôme nul, et cas limite du facteur égal à 0.
 *  multiplication() : 
 *     Produit de deux linéaires, produit linéaire × quadratique, 
 *     produit constante × polynôme, avec un facteur nul, 
 *     avec deux facteurs nuls.
 * 
 * 
 * Division euclidienne et algorithmes associés :
 *  division() : 
 *     Division exacte, division avec reste, 
 *     dividende de degré inférieur au diviseur, 
 *     division par un polynôme constant, 
 *     et cas d'erreur (division par un polynôme nul).
 *     
 *  reste() : 
 *     Reste nul (exact), reste non nul, dividende de degré inférieur,
 *     et cas d'erreur (diviseur nul).
 *     
 *  pgcd() : 
 *     Résultat de degré supérieur ou égal à 1, polynômes premiers entre eux,
 *     second polynôme nul (b = 0), polynômes identiques (a = b).
 *     
 * Analyse et calcul infinitésimal :
 *  derivee() : 
 *     Sur un polynôme quadratique, cubique, linéaire, nul,
 *     et cas limite d'un polynôme constant.
 *     
 *  primitive() : 
 *     Sur un polynôme linéaire, quadratique, constant, et nul.
 *     
 *  calculImageFonction() : 
 *     Évaluation en une abscisse positive (x &gt; 0), négative (x &lt; 0), 
 *     nulle (x = 0), sur un polynôme nul, et sur un polynôme constant.
 *     
 *  calculValeurMoyenneIntervalle() : 
 *     Cas nominal (bornes entières), cas décimal, sur un polynôme nul,
 *     et cas d'erreur (bornes identiques).
 *     
 *  integrationPolynome() : 
 *     Cas nominal (bornes ordonnées), 
 *     bornes inversées, et bornes identiques (intégrale nulle).
 * 
 * @author Moqué Baptiste
 * @author Liao Mattieu
 * @author Laurençont Yanis
 * @author Higounet Yanis
 */
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

    /**
     * Couverture : même degré, degrés différents, nul, 2 nuls, compensation
     */
    @Test
    final void testAddition() {
        // 1. Même degré
        Polynome resMemeDegre = op.addition(pQuadratique, pQuadratique3);
        assertEquals(2, resMemeDegre.getDegre(), "Valeur attendue : 2 | Valeur obtenue : " + resMemeDegre.getDegre());
        assertEquals(4, resMemeDegre.getCoefficient(0), "Valeur attendue : 4 | Valeur obtenue : " + resMemeDegre.getCoefficient(0));
        assertEquals(5, resMemeDegre.getCoefficient(1), "Valeur attendue : 5 | Valeur obtenue : " + resMemeDegre.getCoefficient(1));
        assertEquals(3, resMemeDegre.getCoefficient(2), "Valeur attendue : 3 | Valeur obtenue : " + resMemeDegre.getCoefficient(2));

        // 2. Degrés différents
        Polynome resDegDiff = op.addition(pCubique, pLinaire);
        assertEquals(3, resDegDiff.getDegre(), "Valeur attendue : 3 | Valeur obtenue : " + resDegDiff.getDegre());
        assertEquals(5, resDegDiff.getCoefficient(0), "Valeur attendue : 5 | Valeur obtenue : " + resDegDiff.getCoefficient(0));
        assertEquals(4, resDegDiff.getCoefficient(1), "Valeur attendue : 4 | Valeur obtenue : " + resDegDiff.getCoefficient(1));

        // 3. Avec un polynôme nul
        Polynome resNul = op.addition(pQuadratique, pNul);
        assertEquals(2, resNul.getDegre(), "Valeur attendue : 2 | Valeur obtenue : " + resNul.getDegre());
        assertEquals(1, resNul.getCoefficient(0), "Valeur attendue : 1 | Valeur obtenue : " + resNul.getCoefficient(0));

        // 4. Deux polynômes nuls
        Polynome resDeuxNuls = op.addition(pNul, pNul);
        assertTrue(resDeuxNuls.estNul(), "Le polynôme devrait être nul.");

        // 5. Compensation (annulation de coefficients)
        Polynome pOppose = new Polynome(new double[]{-1, -3, -2});
        Polynome resCompensation = op.addition(pQuadratique, pOppose);
        assertTrue(resCompensation.estNul() || resCompensation.getDegre() == 0 && resCompensation.getCoefficient(0) == 0, "Le polynôme devrait s'annuler après compensation.");
    }

    /**
     * Couverture : même degré, degrés différents, nul, P-P=0
     */
    @Test
    final void testSoustraction() {
        // 1. Même degré
        Polynome p1 = new Polynome(new double[]{4, 5, 3});
        Polynome resMemeDegre = op.soustraction(p1, pQuadratique3);
        assertEquals(2, resMemeDegre.getDegre(), "Valeur attendue : 2 | Valeur obtenue : " + resMemeDegre.getDegre());
        assertEquals(1, resMemeDegre.getCoefficient(0), "Valeur attendue : 1 | Valeur obtenue : " + resMemeDegre.getCoefficient(0));
        assertEquals(3, resMemeDegre.getCoefficient(1), "Valeur attendue : 3 | Valeur obtenue : " + resMemeDegre.getCoefficient(1));
        assertEquals(2, resMemeDegre.getCoefficient(2), "Valeur attendue : 2 | Valeur obtenue : " + resMemeDegre.getCoefficient(2));

        // 2. Degrés différents
        Polynome resDegDiff = op.soustraction(pCubique, pLinaire);
        assertEquals(3, resDegDiff.getDegre(), "Valeur attendue : 3 | Valeur obtenue : " + resDegDiff.getDegre());
        assertEquals(3, resDegDiff.getCoefficient(0), "Valeur attendue : 3 | Valeur obtenue : " + resDegDiff.getCoefficient(0));
        assertEquals(2, resDegDiff.getCoefficient(1), "Valeur attendue : 2 | Valeur obtenue : " + resDegDiff.getCoefficient(1));

        // 3. Avec un polynôme nul
        Polynome resNul = op.soustraction(pQuadratique, pNul);
        assertEquals(1, resNul.getCoefficient(0), "Valeur attendue : 1 | Valeur obtenue : " + resNul.getCoefficient(0));

        // 4. P - P = 0
        Polynome resAnnulation = op.soustraction(pQuadratique, pQuadratique);
        assertTrue(resAnnulation.estNul() || resAnnulation.getCoefficient(0) == 0, "Le polynôme devrait être nul après soustraction par lui-même.");
    }

    /**
     * Couverture : >1, négatif, 1, P nul
     */
    @Test
    final void testMultiplicationScalaire() {
        // 1. Scalaire > 1
        Polynome resGrand = op.multiplicationScalaire(pQuadratique, 2);
        assertEquals(2, resGrand.getDegre(), "Valeur attendue : 2 | Valeur obtenue : " + resGrand.getDegre());
        assertEquals(2, resGrand.getCoefficient(0), "Valeur attendue : 2 | Valeur obtenue : " + resGrand.getCoefficient(0));
        assertEquals(6, resGrand.getCoefficient(1), "Valeur attendue : 6 | Valeur obtenue : " + resGrand.getCoefficient(1));
        assertEquals(4, resGrand.getCoefficient(2), "Valeur attendue : 4 | Valeur obtenue : " + resGrand.getCoefficient(2));

        // 2. Scalaire négatif
        Polynome resNeg = op.multiplicationScalaire(pLinaire, -1);
        assertEquals(-1, resNeg.getCoefficient(0), "Valeur attendue : -1 | Valeur obtenue : " + resNeg.getCoefficient(0));
        assertEquals(-1, resNeg.getCoefficient(1), "Valeur attendue : -1 | Valeur obtenue : " + resNeg.getCoefficient(1));

        // 3. Scalaire = 1
        Polynome resUn = op.multiplicationScalaire(pQuadratique, 1);
        assertEquals(1, resUn.getCoefficient(0), "Valeur attendue : 1 | Valeur obtenue : " + resUn.getCoefficient(0));
        assertEquals(3, resUn.getCoefficient(1), "Valeur attendue : 3 | Valeur obtenue : " + resUn.getCoefficient(1));

        // 4. Polynôme original nul
        Polynome resPInul = op.multiplicationScalaire(pNul, 5);
        assertTrue(resPInul.estNul(), "Le résultat de la multiplication d'un polynôme nul devrait être nul.");
    }

    /**
     * Couverture : vérifie Polynôme nul
     */
    @Test
    final void testMultiplicationScalaireParZero() {
        Polynome resultat = op.multiplicationScalaire(pQuadratique, 0);
        assertTrue(resultat.estNul(), "Le résultat doit être nul après multiplication par 0");
    }

    /**
     * Couverture : 2 linéaires, linéaire×quadratique, constante×P, 1 nul, 2 nuls
     */
    @Test
    final void testMultiplication() {
        // 1. Deux linéaires : (X + 1)(X + 2) = X^2 + 3X + 2
        Polynome resDeuxLin = op.multiplication(pLinaire, pLineaire2);
        assertEquals(2, resDeuxLin.getDegre(), "Valeur attendue : 2 | Valeur obtenue : " + resDeuxLin.getDegre());
        assertEquals(2, resDeuxLin.getCoefficient(0), "Valeur attendue : 2 | Valeur obtenue : " + resDeuxLin.getCoefficient(0));
        assertEquals(3, resDeuxLin.getCoefficient(1), "Valeur attendue : 3 | Valeur obtenue : " + resDeuxLin.getCoefficient(1));
        assertEquals(1, resDeuxLin.getCoefficient(2), "Valeur attendue : 1 | Valeur obtenue : " + resDeuxLin.getCoefficient(2));

        // 2. Linéaire × Quadratique : (X + 1)(X^2 + 2X + 1) = X^3 + 3X^2 + 3X + 1
        Polynome resLinQuad = op.multiplication(pLinaire, pQuadratique2);
        assertEquals(3, resLinQuad.getDegre(), "Valeur attendue : 3 | Valeur obtenue : " + resLinQuad.getDegre());
        assertEquals(1, resLinQuad.getCoefficient(0), "Valeur attendue : 1 | Valeur obtenue : " + resLinQuad.getCoefficient(0));
        assertEquals(3, resLinQuad.getCoefficient(1), "Valeur attendue : 3 | Valeur obtenue : " + resLinQuad.getCoefficient(1));
        assertEquals(3, resLinQuad.getCoefficient(2), "Valeur attendue : 3 | Valeur obtenue : " + resLinQuad.getCoefficient(2));
        assertEquals(1, resLinQuad.getCoefficient(3), "Valeur attendue : 1 | Valeur obtenue : " + resLinQuad.getCoefficient(3));

        // 3. Constante × P
        Polynome resConstP = op.multiplication(pConstante3, pQuadratique);
        assertEquals(3, resConstP.getCoefficient(0), "Valeur attendue : 3 | Valeur obtenue : " + resConstP.getCoefficient(0));
        assertEquals(9, resConstP.getCoefficient(1), "Valeur attendue : 9 | Valeur obtenue : " + resConstP.getCoefficient(1));

        // 4. Un polynôme nul
        Polynome resUnNul = op.multiplication(pQuadratique, pNul);
        assertTrue(resUnNul.estNul(), "Le produit avec un polynôme nul doit être nul.");

        // 5. Deux polynômes nuls
        Polynome resDeuxNuls = op.multiplication(pNul, pNul);
        assertTrue(resDeuxNuls.estNul(), "Le produit de deux polynômes nuls doit être nul.");
    }

    /**
     * Couverture : exacte, avec reste, deg inf, par constante
     */
    @Test
    final void testDivision() {
        // 1. Division exacte : (X^2 + 2X + 1) / (X + 1) = X + 1
        Polynome quotExact = op.division(pQuadratique2, pDiviseur);
        assertEquals(1, quotExact.getDegre(), "Valeur attendue : 1 | Valeur obtenue : " + quotExact.getDegre());
        assertEquals(1, quotExact.getCoefficient(0), "Valeur attendue : 1 | Valeur obtenue : " + quotExact.getCoefficient(0));
        assertEquals(1, quotExact.getCoefficient(1), "Valeur attendue : 1 | Valeur obtenue : " + quotExact.getCoefficient(1));

        // 2. Division avec reste : (X^2 + 2X + 3) / (X + 1) = X + 1 (reste 2)
        Polynome quotAvecReste = op.division(pQuadratique3, pDiviseur);
        assertEquals(1, quotAvecReste.getDegre(), "Valeur attendue : 1 | Valeur obtenue : " + quotAvecReste.getDegre());
        assertEquals(1, quotAvecReste.getCoefficient(0), "Valeur attendue : 1 | Valeur obtenue : " + quotAvecReste.getCoefficient(0));

        // 3. Degré inférieur (deg P1 < deg P2) -> quotient nul
        Polynome quotDegInf = op.division(pLinaire, pQuadratique);
        assertTrue(quotDegInf.estNul() || quotDegInf.getDegre() == 0 && quotDegInf.getCoefficient(0) == 0, "Le quotient devrait être nul si le degré du dividende est inférieur.");

        // 4. Par une constante : (2X^2 + 3X + 1) / 5 = 0.4X^2 + 0.6X + 0.2
        Polynome quotConst = op.division(pQuadratique, pConstante5);
        assertEquals(2, quotConst.getDegre(), "Valeur attendue : 2 | Valeur obtenue : " + quotConst.getDegre());
        assertEquals(0.2, quotConst.getCoefficient(0), 1e-9, "Valeur attendue : 0.2 | Valeur obtenue : " + quotConst.getCoefficient(0));
        assertEquals(0.6, quotConst.getCoefficient(1), 1e-9, "Valeur attendue : 0.6 | Valeur obtenue : " + quotConst.getCoefficient(1));
        assertEquals(0.4, quotConst.getCoefficient(2), 1e-9, "Valeur attendue : 0.4 | Valeur obtenue : " + quotConst.getCoefficient(2));
    }

    /**
     * Couverture : erreur nul
     */
    @Test
    final void testDivisionParPolynomeNul() {
        assertThrows(IllegalArgumentException.class, () -> {
            op.division(pQuadratique, pNul);
        }, "Une division par un polynôme nul doit lever une IllegalArgumentException");
    }

    /**
     * Couverture : exacte, non nul, deg inf., erreur nul
     */
    @Test
    final void testReste() {
        // 1. Reste exact (nul)
        Polynome resteNul = op.reste(pQuadratique2, pDiviseur);
        assertTrue(resteNul.estNul() || Math.abs(resteNul.getCoefficient(0)) < 1e-9, "Le reste devrait être nul.");

        // 2. Reste non nul : (X^2 + 2X + 3) % (X + 1) = 2
        Polynome resteNonNul = op.reste(pQuadratique3, pDiviseur);
        assertEquals(0, resteNonNul.getDegre(), "Valeur attendue : 0 | Valeur obtenue : " + resteNonNul.getDegre());
        assertEquals(2, resteNonNul.getCoefficient(0), "Valeur attendue : 2 | Valeur obtenue : " + resteNonNul.getCoefficient(0));

        // 3. Degré inférieur (le reste est le dividende lui-même)
        Polynome resteDegInf = op.reste(pLinaire, pQuadratique);
        assertEquals(1, resteDegInf.getDegre(), "Valeur attendue : 1 | Valeur obtenue : " + resteDegInf.getDegre());
        assertEquals(1, resteDegInf.getCoefficient(0), "Valeur attendue : 1 | Valeur obtenue : " + resteDegInf.getCoefficient(0));

        // 4. Erreur si diviseur nul
        assertThrows(IllegalArgumentException.class, () -> {
            op.reste(pQuadratique, pNul);
        });
    }

    /**
     * Couverture : deg 1, premiers, b nul, a=b
     */
    @Test
    final void testPgcd() {
        // 1. Degré 1 : pgcd((X+1)^2, X+1) = X + 1
        Polynome pgcdDeg1 = op.pgcd(pQuadratique2, pDiviseur);
        assertEquals(1, pgcdDeg1.getDegre(), "Valeur attendue : 1 | Valeur obtenue : " + pgcdDeg1.getDegre());
        assertEquals(1, pgcdDeg1.getCoefficient(1), "Valeur attendue : 1 | Valeur obtenue : " + pgcdDeg1.getCoefficient(1));

        // 2. Premiers entre eux : pgcd(X+1, X+2) = 1
        Polynome pgcdPremiers = op.pgcd(pLinaire, pLineaire2);
        assertEquals(0, pgcdPremiers.getDegre(), "Valeur attendue : 0 | Valeur obtenue : " + pgcdPremiers.getDegre());
        assertEquals(1, pgcdPremiers.getCoefficient(0), "Valeur attendue : 1 | Valeur obtenue : " + pgcdPremiers.getCoefficient(0));

        // 3. b nul : pgcd(A, 0) = A normalisé
        Polynome pgcdBNul = op.pgcd(pQuadratique, pNul);
        assertEquals(2, pgcdBNul.getDegre(), "Valeur attendue : 2 | Valeur obtenue : " + pgcdBNul.getDegre());
        assertEquals(1.0, pgcdBNul.getCoefficient(2), "Valeur attendue : 1.0 | Valeur obtenue : " + pgcdBNul.getCoefficient(2));

        // 4. a = b : pgcd(A, A) = A normalisé
        Polynome pgcdIdentique = op.pgcd(pQuadratique, pQuadratique);
        assertEquals(2, pgcdIdentique.getDegre(), "Valeur attendue : 2 | Valeur obtenue : " + pgcdIdentique.getDegre());
        assertEquals(1.0, pgcdIdentique.getCoefficient(2), "Valeur attendue : 1.0 | Valeur obtenue : " + pgcdIdentique.getCoefficient(2));
    }

    /**
     * Couverture : quadratique, cubique, linéaire, nul
     */
    @Test
    final void testDerivee() {
        // 1. Quadratique : (2X^2 + 3X + 1)' = 4X + 3
        Polynome resQuad = op.derivee(pQuadratique);
        assertEquals(1, resQuad.getDegre(), "Valeur attendue : 1 | Valeur obtenue : " + resQuad.getDegre());
        assertEquals(3, resQuad.getCoefficient(0), "Valeur attendue : 3 | Valeur obtenue : " + resQuad.getCoefficient(0));
        assertEquals(4, resQuad.getCoefficient(1), "Valeur attendue : 4 | Valeur obtenue : " + resQuad.getCoefficient(1));

        // 2. Cubique : (X^3 + 2X^2 + 3X + 4)' = 3X^2 + 4X + 3
        Polynome resCub = op.derivee(pCubique);
        assertEquals(2, resCub.getDegre(), "Valeur attendue : 2 | Valeur obtenue : " + resCub.getDegre());
        assertEquals(3, resCub.getCoefficient(0), "Valeur attendue : 3 | Valeur obtenue : " + resCub.getCoefficient(0));
        assertEquals(4, resCub.getCoefficient(1), "Valeur attendue : 4 | Valeur obtenue : " + resCub.getCoefficient(1));
        assertEquals(3, resCub.getCoefficient(2), "Valeur attendue : 3 | Valeur obtenue : " + resCub.getCoefficient(2));

        // 3. Linéaire : (X + 1)' = 1
        Polynome resLin = op.derivee(pLinaire);
        assertEquals(0, resLin.getDegre(), "Valeur attendue : 0 | Valeur obtenue : " + resLin.getDegre());
        assertEquals(1, resLin.getCoefficient(0), "Valeur attendue : 1 | Valeur obtenue : " + resLin.getCoefficient(0));

        // 4. Nul : (0)' = 0
        Polynome resNul = op.derivee(pNul);
        assertTrue(resNul.estNul(), "La dérivée d'un polynôme nul doit être nulle.");
    }

    /**
     * Couverture : constante
     */
    @Test
    final void testDeriveeConstante() {
        Polynome resultat = op.derivee(pConstante5);
        assertTrue(resultat.estNul(), "La dérivée d'une constante doit être le polynôme nul");
    }

    /**
     * Couverture : linéaire, quadratique, constante, nul
     */
    @Test
    final void testPrimitive() {
        // 1. Linéaire : primitive de (X + 1) = 0.5X^2 + X
        Polynome primLin = op.primitive(pLinaire);
        assertEquals(2, primLin.getDegre(), "Valeur attendue : 2 | Valeur obtenue : " + primLin.getDegre());
        assertEquals(0.0, primLin.getCoefficient(0), "Valeur attendue : 0.0 | Valeur obtenue : " + primLin.getCoefficient(0));
        assertEquals(1.0, primLin.getCoefficient(1), "Valeur attendue : 1.0 | Valeur obtenue : " + primLin.getCoefficient(1));
        assertEquals(0.5, primLin.getCoefficient(2), "Valeur attendue : 0.5 | Valeur obtenue : " + primLin.getCoefficient(2));

        // 2. Quadratique : primitive de (X^2 + 2X + 1) = 0.333X^3 + X^2 + X
        Polynome primQuad = op.primitive(pQuadratique2);
        assertEquals(3, primQuad.getDegre(), "Valeur attendue : 3 | Valeur obtenue : " + primQuad.getDegre());
        assertEquals(0.0, primQuad.getCoefficient(0), "Valeur attendue : 0.0 | Valeur obtenue : " + primQuad.getCoefficient(0));
        assertEquals(1.0, primQuad.getCoefficient(1), "Valeur attendue : 1.0 | Valeur obtenue : " + primQuad.getCoefficient(1));
        assertEquals(1.0, primQuad.getCoefficient(2), "Valeur attendue : 1.0 | Valeur obtenue : " + primQuad.getCoefficient(2));
        assertEquals(1.0 / 3.0, primQuad.getCoefficient(3), 1e-9, "Valeur attendue : 0.333333333 | Valeur obtenue : " + primQuad.getCoefficient(3));

        // 3. Constante : primitive de 3 = 3X
        Polynome primConst = op.primitive(pConstante3);
        assertEquals(1, primConst.getDegre(), "Valeur attendue : 1 | Valeur obtenue : " + primConst.getDegre());
        assertEquals(0.0, primConst.getCoefficient(0), "Valeur attendue : 0.0 | Valeur obtenue : " + primConst.getCoefficient(0));
        assertEquals(3.0, primConst.getCoefficient(1), "Valeur attendue : 3.0 | Valeur obtenue : " + primConst.getCoefficient(1));

        // 4. Nul : primitive de 0 = 0
        Polynome primNul = op.primitive(pNul);
        assertTrue(primNul.estNul(), "La primitive d'un polynôme nul doit être nulle.");
    }

    /**
     * Couverture : x>0, x<0, P nul, constante
     */
    @Test
    final void testCalculImageFonction() {
        // 1. x > 0 : Image de 2 par (X^2 + 2X + 1) -> 2^2 + 2*2 + 1 = 9
        double imgPos = op.calculImageFonction(pQuadratique2, 2);
        assertEquals(9.0, imgPos, "Valeur attendue : 9.0 | Valeur obtenue : " + imgPos);

        // 2. x < 0 : Image de -1 par (X^2 + 2X + 1) -> (-1)^2 + 2*(-1) + 1 = 0
        double imgNeg = op.calculImageFonction(pQuadratique2, -1);
        assertEquals(0.0, imgNeg, "Valeur attendue : 0.0 | Valeur obtenue : " + imgNeg);

        // 3. Polynôme nul
        double imgPInul = op.calculImageFonction(pNul, 15);
        assertEquals(0.0, imgPInul, "Valeur attendue : 0.0 | Valeur obtenue : " + imgPInul);

        // 4. Polynôme constant : Image de n'importe quoi par 5 -> 5
        double imgConst = op.calculImageFonction(pConstante5, 10);
        assertEquals(5.0, imgConst, "Valeur attendue : 5.0 | Valeur obtenue : " + imgConst);
    }

    /**
     * Couverture : x=0
     */
    @Test
    final void testCalculImageFonctionZero() {
        double resultat = op.calculImageFonction(pQuadratique2, 0);
        assertEquals(1.0, resultat, "Valeur attendue : 1.0 | Valeur obtenue : " + resultat);
    }

    /**
     * Couverture : nominal, décimal, nul
     */
    @Test
    final void testCalculValeurMoyenneIntervalle() {
        // 1. Nominal : moyenne de (X) sur [0, 2] -> primitive 0.5X^2 -> intégrale (2 - 0) = 2 -> moyenne = 2 / 2 = 1.0
        Polynome p = new Polynome(new double[]{0, 1}); // P = X
        double resNominal = op.calculValeurMoyenneIntervalle(p, 0, 2);
        assertEquals(1.0, resNominal, "Valeur attendue : 1.0 | Valeur obtenue : " + resNominal);

        // 2. Décimal : moyenne de (X + 1) sur [0, 1.5]
        double resDecimal = op.calculValeurMoyenneIntervalle(pLinaire, 0, 1.5);
        // intégrale de 0 à 1.5 de (X+1) = [0.5X^2 + X] = 1.125 + 1.5 = 2.625
        // moyenne = 2.625 / 1.5 = 1.75
        assertEquals(1.75, resDecimal, 1e-9, "Valeur attendue : 1.75 | Valeur obtenue : " + resDecimal);

        // 3. Polynôme nul
        double resNul = op.calculValeurMoyenneIntervalle(pNul, 1, 4);
        assertEquals(0.0, resNul, "Valeur attendue : 0.0 | Valeur obtenue : " + resNul);
    }

    /**
     * Couverture : erreur bornes identiques
     */
    @Test
    final void testCalculValeurMoyenneBornesIdentiques() {
        Polynome p = new Polynome(new double[]{1, 2});
        assertThrows(IllegalArgumentException.class, () -> {
            op.calculValeurMoyenneIntervalle(p, 2, 2);
        }, "Une exception doit être levée quand les bornes sont identiques");
    }

    /**
     * Couverture : nominal, bornes inversées, bornes identiques
     */
    @Test
    final void testIntegrationPolynome() {
        // 1. Nominal : intégrale de (X + 1) entre 0 et 2 -> [0.5X^2 + X] = 2 + 2 = 4
        double resNominal = op.integrationPolynome(pLinaire, 0, 2);
        assertEquals(4.0, resNominal, 1e-9, "Valeur attendue : 4.0 | Valeur obtenue : " + resNominal);

        // 2. Bornes inversées : intégrale entre 2 et 0 -> doit être l'opposé (-4)
        double resInverse = op.integrationPolynome(pLinaire, 2, 0);
        assertEquals(-4.0, resInverse, 1e-9, "Valeur attendue : -4.0 | Valeur obtenue : " + resInverse);

        // 3. Bornes identiques : intégrale entre 3 et 3 -> doit être 0
        double resIdentique = op.integrationPolynome(pLinaire, 3, 3);
        assertEquals(0.0, resIdentique, 1e-9, "Valeur attendue : 0.0 | Valeur obtenue : " + resIdentique);
    }
}