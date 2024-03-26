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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import ru.trifonov.featmap.MainActivity
import ru.trifonov.featmap.R
import ru.trifonov.featmap.dto.User


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
        val baseActivity = (requireActivity() as MainActivity)
        Handler(Looper.getMainLooper()).postDelayed({
            val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val uid = sp.getString("uid", "")
            if (!uid.isNullOrEmpty()){
                FirebaseDatabase.getInstance().getReference("User").child(uid).get()
                    .addOnCompleteListener {
                        if (it.result.exists()) {
                            baseActivity.currentUser =
                                it.result.getValue<User>()!!
                            findNavController().navigate(R.id.action_splash_to_map)
                        }
                        else{
                            if (!sp.getBoolean("skipOnboarding", false)) {
                                findNavController().navigate(R.id.action_splash_to_onboarding)
                            } else findNavController().navigate(R.id.action_splash_to_auth)
                        }
                    }
                    .addOnFailureListener {
                        if (!sp.getBoolean("skipOnboarding", false)) {
                            findNavController().navigate(R.id.action_splash_to_onboarding)
                        } else findNavController().navigate(R.id.action_splash_to_auth)
                    }
            }
            else {
                if (!sp.getBoolean("skipOnboarding", false)) {
                    findNavController().navigate(R.id.action_splash_to_onboarding)
                } else findNavController().navigate(R.id.action_splash_to_auth)
            }
        }, 2000)
    }

}