package com.asp424.drawer.ui.screens.main_list

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.asp424.drawer.R
import com.asp424.drawer.databinding.MainListItemBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.TYPE_CHAT
import com.asp424.drawer.utilites.TYPE_GROUP
import com.asp424.drawer.utilites.asTime
import com.asp424.drawer.utilites.downloadAndSetImage
import com.asp424.drawer.viewModel.MainViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainListAdapter(mainViewModel: MainViewModel) :
    RecyclerView.Adapter<MainListAdapter.MainListHolder>() {
    private val mMainViewModel = mainViewModel
    private lateinit var mNavController: NavController
    var listItems = mutableListOf<CommonModel>()

    class MainListHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = MainListItemBinding.bind(view)
        val itemName: TextView = binding.mainListItemName
        val itemLastMessage: TextView = binding.mainListLastMessage
        val itemPhoto: CircleImageView = binding.mainListItemPhoto
        val mCountView: TextView = binding.count
        val mTime: TextView = binding.mainListLastMessageTime
    }

    @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        val holder = MainListHolder(view)
        holder.itemView.setOnClickListener {
            mMainViewModel.contact.value = listItems[holder.adapterPosition]
            mNavController = Navigation.findNavController(holder.itemView)
            when (listItems[holder.adapterPosition].type) {
                TYPE_CHAT -> mNavController.navigate(R.id.action_nav_main_to_nav_single)
                TYPE_GROUP -> mNavController.navigate(R.id.action_nav_main_to_nav_group)
            }
        }
        return holder
    }

    override fun getItemCount(): Int = listItems.size
    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        init(holder, position)
    }

    private fun init(holder: MainListHolder, position: Int) =
        CoroutineScope(Dispatchers.Unconfined).launch {
            holder.itemName.text = listItems[position].fullname
            holder.itemLastMessage.text = listItems[position].lastMessage
            holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)
            if (listItems[position].counter != 0) {
                holder.mCountView.visibility = View.VISIBLE
                holder.mCountView.text = listItems[position].counter.toString()
            } else {
                listItems[position].counter = 0
            }
            if (listItems[position].lastMessageTime != "")
                holder.mTime.text = listItems[position].lastMessageTime.asTime()
            else holder.mTime.text = ""
        }

    @SuppressLint("NotifyDataSetChanged")
    fun updateListItems(item: CommonModel) = CoroutineScope(Dispatchers.Unconfined).launch {
        notifyDataSetChanged()
        when {
            item.counter != 0 && listItems.contains(item) -> {
                listItems.remove(item)
                listItems.add(0, item)
                notifyDataSetChanged()
            }
            item.counter != 0 -> {
                listItems.remove(item)
                listItems.add(0, item)
            }
            !listItems.contains(item) -> {
                listItems.add(item)
                notifyItemInserted(listItems.size)
                notifyDataSetChanged()
            }
        }
    }
}









