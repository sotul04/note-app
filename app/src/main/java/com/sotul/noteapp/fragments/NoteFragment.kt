package com.sotul.noteapp.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.sotul.noteapp.MainActivity
import com.sotul.noteapp.R
import com.sotul.noteapp.adapters.NoteAdapter
import com.sotul.noteapp.databinding.FragmentNoteBinding
import com.sotul.noteapp.model.Note
import com.sotul.noteapp.utils.SwipeDelete
import com.sotul.noteapp.utils.hideKeyboard
import com.sotul.noteapp.viewmodel.NoteViewModel
import com.sotul.noteapp.viewmodel.StateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NoteFragment : Fragment(R.layout.fragment_note) {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var  noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private val stateViewModel: StateViewModel by activityViewModels()

    private var filterOption = mutableListOf("All")
    private val sortMode = listOf("Default", "Title (A-Z)", "Title (Z-A)", "CreatedAt newer", "CreatedAt older", "UpdatedAt newer", "UpdatedAt older")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNoteBinding.inflate(
            inflater,
            container,
            false
        )

        exitTransition = MaterialElevationScale(false).apply {
            duration=350
        }
        enterTransition = MaterialElevationScale(true).apply {
            duration=350
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as MainActivity
        val navController = Navigation.findNavController(view)
        requireView().hideKeyboard()

        noteViewModel = (activity as MainActivity).noteViewModel
        
        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor= Color.parseColor("#9E9D9D")
        }

        binding.fabAddNote.setOnClickListener {
            binding.appBarLayout.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveOrUpdateFragment())
        }

        binding.innerFab.setOnClickListener {
            binding.appBarLayout.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveOrUpdateFragment())
        }

        binding.settingTextFab.setOnClickListener {
            binding.appBarLayout.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSettingFragment())
        }

        binding.settingFab.setOnClickListener {
            binding.appBarLayout.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSettingFragment())
        }

        recyclerViewDisplay()

        swipeDelete(binding.rvNote)

        binding.search.addTextChangedListener (object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.noData.isVisible = false
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.toString().isNotEmpty()) {
                    // search code
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // do something for search
            }

        })

        binding.search.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                v.clearFocus()
                requireView().hideKeyboard()
            }
            return@setOnEditorActionListener true
        }

        binding.rvNote.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->

            when{
                scrollY > oldScrollY -> {
                    binding.chatFabText.isVisible = false
                    binding.settingTextFab.isVisible = false
                }
                scrollX == scrollY -> {
                    binding.chatFabText.isVisible = true
                    binding.settingTextFab.isVisible = true
                }
                else -> {
                    binding.chatFabText.isVisible = true
                    binding.settingTextFab.isVisible = false
                }
            }
        }

        setUpSpinnerListener()

    }

    private fun setUpSpinnerListener() {
        lifecycleScope.launch {
            val notes = noteViewModel.getAll()
            val category = notes.map { it.category }.toSet()
            filterOption.clear()
            filterOption.add("All")
            category.forEach { filterOption.add(it) }

            val filterSpinner: Spinner = binding.filterSpinner
            val filterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOption)
            filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            filterSpinner.adapter = filterAdapter
            filterSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(view: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    val item = view!!.getItemAtPosition(position).toString()
                    Toast.makeText(activity, "Selected filter: $item", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

            val sortModeSpinner: Spinner = binding.sortModeSpinner
            val sortModeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortMode)
            sortModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sortModeSpinner.adapter = sortModeAdapter
            sortModeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedSortMode = sortMode[position]
                    Toast.makeText(requireContext(), "Selected sort mode: $selectedSortMode", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }

    private fun swipeDelete(rvNote: RecyclerView) {
        val swipeDeleteCallback = object: SwipeDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = noteAdapter.currentList[position]
                noteViewModel.deleteNote(note)
                binding.search.apply {
                    hideKeyboard()
                    clearFocus()
                }
                if (binding.search.text.toString().isEmpty()) {
                    observeDataChanges()
                }
                val snackbar = Snackbar.make(
                    requireView(), "Note deleted", Snackbar.LENGTH_LONG
                ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)

                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        transientBottomBar?.setAction("UNDO") {
                            noteViewModel.addNote(note)
                            binding.noData.isVisible = false
                        }
                        super.onShown(transientBottomBar)
                    }
                }).apply {
                    animationMode = Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.fab_add_note)
                }
                snackbar.setActionTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.yellowOrange
                    )
                )
                snackbar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeDeleteCallback)
        itemTouchHelper.attachToRecyclerView(rvNote)
    }

    private fun observeDataChanges() {
        noteViewModel.getAllNotes().observe(viewLifecycleOwner) { list ->
            binding.noData.isVisible = list.isEmpty()
            noteAdapter.submitList(list)
        }
    }

    private fun recyclerViewDisplay() {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> setUpRecyclerView(2)
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecyclerView(3)
        }
    }

    private fun setUpRecyclerView(spanCount: Int) {
        binding.rvNote.apply {
            layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            noteAdapter = NoteAdapter()
            noteAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = noteAdapter
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        observeDataChanges()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}