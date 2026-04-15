package iut.info1.polynome.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import iut.info1.polynome.Polynome;

class TestPolynome {
	
	private Polynome pNul;
	private Polynome pDegre2;
	private Polynome pDegre3; // Utile pour tester des limites différentes (degré impair)

	@BeforeEach
	void setUp() throws Exception {
		System.out.println("BeforeEach");
		pNul = new Polynome();
		// P(X) = 1.0 - 5.0X + 3.2X^2
		pDegre2 = new Polynome(new double[]{1.0, -5.0, 3.2});
		// P(X) = 2.0X^3
		pDegre3 = new Polynome(new double[]{0.0, 0.0, 0.0, 2.0});
	}

	@AfterEach
	void tearDown() throws Exception {
		System.out.println("AfterEach");
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
            "Le constructeur avec tableau valide ne doit pas lever d'exception.");
        assertNotNull(pDegre2,"Ce polynôme n'est pas nul mais l'assert renvoi null");

        assertThrows(IllegalArgumentException.class, () -> new Polynome(null), 
            "Un tableau null doit déclencher une IllegalArgumentException.");

        assertThrows(IllegalArgumentException.class, () -> new Polynome(new double[]{}), 
            "Un tableau vide doit déclencher une IllegalArgumentException.");
    }

	@Test
	final void testPolynomeDoubleArray() {
		// La vérification de l'instanciation pure est déjà faite au-dessus.
		// On s'assure ici que deux instanciations indépendantes créent bien des objets distincts.
		Polynome pTest = new Polynome(new double[]{1.0, -5.0, 3.2});
		assertNotSame(pDegre2, pTest, "Deux instanciations doivent créer des références mémoire différentes.");
	}

	@Test
	final void testGetDegre() {
		assertEquals(0, pNul.getDegre(), "Le degré du polynôme nul devrait être 0 (ou -1 selon votre convention mathématique).");
		assertEquals(2, pDegre2.getDegre(), "Le degré de P(X) = 1.0 - 5.0X + 3.2X^2 devrait être 2.");
		assertEquals(3, pDegre3.getDegre(), "Le degré de P(X) = 2.0X^3 devrait être 3.");
	}

	@Test
	final void testGetCoefficient() {
		// Test sur le polynôme de degré 2
		assertEquals(1.0, pDegre2.getCoefficient(0), "Le coefficient de degré 0 est incorrect.");
		assertEquals(-5.0, pDegre2.getCoefficient(1), "Le coefficient de degré 1 est incorrect.");
		assertEquals(3.2, pDegre2.getCoefficient(2), "Le coefficient de degré 2 est incorrect.");
		
		// Test avec une puissance supérieure au degré (doit renvoyer 0)
		assertEquals(0.0, pDegre2.getCoefficient(5), "Le coefficient pour une puissance supérieure au degré doit être 0.0.");
		
		// Test sur le polynôme nul
		assertEquals(0.0, pNul.getCoefficient(0), "Le coefficient du polynôme nul doit être 0.0.");
	}

	@Test
	final void testGetLimitesMoinsInfini() {
		assertEquals(0.0, pNul.getLimitesMoinsInfini(), "La limite du polynôme nul est 0.");
		// Pour 3.2X^2, en -infini, la limite est +infini
		assertEquals(Double.POSITIVE_INFINITY, pDegre2.getLimitesMoinsInfini(), "La limite de 3.2X^2 en -infini devrait être +infini.");
		// Pour 2.0X^3, en -infini, la limite est -infini
		assertEquals(Double.NEGATIVE_INFINITY, pDegre3.getLimitesMoinsInfini(), "La limite de 2.0X^3 en -infini devrait être -infini.");
	}

	@Test
	final void testGetLimitesPlusInfini() {
		assertEquals(0.0, pNul.getLimitesPlusInfini(), "La limite du polynôme nul est 0.");
		// Pour 3.2X^2, en +infini, la limite est +infini
		assertEquals(Double.POSITIVE_INFINITY, pDegre2.getLimitesPlusInfini(), "La limite de 3.2X^2 en +infini devrait être +infini.");
		// Pour 2.0X^3, en +infini, la limite est +infini
		assertEquals(Double.POSITIVE_INFINITY, pDegre3.getLimitesPlusInfini(), "La limite de 2.0X^3 en +infini devrait être +infini.");
	}

	@Test
	final void testGetRacines() {
		// Pour le polynôme nul, on attend soit un tableau vide, soit null selon votre conception
		double[] racinesNul = pNul.getRacines();
		assertTrue(racinesNul == null || racinesNul.length == 0, "Les racines du polynôme nul doivent être null ou un tableau vide.");
		
		// Pour P(X) = 3.2X^2 - 5.0X + 1.0 (Delta > 0), il y a deux racines réelles
		double[] racinesDegre2 = pDegre2.getRacines();
		assertNotNull(racinesDegre2, "Le tableau de racines ne doit pas être null.");
		assertEquals(2, racinesDegre2.length, "Ce polynôme de degré 2 possède deux racines réelles.");
	}

	final void testEstNul() {
		assertTrue(pNul.estNul(), "estNul() doit retourner true pour le polynôme nul.");
		assertFalse(pDegre2.estNul(), "estNul() doit retourner false pour un polynôme non nul.");
		
		// Test d'un faux polynôme nul (ex: des zéros dans le constructeur)
		Polynome pFauxNul = new Polynome(new double[]{0.0, 0.0, 0.0});
		assertTrue(pFauxNul.estNul(), "Un polynôme construit avec que des 0.0 doit être considéré comme nul.");
	}

	@Test
	final void testToString() {
		assertNotNull(pNul.toString(), "toString ne doit pas retourner null.");
		assertNotEquals("", pNul.toString().trim(), "La représentation textuelle ne doit pas être vide.");
		
		String affichage = pDegre2.toString();
		assertTrue(affichage.contains("3.2") && affichage.contains("X^2"), 
				"L'affichage doit contenir le monôme de plus haut degré '3.2' et 'X^2'.");
		assertTrue(affichage.contains("5.0"), 
				"L'affichage doit contenir le coefficient '5.0'.");
	}
}