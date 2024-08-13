package com.sotul.noteapp.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.transition.MaterialElevationScale
import com.sotul.noteapp.MainActivity
import com.sotul.noteapp.utils.exportToXLS
import com.sotul.noteapp.utils.exportToXLSX
import com.sotul.noteapp.utils.importFromExcel
import com.sotul.noteapp.databinding.FragmentSettingBinding
import com.sotul.noteapp.viewmodel.NoteViewModel
import com.sotul.noteapp.viewmodel.StateViewModel
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private val stateViewModel: StateViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(
            inflater,
            container,
            false
        )
        exitTransition = MaterialElevationScale(false).apply {
            duration = 350
        }
        enterTransition = MaterialElevationScale(true).apply {
            duration = 350
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel = (activity as MainActivity).noteViewModel

        binding.sortAlgoModeSwitch.isChecked = stateViewModel.sortMode.value!!
        binding.stringMatchingModeSwitch.isChecked = stateViewModel.stringMatchingMode.value!!

        setUpListeners(view)
    }

    private fun setUpListeners(view: View) {
        binding.sortAlgoModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (stateViewModel.sortMode.value != isChecked) {
                stateViewModel.setSortMode(isChecked)
                Toast.makeText(activity, "Sort algorithm updated to ${if (isChecked) "Quick Algorithm" else "Merge Algorithm"}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.stringMatchingModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (stateViewModel.stringMatchingMode.value != isChecked) {
                stateViewModel.setStringMatchingMode(isChecked)
                Toast.makeText(activity, "String Matching algorithm updated to ${if (isChecked) "BM Algorithm" else "KMP Algorithm"}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            view.findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToNoteFragment())
        }

        binding.importBtn.setOnClickListener {
            openFileChooser()
        }

        binding.exportXLSBtn.setOnClickListener {
            lifecycleScope.launch {
                val notes = noteViewModel.getAll()
                exportToXLS(notes, requireContext())
            }
        }

        binding.exportXLSXBtn.setOnClickListener {
            lifecycleScope.launch {
                val notes = noteViewModel.getAll()
                exportToXLSX(notes, requireContext())
            }
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            ))
        }
        startActivityForResult(intent, REQUEST_CODE_IMPORT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_IMPORT && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                // Handle the file import
                importFile(uri)
            }
        }
    }

    private fun importFile(uri: Uri) {
        val contentResolver = requireContext().contentResolver
        val mimeType = contentResolver.getType(uri)

        if (mimeType != null) {
            lifecycleScope.launch {
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val notes = when {
                            mimeType.contains("vnd.ms-excel") -> importFromExcel(inputStream, requireContext())
                            mimeType.contains("sheet") -> importFromExcel(inputStream, requireContext())
                            else -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Unsupported file type",
                                    Toast.LENGTH_SHORT
                                ).show()
                                emptyList()
                            }
                        }

                        if (notes == null) {
                            Toast.makeText(requireContext(), "Error importing file: The file's data format is not appropriate", Toast.LENGTH_SHORT).show()
                        } else {
                            noteViewModel.importNotes(notes)
                            Toast.makeText(requireContext(), "${notes.size} note(s) successfully imported", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error importing file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val REQUEST_CODE_IMPORT = 101
    }
}
