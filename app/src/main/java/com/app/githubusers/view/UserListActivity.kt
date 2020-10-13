package com.app.githubusers.view

import android.R.color
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.app.githubusers.R
import com.app.githubusers.adapter.UserListAdapter
import com.app.githubusers.viewModel.UserListActivityViewModel

class UserListActivity : AppCompatActivity() {
    private lateinit var viewModel : UserListActivityViewModel
    private lateinit var adapter : UserListAdapter

    lateinit var recycleView: RecyclerView
    lateinit var editTextSearch: EditText
    lateinit var buttonSearch: ImageView
    lateinit var buttonEnableSearch: Button
    lateinit var linearLayoutSearch: LinearLayout

    private lateinit var networkStateReceiver: BroadcastReceiver
    private val broadcastFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generateViews()
        initializeViews()
        monitorNetwork()
    }

    private fun generateViews() {
        val linearLayoutRoot = LinearLayout(baseContext)
        val layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        linearLayoutRoot.layoutParams = layoutParams
        linearLayoutRoot.gravity = Gravity.CENTER_HORIZONTAL
        linearLayoutRoot.orientation = LinearLayout.VERTICAL

        linearLayoutSearch = LinearLayout(baseContext)
        val layoutParamsMain = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        linearLayoutSearch.layoutParams = layoutParamsMain
        linearLayoutSearch.orientation = LinearLayout.HORIZONTAL

        val buttonStyle = android.R.style.Widget_Material_Button_Colored
        buttonEnableSearch = Button(ContextThemeWrapper(this, buttonStyle), null, buttonStyle)
        buttonEnableSearch.text = getString(R.string.button_enable_search)
        buttonEnableSearch.setOnClickListener {
            viewModel.setIsSearching(true)
        }

        val padding = resources.getDimensionPixelSize(R.dimen.padding_small)

        val buttonBack = ImageView(baseContext)
        buttonBack.setImageResource(R.drawable.arrow_back)
        buttonBack.setPadding(padding, padding, padding, padding)
        ImageViewCompat.setImageTintList(
            buttonBack,
            ColorStateList.valueOf(ContextCompat.getColor(baseContext, color.white))
        )
        buttonBack.setOnClickListener {
            viewModel.setIsSearching(false)
        }

        editTextSearch = EditText(this)
        val layoutParamsEdit = LinearLayout.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT,
            1f
        )
        editTextSearch.layoutParams = layoutParamsEdit
        editTextSearch.hint = getString(R.string.search_hint)
        editTextSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
        editTextSearch.setSingleLine()
        editTextSearch.setPadding(padding, padding, padding, padding)
        editTextSearch.setTextColor(ContextCompat.getColor(this, color.white))
        editTextSearch.setHintTextColor(ContextCompat.getColor(this, color.white))
        editTextSearch.backgroundTintList = ColorStateList.valueOf(Color.WHITE)

        buttonSearch = ImageView(baseContext)
        buttonSearch.setPadding(padding, padding, padding, padding)
        buttonSearch.setImageResource(android.R.drawable.ic_menu_search)
        ImageViewCompat.setImageTintList(
            buttonSearch,
            ColorStateList.valueOf(ContextCompat.getColor(baseContext, color.white))
        )

        linearLayoutSearch.setBackgroundColor(
            ContextCompat.getColor(
                baseContext,
                R.color.colorPrimaryDark
            )
        )
        linearLayoutSearch.setPadding(padding * 2, padding, padding * 2, padding)
        linearLayoutSearch.gravity = Gravity.CENTER_VERTICAL

        recycleView = RecyclerView(baseContext)
        val recycleViewParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )
        recycleView.layoutParams = recycleViewParams

        linearLayoutSearch.addView(buttonBack)
        linearLayoutSearch.addView(editTextSearch)
        linearLayoutSearch.addView(buttonSearch)
        linearLayoutRoot.addView(buttonEnableSearch)
        linearLayoutRoot.addView(linearLayoutSearch)
        linearLayoutRoot.addView(recycleView)
        setContentView(linearLayoutRoot)
    }

    private fun initializeViews() {
        viewModel = ViewModelProvider(this).get(UserListActivityViewModel::class.java)

        viewModel.userList.observe(this, Observer {
            adapter.setUserList(it)
        })

        viewModel.noteList.observe(this, Observer {
            adapter.setNotesList(it)
            adapter.notifyDataSetChanged()
        })

        viewModel.showProgress.observe(this, Observer {
            if (it) {
                adapter.showLoading()
            } else {
                adapter.hideLoading()
            }
        })

        viewModel.isSearching.observe(this, Observer {
            if(it) {
                linearLayoutSearch.visibility = View.VISIBLE
                buttonEnableSearch.visibility = View.GONE
                editTextSearch.requestFocus()

                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editTextSearch, InputMethodManager.SHOW_IMPLICIT)
            } else {
                linearLayoutSearch.visibility = View.GONE
                buttonEnableSearch.visibility = View.VISIBLE
            }
        })

        buttonSearch.setOnClickListener {
            viewModel.searchUsers(editTextSearch.text.toString())
        }

        editTextSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchUsers(editTextSearch.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        adapter = UserListAdapter(this)
        adapter.setInvertedAvatarColorPosition(4)
        val layoutManager = LinearLayoutManager(this)
        recycleView.adapter = adapter
        recycleView.layoutManager = layoutManager

        viewModel.searchTerm.observe(this, Observer {
            editTextSearch.setText(it)
            viewModel.searchUsers(it)
        })
        viewModel.init()

        var isScrolling = false

        recycleView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentItems = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val scrollOutItems = layoutManager.findFirstVisibleItemPosition()

                if (isScrolling && (currentItems + scrollOutItems) >= totalItemCount) {
                    isScrolling = false
                    viewModel.loadUsers()
                }
            }
        })
    }

    override fun onResume() {
        viewModel.loadNotes()
        monitorNetwork()
        super.onResume()
    }

    override fun onBackPressed() {
        if (viewModel.isSearching.value!!) {
            viewModel.setIsSearching(false)
        } else {
            finish()
        }
    }

    private fun monitorNetwork() {
        if (!::networkStateReceiver.isInitialized) {
            networkStateReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent?) {
                    viewModel.loadUsers()
                }
            }
        }

        registerReceiver(networkStateReceiver, broadcastFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkStateReceiver)
    }
}
