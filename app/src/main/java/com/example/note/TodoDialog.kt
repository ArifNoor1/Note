import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.example.note.R
import com.example.note.RealmTodo

class TodoDialog(
    context: Context,
    private val todo: RealmTodo? = null,
    private val onSave: (String, Boolean) -> Unit
) : AlertDialog(context) {

    private lateinit var titleEditText: EditText
    private lateinit var completedCheckBox: CheckBox
    private lateinit var saveButton: Button

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.add_update, null)
        setView(view)

        titleEditText = view.findViewById(R.id.todoTitleEditText)
        completedCheckBox = view.findViewById(R.id.todoCompletedCheckBox)
        saveButton = view.findViewById(R.id.saveButton)

        // Set initial values if editing
        todo?.let {
            titleEditText.setText(it.title)
            completedCheckBox.isChecked = it.completed
        }

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val completed = completedCheckBox.isChecked
            if (title.isNotEmpty()) {
                try {
                    onSave(title, completed)
                    dismiss()
                } catch (e: Exception) {
                    Log.e("TodoDialog", "Error saving todo", e)
                }
            } else {
                titleEditText.error = "Title cannot be empty"
            }
        }
    }
}
