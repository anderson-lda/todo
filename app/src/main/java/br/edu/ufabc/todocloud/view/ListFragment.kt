package br.edu.ufabc.todocloud.view

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import br.edu.ufabc.todocloud.R
import br.edu.ufabc.todocloud.databinding.FragmentListBinding

import br.edu.ufabc.todocloud.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * Fragment to show lists of tasks.
 */
class ListFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentListBinding
    private val args: ListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.shouldRefresh.observe(viewLifecycleOwner) {
            if (it) refresh()
        }
        binding.fabAddTask.setOnClickListener {
            ListFragmentDirections.newTask().let {
                findNavController().navigate(it)
            }
        }
        val provider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_refresh -> viewModel.refresh().observe(viewLifecycleOwner) { refreshStatus ->
                        when (refreshStatus) {
                            is MainViewModel.RefreshStatus.Loading -> binding.progressHorizontal.visibility =
                                View.VISIBLE
                            is MainViewModel.RefreshStatus.Done -> binding.progressHorizontal.visibility =
                                View.INVISIBLE
                            is MainViewModel.RefreshStatus.Failure -> {
                                Log.e("VIEW", "Failed to refresh list", refreshStatus.e)
                                Snackbar.make(binding.root, "Failed to refresh data", Snackbar.LENGTH_LONG)
                                    .show()
                                binding.progressHorizontal.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
                return true
            }

        }
        activity?.addMenuProvider(provider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun refresh() {
        binding.recyclerViewList.apply {
            when (args.filterCriteria) {
                FilterCriteria.ALL -> viewModel.getPending()
                FilterCriteria.OVERDUE -> viewModel.getOverdue()
                FilterCriteria.COMPLETED -> viewModel.getCompleted()
                FilterCriteria.TAG -> viewModel.getByTag(args.tag)
            }.observe(viewLifecycleOwner) { status ->
                when (status) {
                    is MainViewModel.Status.Loading -> {
                        binding.progressHorizontal.visibility = View.VISIBLE
                    }
                    is MainViewModel.Status.Failure -> {
                        Log.e("VIEW", "Failed to fetch items", status.e)
                        Snackbar.make(binding.root, "Failed to list items", Snackbar.LENGTH_LONG)
                            .show()
                        binding.progressHorizontal.visibility = View.INVISIBLE
                    }
                    is MainViewModel.Status.Success -> {
                        val tasks = (status.result as MainViewModel.Result.TaskList).value

                        swapAdapter(TaskAdapter(tasks.sortedBy { it.deadline }, findNavController()), false)
                        addItemDecoration(
                            DividerItemDecoration(
                                this.context,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        binding.progressHorizontal.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }
}
