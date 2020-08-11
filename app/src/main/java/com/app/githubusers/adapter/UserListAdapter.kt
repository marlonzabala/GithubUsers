package com.app.githubusers.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.githubusers.R
import com.app.githubusers.extensions.loadInvertedColor
import com.app.githubusers.extensions.loadNormalColor
import com.app.githubusers.network.model.Note
import com.app.githubusers.network.model.User
import com.app.githubusers.view.DetailsActivity

const val VIEW_TYPE_USER = 1
const val VIEW_TYPE_LOADING = 2

class UserListAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var userList: List<User> = ArrayList()
    private var noteList: List<Note> = ArrayList()
    var invertedColorLocation = 0
    var showingLoading = false

    var imageViewAvatarID = View.generateViewId()
    var noteImageID = View.generateViewId()
    var usernameID = View.generateViewId()
    var detailsID = View.generateViewId()

    fun setUserList(list: List<User>){
        this.userList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if(showingLoading)
            return userList.size + 1
        else
            return userList.size
    }

    fun showLoading() {
        showingLoading = true
        notifyDataSetChanged()
    }

    fun hideLoading() {
        showingLoading = false
        notifyDataSetChanged()
    }

    fun setInvertedAvatarColorPosition(location: Int) {
        invertedColorLocation = location
    }

    private inner class ViewHolderUser internal constructor(v: View) : RecyclerView.ViewHolder(v) {
        val avatar = v.findViewById<ImageView>(imageViewAvatarID)
        val username = v.findViewById<TextView>(usernameID)
        val detail = v.findViewById<TextView>(detailsID)
        val note = v.findViewById<ImageView>(noteImageID)
        val rootView = v

        internal fun bind(position: Int) {
            username.text = userList[position].login
            rootView.setOnClickListener {
                if(userList[position].id.toString().isNotEmpty()) {
                    val intent = Intent(context, DetailsActivity::class.java)
                    intent.putExtra("LOGIN", userList[position].login)
                    context.startActivity(intent)
                }
            }

            var detailContent = "Id: ${userList[position].id}"

            if(getUserHasNotes(userList[position].id)) {
                note.visibility = View.VISIBLE
                detailContent += "\nNote: ${getUserNotes(userList[position].id)}"
            } else {
                note.visibility = View.GONE
            }

            detail.text = detailContent

            if((position + 1) % invertedColorLocation == 0)
                avatar.loadInvertedColor(userList[position].avatar_url)
            else
                avatar.loadNormalColor(userList[position].avatar_url)
        }
    }

    private inner class ViewHolderLoading internal constructor(v: View) : RecyclerView.ViewHolder(v) {
        internal fun bind() {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            // create views programmatically
            val linearLayoutUser = LinearLayout(parent.context)
            val layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            linearLayoutUser.layoutParams = layoutParams
            linearLayoutUser.gravity = Gravity.CENTER_VERTICAL
            linearLayoutUser.orientation = LinearLayout.HORIZONTAL
            linearLayoutUser.weightSum = 1F
            linearLayoutUser.setPadding(30,30,30,30)

            val imageViewAvatar = ImageView(parent.context)
            val params = LinearLayout.LayoutParams(200, 200)
            imageViewAvatar.layoutParams = params
            imageViewAvatar.id = imageViewAvatarID

            val linearLayoutDetails = LinearLayout(parent.context)
            val layoutParams4 = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
            linearLayoutDetails.layoutParams = layoutParams4
            linearLayoutDetails.gravity = Gravity.CENTER_HORIZONTAL
            linearLayoutDetails.orientation = LinearLayout.VERTICAL

            val padding = 20
            val contextTheme = ContextThemeWrapper(parent.context, R.style.AppTheme)
            val textViewUsername = TextView(contextTheme)
            textViewUsername.id = usernameID
            textViewUsername.setPadding(padding,0,0,0)
            textViewUsername.apply {
                setTypeface(typeface, Typeface.BOLD)
            }

            val textViewDetails = TextView(contextTheme)
            textViewDetails.setPadding(padding,0,0,0)
            textViewDetails.id = detailsID

            linearLayoutDetails.addView(textViewUsername)
            linearLayoutDetails.addView(textViewDetails)

            val imageViewNote = ImageView(parent.context)
            val paramsNote = LinearLayout.LayoutParams(200, 200)
            imageViewNote.layoutParams = paramsNote
            imageViewNote.setImageResource(R.drawable.note)
            imageViewNote.id = noteImageID

            linearLayoutUser.addView(imageViewAvatar)
            linearLayoutUser.addView(linearLayoutDetails)
            linearLayoutUser.addView(imageViewNote)

            return ViewHolderUser(linearLayoutUser)
        } else {
            val layoutProgress = LinearLayout(parent.context)
            val layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            layoutProgress.layoutParams = layoutParams
            layoutProgress.gravity = Gravity.CENTER_HORIZONTAL
            layoutProgress.addView(ProgressBar(parent.context))

            return ViewHolderLoading(layoutProgress)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (showingLoading && (position + 1 == getItemCount())) {
            (holder as ViewHolderLoading).bind()
        } else {
            (holder as ViewHolderUser).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (showingLoading && (position + 1 == getItemCount())) {
            VIEW_TYPE_LOADING
        } else VIEW_TYPE_USER
    }

    fun getUserHasNotes(id: Int): Boolean {
        for(note in noteList) {
            if(note.id == id)
                return true
        }
        return false
    }

    fun getUserNotes(id: Int): String {
        for(note in noteList) {
            if(note.id == id)
                return note.note
        }
        return ""
    }

    fun setNotesList(noteList: List<Note>) {
        this.noteList = noteList
    }
}