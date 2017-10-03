package com.hfad.mypasswords;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hfad.mypasswords.data.Item;

import java.sql.SQLException;

/**
 * Created by a602256 on 26/09/2017.
 */

public class UpdateCredentialActivity extends BaseActivity {

    private int credentialId;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_credential);
        try{
            credentialId = (Integer)getIntent().getExtras().get(Utils.ITEM_ID);
            item = (Item)getItemQueryBuilder().where().eq("id", credentialId).queryForFirst();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(savedInstanceState == null){
            setEditText(R.id.update_name ,item.getName() );
            setEditText(R.id.update_login , item.getLogin());
        }else{
            setEditText(R.id.update_name , savedInstanceState.getString(Utils.NAME) );
            setEditText(R.id.update_login , savedInstanceState.getString(Utils.LOGIN));
            setEditText(R.id.update_password , savedInstanceState.getString(Utils.PASSWORD));
            if(Utils.hasText(savedInstanceState.getString(Utils.ERROR))){
                setTextView(R.id.error_msg, savedInstanceState.getString(Utils.ERROR));
                setTextViewVisibility(true);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.NAME, getEditTextValue(R.id.update_name));
        outState.putString(Utils.LOGIN, getEditTextValue(R.id.update_login));
        outState.putString(Utils.PASSWORD, getEditTextValue(R.id.update_password));
        outState.putString(Utils.ERROR, getTextViewValue(R.id.error_msg));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }


    protected boolean updateCredentialItem(){
        String name = getEditTextValue(R.id.update_name);
        String login = getEditTextValue(R.id.update_login);
        String password = getEditTextValue(R.id.update_password);
        String error = Utils.validateInputs(name,login, password);

        if(Utils.NOINPUT.equals(error)){
            return true;
        }else if (Utils.hasText(error)){
            TextView errorMsg = (TextView) findViewById(R.id.error_msg);
            errorMsg.setText(error);
            errorMsg.setVisibility(View.VISIBLE);
            return false;
        }else {
            persistCredential(login, name, password);
            return true;
        }
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
                boolean quit = updateCredentialItem();
                if(quit){
                    this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void persistCredential(String login, String name, String password){
        item.setLogin(Utils.capitalize(login));
        item.setName(Utils.capitalize(name));
        try {
            if(Utils.hasText(password)){
                item.setPassword(EncUtil.encryptData(password.trim()));
            }else{
                item.setPassword(Utils.EMPTY);
            }
            getHelper().getItemDao().update(item);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setEditText(int id, String value){
        ((EditText)findViewById(id)).setText(value, TextView.BufferType.EDITABLE);
    }

    private String getEditTextValue(int id){
        return ((EditText)findViewById(id)).getText().toString();
    }

    private void setTextView(int id, String value){
        ((TextView) findViewById(R.id.error_msg)).setText(value);
    }

    private String getTextViewValue(int id){
        CharSequence charSequence = ((TextView)findViewById(id)).getText();
        if(charSequence != null){
            return charSequence.toString();
        }
        return null;
    }

    private void setTextViewVisibility(boolean visible){
        if(visible){
            ((TextView) findViewById(R.id.error_msg)).setVisibility(View.VISIBLE);
        }else{
            ((TextView) findViewById(R.id.error_msg)).setVisibility(View.INVISIBLE);
        }
    }

}
