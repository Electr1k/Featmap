package ru.trifonov.featmap.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import ru.trifonov.featmap.R


class SplashScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        return inflater.inflate(R.layout.splash_screen_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topImage: ImageView = view.findViewById(R.id.topImage)
        val leftImage: ImageView = view.findViewById(R.id.leftImage)
        val rightImage: ImageView = view.findViewById(R.id.rightImage)
        val bottomText: TextView = view.findViewById(R.id.bottomText)
        val slideAnimationTop = AnimationUtils.loadAnimation(requireContext(), R.anim.animation_splash_screen_top)
        val slideAnimationLeft = AnimationUtils.loadAnimation(requireContext(), R.anim.animation_splash_screen_left)
        val slideAnimationRight = AnimationUtils.loadAnimation(requireContext(), R.anim.animation_splash_screen_right)
        val slideAnimationBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.animation_splash_screen_bottom)

        topImage.startAnimation(slideAnimationTop)
        leftImage.startAnimation(slideAnimationLeft)
        rightImage.startAnimation(slideAnimationRight)
        bottomText.startAnimation(slideAnimationBottom)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("skipOnboarding", false)){
                findNavController().navigate(R.id.action_splash_to_onboarding)
            }
            else findNavController().navigate(R.id.action_splash_to_auth)
        }, 2000)
    }

}