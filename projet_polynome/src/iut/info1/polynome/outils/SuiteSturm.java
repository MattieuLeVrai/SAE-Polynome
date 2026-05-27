/*
 * SuiteSturm                                          11/05/26
 * Iut de rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome.outils;

import java.util.ArrayList;
import java.util.List;

import iut.info1.polynome.OperationPolynome;
import iut.info1.polynome.Polynome;

/**
 * Implémente la suite de Sturm pour localiser les racines réelles d'un polynôme,
 * puis les approcher par dichotomie ou par la méthode de Newton.
 * <h2>Principe (théorème de Sturm)</h2>
 * <ol>
 *   <li><b>Étape 1</b> : Supprimer les racines multiples en divisant P par
 *       pgcd(P, P'). On obtient P0, qui n'a que des racines simples.</li>
 *   <li><b>Étape 2</b> : Construire la suite de Sturm (P0, P1, P2, ..., Pm) :
 *       <ul>
 *         <li>P1 = P0'</li>
 *         <li>P_{k+1} = −reste(P_{k−1} / P_k)</li>
 *         <li>on s'arrête quand le reste est nul</li>
 *       </ul>
 *   </li>
 *   <li><b>Étape 3</b> : Compter les changements de signe V(x) de la suite
 *       évaluée en x (les valeurs nulles ne comptent pas).</li>
 *   <li><b>Étape 4</b> : Le nombre de racines réelles de P0 dans ]a, b[ est
 *       V(a) − V(b), à condition que a et b ne soient pas racines de P0.</li>
 * </ol>
 *
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
public class SuiteSturm {

    private final OperationPolynome op = new OperationPolynome();

    /**
     * Supprime les racines multiples de P en le divisant par pgcd(P, P').
     * Le polynôme résultant P0 n'a que des racines simples (toutes les mêmes
     * racines réelles que P, mais avec la multiplicité ramenée à 1).
     * Si pgcd(P, P') = constante, P n'a pas de racine multiple et P0 = P.
     *
     * @param p Le polynôme d'origine
     * @return P0 = P / pgcd(P, P') sans racines multiples
     */
    public Polynome supprimerRacinesMultiples(Polynome p) {
        Polynome derivee = op.derivee(p);
        if (derivee.estNul()) {
            return p; // polynôme constant, aucune racine
        }
        Polynome g = op.pgcd(p, derivee);
        if (g.getDegre() == 0) {
            return p; // pgcd = constante => pas de racine multiple
        }
        return op.division(p, g);
    }


    /**
     * Construit la suite de Sturm associée à un polynôme P0 sans racines multiples.
     *   P0 (donné en entrée)
     *   P1 = P0'
     *   P2 = −reste(P0 / P1)
     *   P3 = −reste(P1 / P2)
     *   ...
     * La suite s'arrête quand le reste est nul.
     * Le polynôme P0 passé en paramètre doit idéalement être sans racines
     * multiples (obtenu via {@link #supprimerRacinesMultiples}).
     *
     * @param p0 Polynôme de départ (sans racines multiples de préférence)
     * @return Le tableau (P0, P1, ..., Pm) formant la suite de Sturm
     */
    public Polynome[] construireSuite(Polynome p0) {
        List<Polynome> suite = new ArrayList<>();
        suite.add(p0);
        suite.add(op.derivee(p0));

        int n = suite.size();
        do {
            Polynome pk1 = suite.get(n - 2);
            Polynome pk = suite.get(n - 1);

            if (!pk.estNul()) {
                Polynome resteDiv = op.reste(pk1, pk);
                Polynome suivant = op.multiplicationScalaire(resteDiv, -1.0);
                suite.add(suivant);
                n++;
            }
        } while (n > 1 && !suite.get(suite.size() - 1).estNul());

        if (!suite.isEmpty() && suite.get(suite.size() - 1).estNul()) {
            suite.remove(suite.size() - 1);
        }

        return suite.toArray(new Polynome[0]);
    }

    /**
     * Compte le nombre de changements de signe V(x) dans la suite de Sturm
     * évaluée au point x. Les valeurs nulles sont ignorées dans le décompte.
     *
     * @param suite La suite de Sturm (P0, P1, ..., Pm)
     * @param x     La valeur en laquelle évaluer la suite
     * @return V(x) = nombre de changements de signe
     */
    public int compterChangementsDeSignes(Polynome[] suite, double x) {
        int    changements = 0;
        double signePrec   = 0.0;

        for (Polynome p : suite) {
            double valeur = p.evaluer(x);
            if (Math.abs(valeur) >= 1e-10) {
                double signe = Math.signum(valeur);
                if (signePrec != 0.0 && signe != signePrec) {
                    changements++;
                }
                signePrec = signe;
            }
        }

        return changements;
    }

    /**
     * Retourne le nombre de racines réelles de P dans l'intervalle ]a, b[.
     * Applique le théorème de Sturm : nombre de racines = V(a) − V(b).
     * Précondition : a et b ne doivent pas être des racines de P.
     *
     * @param p Le polynôme (peut avoir des racines multiples, elles seront gérées)
     * @param a Borne inférieure de l'intervalle
     * @param b Borne supérieure de l'intervalle (b > a)
     * @return Le nombre de racines réelles de P dans ]a, b[
     * @throws IllegalArgumentException si a >= b
     */
    public int nombreRacinesIntervalle(Polynome p, double a, double b) {
        if (a >= b) {
            throw new IllegalArgumentException("On doit avoir a < b.");
        }
        Polynome    p0    = supprimerRacinesMultiples(p);
        Polynome[]  suite = construireSuite(p0);
        return compterChangementsDeSignes(suite, a)
             - compterChangementsDeSignes(suite, b);
    }

    /**
     * Localise toutes les racines réelles de P en balayant un intervalle global
     * et en appliquant le théorème de Sturm pour isoler chaque racine individuelle.
     *
     * @param p     Le polynôme dont on cherche les racines
     * @param debut Borne gauche de la recherche
     * @param fin   Borne droite de la recherche
     * @param pas   Largeur de la fenêtre de balayage (plus petit = plus précis 
     *              mais plus lent). Valeur typique : 0.5 ou 1.0.
     * @return Liste d'intervalles [a, b] contenant chacun exactement une racine
     */
    public List<double[]> localiserRacines(Polynome p, double debut,
                                            double fin, double pas) {
        List<double[]> intervalles = new ArrayList<>();
        Polynome p0 = supprimerRacinesMultiples(p);
        Polynome[] suite = construireSuite(p0);

        for (double a = debut; a < fin; a += pas) {
            double b = Math.min(a + pas, fin);
            int nb = compterChangementsDeSignes(suite, a)
                   - compterChangementsDeSignes(suite, b);
            if (nb == 1) {
                intervalles.add(new double[]{a, b});
            }
        }

        return intervalles;
    }


    /**
     * Approxime une racine réelle de P dans l'intervalle [a, b] par dichotomie.
     * L'intervalle [a, b] doit contenir exactement une racine
     * Critère d'arrêt : largeur de l'intervalle < precision.
     *
     * @param p         Le polynôme
     * @param a         Borne inférieure de l'intervalle
     * @param b         Borne supérieure de l'intervalle
     * @param precision Précision souhaitée (ex : 1e-9)
     * @return La racine approchée avec la précision demandée
     * @throws IllegalArgumentException si p(a) et p(b) ont le même signe
     */
    public double approximerParDichotomie(Polynome p, double a, double b,
                                           double precision) {
        double fa = p.evaluer(a);
        double fb = p.evaluer(b);

        if (fa * fb > 0) {
            throw new IllegalArgumentException(
                    "p(a) et p(b) doivent être de signes opposés pour la dichotomie.");
        }
        if (Math.abs(fa) < precision) return a;
        if (Math.abs(fb) < precision) return b;

        double milieu = a;
        while ((b - a) > precision && Math.abs(p.evaluer(milieu)) >= precision){
            milieu          = (a + b) / 2.0;
            double fMilieu  = p.evaluer(milieu);

            if (fa * fMilieu <= 0) {
                b  = milieu;
                fb = fMilieu;
            } else {
                a  = milieu;
                fa = fMilieu;
            }
        }

        return milieu;
    }

    /**
     * Approxime une racine réelle de P au voisinage de x0 par la méthode de Newton.
     * Formule itérative : x_{n+1} = x_n − P(x_n) / P'(x_n)
     *
     * Converge rapidement si x0 est proche de la racine et P'(x0) != 0.
     * La convergence est quadratique pour des racines simples.
     *
     * Critère d'arrêt : |P(x_n)| < precision  ou  nombre maximum d'itérations atteint.
     *
     * @param p           Le polynôme
     * @param x0          Point de départ de l'itération
     * @param precision   Précision souhaitée (ex : 1e-9)
     * @param maxIterations Nombre maximum d'itérations (ex : 100)
     * @return La racine approchée
     * @throws ArithmeticException si P'(x_n) devient nul (méthode diverge)
     */
    public double approximerParNewton(Polynome p, double x0, double precision,
                                       int maxIterations) {
        Polynome dp = op.derivee(p);
        double   x  = x0;

        for (int iter = 0; 
        		iter < maxIterations && Math.abs(p.evaluer(x)) >= precision;
        		iter++) {
            double fx  = p.evaluer(x);
            double dfx = dp.evaluer(x);

            if (Math.abs(dfx) < 1e-15) {
                throw new ArithmeticException(
                        "La dérivée est nulle en x=" + x 
                        + " : la méthode de Newton diverge.");
            }

            x = x - fx / dfx;
        }
        return x;
    }


    /**
     * Trouve toutes les racines réelles approchées de P dans [debut, fin] :
     * <ol>
     *   <li>Localise les intervalles contenant chacun une racine (Sturm)</li>
     *   <li>Affine chaque racine par dichotomie</li>
     * </ol>
     *
     * @param p         Le polynôme
     * @param debut     Borne gauche de la recherche
     * @param fin       Borne droite de la recherche
     * @param pas       Pas de balayage pour la localisation (ex : 0.5)
     * @param precision Précision des racines approchées (ex : 1e-9)
     * @return Tableau des racines approchées trouvées dans [debut, fin]
     */
    public double[] chercherToutesLesRacines(Polynome p, double debut, double fin,
                                              double pas, double precision) {
        List<double[]> intervalles = localiserRacines(p, debut, fin, pas);
        double[] racines = new double[intervalles.size()];

        for (int i = 0; i < intervalles.size(); i++) {
            double a = intervalles.get(i)[0];
            double b = intervalles.get(i)[1];
            racines[i] = approximerParDichotomie(p, a, b, precision);
        }

        return racines;
    }
}