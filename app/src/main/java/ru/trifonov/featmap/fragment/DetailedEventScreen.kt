package ru.trifonov.featmap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import ru.trifonov.featmap.MainActivity
import ru.trifonov.featmap.R
import ru.trifonov.featmap.adapter.ImagePagerAdapter
import ru.trifonov.featmap.adapter.UsersAdapter
import ru.trifonov.featmap.dto.Event
import ru.trifonov.featmap.dto.User
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import kotlin.math.max


class DetailedEventScreen : Fragment() {
    private lateinit var baseActivity: MainActivity
    private val eventsDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("events")
    private val usersDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("User")
    private lateinit var currentEvent: Event
    private lateinit var bottomSheet: LinearLayout
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var title: TextView
    private lateinit var subtitle: TextView
    private lateinit var date: TextView
    private lateinit var viewPager: ViewPager
    private var progress = 0f
    private lateinit var linearIndicator: LinearLayout
//    private lateinit var collapseContent: LinearLayout
    private lateinit var usersRecyclerView: RecyclerView
    private var peekHeight = 400
    private lateinit var goToEvent: Button
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var list_events_btn: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detailed_event_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity = requireActivity() as MainActivity
        bottomSheet = view.findViewById(R.id.bottom_sheet)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        mBottomSheetBehavior.isHideable = true
        title = view.findViewById(R.id.title)
        subtitle = view.findViewById(R.id.subtitle)
        date = view.findViewById(R.id.eventDate)
        viewPager = view.findViewById(R.id.imagesPager)
        linearIndicator = view.findViewById(R.id.linearIndicator)
        goToEvent = view.findViewById(R.id.go_event_btn)
        usersRecyclerView = view.findViewById(R.id.users)
        list_events_btn = view.findViewById(R.id.list_events_btn)
        list_events_btn.setOnClickListener{
            findNavController().navigate(R.id.action_detailed_event_to_events)
        }
//        collapseContent = view.findViewById(R.id.collapse_content)

        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mBottomSheetBehavior.isHideable = true
        mBottomSheetBehavior.skipCollapsed = false
        mBottomSheetBehavior.peekHeight = peekHeight

        val density = requireContext().resources.displayMetrics.density
        mBottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN){
                        findNavController().popBackStack()
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    view.findViewById<LinearLayout>(R.id.linearLayout).translationY = max(-1 * 260 * density * (1 - slideOffset), -1 * 260 * density)
                }
            }
        )
        usersAdapter = UsersAdapter(listOf())
        usersRecyclerView.adapter = usersAdapter
        getEvent(arguments?.getInt("id") ?: 0, true)


    }

    private fun getUsers(){
        FirebaseDatabase.getInstance().getReference("User").get().addOnCompleteListener {
            if(it.result.exists()){
                val allUser = it.result.getValue<HashMap<String, User>>()!!.values.toMutableList()
                val userList = mutableListOf(User(name = "Кто идет?"))
                currentEvent.users.forEach{userInEvent ->
                    allUser.map { if (userInEvent == it.uid) userList.add(it) }
                }
                println(userList)
                usersAdapter.updateList(userList as List<User>)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bottomSheet.visibility = View.VISIBLE
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        collapseContent.post {
//                peekHeight = collapseContent.height
//            mBottomSheetBehavior.peekHeight = peekHeight
//        }
    }


    private fun getEvent(id: Int, initPager: Boolean = false){
        eventsDatabase.child(id.toString()).get()
            .addOnCompleteListener {
                if (it.result.exists()){
                    currentEvent = it.result.getValue<Event>()!!
                    getUsers()
                    initializeView(initPager)
                }
                else{
                    // TODO: ОБРАБОТКА ОШИБКИ ЗАГРУЗКИ
                }
            }
            .addOnFailureListener{
                Toast.makeText(requireContext(), "Произошла ошибка", Toast.LENGTH_SHORT).show()
            }
    }

    private fun initializeView(initPager: Boolean ){
        title.text = currentEvent.title
        subtitle.text = currentEvent.subtitle
        goToEvent.text = if (baseActivity.currentUser.uid in currentEvent.users) getString(R.string.not_go) else getString(R.string.go)
        usersRecyclerView.visibility = if (baseActivity.currentUser.uid in currentEvent.users) View.VISIBLE else View.GONE
        goToEvent.setOnClickListener {

            if (baseActivity.currentUser.uid in currentEvent.users) {
                currentEvent.users.remove(baseActivity.currentUser.uid)
                eventsDatabase.child(currentEvent.id.toString()).child("users").setValue(currentEvent.users)
                baseActivity.currentUser.eventsList?.remove(currentEvent.id)
                usersDatabase.child(baseActivity.currentUser.uid.toString()).setValue(baseActivity.currentUser)
                getEvent(currentEvent.id!!)
            }
            else{
                eventsDatabase.child(currentEvent.id.toString()).child("users").child(currentEvent.users.size.toString()).setValue(baseActivity.currentUser.uid)
                if (baseActivity.currentUser.eventsList == null) baseActivity.currentUser.eventsList = ArrayList()
                baseActivity.currentUser.eventsList!!.add(currentEvent.id!!)
                usersDatabase.child(baseActivity.currentUser.uid.toString()).setValue(baseActivity.currentUser)
                getEvent(currentEvent.id!!)
            }
        }
        val dateStart = convertStringToDateTime(currentEvent.startDate!!)
        val dateEnd= convertStringToDateTime(currentEvent.endDate!!)
        val monthList = listOf("Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря", )
        date.text = "${dateStart.hours}:${dateStart.minutes} ${dateStart.date} ${monthList[dateStart.day]} — ${dateEnd.hours}:${dateEnd.minutes} ${dateEnd.date} ${monthList[dateEnd.day]}"

        if (initPager) initPager(currentEvent)
    }

    private fun convertStringToDateTime(timestampString: String): Date {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return sdf.parse(timestampString)
    }

    private fun initPager(event: Event){

        /**
         * Для создания циклического ViewPager пришлось придумать неочевидную структру
         * Размер списка, который подается в адаптер, на 2 элемента больше исходного:
         * В голову списка копируем последний элемент, а в хвост первый
         * Указатель пейджера устанавливаем на 1 и когда пользователь перелистнет пейджер на 0 элемент
         * мы без анимации передвигаем указатель на N элемент списка (N - количество исходных фото или же n-2, где n - уже изменный список),
         * а если пользователь перелистнет последнее фото исходного списка, он попадет на первое фото(которое копировали) и в этот момент мы
         * сдвигаем указатель на 1 индекс
         * Таким образом мы "подменяем" картинки и появляется возможность смотреть пейджер по кругу
         * Но остается проблема с индикатором, так как количество реальных фото на 2 меньше размера списка в адаптере пришлось местами изголяться
         * https://medium.com/@ali.muzaffar/looping-infinite-viewpager-with-page-indicator-in-android-ce741f25702a
         * */

        /**
         * Исходный список
         */
        val imageList = event.images!!.toMutableList()

        // Копируем первый и последний элемент в хвост и голову соответственно
        imageList.add(0, imageList.takeLast(1)[0])
        imageList.add(imageList.size, imageList[1])

        // Создаем индикаторы
        createLinearIndicator(imageList.size - 2)

        viewPager.currentItem = 1 // Устанавливаем указатель на 0 элемент "исходного" списка

        // Создаем отдельный поток,в котором будет изменять индикаторы и двигать пейджер
        var runProgress =true
        val progressThread = Thread{
            var stopOnException = false
            while(!stopOnException){
                progress = 0f
                while (progress != 100f){
                    if (runProgress) {
                        Thread.sleep(40) // 40* 100 = 4000 ms
                        progress += 1f // 100 iteration
                        try {
                            requireActivity().runOnUiThread {
                                updateLinearIndicator(getIndexByPosition(viewPager.currentItem))
                            }
                        } catch (e: Exception) {
                            stopOnException = true
                            break
                        }
                    }
                }
                try {
                    requireActivity().runOnUiThread {
                        viewPager.setCurrentItem( viewPager.currentItem + 1,true)
                    }
                }
                catch (e: Exception){
                    stopOnException = true
                    break
                }
            }
        }
        progressThread.start()

        val viewPagerAdapter = ImagePagerAdapter(requireActivity() as MainActivity, imageList, { runProgress = false }, { runProgress = true })
        viewPager.adapter = viewPagerAdapter

        viewPager.currentItem = 1 // Устанавливаем указатель на 0 элемент "исходного" списка

        // Чтобы не обрывать анимацию перелистывания у пользователя,
        // когда подменяем первый или последний элемент
        // мы ждем конец анимации (изменится стейт) и сдвигаем указатель
        var skipTo: Int? = null
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                // Размер списка на 2 элемента больше количества фото и индикаторов,
                // поэтому корректируем позицию текущего индикатора
                val positionForIndicator: Int
                skipTo = when (position) {
                    0 -> {
                        positionForIndicator = viewPagerAdapter.count - 2
                        viewPagerAdapter.count - 2
                    }
                    viewPagerAdapter.count - 1 -> {
                        positionForIndicator = 0
                        1
                    }
                    else -> {
                        positionForIndicator = position - 1
                        null
                    }
                }
                // Обновляем цвета индикаторов
                updateLinearIndicator(positionForIndicator)
            }
            override fun onPageScrollStateChanged(state: Int) {
                if (state == SCROLL_STATE_SETTLING) progress = 0f
                // Когда анимация закончена и если есть необходимость, то сдвигаем указатель
                if (state == 0 && skipTo != null){
                    viewPager.setCurrentItem(skipTo!!, false)
                    skipTo = null
                }
            }
        })
    }

    /**
     * Создает индикатор для каждой картинки
     * @Param [count] count Количество картинок
     */
    private fun createLinearIndicator(count: Int){

        for(i in 0 until count){
            val linear = LinearProgressIndicator(requireContext())
            val layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            layoutParams.marginStart = 7
            layoutParams.marginEnd = 7
            linear.trackCornerRadius = 50
            linear.layoutParams = layoutParams
            linear.trackColor = resources.getColor(R.color.grey)
            linear.setIndicatorColor(resources.getColor(R.color.white))
            linear.indicatorDirection = LinearProgressIndicator.INDICATOR_DIRECTION_START_TO_END
            linear.setProgressCompat(if (i < viewPager.currentItem) 100 else{ if (i == viewPager.currentItem) progress.toInt() else 0}, true)
            linearIndicator.addView(linear)
        }

        updateLinearIndicator(0)
    }

    /**
     * Обновляет индикаторы
     * @Param [position] position индекс текущей картинки
     */
    private fun updateLinearIndicator(position: Int){

        for(i in 0 until linearIndicator.childCount){
            val linear: LinearProgressIndicator = linearIndicator.getChildAt(i) as LinearProgressIndicator
            linear.setProgressCompat(if (i < position) 100 else{ if (i == position) progress.toInt() else 0}, i <= position)
        }
    }

    /**
     * Вычисляет индекс индикатора/картинки от поизиции педжера
     * @Param [position] position указатель пейджера
     * @return индекс индикатора/картинки
     */
    private fun getIndexByPosition(position: Int): Int{
        return when (position) {
            0 -> {
                viewPager.adapter!!.count - 2
            }
            viewPager.adapter!!.count - 1 -> {
                0
            }
            else -> {
                position - 1
            }
        }
    }
}