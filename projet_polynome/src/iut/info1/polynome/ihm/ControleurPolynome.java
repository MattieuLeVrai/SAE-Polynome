/*
 * ControleurPolynome                                              29/05/26
 * IUT de Rodez, pas de copyright ni copyleft
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
import java.util.List;

/**
 * Contrôleur de la vue VuePolynome.fxml.
 * Gère toutes les interactions utilisateur et délègue les calculs à
 * {@link OperationPolynome}, {@link InterpolationPolynomiale} et
 * {@link PersistancePolynome} — exactement comme le faisait Main.java.
 *
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
public class ControleurPolynome {

    // Barre du haut
    @FXML private TextField txtCoeffs;
    @FXML private ComboBox<String> comboTypeSaisie;
    @FXML private Button    btnAjouter;
    @FXML private Button    btnCharger;
    @FXML private Button    btnSauvegarder;

    // Panneau gauche
    @FXML private ListView<Polynome> listView;
    @FXML private Label lblFormule;
    @FXML private Label lblDegre;
    @FXML private Label lblLimMoins;
    @FXML private Label lblLimPlus;
    @FXML private Label lblRacines;

    // Graphique
    @FXML private LineChart<Number, Number> lineChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private TextField txtXMin;
    @FXML private TextField txtXMax;

    // Onglet Opérations binaires
    @FXML private ComboBox<Polynome> comboP1;
    @FXML private ComboBox<Polynome> comboP2;
    @FXML private Label lblResultatOp;

    // Onglet Calculs
    @FXML private ComboBox<Polynome> comboCalcP1;
    @FXML private TextField txtValeurX;
    @FXML private TextField txtBorneA;
    @FXML private TextField txtBorneB;
    @FXML private Label lblResultatCalc;

    // Onglet Interpolation
    @FXML private TextField txtPointsInterp;
    @FXML private Label     lblResultatInterp;

    // Barre de statut
    @FXML private Label lblStatut;

    // ===================== Attributs internes =====================

    /** Liste observable liée à la ListView et aux ComboBox (comme dans le cours). */
    private final ObservableList<Polynome> listePolynomes =
            FXCollections.observableArrayList();

    /** Gestionnaire d'opérations — même instance que dans Main.java */
    private final OperationPolynome operations = new OperationPolynome();

    /** Dernier polynôme calculé dans l'onglet Opérations (pour pouvoir l'ajouter). */
    private Polynome dernierResultatOp   = null;

    /** Dernier polynôme calculé dans l'onglet Calculs (primitive / dérivée). */
    private Polynome dernierResultatCalc = null;

    /** Dernier polynôme calculé par interpolation. */
    private Polynome dernierResultatInterp = null;

    // ===================== Méthode initialize =====================

    /**
     * Appelée AUTOMATIQUEMENT après le chargement du fichier FXML
     * (comme dans le cours, section 6.2 — méthode initialize).
     * On y initialise les composants dynamiques.
     */
    @FXML
    private void initialize() {
        // On associe la même liste observable à la ListView et aux 3 ComboBox
        listView.setItems(listePolynomes);
        comboP1.setItems(listePolynomes);
        comboP2.setItems(listePolynomes);
        comboCalcP1.setItems(listePolynomes);

        // Configuration du graphique
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        lineChart.setTitle("Courbe de P(X)");
        lineChart.setStyle("-fx-background-color: #16213e;");

        // Écoute la sélection dans la liste — comme dans PolynomeApp.java du cours
        listView.getSelectionModel().selectedItemProperty().addListener(
            (obs, ancien, nouveau) -> actualiserInfos(nouveau)
        );

        statut("Prêt. Entrez des coefficients ou chargez un fichier.");
        
        
        
        
        // Gestion dynamique du texte d'aide selon le mode de saisie
        if (comboTypeSaisie != null) {
            comboTypeSaisie.getSelectionModel().selectedItemProperty().addListener((obs, ancienMode, nouveauMode) -> {
                if ("Par racines".equals(nouveauMode)) {
                    txtCoeffs.setPromptText("ex: 2.0 3:1 -1:2 (coeff_dominant racine:mult...)");
                } else {
                    txtCoeffs.setPromptText("ex: -6 1 1");
                }
            });
        }
    }

    // ===================== Gestion de la liste =====================

    /**
     * Ajoute un polynôme saisi par coefficients.
     * Même logique que {@code creerParCoefficients} dans Main.java.
     */
    @FXML
    private void gererClicAjouter() {
        String saisie = txtCoeffs.getText().trim();
        if (saisie.isEmpty()) {
            erreur("Saisie vide", "Veuillez entrer des valeurs avant d'ajouter.");
            return;
        }

        try {
            Polynome nouveauPolynome;

            // MODE 1 : Saisie par racines
            if (comboTypeSaisie != null && "Par racines".equals(comboTypeSaisie.getValue())) {
                String[] parts = saisie.split("\\s+");
                
                // Le premier nombre est toujours le coefficient dominant
                double coeffDominant = Double.parseDouble(parts[0]);
                nouveauPolynome = new Polynome(new double[]{coeffDominant});
                
                // On parcourt les couples racine:multiplicité
                for (int i = 1; i < parts.length; i++) {
                    String[] paire = parts[i].split(":");
                    if (paire.length != 2) {
                        throw new IllegalArgumentException("Format de racine invalide. Attendu -> racine:multiplicité (ex: 3:1)");
                    }
                    
                    double racine = Double.parseDouble(paire[0]);
                    int multiplicite = Integer.parseInt(paire[1]);
                    
                    if (multiplicite < 0) {
                        throw new IllegalArgumentException("La multiplicité ne peut pas être négative.");
                    }
                    
                    // En ordre décroissant, {1.0, -racine} représente (1.0*X - racine)
                    Polynome facteur = new Polynome(new double[]{1.0, -racine});
                    
                    // On multiplie autant de fois que l'ordre de multiplicité
                    for (int m = 0; m < multiplicite; m++) {
                        nouveauPolynome = op.multiplication(nouveauPolynome, facteur);
                    }
                }
                
            // MODE 2 : Saisie classique par coefficients
            } else {
                String[] tokens = saisie.split("\\s+");
                double[] coeffs = new double[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    coeffs[i] = Double.parseDouble(tokens[i]);
                }
                nouveauPolynome = new Polynome(coeffs);
            }

            // Ajout final dans votre liste et mise à jour de l'IHM
            listePolynomes.add(nouveauPolynome);
            listView.getSelectionModel().select(nouveauPolynome);
            txtCoeffs.clear();
            statut("Polynôme ajouté avec succès : " + nouveauPolynome);

        } catch (NumberFormatException e) {
            erreur("Erreur de format", "Veuillez vérifier que vous n'avez inséré que des nombres valides.");
        } catch (Exception e) {
            erreur("Erreur de construction", e.getMessage());
        }
    }

    /**
     * Supprime le polynôme sélectionné dans la liste.
     */
    @FXML
    private void gererClicSupprimer() {
        Polynome selection = listView.getSelectionModel().getSelectedItem();
        if (selection != null) {
            listePolynomes.remove(selection);
            actualiserInfos(null);
            statut("Polynôme supprimé.");
        }
    }

    // ===================== Persistance (comme PersistancePolynome dans Main.java) =====================

    /**
     * Charge des polynômes depuis un fichier texte.
     * Même logique que {@code chargerDonnees} dans Main.java, mais avec
     * FileChooser JavaFX au lieu de JFileChooser.
     */
    @FXML
    private void gererClicCharger() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Ouvrir un fichier de polynômes");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Fichiers texte (*.txt)", "*.txt"));

        File fichier = fc.showOpenDialog(null);
        if (fichier != null) {
            try {
                List<Polynome> charges = PersistancePolynome.charger(fichier.getAbsolutePath());
                listePolynomes.addAll(charges);
                if (!charges.isEmpty()) {
                    listView.getSelectionModel().select(charges.get(0));
                }
                statut(charges.size() + " polynôme(s) chargé(s) depuis " + fichier.getName());
            } catch (IOException ex) {
                erreur("Erreur de lecture", ex.getMessage());
            }
        }
    }

    /**
     * Sauvegarde la liste dans un fichier texte.
     * Même logique que {@code sauvegarderDonnees} dans Main.java.
     */
    @FXML
    private void gererClicSauvegarder() {
        if (listePolynomes.isEmpty()) {
            erreur("Liste vide", "Aucun polynôme à sauvegarder.");
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer les polynômes");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Fichiers texte (*.txt)", "*.txt"));

        File fichier = fc.showSaveDialog(null);
        if (fichier != null) {
            try {
                String chemin = fichier.getAbsolutePath();
                if (!chemin.toLowerCase().endsWith(".txt")) chemin += ".txt";
                PersistancePolynome.sauvegarder(listePolynomes, chemin);
                info("Sauvegarde réussie",
                        listePolynomes.size() + " polynôme(s) sauvegardé(s).");
                statut("Sauvegardé dans " + fichier.getName());
            } catch (IOException ex) {
                erreur("Erreur d'écriture", ex.getMessage());
            }
        }
    }

    // ===================== Graphique =====================

    /**
     * Recalcule la courbe du polynôme sélectionné sur [xMin, xMax].
     * Même logique que {@code actualiserAffichageAffaire} dans PolynomeApp.java.
     */
    @FXML
    private void gererClicRecalculer() {
        actualiserInfos(listView.getSelectionModel().getSelectedItem());
    }

    // ===================== Onglet Opérations binaires =====================

    /** Addition — appelle {@code operations.addition(p1, p2)}. */
    @FXML
    private void gererAddition() {
        Polynome[] paire = getP1P2();
        if (paire == null) return;
        dernierResultatOp = operations.addition(paire[0], paire[1]);
        afficherResultatOp("P1 + P2 = " + dernierResultatOp);
    }

    /** Soustraction — appelle {@code operations.soustraction(p1, p2)}. */
    @FXML
    private void gererSoustraction() {
        Polynome[] paire = getP1P2();
        if (paire == null) return;
        dernierResultatOp = operations.soustraction(paire[0], paire[1]);
        afficherResultatOp("P1 − P2 = " + dernierResultatOp);
    }

    /** Multiplication — appelle {@code operations.multiplication(p1, p2)}. */
    @FXML
    private void gererMultiplication() {
        Polynome[] paire = getP1P2();
        if (paire == null) return;
        dernierResultatOp = operations.multiplication(paire[0], paire[1]);
        afficherResultatOp("P1 × P2 = " + dernierResultatOp);
    }

    /** Division euclidienne (quotient) — appelle {@code operations.division(p1, p2)}. */
    @FXML
    private void gererDivision() {
        Polynome[] paire = getP1P2();
        if (paire == null) return;
        try {
            dernierResultatOp = operations.division(paire[0], paire[1]);
            afficherResultatOp("Quotient = " + dernierResultatOp);
        } catch (IllegalArgumentException e) {
            erreur("Division impossible", e.getMessage());
        }
    }

    /** Reste euclidien — appelle {@code operations.reste(p1, p2)}. */
    @FXML
    private void gererReste() {
        Polynome[] paire = getP1P2();
        if (paire == null) return;
        try {
            dernierResultatOp = operations.reste(paire[0], paire[1]);
            afficherResultatOp("Reste = " + dernierResultatOp);
        } catch (IllegalArgumentException e) {
            erreur("Reste impossible", e.getMessage());
        }
    }

    /** PGCD — appelle {@code operations.pgcd(p1, p2)}. */
    @FXML
    private void gererPgcd() {
        Polynome[] paire = getP1P2();
        if (paire == null) return;
        dernierResultatOp = operations.pgcd(paire[0], paire[1]);
        afficherResultatOp("PGCD = " + dernierResultatOp);
    }

    /** Ajoute le résultat d'une opération binaire à la liste. */
    @FXML
    private void gererAjouterResultat() {
        ajouterDansListe(dernierResultatOp, "résultat opération");
    }

    // ===================== Onglet Calculs =====================

    /** Dérivée — appelle {@code operations.derivee(p)}. */
    @FXML
    private void gererDerivee() {
        Polynome p = comboCalcP1.getValue();
        if (p == null) { erreur("Aucun polynôme", "Sélectionnez P1."); return; }
        dernierResultatCalc = operations.derivee(p);
        afficherResultatCalc("P'(X) = " + dernierResultatCalc);
    }

    /** Primitive — appelle {@code operations.primitive(p)}. */
    @FXML
    private void gererPrimitive() {
        Polynome p = comboCalcP1.getValue();
        if (p == null) { erreur("Aucun polynôme", "Sélectionnez P1."); return; }
        dernierResultatCalc = operations.primitive(p);
        afficherResultatCalc("∫P(X)dX = " + dernierResultatCalc);
    }

    /** Image P(x) — appelle {@code operations.calculImageFonction(p, x)}. */
    @FXML
    private void gererImage() {
        Polynome p = comboCalcP1.getValue();
        if (p == null) { erreur("Aucun polynôme", "Sélectionnez P1."); return; }
        try {
            double x = Double.parseDouble(txtValeurX.getText().trim());
            double res = operations.calculImageFonction(p, x);
            dernierResultatCalc = null;
            afficherResultatCalc("P(" + x + ") = " + res);
        } catch (NumberFormatException e) {
            erreur("Valeur x invalide", "Entrez un nombre réel dans le champ x.");
        }
    }

    /** Intégrale définie — appelle {@code operations.integrationPolynome(p, a, b)}. */
    @FXML
    private void gererIntegrale() {
        Polynome p = comboCalcP1.getValue();
        if (p == null) { erreur("Aucun polynôme", "Sélectionnez P1."); return; }
        try {
            double a = Double.parseDouble(txtBorneA.getText().trim());
            double b = Double.parseDouble(txtBorneB.getText().trim());
            double res = operations.integrationPolynome(p, a, b);
            dernierResultatCalc = null;
            afficherResultatCalc("∫[" + a + "," + b + "] P(x)dx = " + String.format("%.6f", res));
        } catch (NumberFormatException e) {
            erreur("Bornes invalides", "Entrez des nombres réels dans a et b.");
        }
    }

    /** Valeur moyenne — appelle {@code operations.calculValeurMoyenneIntervalle(p, a, b)}. */
    @FXML
    private void gererValeurMoyenne() {
        Polynome p = comboCalcP1.getValue();
        if (p == null) { erreur("Aucun polynôme", "Sélectionnez P1."); return; }
        try {
            double a = Double.parseDouble(txtBorneA.getText().trim());
            double b = Double.parseDouble(txtBorneB.getText().trim());
            double res = operations.calculValeurMoyenneIntervalle(p, a, b);
            dernierResultatCalc = null;
            afficherResultatCalc("Valeur moy. sur [" + a + "," + b + "] = " + String.format("%.6f", res));
        } catch (NumberFormatException e) {
            erreur("Bornes invalides", "Entrez des nombres réels dans a et b.");
        } catch (IllegalArgumentException e) {
            erreur("Erreur", e.getMessage());
        }
    }

    /** Ajoute le résultat de l'onglet Calculs à la liste. */
    @FXML
    private void gererAjouterResultatCalc() {
        ajouterDansListe(dernierResultatCalc, "résultat calcul");
    }

    // ===================== Onglet Interpolation =====================

    /**
     * Interpolation de Lagrange — appelle {@code InterpolationPolynomiale.interpolerLagrange}.
     * Même logique que {@code interpolerLagrange} dans Main.java.
     * Format attendu : "x0,y0  x1,y1  x2,y2 ..."
     */
    @FXML
    private void gererInterpolation() {
        String input = txtPointsInterp.getText().trim();
        if (input.isEmpty()) {
            erreur("Aucun point", "Entrez des points au format : x0,y0  x1,y1 ...");
            return;
        }
        try {
            String[] points = input.split("\\s+");
            double[] x = new double[points.length];
            double[] y = new double[points.length];

            for (int i = 0; i < points.length; i++) {
                String[] paire = points[i].split(",");
                if (paire.length != 2) throw new NumberFormatException("Format invalide");
                x[i] = Double.parseDouble(paire[0]);
                y[i] = Double.parseDouble(paire[1]);
            }

            InterpolationPolynomiale interp = new InterpolationPolynomiale();
            dernierResultatInterp = interp.interpolerLagrange(x, y);
            lblResultatInterp.setText(dernierResultatInterp.toString());
            statut("Interpolation calculée : " + dernierResultatInterp);
        } catch (NumberFormatException e) {
            erreur("Format invalide",
                    "Utilisez le format : x0,y0  x1,y1  x2,y2\nExemple : 0,1  1,3  2,7");
        } catch (IllegalArgumentException e) {
            erreur("Erreur d'interpolation", e.getMessage());
        }
    }

    /** Ajoute le polynôme interpolé à la liste. */
    @FXML
    private void gererAjouterInterp() {
        ajouterDansListe(dernierResultatInterp, "polynôme interpolé");
    }

    // ===================== Méthodes privées utilitaires =====================

    /**
     * Met à jour les labels d'info et le graphique pour le polynôme p.
     * Équivalent de {@code actualiserAffichageAffaire} dans PolynomeApp.java.
     */
    private void actualiserInfos(Polynome p) {
        lineChart.getData().clear();

        if (p == null) {
            lblFormule.setText("-");
            lblDegre.setText("-");
            lblLimMoins.setText("-");
            lblLimPlus.setText("-");
            lblRacines.setText("-");
            return;
        }

        // Infos textuelles
        lblFormule.setText(p.toString());
        lblDegre.setText(String.valueOf(p.getDegre()));
        lblLimMoins.setText(formaterLimite(p.getLimitesMoinsInfini()));
        lblLimPlus.setText(formaterLimite(p.getLimitesPlusInfini()));

        // Racines (peut être long — on limite l'affichage)
        double[] racines = p.getRacines();
        if (racines.length == 0) {
            lblRacines.setText("aucune racine réelle");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < racines.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(String.format("%.4f", racines[i]));
            }
            lblRacines.setText(sb.toString());
        }

        // Courbe
        try {
            double xMin = Double.parseDouble(txtXMin.getText().trim());
            double xMax = Double.parseDouble(txtXMax.getText().trim());
            if (xMin >= xMax) {
                erreur("Bornes invalides", "X min doit être < X max.");
                return;
            }
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(xMin);
            xAxis.setUpperBound(xMax);
            xAxis.setTickUnit((xMax - xMin) / 10.0);

            XYChart.Series<Number, Number> serie = new XYChart.Series<>();
            double pas = (xMax - xMin) / 300.0;
            for (double xi = xMin; xi <= xMax; xi += pas) {
                double yi = p.evaluer(xi);
                if (!Double.isInfinite(yi) && !Double.isNaN(yi)) {
                    serie.getData().add(new XYChart.Data<>(xi, yi));
                }
            }
            lineChart.getData().add(serie);

            // Style de la courbe
            if (!serie.getData().isEmpty()) {
                serie.getNode().setStyle("-fx-stroke: #e94560; -fx-stroke-width: 2px;");
            }
        } catch (NumberFormatException e) {
            erreur("Bornes invalides", "Entrez des nombres réels dans X min et X max.");
        }
    }

    /**
     * Récupère P1 et P2 dans les ComboBox de l'onglet Opérations.
     * @return tableau [p1, p2] ou null si l'un est absent.
     */
    private Polynome[] getP1P2() {
        Polynome p1 = comboP1.getValue();
        Polynome p2 = comboP2.getValue();
        if (p1 == null || p2 == null) {
            erreur("Polynômes manquants", "Sélectionnez P1 et P2 dans les listes déroulantes.");
            return null;
        }
        return new Polynome[]{p1, p2};
    }

    /** Affiche un résultat dans l'onglet Opérations et met à jour le statut. */
    private void afficherResultatOp(String texte) {
        lblResultatOp.setText(texte);
        statut(texte);
    }

    /** Affiche un résultat dans l'onglet Calculs et met à jour le statut. */
    private void afficherResultatCalc(String texte) {
        lblResultatCalc.setText(texte);
        statut(texte);
    }

    /** Ajoute un polynôme à la liste s'il est non null. */
    private void ajouterDansListe(Polynome p, String origine) {
        if (p == null) {
            erreur("Aucun résultat", "Effectuez d'abord un calcul dans cet onglet.");
            return;
        }
        listePolynomes.add(p);
        listView.getSelectionModel().select(p);
        statut("Ajouté dans la liste [" + origine + "] : " + p);
    }

    /** Formate une limite infinie ou réelle pour l'affichage. */
    private String formaterLimite(double val) {
        if (val == Double.NEGATIVE_INFINITY) return "-∞";
        if (val == Double.POSITIVE_INFINITY) return "+∞";
        return String.format("%.2f", val);
    }

    /** Affiche un message dans la barre de statut. */
    private void statut(String msg) {
        lblStatut.setText(msg);
    }

    /** Affiche une boîte d'alerte d'erreur (comme dans le cours section 5.1). */
    private void erreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Affiche une boîte d'information (comme dans le cours section 5.1). */
    private void info(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
