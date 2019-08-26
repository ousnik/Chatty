package com.example.chatty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chats.*
import kotlinx.android.synthetic.main.chat_item.*
import java.text.DateFormat
import java.util.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.cardview.widget.CardView
import com.example.chatty.ChatsActivity
import com.example.chatty.ChatsActivity.MessageViewHolder
import java.text.SimpleDateFormat


class ChatsActivity : AppCompatActivity() {

    class MessageViewHolder(v: View): RecyclerView.ViewHolder(v) {
        internal var nameTextView: TextView
        internal var timeTextView: TextView
        internal var messageTextView: TextView

        init {
            nameTextView = itemView.findViewById(R.id.name_msg)
            timeTextView = itemView.findViewById(R.id.time_msg)
            messageTextView = itemView.findViewById(R.id.message_msg)
        }
    }

    lateinit var mAuth: FirebaseAuth
    lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<MsgDataClass,MessageViewHolder>
    var dateFormat = SimpleDateFormat(
        "dd-MMM HH:mm a"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        supportActionBar?.setLogo(R.mipmap.chatty_icon_trans)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mAuth= FirebaseAuth.getInstance()
        val mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.stackFromEnd = true

        recyclerView.layoutManager = mLinearLayoutManager

        val mDatabaseReference = FirebaseDatabase.getInstance().reference
        val parser = SnapshotParser {datasnapshot: DataSnapshot ->
            val message: MsgDataClass? = datasnapshot.getValue(MsgDataClass::class.java)
            message?.id = datasnapshot.key
            message ?:MsgDataClass( )
        }

        val messageRef = mDatabaseReference.child("messages")
        val options = FirebaseRecyclerOptions
                                                            .Builder<MsgDataClass>()
                                                                .setQuery(messageRef,parser)
                                                                    .build()
        mFirebaseAdapter = object : FirebaseRecyclerAdapter<MsgDataClass,MessageViewHolder>(options)
        {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
                val  inflater = LayoutInflater.from(parent.context)
                val messageViewHolder = MessageViewHolder(inflater.inflate(R.layout.chat_item,parent,false))

                return messageViewHolder
            }

            override fun onBindViewHolder(viewHolder: MessageViewHolder, position: Int, message: MsgDataClass) {
                viewHolder.nameTextView.text = message.name
                viewHolder.messageTextView.text = message.msg
                viewHolder.timeTextView.text = dateFormat.format(Date(message.time)).toString()
            }
        }

        recyclerView.adapter = mFirebaseAdapter

        send_button.setOnClickListener {
            val message = MsgDataClass(
                msg = msg_editText.text.toString(),
                name = mAuth.currentUser?.displayName!!,
                time = Date().time

            )
            if (message.msg.isNotEmpty()) {
                mDatabaseReference.child("messages").push().setValue(message)
                Toast.makeText(application, "Message Sent!", Toast.LENGTH_SHORT).show()
                msg_editText.setText("")
                recyclerView.scrollToPosition(mFirebaseAdapter.itemCount)
            }
            else
                Toast.makeText(application,"Type a message",Toast.LENGTH_SHORT).show()
        }


    }

    public override fun onStart() {
        super.onStart()
        mFirebaseAdapter.startListening()
    }

    public override fun onPause() {
        super.onPause()
        mFirebaseAdapter.stopListening()
    }

    data class MsgDataClass(var id:String? = null, var name: String, var msg: String, var time:Long)
    {
        constructor():this(null,"Anonymous","", 0L)
        constructor(name: String,msg: String):this(null,name, msg, 0L)
        constructor(name: String,msg: String, time: Long):this(null,name,msg, time)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out_menu -> {
                FirebaseAuth.getInstance().signOut()
                finish()
                startActivity(Intent(this, AuthActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
