package mx.edu.utng.tasking

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksFragment : Fragment() {

    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var dbHandler: DatabaseHandler
    private var userId: Int = -1
    private lateinit var noTasksTextView: TextView
    private lateinit var addTaskButton: FloatingActionButton
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task, container, false)

        tasksRecyclerView = view.findViewById(R.id.tasksRecyclerView)
        noTasksTextView = view.findViewById(R.id.textViewNoTasks)
        addTaskButton = view.findViewById(R.id.addTaskButton)
        searchView = view.findViewById(R.id.searchView)
        dbHandler = DatabaseHandler(requireContext())

        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", -1)

        // Configurar RecyclerView
        tasksRecyclerView.layoutManager = LinearLayoutManager(context)
        loadTasks()

        // Configurar búsqueda en tiempo real
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Se puede ocultar el teclado si se desea
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                taskAdapter.filter.filter(newText)
                return true
            }
        })

        // Configurar el botón de agregar tarea
        addTaskButton.setOnClickListener {
            val dialogFragment = AddTareaDialogFragment()
            val bundle = Bundle()
            bundle.putInt("USER_ID", userId)
            dialogFragment.arguments = bundle
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(parentFragmentManager, "AddTareaDialogFragment")
        }

        childFragmentManager.setFragmentResultListener("editTask", viewLifecycleOwner) { _, _ ->
            loadTasks()
        }

        // Implementar el deslizar para eliminar en RecyclerView
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = taskAdapter.getItem(position)

                // Mostrar diálogo de confirmación
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar Tarea")
                    .setMessage("¿Estás seguro de eliminar esta tarea?")
                    .setPositiveButton("Sí") { dialog, which ->
                        task.id?.let { dbHandler.deleteTarea(it) }
                        loadTasks()
                    }
                    .setNegativeButton("No") { dialog, which ->
                        taskAdapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }
                    .setOnCancelListener {
                        taskAdapter.notifyItemChanged(position)
                    }
                    .show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)

        return view
    }

    // Función para cargar las tareas
    fun loadTasks() {
        if (userId == -1) {
            noTasksTextView.visibility = View.VISIBLE
            tasksRecyclerView.visibility = View.GONE
        } else {
            val tasks = dbHandler.getUserTasks(userId)
            if (tasks.isNotEmpty()) {
                taskAdapter = TaskAdapter(
                    requireContext(), tasks,
                    onEditTask = { task ->
                        val editTareaDialog = EditTareaDialogFragment()
                        val bundle = Bundle()
                        bundle.putSerializable("TAREA", task)
                        editTareaDialog.arguments = bundle
                        editTareaDialog.setTargetFragment(this, 0)
                        editTareaDialog.show(parentFragmentManager, "EditTareaDialogFragment")
                    },
                    onLongClick = { task ->
                        val infoDialog = InfoTaskDialogFragment()  // Debes implementar este DialogFragment
                        val bundle = Bundle()
                        bundle.putSerializable("TASK_INFO", task)
                        infoDialog.arguments = bundle
                        infoDialog.show(parentFragmentManager, "InfoTaskDialogFragment")
                    }
                )
                tasksRecyclerView.adapter = taskAdapter
                taskAdapter.notifyDataSetChanged()
                noTasksTextView.visibility = View.GONE
                tasksRecyclerView.visibility = View.VISIBLE
            } else {
                noTasksTextView.visibility = View.VISIBLE
                tasksRecyclerView.visibility = View.GONE
            }
        }
    }
}
