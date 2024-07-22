import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.note.R
import com.example.note.RealmTodo
import io.realm.Realm
import io.realm.kotlin.where

class TodoAdapter(
    private val todos: List<RealmTodo>,
    private val onEdit: (RealmTodo) -> Unit,
    private val onDelete: (RealmTodo) -> Unit)
    : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]
        holder.bind(todo)
    }

    override fun getItemCount() = todos.size


    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val todoTitle: TextView = itemView.findViewById(R.id.textView_title)
        private val todoCompleted: CheckBox = itemView.findViewById(R.id.checkBox_completed)
        private val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(todo: RealmTodo) {
            todoTitle.text = todo.title
            todoCompleted.isChecked = todo.completed
            editButton.setOnClickListener { onEdit(todo) }
            deleteButton.setOnClickListener { onDelete(todo) }
        }
    }
}
