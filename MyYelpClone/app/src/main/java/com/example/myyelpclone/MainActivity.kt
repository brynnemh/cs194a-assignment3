package com.example.myyelpclone

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "MainActivity"
private const val BASE_URL = "https://api.yelp.com/v3/"
private const val API_KEY = "aULBLPYqkrIBQrRzJ0-P9whrikX0HeecZMtz2FNvL2kYgMz5WHbmltdw0mq0uivICBVezso-nz9wCIf7eP7BHrLkT4uIfDbYZJZieryNMsHRIY_GlQL-XHMEihLHXnYx"
class MainActivity : AppCompatActivity() {

    private val restaurants = mutableListOf<YelpRestaurant>();
    private val adapter = RestaurantsAdapter(this, restaurants);
    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
        .build();
    private val yelpService = retrofit.create(YelpService::class.java);

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager;
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            val searchable = searchManager.getSearchableInfo(componentName);
            setSearchableInfo(searchable);
        }

        return true;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvRestaurants.adapter = adapter
        rvRestaurants.layoutManager = LinearLayoutManager(this)

        //getRestaurants("Avocado Toast", "New York");

        handleIntent(intent);
    }

    private fun getRestaurants(searchTerm : String, location : String) {
        yelpService.searchRestaurant("Bearer $API_KEY", searchTerm, location)
            .enqueue(object : Callback<YelpSearchResult> {
                override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                    Log.i(TAG, "onResponse $response");
                    val body = response.body();
                    if (body == null) {
                        Log.w(TAG, "Did not receive valid response body from Yelp API... exiting");
                        return;
                    }

                    restaurants.clear();
                    restaurants.addAll(body.restaurants);
                    adapter.notifyDataSetChanged();
                }

                override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                    Log.i(TAG, "onFailure $t");
                }
            });
    }

    override fun onNewIntent(intent : Intent) {
        handleIntent(intent);
        super.onNewIntent(intent);
    }

    private fun handleIntent(intent : Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY) as String;
            if (!query.isEmpty()) {
                Log.i(TAG, "new query $query");

                getRestaurants(query, "New York");
            }
        }
    }

}
