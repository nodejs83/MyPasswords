package com.hfad.mypasswords;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hfad.mypasswords.data.Item;

/**
 * Created by a602256 on 26/09/2017.
 */

public class UpdateCredentialActivity extends AddItemActivity {

    private int credentialId;
    private Item item;

    @Override
    protected void onStart() {
        super.onStart();
        try{
            credentialId = (Integer)getIntent().getExtras().get(Utils.ITEM_ID);
            item = (Item)getItemQueryBuilder().where().eq("id", credentialId).queryForFirst();
            setEditText(R.id.add_name ,item.getName() );
            setEditText(R.id.add_login , item.getLogin());
            //setEditText(R.id.add_password, EncUtil.decryptData(item.getPassword()));
        }catch(Exception e){
            e.printStackTrace();
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
                boolean quit = addCredentialItem();
                if(quit){
                    this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
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

}
