package ru.trifonov.featmap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.trifonov.featmap.R
import ru.trifonov.featmap.dto.Event

class EventsAdapter  (
    private var eventsList: List<Event>,
    private val action: (Event) -> Unit
): RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent,false)
        return EventViewHolder(view)
    }
    override fun getItemCount(): Int {
        return eventsList.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.title.text = eventsList[position].title
        holder.icon.load(eventsList[position].images[0])
        holder.itemView.setOnClickListener{
            action(eventsList[position])
        }
    }

    internal fun updateList(newList: List<Event>){
        eventsList = newList
        notifyDataSetChanged()
    }

    class EventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val title: TextView
        val icon: ImageView
        init {
            title = itemView.findViewById(R.id.title)
            icon = itemView.findViewById(R.id.icon)
        }
    }
}