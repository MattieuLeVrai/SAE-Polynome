/*
 * PersistancePolynome                                              11/05/26
 * Iut de rodez, pas de copyright ni copyleft
 */

package iut.info1.polynome.outils;

import java.io.*;
import iut.info1.polynome.Polynome;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère la persistance des polynômes dans des fichiers texte.
 *
 * <h2>Format du fichier</h2>
 * Chaque polynôme occupe une ligne. Deux formats sont supportés :
 *
 * <ul>
 *   <li><b>Format coefficients</b> (mot-clé {@code COEFF}) :<br>
 *       {@code COEFF an an-1 an-2 ... a0}<br>
 *       où a0 est le coefficient de degré 0, a1 celui de degré 1, etc.<br>
 *       Exemple : {@code COEFF 3.2 -5.0 1.0}  →  P(X) = 3.2X² − 5X + 1</li> 
 *
 *   <li><b>Format racines</b> (mot-clé {@code RACINES}) :<br>
 *       {@code RACINES coeffDominant r1:k1 r2:k2 ...}<br>
 *       où coeffDominant est le coefficient du monôme de plus haut degré,
 *       r_i la valeur de la racine et k_i son ordre de multiplicité.<br>
 *       Exemple : {@code RACINES 2.0 1.0:2 -3.0:1}  →  P(X) = 2(X−1)²(X+3)</li>
 * </ul> 
 *
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste
 */
public class PersistancePolynome {

    /** Mot-clé identifiant le format coefficients. */
    private static final String PREFIXE_COEFF   = "COEFF";

    /** Mot-clé identifiant le format racines. */
    private static final String PREFIXE_RACINES = "RACINES";

    /**
     * Sauvegarde une liste de polynômes dans
     * un fichier texte au format coefficients.
     *
     * @param polynomes La liste de polynômes à sauvegarder (non null)
     * @param chemin    Le chemin du fichier de destination
     * @throws IOException en cas d'erreur d'écriture
     * @throws IllegalArgumentException si la liste est null
     */
    public static void sauvegarder(List<Polynome> polynomes, String chemin)
            throws IOException {
        if (polynomes == null) {
            throw new IllegalArgumentException(
                "La liste de polynômes ne doit pas être null.");
        }

        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(new FileWriter(chemin)))) {

        	writer.println(
        	  "# ============================================================");
        	writer.println("#   Fichier de polynomes - Bibliotheque IR[X]");
        	writer.println(
        	  "# ============================================================");
        	writer.println("#");
        	writer.println("#  FORMAT 1 - COEFF");
        	writer.println("#  -----------------");
        	writer.println("#  Syntaxe  : COEFF  an  an-1  ...  a1  a0");
        	writer.println("#  Exemple  : COEFF  3.2  -5.0  1.0");
        	writer.println("#  Resultat :  P(X) = 3.2X^2 - 5.0X + 1.0");
        	writer.println("#");
        	writer.println("#  FORMAT 2 - RACINES");
        	writer.println("#  -------------------");
        	writer.println(
        	  "#  Syntaxe  : RACINES  coeffDom  racine:multiplicite  ...");
        	writer.println("#  Exemple  : RACINES  2.0  1.0:2  -3.0:1");
        	writer.println("#  Resultat :  P(X) = 2.0 * (X - 1.0)^2 * (X + 3.0)");
        	writer.println("#");
        	writer.println("#  NOTE : lignes vides et lignes '#' sont ignorees");
        	writer.println(
        	  "# ============================================================");
        	writer.println("#  VOS POLYNOMES CI-DESSOUS :");
        	writer.println(
        	  "# ============================================================");
        	writer.println();

            for (Polynome p : polynomes) {
                writer.println(serialiserCoefficients(p));
            }
        }
    }

    /**
     * Sauvegarde une liste de polynômes en format racines dans un fichier texte.
     *
     * <p>Chaque polynôme est décrit par son tableau de racines, les ordres de
     * multiplicité associés et son coefficient dominant.
     *
     * @param racinesListe Tableau de tableaux de racines (une ligne par polynôme)
     * @param ordresListe  Tableau de tableaux d'ordres
     * @param coeffsDominants  Coefficients dominants
     * @param chemin   Chemin du fichier de destination
     * @throws IOException    en cas d'erreur d'écriture
     * @throws IllegalArgumentException si les tableaux ont
     * des tailles incohérentes
     */
    public static void sauvegarderParRacines(double[][] racinesListe,
                                              int[][]    ordresListe,
                                              double[]   coeffsDominants,
                                              String     chemin)
            throws IOException {

        if (racinesListe == null || ordresListe == null 
            || coeffsDominants == null) {
            throw new IllegalArgumentException(
                "Les tableaux de données ne doivent pas être null.");
        }
        if (racinesListe.length != ordresListe.length
                || racinesListe.length != coeffsDominants.length) {
            throw new IllegalArgumentException(
                "Les tableaux racinesListe, ordresListe et coeffsDominants "
              + "doivent avoir la même taille.");
        }

        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(new FileWriter(chemin)))) {

        	writer.println(
        	  "# ============================================================");
        	writer.println("#   Fichier de polynomes - Bibliotheque IR[X]");
        	writer.println(
        	  "# ============================================================");
        	writer.println("#");
        	writer.println("#  FORMAT 2 - RACINES");
        	writer.println("#  -------------------");
        	writer.println(
        	  "#  Syntaxe  : RACINES  coeffDom  racine:multiplicite  ...");
        	writer.println("#  Exemple  : RACINES  2.0  1.0:2  -3.0:1");
        	writer.println("#  Resultat :  P(X) = 2.0 * (X - 1.0)^2 * (X + 3.0)");
        	writer.println("#");
        	writer.println("#  NOTE : lignes vides et lignes '#' sont ignorees");
        	writer.println(
        	  "# ============================================================");
        	writer.println("#  VOS POLYNOMES CI-DESSOUS :");
        	writer.println(
        	  "# ============================================================");
        	writer.println();

            for (int i = 0; i < racinesListe.length; i++) {
                writer.println(serialiserRacines(
                    racinesListe[i], ordresListe[i], coeffsDominants[i]));
            }
        }
    }

    /**
     * Ajoute un polynôme (format coefficients) à un fichier existant.
     *
     * @param p Le polynôme à ajouter
     * @param chemin Le chemin du fichier
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void ajouterPolynome(Polynome p, String chemin)
            throws IOException {
        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(new FileWriter(chemin, true)))) {
            writer.println(serialiserCoefficients(p));
        }
    }

    /**
     * Charge une liste de polynômes depuis un fichier texte.
     * Les formats {@code COEFF} et {@code RACINES} sont tous deux reconnus.
     *
     * @param chemin Le chemin du fichier source
     * @return La liste des polynômes lus (dans l'ordre du fichier)
     * @throws IOException  en cas d'erreur de lecture ou de format invalide
     * @throws IllegalArgumentException si un polynôme ne peut pas être construit
     *                                  (coefficients invalides, ordres ≤ 0…)
     */
    public static List<Polynome> charger(String chemin) throws IOException {
        List<Polynome> polynomes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(chemin))) {
            String  ligne;
            int     numeroLigne = 0;

            while ((ligne = reader.readLine()) != null) {
                numeroLigne++;
                ligne = ligne.trim();

                // Ignorer les lignes vides et les commentaires
                if (ligne.isEmpty() || ligne.startsWith("#")) {
                    continue;
                }

                try {
                    Polynome p = deserialiser(ligne);
                    polynomes.add(p);
                } catch (NumberFormatException e) {
                    throw new IOException(
                        "Erreur de format à la ligne " + numeroLigne
                        + " : valeur numérique invalide. Ligne : \""
                        + ligne + "\"", e);
                } catch (IllegalArgumentException e) {
                    throw new IOException(
                        "Erreur à la ligne " + numeroLigne + " : "
                        + e.getMessage() + ". Ligne : \"" + ligne + "\"", e);
                }
            }
        }

        return polynomes;
    }

    /**
     * Sérialise un polynôme au format {@code COEFF}.
     * Ordre : commence par la fin pour afficher de an à a0.
     */
    private static String serialiserCoefficients(Polynome p) {
        StringBuilder sb = new StringBuilder(PREFIXE_COEFF);
        for (int i = p.getDegre(); i >= 0; i--) {
            sb.append(' ').append(p.getCoefficient(i));
        }
        return sb.toString();
    }
    
    /**
     * Sérialise un polynôme au format {@code RACINES}.
     */
    private static String serialiserRacines(double[] racines,
                                             int[]    ordres,
                                             double   coeffDominant) {
        if (racines.length != ordres.length) {
            throw new IllegalArgumentException(
                "Tableaux racines et ordres de tailles différentes.");
        }

        StringBuilder sb = new StringBuilder(PREFIXE_RACINES);
        sb.append(' ').append(coeffDominant);
        for (int i = 0; i < racines.length; i++) {
            sb.append(' ').append(racines[i]).append(':').append(ordres[i]);
        }
        return sb.toString();
    }

    /**
     * Désérialise une ligne de fichier en polynôme.
     *
     * @param ligne La ligne à analyser
     * @return Le polynôme correspondant
     * @throws IOException              si le format est inconnu
     * @throws NumberFormatException    si une valeur numérique est invalide
     * @throws IllegalArgumentException si le polynôme ne peut pas être construit
     */
    private static Polynome deserialiser(String ligne)
            throws IOException {

        if (ligne.startsWith(PREFIXE_COEFF)) {
            return deserialiserCoeff(ligne.
            		                 substring(PREFIXE_COEFF.length()).trim());

        } else if (ligne.startsWith(PREFIXE_RACINES)) {
            return deserialiserRacines(ligne.
            		                substring(PREFIXE_RACINES.length()).trim());

        } else {
            throw new IOException(
                "Format non reconnu (ligne doit commencer par "
              + PREFIXE_COEFF + " ou " + PREFIXE_RACINES + ").");
        }
    }

    /**
     * Désérialise la partie données d'une ligne {@code COEFF}.
     */
    private static Polynome deserialiserCoeff(String donnees) {
        if (donnees.isEmpty()) {
            throw new IllegalArgumentException(
                "Aucun coefficient fourni après le mot-clé COEFF.");
        }
        String[] parts  = donnees.split("\\s+");
        double[] coeffs = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            coeffs[i] = Double.parseDouble(parts[i]);
        }
        return new Polynome(coeffs);
    }

    /**
     * Désérialise la partie données d'une ligne {@code RACINES}.
     */
    private static Polynome deserialiserRacines(String donnees) {
        if (donnees.isEmpty()) {
            throw new IllegalArgumentException(
                "Aucune donnée fournie après le mot-clé RACINES.");
        }
        String[] parts         = donnees.split("\\s+");
        double   coeffDominant = Double.parseDouble(parts[0]);

        int      n      = parts.length - 1;
        double[] racines = new double[n];
        int[]    ordres  = new int[n];

        for (int i = 0; i < n; i++) {
            String[] paire = parts[i + 1].split(":");
            if (paire.length != 2) {
                throw new IllegalArgumentException(
                    "Format de racine invalide (attendu r:k) : " + parts[i + 1]);
            }
            racines[i] = Double.parseDouble(paire[0]);
            ordres[i]  = Integer.parseInt(paire[1]);
        }

        return new Polynome(coeffDominant, racines, ordres);
    }
}