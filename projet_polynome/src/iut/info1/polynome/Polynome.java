/*
 * Polynome                                                         22/05/26 
 * Iut de rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome;

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
    	// Initialisation des tableaux pour le polynôme nul
    	this.coefficients = new double[] {0.0};
        this.racines = new double[0];
    }
    
    /**
     * Constructeur à partir d'un tableau de coefficients.
     * @param coefficients Le tableau des coefficients donnés par l'utilisateur
     */
    public Polynome(double[] coefficients) {
        // TODO: Copier le tableau reçu dans l'attribut de la classe
    	if (coefficients == null || coefficients.length == 0) {
    		this.coefficients = new double[] {0.0};
    	} else {
    		this.coefficients = coefficients;
    	}
    }
    
    /**
     * Retourne le degré du polynôme.
     * @return Le degré (entier)
     */
    public int getDegre() {
        // TODO: Écrire la logique pour trouver et retourner le degré
    	// On part de la fin du tableau (le plus haut degré potentiel)
        for (int indiceCoefficient = this.coefficients.length - 1; indiceCoefficient >= 0; indiceCoefficient--) {
            if (this.coefficients[indiceCoefficient] != 0) {
                return indiceCoefficient; // On a trouvé le terme de plus haut degré
            }
        }
        
        // Si tous les coefficients sont nuls, le degré est techniquement 0 
        // (ou parfois défini comme -1 ou -infini pour le polynôme nul)
        return 0;
    }
    
    /**
     * Retourne le coefficient correspondant à une puissance donnée.
     * @param puissance L'exposant de X
     * @return Le coefficient (réel)
     */
    public double getCoefficient(int puissance) {
        // Retourner la valeur située au bon index du tableau
    	int degreMin = this.coefficients.length - 1;
    	
    	if (puissance < 0 || puissance >= this.coefficients.length) {
            return 0.0; // Un polynôme a un coefficient de 0 pour les puissances hors de son degré
        }
    	return this.coefficients[degreMin - puissance];
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
        // Retourner l'attribut racines
        return this.racines;
    }
    
    /**
     * Vérifie si le polynôme actuel est le polynôme nul.
     * @return Vrai si c'est le polynôme nul, Faux sinon
     */
    public boolean estNul() {
        // TODO: Parcourir le tableau pour vérifier si tous les coefficients valent 0
        for (int indiceCoefficient = 0; indiceCoefficient <= this.coefficients.length - 1; indiceCoefficient++) {
        	if (this.coefficients[indiceCoefficient] != 0) {
        		return false;
            }
        }
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
