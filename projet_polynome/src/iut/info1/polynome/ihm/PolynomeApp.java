/*
 * PolynomeApp.java                                                      15/05/26
 * Iut de Rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome.ihm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée de l'interface graphique JavaFX de l'application Polynôme.
 * <p>
 * Pour lancer l'application, exécutez cette classe 
 * (elle étend {@link Application}). Elle charge la vue définie dans
 * {@code VuePolynome.fxml} et instancie automatiquement le contrôleur 
 * {@link ControleurPolynome} grâce au mécanisme FXML de JavaFX.
 * </p>
 *
 * <h2>Structure MVC</h2>
 * <ul>
 *   <li><b>Modèle</b>
 *       : {@code iut.info1.polynome.Polynome}, {@code OperationPolynome},
 *       {@code PersistancePolynome},
 *       {@code InterpolationPolynomiale},
 *       {@code SuiteSturm}</li>
 *   <li><b>Vue</b>   
 *       : {@code VuePolynome.fxml} (décrit l'interface en XML)</li>
 *   <li><b>Contrôleur</b> 
 *       : {@code ControleurPolynome.java} (gère les événements)</li>
 * </ul>
 *
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
public class PolynomeApp extends Application {

    /**
     * Méthode principale de JavaFX, appelée après {@link #main(String[])}.
     * Charge le fichier FXML, crée la scène et affiche la fenêtre principale.
     *
     * @param fenetrePrincipale La fenêtre principale fournie par JavaFX.
     * @throws Exception si le fichier FXML est introuvable ou mal formé.
     */
    @Override
    public void start(Stage fenetrePrincipale) throws Exception {
        FXMLLoader chargeur = new FXMLLoader(
            getClass().getResource("VuePolynome.fxml")
        );
        Parent racine = chargeur.load();
        
        fenetrePrincipale.setTitle("Application Polynôme – IUT de Rodez");
        fenetrePrincipale.setScene(new Scene(racine));
        fenetrePrincipale.setMinWidth(900);
        fenetrePrincipale.setMinHeight(600);
        fenetrePrincipale.show();
    }

    /**
     * Point d'entrée Java classique.
     * Délègue à {@link Application#launch(String...)} pour démarrer JavaFX.
     *
     * @param args Les arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        launch(args);
    }
}