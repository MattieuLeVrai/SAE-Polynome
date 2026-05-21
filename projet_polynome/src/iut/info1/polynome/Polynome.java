/*
 * Polynome                                                         22/05/26 
 * Iut de rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome;

import java.util.ArrayList;
import java.util.List;
import iut.info1.polynome.outils.SuiteSturm;

/**
 * Représente un polynôme à coefficients réels et fournit des outils
 * pour manipuler ses propriétés algébriques.
 * Cette classe permet notamment de :
 * <ul>
 * <li>Déterminer le degré du polynôme.</li>
 * <li>Calculer les limites aux voisinages de l'infini.</li>
 * <li>Obtenir les coefficients pour une puissance donnée.</li>
 * </ul>
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
public class Polynome {

	private double[] coefficients;
    
    /**
     * Constructeur par défaut.
     * Crée le polynôme nul P(X) = 0.0
     */
    public Polynome() {
    	this.coefficients = new double[] {0.0};
    }
    
    /**
     * Constructeur à partir d'un tableau de coefficients
     * @param coefficients Le tableau des coefficients donnés par l'utilisateur
     */
    public Polynome(double[] coefficients) {
    	if (coefficients == null || coefficients.length == 0) {
            throw new IllegalArgumentException("Le tableau ne doit pas être" +
    	                                       "null ou vide.");
        }
    	
    	for (double coeff : coefficients) {
    		if (Double.isNaN(coeff) || Double.isInfinite(coeff)) {
                throw new IllegalArgumentException("Le tableau ne doit pas" +
    		                                       "contenir de coefficients NaN" +
                		                            ", ou infinis.");
            }
    	}
        
        this.coefficients = coefficients;
    }
    
    /**
     * Constructeur à partir des racines réelles, de leurs ordres de multiplicité
     * et du coefficient du monôme de plus haut degré.
     * Exemple : coefficientDominant = 2.0, racines={1.0, -3.0}, ordres = {2, 1}
     * donne P = 2(X−1)²(X+3) = 2X³ + 2X² − 8X + 6
     * @param coefficientDominant Coefficient du terme de plus haut degré(non nul)
     * @param racines Racines réelles du polynôme
     * @param ordres Ordres de multiplicité (>= 1 chacun)
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    public Polynome(double coefficientDominant, double[] racines, int[] ordres) {
        if (racines == null || ordres == null) {
            throw new IllegalArgumentException(
                    "Les tableaux racines et ordres ne peuvent pas être null.");
        }
        if (racines.length != ordres.length) {
            throw new IllegalArgumentException(
                    "Les tableaux racines et ordres doivent avoir la même taille.");
        }
        if (coefficientDominant == 0.0) {
            throw new IllegalArgumentException("Le coefficient dominant ne peut" +
                                               " pas être zéro.");
        }
        for (int o : ordres) {
            if (o < 1) {
                throw new IllegalArgumentException(
                        "Chaque ordre de multiplicité doit être >= 1.");
            }
        }
        // Départ : polynôme constant égal au coefficient dominant
        double[] result = new double[]{coefficientDominant};
 
        // Pour chaque racine r d'ordre k, on multiplie par (X − r)^k
        for (int i = 0; i < racines.length; i++) {
            // (X − r) en tableau : [-r, 1.0]
            double[] facteur = new double[]{-racines[i], 1.0};
            for (int k = 0; k < ordres[i]; k++) {
                result = multiplierTableaux(result, facteur);
            }
        }
 
        this.coefficients = result;
    }
    /**
     * Retourne le degré du polynôme.
     * @return Le degré (entier)
     */
    public int getDegre() {
    	// On cherche l'indice du premier coefficient non nul en partant de la fin
    	for (int indiceCoefficient = this.coefficients.length - 1;
    	     indiceCoefficient >= 0;
    		 indiceCoefficient--) {
            if (this.coefficients[indiceCoefficient] != 0) {
                return indiceCoefficient;
            }
        }
        return 0; // Polynome nul
    }
    
    /**
     * Retourne le coefficient correspondant à une puissance donnée.
     * @param puissance L'exposant de X
     * @return Le coefficient (réel)
     */
    public double getCoefficient(int puissance) {
    	if (puissance < 0 || puissance >= this.coefficients.length) {
            return 0.0;
        }
    	return this.coefficients[puissance];
    }
    
    /**
     * Calcule et retourne la limite du polynôme quand X tend vers -infini.
     * @return La limite (peut utiliser Double.NEGATIVE_INFINITY ou POSITIVE_INFINITY)
     */
    public double getLimitesMoinsInfini() {
    	int degre = this.getDegre();
        double coefficientDominant = this.getCoefficient(degre);

        if (degre == 0) return coefficientDominant;

        if (degre % 2 == 0) {
            return (coefficientDominant > 0) ?
            		                         Double.POSITIVE_INFINITY :
            		                         Double.NEGATIVE_INFINITY;
        } else {
            return (coefficientDominant > 0) ?
            		                         Double.NEGATIVE_INFINITY :
            		                         Double.POSITIVE_INFINITY;
        }
    }
    
    /**
     * Calcule et retourne la limite du polynôme quand X tend vers +infini.
     * @return La limite 
     */
    public double getLimitesPlusInfini() {
    	int degre = this.getDegre();
        double coefficientDominant = this.getCoefficient(degre);

        if (degre == 0) return coefficientDominant;

        return (coefficientDominant > 0) ?
        		                         Double.POSITIVE_INFINITY :
        		                         Double.NEGATIVE_INFINITY;
    }
    
    /**
     * Retourne le tableau des racines réelles du polynôme.
     * Utilise la suite de Sturm pour localiser les racines avec certitude
     * (gère les racines multiples et les racines proches), puis les affine
     * par dichotomie avec une précision de 1e-9.
     * @return Un tableau de réels contenant les racines trouvées
     */
    public double[] getRacines() {
        if (this.estNul() || this.getDegre() == 0) {
            return new double[0]; // Pas de racine (ou infinité si P=0, on retourne vide)
        }

        // 1. Calcul automatique de la borne de recherche (théorème de Cauchy).
        //    Toutes les racines réelles de P sont dans ]-limite, +limite[.
        int    degre               = this.getDegre();
        double coefficientDominant = Math.abs(this.getCoefficient(degre));
        double coefficientMax      = 0;

        for (int indice = 0; indice < degre; indice++) {
            coefficientMax = Math.max(coefficientMax,
                                      Math.abs(this.getCoefficient(indice)));
        }

        double limite = 1.0 + (coefficientMax / coefficientDominant);

        if (Double.isInfinite(limite) || limite > 1e15) {
            limite = 1e15; // borne raisonnable pour éviter l'overflow
        }

        // 2. Pas de balayage : plus serré pour les petites bornes,
        //    proportionnel sinon. La suite de Sturm garantit qu'on ne rate
        //    aucune racine indépendamment du pas choisi.
        double pas = (limite > 1000) ? (limite / 10000.0) : 0.5;

        // 3. Délégation à SuiteSturm :
        //    - supprime les racines multiples (pour que Sturm soit applicable),
        //    - localise chaque racine individuelle dans un sous-intervalle,
        //    - affine par dichotomie à la précision demandée.
        SuiteSturm sturm = new SuiteSturm();
        return sturm.chercherToutesLesRacines(this, -limite, limite, pas, 1e-9);
    }
    
    /**
     * Calcule la valeur de P(x) pour un x donné (indispensable pour la dichotomie).
     * @param x La valeur à tester
     * @return Le résultat de l'équation
     */
    public double evaluer(double x) {
        double resultat = 0;

        for (int indice = 0; indice < this.coefficients.length; indice++) {
            // On laisse Java calculer naturellement. 
            // Si le chiffre dépasse, terme deviendra "Infinity".
            double terme = this.coefficients[indice] * Math.pow(x, indice);
            resultat += terme;
        }

        return resultat;
    }
    
    
    /**
     * Multiplie deux polynômes représentés sous forme de tableaux de coefficients.
     * Utilisée par le constructeur par racines pour éviter une dépendance circulaire
     * avec OperationPolynome.
     *
     * @param a Coefficients du premier polynôme (indice = puissance)
     * @param b Coefficients du second polynôme  (indice = puissance)
     * @return Tableau de coefficients du produit
     */
    public double[] multiplierTableaux(double[] a, double[] b) {
        double[] produit = new double[a.length + b.length - 1];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                produit[i + j] += a[i] * b[j];
            }
        }
        return produit;
    }
    
    /**
     * Vérifie si le polynôme actuel est le polynôme nul.
     * @return Vrai si c'est le polynôme nul, Faux sinon
     */
    public boolean estNul() {
        for (double coeff : this.coefficients) {
        	if (coeff != 0) {
        		return false;
            }
        }
		return true;
    }
    
    /**
     * Retourne une représentation textuelle du polynôme (ex: "7.0X^4 + 2.0X^3 + 12.0").
     * L'ordre d'affichage est décroissant (du plus haut degré vers le terme constant).
     * @return La chaîne de caractères représentant le polynôme
     */
    @Override
    public String toString() {
        // Gestion du cas particulier : polynôme nul
        if (this.estNul()) {
            return "0.0";
        }

        StringBuilder sb = new StringBuilder();
        boolean premierTerme = true;

        /* * On parcourt le tableau à l'envers :
         * L'indice 'puissance' correspond à l'exposant de X.
         * coefficients[coefficients.length - 1] est le coefficient de plus haut degré (an).
         */
        for (int puissance = this.coefficients.length - 1; puissance >= 0; puissance--) {
            double coeff = this.coefficients[puissance];

            // On ignore les termes dont le coefficient est zéro
            if (coeff == 0) {
                continue;
            }

            // 1. Gestion du signe et des opérateurs de liaison (+ / -)
            if (coeff > 0) {
                if (!premierTerme) {
                    sb.append(" + ");
                }
            } else {
                // Pour un coefficient négatif
                if (premierTerme) {
                    sb.append("-");
                } else {
                    sb.append(" - ");
                }
            }

            // 2. Gestion de la valeur absolue du coefficient
            double absCoeff = Math.abs(coeff);
            
            /* * On affiche le coefficient si :
             * - Il est différent de 1.0 (on préfère "X" à "1.0X")
             * - OU si c'est le terme constant (puissance 0), car là il faut afficher "1.0"
             */
            if (absCoeff != 1.0 || puissance == 0) {
                sb.append(absCoeff);
            }

            // 3. Gestion de la variable X et de sa puissance
            if (puissance > 0) {
                sb.append("X");
                if (puissance > 1) {
                    sb.append("^").append(puissance);
                }
            }

            // Une fois qu'on a ajouté un terme, le suivant ne sera plus le "premier"
            premierTerme = false;
        }

        return sb.toString();
    }
}