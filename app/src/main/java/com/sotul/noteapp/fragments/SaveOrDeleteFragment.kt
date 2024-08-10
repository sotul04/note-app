package com.sotul.noteapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import com.sotul.noteapp.MainActivity
import com.sotul.noteapp.R
import com.sotul.noteapp.databinding.BottomSheetLayoutBinding
import com.sotul.noteapp.databinding.FragmentSaveOrDeleteBinding
import com.sotul.noteapp.model.Note
import com.sotul.noteapp.utils.hideKeyboard
import com.sotul.noteapp.viewmodel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Date

class SaveOrDeleteFragment : Fragment(R.layout.fragment_save_or_delete) {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSaveOrDeleteBinding
    private var note: Note? = null
    private var color = -1
    private val noteViewModel: NoteViewModel by activityViewModels()
    private val job = CoroutineScope(Dispatchers.Main)
    private val args: SaveOrDeleteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = MaterialContainerTransform().apply {
            drawingViewId=R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration=300L
        }

        sharedElementEnterTransition=animation
        sharedElementReturnTransition=animation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSaveOrDeleteBinding.bind(view)
        navController = Navigation.findNavController(view)
        val activity = activity as MainActivity

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

    }

    private fun saveNote() {
        if (binding.etNoteContent.text.toString().isEmpty() ||
            binding.etTitle.text.toString().isEmpty()) {
            Toast.makeText(activity, "Some fields are empty", Toast.LENGTH_SHORT).show()
        } else {
            note = args.note
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
                    navController.navigate(SaveOrDeleteFragmentDirections.actionSaveOrDeleteFragmentToNoteFragment())
                }
                else -> {
                    // update the note
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_save_or_delete, container, false)
    }
}