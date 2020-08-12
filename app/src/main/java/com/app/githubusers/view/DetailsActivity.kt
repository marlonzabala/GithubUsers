package com.app.githubusers.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.githubusers.R
import com.app.githubusers.viewModel.DetailsActivityViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {
    private lateinit var viewModel : DetailsActivityViewModel
    private lateinit var networkStateReceiver: BroadcastReceiver
    private val broadcastFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        initializeViews()
    }

    private fun initializeViews() {
        viewModel = ViewModelProvider(this).get(DetailsActivityViewModel::class.java)
        viewModel.user.observe(this, Observer {
            textViewName.text = it.name
            textViewCompany.text = it.company
            textViewBlog.text = it.blog
            textViewFollowersCount.text = it.followers.toString()
            textViewFollowingCount.text = it.following.toString()

            Glide.with(this)
                .load(it.avatar_url)
                .into(imageViewAvatar)
        })

        viewModel.note.observe(this, Observer {
            editTextNote.setText(it.note)
        })

        viewModel.showProgress.observe(this, Observer {
            if(it){
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        buttonSave.setOnClickListener {
            Toast.makeText(applicationContext, getString(R.string.note_saved), Toast.LENGTH_SHORT).show()
            viewModel.saveNote(editTextNote.text.toString())
        }

        val username = intent.getStringExtra("LOGIN")
        viewModel.getUser(username)

        monitorNetwork()
        registerReceiver(networkStateReceiver, broadcastFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkStateReceiver)
    }

    override fun onResume() {
        super.onResume()
        monitorNetwork()
    }

    private fun monitorNetwork() {
        if(!::networkStateReceiver.isInitialized) {
            val username = intent.getStringExtra("LOGIN")
            networkStateReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent?) {
                    if(viewModel.user.value != null)
                        viewModel.getUser(username)
                }
            }
        }

        registerReceiver(networkStateReceiver, broadcastFilter)
    }
}