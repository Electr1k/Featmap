package ru.trifonov.featmap.fragment

import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import ru.trifonov.featmap.MainActivity
import ru.trifonov.featmap.R
import ru.trifonov.featmap.adapter.AuthPagerAdapter
import ru.trifonov.featmap.dto.User
import ru.trifonov.featmap.view.CustomViewPager


class AuthScreen : Fragment() {
    private lateinit var viewPager: CustomViewPager
    lateinit var loading: ConstraintLayout
    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var sharedPreferences: SharedPreferences
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("User")
    lateinit var baseActivity: MainActivity

    val newUser = User()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.auth_screen_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity = requireActivity() as MainActivity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseActivity)
        auth = Firebase.auth
        auth.setLanguageCode("ru")
        viewPager = view.findViewById(R.id.view_pager)
        loading = view.findViewById(R.id.loading)
        val fragments = mutableListOf(R.layout.auth_send_phone_number, R.layout.auth_registration_form)
        viewPager.adapter = AuthPagerAdapter(requireActivity(), this, fragments)
    }

    fun loginByCredential(credential: PhoneAuthCredential, code: TextView){
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    database.child(user!!.uid).get()
                        .addOnCompleteListener {
                            if (it.result.exists()){
                                sharedPreferences.edit().putString("uid", user.uid).apply()
                                baseActivity.currentUser = it.result.getValue<User>()!!
                                findNavController().navigate(R.id.action_auth_to_map)
                            }
                            else{
                                newUser.uid = user.uid
                                viewPager.setCurrentItem(1, true)
                            }
                        }
                        .addOnFailureListener{
                            Toast.makeText(requireContext(), "Произошла ошибка", Toast.LENGTH_SHORT).show()
                        }

                } else {
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        code.setText("Ошибка")
                        code.visibility = View.VISIBLE
                    }
                }
            }
    }

    fun registration(){
        database.child(newUser.uid!!).setValue(newUser)
        sharedPreferences.edit().putString("uid", newUser.uid).apply()
        baseActivity.currentUser = newUser
        findNavController().navigate(R.id.action_auth_to_map)
    }

}