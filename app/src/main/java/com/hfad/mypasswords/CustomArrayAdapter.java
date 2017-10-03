package com.hfad.mypasswords;


import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.hfad.mypasswords.data.Item;

import java.util.List;



public class CustomArrayAdapter<T extends Item> extends ArrayAdapter<T> {

    public CustomArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        final T item = getItem(position);
        ImageView imageView = (ImageView)view.findViewById(R.id.itemImage);
        if(item.isGroup()){
            imageView.setImageResource(R.drawable.ic_group);
        }else{
            imageView.setImageResource(R.drawable.ic_acount);
        }
        return view;
    }
}
