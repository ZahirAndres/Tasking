package mx.edu.utng.tasking

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class EditProfileFragment : Fragment() {

    private lateinit var editUserName: EditText
    private lateinit var editUserEmail: EditText
    private lateinit var editUserImage: EditText
    private lateinit var saveButton: Button
    private lateinit var databaseHandler: DatabaseHandler
    private var userId: Int = -1  // ID del usuario logueado

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        editUserName = view.findViewById(R.id.editUserName)
        editUserEmail = view.findViewById(R.id.editUserEmail)
        editUserImage = view.findViewById(R.id.editUserImage)
        saveButton = view.findViewById(R.id.saveProfileButton)

        databaseHandler = DatabaseHandler(requireContext())

        // Obtener ID del usuario logueado desde SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", -1)

        if (userId != -1) {
            cargarDatosUsuario(userId)
        }

        saveButton.setOnClickListener {
            actualizarPerfil()
        }

        return view
    }

    private fun cargarDatosUsuario(userId: Int) {
        val usuario = databaseHandler.getUsuarios().find { it.userId == userId }
        usuario?.let {
            editUserName.setText(it.userName)
            editUserEmail.setText(it.userEmail)
            editUserImage.setText(it.image)
        }
    }

    private fun actualizarPerfil() {
        val newUserName = editUserName.text.toString().trim()
        val newUserEmail = editUserEmail.text.toString().trim()
        val newImage = editUserImage.text.toString().trim()

        if (newUserName.isEmpty() || newUserEmail.isEmpty() || newImage.isEmpty()) {
            Toast.makeText(requireContext(), "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show()
            return
        }

        val usuarioActualizado = UsuarioModelClass(
            userId = userId,
            userName = newUserName,
            image = newImage,
            password = "",  // No se cambia la contraseña
            userEmail = newUserEmail
        )

        val result = databaseHandler.updateUsuario(usuarioActualizado)

        if (result > 0) {
            // Guardar nuevos datos en SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("userName", newUserName)
                putString("userEmail", newUserEmail)
                putString("userImage", newImage)
                apply()
            }

            Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()

            // Regresar al perfil
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
        }
    }
}
