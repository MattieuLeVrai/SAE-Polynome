package iut.info1.polynome;

import iut.info1.polynome.outils.PersistancePolynome;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Classe principale de l'application de gestion de polynômes.
 * Permet à l'utilisateur de créer interactivement des polynômes (par coefficients ou par racines),
 * de les stocker en mémoire et de les sauvegarder dans un fichier texte.
 * @author Moqué Baptiste
 * @author Liao Mattieu
 * @author Laurençont Yanis
 * @author Higounet Yanis
 */
public class Main {
	
	// Instance unique pour effectuer les opérations
    private static final OperationPolynome GESTIONNAIRE_OPERATIONS = new OperationPolynome();
    
    /**
     * Point d'entrée principal de l'application.
     * Gère la boucle de l'interface utilisateur en mode console, capture les choix du menu
     * et orchestre la création et la sauvegarde des polynômes.
     *
     * @param args Les arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        // Force l'utilisation des fenêtres natives du système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // On ignore si le changement de style échoue
        }

        Scanner entreeUtilisateur = new Scanner(System.in);
        List<Polynome> listeDePolynomes = new ArrayList<>();

        System.out.println("=== Créateur de Polynômes Interactif ===");
        
        boolean sessionActive = true;
        while (sessionActive) {
            afficherMenu();
            
            // On récupère toute la ligne tapée par l'utilisateur
            String ligneSaisie = entreeUtilisateur.nextLine().trim();
            
            // On découpe la ligne par les espaces pour voir s'il y a plusieurs entrées
            String[] morceaux = ligneSaisie.split("\\s+");
            
            // Si l'utilisateur a juste appuyé sur Entrée sans rien taper
            if (ligneSaisie.isEmpty()) {
                System.out.println("Veuillez faire un choix.");
                continue;
            }
            
            // Si le premier morceau n'est pas un nombre
            int choixMenu;
            try {
                choixMenu = Integer.parseInt(morceaux[0]);
            } catch (NumberFormatException e) {
                System.out.println("Veuillez saisir un nombre valide (ex: 1, 2, 3...).");
                continue;
            }
            
            // Si l'utilisateur a par exemple tapé "2 3" ou "1 des_trucs_en_trop"
            if (morceaux.length > 1) {
                System.out.println("Erreur : Veuillez saisir UNIQUEMENT le numéro de votre choix, sans espaces ni caractères supplémentaires.");
                continue; // On rejette la saisie complète et on réaffiche le menu
            }

            // Si on arrive ici, la saisie est parfaitement propre
            switch (choixMenu) {
                case 1:
                    creerParCoefficients(entreeUtilisateur, listeDePolynomes);
                    break;
                case 2:
                    creerParRacines(entreeUtilisateur, listeDePolynomes);
                    break;
                case 3:
                    chargerDonnees(listeDePolynomes);
                    break;
                case 4:
                    gererMenuOperations(entreeUtilisateur, listeDePolynomes);
                    break;
                case 5:
                    sauvegarderDonnees(listeDePolynomes);
                    break; 
                case 6:
                    sessionActive = false;
                    break;
                default:
                    System.out.println("Choix invalide, veuillez choisir un nombre entre 1 et 5.");
            }
        }
        
        System.out.println("Au revoir !");
        entreeUtilisateur.close();
        
        // Force la fermeture des threads graphiques cachés (JFileChooser)
        System.exit(0);
    }

    /**
     * Affiche les différentes options du menu principal dans la console.
     */
    private static void afficherMenu() {
    	System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Créer un polynôme par coefficients (ex: 3.0 -5.0 2.0)");
        System.out.println("2. Créer un polynôme par racines");
        System.out.println("3. Charger des polynômes depuis un fichier");
        System.out.println("4. Effectuer des opérations sur les polynômes");
        System.out.println("5. Sauvegarder la liste de polynômes actuelle");
        System.out.println("6. Quitter");
        System.out.print("Votre choix : ");
    }
    
    /**
     * Sous-menu gérant toutes les opérations disponibles de la classe OperationPolynome
     */
    private static void gererMenuOperations(Scanner sc, List<Polynome> liste) {
        if (liste.isEmpty()) {
            System.out.println("\n[!] Erreur : Vous devez d'abord créer ou charger des polynômes.");
            return;
        }

        boolean dansSousMenu = true;
        while (dansSousMenu) {
            System.out.println("\n--- LISTE DES POLYNÔMES DISPONIBLES ---");
            for (int i = 0; i < liste.size(); i++) {
                System.out.println("[" + i + "] : " + liste.get(i));
            }

            System.out.println("\n--- MENU OPÉRATIONS ---");
            System.out.println("1. Additionner deux polynômes");
            System.out.println("2. Soustraire deux polynômes");
            System.out.println("3. Multiplier par un scalaire (réel)");
            System.out.println("4. Multiplier deux polynômes");
            System.out.println("5. Division Euclidienne (Quotient & Reste)");
            System.out.println("6. Dériver un polynôme");
            System.out.println("7. Calculer l'image d'un nombre x");
            System.out.println("8. Calculer l'intégrale sur [a, b]");
            System.out.println("9. Retour au menu principal");
            System.out.print("Votre choix d'opération : ");

            int choixOp = selectionnerIndex(sc, 1, 9);

            if (choixOp == 9) {
                dansSousMenu = false;
                continue;
            }

            Polynome resultat;
            int idx1, idx2;
            
            switch (choixOp) {
                case 1: // Addition
                    System.out.print("Index du premier polynôme : ");
                    idx1 = selectionnerIndex(sc, 0, liste.size() - 1);
                    System.out.print("Index du deuxième polynôme : ");
                    idx2 = selectionnerIndex(sc, 0, liste.size() - 1);
                    
                    resultat = GESTIONNAIRE_OPERATIONS.addition(liste.get(idx1), liste.get(idx2));
                    ajouterResultatAuPanier(sc, liste, resultat);
                    break;

                case 2: // Soustraction
                    System.out.print("Index du polynôme de base : ");
                    idx1 = selectionnerIndex(sc, 0, liste.size() - 1);
                    System.out.print("Index du polynôme à soustraire : ");
                    idx2 = selectionnerIndex(sc, 0, liste.size() - 1);
                    
                    resultat = GESTIONNAIRE_OPERATIONS.soustraction(liste.get(idx1), liste.get(idx2));
                    ajouterResultatAuPanier(sc, liste, resultat);
                    break;

                case 3: // Multiplication Scalaire
                    System.out.print("Index du polynôme : ");
                    idx1 = selectionnerIndex(sc, 0, liste.size() - 1);
                    System.out.print("Entrez la valeur du scalaire (double) : ");
                    double scalaire = sc.nextDouble();
                    sc.nextLine(); // Nettoyage du tampon
                    
                    resultat = GESTIONNAIRE_OPERATIONS.multiplicationScalaire(liste.get(idx1), scalaire);
                    ajouterResultatAuPanier(sc, liste, resultat);
                    break;

                case 4: // Multiplication de 2 polynômes
                    System.out.print("Index du premier polynôme : ");
                    idx1 = selectionnerIndex(sc, 0, liste.size() - 1);
                    System.out.print("Index du deuxième polynôme : ");
                    idx2 = selectionnerIndex(sc, 0, liste.size() - 1);
                    
                    resultat = GESTIONNAIRE_OPERATIONS.multiplication(liste.get(idx1), liste.get(idx2));
                    ajouterResultatAuPanier(sc, liste, resultat);
                    break;

                case 5: // Division Euclidienne
                    System.out.print("Index du Dividende : ");
                    idx1 = selectionnerIndex(sc, 0, liste.size() - 1);
                    System.out.print("Index du Diviseur : ");
                    idx2 = selectionnerIndex(sc, 0, liste.size() - 1);
                    
                    try {
                        Polynome q = GESTIONNAIRE_OPERATIONS.division(liste.get(idx1), liste.get(idx2));
                        Polynome r = GESTIONNAIRE_OPERATIONS.reste(liste.get(idx1), liste.get(idx2));
                        System.out.println("\nRésultat division euclidienne :");
                        System.out.println(" -> Quotient (Q) = " + q);
                        System.out.println(" -> Reste (R)    = " + r);
                        
                        System.out.print("Voulez-vous enregistrer le quotient ? (O/N) : ");
                        if (sc.nextLine().trim().equalsIgnoreCase("O")) liste.add(q);
                        System.out.print("Voulez-vous enregistrer le reste ? (O/N) : ");
                        if (sc.nextLine().trim().equalsIgnoreCase("O")) liste.add(r);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Erreur : " + e.getMessage());
                    }
                    break;

                case 6: // Dérivation
                    System.out.print("Index du polynôme à dériver : ");
                    idx1 = selectionnerIndex(sc, 0, liste.size() - 1);
                    resultat = GESTIONNAIRE_OPERATIONS.derivee(liste.get(idx1));
                    ajouterResultatAuPanier(sc, liste, resultat);
                    break;

                case 7: // Image f(x)
                    System.out.print("Index du polynôme : ");
                    idx1 = selectionnerIndex(sc, 0, liste.size() - 1);
                    System.out.print("Entrez x : ");
                    double x = sc.nextDouble();
                    sc.nextLine(); // Nettoyage tampon
                    double img = GESTIONNAIRE_OPERATIONS.calculImageFonction(liste.get(idx1), x);
                    System.out.println("\n--> F(" + x + ") = " + img);
                    break;

                case 8: // Intégration
                    System.out.print("Index du polynôme : ");
                    idx1 = selectionnerIndex(sc, 0, liste.size() - 1);
                    System.out.print("Borne a : ");
                    double a = sc.nextDouble();
                    System.out.print("Borne b : ");
                    double b = sc.nextDouble();
                    sc.nextLine(); // Nettoyage tampon
                    double aire = GESTIONNAIRE_OPERATIONS.integrationPolynome(liste.get(idx1), a, b);
                    System.out.println("\n--> Intégrale de " + a + " à " + b + " = " + aire);
                    break;
            }
        }
    }

    /**
     * Méthode utilitaire pour sécuriser la saisie d'un index numérique ou d'un choix
     */
    private static int selectionnerIndex(Scanner sc, int min, int max) {
        while (true) {
            try {
                String saisie = sc.nextLine().trim();
                int valeur = Integer.parseInt(saisie);
                if (valeur >= min && valeur <= max) {
                    return valeur;
                }
                System.out.print("Hors limites. Entrez une valeur entre " + min + " et " + max + " : ");
            } catch (NumberFormatException e) {
                System.out.print("Saisie invalide. Veuillez entrer un nombre : ");
            }
        }
    }

    /**
     * Propose à l'utilisateur de stocker le polynôme fraîchement calculé dans son panier global.
     */
    private static void ajouterResultatAuPanier(Scanner sc, List<Polynome> liste, Polynome res) {
        System.out.println("\nRésultat obtenu : " + res);
        System.out.print("Voulez-vous ajouter ce résultat à votre liste ? (O/N) : ");
        String choix = sc.nextLine().trim();
        if (choix.equalsIgnoreCase("O") || choix.equalsIgnoreCase("oui")) {
            liste.add(res);
            System.out.println("Polynôme sauvegardé en index [" + (liste.size() - 1) + "]");
        }
    }
    
    /**
     * Gère la saisie utilisateur et la création d'un polynôme à partir de ses coefficients.
     * Le polynôme créé est automatiquement ajouté à la liste.
     *
     * @param sc L'instance de {@link Scanner} utilisée pour lire les entrées de la console.
     * @param list La liste de {@link Polynome} dans laquelle ajouter le nouveau polynôme.
     */
    private static void creerParCoefficients(Scanner sc, List<Polynome> list) {
        System.out.print("Entrez les coefficients séparés par des espaces (de an à a0) : ");
        String saisieCoefficients = sc.nextLine();
        String[] segmentsCoefficients = saisieCoefficients.split("\\s+");
        
        double[] tableauCoefficients = new double[segmentsCoefficients.length];
        for (int i = 0; i < segmentsCoefficients.length; i++) {
            tableauCoefficients[i] = Double.parseDouble(segmentsCoefficients[i]);
        }
        
        Polynome nouveauPolynome = new Polynome(tableauCoefficients);
        list.add(nouveauPolynome);
        System.out.println("Ajouté : " + nouveauPolynome);
    }

    /**
     * Gère la saisie utilisateur et la création d'un polynôme à partir de son coefficient 
     * dominant et de ses racines (avec leurs multiplicités).
     * Le polynôme créé est automatiquement ajouté à la liste.
     *
     * @param sc L'instance de {@link Scanner} utilisée pour lire les entrées de la console.
     * @param list La liste de {@link Polynome} dans laquelle ajouter le nouveau polynôme.
     */
    private static void creerParRacines(Scanner sc, List<Polynome> list) {
        System.out.print("Coefficient dominant (an) : ");
        double coefficientDominant = sc.nextDouble();
        
        System.out.print("Nombre de racines distinctes : ");
        int nombreDeRacines = sc.nextInt();
        
        double[] valeursRacines = new double[nombreDeRacines];
        int[] ordresMultiplicite = new int[nombreDeRacines];
        
        for (int i = 0; i < nombreDeRacines; i++) {
            System.out.print("Racine n°" + (i + 1) + " : ");
            valeursRacines[i] = sc.nextDouble();
            System.out.print("Ordre de multiplicité : ");
            ordresMultiplicite[i] = sc.nextInt();
        }
        
        Polynome nouveauPolynome = new Polynome(coefficientDominant, valeursRacines, ordresMultiplicite);
        list.add(nouveauPolynome);
        System.out.println("Ajouté : " + nouveauPolynome);
    }
    
    /**
     * Ouvre une boîte de dialogue pour sélectionner un fichier de polynômes (.txt)
     * et ajoute les polynômes lus à la liste de l'application en utilisant PersistancePolynome.
     *
     * @param listeACompleter La liste actuelle à laquelle ajouter les nouveaux polynômes. 
     */
    private static void chargerDonnees(List<Polynome> listeACompleter) {
        System.out.println("Ouverture de la fenêtre de sélection de fichier...");

        JFileChooser selecteurFichier = new JFileChooser();
        selecteurFichier.setDialogTitle("Choisir le fichier de polynômes à charger");
        
        FileNameExtensionFilter filtre = new FileNameExtensionFilter("Fichiers texte (*.txt)", "txt");
        selecteurFichier.setFileFilter(filtre);

        java.awt.Window fenetreFantome = new java.awt.Window(null);
        fenetreFantome.setAlwaysOnTop(true); 

        int choixUtilisateur = selecteurFichier.showOpenDialog(fenetreFantome);
        fenetreFantome.dispose();

        if (choixUtilisateur == JFileChooser.APPROVE_OPTION) {
            File fichierSelectionne = selecteurFichier.getSelectedFile();
            String chemin = fichierSelectionne.getAbsolutePath();

            try {
                // Liaison avec la méthode existante de PersistancePolynome
                List<Polynome> polynomesCharges = PersistancePolynome.charger(chemin);
                
                // Fusion avec votre panier global
                listeACompleter.addAll(polynomesCharges);
                
                System.out.println("\nSuccès ! " + polynomesCharges.size() + " polynôme(s) chargé(s) depuis le fichier.");
                for (Polynome p : polynomesCharges) {
                    System.out.println(" -> " + p);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement : " + e.getMessage());
            }
        } else {
            System.out.println("Chargement annulé par l'utilisateur.");
        }
    }
    
    /**
     * Ouvre une boîte de dialogue graphique permettant à l'utilisateur de choisir 
     * un emplacement pour exporter la liste de polynômes au format texte (.txt).
     *
     * @param polynomesASauvegarder La liste des {@link Polynome} à enregistrer.
     */
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

        // Utiliser un composant invisible pour forcer la fenêtre devant la console
        java.awt.Window fenetreFantome = new java.awt.Window(null);
        fenetreFantome.setAlwaysOnTop(true); 

        int choixUtilisateur = selecteurFichier.showSaveDialog(fenetreFantome);
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