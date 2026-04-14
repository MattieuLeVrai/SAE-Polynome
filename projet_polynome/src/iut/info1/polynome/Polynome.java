/*
 * Polynome                                                         22/05/26 
 * Iut de rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome;

import java.util.Arrays;

/**
 * TODO Décrire ce que fait la class
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
    	this.coefficients = new double[]{0.0};
    }
    
    /**
     * Constructeur à partir d'un tableau de coefficients.
     * @param coefficients Le tableau des coefficients donnés par l'utilisateur
     */
    public Polynome(double[] coefficients) {
    	if (coefficients == null || coefficients.length == 0) {
            this.coefficients = new double[]{0.0};
        } else {
            // On utilise copyOf pour éviter que des modifs externes impactent l'objet
            this.coefficients = Arrays.copyOf(coefficients, coefficients.length);
        }
    }
    
    /**
     * Retourne le degré du polynôme.
     * @return Le degré (entier)
     */
    public int getDegre() {
        // TODO: Écrire la logique pour trouver et retourner le degré
        return 0; // Valeur par défaut pour que ça compile
    }
    
    /**
     * Retourne le coefficient correspondant à une puissance donnée.
     * @param puissance L'exposant de X
     * @return Le coefficient (réel)
     */
    public double getCoefficient(int puissance) {
        // TODO: Retourner la valeur située au bon index du tableau
    	if (puissance < 0 || puissance >= coefficients.length) {
            return 0.0;
        }
        return coefficients[puissance];
    }
    
    /**
     * Calcule et retourne la limite du polynôme quand X tend vers -infini.
     * @return La limite (peut utiliser Double.NEGATIVE_INFINITY ou POSITIVE_INFINITY)
     */
    public double getLimitesMoinsInfini() {
        // TODO: Calculer la limite en fonction du monôme de plus haut degré
        return 0.0;
    }
    
    /**
     * Calcule et retourne la limite du polynôme quand X tend vers +infini.
     * @return La limite
     */
    public double getLimitesPlusInfini() {
        // TODO: Calculer la limite en fonction du monôme de plus haut degré
        return 0.0;
    }
    
    /**
     * Retourne le tableau des racines réelles du polynôme.
     * @return Un tableau de réels
     */
    public double[] getRacines() {
        // TODO: Retourner l'attribut racines
        return null;
    }
    
    /**
     * Vérifie si le polynôme actuel est le polynôme nul.
     * @return Vrai si c'est le polynôme nul, Faux sinon
     */
    public boolean estNul() {
        // TODO: Parcourir le tableau pour vérifier si tous les coefficients valent 0
        return false;
    }
    
    /**
     * Retourne une représentation textuelle du polynôme (ex: "3.0X^2 + 2.0X - 5.0").
     * @return La chaîne de caractères représentant le polynôme
     */
    @Override
    public String toString() {
        // TODO: Construire la chaîne d'affichage
        return "";
    }
	
}
