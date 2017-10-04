package com.hfad.mypasswords;


import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;


import com.hfad.mypasswords.data.Item;


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
        setTitle(Utils.EMPTY);
        if(savedInstanceState == null){
            Utils.setEditTextValue(this,R.id.update_name ,item.getName() );
            Utils.setEditTextValue(this,R.id.update_login , item.getLogin());
        }else{
            Utils.setEditTextValue(this,R.id.update_name , savedInstanceState.getString(Utils.NAME) );
            Utils.setEditTextValue(this,R.id.update_login , savedInstanceState.getString(Utils.LOGIN));
            Utils.setEditTextValue(this,R.id.update_password , savedInstanceState.getString(Utils.PASSWORD));
            if(Utils.hasText(savedInstanceState.getString(Utils.ERROR))){
                Utils.setTextViewValue(this,R.id.error_msg, savedInstanceState.getString(Utils.ERROR));
                Utils.setTextViewVisibility(this,true);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.NAME, Utils.getEditTextValue(this,R.id.update_name));
        outState.putString(Utils.LOGIN, Utils.getEditTextValue(this,R.id.update_login));
        outState.putString(Utils.PASSWORD, Utils.getEditTextValue(this,R.id.update_password));
        outState.putString(Utils.ERROR, Utils.getTextViewValue(this,R.id.error_msg));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }


    protected boolean updateCredentialItem(){
        String name = Utils.getEditTextValue(this,R.id.update_name);
        String login = Utils.getEditTextValue(this,R.id.update_login);
        String password = Utils.getEditTextValue(this,R.id.update_password);
        String error = Utils.validateInputs(this,name,login, password);

        if(Utils.NOINPUT.equals(error)){
            return true;
        }else if (Utils.hasText(error)){
            Utils.setTextViewValue(this, R.id.error_msg, error);
            Utils.setTextViewVisibility(this, true);
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


}
