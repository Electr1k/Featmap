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
import ru.trifonov.featmap.MainActivity
import ru.trifonov.featmap.R
import ru.trifonov.featmap.adapter.EventsAdapter
import ru.trifonov.featmap.dto.Event


class MyEventsPagerFragment : Fragment() {
    private lateinit var eventsRV: RecyclerView
    private lateinit var adapter: EventsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_events, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventsRV = view.findViewById(R.id.eventsRV)
        adapter = EventsAdapter(listOf()){
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
                val events = it.result.getValue(object : GenericTypeIndicator<List<Event>>() {})!!
                val myEvents =
                    events.filter {event->
                        (requireActivity() as MainActivity).currentUser.uid in event.users
                }
                adapter.updateList(myEvents)
            }
        }

    }
}