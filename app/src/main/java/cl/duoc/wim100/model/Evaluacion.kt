package cl.duoc.wim100.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Evaluación de una candidata hecha por una evaluadora.
 * El índice único (candidataId, evaluadora) impide duplicados en la propia base de datos.
 * NOCASE hace que "Ana Rojas" y "ana rojas" cuenten como la misma evaluadora.
 */
@Entity(
    tableName = "evaluaciones",
    foreignKeys = [ForeignKey(
        entity = Candidata::class,
        parentColumns = ["id"],
        childColumns = ["candidataId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["candidataId", "evaluadora"], unique = true)]
)
data class Evaluacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val candidataId: Long,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val evaluadora: String,
    val puntajeTotal: Int,            // 0 a 100
    val comentario: String,
    val fecha: Long = System.currentTimeMillis()
) {
    companion object {
        /** Evaluaciones necesarias para que la candidata pase a "Evaluada". */
        const val REQUERIDAS = 2
    }
}

/** Puntaje (1–5) de un criterio dentro de una evaluación. */
@Entity(
    tableName = "puntajes",
    primaryKeys = ["evaluacionId", "criterio"],   // un criterio por evaluación
    foreignKeys = [ForeignKey(
        entity = Evaluacion::class,
        parentColumns = ["id"],
        childColumns = ["evaluacionId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Puntaje(
    val evaluacionId: Long,
    val criterio: Criterio,
    val valor: Int,
    val observacion: String = ""      // obligatoria si valor ≤ 2 (ver validation/)
)
