/*
 * Polynome                                                         22/05/26 
 * Iut de rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome;

/**
 * Représente un polynôme à coefficients réels et fournit des outils
 * pour manipuler ses propriétés algébriques.
 * Cette classe permet notamment de :
 * <ul>
 * 		<li>Déterminer le degré du polynôme.</li>
 * 		<li>Calculer les limites aux voisinages de l'infini.</li>
 * 		<li>Obtenir les coefficients pour une puissance donnée.</li>
 * 		<li>Générer une représentation textuelle formatée de l'expression.</li>
 * </ul>
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
public class Polynome {

	private double[] coefficients;
    private double[] racines;
    
    /**
     * Constructeur par défaut.
     * Crée le polynôme nul P(X) = 0.0
     */
    public Polynome() {
    	// Initialisation des tableaux pour le polynôme nul
    	this.coefficients = new double[] {0.0};
        this.racines = new double[0];
    }
    
    /**
     * Constructeur à partir d'un tableau de coefficients.
     * @param coefficients Le tableau des coefficients donnés par l'utilisateur
     */
    public Polynome(double[] coefficients) {
    	if (coefficients == null || coefficients.length == 0) {
            throw new IllegalArgumentException("Le tableau ne doit pas être null ou vide.");
        }
        
        this.coefficients = coefficients;

        // Calcul des racines pour les degrés 1 et 2
        int degre = this.getDegre();

        if (degre == 2) {
            double coeffA = getCoefficient(2);
            double coeffB = getCoefficient(1);
            double coeffC = getCoefficient(0);

            // Le discriminant détermine le nombre de racines réelles
            double discriminant = (coeffB * coeffB) - (4 * coeffA * coeffC);

            if (discriminant > 0) {
                this.racines = new double[] {
                    (-coeffB - Math.sqrt(discriminant)) / (2 * coeffA),
                    (-coeffB + Math.sqrt(discriminant)) / (2 * coeffA)
                };
            } else if (discriminant == 0) {
                this.racines = new double[] { -coeffB / (2 * coeffA) };
            } else {
                this.racines = new double[0];
            }
        } else if (degre == 1) {
            this.racines = new double[] { -getCoefficient(0) / getCoefficient(1) };
        } else {
            this.racines = new double[0];
        }
    }
    
    /**
     * Retourne le degré du polynôme.
     * @return Le degré (entier)
     */
    public int getDegre() {
    	// On cherche l'indice du premier coefficient non nul en partant du début
    	for (int i = 0; i < this.coefficients.length; i++) {
            if (this.coefficients[i] != 0) {
                return this.coefficients.length - 1 - i;
            }
        }
        return 0;
    }
    
    /**
     * Retourne le coefficient correspondant à une puissance donnée.
     * @param puissance L'exposant de X
     * @return Le coefficient (réel)
     */
    public double getCoefficient(int puissance) {
        // Retourner la valeur située au bon index du tableau (inversion d'indice)
    	int indice = this.coefficients.length - 1 - puissance;
    	
    	if (indice < 0 || indice >= this.coefficients.length) {
            return 0.0;
        }
    	return this.coefficients[indice];
    }
    
    /**
     * Calcule et retourne la limite du polynôme quand X tend vers -infini.
     * @return La limite (peut utiliser Double.NEGATIVE_INFINITY ou POSITIVE_INFINITY)
     */
    public double getLimitesMoinsInfini() {
    	int n = this.getDegre();
        double an = this.getCoefficient(n);

        if (n == 0) return an;

        if (n % 2 == 0) {
            return (an > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        } else {
            return (an > 0) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        }
    }
    
    /**
     * Calcule et retourne la limite du polynôme quand X tend vers +infini.
     * @return La limite
     */
    public double getLimitesPlusInfini() {
    	int n = this.getDegre();
        double an = this.getCoefficient(n);

        if (n == 0) return an;

        return (an > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }
    
    /**
     * Retourne le tableau des racines réelles du polynôme.
     * @return Un tableau de réels
     */
    public double[] getRacines() {
        // Retourner l'attribut racines
        return this.racines;
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
     * Retourne une représentation textuelle du polynôme (ex: "3.0X^2 + 2.0X - 5.0").
     * @return La chaîne de caractères représentant le polynôme
     */
    @Override
    public String toString() {
    	if (this.estNul()) return "0.0";

        StringBuilder sb = new StringBuilder();
        boolean premierTerme = true;

        for (int i = 0; i < this.coefficients.length; i++) {
            double coeff = this.coefficients[i];
            if (coeff == 0) continue;

            int puissance = this.coefficients.length - 1 - i;

            if (coeff > 0 && !premierTerme) {
                sb.append(" + ");
            } else if (coeff < 0) {
                sb.append(premierTerme ? "-" : " - ");
            }

            double absCoeff = Math.abs(coeff);
            if (absCoeff != 1 || puissance == 0) {
                sb.append(absCoeff);
            }

            if (puissance > 0) {
                sb.append("X");
                if (puissance > 1) {
                    sb.append("^").append(puissance);
                }
            }
            premierTerme = false;
        }
        return sb.toString();
    }
}
