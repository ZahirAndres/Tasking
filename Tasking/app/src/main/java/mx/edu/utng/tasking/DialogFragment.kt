package mx.edu.utng.tasking

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class AddTareaDialogFragment : DialogFragment() {

    private lateinit var tituloEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var estadoSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var databaseHandler: DatabaseHandler
    private var userId: Int = -1

    private lateinit var fechaFinalizacionEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dialog, container, false)

        // Inicializar vistas
        tituloEditText = view.findViewById(R.id.editTextTitulo)
        descripcionEditText = view.findViewById(R.id.editTextDescripcion)
        estadoSpinner = view.findViewById(R.id.spinnerEstado)
        saveButton = view.findViewById(R.id.buttonSave)
        fechaFinalizacionEditText = view.findViewById(R.id.editTextFechaFinalizacion)

        // Configurar el DatePickerDialog
        fechaFinalizacionEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Actualizar el EditText con la fecha seleccionada
                val fecha = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                fechaFinalizacionEditText.setText(fecha)
            }, year, month, day)

            datePickerDialog.show()
        }

        // Inicializar el manejador de la base de datos
        databaseHandler = DatabaseHandler(requireContext())

        // Obtener el userId pasado como argumento
        userId = arguments?.getInt("USER_ID", -1) ?: -1

        // Configurar el Spinner de estado
        val estados = arrayOf("Pendiente", "En Progreso", "Completada")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        estadoSpinner.adapter = adapter

        // Configurar el botón de guardar
        saveButton.setOnClickListener {
            saveTarea()
        }

        return view
    }


    private fun saveTarea() {
        val titulo = tituloEditText.text.toString()
        val descripcion = descripcionEditText.text.toString()
        val estado = estadoSpinner.selectedItem.toString()
        val fechaFinalizacion = fechaFinalizacionEditText.text.toString()

        if (titulo.isNotEmpty() && descripcion.isNotEmpty() && estado.isNotEmpty() && fechaFinalizacion.isNotEmpty() && userId != -1) {
            val tarea = TareaModelClass(
                userId = userId,
                titulo = titulo,
                descripcion = descripcion,
                estado = estado,
                fechaFinalizacion = fechaFinalizacion
            )

            val result = databaseHandler.addTarea(tarea)

            if (result != -1L) {
                Toast.makeText(requireContext(), "Tarea guardada correctamente", Toast.LENGTH_SHORT).show()
                dismiss()

                // Notificar al fragmento principal para recargar la lista
                targetFragment?.let {
                    (it as TasksFragment).loadTasks()
                }
            } else {
                Toast.makeText(requireContext(), "Error al guardar la tarea", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }



}