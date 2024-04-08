package ru.trifonov.featmap.fragment

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import coil.load
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.yandex.mapkit.geometry.Point
import com.yandex.runtime.ui_view.ViewProvider
import ru.trifonov.featmap.MainActivity
import ru.trifonov.featmap.R
import ru.trifonov.featmap.adapter.EventsPagerAdapter
import ru.trifonov.featmap.dto.Event
import kotlin.properties.Delegates


class EventsScreen : Fragment() {
    private lateinit var baseActivity: MainActivity
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.events_screen_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity = (requireContext() as MainActivity)
        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)

        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0

        val layoutParams = tabLayout.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, statusBarHeight, 0, 0)
        tabLayout.layoutParams = layoutParams


        println("Set adapter for view pager")
        val adapter = EventsPagerAdapter(baseActivity.supportFragmentManager)
        adapter.addFragment(AllEventsPagerFragment(), "Все мероприятия")
        adapter.addFragment(MyEventsPagerFragment(), "Я иду")
        viewPager.adapter = adapter

        tabLayout.setupWithViewPager(viewPager)
    }
}