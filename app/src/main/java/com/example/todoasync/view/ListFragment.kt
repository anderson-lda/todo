package com.example.todoasync.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.todoasync.databinding.FragmentListBinding
import com.example.todoasync.viewmodel.MainViewModel

/**
 * Fragmento que mostra uma recyclerview.
 */
class ListFragment: Fragment() {
    private lateinit var binding: FragmentListBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val args: ListFragmentArgs by navArgs()

    enum class FilterCriteria {
        ALL,
        COMPLETED,
        OVERDUE,
        TAG
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        viewModel.isDataReady.observe(viewLifecycleOwner) { isReady ->
            if(isReady) {
                val adapter = TaskAdapter(when (args.filterCriteria as FilterCriteria){
                    FilterCriteria.ALL -> viewModel.getPending()
                    FilterCriteria.OVERDUE -> viewModel.getOverdue()
                    FilterCriteria.COMPLETED -> viewModel.getCompleted()
                    FilterCriteria.TAG -> viewModel.getByTag(args.tag)
                }.sortedBy { task -> task.deadline })

                binding.recyclerViewList.apply {
                    addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
                }
                binding.recyclerViewList.swapAdapter(adapter, false)
            }
        }
    }
}