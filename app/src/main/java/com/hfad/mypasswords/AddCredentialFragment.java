package com.hfad.mypasswords;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class AddCredentialFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_credential, container, false);

        return view;
    }


//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        EditText textInputLayout = (EditText) getView().findViewById(R.id.add_password);
//        textInputLayout.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//                EditText editText = (EditText)v.findViewById(R.id.add_password_confirm);
//                if(MotionEvent.ACTION_UP == event.getAction()){
//                    editText.setVisibility(View.INVISIBLE);
//                }else{
//                    editText.setVisibility(View.VISIBLE);
//                }
//                return false;
//            }
//        });
//    }
}
