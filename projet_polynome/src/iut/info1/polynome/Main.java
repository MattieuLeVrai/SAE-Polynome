package iut.info1.polynome;

import iut.info1.polynome.outils.PersistancePolynome;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner entreeUtilisateur = new Scanner(System.in);
        OperationPolynome calculateur = new OperationPolynome();
        List<Polynome> listeDePolynomes = new ArrayList<>();

        System.out.println("=== Créateur de Polynômes Interactif ===");
        
        boolean sessionActive = true;
        while (sessionActive) {
            afficherMenu();
            
            int choixMenu = entreeUtilisateur.nextInt();
            entreeUtilisateur.nextLine(); // Consomme le retour à la ligne

            if (choixMenu == 1) {
                // Création par coefficients
                System.out.print("Entrez les coefficients séparés par des espaces (de a0 à an) : ");
                String saisieCoefficients = entreeUtilisateur.nextLine();
                String[] segmentsCoefficients = saisieCoefficients.split("\\s+");
                
                double[] tableauCoefficients = new double[segmentsCoefficients.length];
                for (int i = 0; i < segmentsCoefficients.length; i++) {
                    tableauCoefficients[i] = Double.parseDouble(segmentsCoefficients[i]);
                }
                
                Polynome nouveauPolynome = new Polynome(tableauCoefficients);
                listeDePolynomes.add(nouveauPolynome);
                System.out.println("Ajouté : " + nouveauPolynome);

            } else if (choixMenu == 2) {
                // Création par racines
                System.out.print("Coefficient dominant (an) : ");
                double coefficientDominant = entreeUtilisateur.nextDouble();
                
                System.out.print("Nombre de racines distinctes : ");
                int nombreDeRacines = entreeUtilisateur.nextInt();
                
                double[] valeursRacines = new double[nombreDeRacines];
                int[] ordresMultiplicite = new int[nombreDeRacines];
                
                for (int i = 0; i < nombreDeRacines; i++) {
                    System.out.print("Racine n°" + (i + 1) + " : ");
                    valeursRacines[i] = entreeUtilisateur.nextDouble();
                    System.out.print("Ordre de multiplicité : ");
                    ordresMultiplicite[i] = entreeUtilisateur.nextInt();
                }
                
                Polynome nouveauPolynome = new Polynome(coefficientDominant, valeursRacines, ordresMultiplicite);
                listeDePolynomes.add(nouveauPolynome);
                System.out.println("Ajouté : " + nouveauPolynome);

            } else if (choixMenu == 3) {
                sessionActive = false;
            } else {
                System.out.println("Choix invalide, veuillez recommencer.");
            }
        }

        sauvegarderDonnees(listeDePolynomes);
        
        System.out.println("Au revoir !");
        entreeUtilisateur.close();
    }

    private static void afficherMenu() {
        System.out.println("\nQuel type de polynôme voulez-vous créer ?");
        System.out.println("1. Par coefficients (ex: 3.0 -5.0 2.0)");
        System.out.println("2. Par racines (ex: 2.0 * (X-1)^2 * (X+3))");
        System.out.println("3. Terminer et sauvegarder");
        System.out.print("Votre choix : ");
    }

    private static void sauvegarderDonnees(List<Polynome> polynomesASauvegarder) {
        if (!polynomesASauvegarder.isEmpty()) {
            try {
                String cheminFichierSauvegarde = "polynomes_utilisateur.txt";
                PersistancePolynome.sauvegarder(polynomesASauvegarder, cheminFichierSauvegarde);
                System.out.println("\nSuccès ! " + polynomesASauvegarder.size() + " polynômes sauvegardés.");
            } catch (Exception e) {
                System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
            }
        }
    }
}