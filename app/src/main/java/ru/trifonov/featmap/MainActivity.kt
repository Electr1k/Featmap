package ru.trifonov.featmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import ru.trifonov.featmap.databinding.ActivityMainBinding
import ru.trifonov.featmap.dto.User


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mNavController: NavController
    lateinit var currentUser: User
    lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey("b16667de-040a-4f36-b577-a3e1c221b563")
        MapKitFactory.initialize(this)
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mNavController = findNavController(R.id.nav_host_fragment_activity_bottom_navigation)
        mapView = findViewById(R.id.mapview)
        mapView.map.move(
            CameraPosition(
            Point(47.208735,38.936699),
            /* zoom = */ 13.0f,
            /* azimuth = */ 0.0f,
            /* tilt = */ 30.0f
        )
        )
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