/*
 * OperationPolynome                                   11/05/26 
 * Iut de rodez, pas de copyright ni copyleft
 */
package iut.info1.polynome;

/**
 * Fournit les principales opérations algébriques sur les polynômes de IR[X] :
 * addition, soustraction, multiplication, division euclidienne, dérivation,
 * intégration, calcul d'image et valeur moyenne sur un intervalle.
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
public class OperationPolynome {

    /**
     * Additionne deux polynômes P1 et P2.
     * Le degré du résultat est au plus max(deg P1, deg P2).
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
     * Multiplie le polynôme P par un scalaire réel lambda.
     * @param p Le polynôme
     * @param scalaire Le scalaire multiplicateur
     * @return lambda x P
     */
    public Polynome multiplicationScalaire(Polynome p, double scalaire) {
        if (scalaire == 0.0 || p.estNul()) {
            return new Polynome();
        }
        int degre = p.getDegre();
        double[] coeffs = new double[degre + 1];
        for (int i = 0; i <= degre; i++) {
            coeffs[i] = p.getCoefficient(i) * scalaire;
        }
        return new Polynome(coeffs);
    }
    /**
     * Multiplie deux polynômes P1 et P2.
     * Le degré du résultat est deg(P1) + deg(P2).
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
     * Division euclidienne interne : retourne [quotient, reste].
     * Algorithme du cours (puissances décroissantes) :
     *   R <- P1 ; Q <- 0 ; bmax <- coeff dominant de P2
     *   Tant que deg(R) >= deg(P2) :
     *       alpha <- coeff dominant de R / bmax
     *       Q <- Q + alpha * X^(deg(R)-deg(P2))
     *       R <- R - alpha * X^(deg(R)-deg(P2)) * P2
     * @param p1 Dividende
     * @param p2 Diviseur (non nul)
     * @return tableau [quotient, reste]
     */
    private Polynome[] divisionEuclidienne(Polynome p1, Polynome p2) {
        if (p2.estNul()) {
            throw new IllegalArgumentException("Division par le polynôme"
            		                           + " nul impossible.");
        }
        int degreP1 = p1.getDegre();
        int degreP2 = p2.getDegre();
        if (degreP1 < degreP2) {
            return new Polynome[]{new Polynome(), p1};
        }
        double[] r = new double[degreP1 + 1];
        for (int i = 0; i <= degreP1; i++) {
            r[i] = p1.getCoefficient(i);
        }
        double[] q = new double[degreP1 - degreP2 + 1];
        double bmax = p2.getCoefficient(degreP2);
        int degreR = degreP1;

        while (degreR >= degreP2) {
            double alpha = r[degreR] / bmax;
            int exposant = degreR - degreP2;
            q[exposant] += alpha;
            
            for (int i = 0; i <= degreP2; i++) {
                r[i + exposant] -= alpha * p2.getCoefficient(i);
            }
            while (degreR >= 0 && Math.abs(r[degreR]) < 1e-10) {
                if (degreR >= 0) { 
                	r[degreR] = 0.0;
                }
                degreR--;
            }
        }
        int dq = q.length - 1;
        while (dq > 0 && Math.abs(q[dq]) < 1e-10) dq--;
        double[] qFinal = new double[dq + 1];
        System.arraycopy(q, 0, qFinal, 0, dq + 1);
        if (degreR < 0) {
            return new Polynome[]{new Polynome(qFinal), new Polynome()};
        }

        double[] rFinal = new double[degreR + 1];
        for (int i = 0; i <= degreR; i++) {
            rFinal[i] = r[i];
        }
        return new Polynome[]{
            new Polynome(qFinal),
            new Polynome(rFinal)
        };
    }
    
    /**
     * Retourne le quotient Q de la division euclidienne de P1 par P2,
     * telle que P1 = P2 x Q + R avec deg(R) < deg(P2).
     *
     * @param p1 Polynôme dividende
     * @param p2 Polynôme diviseur (non nul)
     * @return Le quotient Q
     * @throws IllegalArgumentException si le diviseur est le polynôme nul
     */
    public Polynome division(Polynome p1, Polynome p2) {
        return divisionEuclidienne(p1, p2)[0];
    }
    
    /**
     * Retourne le reste R de la division euclidienne de P1 par P2,
     * telle que P1 = P2 x Q + R avec deg(R) < deg(P2).
     *
     * @param p1 Polynôme dividende
     * @param p2 Polynôme diviseur (non nul)
     * @return Le reste R
     * @throws IllegalArgumentException si le diviseur est le polynôme nul
     */
    public Polynome reste(Polynome p1, Polynome p2) {
        return divisionEuclidienne(p1, p2)[1];
    }
    
    /**
     * Calcule le PGCD de deux polynômes par l'algorithme d'Euclide.
     * Le résultat est normalisé (polynôme unitaire : coefficient dominant = 1).
     * pgcd(A, 0) = A  ;  pgcd(A, B) = pgcd(B, reste(A, B))
     *
     * @param a Premier polynôme
     * @param b Deuxième polynôme
     * @return Le PGCD unitaire de a et b
     */
    public Polynome pgcd(Polynome a, Polynome b) {
        if (estEffectivementNul(b)) {
            return normaliser(a);
        }
        Polynome r = nettoyer(reste(a, b));
        return pgcd(b, r);
    }

    /**
     * Calcule le polynôme dérivé de P.
     * Si P = a0 + a1*X + ... + an*X^n, 
     * alors P' = a1 + 2*a2*X + ... + n*an*X^(n-1).
     * @param p Le polynôme à dériver
     * @return Le polynôme dérivé P'
     */
    public Polynome derivee(Polynome p) {
        int degre = p.getDegre();
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
     * Calcule la primitive F de P (constante d'intégration = 0).
     * Si P = a0 + a1*X + ... + an*X^n,
     * alors F = a0*X + (a1/2)*X^2 + ... + (an/(n+1))*X^(n+1)
     * @param p Le polynôme à intégrer
     * @return F tel que F' = P, avec F(0) = 0
     */
    public Polynome primitive(Polynome p) {
        int degre = p.getDegre();
        double[] coeffs = new double[degre + 2];
        coeffs[0] = 0.0;
        for (int i = 0; i <= degre; i++) {
            coeffs[i + 1] = p.getCoefficient(i) / (i + 1.0);
        }
        return new Polynome(coeffs);
    }

    /**
     * Calcule l'image de x par le polynôme P, soit p(x).
     * Utilise l'algorithme de Horner pour minimiser le nombre d'opérations :
     * n multiplications et n additions au lieu de n(n+1)/2 
     * multiplications naïves.
     *
     * @param p Le polynôme
     * @param x La valeur à évaluer
     * @return La valeur p(x)
     */
    public double calculImageFonction(Polynome p, double x) {
        int degre = p.getDegre();
        double resultat = p.getCoefficient(degre);
        for (int i = degre - 1; i >= 0; i--) {
            resultat = resultat * x + p.getCoefficient(i);
        }
        return resultat;
    }

    /**
     * Calcule la valeur moyenne de la fonction polynômiale associée 
     * à P sur [a, b].
     * Formule : (1 / (b - a)) * intégrale de a à b de p(x) dx
     *
     * @param p Le polynôme
     * @param a Borne inférieure de l'intervalle
     * @param b Borne supérieure de l'intervalle (b doit être différent de a)
     * @return La valeur moyenne de p sur [a, b]
     * @throws IllegalArgumentException si a == b
     */
    public double calculValeurMoyenneIntervalle(Polynome p, double a, double b){
        if (a == b) {
            throw new IllegalArgumentException("Les bornes a et b doivent"
                                               + " être distinctes.");
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
        double[] coeffsPrimitive = new double[degre + 2];
        coeffsPrimitive[0] = 0.0; // constante d'intégration = 0

        for (int i = 0; i <= degre; i++) {
            coeffsPrimitive[i + 1] = p.getCoefficient(i) / (i + 1);
        }

        Polynome primitive = new Polynome(coeffsPrimitive);

        // Intégrale = F(b) - F(a)
        return primitive.evaluer(b) - primitive.evaluer(a);
    }
    
    /**
     * Normalise un polynôme pour le rendre unitaire (coefficient dominant = 1).
     * Utilisé par pgcd pour retourner un résultat canonique.
     * @param p Le polynôme à normaliser
     * @return Le polynôme unitaire équivalent, ou le polynôme nul si p est nul
     */
    private Polynome normaliser(Polynome p) {
        if (estEffectivementNul(p)) return new Polynome();
        double coeffDom = p.getCoefficient(p.getDegre());
        return multiplicationScalaire(p, 1.0 / coeffDom);
    }
    
    /**
     * Supprime les coefficients négligeables du polynôme.
     * Un coefficient est mis à zéro si sa valeur absolue est inférieure à
     * 1e-9 × max(|coefficients|). Utile pour stabiliser l'algorithme d'Euclide.
     * @param p Le polynôme à nettoyer
     * @return Un nouveau polynôme avec les petits coefficients mis à zéro
     */
    private Polynome nettoyer(Polynome p) {
        int    degre = p.getDegre();
        double max   = 0.0;
        for (int i = 0; i <= degre; i++) {
            max = Math.max(max, Math.abs(p.getCoefficient(i)));
        }
        if (max < 1e-15) return new Polynome();
 
        double   seuil  = 1e-9 * max;
        double[] coeffs = new double[degre + 1];
        for (int i = 0; i <= degre; i++) {
            double c = p.getCoefficient(i);
            coeffs[i] = Math.abs(c) < seuil ? 0.0 : c;
        }
        return new Polynome(coeffs);
    }
    
    /**
     * Vérifie si un polynôme est numériquement nul (tous coefficients < 1e-9).
     * @param p Le polynôme à tester
     * @return true si le polynôme est considéré comme nul
     */
    private boolean estEffectivementNul(Polynome p) {
        if (p.estNul()) return true;
        int degre = p.getDegre();
        for (int i = 0; i <= degre; i++) {
            if (Math.abs(p.getCoefficient(i)) >= 1e-9) return false;
        }
        return true;
    }
    
}