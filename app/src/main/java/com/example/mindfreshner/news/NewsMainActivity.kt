package com.example.mindfreshner.news

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mindfreshner.MySingleton
import com.example.mindfreshner.R
import org.json.JSONArray
import org.json.JSONObject
import  java.util.HashMap
class NewsMainActivity : AppCompatActivity(), NewsItemClicked {

    internal var country = arrayOf("Global", "India", "Brazil", "China", "Israel",
        "Italy","Japan","Korea","Russia","Saudi Arabia","Sweden","USA")
    internal var country_images =
        intArrayOf(R.drawable.global, R.drawable.india, R.drawable.brazil, R.drawable.china, R.drawable.israel,
            R.drawable.italy,R.drawable.japan,R.drawable.korea,R.drawable.russia,R.drawable.saudi_arabia,
            R.drawable.sweden,R.drawable.usa)
    internal var category = arrayOf("All", "Business", "Entertainment", "General", "Health",
            "Science","Sports","Technology")
    internal var cateory_images =
        intArrayOf(R.drawable.all, R.drawable.business, R.drawable.entertainment, R.drawable.general, R.drawable.health,
            R.drawable.science,R.drawable.sports,R.drawable.technology)

    //Create hashmap of position->countrycode and position->category
    var country_hashmap:HashMap<Int,String> = HashMap<Int,String>()
    var category_hashMap:HashMap<Int,String> = HashMap()



    private lateinit var country_spinner:Spinner
    private lateinit var category_spinner:Spinner
    private lateinit var progressBar:ProgressBar
    private lateinit var mAdapter: NewsListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_main)
        supportActionBar?.hide()

        progressBar = findViewById(R.id.progress_circular_news)
        //Fill values in country hashmap
        country_hashmap.put(0,"global");
        country_hashmap.put(1,"in")
        country_hashmap.put(2,"br")
        country_hashmap.put(3,"cn")
        country_hashmap.put(4,"il")
        country_hashmap.put(5,"it")
        country_hashmap.put(6,"jp")
        country_hashmap.put(7,"kr")
        country_hashmap.put(8,"ru")
        country_hashmap.put(9,"sa")
        country_hashmap.put(10,"se")
        country_hashmap.put(11,"us")

        //Fill values in category hashmap
        category_hashMap.put(0,"all")
        category_hashMap.put(1,"business")
        category_hashMap.put(2,"entertainment")
        category_hashMap.put(3,"general")
        category_hashMap.put(4,"health")
        category_hashMap.put(5,"science")
        category_hashMap.put(6,"sports")
        category_hashMap.put(7,"technology")

        country_spinner = findViewById<View>(R.id.spinnerCountry) as Spinner
        country_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(
                    this@NewsMainActivity,
                    "You Select Position: " + position + " " + country[position],
                    Toast.LENGTH_SHORT
                ).show()
                fetchNews(country_spinner.selectedItemPosition,category_spinner.selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val customAdapter = CustomAdapter(applicationContext, country_images, country)
        country_spinner.adapter = customAdapter


        category_spinner = findViewById<View>(R.id.spinnerCategory) as Spinner
        category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(
                    this@NewsMainActivity,
                    "Your Select Position: " + position + " " + category[position],
                    Toast.LENGTH_SHORT
                ).show()
                fetchNews(country_spinner.selectedItemPosition,category_spinner.selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val customAdapter2 = CustomAdapter(applicationContext, cateory_images, category)
        category_spinner.adapter = customAdapter2


        recyclerView = findViewById(R.id.recyclerViewNews) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchNews(country_spinner.selectedItemPosition,category_spinner.selectedItemPosition)
        mAdapter = NewsListAdapter(this)
        recyclerView.adapter = mAdapter
    }

    private fun fetchNews(countryPos:Int,categoryPos:Int) {
        progressBar.visibility = View.VISIBLE
        var url=""
        var current_category:String?=null
        var current_country:String?=null
        if(countryPos==0 && categoryPos==0){
             url = "https://newsapi.org/v2/top-headlines?sources=bbc-news&apiKey=86b044d08ee54346b444ab0f1ed0edcb"
        }else if(countryPos==0){
            current_category = category_hashMap.get(categoryPos)
            url = "https://newsapi.org/v2/top-headlines?category=$current_category&apiKey=86b044d08ee54346b444ab0f1ed0edcb"
        }else if(categoryPos==0){
            current_country = country_hashmap.get(countryPos)
            url = "https://newsapi.org/v2/top-headlines?country=$current_country&apiKey=86b044d08ee54346b444ab0f1ed0edcb"
        }else{
            current_category = category_hashMap.get(categoryPos)
            current_country = country_hashmap.get(countryPos)
            url = "https://newsapi.org/v2/top-headlines?country=$current_country&category=$current_category&apiKey=86b044d08ee54346b444ab0f1ed0edcb"
        }
        /*
        val jsonObjectRequest = object:JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {
                Log.d("JsonObject","Success")
                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                for(i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
                    )
                    newsArray.add(news)
                }

                mAdapter.updateNews(newsArray)
                progressBar.visibility = View.GONE
            },
            Response.ErrorListener {
                progressBar.visibility = View.GONE
                Log.d("JsonObject","Failure:${it.message}")

            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"]="Mozilla/5.0"
                return headers
            }
        }

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)*/
        val request = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener {
                Log.d("JsonObject","Success")
                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                for(i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
                    )
                    newsArray.add(news)
                }

                mAdapter.updateNews(newsArray)
                progressBar.visibility = View.GONE
            },
            Response.ErrorListener {
                progressBar.visibility = View.GONE
                Log.d("JsonObject","Failure:${it.message}")

            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"]="Mozilla/5.0"
                return headers
            }
        }

        MySingleton.getInstance(this).addToRequestQueue(request)
    }

    override fun onItemClicked(item: News) {
        val builder =  CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }
}
