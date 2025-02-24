package mx.edu.utng.tasking

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 4
        private const val DATABASE_NAME = "TaskingDBv"

        // Tabla Usuarios
        private const val TABLE_USUARIOS = "Usuarios"
        private const val KEY_ID = "id"
        private const val KEY_IMAGE = "image"
        private const val KEY_NAME = "name"
        private const val KEY_PASS = "pass"
        private const val KEY_EMAIL = "email"

        // Tabla Tareas
        private const val TABLE_TAREAS = "Tareas"
        private const val KEY_TAREA_ID = "id"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_TITULO = "titulo"
        private const val KEY_DESCRIPCION = "descripcion"
        private const val KEY_ESTADO = "estado"
        private const val KEY_FECHA_CREACION = "fecha_creacion"
        private const val KEY_FECHA_FINALIZACION  = "fecha_finalizacion"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsuariosTable = """
            CREATE TABLE $TABLE_USUARIOS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_NAME TEXT,
                $KEY_IMAGE TEXT,
                $KEY_PASS TEXT,
                $KEY_EMAIL TEXT UNIQUE
            );
        """.trimIndent()

        val createTareasTable = """
    CREATE TABLE $TABLE_TAREAS (
        $KEY_TAREA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $KEY_USER_ID INTEGER,
        $KEY_TITULO TEXT,
        $KEY_DESCRIPCION TEXT,
        $KEY_ESTADO TEXT CHECK($KEY_ESTADO IN ('Pendiente', 'En Progreso', 'Completada')),
        $KEY_FECHA_CREACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        $KEY_FECHA_FINALIZACION TIMESTAMP,  -- Nuevo campo para fecha de finalización
        FOREIGN KEY($KEY_USER_ID) REFERENCES $TABLE_USUARIOS($KEY_ID) ON DELETE CASCADE
    );
""".trimIndent()


        db?.execSQL(createUsuariosTable)
        db?.execSQL(createTareasTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TAREAS")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.execSQL("PRAGMA foreign_keys = ON;")
    }



    fun addUsuario(user: UsuarioModelClass): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, user.userName)
            put(KEY_IMAGE, user.image)
            put(KEY_PASS, user.password)
            put(KEY_EMAIL, user.userEmail)
        }
        val result = db.insert(TABLE_USUARIOS, null, values)
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun getUsuarios(): List<UsuarioModelClass> {
        val usuarios = mutableListOf<UsuarioModelClass>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIOS", null)

        if (cursor.moveToFirst()) {
            do {
                val usuario = UsuarioModelClass(
                    userId = cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    userName = cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                    image = cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                    password = cursor.getString(cursor.getColumnIndex(KEY_PASS)),
                    userEmail = cursor.getString(cursor.getColumnIndex(KEY_EMAIL))
                )
                usuarios.add(usuario)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return usuarios
    }


    fun updateUsuario(user: UsuarioModelClass): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, user.userName)
            put(KEY_EMAIL, user.userEmail)
            put(KEY_IMAGE, user.image)
        }
        val result = db.update(TABLE_USUARIOS, values, "$KEY_ID = ?", arrayOf(user.userId.toString()))
        db.close()
        return result
    }

    fun deleteUsuario(userId: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_USUARIOS, "$KEY_ID = ?", arrayOf(userId.toString()))
        db.close()
        return result
    }



    fun addTarea(tarea: TareaModelClass): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_USER_ID, tarea.userId)
            put(KEY_TITULO, tarea.titulo)
            put(KEY_DESCRIPCION, tarea.descripcion)
            put(KEY_ESTADO, tarea.estado)
            put(KEY_FECHA_FINALIZACION, tarea.fechaFinalizacion) // Guardar la fecha de finalización
        }
        val result = db.insert(TABLE_TAREAS, null, values)
        db.close()
        return result
    }



    fun updateTarea(tarea: TareaModelClass): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_TITULO, tarea.titulo)
            put(KEY_DESCRIPCION, tarea.descripcion)
            put(KEY_ESTADO, tarea.estado)
        }
        val result = db.update(TABLE_TAREAS, values, "$KEY_TAREA_ID = ?", arrayOf(tarea.id.toString()))
        db.close()
        return result
    }

    fun deleteTarea(tareaId: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_TAREAS, "$KEY_TAREA_ID = ?", arrayOf(tareaId.toString()))
        db.close()
        return result
    }

    fun getUser(username: String, password: String): UsuarioModelClass? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USUARIOS WHERE $KEY_NAME = ? AND $KEY_PASS = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))

        return if (cursor.moveToFirst()) {
            val user = UsuarioModelClass(
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                userName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                image = cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)),
                userEmail = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASS))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun getUserTasks(userId: Int): List<TareaModelClass> {
        val db = readableDatabase
        val taskList = mutableListOf<TareaModelClass>()
        val query = "SELECT * FROM $TABLE_TAREAS WHERE $KEY_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        while (cursor.moveToNext()) {
            val tarea = TareaModelClass(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TAREA_ID)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID)),
                titulo = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITULO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPCION)),
                estado = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ESTADO)),
                fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow(KEY_FECHA_CREACION)),
                fechaFinalizacion = cursor.getString(cursor.getColumnIndexOrThrow(KEY_FECHA_FINALIZACION))

            )
            taskList.add(tarea)
        }
        cursor.close()
        return taskList
    }

}
