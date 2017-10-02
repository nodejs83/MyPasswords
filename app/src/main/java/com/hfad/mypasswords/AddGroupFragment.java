package com.hfad.mypasswords;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hfad.mypasswords.data.Item;
import com.j256.ormlite.stmt.QueryBuilder;


public class AddGroupFragment extends Fragment {


    private String name;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null){
            setName(savedInstanceState.getString(Utils.NAME));
        }
        return inflater.inflate(R.layout.fragment_add_group, container, false);
    }


    @Override
    public void onStart() {
         super.onStart();
        ((EditText)getView().findViewById(R.id.add_group_name)).setText(name, TextView.BufferType.EDITABLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.NAME, getName());
    }

    public String getName() {
        return ((EditText)getView().findViewById(R.id.add_group_name)).getText().toString();
    }

    public void setName(String name) {
        this.name = name;
    }
}
