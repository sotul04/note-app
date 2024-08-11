package com.sotul.noteapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.currentCompositionErrors
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.sotul.noteapp.MainActivity
import com.sotul.noteapp.R
import com.sotul.noteapp.databinding.BottomSheetLayoutBinding
import com.sotul.noteapp.databinding.FragmentSaveOrDeleteBinding
import com.sotul.noteapp.model.Note
import com.sotul.noteapp.utils.hideKeyboard
import com.sotul.noteapp.viewmodel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SaveOrDeleteFragment : Fragment(R.layout.fragment_save_or_delete) {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSaveOrDeleteBinding
    private var note: Note? = null
    private var color = -1
    private lateinit var result: String
    private val noteViewModel: NoteViewModel by activityViewModels()
    private val job = CoroutineScope(Dispatchers.Main)
    private val args: SaveOrDeleteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialElevationScale(false).apply {
            duration=350
        }
        enterTransition = MaterialElevationScale(true).apply {
            duration=350
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSaveOrDeleteBinding.bind(view)
        navController = Navigation.findNavController(view)
        val activity = activity as MainActivity

        ViewCompat.setTransitionName(
            binding.noteContentFragmentParent,
            "recyclerView_${args.note?.id}"
        )

        binding.backButton.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

        binding.saveNote.setOnClickListener {
            saveNote()
        }

        try {
            binding.etNoteContent.setOnFocusChangeListener {_, hasfocus ->
                if (hasfocus) {
                    binding.bottomBar.visibility= View.VISIBLE
                    binding.etNoteContent.setStylesBar(binding.styleBar)
                } else binding.bottomBar.visibility = View.GONE
            }
        } catch (e: Throwable) {
            Log.d("TAG", e.stackTraceToString())
        }

        binding.fabColorPick.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                requireContext(),
                R.style.BottomSheetDialogTheme
            )
            val bottomSheetView: View = layoutInflater.inflate(
                R.layout.bottom_sheet_layout,
                null
            )
            with(bottomSheetDialog) {
                setContentView(bottomSheetView)
                show()
            }

            val bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView)
            bottomSheetBinding.apply {
                colorPicker.apply {
                    setSelectedColor(color)
                    setOnColorSelectedListener { value ->
                        color = value
                        binding.apply {
                            noteContentFragmentParent.setBackgroundColor(color)
                            toolBarFragmentNoteContent.setBackgroundColor(color)
                            bottomBar.setBackgroundColor(color)
                            activity.window.statusBarColor = color
                        }
                        bottomSheetBinding.bottomSheetParent.setBackgroundColor(color)
                    }
                }
                bottomSheetParent.setCardBackgroundColor(color)
            }
            bottomSheetView.post {
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        setUpNote()

    }

    private fun setUpNote() {
        val note = args.note
        if (note != null) {
            val currentNote = args.note!!
            if (currentNote.createdAt != currentNote.updatedAt) {
                val content = "Edited at ${currentNote.updatedAt}"
                binding.lastEdited.text = content
            } else {
                val content = "Created at ${currentNote.createdAt}"
                binding.lastEdited.text = content
            }
            binding.etTitle.setText(note.title)
            binding.etNoteContent.renderMD(note.content)
            binding.etCategory.setText(note.category)
            binding.apply {
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(note.color)
                }
                toolBarFragmentNoteContent.setBackgroundColor(note.color)
                bottomBar.setBackgroundColor(note.color)
            }
            activity?.window?.statusBarColor = note.color
        } else {
            binding.lastEdited.visibility = View.GONE
        }
    }

    private fun saveNote() {
        if (binding.etNoteContent.text.toString().isEmpty() ||
            binding.etTitle.text.toString().isEmpty()) {
            Toast.makeText(activity, "Some fields are empty", Toast.LENGTH_SHORT).show()
        } else {
            note = args.note
            Log.d("SaveOrDeleteFragment", "Markdown content: ${binding.etNoteContent.text.toString()}")
            when(note) {
                null -> {
                    noteViewModel.addNote(
                        Note(
                            0,
                                binding.etTitle.text.toString(),
                                binding.etNoteContent.text.toString(),
                                binding.etCategory.text.toString(),
                                color
                        )
                    )

                    result = "Note Saved"
                    setFragmentResult(
                        "key",
                        bundleOf("bundleKey" to result)
                    )
                    Toast.makeText(activity, "Your note has been saved", Toast.LENGTH_SHORT).show()
                    navController.navigate(SaveOrDeleteFragmentDirections.actionSaveOrDeleteFragmentToNoteFragment())
                }
                else -> {
                    updateNote()
                    navController.popBackStack()
                }
            }
        }
    }

    private fun updateNote() {
        if (note != null) {
            noteViewModel.modifyNote(
                Note(
                    note!!.id,
                    binding.etTitle.toString(),
                    binding.etNoteContent.toString(),
                    binding.etCategory.toString(),
                    color,
                    note!!.createdAt
                )
            )
        }
    }


}