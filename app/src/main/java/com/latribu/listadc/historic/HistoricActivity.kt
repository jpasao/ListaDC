package com.latribu.listadc.historic
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.adapters.HistoricAdapter
import com.latribu.listadc.common.factories.HistoricViewModelFactory
import com.latribu.listadc.common.factories.UserViewModelFactory
import com.latribu.listadc.common.models.Historic
import com.latribu.listadc.common.models.Status
import com.latribu.listadc.common.models.User
import com.latribu.listadc.common.repositories.historic.AppCreator
import com.latribu.listadc.common.sendEmail
import com.latribu.listadc.common.viewmodels.HistoricViewModel
import com.latribu.listadc.common.viewmodels.PreferencesViewModel
import com.latribu.listadc.common.viewmodels.UserViewModel
import com.latribu.listadc.databinding.ActivityHistoricBinding
import com.latribu.listadc.main.MainActivity

class HistoricActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoricBinding
    private var installationId: String = ""
    private lateinit var recyclerview: RecyclerView
    private var initialized = false
    private lateinit var mHistoricViewModel: HistoricViewModel
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var mRecyclerAdapter: HistoricAdapter
    private var savedUser: User = Constants.DEFAULT_USER
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var spinner: ProgressBar
    private lateinit var userDropdown: Spinner
    private lateinit var dateDropdown: Spinner
    private var filterObject = FilterObject("0", "0")

    class FilterObject(days: String, user: String) {
        var daysBefore = days
        var selectedUser = user
        override fun toString(): String {
            return "${daysBefore}|${selectedUser}"
        }
    }

    class FilterDate(days: String, name: String) {
        val daysBefore = days
        val displayName = name
        override fun toString(): String {
            return displayName
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        getUser()
        getInstallationId(binding)
        bindElements(binding)
        setListeners()
        setRecyclers(binding)
    }

    private fun bindElements(binding: ActivityHistoricBinding) {
        pullToRefresh = binding.historicSwipeLayout
        spinner = binding.spinningList
        userDropdown = binding.userFilter
        dateDropdown = binding.dateFilter
    }

    private fun setListeners() {
        pullToRefresh.setOnRefreshListener {
            getHistoric()
            pullToRefresh.isRefreshing = false
        }
    }

    private fun initData() {
        mHistoricViewModel = ViewModelProvider(
            this,
            HistoricViewModelFactory(AppCreator.getApiHelperInstance())
        )[HistoricViewModel::class.java]

        mUserViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(com.latribu.listadc.common.repositories.user.AppCreator.getApiHelperInstance())
        )[UserViewModel::class.java]

        preferencesViewModel = ViewModelProvider(
            this
        )[PreferencesViewModel::class.java]

        mRecyclerAdapter = HistoricAdapter(
            longClickListener = { item: Historic -> itemLongPressed(item) }
        )
    }

    private fun getUsers() {
        mUserViewModel
            .getAllUsers(installationId)
            .observe(this) {
                when(it.status) {
                    Status.SUCCESS -> {
                        loadUserOptions(it.data!!)
                        loadDateOptions()
                        spinner.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        sendEmail(this,
                            this,
                            findViewById(R.id.historic_activity),
                            "Error en getUsers",
                            getString(R.string.saveError, installationId, "al obtener los usuarios: ${it.message}"),
                            installationId)
                        spinner.visibility = View.GONE
                    }
                }
            }
    }

    private fun getUser() {
        binding.apply {
            preferencesViewModel.getUser.observe(this@HistoricActivity) { data ->
                savedUser = data
            }
        }
    }

    private fun getInstallationId(binding: ActivityHistoricBinding) {
        val firebaseInstance = Observer<String> { data ->
            if (data.isNotEmpty()) {
                installationId = data
                if (!initialized) setRecyclers(binding)
                getUsers()
                getHistoric()
            }
        }
        MainActivity.firebaseInstanceId.observeForever(firebaseInstance)
    }

    private fun setRecyclers(binding: ActivityHistoricBinding) {
        recyclerview = binding.historicRecyclerView
        with(recyclerview) {
            layoutManager = LinearLayoutManager(this.context)
            adapter = mRecyclerAdapter
        }
        initialized = true
    }

    private fun itemLongPressed(item: Historic) {
        val sentText = if (item.firebaseSent == 1) "Ya se envió la notificación" else "Aún no ha salido la notificación"
        val showDetails = HistoricDetailBottomFragment(sentText, item.remoteAddr)
        supportFragmentManager.let { showDetails.show(it, HistoricDetailBottomFragment.TAG) }
    }

    private fun loadUserOptions(users: List<User>) {
        val userArray = arrayListOf(getString(R.string.historic_filter_all))
        users.forEach { userArray.add(it.name) }
        val spAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, userArray)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(userDropdown) {
            adapter = spAdapter
            setSelection(0, false)
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val dateSelection: String = (dateDropdown.selectedItem as FilterDate).daysBefore
                    val userSelection: String = userArray[position]
                    filterObject.daysBefore = dateSelection
                    filterObject.selectedUser = userSelection
                   // search follows the 'days|user' structure
                    val searchTerm: String = filterObject.toString()
                    mRecyclerAdapter.filter.filter(searchTerm)
                }
            }
        }
    }

    private fun loadDateOptions() {
        val dateList: MutableList<FilterDate> = arrayListOf(
            FilterDate("0", getString(R.string.historic_filter_today)),
            FilterDate("1", getString(R.string.historic_filter_yesterday)),
            FilterDate("7", getString(R.string.historic_filter_week)),
            FilterDate("30", getString(R.string.historic_filter_month))
        )

        val dateArrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dateList)
        with(dateDropdown) {
            adapter = dateArrayAdapter
            setSelection(3, false)
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val dateSelection = (parent?.selectedItem as FilterDate).daysBefore
                    val userSelection: String = userDropdown.selectedItem.toString()
                    filterObject.daysBefore = dateSelection
                    filterObject.selectedUser = userSelection
                    // search follows the 'days|user' structure
                    val searchTerm: String = filterObject.toString()
                    mRecyclerAdapter.filter.filter(searchTerm)
                }
            }
        }
    }

    private fun getHistoric() {
        mHistoricViewModel
            .getHistoric(installationId, savedUser.id, -1)
            .observe(this) {
                when(it.status) {
                    Status.SUCCESS -> {
                        mRecyclerAdapter.updateRecyclerData(it.data!!)
                        spinner.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        spinner.visibility = View.VISIBLE
                    }
                    Status.FAILURE -> {
                        val savedUserName: String = savedUser.name.ifEmpty { installationId }
                        sendEmail(this,
                            this,
                            findViewById(R.id.historic_activity),
                            "Error en getHistoric",
                            getString(R.string.saveError, savedUserName, "al obtener el histórico: ${it.message}"),
                            installationId)
                        spinner.visibility = View.GONE
                    }
                }
            }
    }
}