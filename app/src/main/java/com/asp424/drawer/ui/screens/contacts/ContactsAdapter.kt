package com.asp424.drawer.ui.screens.main_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asp424.drawer.R
import com.asp424.drawer.databinding.ContactItemBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.*
import com.asp424.drawer.viewModel.MainViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsAdapter(ViewModel: MainViewModel) : RecyclerView.Adapter<ContactsAdapter.ContactsHolder>() {
    private val mSingleChatViewModel = ViewModel
    private var listItems = mutableListOf<CommonModel>()
    class ContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding = ContactItemBinding.bind(view)
        val name: TextView = binding.toolbarContChatFullname
        val status: TextView = binding.toolbarContChatStatus
        val photo: CircleImageView = binding.contactPhoto
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        val holder = ContactsHolder(view)
        return holder
    }

    override fun getItemCount(): Int = listItems.size
    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        init(holder, position)
    }

    fun init(holder: ContactsHolder, position: Int) =
        CoroutineScope(Dispatchers.Unconfined).launch {
            holder.name.text = listItems[position].fullname
            holder.photo.downloadAndSetImage(listItems[position].photoUrl)
            if (listItems[position].timeExitStamp == "в сети") holder.status.text = "в сети"
            else {
                val y = listItems[position].timeExitStamp.toString().asTime()
                val yy = listItems[position].timeExitStamp.toString().asDate()
                val o = "был(а) $yy в $y"
                holder.status.text = o
            }
            holder.itemView.setOnClickListener {
                mSingleChatViewModel.contact.value = listItems[holder.adapterPosition]
                APP_ACTIVITY.navController.navigate(R.id.action_nav_contacts_to_nav_single)
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    fun updateListItems(item: CommonModel) = CoroutineScope(Dispatchers.Unconfined).launch {
        if (listItems.contains(item)){
            listItems.remove(item)
            listItems.add(0, item)
        }
        else listItems.add(item)

        if (item.timeExitStamp == "в сети") {
            listItems.remove(item)
            listItems.add(0, item)
        }
        notifyDataSetChanged()
    }
}









