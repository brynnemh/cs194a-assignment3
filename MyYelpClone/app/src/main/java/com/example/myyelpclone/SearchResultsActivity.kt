package com.example.myyelpclone

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log

private const val TAG = "SearchResultsActivity"
class SearchResultsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        handleIntent(intent);
        super.onCreate(savedInstanceState);
    }

    override fun onNewIntent(intent : Intent) {
        handleIntent(intent);
    }

    private fun handleIntent(intent : Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY) as String;
            // to do: send query
            if (!query.isEmpty()) {
                Log.i(TAG, "new query $query");
            }
        }
    }
}