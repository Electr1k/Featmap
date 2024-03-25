package ru.trifonov.featmap.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.trifonov.featmap.R


class SplashScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("skipOnboarding", false)){
                findNavController().navigate(R.id.action_splash_to_onboarding)
            }
            else findNavController().navigate(R.id.action_splash_to_auth)
        }, 2000)
        return inflater.inflate(R.layout.splash_screen_fragment, container, false)
    }

}