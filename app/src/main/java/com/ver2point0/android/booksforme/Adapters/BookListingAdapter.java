package com.ver2point0.android.booksforme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ver2point0.android.booksforme.Models.BookListing;
import com.ver2point0.android.booksforme.R;

public class BookListingAdapter extends ArrayAdapter<BookListing> {

    public BookListingAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        BookListing bookListing = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        TextView bookTitle = (TextView) view.findViewById(R.id.book_title);
        TextView bookAuthor = (TextView) view.findViewById(R.id.author_name);
        bookTitle.setText(bookListing.getBookTitle());
        bookAuthor.setText(bookListing.getAuthor());

        return view;
    }

}
