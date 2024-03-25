package ru.trifonov.featmap.adapter

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import androidx.viewpager.widget.PagerAdapter
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import ru.trifonov.featmap.R
import ru.trifonov.featmap.fragment.AuthScreen
import java.util.concurrent.TimeUnit


class AuthPagerAdapter(
    private val activity: Activity,
    private val baseFragment: AuthScreen,
    private val fragments: List<Int>,
    private val navController: NavController
) : PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(fragments[position], container, false)
        when (position){
            0 -> {
                val sendNumber = view.findViewById<CardView>(R.id.send)
                val number = view.findViewById<EditText>(R.id.input_number)
                val numberMessage = view.findViewById<TextView>(R.id.message_error_number)
                val verifyGroup = view.findViewById<ConstraintLayout>(R.id.verifyGroup)
                val sendCode = view.findViewById<CardView>(R.id.verify_code)
                val code = view.findViewById<EditText>(R.id.input_code)
                val codeMessage = view.findViewById<TextView>(R.id.message_error_code)

                sendNumber.setOnClickListener {
                    if (number.text.length != 11){
                        numberMessage.text = "Введите номер телефона полностью"
                        numberMessage.visibility = View.VISIBLE
                    }
                    else{
                        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(number.windowToken, 0)
                        numberMessage.visibility = View.GONE
                        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                Log.d(TAG, "onVerificationCompleted:$credential")
                                baseFragment.loginByCredential(credential, codeMessage)
                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                numberMessage.text = "Произошла ошибка"
                                numberMessage.visibility = View.VISIBLE
                            }

                            override fun onCodeSent(
                                verificationId: String,
                                token: PhoneAuthProvider.ForceResendingToken,
                            ) {
                                Log.d(TAG, "onCodeSent:$verificationId")
                                verifyGroup.visibility = View.VISIBLE
                                code.requestFocus()

                                baseFragment.storedVerificationId = verificationId
                                baseFragment.resendToken = token
                            }
                        }

                        val options = PhoneAuthOptions.newBuilder(baseFragment.auth)
                            .setPhoneNumber("+${number.text}") // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(activity) // Activity (for callback binding)
                            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                            .build()

                        PhoneAuthProvider.verifyPhoneNumber(options)

                    }
                }

                sendCode.setOnClickListener {
                    if (code.text.length != 6){
                        codeMessage.text = "Слишком короткий код"
                        codeMessage.visibility = View.VISIBLE
                    }
                    else{
                        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(code.windowToken, 0)
                        codeMessage.visibility = View.GONE
                        val credential = PhoneAuthProvider.getCredential(baseFragment.storedVerificationId, code.text.toString())
                        baseFragment.loginByCredential(credential, codeMessage)
                    }
                }
                number.addTextChangedListener{
                    codeMessage.visibility = View.GONE
                    numberMessage.visibility = View.GONE
                }
                code.addTextChangedListener{
                    codeMessage.visibility = View.GONE
                    numberMessage.visibility = View.GONE
                }
            }
        }

        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}

