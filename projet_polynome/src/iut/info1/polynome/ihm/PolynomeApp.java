/*
 * PolynomeApp                                                     29/05/26
 * IUT de Rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome.ihm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale de l'application graphique IR[X].
 * <p>
 * Cette classe hérite de {@link Application} comme exigé par JavaFX.
 * Elle suit exactement la structure enseignée dans le cours (section 6) :
 * <ol>
 *   <li>Création d'un {@link FXMLLoader}</li>
 *   <li>Chargement du fichier {@code VuePolynome.fxml} (la vue)</li>
 *   <li>Création de la {@link javafx.scene.Scene} à partir du conteneur racine</li>
 *   <li>Configuration du {@link Stage} et appel à {@code show()}</li>
 * </ol>
 * Toute la logique applicative est déléguée au contrôleur
 * {@link ControleurPolynome}, déclaré dans le fichier FXML via
 * {@code fx:controller}.
 *
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
public class PolynomeApp extends Application {

    /**
     * Point d'entrée JavaFX : appelé automatiquement par {@code launch(args)}.
     * C'est ici que l'on charge la vue FXML et que l'on affiche la fenêtre.
     *
     * @param primaryStage La fenêtre principale fournie par JavaFX
     * @throws IOException si le fichier FXML est introuvable ou mal formé
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        // 1. Création du chargeur de code FXML (comme dans le cours section 6.3)
        FXMLLoader chargeurFXML = new FXMLLoader();

        // 2. On indique quel fichier FXML charger (la vue)
        chargeurFXML.setLocation(getClass().getResource("VuePolynome.fxml"));

        // 3. Chargement : le code FXML est traduit en objets Java
        //    Le contrôleur ControleurPolynome est instancié automatiquement
        //    et sa méthode initialize() est appelée juste après.
        Parent racine = chargeurFXML.load();

        // 4. Création de la scène
        Scene scene = new Scene(racine);

        // 5. Configuration de la fenêtre principale
        primaryStage.setTitle("Bibliothèque Algébrique IR[X] — IUT de Rodez");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(720);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);

        // 6. Affichage
        primaryStage.show();
    }

    /**
     * Programme principal : lance l'application JavaFX.
     *
     * @param args arguments non utilisés
     */
    public static void main(String[] args) {
        launch(args);
    }
}
