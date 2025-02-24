package mx.edu.utng.tasking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    // Declaración a nivel de clase para que sean accesibles en todos los métodos
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var imageEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Inicializamos las variables de la clase
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        imageEditText = findViewById(R.id.imageEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginTextView = findViewById(R.id.loginTextView)

        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            registerUser()
        }

        loginTextView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


    fun registerUser() {
        val name = usernameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val image = imageEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validación básica de campos
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || image.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Crea el objeto usuario. Se usa 0 para el ID, ya que se autogenerará.
        val usuario = UsuarioModelClass(
            userId = null,
            userName = name,
            image = image,
            userEmail = email,
            password = password
        )

        // Inserta el usuario en la base de datos
        val dbHandler = DatabaseHandler(this)
        val result = dbHandler.addUsuario(usuario)

        if (result > -1) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()
            // Opcional: limpia los campos o redirige al login
            usernameEditText.text?.clear()
            emailEditText.text?.clear()
            imageEditText.text?.clear()
            passwordEditText.text?.clear()
        } else {
            Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_LONG).show()
        }
    }


}
