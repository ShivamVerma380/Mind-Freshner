package com.example.mindfreshner.memes

import android.content.Intent
import android.graphics.drawable.Drawable
import com.example.mindfreshner.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.mindfreshner.MySingleton
import com.bumptech.glide.request.target.Target
import java.util.ArrayDeque

class MemeMainActivity : AppCompatActivity() {
    private lateinit var memeImageView:ImageView
    private lateinit var memeProgressBar:ProgressBar
    private lateinit var btnShareMeme:ImageView
    private lateinit var btnNextMeme: Button
    private lateinit var btnPrevMeme:Button
    //var prevImageUrl = ArrayDeque<String>()
    var prevImageUrl = ArrayDeque<String>() // to store all previous images urls
    var poppedImageUrl = ArrayDeque<String>() // to store all popped images urls

    private lateinit var currentImageUrl:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_main)


        supportActionBar?.hide()

        memeImageView = findViewById(R.id.imgMeme)
        memeProgressBar = findViewById(R.id.progressBarMeme)
        btnShareMeme = findViewById(R.id.btnimgShare)
        btnNextMeme = findViewById(R.id.btnNextMeme)
        btnPrevMeme = findViewById(R.id.btnPreviousMeme)

        updateMeme()
        btnNextMeme.setOnClickListener {
            updateMeme()
        }
        btnShareMeme.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT,"Hey checkout this cool meme $currentImageUrl")
            val chooser = Intent.createChooser(intent,"Share this meme using....")
            startActivity(chooser)
        }
        btnPrevMeme.setOnClickListener {
            memeProgressBar.visibility = View.VISIBLE
            if(prevImageUrl.isEmpty()){
                memeProgressBar.visibility = View.GONE
                Toast.makeText(applicationContext,"No Previous Memes!!",Toast.LENGTH_LONG).show()
            }else{
                currentImageUrl = prevImageUrl.pop()
                poppedImageUrl.push(currentImageUrl)

                Glide.with(this).load(currentImageUrl).listener(object:RequestListener<Drawable>{
                    override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                        memeProgressBar.visibility= View.GONE
                        return false
                    }
                    override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                        memeProgressBar.visibility= View.GONE
                        return false
                    }
                }).into(memeImageView)

            }
        }

    }
    private fun updateMeme(){
        memeProgressBar.visibility = View.VISIBLE
        /*while(!poppedImageUrl.isEmpty()){
            prevImageUrl.push(poppedImageUrl.pop())
        }*/
        if(poppedImageUrl.isEmpty()){
            val url = "https://meme-api.herokuapp.com/gimme"
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,url,null,
                {
                    currentImageUrl = it.getString("url")
                    prevImageUrl.push(currentImageUrl)
                    //Glide.with(this).load(imageUrl).into(memeImageView)
                    Glide.with(this).load(currentImageUrl).listener(object:RequestListener<Drawable>{
                        override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                            memeProgressBar.visibility= View.GONE
                            return false
                        }
                        override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                            memeProgressBar.visibility= View.GONE
                            return false
                        }
                    }).into(memeImageView)
                }, {
                    Toast.makeText(applicationContext,"${it.toString()}",Toast.LENGTH_SHORT).show()
                })
            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

        }else{
            currentImageUrl = poppedImageUrl.pop()
            prevImageUrl.push(currentImageUrl)

            Glide.with(this).load(currentImageUrl).listener(object:RequestListener<Drawable>{
                override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                    memeProgressBar.visibility= View.GONE
                    return false
                }
                override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                    memeProgressBar.visibility= View.GONE
                    return false
                }
            }).into(memeImageView)
        }

    }
}


