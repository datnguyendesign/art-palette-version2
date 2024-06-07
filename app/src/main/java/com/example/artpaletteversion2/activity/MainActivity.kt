package com.example.artpaletteversion2.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.artpaletteversion2.R
import com.example.artpaletteversion2.adapter.ImageDemoAdapter
import com.example.artpaletteversion2.databinding.ActivityMainBinding
import com.example.artpaletteversion2.model.ImageDemoModel
import com.example.artpaletteversion2.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageAdapter: ImageDemoAdapter

    private var imageViewPager:ViewPager2? = null
    private var images: MutableList<ImageDemoModel>? = null

    private var authProfile: FirebaseAuth? = null
    private var textViewUserName: TextView? = null
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textViewUserName = findViewById(R.id.usernameTv)
        authProfile = FirebaseAuth.getInstance()
        val firebaseUser = authProfile?.currentUser

        if (firebaseUser == null) {
            Toast.makeText(this@MainActivity, "Something went wrong! User's details are not available at the moment", Toast.LENGTH_LONG).show()
        } else {
            showUserName(firebaseUser)
        }

        imageViewPager = findViewById(R.id.imageViewPager)
        images = loadData()

        imageAdapter = ImageDemoAdapter(images)

        imageViewPager?.adapter = imageAdapter

        imageViewPager?.clipToPadding = false
        imageViewPager?.clipChildren = false
        imageViewPager?.offscreenPageLimit = 3
        val recyclerView = imageViewPager?.getChildAt(0) as RecyclerView
        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.95f + r * 0.05f
        }

        imageViewPager?.setPageTransformer(compositePageTransformer)

        // Transition between pages
        val menu = findViewById<ChipNavigationBar>(R.id.menu)
        menu.setItemSelected(R.id.home)
        menu.setOnItemSelectedListener {
            if (it == R.id.add) {
                startActivity(Intent(this@MainActivity, AddImageActivity::class.java))
                this@MainActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.explore) {
                startActivity(Intent(this@MainActivity, ExploreActivity::class.java))
                this@MainActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.saved) {
                startActivity(Intent(this@MainActivity, SavedActivity::class.java))
                this@MainActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.account) {
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                this@MainActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
        }
    }

    private fun showUserName(firebaseUser: FirebaseUser) {
        val userID = firebaseUser.uid

        // Extracting User Reference from Database for "Registered Users"
        val referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")
        referenceProfile.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                if (user != null) {
                    username = user.username

                    textViewUserName?.text = username
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun loadData(): MutableList<ImageDemoModel> {
        val images: MutableList<ImageDemoModel> = mutableListOf()
        images.add(ImageDemoModel("France", "me", "https://www.travelandleisure.com/thmb/9xr8CFGR14sLvR4IhLwKV64fEV0=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/TAL-Eiffel-Tower-BESTFRANCE0323-dada0673f8764c099b68d01695ef8057.jpg", 4.8f))
        images.add(ImageDemoModel("VietNam", "me", "https://vietnam.travel/sites/default/files/styles/top_banner/public/2022-08/VNAT%20Cover%203.png?itok=atu5sEjq", 4.8f))
        images.add(ImageDemoModel("Pasta", "me", "https://www.foodandwine.com/thmb/fjNakOY7IcuvZac1hR3JcSo7vzI=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/FAW-recipes-pasta-sausage-basil-and-mustard-hero-06-cfd1c0a2989e474ea7e574a38182bbee.jpg", 4.8f))
        images.add(ImageDemoModel("Wallpaper", "me", "https://wallpapers.com/images/featured/picture-en3dnh2zi84sgt3t.jpg", 4.8f))
        images.add(ImageDemoModel("Nature", "me", "https://cdn.pixabay.com/photo/2016/11/08/04/49/jungle-1807476_640.jpg", 4.8f))
        return images
    }
}