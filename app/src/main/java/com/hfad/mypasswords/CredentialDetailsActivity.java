package com.hfad.mypasswords;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hfad.mypasswords.data.*;

import java.sql.SQLException;

public class CredentialDetailsActivity extends BaseActivity{


    private Item itemObject = null;
    private Menu menu;
    private AlertDialog alertDialog;
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
        if(Utils.hasText(itemObject.getPassword())){
            Utils.setTextViewValue(this, R.id.password, Utils.STARS);
        }
        setActionBarTitle();

        if(savedInstanceState != null && savedInstanceState.getBoolean(Utils.RUNNING)){
            createAlertDialog();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(running){
            outState.putBoolean(Utils.RUNNING, running);
        }
    }

    public void setActionBarTitle(){
        String title = getIntent().getExtras() != null ? (String)getIntent().getExtras().get(Utils.GROUP_NAME) : Utils.EMPTY;
        if(Utils.hasText(title)){
            setTitle(title);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display, menu);

        this.menu = menu;

        if(!Utils.hasText(itemObject.getPassword())){
            controlMenuItem(R.id.action_show_pwd, false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public void onCopyLogin(View v){
        copy(R.id.login);
        Toast.makeText(this, R.string.login_copied, Toast.LENGTH_LONG).show();
    }

    public void onCopyPassword(View v){
        copy(R.id.password);
        Toast.makeText(this, R.string.pwd_copied, Toast.LENGTH_LONG).show();
    }

    private void copy(int id){
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(Utils.SIMPLE_TEXT, Utils.getEditTextValue(this, id));
        clipboard.setPrimaryClip(clip);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_show_pwd:
                createAlertDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void createAlertDialog(){
        alertDialog = Utils.getAlertDialog(R.layout.dialog_fragment, this, getOkListener(), getCancelListener(), true);
        alertDialog.show();
        running = true;
        alertDialog.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(getDialogOnClickListener());
    }

    private DialogInterface.OnClickListener getOkListener(){
        return new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {

            }
        };
    }

    private DialogInterface.OnClickListener getCancelListener(){
        return new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                running = false;
                dialog.dismiss();
            }
        };
    }


    public View.OnClickListener getDialogOnClickListener(){
        return new View.OnClickListener() {
            public void onClick(View v) {
                String appPass = ((EditText) alertDialog.findViewById(R.id.dialog_password)).getText().toString();
                try {
                    if(Utils.hasText(appPass)){
                        Password object = (Password) getPasswordQueryBuilder().queryForFirst();
                        if (EncUtil.decryptData(object.getPassword()).equals(appPass)) {
                            Utils.setTextViewValue(CredentialDetailsActivity.this, R.id.password, EncUtil.decryptData(itemObject.getPassword()));
                            controlMenuItem(R.id.action_show_pwd, false);
                            running = false;
                            alertDialog.dismiss();
                        }else{
                            TextView textView = ((TextView)alertDialog.findViewById(R.id.message));
                            textView.setText(getString(R.string.pwd_msg));
                            textView.setVisibility(View.VISIBLE);
                        }
                    }else{
                        TextView textView = ((TextView)alertDialog.findViewById(R.id.message));
                        textView.setText(getString(R.string.mandat_pwd_msg));
                        textView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void controlMenuItem(int id, boolean enabled){
        menu.findItem(id).setEnabled(enabled);
    }

    private void initConfig(){
        Utils.setTextViewValue(this ,R.id.password, Utils.STARS);
        controlMenuItem(R.id.action_show_pwd, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        initConfig();
    }

}
