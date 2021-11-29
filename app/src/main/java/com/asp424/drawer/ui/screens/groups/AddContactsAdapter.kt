package com.asp424.drawer.ui.screens.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asp424.drawer.R
import com.asp424.drawer.databinding.AddContactsItemBinding
import com.asp424.drawer.models.CommonModel
import com.asp424.drawer.utilites.TYPE_CHAT
import com.asp424.drawer.utilites.asDate
import com.asp424.drawer.utilites.asTime
import com.asp424.drawer.utilites.downloadAndSetImage
import de.hdodenhof.circleimageview.CircleImageView

class AddContactsAdapter: RecyclerView.Adapter<AddContactsAdapter.AddContactsHolder>() {
    private var listItems = mutableListOf<CommonModel>()
    class AddContactsHolder(view: View): RecyclerView.ViewHolder(view){
        private var bindingContactItem = AddContactsItemBinding.bind(view)
        val itemName: TextView = bindingContactItem.addContactsItemName
        val itemLastMessage: TextView = bindingContactItem.addContactsLastMessage
        val itemPhoto: CircleImageView = bindingContactItem.addContactsItemPhoto
        val itemChoose: CircleImageView = bindingContactItem.addContactsItemChoose
        val mTimestamp: TextView = bindingContactItem.addContactsItemStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_contacts_item,
            parent,
            false)

        val  holder = AddContactsHolder(view)
        holder.itemView.setOnClickListener{
           if (listItems[holder.adapterPosition].choose){
               holder.itemChoose.visibility = View.INVISIBLE
               listItems[holder.adapterPosition].choose = false
               AddContactsFragment.listContacts.remove(listItems[holder.adapterPosition])
           }else{
               holder.itemChoose.visibility = View.VISIBLE
               listItems[holder.adapterPosition].choose = true
AddContactsFragment.listContacts.add(listItems[holder.adapterPosition])
           }
        }
        return holder
    }

    override fun onBindViewHolder(holder: AddContactsHolder, position: Int) {
holder.itemName.text = listItems[position].fullname
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)

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

    }
fun updateListItems(item:CommonModel){
    if (!listItems.contains(item) && item.type == TYPE_CHAT){
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}
    override fun getItemCount(): Int = listItems.size
}