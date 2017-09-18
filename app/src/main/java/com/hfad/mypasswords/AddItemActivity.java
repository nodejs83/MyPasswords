package com.hfad.mypasswords;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

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

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            AddCredentialFragment();
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
            case R.id.action_cancel:
                this.finish();
                return true;
            case R.id.action_save:

                boolean isGroup = false;

                if(groupId == null){
                    isGroup = ((Spinner) findViewById(R.id.entry_types)).getSelectedItemPosition() == 1;
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

    private boolean addGroupItem(){
        String name = ((EditText)findViewById(R.id.add_group_name)).getText().toString();
        if(!Utils.hasText(name)){
            return true;
        }
        try {
            Item groupItem = new Item();
            groupItem.setName(Utils.capitalize(name));
            groupItem.setGroup(true);
            getHelper().getItemDao().create(groupItem);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    private boolean addCredentialItem(){
        String name = ((EditText)findViewById(R.id.add_name)).getText().toString();
        String login = ((EditText)findViewById(R.id.add_login)).getText().toString();
        String password = ((EditText)findViewById(R.id.add_password)).getText().toString();
        String confirm = ((EditText)findViewById(R.id.add_password_confirm)).getText().toString();
        String error = validateInputs(name,login, password, confirm);

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
            Item credentialItem = new Item();
            credentialItem.setLogin(login);
            credentialItem.setName(Utils.capitalize(name));
            credentialItem.setPassword(EncUtil.encrypt(password));
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
            return true;
        }
    }

    private String validateInputs(String name, String login, String password, String confirm){
        if(!Utils.hasText(name) && !Utils.hasText(login)
                &&  !Utils.hasText(password) &&  !Utils.hasText(confirm)){
            return Utils.NOINPUT;
        }else if (!Utils.hasText(name)){
            return "Enter a name";
        }else if (!Utils.hasText(login) &&  !Utils.hasText(password)
                &&  !Utils.hasText(confirm)){
            return "Enter a login or a password";
        }

        if(Utils.hasText(password) || Utils.hasText(confirm)){
            if(!password.equals(confirm)){
                return "The passwords do not match";
            }
        }
        return null;
    }


    private AdapterView.OnItemSelectedListener getOnItemSelectedListener(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner mySpinner=(Spinner) findViewById(R.id.entry_types);
                replaceFragment(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private void replaceFragment(int position){
        Fragment fragment = null;
        if(position == 0){
            fragment = new AddCredentialFragment();
        }else{
            fragment = new AddGroupFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }



    private void AddCredentialFragment(){
        AddCredentialFragment firstFragment = new AddCredentialFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
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
