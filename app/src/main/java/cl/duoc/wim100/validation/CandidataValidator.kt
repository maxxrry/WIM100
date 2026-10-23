package cl.duoc.wim100.validation

/** Reglas del formulario de candidata. Cada función recibe el texto tal como lo escribió la usuaria. */
object CandidataValidator {

    const val ANIOS_MAX = 50
    const val RESUMEN_MIN = 20
    const val RESUMEN_MAX = 400
    val DOMINIOS_PERMITIDOS = listOf("ejemplo.cl", "test.cl")   // solo datos ficticios

    private val SOLO_LETRAS = Regex("^[\\p{L} ]+$")
    private val FORMATO_CORREO = Regex("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")
    private val FORMATO_PERFIL = Regex("^https://(www\\.)?linkedin\\.com/in/[A-Za-z0-9-]+/?$")

    fun nombre(valor: String): ValidationResult {
        val v = valor.trim()
        return when {
            v.isEmpty() -> ValidationResult.Error("El nombre es obligatorio")
            !SOLO_LETRAS.matches(v) -> ValidationResult.Error("Solo letras y espacios")
            v.split(Regex("\\s+")).size < 2 -> ValidationResult.Error("Escribe nombre y apellido")
            else -> ValidationResult.Ok
        }
    }

    fun correo(valor: String): ValidationResult {
        val v = valor.trim().lowercase()
        return when {
            v.isEmpty() -> ValidationResult.Error("El correo es obligatorio")
            !FORMATO_CORREO.matches(v) -> ValidationResult.Error("Correo inválido")
            v.substringAfter("@") !in DOMINIOS_PERMITIDOS -> ValidationResult.Error("Usa un correo @ejemplo.cl o @test.cl")
            else -> ValidationResult.Ok
        }
    }

    /** Acepta "+56 9 1234 5678", "56912345678" o "912345678". */
    fun telefono(valor: String): ValidationResult {
        val digitos = valor.filter { it.isDigit() }.removePrefix("56")
        return when {
            valor.isBlank() -> ValidationResult.Error("El teléfono es obligatorio")
            valor.any { !it.isDigit() && it != ' ' && it != '+' } -> ValidationResult.Error("Solo números, espacios y +")
            digitos.length != 9 || !digitos.startsWith("9") -> ValidationResult.Error("Formato: +56 9 1234 5678")
            else -> ValidationResult.Ok
        }
    }

    fun especialidad(valor: String?): ValidationResult =
        if (valor.isNullOrBlank()) ValidationResult.Error("Elige una especialidad") else ValidationResult.Ok

    fun anios(valor: String): ValidationResult {
        val numero = valor.trim().toIntOrNull()
        return when {
            valor.isBlank() -> ValidationResult.Error("Indica los años de experiencia")
            numero == null -> ValidationResult.Error("Debe ser un número entero")
            numero !in 0..ANIOS_MAX -> ValidationResult.Error("Entre 0 y $ANIOS_MAX años")
            else -> ValidationResult.Ok
        }
    }

    fun resumen(valor: String): ValidationResult {
        val largo = valor.trim().length
        return when {
            largo == 0 -> ValidationResult.Error("Describe brevemente su experiencia")
            largo < RESUMEN_MIN -> ValidationResult.Error("Mínimo $RESUMEN_MIN caracteres ($largo/$RESUMEN_MIN)")
            largo > RESUMEN_MAX -> ValidationResult.Error("Máximo $RESUMEN_MAX caracteres")
            else -> ValidationResult.Ok
        }
    }

    fun perfil(valor: String): ValidationResult {
        val v = valor.trim()
        return when {
            v.isEmpty() -> ValidationResult.Error("El enlace al perfil es obligatorio")
            !FORMATO_PERFIL.matches(v) -> ValidationResult.Error("Formato: https://www.linkedin.com/in/usuario")
            else -> ValidationResult.Ok
        }
    }
}
