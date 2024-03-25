package ru.trifonov.featmap.adapter

import android.content.Context
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
import ru.trifonov.featmap.R


class AuthPagerAdapter(
    private val context: Context,
    private val fragments: List<Int>,
    private val navController: NavController
) : PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
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
                        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(number.windowToken, 0)
                        numberMessage.visibility = View.GONE
                        verifyGroup.visibility = View.VISIBLE
                        code.requestFocus()
                    }
                }

                sendCode.setOnClickListener {
                    if (code.text.length != 6){
                        codeMessage.text = "Слишком короткий код"
                        codeMessage.visibility = View.VISIBLE
                    }
                    else{
                        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(code.windowToken, 0)
                        codeMessage.visibility = View.GONE
                        println("Success")
                        navController.navigate(R.id.action_auth_to_map)
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

