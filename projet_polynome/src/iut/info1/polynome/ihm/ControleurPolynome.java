/*
 * ControleurPolynome.java                                               15/05/26
 * Iut de Rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome.ihm;

import iut.info1.polynome.OperationPolynome;
import iut.info1.polynome.Polynome;
import iut.info1.polynome.outils.InterpolationPolynomiale;
import iut.info1.polynome.outils.PersistancePolynome;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur JavaFX de l'IHM Polynôme.
 * Fait le lien entre la vue (VuePolynome.fxml) et les classes métier
 * (Polynome, OperationPolynome, PersistancePolynome, InterpolationPolynomiale).
 *
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
public class ControleurPolynome {

    // Saisie par coefficients
    @FXML private TextField champCoefficients;

    // Saisie par racines
    @FXML private TextField champCoeffDominant;
    @FXML private TextField champRacines;

    // Interpolation
    @FXML private TextField champPoints;

    // Liste des polynômes
    @FXML private ListView<String> listePolynomes;

    // Indices pour les opérations
    @FXML private TextField champIndexP1;
    @FXML private TextField champIndexP2;

    // Scalaire
    @FXML private TextField champScalaire;

    // Évaluation en un point
    @FXML private TextField champX;

    // Bornes d'intégration
    @FXML private TextField champBorneA;
    @FXML private TextField champBorneB;

    // Zone de résultat
    @FXML private TextArea zoneResultat;

    // Courbe
    @FXML private TextField champIndexCourbe;
    @FXML private TextField champIntervalleMin;
    @FXML private TextField champIntervalleMax;
    @FXML private LineChart<Number, Number> graphique;
    @FXML private NumberAxis axeX;
    @FXML private NumberAxis axeY;

    // Infos
    @FXML private TextField champIndexInfos;
    @FXML private TextArea  zoneInfos;

    /** Liste interne des polynômes (modèle). */
    private final List<Polynome> polynomes = new ArrayList<>();

    /** Liste observable utilisée par le ListView. */
    private final ObservableList<String> affichage 
    = FXCollections.observableArrayList();

    /** Moteur de calcul (réutilise la classe du projet). */
    private final OperationPolynome operations = new OperationPolynome();

    /** Dernier résultat calculé (peut être ajouté à la liste). */
    private Polynome dernierResultat = null;

    /**
     * Méthode appelée automatiquement par JavaFX après le chargement du FXML.
     * Configure la liste observable.
     */
    @FXML
    public void initialize() {
        listePolynomes.setItems(affichage);
    }

    /**
     * Crée un polynôme à partir des coefficients saisis dans le champ texte.
     * Format attendu : coefficients de an à a0, séparés par des espaces.
     * Exemple : "3.0 -5.0 2.0" donne 3X² - 5X + 2.
     */
    @FXML
    private void ajouterParCoefficients() {
        String saisie = champCoefficients.getText().trim();
        if (saisie.isEmpty()) {
            afficherStatut("Erreur : veuillez saisir au moins un coefficient.");
            return;
        }
        try {
            String[] parties = saisie.split("\\s+");
            // Le tableau attendu par Polynome est de a0 à an (ordre croissant).
            // L'utilisateur saisit de an à a0, donc on inverse.
            double[] coeffs = new double[parties.length];
            for (int i = 0; i < parties.length; i++) {
                // parties[0] = an, doit aller en position (n) du tableau interne
                coeffs[parties.length - 1 - i] = Double.parseDouble(parties[i]);
            }
            ajouterPolynome(new Polynome(coeffs));
            champCoefficients.clear();
            afficherStatut("Polynôme ajouté avec succès.");
        } catch (NumberFormatException e) {
            afficherStatut("Erreur : coefficients invalides." 
                          +"Utilisez des nombres séparés par des espaces.");
        } catch (IllegalArgumentException e) {
            afficherStatut("Erreur : " + e.getMessage());
        }
    }

    /**
     * Crée un polynôme à partir de son coefficient dominant et de ses racines.
     * Format des racines : "racine:multiplicite" séparés par des espaces.
     * Exemple : coefficient = 2.0, racines = "1.0:2 -3.0:1" → 2(X-1)²(X+3)
     */
    @FXML
    private void ajouterParRacines() {
        String saisieCoeff   = champCoeffDominant.getText().trim();
        String saisieRacines = champRacines.getText().trim();
        if (saisieCoeff.isEmpty() || saisieRacines.isEmpty()) {
            afficherStatut("Erreur : remplissez le coefficient" 
                           +"dominant et les racines.");
            return;
        }
        try {
            double   coeffDom = Double.parseDouble(saisieCoeff);
            String[] parties  = saisieRacines.split("\\s+");
            double[] racines  = new double[parties.length];
            int[]    ordres   = new int[parties.length];

            for (int i = 0; i < parties.length; i++) {
                String[] paire = parties[i].split(":");
                if (paire.length != 2) {
                    throw new IllegalArgumentException(
                        "Format invalide pour la racine : " + parties[i] +
                         ". Attendu : valeur:multiplicite");
                }
                racines[i] = Double.parseDouble(paire[0]);
                ordres[i]  = Integer.parseInt(paire[1]);
            }

            ajouterPolynome(new Polynome(coeffDom, racines, ordres));
            champCoeffDominant.clear();
            champRacines.clear();
            afficherStatut("Polynôme ajouté avec succès.");
        } catch (NumberFormatException e) {
            afficherStatut("Erreur : valeur numérique invalide.");
        } catch (IllegalArgumentException e) {
            afficherStatut("Erreur : " + e.getMessage());
        }
    }

    /**
     * Calcule le polynôme d'interpolation de Lagrange passant
     * par les points saisis. Format attendu : "x:y" séparés par des espaces.
     * Exemple : "0:1 1:3 2:7"
     */
    @FXML
    private void interpolerLagrange() {
        String saisie = champPoints.getText().trim();
        if (saisie.isEmpty()) {
            afficherStatut("Erreur : saisissez des points au format x:y" 
                          +"séparés par des espaces.");
            return;
        }
        try {
            String[] parties = saisie.split("\\s+");
            double[] x = new double[parties.length];
            double[] y = new double[parties.length];

            for (int i = 0; i < parties.length; i++) {
                String[] paire = parties[i].split(":");
                if (paire.length != 2) {
                    throw new IllegalArgumentException("Format invalide : "
                              + parties[i] + ". Attendu : x:y");
                }
                x[i] = Double.parseDouble(paire[0]);
                y[i] = Double.parseDouble(paire[1]);
            }

            InterpolationPolynomiale interpolateur = new InterpolationPolynomiale();
            Polynome resultat = interpolateur.interpolerLagrange(x, y);
            afficherResultat("Interpolation de Lagrange : " + resultat, resultat);
            champPoints.clear();
        } catch (NumberFormatException e) {
            afficherStatut("Erreur : valeur numérique invalide dans les points.");
        } catch (IllegalArgumentException e) {
            afficherStatut("Erreur : " + e.getMessage());
        }
    }

    /** Addition P1 + P2. */
    @FXML
    private void additionner() {
        executerOperationBinaire("addition");
    }

    /** Soustraction P1 - P2. */
    @FXML
    private void soustraire() {
        executerOperationBinaire("soustraction");
    }

    /** Multiplication P1 × P2. */
    @FXML
    private void multiplier() {
        executerOperationBinaire("multiplication");
    }

    /** PGCD(P1, P2). */
    @FXML
    private void pgcd() {
        executerOperationBinaire("pgcd");
    }

    /** Quotient de la division euclidienne P1 / P2. */
    @FXML
    private void quotient() {
        executerOperationBinaire("quotient");
    }

    /** Reste de la division euclidienne P1 / P2. */
    @FXML
    private void reste() {
        executerOperationBinaire("reste");
    }

    /**
     * Exécute une opération binaire (entre deux polynômes) identifiée par son nom.
     * @param operation Le nom de l'opération à effectuer
     */
    private void executerOperationBinaire(String operation) {
        int idx1 = lireIndex(champIndexP1, "P1");
        int idx2 = lireIndex(champIndexP2, "P2");
        if (idx1 < 0 || idx2 < 0) return;

        Polynome p1 = polynomes.get(idx1);
        Polynome p2 = polynomes.get(idx2);
        Polynome res;
        String   label;

        try {
            switch (operation) {
                case "addition":
                    res   = operations.addition(p1, p2);
                    label = "P" + idx1 + " + P" + idx2;
                    break;
                case "soustraction":
                    res   = operations.soustraction(p1, p2);
                    label = "P" + idx1 + " - P" + idx2;
                    break;
                case "multiplication":
                    res   = operations.multiplication(p1, p2);
                    label = "P" + idx1 + " × P" + idx2;
                    break;
                case "pgcd":
                    res   = operations.pgcd(p1, p2);
                    label = "PGCD(P" + idx1 + ", P" + idx2 + ")";
                    break;
                case "quotient":
                    res   = operations.division(p1, p2);
                    label = "Quotient(P" + idx1 + " / P" + idx2 + ")";
                    break;
                case "reste":
                    res   = operations.reste(p1, p2);
                    label = "Reste(P" + idx1 + " / P" + idx2 + ")";
                    break;
                default:
                    return;
            }
            afficherResultat(label + " = " + res, res);
        } catch (IllegalArgumentException e) {
            afficherStatut("Erreur : " + e.getMessage());
        }
    }

    /** Dérive le polynôme P1. */
    @FXML
    private void deriver() {
        int idx = lireIndex(champIndexP1, "P1");
        if (idx < 0) return;
        Polynome res = operations.derivee(polynomes.get(idx));
        afficherResultat("Dérivée de P" + idx + " = " + res, res);
    }

    /** Calcule la primitive de P1. */
    @FXML
    private void primitive() {
        int idx = lireIndex(champIndexP1, "P1");
        if (idx < 0) return;
        Polynome res = operations.primitive(polynomes.get(idx));
        afficherResultat("Primitive de P" + idx + " = " + res, res);
    }

    /** Multiplie P1 par le scalaire saisi. */
    @FXML
    private void multiplierScalaire() {
        int idx = lireIndex(champIndexP1, "P1");
        if (idx < 0) return;
        try {
            double scalaire = Double.parseDouble(champScalaire.getText().trim());
            Polynome res = operations.multiplicationScalaire(polynomes.get(idx),
            		                                         scalaire);
            afficherResultat(scalaire + " × P" + idx + " = " + res, res);
        } catch (NumberFormatException e) {
            afficherStatut("Erreur : scalaire invalide.");
        }
    }

    /** Évalue P1 en la valeur x saisie. */
    @FXML
    private void evaluer() {
        int idx = lireIndex(champIndexP1, "P1");
        if (idx < 0) return;
        try {
            double x   = Double.parseDouble(champX.getText().trim());
            double val = operations.calculImageFonction(polynomes.get(idx), x);
            // Pas de polynôme résultat ici, juste une valeur numérique
            zoneResultat.setText("P" + idx + "(" + x + ") = " + val);
            dernierResultat = null;
            afficherStatut("Évaluation effectuée.");
        } catch (NumberFormatException e) {
            afficherStatut("Erreur : x invalide.");
        }
    }

    /** Calcule l'intégrale de P1 sur [a, b]. */
    @FXML
    private void integrale() {
        int idx = lireIndex(champIndexP1, "P1");
        if (idx < 0) return;
        try {
            double a   = Double.parseDouble(champBorneA.getText().trim());
            double b   = Double.parseDouble(champBorneB.getText().trim());
            double val = operations.integrationPolynome(
            		                polynomes.get(idx), a, b);
            zoneResultat.setText("∫[" + a + "," + b + "] P" 
                                 + idx + " dx = " + val);
            dernierResultat = null;
            afficherStatut("Intégrale calculée.");
        } catch (NumberFormatException e) {
            afficherStatut("Erreur : bornes invalides.");
        } catch (IllegalArgumentException e) {
            afficherStatut("Erreur : " + e.getMessage());
        }
    }

    /** Calcule la valeur moyenne de P1 sur [a, b]. */
    @FXML
    private void valeurMoyenne() {
        int idx = lireIndex(champIndexP1, "P1");
        if (idx < 0) return;
        try {
            double a   = Double.parseDouble(champBorneA.getText().trim());
            double b   = Double.parseDouble(champBorneB.getText().trim());
            double val = operations.calculValeurMoyenneIntervalle(
            		                polynomes.get(idx), a, b);
            zoneResultat.setText("Valeur moyenne de P" + idx 
            		             + " sur [" + a + "," + b + "] = " + val);
            dernierResultat = null;
            afficherStatut("Valeur moyenne calculée.");
        } catch (NumberFormatException e) {
            afficherStatut("Erreur : bornes invalides.");
        } catch (IllegalArgumentException e) {
            afficherStatut("Erreur : " + e.getMessage());
        }
    }

    /** Calcule et affiche les racines réelles de P1. */
    @FXML
    private void calculerRacines() {
        int idx = lireIndex(champIndexP1, "P1");
        if (idx < 0) return;
        double[] racines = polynomes.get(idx).getRacines();
        if (racines.length == 0) {
            zoneResultat.setText("P" + idx + " n'a pas de racine réelle.");
        } else {
            StringBuilder sb = new StringBuilder("Racines de P" + idx + " :\n");
            for (double r : racines) {
                sb.append("  x ≈ ").append(String.format("%.9f", r)).append("\n");
            }
            zoneResultat.setText(sb.toString());
        }
        dernierResultat = null;
        afficherStatut("Calcul des racines terminé.");
    }

    
    /**
     * Ajoute le dernier résultat polynomial calculé à la liste des polynômes.
     * Ne fait rien si le dernier résultat est une
     * valeur numérique (pas un polynôme).
     */
    @FXML
    private void ajouterResultat() {
        if (dernierResultat == null) {
            afficherStatut("Aucun résultat polynomial à ajouter" 
            +"(les valeurs numériques ne peuvent pas être ajoutées).");
            return;
        }
        ajouterPolynome(dernierResultat);
        dernierResultat = null;
        afficherStatut("Résultat ajouté à la liste.");
    }

    /** Supprime le polynôme sélectionné dans la liste. */
    @FXML
    private void supprimerPolynome() {
        int idx = listePolynomes.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            afficherStatut("Sélectionnez un polynôme à supprimer.");
            return;
        }
        polynomes.remove(idx);
        rafraichirListe();
        afficherStatut("Polynôme [" + idx + "] supprimé.");
    }

    
    /**
     * Trace la courbe du polynôme sélectionné sur l'intervalle saisi.
     * Génère 300 points régulièrement espacés pour un rendu fluide.
     */
    @FXML
    private void tracerCourbe() {
        int idx = lireIndex(champIndexCourbe, "courbe");
        if (idx < 0) return;
        try {
            double min = Double.parseDouble(champIntervalleMin.getText().trim());
            double max = Double.parseDouble(champIntervalleMax.getText().trim());
            if (min >= max) {
                afficherStatut("Erreur : le minimum doit" 
               + "être inférieur au maximum.");
                return;
            }

            graphique.getData().clear();

            XYChart.Series<Number, Number> serie = new XYChart.Series<>();
            serie.setName("P" + idx);

            int    nbPoints = 300;
            double pas      = (max - min) / nbPoints;

            for (int i = 0; i <= nbPoints; i++) {
                double x = min + i * pas;
                double y = polynomes.get(idx).evaluer(x);
                // On ignore les valeurs infinies pour ne pas déformer le graphe
                if (Double.isFinite(y)) {
                    serie.getData().add(new XYChart.Data<>(x, y));
                }
            }

            graphique.getData().add(serie);
            afficherStatut("Courbe de P" + idx + " tracée sur [" 
                            + min + ", " + max + "].");

        } catch (NumberFormatException e) {
            afficherStatut("Erreur : intervalle invalide.");
        }
    }

    
    /**
     * Ouvre une boîte de dialogue pour charger un fichier .txt de polynômes.
     * Utilise PersistancePolynome.charger() et ajoute les polynômes à la liste.
     */
    @FXML
    private void chargerFichier() {
        File fichier = choisirFichier(false);
        if (fichier == null) return;
        try {
            List<Polynome> charges = PersistancePolynome.charger(
            		                 fichier.getAbsolutePath());
            for (Polynome p : charges) {
                ajouterPolynome(p);
            }
            afficherStatut(charges.size() + " polynôme(s) chargé(s) depuis " 
                          + fichier.getName());
        } catch (IOException e) {
            afficherStatut("Erreur lors du chargement : " + e.getMessage());
        }
    }

    /**
     * Ouvre une boîte de dialogue pour sauvegarder 
     * tous les polynômes dans un fichier .txt.
     * Utilise PersistancePolynome.sauvegarder().
     */
    @FXML
    private void sauvegarderFichier() {
        if (polynomes.isEmpty()) {
            afficherStatut("Aucun polynôme à sauvegarder.");
            return;
        }
        File fichier = choisirFichier(true);
        if (fichier == null) return;

        String chemin = fichier.getAbsolutePath();
        if (!chemin.toLowerCase().endsWith(".txt")) {
            chemin += ".txt";
        }
        try {
            PersistancePolynome.sauvegarder(polynomes, chemin);
            afficherStatut(polynomes.size() + 
            		      " polynôme(s) sauvegardé(s) dans " 
            		      + fichier.getName());
        } catch (IOException e) {
            afficherStatut("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    /** Quitte l'application. */
    @FXML
    private void quitter() {
        System.exit(0);
    }

    
    /**
     * Ajoute un polynôme à la liste du modèle et met à jour l'affichage.
     * @param p Le polynôme à ajouter
     */
    private void ajouterPolynome(Polynome p) {
        polynomes.add(p);
        rafraichirListe();
    }

    /**
     * Reconstruit la liste observable à partir du modèle.
     * Chaque entrée affiche l'index et la représentation textuelle du polynôme.
     */
    private void rafraichirListe() {
        affichage.clear();
        for (int i = 0; i < polynomes.size(); i++) {
            affichage.add("[" + i + "] " + polynomes.get(i).toString());
        }
    }

    /**
     * Affiche un résultat polynomial dans la zone de texte et le mémorise
     * pour un éventuel ajout à la liste.
     * @param message Le texte à afficher
     * @param resultat Le polynôme résultant (peut être null si résultat numérique)
     */
    private void afficherResultat(String message, Polynome resultat) {
        zoneResultat.setText(message);
        dernierResultat = resultat;
        afficherStatut("Calcul effectué.");
    }

    /**
     * Affiche un message dans la zone de résultat (statut ou erreur).
     * @param message Le message à afficher
     */
    private void afficherStatut(String message) {
        zoneResultat.setText(message);
    }

    /**
     * Formate une limite (±Infinity ou réel) pour l'affichage.
     * @param valeur La valeur de la limite
     * @return La chaîne correspondante
     */
    private String formatLimite(double valeur) {
        if (valeur == Double.POSITIVE_INFINITY) return "+∞";
        if (valeur == Double.NEGATIVE_INFINITY) return "-∞";
        return String.valueOf(valeur);
    }
    
    /**
     * Affiche le degré et les limites en ±∞ du polynôme dont l'index est saisi.
     */
    @FXML
    private void afficherInfos() {
        int idx = lireIndex(champIndexInfos, "Infos");
        if (idx < 0) return;
 
        Polynome p = polynomes.get(idx);
 
        String limitemoins = formatLimite(p.getLimitesMoinsInfini());
        String limitePlus  = formatLimite(p.getLimitesPlusInfini());
 
        zoneInfos.setText(
            "Polynôme P" + idx + " :\n"
            + "  Degré          : " + p.getDegre() + "\n"
            + "  lim x→-∞ P(x) : " + limitemoins + "\n"
            + "  lim x→+∞ P(x) : " + limitePlus
        );
    }

    /**
     * Lit un entier depuis un champ texte et vérifie qu'il
     * correspond à un index valide.
     * Affiche un message d'erreur et retourne -1 si la valeur est invalide.
     * @param champ  Le champ TextField à lire
     * @param nom    Le nom du polynôme (pour le message d'erreur)
     * @return L'index valide, ou -1 en cas d'erreur
     */
    private int lireIndex(TextField champ, String nom) {
        try {
            int idx = Integer.parseInt(champ.getText().trim());
            if (idx < 0 || idx >= polynomes.size()) {
                afficherStatut("Erreur : l'index de " + nom + " (" + idx
                    + ") est hors de la liste (0 à " 
                	+ (polynomes.size() - 1) + ").");
                return -1;
            }
            return idx;
        } catch (NumberFormatException e) {
            afficherStatut("Erreur : index de " + nom 
            		       + " invalide. Saisissez un entier.");
            return -1;
        }
    }

    /**
     * Ouvre une boîte de dialogue de sélection de fichier .txt.
     * @param sauvegarder true pour une boîte de sauvegarde,
     * false pour une boîte d'ouverture
     * @return Le fichier sélectionné, ou null si annulé
     */
    private File choisirFichier(boolean sauvegarder) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(sauvegarder ? 
        		        "Sauvegarder les polynômes" : "Charger des polynômes");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Fichiers texte (*.txt)", "*.txt"));

        Stage stage = (Stage) listePolynomes.getScene().getWindow();
        return sauvegarder ? 
        	   chooser.showSaveDialog(stage) : chooser.showOpenDialog(stage);
    }
}