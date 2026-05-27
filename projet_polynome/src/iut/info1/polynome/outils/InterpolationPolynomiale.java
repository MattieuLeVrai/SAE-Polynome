/*
 * InterpolationPolynomiale                                         22/05/26 
 * Iut de rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome.outils;

import iut.info1.polynome.OperationPolynome;
import iut.info1.polynome.Polynome;

/**
 * Fournit des outils pour calculer le polynôme d'interpolation unique 
 * de degré au plus n-1 passant par un ensemble de n points donnés 
 * en utilisant la méthode des polynômes de Lagrange.
 * * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
public class InterpolationPolynomiale {

    /**
     * Calcule le polynôme d'interpolation de Lagrange passant par les points (x_i, y_i).
     * <p>
     * La formule utilisée est : P(X) = Σ (y_i * L_i(X))
     * où L_i(X) est le polynôme de base de Lagrange associé à l'abscisse x_i :
     * L_i(X) = Π_j≠i ((X - x_j) / (x_i - x_j))
     * </p>
     * * @param x Tableau des abscisses des points (doivent toutes être distinctes)
     * @param y Tableau des ordonnées des points
     * @return Le polynôme d'interpolation unique sous forme d'une instance de {@link Polynome}
     * @throws IllegalArgumentException si les tableaux sont null, vides, de tailles différentes,
     * ou si deux abscisses sont identiques.
     */
    public Polynome interpolerLagrange(double[] x, double[] y) {
        // 1. Validations de sécurité sur les données d'entrée
        if (x == null || y == null) {
            throw new IllegalArgumentException("Les tableaux de points ne peuvent pas être null.");
        }
        if (x.length != y.length) {
            throw new IllegalArgumentException("Les tableaux x et y doivent avoir la même taille.");
        }
        if (x.length == 0) {
            throw new IllegalArgumentException("Il faut au moins un point pour effectuer une interpolation.");
        }

        int nombreDePoints = x.length;

        // 2. Vérification que toutes les abscisses x_i sont distinctes
        for (int i = 0; i < nombreDePoints; i++) {
            for (int j = i + 1; j < nombreDePoints; j++) {
                if (Math.abs(x[i] - x[j]) < 1e-12) {
                    throw new IllegalArgumentException("Les abscisses (x) doivent toutes être distinctes "
                            + "pour pouvoir réaliser l'interpolation de Lagrange.");
                }
            }
        }

        // Cas particulier : un seul point (x0, y0) -> donne le polynôme constant P(X) = y0
        if (nombreDePoints == 1) {
            return new Polynome(new double[]{y[0]});
        }

        OperationPolynome calculateur = new OperationPolynome();
        Polynome polynomeResultat = new Polynome(); // Initialisé au polynôme nul P(X) = 0.0

        // 3. Application de la méthode de Lagrange
        for (int i = 0; i < nombreDePoints; i++) {
            
            // On initialise le polynôme de base L_i(X) à la constante 1.0
            Polynome li = new Polynome(new double[]{1.0});

            for (int j = 0; j < nombreDePoints; j++) {
                if (i != j) {
                    // Création du monôme numérateur (X - x_j) 
                    // En tableau de coefficients (croissants) : [-x_j, 1.0] car -x_j*X^0 + 1.0*X^1
                    Polynome numerateur = new Polynome(new double[]{-x[j], 1.0});
                    
                    // li = li * (X - x_j)
                    li = calculateur.multiplication(li, numerateur);
                    
                    // Calcul du dénominateur (x_i - x_j)
                    double denominateur = x[i] - x[j];
                    
                    // li = li * (1 / (x_i - x_j))
                    li = calculateur.multiplicationScalaire(li, 1.0 / denominateur);
                }
            }

            // On multiplie le polynôme de base L_i(X) obtenu par son ordonnée y_i
            Polynome termeCourant = calculateur.multiplicationScalaire(li, y[i]);

            // On l'ajoute au polynôme final : P(X) = P(X) + (y_i * L_i(X))
            polynomeResultat = calculateur.addition(polynomeResultat, termeCourant);
        }

        return polynomeResultat;
    }
}