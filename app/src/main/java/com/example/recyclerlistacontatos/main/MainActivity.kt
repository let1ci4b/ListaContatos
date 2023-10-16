package com.example.recyclerlistacontatos.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerlistacontatos.R
import com.example.recyclerlistacontatos.addcontacts.AddContactActivity
import com.example.recyclerlistacontatos.databinding.MainLayoutBinding
import com.example.recyclerlistacontatos.models.ConfirmationDialog
import com.example.recyclerlistacontatos.models.Contacts
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), RecyclerViewAdapter.OnItemClick, ConfirmationDialog.ConfirmationDialogListener {
    private lateinit var binding: MainLayoutBinding
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private var itemViewPosition: Int = 0
    var REQUEST_PHONE_CALL = 1
    var REQUEST_SEND_SMS = 2
    private var viewModel: MainViewModel = MainViewModel()
    enum class ViewLayoutMode { NOSEARCH, NORESULTONSEARCH, RESULTONSEARCH, ONSEARCHDEFAULT, ONSEARCHMODEEXIT, DELETEMODEON, DELETEMODEOFF }

    /// TODO replace project to MVVM architecture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        viewModel.showNoContactsWarning(binding.iconNoContactWarning, binding.noContactsWarning)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.mainToolbar.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun setupListeners() {
        with(binding) {
            buttonAddContact.setOnClickListener {
                val intent = Intent(this@MainActivity, AddContactActivity::class.java)
                startActivity(intent)
            }
            iconCloseSearchView.setOnClickListener {
                setupViewModeBackground(ViewLayoutMode.ONSEARCHMODEEXIT)
            }
            iconBackSearchView.setOnClickListener {
                if (searchViewQuery.text.isNullOrEmpty()) setupViewModeBackground(ViewLayoutMode.ONSEARCHMODEEXIT)
                else searchViewQuery.text = null
            }
            bottomBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.deleteItem -> {
                        if(viewModel.getSelectItemsCount() != 0) showCancelDialog()
                        true
                    }
                    R.id.exitItem -> {
                        setupViewModeBackground(ViewLayoutMode.DELETEMODEOFF)
                        true
                    }
                    else -> false
                }
            }
            searchViewQuery.doOnTextChanged { text, start, before, count ->
                viewModel.filter(searchViewQuery.text.toString(), recyclerViewAdapter, iconNoContactWarning, noContactsWarning, recyclerView)
            }
            selectAll.setOnClickListener {
                viewModel.selectAllItems(selectAll, allSelected, recyclerViewAdapter)
                setupViewModeBackground(ViewLayoutMode.DELETEMODEON)
            }
            allSelected.setOnClickListener {
                viewModel.allItemsSelected(selectAll, allSelected, recyclerViewAdapter)
                setupViewModeBackground(ViewLayoutMode.DELETEMODEON)
            }
            searchIcon.setOnClickListener {
                viewModel.onSearchDefault(recyclerViewAdapter, searchBar, searchIcon, searchViewQuery)
                viewModel.noSearch(noContactsWarning, iconNoContactWarning, recyclerView)
                searchViewQuery.requestFocus()
                UIUtil.showKeyboard(this@MainActivity, searchViewQuery)
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerViewAdapter = RecyclerViewAdapter(viewModel.getAlphabeticalSortedList(), binding.recyclerView, this)
        binding.recyclerView.apply {
            setHasFixedSize(false)
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        }
        setupTouchOnSwiped()
    }

    override fun onPause() {
        super.onPause()
        setupViewModeBackground(ViewLayoutMode.ONSEARCHMODEEXIT)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        recyclerViewAdapter.filterList(viewModel.getAlphabeticalSortedList())
        recyclerViewAdapter.notifyDataSetChanged()
        setupViewModeBackground(ViewLayoutMode.DELETEMODEOFF)
    }

    private fun setupTouchOnSwiped() {
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.message_background))
                    .addSwipeRightActionIcon(R.drawable.ic_message)
                    .addSwipeRightLabel(getString(R.string.message_label))
                    .setSwipeRightLabelColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    .addCornerRadius(1,32)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.call_background))
                    .addSwipeLeftActionIcon(R.drawable.ic_call)
                    .addSwipeLeftLabel(getString(R.string.call_label))
                    .setSwipeLeftLabelColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        itemViewPosition = position
                        checkPermissionAndSendIntent(Manifest.permission.CALL_PHONE, REQUEST_PHONE_CALL)
                    }
                    ItemTouchHelper.RIGHT -> {
                        itemViewPosition = position
                        checkPermissionAndSendIntent(Manifest.permission.SEND_SMS, REQUEST_SEND_SMS)
                    }
                }
            }
        }
        ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.recyclerView)
    }

    private fun sendIntent(action: String, uriString: String){
        val phoneIntent = Intent(action)
        phoneIntent.data = Uri.parse(uriString)
        startActivity(phoneIntent)
    }

    private fun checkPermissionAndSendIntent(permission: String, requestCode: Int) {
        if (ActivityCompat.checkSelfPermission(this@MainActivity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            when(requestCode) {
                1 -> sendIntent(Intent.ACTION_CALL, "tel: ${viewModel.getContactNumber(itemViewPosition)}")
                2 -> sendIntent(Intent.ACTION_SENDTO, "smsto: ${viewModel.getContactNumber(itemViewPosition)}")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            1 -> if(grantResults[0]== PackageManager.PERMISSION_GRANTED) sendIntent(Intent.ACTION_CALL, "tel: ${viewModel.getContactNumber(itemViewPosition)}")
            else printPermissionDeniedTextOnScreen()
            2 -> if(grantResults[0]== PackageManager.PERMISSION_GRANTED) sendIntent(Intent.ACTION_SENDTO, "smsto: ${viewModel.getContactNumber(itemViewPosition)}")
            else printPermissionDeniedTextOnScreen()
        }
    }

    private fun printPermissionDeniedTextOnScreen() {
        Toast.makeText(this@MainActivity, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
    }

    private fun setupViewModeBackground(searchModeAction: ViewLayoutMode) {
        with(binding) {
            when (searchModeAction) {
                ViewLayoutMode.NORESULTONSEARCH -> {
                    viewModel.noResultOnSearch(noContactsWarning, recyclerView)
                }
                ViewLayoutMode.RESULTONSEARCH -> {
                    viewModel.resultOnSearch(noContactsWarning, recyclerView)
                }
                ViewLayoutMode.NOSEARCH -> {
                    viewModel.noSearch(noContactsWarning, iconNoContactWarning, recyclerView)
                }
                ViewLayoutMode.ONSEARCHDEFAULT -> {
                    viewModel.onSearchDefault(recyclerViewAdapter, searchBar, searchIcon, searchViewQuery)
                    title = null
                }
                ViewLayoutMode.ONSEARCHMODEEXIT -> {
                    viewModel.onSearchModeExit(recyclerViewAdapter, searchIcon, searchBar, noContactsWarning, iconNoContactWarning, recyclerView)
                    title = getString(R.string.app_name)
                    UIUtil.hideKeyboard(this@MainActivity, searchViewQuery)
                }
                ViewLayoutMode.DELETEMODEON -> {
                    viewModel.deleteModeOn(selectionItems, bottomBar, bottomBarTitle, selectAll, allSelected)
                }
                ViewLayoutMode.DELETEMODEOFF -> {
                    viewModel.deleteModeOff(recyclerViewAdapter, selectionItems, bottomBar, bottomBarTitle, iconNoContactWarning, noContactsWarning)
                }
            }
        }
    }

    override fun onItemClick(position: Int, action: Int) {
        itemViewPosition = position
        when(action) {
            1 -> checkPermissionAndSendIntent(Manifest.permission.CALL_PHONE, REQUEST_PHONE_CALL)
            2 -> checkPermissionAndSendIntent(Manifest.permission.SEND_SMS, REQUEST_SEND_SMS)
            3 -> setupViewModeBackground(ViewLayoutMode.DELETEMODEON)
        }
    }

    override fun onLongPress(view: View, contact: Contacts, position: Int) {
        setupViewModeBackground(ViewLayoutMode.DELETEMODEON)
    }

    private fun showCancelDialog() {
        val dialog = ConfirmationDialog.newInstance(R.string.remove_items_dialog_title, R.string.remove_items_dialog_message, R.string.button_delete, R.string.button_cancel)
        dialog.show(supportFragmentManager, ConfirmationDialog.TAG)
    }

    override fun onPositiveButtonClicked() {
        viewModel.deleteListItem(recyclerViewAdapter)
        setupViewModeBackground(ViewLayoutMode.DELETEMODEOFF)
        setupViewModeBackground(ViewLayoutMode.ONSEARCHMODEEXIT)
    }

    override fun onNegativeButtonClicked() {}

    override fun onNeutralButtonClicked() {}
}

