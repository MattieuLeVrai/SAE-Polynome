package iut.info1.polynome.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import iut.info1.polynome.Polynome;

/**
 * Test unitaire pour la classe Polynome.
 * Plan de test :
 * 	- Test des constructeurs :
 *         - Constructeur par défaut : doit créer un polynôme nul sans exception.
 *         - Constructeur avec tableau de coefficients :
 *         - Cas valide : doit créer un polynôme correct sans exception.
 *         
 *     - Cas invalides :
 *         - Tableau null : doit lancer IllegalArgumentException.
 *         - Tableau vide : doit lancer IllegalArgumentException.
 *         - Coefficients non finis (NaN, Infinity) : 
 *              doit lancer IllegalArgumentException.
 *         
 * - Test des méthodes d'accès :
 *         - getDegre() : 
 *             doit retourner le degré correct pour différents polynômes.
 *         - getCoefficient(int n) : 
 *             doit retourner le coefficient correct pour différentes puissances,
 *         et 0 pour les puissances supérieures au degré.
 *         - getLimitesMoinsInfini() et getLimitesPlusInfini() : 
 *             doit retourner les limites correctes en -infini et +infini.
 *         - getRacines() : 
 *             doit retourner les racines correctes pour différents polynômes
 *             (nul, constant, linéaire, 
 *             quadratique avec ou sans racines réelles).
 * - Test des méthodes de comportement :
 *         - estNul() : doit retourner true pour le polynôme nul 
 *                      et false pour les autres.
 *         - toString() : doit retourner une représentation textuelle non nulle 
 *                        et non vide du polynôme.
 * - Test de l'évaluation du polynôme :
 *         - evaluer(double x) : 
 *             doit retourner la valeur correcte du polynôme 
 *             pour différentes valeurs de x, y compris des cas extrêmes.
 * - Test de robustesse et performance :
 *         - Evaluer un polynôme avec des coefficients très grands pour vérifier 
 *             que cela ne cause pas de crash (doit retourner Infinity 
 *             ou un grand nombre au lieu de lancer une exception).
 *         - Test de multiplication de tableaux (si la méthode est publique) : 
 *             doit retourner le résultat correct 
 *             pour des tableaux d'entrée spécifiques.
 * - Test du constructeur avec racines et ordres de multiplicité :
 *         - Cas valide : doit créer un polynôme correct sans exception pour
 *             des racines et ordres valides.
 *         - Cas invalides : doit lancer IllegalArgumentException pour
 *             des racines et ordres de tailles différentes, 
 *             coefficient dominant zéro, ordre de multiplicité invalide, 
 *             ou tableaux null.
 *     
 * 
 * @author Higounet Kelvin
 * @author Laurençont Yanis
 * @author Liao Mattieu
 * @author Moqué Baptiste 
 * 
 */
class TestPolynome {
	
	private Polynome pNul;
	private Polynome pDegre2;
	private Polynome pDegre3; // Utile pour tester des limites différentes
	private double grosCoefficient;

	@BeforeEach
	void setUp() throws Exception {
		pNul = new Polynome();
		// P(X) = 1.0 - 5.0X + 3.2X^2
		pDegre2 = new Polynome(new double[]{1.0, -5.0, 3.2});
		// P(X) = 2.0X^3
		pDegre3 = new Polynome(new double[]{0.0, 0.0, 0.0, 2.0});
		
		grosCoefficient = Double.MAX_VALUE * 2.0;
	}

	@Test
	final void testPolynomeDefaut() {
		assertDoesNotThrow(() -> new Polynome(), 
	            "Le constructeur par défaut ne doit pas lever d'exception.");
		assertNotNull(pNul, "L'instance de Polynome ne doit pas être null.");
	}

	@Test
    final void testPolynomeDoubleArray_InstanciationEtExceptions() {
        assertDoesNotThrow(() -> new Polynome(new double[]{1.0, 2.0}), 
            "Le constructeur avec tableau valide "
             +"ne doit pas lever d'exception.");
        assertNotNull(pDegre2,
        		      "Ce polynôme n'est pas nul mais l'assert renvoi null");

        assertThrows(IllegalArgumentException.class, () -> new Polynome(null), 
            "Un tableau null doit déclencher une IllegalArgumentException.");

        assertThrows(IllegalArgumentException.class, 
        		      () -> new Polynome(new double[]{}), 
            "Un tableau vide doit déclencher une IllegalArgumentException.");
        assertThrows(IllegalArgumentException.class, () -> {
			new Polynome(new double[] {1.0, grosCoefficient});
		},"Le constructeur doit refuser les coefficients Infinity (overflow).");
    }

	@Test
	final void testPolynomeDoubleArray() {
		Polynome pTest = new Polynome(new double[]{1.0, -5.0, 3.2});
		assertNotSame(pDegre2, pTest, 
				      "Deux instanciations doivent créer des références"
		              +" mémoire différentes.");
	
		assertThrows(IllegalArgumentException.class, 
  		            () -> new Polynome(new double[]{Double.NaN, -5.0, 3.2}), 
  		                  "Un tableau qui contient NaN doit déclencher une "
  		                  + "IllegalArgumentException.");
		assertThrows(IllegalArgumentException.class, 
		            () -> new Polynome(new double[]{1.6,
		            	  Double.POSITIVE_INFINITY, -5.0, 3.2}), 
		                  "Un tableau qui contient NaN doit déclencher une "
		                  + "IllegalArgumentException.");
		assertThrows(IllegalArgumentException.class, 
	            () -> new Polynome(new double[]{1.6, 6.4, -5.0, 3.2,
	            	  Double.NEGATIVE_INFINITY}), 
	                  "Un tableau qui contient NaN doit déclencher une "
	                  + "IllegalArgumentException.");
	}

	@Test
	final void testGetDegre() {
		assertEquals(0, pNul.getDegre(),
				      "Le degré du polynôme nul devrait être 0"
		             + " (ou -1 selon votre convention mathématique).");
		assertEquals(2, pDegre2.getDegre(),
				     "Le degré de P(X) = 1.0 - 5.0X + 3.2X^2 devrait être 2.");
		assertEquals(3, pDegre3.getDegre(), 
				     "Le degré de P(X) = 2.0X^3 devrait être 3.");
	}

	@Test
	final void testGetCoefficient() {
		// Test sur le polynôme de degré 2
		assertEquals(1.0, pDegre2.getCoefficient(0),
				     "Le coefficient de degré 0 est incorrect.");
		assertEquals(-5.0, pDegre2.getCoefficient(1), 
				     "Le coefficient de degré 1 est incorrect.");
		assertEquals(3.2, pDegre2.getCoefficient(2), 
				     "Le coefficient de degré 2 est incorrect.");
		
		// Test avec une puissance supérieure au degré (doit renvoyer 0)
		assertEquals(0.0, pDegre2.getCoefficient(5),
				      "Le coefficient pour une puissance"
		              + " supérieure au degré doit être 0.0.");
		
		// Test sur le polynôme nul
		assertEquals(0.0, pNul.getCoefficient(0), 
				     "Le coefficient du polynôme nul doit être 0.0.");
		int puissanceOverflow = Integer.MAX_VALUE + 1;
		assertEquals(0.0, pDegre2.getCoefficient(puissanceOverflow),
			"Une puissance ayant subi un overflow (négative) doit retourner 0.0.");
	}

	@Test
	final void testGetLimitesMoinsInfini() {
		assertEquals(0.0, pNul.getLimitesMoinsInfini(), 
				     "La limite du polynôme nul est 0.");
		// Pour 3.2X^2, en -infini, la limite est +infini
		assertEquals(Double.POSITIVE_INFINITY, pDegre2.getLimitesMoinsInfini(), 
				     "La limite de 3.2X^2 en -infini devrait être +infini.");
		// Pour 2.0X^3, en -infini, la limite est -infini
		assertEquals(Double.NEGATIVE_INFINITY, pDegre3.getLimitesMoinsInfini(),
				     "La limite de 2.0X^3 en -infini devrait être -infini.");
	}

	@Test
	final void testGetLimitesPlusInfini() {
		assertEquals(0.0, pNul.getLimitesPlusInfini(),
				     "La limite du polynôme nul est 0.");
		// Pour 3.2X^2, en +infini, la limite est +infini
		assertEquals(Double.POSITIVE_INFINITY, pDegre2.getLimitesPlusInfini(),
				     "La limite de 3.2X^2 en +infini devrait être +infini.");
		// Pour 2.0X^3, en +infini, la limite est +infini
		assertEquals(Double.POSITIVE_INFINITY, pDegre3.getLimitesPlusInfini(),
				     "La limite de 2.0X^3 en +infini devrait être +infini.");
	}

	@Test
	final void testGetRacines() {
	    // Polynôme Nul ou Constant 
	    assertEquals(0, pNul.getRacines().length,
	    		                         "Le polynôme nul ne doit pas" +
	                                     "renvoyer de racines.");
	    Polynome pConstant = new Polynome(new double[]{5.0});
	    assertEquals(0, pConstant.getRacines().length, "Un polynôme constant" +
	                                                   "(P=5) n'a pas de racine.");

	    // Degré 1 : P(X) = 4X - 2 => Racine = 0.5
	    Polynome pDegre1 = new Polynome(new double[]{-2.0, 4.0});
	    double[] racinesDegre1 = pDegre1.getRacines();
	    assertEquals(1, racinesDegre1.length, "Un degré 1 doit avoir une racine.");
	    assertEquals(0.5, racinesDegre1[0], 0.0001, "La racine de 4X - 2 doit être 0.5.");

	    // Degré 2 sans racines réelles : P(X) = X^2 + 4
	    Polynome pSansRacine = new Polynome(new double[]{4.0, 0.0, 1.0});
	    assertEquals(0, pSansRacine.getRacines().length, "X^2 + 4 ne doit" +
	                                                     "pas avoir de racines réelles.");

	    // Racines décimales : P(X) = 4X^2 - 5X - 6 
	    Polynome pDecimal = new Polynome(new double[]{-6.0, -5.0, 4.0});
	    double[] racinesDecimales = pDecimal.getRacines();
	    Arrays.sort(racinesDecimales); // Important pour comparer les indices
	    assertEquals(2, racinesDecimales.length,"Le polynôme 4X² - 5X - 6" +
	                                            "devrait avoir exactement 2 racines réelles.");
	    assertEquals(-0.75, racinesDecimales[0], 0.0001,"La première racine" +
	                                                    "(x1) est incorrecte." +
	    		                                        "Attendu : -0.75.");
	    assertEquals(2.0, racinesDecimales[1], 0.0001,"La deuxième racine " +
	    		                                      "(x2) est incorrecte. Attendu : 2.0.");

	    // Racine double (Multiple) : P(X) = (X-1)^2 = X^2 - 2X + 1 
	    Polynome pRacineDouble = new Polynome(new double[]{1.0, -2.0, 1.0});
	    double[] rDouble = pRacineDouble.getRacines();
	    assertTrue(rDouble.length >= 1, "Doit trouver au moins une racine pour (X-1)^2");
	    assertEquals(1.0, rDouble[0], 0.01, "La racine double doit être proche de 1.0.");
	}

	@Test
	final void testEstNul() {
		assertTrue(pNul.estNul(), 
				     "estNul() doit retourner true pour le polynôme nul.");
		assertFalse(pDegre2.estNul(), 
				     "estNul() doit retourner false pour un polynôme non nul.");
		
		// Test d'un "faux" polynôme nul (ex: des zéros dans le constructeur)
		Polynome pFauxNul = new Polynome(new double[]{0.0, 0.0, 0.0});
		assertTrue(pFauxNul.estNul(), 
				     "Un polynôme construit avec que des 0.0 "
		             + "doit être considéré comme nul.");
	}

	@Test
	final void testToString() {
		assertNotNull(pNul.toString(), 
				       "toString ne doit pas retourner null.");
		assertNotEquals("", pNul.toString().trim(), 
				       "La représentation textuelle ne doit pas être vide.");
		
		String affichage = pDegre2.toString();
		assertTrue(affichage.contains("3.2") && affichage.contains("X^2"), 
				"L'affichage doit contenir le monôme de plus haut degré "
		        + "'3.2' et 'X^2'.");
		assertTrue(affichage.contains("5.0"), 
				"L'affichage doit contenir le coefficient '5.0'.");
	}
	
	@Test
	final void testEvaluer() {
	    // P(X) = 3.2X² - 5.0X + 1.0 (défini dans setUp)
	    // P(0) = 1.0
	    assertEquals(1.0, pDegre2.evaluer(0.0), 1e-6);
	    
	    // P(2) = 3.2(4) - 5.0(2) + 1.0 = 12.8 - 10 + 1 = 3.8
	    assertEquals(3.8, pDegre2.evaluer(2.0), 1e-6);
	    
	    // P(-1) = 3.2(1) - 5.0(-1) + 1.0 = 3.2 + 5 + 1 = 9.2
	    assertEquals(9.2, pDegre2.evaluer(-1.0), 1e-6);

	    // Test sur le polynôme nul
	    assertEquals(0.0, pNul.evaluer(99.0), 1e-6);
	}
	
	@Test
	final void testOverflow() {
	    // Cas du polynôme avec un coefficient immense qui causait le crash
	    Polynome pExtreme = new Polynome(new double[] {Double.MAX_VALUE, 0.01});
	    
	    // On vérifie que evaluer() ne crash pas (elle doit renvoyer Infinity)
	    assertDoesNotThrow(() -> {
	        double res = pExtreme.evaluer(1e10);
	        assertTrue(Double.isInfinite(res) || res > 1e30);
	    }, "Evaluer ne doit plus lancer d'ArithmeticException sur de grands nombres.");

	    // Test de performance : s'assurer que getRacines ne boucle pas à l'infini
	    assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
	        pExtreme.getRacines();
	    }, "L'algorithme doit abandonner ou finir rapidement si les nombres sont trop grands.");
	}
	
    @Test
    final void testMultiplierTableaux() {
        Polynome p = new Polynome();

        // Test 1 : (X - 1) * (X - 2) = X² - 3X + 2
        double[] a = {-1.0, 1.0}; // -1 + X
        double[] b = {-2.0, 1.0}; // -2 + X
        double[] resultat = p.multiplierTableaux(a, b);

        assertEquals(3, resultat.length);
        assertEquals(2.0, resultat[0], 1e-6); // constant
        assertEquals(-3.0, resultat[1], 1e-6); // coefficient de X
        assertEquals(1.0, resultat[2], 1e-6); // coefficient de X²

        // Test 2 : (2X + 1) * (X - 1) = 2X² - X - 1
        double[] c = {1.0, 2.0}; // 1 + 2X
        double[] d = {-1.0, 1.0}; // -1 + X
        double[] resultat2 = p.multiplierTableaux(c, d);

        assertEquals(3, resultat2.length);
        assertEquals(-1.0, resultat2[0], 1e-6);
        assertEquals(-1.0, resultat2[1], 1e-6);
        assertEquals(2.0, resultat2[2], 1e-6);
    }

    @Test
    final void testConstructeurAvecRacinesValides() {
        // Test 1 : P = 2(X-1)²(X+3) = 2X³ + 2X² - 8X + 6
        Polynome p1 = new Polynome(2.0, new double[]{1.0, -3.0}, new int[]{2, 1});

        assertEquals(3, p1.getDegre());
        assertEquals(2.0, p1.getCoefficient(3), 1e-6); // coeff de X³
        assertEquals(2.0, p1.getCoefficient(2), 1e-6); // coeff de X²
        assertEquals(-10.0, p1.getCoefficient(1), 1e-6); // coeff de X
        assertEquals(6.0, p1.getCoefficient(0), 1e-6); // constant

        // Test 2 : P = (X - 2) = X - 2
        Polynome p2 = new Polynome(1.0, new double[]{2.0}, new int[]{1});

        assertEquals(1, p2.getDegre());
        assertEquals(-2.0, p2.getCoefficient(0), 1e-6);
        assertEquals(1.0, p2.getCoefficient(1), 1e-6);

        // Test 3 : P = 3(X + 1)³ 
        Polynome p3 = new Polynome(3.0, new double[]{-1.0}, new int[]{3});

        assertEquals(3, p3.getDegre());
        assertEquals(3.0, p3.getCoefficient(3), 1e-6); // 3X³
    }

    @Test
    final void testConstructeurAvecRacinesInvalides() {
        // Test : racines et ordres de tailles différentes
        assertThrows(IllegalArgumentException.class, () -> {
            new Polynome(1.0, new double[]{1.0, 2.0}, new int[]{1});
        });

        // Test : coefficient dominant zéro
        assertThrows(IllegalArgumentException.class, () -> {
            new Polynome(0.0, new double[]{1.0}, new int[]{1});
        });

        // Test : ordre de multiplicité invalide
        assertThrows(IllegalArgumentException.class, () -> {
            new Polynome(1.0, new double[]{1.0}, new int[]{0});
        });

        // Test : tableaux null
        assertThrows(IllegalArgumentException.class, () -> {
            new Polynome(1.0, null, new int[]{1});
        });
    }
	
}