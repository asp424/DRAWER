package com.asp424.drawer.ui.screens.intents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asp424.drawer.R
import com.asp424.drawer.databinding.AddContactsItemBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.asDate
import com.asp424.drawer.utilites.asTime
import com.asp424.drawer.utilites.downloadAndSetImage
import de.hdodenhof.circleimageview.CircleImageView

class IntentAdapter :
    RecyclerView.Adapter<IntentAdapter.AddContactsIntentTextHolder>() {
    private var listItems = mutableListOf<CommonModel>()


    class AddContactsIntentTextHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = AddContactsItemBinding.bind(view)
        val itemName: TextView = binding.addContactsItemName
        val itemLastMessage: TextView = binding.addContactsLastMessage
        val itemPhoto: CircleImageView = binding.addContactsItemPhoto
        val itemChoose: CircleImageView = binding.addContactsItemChoose
        val mTimestamp: TextView = binding.addContactsItemStatus

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsIntentTextHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_contacts_item,
            parent,
            false)

        val holder = AddContactsIntentTextHolder(view)
        holder.itemView.setOnClickListener {
            if (listItems[holder.adapterPosition].choose) {
                holder.itemChoose.visibility = View.INVISIBLE
                listItems[holder.adapterPosition].choose = false
                IntentFragment.listContacts.remove(listItems[holder.adapterPosition])
            } else {
                holder.itemChoose.visibility = View.VISIBLE
                listItems[holder.adapterPosition].choose = true
                IntentFragment.listContacts.add(listItems[holder.adapterPosition])
            }

        }
        return holder
    }

    override fun onBindViewHolder(holder: AddContactsIntentTextHolder, position: Int) {
        holder.itemName.text = listItems[position].fullname
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)
        if (listItems[position].type == "chat") {
            if (listItems[position].timeStamp.toString() == "в сети" && listItems[position].timeStamp.toString()
                    .isNotEmpty()
            ) {
                holder.mTimestamp.text = listItems[position].timeStamp.toString()
            } else {
                if (listItems[position].timeStamp.toString().isNotEmpty()) {
                    val p = listItems[position].timeStamp.toString().asTime()
                    val o = listItems[position].timeStamp.toString().asDate()
                    val i = "был(а) $o в $p"
                    holder.mTimestamp.text = i
                }
            }
        } else {
            holder.mTimestamp.visibility = View.GONE
        }
    }

    fun updateListItems(item: CommonModel) {
        if (!listItems.contains(item)){
            listItems.add(item)
            notifyItemInserted(listItems.size)
        }
    }

    override fun getItemCount(): Int = listItems.size


}