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
	

	@BeforeEach
	void setUp() throws Exception {
		System.out.println("BeforeEach");
		pNul = new Polynome();
		pDegre2 = new Polynome(new double[]{1.0, -5.0, 3.2});

	}

	@AfterEach
	void tearDown() throws Exception {
		System.out.println("AfterEach");
	}
	
	@Test
	final void testPolynomeDefaut() {
		assertDoesNotThrow(() -> pNul, 
	            "Le constructeur par défaut ne doit pas lever d'exception.");
		assertNotNull(pNul, "L'instance de Polynome ne doit pas être null.");
	}

	@Test
    final void testPolynomeDoubleArray_InstanciationEtExceptions() {
        assertDoesNotThrow(() -> pDegre2, 
            "Le constructeur avec tableau valide ne doit pas lever d'exception.");
        assertNotNull(pDegre2,"Ce polynôme n'est pas nul mais l'assert renvoi null");

        assertThrows(IllegalArgumentException.class, () -> new Polynome(null), 
            "Un tableau null doit déclencher une IllegalArgumentException.");

        assertThrows(IllegalArgumentException.class, () -> new Polynome(new double[]{}), 
            "Un tableau vide doit déclencher une IllegalArgumentException.");
    }

	@Test
	final void testPolynomeDoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	final void testGetDegre() {
		fail("Not yet implemented");
	}

	@Test
	final void testGetCoefficient() {
		fail("Not yet implemented");
	}

	@Test
	final void testGetLimitesMoinsInfini() {
		fail("Not yet implemented");
	}

	@Test
	final void testGetLimitesPlusInfini() {
		fail("Not yet implemented");
	}

	@Test
	final void testGetRacines() {
		fail("Not yet implemented");
	}

	@Test
	final void testEstNul() {
		fail("Not yet implemented");
	}

	@Test
	final void testToString() {
		fail("Not yet implemented");
	}

}
