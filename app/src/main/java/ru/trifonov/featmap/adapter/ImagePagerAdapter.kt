package ru.trifonov.featmap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import coil.load
import ru.trifonov.featmap.MainActivity
import ru.trifonov.featmap.R


class ImagePagerAdapter(
    val activity: MainActivity,
    private val images: List<String>,
    private val stopProgress: () -> Unit,
    private val resumeProgress: () -> Unit,
) : PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.image_pager_item, container, false)
        val imageView = view.findViewById<ImageView>(R.id.image_item)
//        val shimmer = view.findViewById<ShimmerFrameLayout>(R.id.shimmer_image)
        val imagePosition = position % images.size
//        shimmer.startShimmer()
        imageView.load(images[imagePosition]){
            error(R.drawable.bad_connection_icon)
            println(images[imagePosition])
            listener(
                onSuccess = { _, _ ->
//                    shimmer.stopShimmer()
//                    shimmer.hideShimmer()
                    resumeProgress()
                },
                onError = { _, result ->
//                    shimmer.stopShimmer()
//                    shimmer.hideShimmer()
                    println("Error ${result.throwable.message}")
                    resumeProgress()
                },
                onStart = {
                    stopProgress()
                })
        }

        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}