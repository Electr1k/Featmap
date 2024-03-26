package ru.trifonov.featmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import ru.trifonov.featmap.databinding.ActivityMainBinding
import ru.trifonov.featmap.dto.User


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mNavController: NavController
    lateinit var currentUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mNavController = findNavController(R.id.nav_host_fragment_activity_bottom_navigation)
    }
}