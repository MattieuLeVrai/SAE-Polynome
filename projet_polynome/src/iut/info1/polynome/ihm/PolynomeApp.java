package iut.info1.polynome.ihm;

import iut.info1.polynome.Polynome;
import iut.info1.polynome.outils.PersistancePolynome;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Page d'accueil et interface principale de l'application de gestion de polynômes.
 * Permet de charger, sauvegarder, saisir manuellement et tracer les courbes des polynômes.
 */
public class PolynomeApp extends Application {

    // Liste observable pour mettre à jour automatiquement la vue (ListView)
    private final ObservableList<Polynome> listePolynomes = FXCollections.observableArrayList();
    private final ListView<Polynome> listView = new ListView<>(listePolynomes);
    
    // Éléments du graphique interactif
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    
    // Composants d'affichage des détails
    private final Label lblFormule = new Label("Sélectionnez un polynôme");
    private final Label lblDegre = new Label("-");
    private final Label lblLimites = new Label("-");

    // Bornes de l'axe X modifiables par l'utilisateur
    private final TextField txtXMin = new TextField("-10");
    private final TextField txtXMax = new TextField("10");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bibliothèque Algébrique IR[X] - IUT de Rodez");

        // ==========================================
        // 1. BARRE DE CONTRÔLE SUPÉRIEURE (Top)
        // ==========================================
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");

        Button btnCharger = new Button("Charger Fichier");
        Button btnSauvegarder = new Button("sSauvegarder Fichier");
        
        TextField txtCoeffs = new TextField();
        txtCoeffs.setPromptText("Coefficients : a0 a1 a2... (ex: 2 0 -3)");
        txtCoeffs.setPrefWidth(220);
        Button btnAjouter = new Button("➕ Ajouter Polynôme");

        topBar.getChildren().addAll(btnCharger, btnSauvegarder, new Separator(), txtCoeffs, btnAjouter);

        // ==========================================
        // 2. PANNEAU LATÉRAL GAUCHE (Left)
        // ==========================================
        VBox leftBox = new VBox(10);
        leftBox.setPadding(new Insets(15));
        leftBox.setPrefWidth(260);
        
        Label lblListe = new Label("Polynômes en mémoire :");
        lblListe.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        
        Button btnSupprimer = new Button("❌ Supprimer de la liste");
        btnSupprimer.setMaxWidth(Double.MAX_VALUE);
        
        leftBox.getChildren().addAll(lblListe, listView, btnSupprimer);
        VBox.setVgrow(listView, Priority.ALWAYS); // La liste prend toute la hauteur disponible

        // ==========================================
        // 3. PANNEAU CENTRAL : DÉTAILS & GRAPHIC (Center)
        // ==========================================
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(15));

        // Grille de caractéristiques du polynôme sélectionné
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(10);
        infoGrid.setPadding(new Insets(12));
        infoGrid.setStyle("-fx-background-color: #fafafa; -fx-border-color: #e0e0e0; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        lblFormule.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #2c3e50;");
        
        infoGrid.add(new Label("Équation textuelle :"), 0, 0);
        infoGrid.add(lblFormule, 1, 0);
        infoGrid.add(new Label("Degré :"), 0, 1);
        infoGrid.add(lblDegre, 1, 1);
        infoGrid.add(new Label("Limites (-∞ / +∞) :"), 0, 2);
        infoGrid.add(lblLimites, 1, 2);

        // Ajustement dynamique des fenêtres d'affichage (Bornes X)
        HBox boundsBox = new HBox(10);
        boundsBox.setAlignment(Pos.CENTER_LEFT);
        txtXMin.setPrefWidth(50);
        txtXMax.setPrefWidth(50);
        Button btnRefreshGraph = new Button("🔄 Recalculer Courbe");
        boundsBox.getChildren().addAll(new Label("Fenêtre X min :"), txtXMin, new Label("X max :"), txtXMax, btnRefreshGraph);

        // Configuration graphique
        lineChart.setTitle("Courbe Représentative de P(X)");
        lineChart.setCreateSymbols(false); // Supprime les points ronds disgracieux pour lisser la ligne
        lineChart.setLegendVisible(false); // Pas besoin de légende pour une seule courbe unitaire
        xAxis.setLabel("Axe des Abscisses (X)");
        yAxis.setLabel("Axe des Ordonnées (Y)");

        centerBox.getChildren().addAll(infoGrid, boundsBox, lineChart);
        VBox.setVgrow(lineChart, Priority.ALWAYS);

        // ==========================================
        // 4. ARCHITECTURE ET DISPOSITION GLOBALE
        // ==========================================
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(leftBox);
        root.setCenter(centerBox);

        // ==========================================
        // 5. GESTION DES ÉVÉNEMENTS & ÉCOUTEURS
        // ==========================================

        // Écriture / Ajout d'un polynôme manuellement
        btnAjouter.setOnAction(e -> {
            String input = txtCoeffs.getText().trim();
            if (!input.isEmpty()) {
                try {
                    String[] chaines = input.split("\\s+");
                    double[] coeffs = new double[chaines.length];
                    for (int i = 0; i < chaines.length; i++) {
                        coeffs[i] = Double.parseDouble(chaines[i]);
                    }
                    Polynome nouveau = new Polynome(coeffs);
                    listePolynomes.add(nouveau);
                    txtCoeffs.clear();
                    listView.getSelectionModel().select(nouveau);
                } catch (Exception ex) {
                    afficherMessageErreur("Saisie Incorrecte", "Impossible de décoder les coefficients. Entrez des nombres réels séparés par des espaces.");
                }
            }
        });

        // Changement de sélection dans la liste -> Met à jour l'IHM
        listView.getSelectionModel().selectedItemProperty().addListener((obs, ancSelection, nouvSelection) -> {
            actualiserAffichageAffaire(nouvSelection);
        });

        // Suppression d'un élément sélectionné
        btnSupprimer.setOnAction(e -> {
            Polynome selection = listView.getSelectionModel().getSelectedItem();
            if (selection != null) {
                listePolynomes.remove(selection);
            }
        });

        // Lecture / Chargement d'un fichier texte externe
        btnCharger.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Ouvrir un fichier de données Polynômes");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte (*.txt)", "*.txt"));
            File fichier = fileChooser.showOpenDialog(primaryStage);
            
            if (fichier != null) {
                try {
                    List<Polynome> chargees = PersistancePolynome.charger(fichier.getAbsolutePath());
                    listePolynomes.addAll(chargees);
                    if (!chargees.isEmpty()) {
                        listView.getSelectionModel().select(chargees.get(0));
                    }
                } catch (IOException ex) {
                    afficherMessageErreur("Erreur Critique d'E/S", "Le fichier n'a pas pu être lu :\n" + ex.getMessage());
                }
            }
        });

        // Sauvegarde de l'ensemble de la mémoire dans un fichier texte
        btnSauvegarder.setOnAction(e -> {
            if (listePolynomes.isEmpty()) {
                afficherMessageErreur("Contenu Vide", "Aucun élément présent dans la liste à exporter.");
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le catalogue de polynômes");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte (*.txt)", "*.txt"));
            File fichier = fileChooser.showSaveDialog(primaryStage);
            
            if (fichier != null) {
                try {
                    PersistancePolynome.sauvegarder(listePolynomes, fichier.getAbsolutePath());
                    Alert confirmation = new Alert(Alert.AlertType.INFORMATION, "Données enregistrées avec succès !", ButtonType.OK);
                    confirmation.setHeaderText(null);
                    confirmation.showAndWait();
                } catch (IOException ex) {
                    afficherMessageErreur("Erreur d'Écriture", "Échec de la sauvegarde :\n" + ex.getMessage());
                }
            }
        });

        // Forcer le re-calcul du graphique (changement de bornes par exemple)
        btnRefreshGraph.setOnAction(e -> {
            Polynome selection = listView.getSelectionModel().getSelectedItem();
            if (selection != null) {
                actualiserAffichageAffaire(selection);
            }
        });

        // Lancement de l'affichage de l'application
        Scene scene = new Scene(root, 950, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Calcule les points et régénère les données graphiques et textuelles pour le polynôme choisi.
     */
    private void actualiserAffichageAffaire(Polynome p) {
        lineChart.getData().clear();
        if (p == null) {
            lblFormule.setText("Sélectionnez un polynôme");
            lblDegre.setText("-");
            lblLimites.setText("-");
            return;
        }

        // 1. Renseignement des métadonnées analytiques
        lblFormule.setText(p.toString());
        lblDegre.setText(String.valueOf(p.getDegre()));
        
        String moinsInf = formaterLimite(p.getLimitesMoinsInfini());
        String plusInf  = formaterLimite(p.getLimitesPlusInfini());
        lblLimites.setText(moinsInf + "   /   " + plusInf);

        // 2. Génération mathématique des points de la courbe
        try {
            double xMin = Double.parseDouble(txtXMin.getText());
            double xMax = Double.parseDouble(txtXMax.getText());
            
            if (xMin >= xMax) {
                afficherMessageErreur("Paramétrage Aberrant", "La valeur X minimale doit être strictement inférieure à X maximale.");
                return;
            }

            // Fixer manuellement les limites d'affichage de l'axe pour éviter les sauts brutaux
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(xMin);
            xAxis.setUpperBound(xMax);
            xAxis.setTickUnit((xMax - xMin) / 10);
            
            XYChart.Series<Number, Number> donneesCourbe = new XYChart.Series<>();
            
            // Échantillonnage de 250 points pour assurer une fluidité géométrique sans perte de performance
            double pas = (xMax - xMin) / 250.0;
            for (double x = xMin; x <= xMax; x += pas) {
                double y = p.evaluer(x);
                // Filtrer les valeurs hors-normes ou infinies pour ne pas casser le dessin de JavaFX
                if (!Double.isInfinite(y) && !Double.isNaN(y)) {
                    donneesCourbe.getData().add(new XYChart.Data<>(x, y));
                }
            }
            
            lineChart.getData().add(donneesCourbe);
            
        } catch (NumberFormatException e) {
            afficherMessageErreur("Saisie de Bornes Invalide", "Les valeurs d'intervalles graphiques doivent être des nombres.");
        }
    }

    private String formaterLimite(double limite) {
        if (limite == Double.NEGATIVE_INFINITY) return "-∞";
        if (limite == Double.POSITIVE_INFINITY) return "+∞";
        return String.format("%.2f", limite);
    }

    private void afficherMessageErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}