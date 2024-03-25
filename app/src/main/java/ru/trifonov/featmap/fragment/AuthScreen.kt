package ru.trifonov.featmap.fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import ru.trifonov.featmap.R
import ru.trifonov.featmap.adapter.AuthPagerAdapter


class AuthScreen : Fragment() {
    private lateinit var viewPager: ViewPager
    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.auth_screen_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        auth.setLanguageCode("ru")

        viewPager = view.findViewById(R.id.view_pager)
        val fragments = mutableListOf(R.layout.auth_send_phone_number)
        viewPager.adapter = AuthPagerAdapter(requireActivity(), this, fragments, findNavController())
    }

    fun loginByCredential(credential: PhoneAuthCredential, code: TextView){
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = task.result?.user
                    user!!.uid
                    println("User $user")
                    findNavController().navigate(R.id.action_auth_to_map)

                } else {
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        code.setText("Ошибка")
                        code.visibility = View.VISIBLE
                    }
                }
            }
    }
}