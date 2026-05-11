/*
 * OperationPolynome                                   11/05/26 
 * Iut de rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome;

import iut.info1.polynome.Polynome;

/**
 * Fournit les principales opérations algébriques sur les polynômes de IR[X] :
 * addition, soustraction, multiplication, division euclidienne, dérivation,
 * intégration, calcul d'image et valeur moyenne sur un intervalle.
 *
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
public class OperationPolynome {

    /**
     * Additionne deux polynômes P1 et P2.
     * Le degré du résultat est au plus max(deg P1, deg P2).
     *
     * @param p1 Premier polynôme
     * @param p2 Deuxième polynôme
     * @return Le polynôme P1 + P2
     */
    public Polynome addition(Polynome p1, Polynome p2) {
        int degre = Math.max(p1.getDegre(), p2.getDegre());
        double[] coeffs = new double[degre + 1];

        for (int i = 0; i <= degre; i++) {
            coeffs[i] = p1.getCoefficient(i) + p2.getCoefficient(i);
        }

        return new Polynome(coeffs);
    }

    /**
     * Soustrait le polynôme P2 du polynôme P1.
     * Le degré du résultat est au plus max(deg P1, deg P2).
     *
     * @param p1 Polynôme dont on soustrait
     * @param p2 Polynôme à soustraire
     * @return Le polynôme P1 - P2
     */
    public Polynome soustraction(Polynome p1, Polynome p2) {
        int degre = Math.max(p1.getDegre(), p2.getDegre());
        double[] coeffs = new double[degre + 1];

        for (int i = 0; i <= degre; i++) {
            coeffs[i] = p1.getCoefficient(i) - p2.getCoefficient(i);
        }

        return new Polynome(coeffs);
    }

    /**
     * Multiplie deux polynômes P1 et P2.
     * Le degré du résultat est deg(P1) + deg(P2).
     *
     * @param p1 Premier polynôme
     * @param p2 Deuxième polynôme
     * @return Le polynôme P1 × P2
     */
    public Polynome multiplication(Polynome p1, Polynome p2) {
        if (p1.estNul() || p2.estNul()) {
            return new Polynome(); // 0 × P = 0
        }

        int degre1 = p1.getDegre();
        int degre2 = p2.getDegre();
        double[] coeffs = new double[degre1 + degre2 + 1];

        for (int i = 0; i <= degre1; i++) {
            for (int j = 0; j <= degre2; j++) {
                coeffs[i + j] += p1.getCoefficient(i) * p2.getCoefficient(j);
            }
        }

        return new Polynome(coeffs);
    }

    /**
     * Effectue la division euclidienne de P1 par P2 (suivant les puissances décroissantes)
     * et retourne le quotient Q tel que P1 = P2 × Q + R avec deg(R) < deg(P2).
     *
     * Algorithme du cours :
     *   R ← P1 ; Q ← 0 ; bmax ← coeff dominant de P2
     *   Tant que deg(R) >= deg(P2) :
     *       α ← coeff dominant de R / bmax
     *       Q ← Q + α * X^(deg(R)-deg(P2))
     *       R ← R - α * X^(deg(R)-deg(P2)) * P2
     *
     * @param p1 Polynôme dividende
     * @param p2 Polynôme diviseur (doit être non nul)
     * @return Le quotient Q de la division euclidienne
     * @throws IllegalArgumentException si le diviseur est le polynôme nul
     */
    public Polynome division(Polynome p1, Polynome p2) {
        if (p2.estNul()) {
            throw new IllegalArgumentException("Division par le polynôme nul impossible.");
        }

        int degreP1 = p1.getDegre();
        int degreP2 = p2.getDegre();

        // Si le degré du dividende est inférieur à celui du diviseur, le quotient est nul
        if (degreP1 < degreP2) {
            return new Polynome();
        }

        // Copie des coefficients de R (initialement = P1)
        double[] r = new double[degreP1 + 1];
        for (int i = 0; i <= degreP1; i++) {
            r[i] = p1.getCoefficient(i);
        }

        double[] q = new double[degreP1 - degreP2 + 1];
        double bmax = p2.getCoefficient(degreP2);
        int degreR = degreP1;

        while (degreR >= degreP2) {
            // α = coeff dominant de R / coeff dominant de P2
            double alpha = r[degreR] / bmax;
            int exposant = degreR - degreP2;

            // Q += α * X^exposant
            q[exposant] += alpha;

            // R -= α * X^exposant * P2
            for (int i = 0; i <= degreP2; i++) {
                r[i + exposant] -= alpha * p2.getCoefficient(i);
            }

            // Mise à jour du degré de R (on retire les coefficients nuls en tête)
            while (degreR > 0 && Math.abs(r[degreR]) < 1e-10) {
                r[degreR] = 0.0;
                degreR--;
            }

            // Si R est réduit au terme constant nul, on s'arrête
            if (degreR == 0 && Math.abs(r[0]) < 1e-10) {
                break;
            }
        }

        // Nettoyage des zéros numériques en tête du quotient
        int degreQ = q.length - 1;
        while (degreQ > 0 && Math.abs(q[degreQ]) < 1e-10) {
            degreQ--;
        }

        double[] qTrimme = new double[degreQ + 1];
        System.arraycopy(q, 0, qTrimme, 0, degreQ + 1);

        return new Polynome(qTrimme);
    }

    /**
     * Calcule le polynôme dérivé de P.
     * Si P = a0 + a1*X + ... + an*X^n, alors P' = a1 + 2*a2*X + ... + n*an*X^(n-1).
     *
     * @param p Le polynôme à dériver
     * @return Le polynôme dérivé P'
     */
    public Polynome derivee(Polynome p) {
        int degre = p.getDegre();

        // La dérivée d'une constante est le polynôme nul
        if (degre == 0) {
            return new Polynome();
        }

        double[] coeffs = new double[degre];
        for (int i = 1; i <= degre; i++) {
            coeffs[i - 1] = i * p.getCoefficient(i);
        }

        return new Polynome(coeffs);
    }

    /**
     * Calcule l'image de x par le polynôme P, soit p(x).
     * Utilise l'algorithme de Horner pour minimiser le nombre d'opérations :
     * n multiplications et n additions au lieu de n(n+1)/2 multiplications naïves.
     *
     * Exemple : P = a0 + a1*X + a2*X^2 + a3*X^3
     *   => p(x) = a0 + x*(a1 + x*(a2 + x*a3))
     *
     * @param p Le polynôme
     * @param x La valeur à évaluer
     * @return La valeur p(x)
     */
    public double calculImageFonction(Polynome p, double x) {
        int degre = p.getDegre();

        // Initialisation avec le coefficient dominant (algorithme de Horner)
        double resultat = p.getCoefficient(degre);

        for (int i = degre - 1; i >= 0; i--) {
            resultat = resultat * x + p.getCoefficient(i);
        }

        return resultat;
    }

    /**
     * Calcule la valeur moyenne de la fonction polynômiale associée à P sur [a, b].
     * Formule : (1 / (b - a)) * intégrale de a à b de p(x) dx
     *
     * @param p Le polynôme
     * @param a Borne inférieure de l'intervalle
     * @param b Borne supérieure de l'intervalle (b doit être différent de a)
     * @return La valeur moyenne de p sur [a, b]
     * @throws IllegalArgumentException si a == b
     */
    public double calculValeurMoyenneIntervalle(Polynome p, double a, double b) {
        if (a == b) {
            throw new IllegalArgumentException("Les bornes a et b doivent être distinctes.");
        }

        return integrationPolynome(p, a, b) / (b - a);
    }

    /**
     * Calcule l'intégrale définie de P sur [a, b] (intégrale de Riemann).
     * On calcule d'abord la primitive F de P :
     *   si P = a0 + a1*X + ... + an*X^n
     *   alors F = a0*X + (a1/2)*X^2 + ... + (an/(n+1))*X^(n+1)
     * Puis on retourne F(b) - F(a).
     *
     * @param p Le polynôme à intégrer
     * @param a Borne inférieure
     * @param b Borne supérieure
     * @return La valeur de l'intégrale définie de p entre a et b
     */
    public double integrationPolynome(Polynome p, double a, double b) {
        int degre = p.getDegre();

        // Construction de la primitive F (la constante d'intégration est ignorée car elle s'annule)
        double[] coeffsPrimitive = new double[degre + 2];
        coeffsPrimitive[0] = 0.0; // constante d'intégration = 0

        for (int i = 0; i <= degre; i++) {
            coeffsPrimitive[i + 1] = p.getCoefficient(i) / (i + 1);
        }

        Polynome primitive = new Polynome(coeffsPrimitive);

        // Intégrale = F(b) - F(a)
        return primitive.evaluer(b) - primitive.evaluer(a);
    }
}