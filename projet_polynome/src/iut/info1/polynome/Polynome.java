/*
 * Polynome                                                         22/05/26 
 * Iut de rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un polynôme à coefficients réels et fournit des outils
 * pour manipuler ses propriétés algébriques.
 * Cette classe permet notamment de :
 * <ul>
 * <li>Déterminer le degré du polynôme.</li>
 * <li>Calculer les limites aux voisinages de l'infini.</li>
 * <li>Obtenir les coefficients pour une puissance donnée.</li>
 * <li>Générer une représentation textuelle formatée de l'expression.</li>
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
    	// Initialisation des tableaux pour le polynôme nul
    	this.coefficients = new double[] {0.0};
    }
    
    /**
     * Constructeur à partir d'un tableau de coefficients
     * @param coefficients Le tableau des coefficients donnés par l'utilisateur
     */
    public Polynome(double[] coefficients) {
    	if (coefficients == null || coefficients.length == 0) {
            throw new IllegalArgumentException("Le tableau ne doit pas être null ou vide.");
        }
        
        this.coefficients = coefficients;
    }
    
    /**
     * Retourne le degré du polynôme.
     * @return Le degré (entier)
     */
    public int getDegre() {
    	// On cherche l'indice du premier coefficient non nul en partant de la fin
    	for (int i = this.coefficients.length - 1; i >= 0; i--) {
            if (this.coefficients[i] != 0) {
                return i;
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
    	if (this.estNul() || this.getDegre() == 0) {
            return new double[0]; // Pas de racine (ou infinité si P=0, on retourne vide)
        }

        // 1. Calcul automatique de l'intervalle de recherche
        int n = this.getDegre();
        double an = Math.abs(this.getCoefficient(n));
        double maxCoeff = 0;
        
        for (int i = 0; i < n; i++) {
            maxCoeff = Math.max(maxCoeff, Math.abs(this.getCoefficient(i)));
        }
        
        // Toutes les racines se trouvent forcément entre -limite et +limite.
        // Formule de Borne de Cauchy
        double limite = 1.0 + (maxCoeff / an); 
        
        // 2. Recherche par balayage et dichotomie
        List<Double> listeRacines = new ArrayList<>();
        
        // Si la limite est immense, on adapte un peu le pas
        // Sinon on garde un pas classique de 0.1
        double pas = (limite > 1000) ? (limite / 10000.0) : 0.1; 

        for (double x = -limite; x < limite; x += pas) {
            double y1 = evaluer(x);
            double y2 = evaluer(x + pas);

            // Si la courbe traverse l'axe (changement de signe)
            if (y1 * y2 <= 0) {
                double a = x;
                double b = x + pas;
                double precision = 1e-6; // Précision à 6 décimales

                // Dichotomie pour affiner la position de la racine
                while ((b - a) > precision) {
                    double m = (a + b) / 2.0;
                    if (evaluer(a) * evaluer(m) <= 0) {
                        b = m; 
                    } else {
                        a = m; 
                    }
                }

                // Arrondi à 4 décimales pour gérer les impécisions
                double racine = Math.round(((a + b) / 2.0) * 10000.0) / 10000.0;
                
                if (!listeRacines.contains(racine)) {
                    listeRacines.add(racine);
                }
            }
        }

        // 3. Conversion de la liste en tableau de double
        double[] tableau = new double[listeRacines.size()];
        for (int i = 0; i < listeRacines.size(); i++) {
            tableau[i] = listeRacines.get(i);
        }
        return tableau;
    }
    
    /**
     * Calcule la valeur de P(x) pour un x donné (indispensable pour la dichotomie).
     * @param x La valeur à tester
     * @return Le résultat de l'équation
     */
    public double evaluer(double x) {
        double resultat = 0;
        // On utilise directement 'i' comme puissance
        for (int i = 0; i < this.coefficients.length; i++) {
            resultat += this.coefficients[i] * Math.pow(x, i); 
        }
        return resultat;
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

        // On parcourt à l'envers pour afficher d'abord les X de plus haut degré
        for (int puissance = this.coefficients.length - 1; puissance >= 0; puissance--) {
            double coeff = this.coefficients[puissance];
            if (coeff == 0) continue;

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