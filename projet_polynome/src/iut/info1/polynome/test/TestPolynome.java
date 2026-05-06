package iut.info1.polynome.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import iut.info1.polynome.Polynome;

class TestPolynome {
	
	private Polynome pNul;
	private Polynome pDegre2;
	private Polynome pDegre3; // Utile pour tester des limites différentes

	@BeforeEach
	void setUp() throws Exception {
		pNul = new Polynome();
		// P(X) = 1.0 - 5.0X + 3.2X^2
		pDegre2 = new Polynome(new double[]{1.0, -5.0, 3.2});
		// P(X) = 2.0X^3
		pDegre3 = new Polynome(new double[]{0.0, 0.0, 0.0, 2.0});
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
		            () -> new Polynome(new double[]{1.6, Double.POSITIVE_INFINITY, -5.0, 3.2}), 
		                  "Un tableau qui contient NaN doit déclencher une "
		                  + "IllegalArgumentException.");
		assertThrows(IllegalArgumentException.class, 
	            () -> new Polynome(new double[]{1.6, 6.4, -5.0, 3.2, Double.NEGATIVE_INFINITY}), 
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
		double[] racinesNul = pNul.getRacines();
		assertTrue(racinesNul == null || racinesNul.length == 0, 
				   "Les racines du polynôme nul doivent être null "
		           +"ou un tableau vide.");
		
		double[] racinesDegre2 = pDegre2.getRacines();
		assertNotNull(racinesDegre2,
				      "Le tableau de racines ne doit pas être null.");
		assertEquals(2, racinesDegre2.length, 
				      "Ce polynôme de degré 2 possède deux racines réelles.");
		
		// Test d'un polynôme de degré 2 sans racines réelles : P(X) = X^2 + 4
        Polynome pDegre2SansRacine = new Polynome(new double[]{4.0, 0.0, 1.0});
        double[] racinesVides = pDegre2SansRacine.getRacines();
        assertEquals(0, racinesVides.length, "Ce polynôme de degré 2 ne coupe jamais l'axe des abscisses.");

        // Test d'un polynôme avec des racines non entières : P(X) = 4X^2 - 5X - 6 (Racines : -0.75 et 2.0)
        Polynome pDegre2Decimal = new Polynome(new double[]{-6.0, -5.0, 4.0});
        double[] racinesDecimales = pDegre2Decimal.getRacines();
        assertEquals(2, racinesDecimales.length);
        Arrays.sort(racinesDecimales);
        assertEquals(-0.75, racinesDecimales[0], 0.0001, "La première racine doit être -0.75");
        assertEquals(2.0, racinesDecimales[1], 0.0001, "La deuxième racine doit être 2.0");
	}

	@Test
	final void testEstNul() {
		assertTrue(pNul.estNul(), 
				     "estNul() doit retourner true pour le polynôme nul.");
		assertFalse(pDegre2.estNul(), 
				     "estNul() doit retourner false pour un polynôme non nul.");
		
		// Test d'un faux polynôme nul (ex: des zéros dans le constructeur)
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
	void testEvaluer() {
	    // P(X) = 3.2X² - 5.0X + 1.0
	    // P(2) = 3.2(4) - 5.0(2) + 1.0 = 12.8 - 10 + 1 = 3.8
	    assertEquals(3.8, pDegre2.evaluer(2.0), 1e-6);
	    assertEquals(1.0, pDegre2.evaluer(0.0), 1e-6);
	    assertEquals(0.0, pNul.evaluer(5.0), 1e-6);
	}
	
	
//	@Test
//	final void testCalculerRacines() {
//		// Test du polynôme nul
//		double[] racinesNul = pNul.calculerRacines();
//		assertNotNull(racinesNul, "Le tableau de racines ne doit pas être null.");
//		assertEquals(0, racinesNul.length, "Le polynôme nul ne possède aucune racine.");
//
//		// Test du polynôme de degré 2 défini dans setUp
//		// P(X) = 1.0 - 5.0X + 3.2X^2 => Delta = (-5)^2 - 4(3.2)(1) = 12.2 (> 0)
//		double[] racinesDegre2 = pDegre2.calculerRacines();
//		assertNotNull(racinesDegre2, "Le tableau de racines ne doit pas être null.");
//		assertEquals(2, racinesDegre2.length,
//				      "Ce polynôme de degré 2 avec Delta > 0 possède deux racines réelles.");
//		
//		// Test d'un polynôme de degré 1 : P(X) = -2.0 + 4.0X  (Racine = 0.5)
//		Polynome pDegre1 = new Polynome(new double[]{-2.0, 4.0});
//		double[] racinesDegre1 = pDegre1.calculerRacines();
//		assertEquals(1, racinesDegre1.length, "Un polynôme de degré 1 a une seule racine.");
//		assertEquals(0.5, racinesDegre1[0], 0.0001, "La racine de -2 + 4X doit être 0.5.");
//
//		// Test d'un cas sans racine réelle : P(X) = 5.0 + 0.0X + 1.0X^2 (X^2 + 5 = 0)
//		Polynome pSansRacine = new Polynome(new double[]{5.0, 0.0, 1.0});
//		assertEquals(0, pSansRacine.calculerRacines().length, 
//				      "Ce polynôme ne devrait pas avoir de racines réelles (Delta < 0).");
//
//		// Test de la gestion du degré supérieur à 2 (pDegre3 : P(X) = 2.0X^3)
//		// On vérifie que le code lève bien l'exception prévue
//		assertThrows(UnsupportedOperationException.class, () -> {
//			pDegre3.calculerRacines();
//		}, "Le calcul pour un degré 3 n'est pas encore supporté et doit lever une exception.");
//	}
}