/*
 * TestSuiteSturm                                          21/05/26
 * IUT de Rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import iut.info1.polynome.Polynome;
import iut.info1.polynome.outils.SuiteSturm;

/**
 * Classe de validation unitaire de la classe {@link SuiteSturm}.
 * Tests réalisés en boîte noire : seul le comportement décrit dans la
 * Javadoc est vérifié, sans hypothèse sur l'implémentation interne.
 *
 * Plan de test :
 *
 * supprimerRacinesMultiples() :
 *     Polynôme sans racine multiple (degré inchangé),
 *     racine double — degré réduit à 1,
 *     racine triple — degré réduit à 1,
 *     racines conservées après suppression,
 *     polynôme constant.
 *
 * construireSuite() :
 *     Suite d'un polynôme linéaire (2 éléments),
 *     premier élément = P0 (même degré),
 *     deuxième élément = P0' (degré P0 − 1),
 *     degrés strictement décroissants d'un élément à l'autre,
 *     suite d'un quadratique à deux racines simples (3 éléments).
 *
 * compterChangementsDeSignes() :
 *     Suite de longueur 1 — résultat 0,
 *     tous les termes de même signe — résultat 0,
 *     alternance stricte +−+ — résultat 2,
 *     valeur nulle ignorée dans le décompte.
 *
 * nombreRacinesIntervalle() :
 *     Polynôme constant — 0 racine,
 *     polynôme sans racine réelle — 0 racine,
 *     linéaire dans un grand intervalle — 1 racine,
 *     linéaire hors de l'intervalle — 0 racine,
 *     quadratique — 2 racines dans ]1, 4[, 1 racine dans ]1, 2.5[,
 *     cubique — 3 racines dans ]0, 4[,
 *     racine double comptée une seule fois,
 *     aucune racine dans un intervalle distant,
 *     exception si a ≥ b (a > b et a = b).
 *
 * localiserRacines() :
 *     Nombre d'intervalles = nombre de racines distinctes,
 *     chaque intervalle contient exactement 1 racine (Sturm),
 *     polynôme sans racine — liste vide,
 *     intervalles bien ordonnés (a < b),
 *     racines connues couvertes par les intervalles retournés.
 *
 * approximerParDichotomie() :
 *     Racine de X − 2 en ]1, 3[ ≈ 2.0,
 *     racine 2 de X² − 5X + 6 en ]1, 2.5[,
 *     racine 3 de X² − 5X + 6 en ]2.5, 4[,
 *     P(racine approchée) ≈ 0,
 *     P(a) = 0 — retourne a directement,
 *     P(b) = 0 — retourne b directement,
 *     racine négative − 1 pour (X + 1)³,
 *     exception si P(a) et P(b) ont le même signe.
 *
 * approximerParNewton() :
 *     Racine de X − 2 depuis x0 = 0 ≈ 2.0,
 *     racine 2 de X² − 5X + 6 depuis x0 = 1.5,
 *     racine 3 de X² − 5X + 6 depuis x0 = 3.5,
 *     P(racine approchée) ≈ 0,
 *     racine négative − 1 pour (X + 1)³ depuis x0 = − 0.5,
 *     exception si la dérivée est nulle au point de départ.
 *
 * chercherToutesLesRacines() :
 *     Polynôme constant — tableau vide,
 *     polynôme sans racine réelle — tableau vide,
 *     linéaire — une racine ≈ 2.0,
 *     quadratique — deux racines ≈ 2.0 et 3.0,
 *     cubique — trois racines ≈ 1.0, 2.0, 3.0,
 *     racine double — une seule racine distincte,
 *     chaque racine trouvée annule P,
 *     aucune racine hors de l'intervalle de recherche.
 *
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
class TestSuiteSturm {

    private SuiteSturm sturm;

    /** P(X) = 0 — polynôme nul */
    private Polynome pNul;
    /** P(X) = 5 — polynôme constant, aucune racine réelle */
    private Polynome pConstante;
    /** P(X) = X − 2  (racine simple : 2) */
    private Polynome pLineaire;
    /** P(X) = X² − 5X + 6  (racines simples : 2 et 3) */
    private Polynome pQuadDeuxRacines;
    /** P(X) = X² + 1  (aucune racine réelle) */
    private Polynome pQuadSansRacine;
    /** P(X) = (X − 1)²  = X² − 2X + 1  (racine double : 1) */
    private Polynome pRacineDouble;
    /** P(X) = (X + 1)³ = X³ + 3X² + 3X + 1  (racine triple : −1) */
    private Polynome pRacineTriple;
    /** P(X) = X³ − 6X² + 11X − 6  (racines simples : 1, 2, 3) */
    private Polynome pCubique;

    @BeforeEach
    void setUp() throws Exception {
        sturm = new SuiteSturm();

        pNul             = new Polynome();
        pConstante       = new Polynome(new double[]{5.0});
        pLineaire        = new Polynome(new double[]{-2.0, 1.0});
        pQuadDeuxRacines = new Polynome(new double[]{6.0, -5.0, 1.0});
        pQuadSansRacine  = new Polynome(new double[]{1.0, 0.0, 1.0});
        pRacineDouble    = new Polynome(new double[]{1.0, -2.0, 1.0});
        pRacineTriple    = new Polynome(new double[]{1.0, 3.0, 3.0, 1.0});
        pCubique         = new Polynome(new double[]{-6.0, 11.0, -6.0, 1.0});
    }


    /**
     * Couverture : sans racine multiple, racine double, racine triple,
     * racine conservée après suppression.
     */
    @Test
    final void testSupprimerRacinesMultiples() {
        // 1. Polynôme sans racine multiple : degré inchangé
        Polynome p0SansMultiple = sturm.supprimerRacinesMultiples(pQuadDeuxRacines);
        assertEquals(2, p0SansMultiple.getDegre(),
                "Valeur attendue : 2 | Valeur obtenue : "
                + p0SansMultiple.getDegre());

        // 2. Racine double (X−1)² : P0 doit être de degré 1
        Polynome p0Double = sturm.supprimerRacinesMultiples(pRacineDouble);
        assertEquals(1, p0Double.getDegre(),
                "Valeur attendue : 1 | Valeur obtenue : "
                + p0Double.getDegre());

        // 3. Racine triple (X+1)³ : P0 doit être de degré 1
        Polynome p0Triple = sturm.supprimerRacinesMultiples(pRacineTriple);
        assertEquals(1, p0Triple.getDegre(),
                "Valeur attendue : 1 | Valeur obtenue : "
                + p0Triple.getDegre());

        // 4. Racine double conservée : P0 doit encore s'annuler en 1
        assertEquals(0.0, p0Double.evaluer(1.0), 1e-9,
                "Valeur attendue : 0.0 | Valeur obtenue : "
                + p0Double.evaluer(1.0));

        // 5. Racine triple conservée : P0 doit encore s'annuler en −1
        assertEquals(0.0, p0Triple.evaluer(-1.0), 1e-9,
                "Valeur attendue : 0.0 | Valeur obtenue : "
                + p0Triple.evaluer(-1.0));
    }

    /**
     * Couverture : polynôme constant — retourné tel quel (degré 0).
     */
    @Test
    final void testSupprimerRacinesMultiplesConstant() {
        Polynome p0Const = sturm.supprimerRacinesMultiples(pConstante);
        assertEquals(0, p0Const.getDegre(),
                "Valeur attendue : 0 | Valeur obtenue : "
                + p0Const.getDegre());
    }

    /**
     * Couverture : linéaire — 2 éléments, premier = P0, deuxième = P0'.
     */
    @Test
    final void testConstruireSuiteLineaire() {
        Polynome[] suite = sturm.construireSuite(pLineaire);

        // 1. La suite d'un linéaire contient exactement 2 éléments
        assertEquals(2, suite.length,
                "Valeur attendue : 2 | Valeur obtenue : "
                + suite.length);

        // 2. Premier élément = P0 (degré 1)
        assertEquals(1, suite[0].getDegre(),
                "Valeur attendue : 1 | Valeur obtenue : "
                + suite[0].getDegre());

        // 3. Deuxième élément = P0' (degré 0, constante)
        assertEquals(0, suite[1].getDegre(),
                "Valeur attendue : 0 | Valeur obtenue : "
                + suite[1].getDegre());
    }

    /**
     * Couverture : quadratique à 2 racines — 3 éléments, degrés décroissants.
     */
    @Test
    final void testConstruireSuiteQuadratique() {
        Polynome p0 = sturm.supprimerRacinesMultiples(pQuadDeuxRacines);
        Polynome[] suite = sturm.construireSuite(p0);

        // 1. La suite d'un quadratique à 2 racines simples a 3 éléments
        assertEquals(3, suite.length,
                "Valeur attendue : 3 | Valeur obtenue : "
                + suite.length);

        // 2. Degrés strictement décroissants : deg(P0) > deg(P1) > deg(P2)
        assertTrue(suite[0].getDegre() > suite[1].getDegre(),
                "deg(P0) doit être strictement supérieur à deg(P1).");
        assertTrue(suite[1].getDegre() > suite[2].getDegre(),
                "deg(P1) doit être strictement supérieur à deg(P2).");
    }

    /**
     * Couverture : cubique à 3 racines — aucun élément intermédiaire nul.
     */
    @Test
    final void testConstruireSuiteAucunElementNul() {
        Polynome[] suite = sturm.construireSuite(pCubique);

        // Aucun élément (sauf éventuellement le dernier) ne doit être nul
        for (int i = 0; i < suite.length - 1; i++) {
            assertFalse(suite[i].estNul(),
                    "L'élément d'indice " + i + " ne doit pas être le polynôme nul.");
        }
    }

    /**
     * Couverture : suite de longueur 1, tous positifs, alternance +−+.
     */
    @Test
    final void testCompterChangementsDeSignes() {
        // 1. Suite de longueur 1 : 0 changement
        Polynome[] suiteUn = {pLineaire};
        int resUn = sturm.compterChangementsDeSignes(suiteUn, 0.0);
        assertEquals(0, resUn,
                "Valeur attendue : 0 | Valeur obtenue : " + resUn);

        // 2. Tous les termes de même signe : 0 changement
        Polynome[] suitePos = {
            new Polynome(new double[]{1.0}),
            new Polynome(new double[]{2.0}),
            new Polynome(new double[]{3.0})
        };
        int resPos = sturm.compterChangementsDeSignes(suitePos, 0.0);
        assertEquals(0, resPos,
                "Valeur attendue : 0 | Valeur obtenue : " + resPos);

        // 3. Alternance stricte +−+ : 2 changements
        Polynome[] suiteAlt = {
            new Polynome(new double[]{ 1.0}),
            new Polynome(new double[]{-1.0}),
            new Polynome(new double[]{ 1.0})
        };
        int resAlt = sturm.compterChangementsDeSignes(suiteAlt, 0.0);
        assertEquals(2, resAlt,
                "Valeur attendue : 2 | Valeur obtenue : " + resAlt);
    }

    /**
     * Couverture : valeur nulle ignorée dans le décompte.
     */
    @Test
    final void testCompterChangementsDeSignesValeurNulle() {
        // Suite [+1, 0, +1] : le 0 est ignoré → 0 changement
        Polynome[] suiteZeroMilieu = {
            new Polynome(new double[]{1.0}),
            pNul,
            new Polynome(new double[]{1.0})
        };
        int resIgnore = sturm.compterChangementsDeSignes(suiteZeroMilieu, 0.0);
        assertEquals(0, resIgnore,
                "Valeur attendue : 0 | Valeur obtenue : " + resIgnore);

        // Suite [+1, 0, −1] : le 0 est ignoré → 1 changement
        Polynome[] suiteZeroEntre = {
            new Polynome(new double[]{ 1.0}),
            pNul,
            new Polynome(new double[]{-1.0})
        };
        int resAvecZero = sturm.compterChangementsDeSignes(suiteZeroEntre, 0.0);
        assertEquals(1, resAvecZero,
                "Valeur attendue : 1 | Valeur obtenue : " + resAvecZero);
    }
    
    /**
     * Couverture : constant, sans racine, linéaire, linéaire hors intervalle,
     * quadratique, cubique, racine double, intervalle vide de racines.
     */
    @Test
    final void testNombreRacinesIntervalle() {
        // 1. Polynôme constant : 0 racine dans tout intervalle
        int resConst = sturm.nombreRacinesIntervalle(pConstante, -10.0, 10.0);
        assertEquals(0, resConst,
                "Valeur attendue : 0 | Valeur obtenue : " + resConst);

        // 2. X² + 1, sans racine réelle : 0 racine
        int resSansRacine = sturm.nombreRacinesIntervalle(pQuadSansRacine, -100.0, 100.0);
        assertEquals(0, resSansRacine,
                "Valeur attendue : 0 | Valeur obtenue : " + resSansRacine);

        // 3. X − 2 dans ]−10, 10[ : 1 racine
        int resLin = sturm.nombreRacinesIntervalle(pLineaire, -10.0, 10.0);
        assertEquals(1, resLin,
                "Valeur attendue : 1 | Valeur obtenue : " + resLin);

        // 4. X − 2 dans ]3, 10[ (racine hors intervalle) : 0 racine
        int resLinHors = sturm.nombreRacinesIntervalle(pLineaire, 3.0, 10.0);
        assertEquals(0, resLinHors,
                "Valeur attendue : 0 | Valeur obtenue : " + resLinHors);

        // 5. X² − 5X + 6 dans ]1, 4[ : 2 racines (x=2 et x=3)
        int resQuad2 = sturm.nombreRacinesIntervalle(pQuadDeuxRacines, 1.0, 4.0);
        assertEquals(2, resQuad2,
                "Valeur attendue : 2 | Valeur obtenue : " + resQuad2);

        // 6. X² − 5X + 6 dans ]1, 2.5[ : 1 seule racine (x=2)
        int resQuad1 = sturm.nombreRacinesIntervalle(pQuadDeuxRacines, 1.0, 2.5);
        assertEquals(1, resQuad1,
                "Valeur attendue : 1 | Valeur obtenue : " + resQuad1);

        // 7. Cubique (racines 1, 2, 3) dans ]0, 4[ : 3 racines
        int resCub = sturm.nombreRacinesIntervalle(pCubique, 0.0, 4.0);
        assertEquals(3, resCub,
                "Valeur attendue : 3 | Valeur obtenue : " + resCub);

        // 8. Racine double (X−1)² : comptée une seule fois dans ]−1, 2[
        int resDouble = sturm.nombreRacinesIntervalle(pRacineDouble, -1.0, 2.0);
        assertEquals(1, resDouble,
                "Valeur attendue : 1 | Valeur obtenue : " + resDouble);

        // 9. Cubique dans un intervalle sans racine ]10, 100[ : 0 racine
        int resVide = sturm.nombreRacinesIntervalle(pCubique, 10.0, 100.0);
        assertEquals(0, resVide,
                "Valeur attendue : 0 | Valeur obtenue : " + resVide);
    }

    /**
     * Couverture : exception si a > b.
     */
    @Test
    final void testNombreRacinesIntervalleExceptionASupB() {
        assertThrows(IllegalArgumentException.class, () -> {
            sturm.nombreRacinesIntervalle(pLineaire, 5.0, 3.0);
        }, "Une IllegalArgumentException doit être levée si a > b.");
    }

    /**
     * Couverture : exception si a = b.
     */
    @Test
    final void testNombreRacinesIntervalleExceptionAEgalB() {
        assertThrows(IllegalArgumentException.class, () -> {
            sturm.nombreRacinesIntervalle(pLineaire, 2.0, 2.0);
        }, "Une IllegalArgumentException doit être levée si a = b.");
    }
    /**
     * Couverture : nombre d'intervalles, chaque intervalle contient 1 racine,
     * intervalles bien ordonnés, racines connues couvertes.
     */
    @Test
    final void testLocaliserRacines() {
        List<double[]> res = sturm.localiserRacines(pCubique, -1.0, 5.0, 0.5);

        // 1. 3 racines simples → 3 intervalles isolants
        assertEquals(3, res.size(),
                "Valeur attendue : 3 | Valeur obtenue : " + res.size());

        // 2. Chaque intervalle retourné contient exactement 1 racine (Sturm)
        for (double[] ab : res) {
            int nbRacines = sturm.nombreRacinesIntervalle(pCubique, ab[0], ab[1]);
            assertEquals(1, nbRacines,
                    "Valeur attendue : 1 | Valeur obtenue : " + nbRacines
                    + " pour l'intervalle [" + ab[0] + ", " + ab[1] + "]");
        }

        // 3. Chaque intervalle est bien ordonné : a < b
        for (double[] ab : res) {
            assertTrue(ab[0] < ab[1],
                    "Chaque intervalle doit vérifier a < b.");
        }

        // 4. Les racines connues (1, 2, 3) sont couvertes par les intervalles
        double[] racinesConnues = {1.0, 2.0, 3.0};
        for (double r : racinesConnues) {
            boolean couverte = res.stream().anyMatch(ab -> ab[0] <= r && r <= ab[1]);
            assertTrue(couverte,
                    "La racine " + r + " doit être couverte par un intervalle retourné.");
        }
    }

    /**
     * Couverture : polynôme sans racine réelle — liste vide.
     */
    @Test
    final void testLocaliserRacinesSansRacine() {
        List<double[]> res = sturm.localiserRacines(pQuadSansRacine, -10.0, 10.0, 0.5);
        assertEquals(0, res.size(),
                "Valeur attendue : 0 | Valeur obtenue : " + res.size());
    }

    /**
     * Couverture : linéaire, deux racines d'un quadratique, racine négative,
     * P(a) = 0, P(b) = 0, P(racine) ≈ 0.
     */
    @Test
    final void testApproximerParDichotomie() {
        final double PRECISION = 1e-9;

        // 1. Racine de X − 2 en ]1, 3[ ≈ 2.0
        double r1 = sturm.approximerParDichotomie(pLineaire, 1.0, 3.0, PRECISION);
        assertEquals(2.0, r1, PRECISION,
                "Valeur attendue : 2.0 | Valeur obtenue : " + r1);

        // 2. Racine 2 de X² − 5X + 6 en ]1, 2.5[ ≈ 2.0
        double r2 = sturm.approximerParDichotomie(pQuadDeuxRacines, 1.0, 2.5, PRECISION);
        assertEquals(2.0, r2, PRECISION,
                "Valeur attendue : 2.0 | Valeur obtenue : " + r2);

        // 3. Racine 3 de X² − 5X + 6 en ]2.5, 4[ ≈ 3.0
        double r3 = sturm.approximerParDichotomie(pQuadDeuxRacines, 2.5, 4.0, PRECISION);
        assertEquals(3.0, r3, PRECISION,
                "Valeur attendue : 3.0 | Valeur obtenue : " + r3);

        // 4. P(racine approchée) doit être ≈ 0
        double rCub = sturm.approximerParDichotomie(pCubique, 0.5, 1.5, PRECISION);
        assertEquals(0.0, pCubique.evaluer(rCub), 1e-6,
                "Valeur attendue : 0.0 | Valeur obtenue : "
                + pCubique.evaluer(rCub));

        // 5. P(a) = 0 : doit retourner a directement
        double rPa = sturm.approximerParDichotomie(pQuadDeuxRacines, 2.0, 3.5, PRECISION);
        assertEquals(2.0, rPa, PRECISION,
                "Valeur attendue : 2.0 | Valeur obtenue : " + rPa);

        // 6. P(b) = 0 : doit retourner b directement
        double rPb = sturm.approximerParDichotomie(pQuadDeuxRacines, 1.5, 3.0, PRECISION);
        assertEquals(3.0, rPb, PRECISION,
                "Valeur attendue : 3.0 | Valeur obtenue : " + rPb);

        // 7. Racine négative − 1 pour (X+1)³ en ]−2, 0[
        double rNeg = sturm.approximerParDichotomie(pRacineTriple, -2.0, 0.0, PRECISION);
        assertEquals(-1.0, rNeg, PRECISION,
                "Valeur attendue : -1.0 | Valeur obtenue : " + rNeg);
    }

    /**
     * Couverture : exception si P(a) et P(b) ont le même signe.
     */
    @Test
    final void testApproximerParDichotomieExceptionMemeSigne() {
        // P(4) et P(5) sont tous deux positifs pour X² − 5X + 6
        assertThrows(IllegalArgumentException.class, () -> {
            sturm.approximerParDichotomie(pQuadDeuxRacines, 4.0, 5.0, 1e-9);
        }, "Une IllegalArgumentException doit être levée si P(a) et P(b) ont le même signe.");
    }

    /**
     * Couverture : linéaire, deux racines d'un quadratique, racine négative,
     * P(racine) ≈ 0.
     */
    @Test
    final void testApproximerParNewton() {
        final double PRECISION    = 1e-9;
        final int    MAX_ITER     = 100;

        // 1. Racine de X − 2 depuis x0 = 0 ≈ 2.0
        double r1 = sturm.approximerParNewton(pLineaire, 0.0, PRECISION, MAX_ITER);
        assertEquals(2.0, r1, PRECISION,
                "Valeur attendue : 2.0 | Valeur obtenue : " + r1);

        // 2. Racine 2 de X² − 5X + 6 depuis x0 = 1.5 ≈ 2.0
        double r2 = sturm.approximerParNewton(pQuadDeuxRacines, 1.5, PRECISION, MAX_ITER);
        assertEquals(2.0, r2, PRECISION,
                "Valeur attendue : 2.0 | Valeur obtenue : " + r2);

        // 3. Racine 3 de X² − 5X + 6 depuis x0 = 3.5 ≈ 3.0
        double r3 = sturm.approximerParNewton(pQuadDeuxRacines, 3.5, PRECISION, MAX_ITER);
        assertEquals(3.0, r3, PRECISION,
                "Valeur attendue : 3.0 | Valeur obtenue : " + r3);

        // 4. P(racine approchée) doit être ≈ 0 pour un cubique
        double rCub = sturm.approximerParNewton(pCubique, 2.5, PRECISION, MAX_ITER);
        assertEquals(0.0, pCubique.evaluer(rCub), 1e-6,
                "Valeur attendue : 0.0 | Valeur obtenue : "
                + pCubique.evaluer(rCub));

        // 5. Racine négative − 1 pour (X+1)³ depuis x0 = −0.5
        double rNeg = sturm.approximerParNewton(pRacineTriple, -0.5, PRECISION, MAX_ITER);
        assertEquals(-1.0, rNeg, 1e-3,
                "Valeur attendue : -1.0 | Valeur obtenue : " + rNeg);
    }

    /**
     * Couverture : exception si la dérivée est nulle en x0.
     */
    @Test
    final void testApproximerParNewtonExceptionDeriveeNulle() {
        // P(X) = X² + 1  ->  P'(X) = 2X
        // En x0 = 0.0 : P(0) = 1 (on entre dans la boucle), mais P'(0) = 0 (dérivée nulle !)
        assertThrows(ArithmeticException.class, () -> {
            sturm.approximerParNewton(pQuadSansRacine, 0.0, 1e-9, 100);
        }, "Une ArithmeticException doit être levée si P'(x) = 0.");
    }


    /**
     * Couverture : constant, sans racine, linéaire, quadratique, cubique,
     * racine double, chaque racine annule P, aucune racine hors intervalle.
     */
    @Test
    final void testChercherToutesLesRacines() {
        final double PRECISION = 1e-9;
        final double PAS       = 0.5;

        // 1. Polynôme constant → tableau vide
        double[] resConst = sturm.chercherToutesLesRacines(
                pConstante, -10.0, 10.0, PAS, PRECISION);
        assertEquals(0, resConst.length,
                "Valeur attendue : 0 | Valeur obtenue : " + resConst.length);

        // 2. X² + 1, sans racine réelle → tableau vide
        double[] resSans = sturm.chercherToutesLesRacines(
                pQuadSansRacine, -50.0, 50.0, PAS, PRECISION);
        assertEquals(0, resSans.length,
                "Valeur attendue : 0 | Valeur obtenue : " + resSans.length);

        // 3. X − 2 → 1 racine ≈ 2.0
        double[] resLin = sturm.chercherToutesLesRacines(
                pLineaire, -10.0, 10.0, PAS, PRECISION);
        assertEquals(1, resLin.length,
                "Valeur attendue : 1 | Valeur obtenue : " + resLin.length);
        assertEquals(2.0, resLin[0], PRECISION,
                "Valeur attendue : 2.0 | Valeur obtenue : " + resLin[0]);

        // 4. X² − 5X + 6 → 2 racines ≈ 2.0 et 3.0
        double[] resQuad = sturm.chercherToutesLesRacines(
                pQuadDeuxRacines, 0.0, 5.0, PAS, PRECISION);
        Arrays.sort(resQuad);
        assertEquals(2, resQuad.length,
                "Valeur attendue : 2 | Valeur obtenue : " + resQuad.length);
        assertEquals(2.0, resQuad[0], PRECISION,
                "Valeur attendue : 2.0 | Valeur obtenue : " + resQuad[0]);
        assertEquals(3.0, resQuad[1], PRECISION,
                "Valeur attendue : 3.0 | Valeur obtenue : " + resQuad[1]);

        // 5. Cubique (racines 1, 2, 3) → 3 racines
        double[] resCub = sturm.chercherToutesLesRacines(
                pCubique, -1.0, 5.0, PAS, PRECISION);
        Arrays.sort(resCub);
        assertEquals(3, resCub.length,
                "Valeur attendue : 3 | Valeur obtenue : " + resCub.length);
        assertEquals(1.0, resCub[0], PRECISION,
                "Valeur attendue : 1.0 | Valeur obtenue : " + resCub[0]);
        assertEquals(2.0, resCub[1], PRECISION,
                "Valeur attendue : 2.0 | Valeur obtenue : " + resCub[1]);
        assertEquals(3.0, resCub[2], PRECISION,
                "Valeur attendue : 3.0 | Valeur obtenue : " + resCub[2]);
    }

    /**
     * Couverture : racine double — une seule racine distincte retournée.
     */
    @Test
    final void testChercherToutesLesRacinesRacineDouble() {
        double[] res = sturm.chercherToutesLesRacines(
                pRacineDouble, -2.0, 3.0, 0.5, 1e-9);

        // 1. Une seule racine distincte (la multiplicité est ignorée)
        assertEquals(1, res.length,
                "Valeur attendue : 1 | Valeur obtenue : " + res.length);

        // 2. Cette racine est bien ≈ 1.0
        assertEquals(1.0, res[0], 1e-9,
                "Valeur attendue : 1.0 | Valeur obtenue : " + res[0]);
    }

    /**
     * Couverture : chaque racine trouvée annule bien P.
     */
    @Test
    final void testChercherToutesLesRacinesAnnulentP() {
        double[] racines = sturm.chercherToutesLesRacines(
                pCubique, -1.0, 5.0, 0.5, 1e-9);

        for (double r : racines) {
            assertEquals(0.0, pCubique.evaluer(r), 1e-6,
                    "Valeur attendue : 0.0 | Valeur obtenue : "
                    + pCubique.evaluer(r) + " pour la racine r = " + r);
        }
    }

    /**
     * Couverture : aucune racine retournée hors de l'intervalle de recherche.
     */
    @Test
    final void testChercherToutesLesRacinesHorsIntervalle() {
        // X² − 5X + 6 a des racines en 2 et 3 ; on cherche dans ]4, 10[
        double[] res = sturm.chercherToutesLesRacines(
                pQuadDeuxRacines, 4.0, 10.0, 0.5, 1e-9);
        assertEquals(0, res.length,
                "Valeur attendue : 0 | Valeur obtenue : " + res.length);
    }
}