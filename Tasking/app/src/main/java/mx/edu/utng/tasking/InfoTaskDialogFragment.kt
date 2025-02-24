package mx.edu.utng.tasking

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView

class InfoTaskDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val task = arguments?.getSerializable("TASK_INFO") as? TareaModelClass

        val builder = MaterialAlertDialogBuilder(requireContext())

        val taskDetails = "Título: ${task?.titulo}\n" +
                "Descripción: ${task?.descripcion}\n" +
                "Estado: ${task?.estado}\n" +
                "Fecha liímite: ${task?.fechaFinalizacion}"

        builder.setTitle("Detalles de la Tarea")
            .setMessage(taskDetails)
            .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }

        return builder.create()
    }
}
