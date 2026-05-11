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
    	
    	for (double coeff : coefficients) {
    		if (Double.isNaN(coeff) || Double.isInfinite(coeff)) {
                throw new IllegalArgumentException("Le tableau ne doit pas contenir de coefficients NaN, ou infinis.");
            }
    	}
        
        this.coefficients = coefficients;
    }
    
    /**
     * Retourne le degré du polynôme.
     * @return Le degré (entier)
     */
    public int getDegre() {
    	// On cherche l'indice du premier coefficient non nul en partant de la fin
    	for (int indiceCoefficient = this.coefficients.length - 1; indiceCoefficient >= 0; indiceCoefficient--) {
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
            return (coefficientDominant > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        } else {
            return (coefficientDominant > 0) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
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

        return (coefficientDominant > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
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
        int degre = this.getDegre();
        double coefficientDominant = Math.abs(this.getCoefficient(degre));
        double coefficientMax = 0;
        
        for (int indice = 0; indice < degre; indice++) {
            coefficientMax = Math.max(coefficientMax, Math.abs(this.getCoefficient(indice)));
        }
        
        // Toutes les racines se trouvent forcément entre -limite et +limite.
        double limite = 1.0 + (coefficientMax / coefficientDominant);
        
        if (Double.isInfinite(limite) || limite > 1e15) {
            // On force une limite raisonnable pour éviter l'overflow
            limite = 1e15; 
        }
        
        // 2. Recherche par balayage et dichotomie
        List<Double> listeRacines = new ArrayList<>();
        
        // Si la limite est immense, on adapte un peu le pas
        // Sinon on garde un pas classique de 0.1
        double pas = (limite > 1000) ? (limite / 10000.0) : 0.1; 

        for (double valeurX = -limite; valeurX < limite; valeurX += pas) {
            double valeurY1 = evaluer(valeurX);
            double valeurY2 = evaluer(valeurX + pas);

            // Si la courbe traverse l'axe (changement de signe)
            if (valeurY1 * valeurY2 <= 0) {
                double borneA = valeurX;
                double borneB = valeurX + pas;
                double precision = 1e-6; // Précision à 6 décimales

                // Dichotomie pour affiner la position de la racine
                while ((borneB - borneA) > precision) {
                    double milieu = (borneA + borneB) / 2.0;
                    if (evaluer(borneA) * evaluer(milieu) <= 0) {
                        borneB = milieu; 
                    } else {
                        borneA = milieu; 
                    }
                }

                // Arrondi à 4 décimales pour gérer les impécisions
                double racine = Math.round(((borneA + borneB) / 2.0) * 10000.0) / 10000.0;
                
                if (!listeRacines.contains(racine)) {
                    listeRacines.add(racine);
                }
            }
        }

        // 3. Conversion de la liste en tableau de double
        double[] tableau = new double[listeRacines.size()];
        for (int indice = 0; indice < listeRacines.size(); indice++) {
            tableau[indice] = listeRacines.get(indice);
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

        for (int indice = 0; indice < this.coefficients.length; indice++) {
            // On laisse Java calculer naturellement. 
            // Si le chiffre dépasse, terme deviendra "Infinity".
            double terme = this.coefficients[indice] * Math.pow(x, indice);
            resultat += terme;
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