package mx.edu.utng.tasking

import android.annotation.SuppressLint
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

class MainActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpTextView: TextView
    private lateinit var forgotPasswordTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Aplica insets para que se vea correctamente en pantallas edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar vistas
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signUpTextView = findViewById(R.id.signUpTextView)
        //forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)

        // Botón para iniciar sesión
        loginButton.setOnClickListener {
            loginUser()
        }

        // Enlace para ir a la pantalla de registro
        signUpTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

//        // Enlace para recuperar contraseña (puedes implementar la Activity correspondiente)
//        forgotPasswordTextView.setOnClickListener {
//            Toast.makeText(this, "Función de recuperar contraseña no implementada", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun loginUser() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show()
            return
        }

        val dbHandler = DatabaseHandler(this)
        val user = dbHandler.getUser(username, password) // Obtiene el usuario si existe

        if (user != null) {
            // Guardar usuario en SharedPreferences
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("userId", user.userId ?: -1)
            editor.putString("userName", user.userName)
            editor.putString("userEmail", user.userEmail)
            editor.putString("userImage", user.image)
            editor.apply()

            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()

            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
        }
    }

}
