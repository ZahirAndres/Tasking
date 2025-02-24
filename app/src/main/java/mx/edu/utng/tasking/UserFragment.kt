package mx.edu.utng.tasking

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView

class UserFragment : Fragment() {

    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var logoutButton: MaterialButton
    private lateinit var editProfileButton: MaterialButton
    private lateinit var deleteAccountButton: MaterialButton
    private lateinit var profileImageView: ShapeableImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        userNameTextView = view.findViewById(R.id.userNameTextView)
        userEmailTextView = view.findViewById(R.id.userEmailTextView)
        logoutButton = view.findViewById(R.id.logoutButton)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        deleteAccountButton = view.findViewById(R.id.deleteAccountButton)
        profileImageView = view.findViewById(R.id.profileImageView)

        // Cargar los datos del usuario
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "Desconocido") ?: "Desconocido"
        val userEmail = sharedPreferences.getString("userEmail", "Sin correo") ?: "Sin correo"
        val userImage = sharedPreferences.getString("userImage", "Sin image") ?: "Sin image"

        userNameTextView.text = "Usuario: $userName"
        userEmailTextView.text = "Email: $userEmail"

        // Usamos Glide para cargar la imagen de manera redonda
        Glide.with(requireContext())
            .load(userImage)
            .circleCrop()
            .placeholder(R.drawable.espera)
            .error(R.drawable.desconocido)
            .into(profileImageView)

        logoutButton.setOnClickListener { logout() }
        editProfileButton.setOnClickListener { navigateToEditProfile() }
        deleteAccountButton.setOnClickListener { showDeleteAccountDialog(userName) }

        return view
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToEditProfile() {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, EditProfileFragment())
            addToBackStack(null)
            commit()
        }
    }

    private fun showDeleteAccountDialog(correctUserName: String) {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("Para eliminar tu cuenta, por favor escribe tu nombre de usuario.")
            .setCancelable(false)

        // Crear el campo de entrada
        val input = EditText(requireContext()).apply {
            hint = "Escribe tu nombre de usuario"
            inputType = InputType.TYPE_CLASS_TEXT
            setSingleLine()
        }

        // Establecer el campo de entrada en el diálogo
        builder.setView(input)

        // Botón para eliminar la cuenta
        builder.setPositiveButton("Eliminar") { _, _ ->
            val enteredName = input.text.toString()
            if (enteredName == correctUserName) {
                deleteAccount()
            } else {
                Toast.makeText(requireContext(), "Nombre incorrecto. No se eliminó la cuenta.", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón de cancelación
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        // Mostrar el diálogo
        builder.show()
    }


    private fun deleteAccount() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)

        if (userId != -1) {
            val dbHandler = DatabaseHandler(requireContext())
            val result = dbHandler.deleteUsuario(userId)

            if (result > 0) {
                sharedPreferences.edit().clear().apply()
                Toast.makeText(requireContext(), "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show()

                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No se encontró el usuario", Toast.LENGTH_SHORT).show()
        }
    }
}
