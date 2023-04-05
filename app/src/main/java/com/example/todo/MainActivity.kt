package com.example.todo

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.navigation.fragment.NavHostFragment
import com.example.todo.databinding.ActivityMainBinding
import com.example.todo.view.ListFragment
import com.example.todo.view.ListFragmentDirections
import com.example.todo.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        initComponents()
        configureDynamicMenu()
        configureStaticMenu()
    }

    private fun initComponents(){
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        //val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navController = getNavController() //necessário pela troca de fragment por FragmentContainerView em content_main
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.destination_task_list
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    /**
     * Menu dinâmico, que o navigation UI não faz.
     */
    private fun configureDynamicMenu(){
        binding.navView.menu.findItem(R.id.task_list_tags)?.let{menuItem ->
            menuItem.subMenu?.let{subMenu ->
                viewModel.getTags().sorted().forEach { theTag ->
                    subMenu.add(theTag).setOnMenuItemClickListener{
                        val action = ListFragmentDirections.filterTaskList(
                            filterCriteria = ListFragment.FilterCriteria.TAG,
                            title = getString(R.string.tag_title, theTag),
                            tag = theTag
                        )
                        binding.drawerLayout.close()
                        getNavController().navigate(action)
                        true
                    }
                }
            }
        }
    }

    /**
     * Isso era feito automaticamente pelo navigation UI,
     * mas não servindo mais por causa do menu dinâmico.
     */
    private fun configureStaticMenu(){
        binding.navView.menu.forEach {
            it.setOnMenuItemClickListener { menuItem ->
                val action = when(menuItem.itemId){
                    R.id.task_list_all -> ListFragmentDirections.filterTaskList(
                        filterCriteria = ListFragment.FilterCriteria.ALL,
                        title = getString(R.string.menu_item_all_tasks)
                    )
                    R.id.task_list_overdue -> ListFragmentDirections.filterTaskList(
                        filterCriteria = ListFragment.FilterCriteria.OVERDUE,
                        title = getString(R.string.menu_item_overdue)
                    )
                    R.id.task_list_completed -> ListFragmentDirections.filterTaskList(
                        filterCriteria = ListFragment.FilterCriteria.COMPLETED,
                        title = getString(R.string.menu_item_completed)
                    )
                    else -> ListFragmentDirections.filterTaskList()
                }
                binding.drawerLayout.close()
                getNavController().navigate(action)
                true
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        //val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navController = getNavController()
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getNavController() =
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
                as NavHostFragment).navController
}