package ru.trifonov.featmap.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.Map
import com.yandex.runtime.ui_view.ViewProvider
import ru.trifonov.featmap.MainActivity
import ru.trifonov.featmap.R
import ru.trifonov.featmap.dto.Event


class MapScreen : Fragment() {
    private lateinit var baseActivity: MainActivity
    private lateinit var map: Map
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_screen_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity = requireActivity() as MainActivity

        map = baseActivity.mapView.map


        getEvents()
    }

    private fun getEvents(){
        FirebaseDatabase.getInstance().getReference("events").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val eventsList = dataSnapshot.getValue(object : GenericTypeIndicator<List<Event>>() {})
                    eventsList?.forEach {
                        val point = map.mapObjects.addPlacemark(Point(it.latitude!!, it.longitude!!))
                        val view = layoutInflater.inflate(R.layout.point_event_icon, null)
                        val card = view.findViewById<CardView>(R.id.card)
                        val imageView = view.findViewById<ImageView>(R.id.icon)
                        imageView.load(it.images!![0])

                        point.setView(ViewProvider(card))

                        point.addTapListener{ _, _ ->
                            val bundle = Bundle()
                            bundle.putInt("id", it.id!!)
                            val navController = findNavController()
                            while(navController.currentDestination?.id != R.id.map){
                                navController.popBackStack()
                            }
                            navController.navigate(R.id.action_map_to_detailed_event, bundle)
                            true
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Ошибка при чтении данных: ${databaseError.message}")
            }
        })

    }

}