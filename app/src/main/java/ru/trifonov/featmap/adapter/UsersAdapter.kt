package ru.trifonov.featmap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.trifonov.featmap.R
import ru.trifonov.featmap.dto.User

class UsersAdapter  (
    private var usersList: List<User>,
//    private val action: (Dot) -> Unit
): RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent,false)
        return UserViewHolder(view)
    }
    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        holder.title.text = usersList[position].name
//        holder.itemView.setOnClickListener{
//            action(usersList[position])
//        }
    }

    internal fun updateList(newList: List<User>){
        usersList = newList
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val title: TextView
        init {
            title = itemView.findViewById(R.id.name)
        }
    }
}