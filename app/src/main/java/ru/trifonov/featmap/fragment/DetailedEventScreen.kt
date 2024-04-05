package ru.trifonov.featmap.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.ui_view.ViewProvider
import org.w3c.dom.Text
import ru.trifonov.featmap.MainActivity
import ru.trifonov.featmap.R
import ru.trifonov.featmap.dto.Event
import ru.trifonov.featmap.dto.User


class DetailedEventScreen : Fragment() {
    private lateinit var bottomSheet: LinearLayout
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var title: TextView
    private lateinit var subtitle: TextView
    private lateinit var date: TextView
    private lateinit var image: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detailed_event_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheet = view.findViewById(R.id.bottom_sheet)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        mBottomSheetBehavior.isHideable = true
        title = view.findViewById(R.id.title)
        subtitle = view.findViewById(R.id.subtitle)
        date = view.findViewById(R.id.date)
        image = view.findViewById(R.id.image)

        mBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN){
                    findNavController().popBackStack()
                }
            }
        })

        getEvent(arguments?.getInt("id") ?: 0)
    }


    private fun getEvent(id: Int){
        FirebaseDatabase.getInstance().getReference("events").child(id.toString()).get()
            .addOnCompleteListener {
                if (it.result.exists()){
                    initializeView(it.result.getValue<Event>()!!)
                }
                else{
                    // TODO: ОБРАБОТКА ОШИБКИ ЗАГРУЗКИ
                }
            }
            .addOnFailureListener{
                Toast.makeText(requireContext(), "Произошла ошибка", Toast.LENGTH_SHORT).show()
            }
    }

    private fun initializeView(event: Event){
        title.text = event.title
        subtitle.text = event.subtitle
        date.text = "${event.startDate} - ${event.endDate}"
        image.load(event.images!![0]){
            listener(
                onStart = { println("Start1") },
                onSuccess = { _, _ -> println("success1") },
                onCancel = {  _ -> println("cancel1") },
                onError = {  _, _ -> println("error1") }
            )
        }
    }
}