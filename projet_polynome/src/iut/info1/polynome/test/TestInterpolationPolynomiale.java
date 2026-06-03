/*
 * TestInterpolationPolynomiale                                    22/05/26
 * IUT de Rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import iut.info1.polynome.Polynome;
import iut.info1.polynome.outils.InterpolationPolynomiale;

/**
 * Classe de validation unitaire de la classe {@link InterpolationPolynomiale}.
 * Tests réalisés en boîte noire : seul le comportement décrit dans la 
 * Javadoc et les spécifications est vérifié, sans hypothèse sur l'implémentation interne.
 * Plan de test (Couverture des cas d'utilisation et limites) :
 * interpolerLagrange() :
 * - Cas nominal standard : 3 points distincts alignés ou formant une parabole.
 * - Cas limite à 1 seul point : génère un polynôme constant P(X) = y0.
 * - Cas d'erreurs et exceptions :
 * - Tableaux null (x ou y).
 * - Tableaux vides.
 * - Tableaux de tailles différentes.
 * - Abscisses (x) identiques ou confondues (division par zéro théorique).
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
class TestInterpolationPolynomiale {

    private InterpolationPolynomiale outilInterpolation;

    @BeforeEach
    void setUp() {
        outilInterpolation = new InterpolationPolynomiale();
    }

    /**
     * Couverture : Cas nominal standard (3 points formant la parabole P(X) = X² + X + 1)
     * Points utilisés : A(0, 1), B(1, 3), C(2, 7)
     */
    @Test
    final void testInterpolerLagrangeNominal() {
        double[] x = {0.0, 1.0, 2.0};
        double[] y = {1.0, 3.0, 7.0};

        Polynome resultat = outilInterpolation.interpolerLagrange(x, y);

        assertNotNull(resultat, "Le polynôme de résultat ne doit pas être null.");
        
        // Degré attendu : au plus n-1 = 2
        assertEquals(2, resultat.getDegre(), 
                "Valeur attendue : 2 | Valeur obtenue : " + resultat.getDegre());

        // Vérification des coefficients de X² + X + 1 -> [1.0, 1.0, 1.0]
        assertEquals(1.0, resultat.getCoefficient(0), 1e-9,
                "Valeur attendue (a0) : 1.0 | Valeur obtenue : " + resultat.getCoefficient(0));
        assertEquals(1.0, resultat.getCoefficient(1), 1e-9,
                "Valeur attendue (a1) : 1.0 | Valeur obtenue : " + resultat.getCoefficient(1));
        assertEquals(1.0, resultat.getCoefficient(2), 1e-9,
                "Valeur attendue (a2) : 1.0 | Valeur obtenue : " + resultat.getCoefficient(2));
    }

    /**
     * Couverture : Cas limite avec un seul point fourni.
     * Doit renvoyer un polynôme constant P(X) = y0.
     */
    @Test
    final void testInterpolerLagrangeUnSeulPoint() {
        double[] x = {5.0};
        double[] y = {-3.5};

        Polynome resultat = outilInterpolation.interpolerLagrange(x, y);

        assertNotNull(resultat);
        assertEquals(0, resultat.getDegre(), 
                "Valeur attendue : 0 | Valeur obtenue : " + resultat.getDegre());
        assertEquals(-3.5, resultat.getCoefficient(0), 1e-9,
                "Valeur attendue : -3.5 | Valeur obtenue : " + resultat.getCoefficient(0));
    }

    /**
     * Couverture : Exception levée si l'un des tableaux est null.
     */
    @Test
    final void testInterpolerLagrangeTableauNull() {
        double[] valide = {1.0, 2.0};

        assertThrows(IllegalArgumentException.class, () -> {
            outilInterpolation.interpolerLagrange(null, valide);
        }, "Une exception doit être levée si le tableau x est null.");

        assertThrows(IllegalArgumentException.class, () -> {
            outilInterpolation.interpolerLagrange(valide, null);
        }, "Une exception doit être levée si le tableau y est null.");
    }

    /**
     * Couverture : Exception levée si les tableaux sont vides.
     */
    @Test
    final void testInterpolerLagrangeTableauVide() {
        double[] xVide = {};
        double[] yVide = {};

        assertThrows(IllegalArgumentException.class, () -> {
            outilInterpolation.interpolerLagrange(xVide, yVide);
        }, "Une exception doit être levée si les tableaux fournis sont vides.");
    }

    /**
     * Couverture : Exception levée si les tableaux x et y n'ont pas la même taille.
     */
    @Test
    final void testInterpolerLagrangeTaillesDifferentes() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {4.0, 5.0}; // Manque un point

        assertThrows(IllegalArgumentException.class, () -> {
            outilInterpolation.interpolerLagrange(x, y);
        }, "Une exception doit être levée si les tableaux x et y ont des tailles distinctes.");
    }

    /**
     * Couverture : Exception levée si deux points ont exactement la même abscisse.
     */
    @Test
    final void testInterpolerLagrangeAbscissesIdentiques() {
        double[] x = {1.0, 2.0, 2.0}; // Le 2.0 est répété
        double[] y = {4.0, 5.0, 6.0};

        assertThrows(IllegalArgumentException.class, () -> {
            outilInterpolation.interpolerLagrange(x, y);
        }, "Une exception doit être levée si deux abscisses distinctes possèdent la même valeur.");
    }
}