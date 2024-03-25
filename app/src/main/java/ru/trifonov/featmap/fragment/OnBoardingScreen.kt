package ru.trifonov.featmap.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.size
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import ru.trifonov.featmap.R
import ru.trifonov.featmap.adapter.OnBoardingPagerAdapter


class OnBoardingScreen : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewPager: ViewPager
    private lateinit var nextBtn: Button
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.onboarding_screen_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextBtn = view.findViewById(R.id.next_btn)
        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())


        val fragmentList: List<Int> = listOf(R.layout.onboarding_first, R.layout.onboarding_second, R.layout.onboarding_third)
        viewPager.adapter = OnBoardingPagerAdapter(requireContext(), fragmentList)
        tabLayout.setupWithViewPager(viewPager,true)
        nextBtn.setOnClickListener {
            if (viewPager.currentItem < viewPager.size - 1) viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            else{
                sharedPreferences.edit().putBoolean("skipOnboarding", true).apply()
                findNavController().navigate(R.id.action_onboarding_to_auth)
            }
        }
    }
}