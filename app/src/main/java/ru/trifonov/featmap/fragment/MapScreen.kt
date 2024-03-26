package ru.trifonov.featmap.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import ru.trifonov.featmap.MainActivity
import ru.trifonov.featmap.R


class MapScreen : Fragment() {
    private lateinit var mapView: MapView
    private lateinit var baseActivity: MainActivity
    private lateinit var bottomSheet: LinearLayout
    private lateinit var name: TextView
    private lateinit var number: TextView
    private lateinit var uid: TextView
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MapKitFactory.setApiKey("b16667de-040a-4f36-b577-a3e1c221b563")

        MapKitFactory.initialize(requireContext())
        return inflater.inflate(R.layout.map_screen_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity = requireActivity() as MainActivity
        mapView = view.findViewById(R.id.mapview)
        bottomSheet = view.findViewById(R.id.bottom_sheet)
        name = view.findViewById(R.id.name)
        number = view.findViewById(R.id.number)
        uid = view.findViewById(R.id.uid)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        name.setText("${name.text} ${baseActivity.currentUser.name}")
        number.setText("${number.text} ${baseActivity.currentUser.phoneNumber}")
        uid.setText("${uid.text} ${baseActivity.currentUser.uid}")
        mBottomSheetBehavior.skipCollapsed = true
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}