package com.hfad.mypasswords;

import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hfad.mypasswords.data.Item;;

import java.sql.SQLException;

public class AddItemActivity extends BaseActivity {

    private String mode;
    private Integer groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getExtras();
        configView();
        if(savedInstanceState != null){
            if(Utils.CREDENTIAL.equals(mode) || !isGroup()){
                setEditText(R.id.add_name , savedInstanceState.getString(Utils.NAME) );
                setEditText(R.id.add_login , savedInstanceState.getString(Utils.LOGIN));
                setEditText(R.id.add_password , savedInstanceState.getString(Utils.PASSWORD));
                if(Utils.hasText(savedInstanceState.getString(Utils.ERROR))){
                    setTextView(R.id.error_msg, savedInstanceState.getString(Utils.ERROR));
                    setTextViewVisibility(true);
                }

            }else{
                setEditText(R.id.add_name , savedInstanceState.getString(Utils.NAME) );
            }
        }
    }


    private void configView(){
        Spinner spinner = (Spinner) findViewById(R.id.entry_types);
        if(Utils.CREDENTIAL.equals(mode)){
            spinner.setVisibility(View.GONE);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.add_item_layout);
            linearLayout.setPadding(0,50,0,0);
        }else{
            spinner.setVisibility(View.VISIBLE);
            spinner.setSelection(0);
            spinner.setOnItemSelectedListener(getOnItemSelectedListener());
        }
    }

    private void getExtras(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            setMode((String)bundle.get(Utils.MODE));
            setGroupId((Integer) bundle.get(Utils.GROUPID));
        }else{
            setMode(null);
            setGroupId(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_cancel:
                this.finish();
                return true;
            case R.id.action_save:

                boolean isGroup = false;

                if(groupId == null){
                    isGroup = isGroup();
                }

                boolean quit = false;
                if(isGroup){
                    quit = addGroupItem();
                }else{
                    quit = addCredentialItem();
                }
                if(quit){
                    this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private boolean isGroup(){
        return ((Spinner) findViewById(R.id.entry_types)).getSelectedItemPosition() == 1;
    }

    public boolean addGroupItem(){
        String name = ((EditText)findViewById(R.id.add_name)).getText().toString();
        if(!Utils.hasText(name)){
            return true;
        }
        persistGroup(name);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(Utils.CREDENTIAL.equals(mode) || !isGroup()){
            outState.putString(Utils.NAME, getEditTextValue(R.id.add_name));
            outState.putString(Utils.LOGIN, getEditTextValue(R.id.add_login));
            outState.putString(Utils.PASSWORD, getEditTextValue(R.id.add_password));
            outState.putString(Utils.ERROR, getTextViewValue(R.id.error_msg));
        }else{
            outState.putString(Utils.NAME, getEditTextValue(R.id.add_name));
        }
    }

    public void persistGroup(String name){
        try {
            Item groupItem = new Item();
            groupItem.setName(Utils.capitalize(name));
            groupItem.setGroup(true);
            getHelper().getItemDao().create(groupItem);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    protected boolean addCredentialItem(){
        String name = ((EditText)findViewById(R.id.add_name)).getText().toString();
        String login = ((EditText)findViewById(R.id.add_login)).getText().toString();
        String password = ((EditText)findViewById(R.id.add_password)).getText().toString();
        String error = Utils.validateInputs(name,login, password);

        if(Utils.NOINPUT.equals(error)){
            return true;
        }else if (Utils.hasText(error)){
            setTextView(R.id.error_msg, error);
            setTextViewVisibility(true);
            return false;
        }else {
            persistCredential(login, name, password);
            return true;
        }
    }

    public void persistCredential(String login, String name, String password){
        Item credentialItem = new Item();
        credentialItem.setLogin(Utils.capitalize(login));
        credentialItem.setName(Utils.capitalize(name));
        try {
            if(Utils.hasText(password)){
                credentialItem.setPassword(EncUtil.encryptData(password.trim()));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            if (groupId == null) {
                getHelper().getItemDao().create(credentialItem);
            } else {
                credentialItem.setGroupItem(getHelper().getItemDao().queryForId(groupId));
                getHelper().getItemDao().create(credentialItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private AdapterView.OnItemSelectedListener getOnItemSelectedListener(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeView(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private void changeView(int position){
        if(position == 0){
            ((EditText)findViewById(R.id.add_name)).setVisibility(View.VISIBLE);
            ((EditText)findViewById(R.id.add_login)).setVisibility(View.VISIBLE);
            ((TextInputLayout)findViewById(R.id.password_layout)).setVisibility(View.VISIBLE);
        }else{
            ((EditText)findViewById(R.id.add_name)).setVisibility(View.VISIBLE);
            ((EditText)findViewById(R.id.add_login)).setVisibility(View.INVISIBLE);
            ((TextInputLayout)findViewById(R.id.password_layout)).setVisibility(View.INVISIBLE);
        }
    }

    private void setEditText(int id, String value){
        ((EditText)findViewById(id)).setText(value, TextView.BufferType.EDITABLE);
    }

    private String getEditTextValue(int id){
        return ((EditText)findViewById(id)).getText().toString();
    }

    private String getTextViewValue(int id){
        CharSequence charSequence = ((TextView)findViewById(id)).getText();
        if(charSequence != null){
            return charSequence.toString();
        }
        return null;
    }

    private void setTextView(int id, String value){
        ((TextView) findViewById(R.id.error_msg)).setText(value);
    }

    private void setTextViewVisibility(boolean visible){
        if(visible){
            ((TextView) findViewById(R.id.error_msg)).setVisibility(View.VISIBLE);
        }else{
            ((TextView) findViewById(R.id.error_msg)).setVisibility(View.INVISIBLE);
        }
    }




    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
