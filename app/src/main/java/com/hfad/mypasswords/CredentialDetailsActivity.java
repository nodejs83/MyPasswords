package com.hfad.mypasswords;

import android.app.*;
import android.os.Bundle;

import android.os.Handler;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hfad.mypasswords.data.*;

import java.sql.SQLException;

public class CredentialDetailsActivity extends BaseActivity implements DialogFragment.DialogListener{


    private Item itemObject = null;
    private Menu menu;
    private int seconds ;
    private boolean running;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Integer id = (Integer)getIntent().getExtras().get(Utils.ITEM_ID);
        Integer groupId = (Integer)getIntent().getExtras().get(Utils.GROUPID);
        try{
            itemObject = getHelper().getItemDao().queryForId(id);
        }catch(SQLException e){
            e.printStackTrace();
        }

        TextView login = (TextView)findViewById(R.id.login);
        login.setText(itemObject.getLogin());

        if(savedInstanceState != null && (running = savedInstanceState.getBoolean("running"))){
            setPassword(savedInstanceState.getString("password"));
            //runTimer();
        }else{
            setPassword(Utils.STARS);
            //initTimer();
        }
    }

    private void setPassword(String value){
        TextView password = (TextView)findViewById(R.id.password);
        password.setText(value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display, menu);
        this.menu = menu;

        if(running){
            controlMenuItem(R.id.action_show_pwd, false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_show_pwd:
                DialogFragment dialogFragment = new DialogFragment();
                dialogFragment.setListener(this);
                dialogFragment.show(getFragmentManager(), "DialogFragment");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onDialogPositiveClick(DialogFragment dialog){
        String appPass = ((EditText)dialog.getDialog().findViewById(R.id.dialog_password)).getText().toString();
        try{
            Password object = (Password) getPasswordQueryBuilder().queryForFirst();
            if(EncUtil.decryptData(object.getPassword()).equals(appPass)){
                setPassword(EncUtil.decryptData(itemObject.getPassword()));
                //runTimer();
                controlMenuItem(R.id.action_show_pwd, false);
                running = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void controlMenuItem(int id, boolean enabled){
        menu.findItem(id).setEnabled(enabled);
    }


    private void initTimer(){
        seconds = 60;
        TextView timeView = (TextView)findViewById(R.id.watch);
        timeView.setText("");
    }


    private void runTimer(){
        final TextView timeView = (TextView)findViewById(R.id.watch);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds%3600)/60;
                int secs = seconds%60;
                String time = String.format("%02d:%02d", minutes, secs);
                timeView.setText("The password will be encrypted in: " + time + " seconds");
                seconds--;
                if(seconds >= 0){
                    handler.postDelayed(this, 1000);
                }else{
                    initConfig();
                }
            }
        });
    }

    private void initConfig(){
        setPassword(Utils.STARS);
        controlMenuItem(R.id.action_show_pwd, true);
        running = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        initConfig();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("running", running);
        outState.putInt("seconds", seconds);
        outState.putString("password", getPassword());
    }

    private String getPassword(){
        Editable editable = ((EditText)findViewById(R.id.password)).getText();
        if(editable != null){
            return editable.toString();
        }
        return null;
    }

    public void onDialogNegativeClick(DialogFragment dialog){
        dialog.dismiss();
    }

}
