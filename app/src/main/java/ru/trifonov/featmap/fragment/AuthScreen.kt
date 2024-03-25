package ru.trifonov.featmap.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import ru.trifonov.featmap.R
import ru.trifonov.featmap.adapter.AuthPagerAdapter


class AuthScreen : Fragment() {
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.auth_screen_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.view_pager)
        val fragments = mutableListOf(R.layout.auth_send_phone_number)
        viewPager.adapter = AuthPagerAdapter(requireContext(), fragments, findNavController())
    }
}