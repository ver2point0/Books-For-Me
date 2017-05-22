package com.ver2point0.android.booksforme.Utilities;

import com.ver2point0.android.booksforme.Models.BookListing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private QueryUtils() {}

    public static List<BookListing> extractBookListings(String json) {
        List<BookListing> bookListings = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(json);
            if (jsonResponse.getInt("totalItems") == 0) {
                return bookListings;
            }
            JSONArray jsonArray = jsonResponse.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bookListObject = jsonArray.getJSONObject(i);
                JSONObject bookListInfo = bookListObject.getJSONObject("volumeInfo");
                String bookListTitle = bookListInfo.getString("title");
                JSONArray authorsArray = bookListInfo.getJSONArray("authors");
                String authors = formatAuthorsList(authorsArray);

                BookListing bookListing = new BookListing(bookListTitle, authors);
                bookListings.add(bookListing);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bookListings;
    }

    private static String formatAuthorsList(JSONArray authorsList) throws JSONException {
        String aList = null;

        if (authorsList.length() == 0) {
             return null;
        }

        for (int i = 0; i < authorsList.length(); i++) {
            if (i == 0) {
                aList = authorsList.getString(0);
            } else {
                aList += ", " + authorsList.getString(i);
            }
        }

        return aList;
    }
}
