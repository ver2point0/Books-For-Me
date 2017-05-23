package com.ver2point0.android.booksforme.Activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ver2point0.android.booksforme.Adapters.BookListingAdapter;
import com.ver2point0.android.booksforme.Models.BookListing;
import com.ver2point0.android.booksforme.R;
import com.ver2point0.android.booksforme.Utilities.QueryUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private BookListingAdapter mBookListingAdapter;
    private TextView mTextView;
    private static final String SEARCH_RESULTS = "bookListingSearchResults";
    private static final String GOOGLE_API_URL =
            "https://www.googleapis.com/books/v1/volumes?q=search+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.edit_text);
        ImageButton imageButton = (ImageButton) findViewById(R.id.search_image_button);
        mTextView = (TextView) findViewById(R.id.no_books_found);

        mBookListingAdapter = new BookListingAdapter(this, -1);

        ListView listView = (ListView) findViewById(R.id.book_list_view);
        listView.setAdapter(mBookListingAdapter);

        imageButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (isInternetConnectionAvailable()) {
                   BookListingAsyncTask task = new BookListingAsyncTask();
                   task.execute();
               } else {
                   Toast.makeText(MainActivity.this, R.string.no_internet_connection,
                           Toast.LENGTH_SHORT).show();
               }
           }
        });

        if (savedInstanceState != null) {
            BookListing[] bookListings = (BookListing[]) savedInstanceState.getParcelableArray(SEARCH_RESULTS);
            mBookListingAdapter.addAll(bookListings);
        }
    }

    private boolean isInternetConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        try {
            return networkInfo != null && networkInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateUi(List<BookListing> bookListings) {
        if (bookListings.isEmpty()) {
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mTextView.setVisibility(View.GONE);
        }
        mBookListingAdapter.clear();
        mBookListingAdapter.addAll(bookListings);
    }

    private String getSearchInput() {
        return mEditText.getText().toString();
    }

    private String getUrlForHttpRequest() {
        String formatSearchInput = getSearchInput().trim().replaceAll("\\s+", "+");
        return GOOGLE_API_URL + formatSearchInput;
    }

    private class BookListingAsyncTask extends AsyncTask<URL, Void, List<BookListing>> {

        @Override
        protected List<BookListing> doInBackground(URL... urls) {
            URL url = createUrl(getUrlForHttpRequest());
            String jsonResponse = "";

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return parseJson(jsonResponse);
        }

        @Override
        protected void onPostExecute(List<BookListing> bookListings) {
            if (bookListings == null) {
                return;
            }
            updateUi(bookListings);
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                Log.e("MainActivity", "Problem creating the URL", e);
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromSteam(inputStream);
                } else {
                    Log.e("MainActivity", "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e("MainActivity", "Problem retrieving the Google Books API JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromSteam(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private List<BookListing> parseJson(String json) {
            if (json == null) {
                return null;
            }

            return QueryUtils.extractBookListings(json);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BookListing[] bookListings = new BookListing[mBookListingAdapter.getCount()];
        for (int i = 0; i < bookListings.length; i++) {
            bookListings[i] = mBookListingAdapter.getItem(i);
        }
        outState.putParcelableArray(SEARCH_RESULTS, bookListings);
    }
}
