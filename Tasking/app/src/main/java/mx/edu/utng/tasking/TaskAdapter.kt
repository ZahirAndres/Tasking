package mx.edu.utng.tasking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val context: Context,
    private val originalTasks: List<TareaModelClass>,
    private val onEditTask: (TareaModelClass) -> Unit,
    private val onLongClick: (TareaModelClass) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(), Filterable {

    // Lista que se usará para mostrar los elementos filtrados
    private var filteredTasks: MutableList<TareaModelClass> = originalTasks.toMutableList()

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val statusTextView: TextView = itemView.findViewById(R.id.textViewStatus)

        init {
            // Click para editar la tarea
            itemView.setOnClickListener {
                onEditTask(filteredTasks[adapterPosition])
            }
            // Long press para mostrar información completa
            itemView.setOnLongClickListener {
                onLongClick(filteredTasks[adapterPosition])
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = filteredTasks[position]
        holder.titleTextView.text = task.titulo
        holder.statusTextView.text = task.estado
    }

    override fun getItemCount(): Int = filteredTasks.size

    fun getItem(position: Int): TareaModelClass {
        return filteredTasks[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                filteredTasks = if (charString.isEmpty()) {
                    originalTasks.toMutableList()
                } else {
                    val filteredList = originalTasks.filter {
                        it.titulo.contains(charString, ignoreCase = true) ||
                                it.estado.contains(charString, ignoreCase = true)
                    }
                    filteredList.toMutableList()
                }
                val filterResults = FilterResults()
                filterResults.values = filteredTasks
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredTasks = results?.values as MutableList<TareaModelClass>
                notifyDataSetChanged()
            }
        }
    }
}
