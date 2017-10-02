package com.hfad.mypasswords;

import android.content.DialogInterface;
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
        if(Utils.CREDENTIAL.equals(mode)){
            outState.putString(Utils.NAME, getEditTextValue(R.id.add_name));
            outState.putString(Utils.LOGIN, getEditTextValue(R.id.add_login));
            outState.putString(Utils.PASSWORD, getEditTextValue(R.id.add_password));
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
        String error = validateInputs(name,login, password);

        if(Utils.NOINPUT.equals(error)){
            return true;
        }else if (Utils.hasText(error)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Errors");
            dialog.setMessage(error);
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
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

    private String validateInputs(String name, String login, String password){
        if(!Utils.hasText(name) && !Utils.hasText(login)
                &&  !Utils.hasText(password) ){
            return Utils.NOINPUT;
        }else if (!Utils.hasText(name)){
            return "Enter a name";
        }else if (!Utils.hasText(login) &&  !Utils.hasText(password)){
            return "Enter a login or a password";
        }
        return null;
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
//        setEditText(R.id.add_name , Utils.EMPTY);
//        setEditText(R.id.add_login , Utils.EMPTY);
//        setEditText(R.id.add_password , Utils.EMPTY);
        if(position == 0){
            ((EditText)findViewById(R.id.add_name)).setVisibility(View.VISIBLE);
            ((EditText)findViewById(R.id.add_login)).setVisibility(View.VISIBLE);
            ((EditText)findViewById(R.id.add_password)).setVisibility(View.VISIBLE);
        }else{
            ((EditText)findViewById(R.id.add_name)).setVisibility(View.VISIBLE);
            ((EditText)findViewById(R.id.add_login)).setVisibility(View.INVISIBLE);
            ((EditText)findViewById(R.id.add_password)).setVisibility(View.INVISIBLE);
        }
    }

    private void setEditText(int id, String value){
        ((EditText)findViewById(id)).setText(value, TextView.BufferType.EDITABLE);
    }

    private String getEditTextValue(int id){
        return ((EditText)findViewById(id)).getText().toString();
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
