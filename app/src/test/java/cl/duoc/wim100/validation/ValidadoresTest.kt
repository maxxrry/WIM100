package cl.duoc.wim100.validation

import cl.duoc.wim100.model.Rol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadoresTest {

    // --- Login ---
    @Test
    fun loginAceptaNombreConTildes() = assertTrue(LoginValidator.nombre("María José").esValido)

    @Test
    fun loginRechazaNumerosYVacio() {
        assertFalse(LoginValidator.nombre("Ana123").esValido)
        assertEquals("Ingresa tu nombre", LoginValidator.nombre("   ").mensaje)
    }

    @Test
    fun loginExigeRol() {
        assertFalse(LoginValidator.rol(null).esValido)
        assertTrue(LoginValidator.rol(Rol.DIRECCION).esValido)
    }

    // --- Candidata ---
    @Test
    fun nombreNecesitaApellido() {
        assertFalse(CandidataValidator.nombre("Catalina").esValido)
        assertTrue(CandidataValidator.nombre("Catalina Fuentes").esValido)
    }

    @Test
    fun correoSoloDominiosFicticios() {
        assertTrue(CandidataValidator.correo("ana@test.cl").esValido)
        assertTrue(CandidataValidator.correo("Ana.Rojas@EJEMPLO.cl").esValido)
        assertEquals("Usa un correo @ejemplo.cl o @test.cl", CandidataValidator.correo("ana@gmail.com").mensaje)
        assertEquals("Correo inválido", CandidataValidator.correo("ana@").mensaje)
    }

    @Test
    fun telefonoChileno() {
        assertTrue(CandidataValidator.telefono("+56 9 1234 5678").esValido)
        assertTrue(CandidataValidator.telefono("912345678").esValido)
        assertFalse(CandidataValidator.telefono("812345678").esValido)     // no parte con 9
        assertFalse(CandidataValidator.telefono("9 1234 567").esValido)    // faltan dígitos
        assertFalse(CandidataValidator.telefono("9-1234-5678").esValido)   // guiones no
    }

    @Test
    fun aniosEnRango() {
        assertTrue(CandidataValidator.anios("0").esValido)
        assertFalse(CandidataValidator.anios("-1").esValido)
        assertFalse(CandidataValidator.anios("51").esValido)
        assertFalse(CandidataValidator.anios("dos").esValido)
    }

    @Test
    fun resumenMuestraConteo() =
        assertEquals("Mínimo 20 caracteres (5/20)", CandidataValidator.resumen("Corto").mensaje)

    @Test
    fun perfilLinkedin() {
        assertTrue(CandidataValidator.perfil("https://www.linkedin.com/in/ana-rojas-ficticio").esValido)
        assertFalse(CandidataValidator.perfil("http://linkedin.com/in/ana").esValido)
        assertFalse(CandidataValidator.perfil("https://facebook.com/ana").esValido)
    }

    @Test
    fun especialidadObligatoria() = assertFalse(CandidataValidator.especialidad(null).esValido)

    // --- Evaluación ---
    @Test
    fun puntajeEntreUnoYCinco() {
        assertFalse(EvaluacionValidator.puntaje(null).esValido)
        assertFalse(EvaluacionValidator.puntaje(6).esValido)
        assertTrue(EvaluacionValidator.puntaje(3).esValido)
    }

    @Test
    fun puntajeBajoExigeObservacion() {
        assertFalse(EvaluacionValidator.observacion(2, "").esValido)
        assertTrue(EvaluacionValidator.observacion(2, "Le falta experiencia práctica").esValido)
        assertTrue(EvaluacionValidator.observacion(3, "").esValido)   // 3 o más: opcional
    }

    @Test
    fun comentarioObligatorio() {
        assertFalse(EvaluacionValidator.comentario("").esValido)
        assertTrue(EvaluacionValidator.comentario("Buen perfil general").esValido)
    }
}
