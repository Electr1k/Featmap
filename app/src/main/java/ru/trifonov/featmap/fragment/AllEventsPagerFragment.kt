package ru.trifonov.featmap.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import ru.trifonov.featmap.R
import ru.trifonov.featmap.adapter.EventsAdapter
import ru.trifonov.featmap.dto.Event


class AllEventsPagerFragment : Fragment() {
    private lateinit var eventsRV: RecyclerView
    private lateinit var adapter: EventsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.all_events, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventsRV = view.findViewById(R.id.eventsRV)
        adapter = EventsAdapter(listOf(Event(1, images = listOf(""), title = "Title"))){
            val bundle = Bundle()
            bundle.putInt("id", it.id!!)
            findNavController().navigate(R.id.action_events_to_detailed_event, bundle)
        }
        eventsRV.adapter = adapter
        getEvents()
    }

    private fun getEvents(){
        FirebaseDatabase.getInstance().getReference("events").get().addOnCompleteListener {
            if (it.result.exists()) {
                val list = it.result.getValue(object : GenericTypeIndicator<List<Event>>() {})!!
                adapter.updateList(list)
            }
        }
    }
}