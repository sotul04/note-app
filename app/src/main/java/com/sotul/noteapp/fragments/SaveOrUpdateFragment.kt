package com.sotul.noteapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialElevationScale
import com.sotul.noteapp.MainActivity
import com.sotul.noteapp.R
import com.sotul.noteapp.databinding.BottomSheetLayoutBinding
import com.sotul.noteapp.databinding.FragmentSaveOrUpdateBinding
import com.sotul.noteapp.model.Note
import com.sotul.noteapp.utils.hideKeyboard
import com.sotul.noteapp.viewmodel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SaveOrUpdateFragment : Fragment(R.layout.fragment_save_or_update) {

    private var _binding: FragmentSaveOrUpdateBinding? = null
    private val binding get() = _binding!!
    private var note: Note? = null
    private var color = -1
    private lateinit var result: String
    private val noteViewModel: NoteViewModel by activityViewModels()
    private val job = CoroutineScope(Dispatchers.Main)
    private val args: SaveOrUpdateFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialElevationScale(false).apply {
            duration=350
        }
        enterTransition = MaterialElevationScale(true).apply {
            duration=350
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaveOrUpdateBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity

        ViewCompat.setTransitionName(
            binding.noteContentFragmentParent,
            "recyclerView_${args.note?.id}"
        )

        binding.backButton.setOnClickListener {
            requireView().hideKeyboard()
            view.findNavController().navigate(SaveOrUpdateFragmentDirections.actionSaveOrUpdateFragmentToNoteFragment())
        }

        binding.saveNote.setOnClickListener {
            saveNote(view)
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
            }
            activity?.window?.statusBarColor = note.color
            color = note.color
        } else {
            binding.lastEdited.visibility = View.GONE
        }
    }

    private fun saveNote(view: View) {
        if (binding.etNoteContent.text.toString().isEmpty() ||
            binding.etTitle.text.toString().isEmpty() ||
            binding.etCategory.text.toString().isEmpty()) {
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
                    Log.d("Saving Note", "${binding.etTitle.text}:${binding.etCategory.text}")
                    Log.d("Content Note", binding.etNoteContent.text.toString())
                    result = "Note Saved"
                    setFragmentResult(
                        "key",
                        bundleOf("bundleKey" to result)
                    )
                    Toast.makeText(activity, "Your note has been saved", Toast.LENGTH_SHORT).show()
                    view.findNavController().navigate(SaveOrUpdateFragmentDirections.actionSaveOrUpdateFragmentToNoteFragment())
                }
                else -> {
                    updateNote()
                    view.findNavController().popBackStack()
                }
            }
        }
    }

    private fun updateNote() {
        if (note != null) {
            Toast.makeText(activity, "Your note has been updated", Toast.LENGTH_SHORT).show()
            noteViewModel.modifyNote(
                Note(
                    note!!.id,
                    binding.etTitle.text.toString(),
                    binding.etNoteContent.text.toString(),
                    binding.etCategory.text.toString(),
                    color,
                    note!!.createdAt
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}