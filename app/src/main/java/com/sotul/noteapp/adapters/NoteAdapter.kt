package com.sotul.noteapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.sotul.noteapp.R
import com.sotul.noteapp.databinding.NoteLayoutItemBinding
import com.sotul.noteapp.fragments.NoteFragmentDirections
import com.sotul.noteapp.model.Note
import com.sotul.noteapp.utils.hideKeyboard
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak

class NoteAdapter: ListAdapter<Note, NoteAdapter.NoteViewHolder>(DiffUtilCallback()) {

    inner class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = NoteLayoutItemBinding.bind(itemView)
        val title: MaterialTextView = binding.noteItemTitle
        val content: TextView = binding.noteContentItem
        val date: MaterialTextView = binding.noteDate
        val category: MaterialTextView = binding.noteCategory
        val parent: MaterialCardView = binding.noteItemParent
        val markwon = Markwon.builder(itemView.context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(itemView.context))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(
                        SoftLineBreak::class.java
                    ) {
                            visitor, _ -> visitor.forceNewLine()
                    }
                }
            })
            .build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.note_layout_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        getItem(position).let { note ->
            holder.apply {
                parent.transitionName = "recyclerView_${note.id}"
                title.text = note.title
                markwon.setMarkdown(content, note.content)
                if (note.createdAt == note.updatedAt) {
                    date.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_calendar_today_24, 0,0,0)
                } else {
                    date.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_edit_calendar_24, 0,0,0)
                }
                category.text = note.category
                date.text = note.updatedAt
                parent.setCardBackgroundColor(note.color)
                itemView.setOnClickListener {
                    val action = NoteFragmentDirections.actionNoteFragmentToSaveOrDeleteFragment(note)
                    val extras = FragmentNavigatorExtras(parent to "recyclerView_${note.id}")
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action, extras)
                }
                content.setOnClickListener {
                    val action = NoteFragmentDirections.actionNoteFragmentToSaveOrDeleteFragment(note)
                    val extras = FragmentNavigatorExtras(parent to "recyclerView_${note.id}")
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action, extras)
                }
            }
        }
    }
}
