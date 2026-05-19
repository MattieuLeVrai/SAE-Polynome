package iut.info1.polynome;

import iut.info1.polynome.outils.PersistancePolynome;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {
    public static void main(String[] args) {
        // --- ÉTAPE 1 : Force l'utilisation des fenêtres natives du système ---
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // On ignore si le changement de style échoue
        }

        Scanner entreeUtilisateur = new Scanner(System.in);
        OperationPolynome calculateur = new OperationPolynome();
        List<Polynome> listeDePolynomes = new ArrayList<>();

        System.out.println("=== Créateur de Polynômes Interactif ===");
        
        boolean sessionActive = true;
        while (sessionActive) {
            afficherMenu();
            
            if (entreeUtilisateur.hasNextInt()) {
                int choixMenu = entreeUtilisateur.nextInt();
                entreeUtilisateur.nextLine(); // Consomme le retour à la ligne

                if (choixMenu == 1) {
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
            } else {
                System.out.println("Veuillez saisir un nombre valide.");
                entreeUtilisateur.nextLine(); // Nettoie la mauvaise saisie
            }
        }

        // --- ÉTAPE 2 : On sauvegarde AVANT de fermer le scanner de la console ---
        sauvegarderDonnees(listeDePolynomes);
        
        System.out.println("Au revoir !");
        entreeUtilisateur.close();
        
        // --- ÉTAPE 3 : Force la fermeture des threads graphiques cachés ---
        System.exit(0);
    }

    private static void afficherMenu() {
        System.out.println("\nQuel type de polynôme voulez-vous créer ?");
        System.out.println("1. Par coefficients (ex: 3.0 -5.0 2.0)");
        System.out.println("2. Par racines (ex: 2.0 * (X-1)^2 * (X+3))");
        System.out.println("3. Terminer et sauvegarder");
        System.out.println("Votre choix : ");
    }

    private static void sauvegarderDonnees(List<Polynome> polynomesASauvegarder) {
        if (polynomesASauvegarder.isEmpty()) {
            System.out.println("Aucun polynôme à sauvegarder.");
            return;
        }

        System.out.println("Ouverture de la fenêtre de sélection de fichier...");

        JFileChooser selecteurFichier = new JFileChooser();
        selecteurFichier.setDialogTitle("Choisir l'emplacement de sauvegarde");
        
        FileNameExtensionFilter filtre = new FileNameExtensionFilter("Fichiers texte (*.txt)", "txt");
        selecteurFichier.setFileFilter(filtre);

        // L'astuce : Utiliser un composant invisible pour forcer la fenêtre devant la console
        java.awt.Window fenetreFantome = new java.awt.Window(null);
        fenetreFantome.setAlwaysOnTop(true); 

        int choixUtilisateur = selecteurFichier.showSaveDialog(fenetreFantome);

        // On libère la mémoire de la fenêtre fantôme
        fenetreFantome.dispose();

        if (choixUtilisateur == JFileChooser.APPROVE_OPTION) {
            File fichierSelectionne = selecteurFichier.getSelectedFile();
            String cheminFichierSauvegarde = fichierSelectionne.getAbsolutePath();

            if (!cheminFichierSauvegarde.toLowerCase().endsWith(".txt")) {
                cheminFichierSauvegarde += ".txt";
            }

            try {
                PersistancePolynome.sauvegarder(polynomesASauvegarder, cheminFichierSauvegarde);
                System.out.println("\nSuccès ! " + polynomesASauvegarder.size() + " polynôme(s) sauvegardé(s) dans :\n" + cheminFichierSauvegarde);
            } catch (Exception e) {
                System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
            }
        } else {
            System.out.println("Sauvegarde annulée par l'utilisateur.");
        }
    }
}