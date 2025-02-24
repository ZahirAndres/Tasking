    package mx.edu.utng.tasking

    import android.app.DatePickerDialog
    import android.os.Bundle
    import androidx.fragment.app.Fragment
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

    class EditTareaDialogFragment : DialogFragment() {

        private lateinit var dbHandler: DatabaseHandler
        private lateinit var tarea: TareaModelClass
        private lateinit var titleEditText: EditText
        private lateinit var descriptionEditText: EditText
        private lateinit var statusSpinner: Spinner
        private lateinit var fechaFinalizacionEditText: EditText

        private var dateSelected: String = ""  // Guardar la fecha seleccionada

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_edit_tarea_dialog, container, false)

            dbHandler = DatabaseHandler(requireContext())

            titleEditText = view.findViewById(R.id.editTextTitulo)
            descriptionEditText = view.findViewById(R.id.editTextDescripcion)
            statusSpinner = view.findViewById(R.id.spinnerEstado)
            fechaFinalizacionEditText = view.findViewById(R.id.editTextFechaFinalizacion)

            // Obtener la tarea desde el argumento
            tarea = arguments?.getSerializable("TAREA") as? TareaModelClass
                ?: throw IllegalArgumentException("TAREA no proporcionada en los argumentos")

            // Rellenar los campos con los datos actuales
            titleEditText.setText(tarea.titulo)
            descriptionEditText.setText(tarea.descripcion)
            fechaFinalizacionEditText.setText(tarea.fechaFinalizacion)

            // Configurar el Spinner de estado
            val estados = arrayOf("Pendiente", "En Progreso", "Completada")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            statusSpinner.adapter = adapter

            // Establecer el valor actual del estado en el Spinner
            val estadoIndex = estados.indexOf(tarea.estado)
            if (estadoIndex >= 0) {
                statusSpinner.setSelection(estadoIndex)
            }

            // Configurar el click en el campo de fecha
            fechaFinalizacionEditText.setOnClickListener {
                showDatePickerDialog()
            }

            // Botón de actualizar
            view.findViewById<Button>(R.id.updateButton).setOnClickListener {
                updateTask()
            }

            return view
        }

        // Método para mostrar el DatePickerDialog
        private fun showDatePickerDialog() {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val monthStr = if (selectedMonth + 1 < 10) "0${selectedMonth + 1}" else "${selectedMonth + 1}"
                    val dayStr = if (selectedDay < 10) "0$selectedDay" else "$selectedDay"
                    dateSelected = "$selectedYear-$monthStr-$dayStr"
                    fechaFinalizacionEditText.setText(dateSelected)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        private fun updateTask() {
            // Obtener los nuevos valores
            val newTitle = titleEditText.text.toString()
            val newDescription = descriptionEditText.text.toString()
            val newStatus = statusSpinner.selectedItem.toString()
            val newFechaFinalizacion = fechaFinalizacionEditText.text.toString()

            // Validar campos
            if (newTitle.isBlank() || newDescription.isBlank() || newStatus.isBlank() || newFechaFinalizacion.isBlank()) {
                Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return
            }

            // Actualizar la tarea en la base de datos
            tarea.titulo = newTitle
            tarea.descripcion = newDescription
            tarea.estado = newStatus
            tarea.fechaFinalizacion = newFechaFinalizacion

            val result = dbHandler.updateTarea(tarea)

            if (result > 0) {
                // Notificar al fragmento principal para recargar la lista
                targetFragment?.let {
                    (it as TasksFragment).loadTasks()
                }
                Toast.makeText(requireContext(), "Tarea actualizada", Toast.LENGTH_SHORT).show()

                parentFragmentManager.setFragmentResult("editTask", Bundle())
                dismiss()

            } else {
                Toast.makeText(requireContext(), "Error al actualizar la tarea", Toast.LENGTH_SHORT).show()
            }
        }


    }


