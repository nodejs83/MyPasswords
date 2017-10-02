package com.hfad.mypasswords;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.hfad.mypasswords.data.Item;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by a602256 on 29/09/2017.
 */

public class UpdateGroupActivity  extends BaseActivity{

    private int groupId;
    private Item item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_group);
        groupId = (Integer)getIntent().getExtras().get(Utils.ITEM_ID);
        try{
            item = (Item)getItemQueryBuilder().where().eq("id", groupId).queryForFirst();
        }catch(SQLException e){
            e.printStackTrace();
        }
        if(savedInstanceState == null){
            setEditText(R.id.update_group_name, item.getName());
        }else{
            setEditText(R.id.update_group_name, savedInstanceState.getString(Utils.NAME));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.NAME, getEditTextValue(R.id.update_group_name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean updateGroupItem(){
        String name = getEditTextValue(R.id.update_group_name);
        if(!Utils.hasText(name)){
            return true;
        }
        persistGroup(name);
        return true;
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
                boolean quit = updateGroupItem();
                if(quit){
                    this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void persistGroup(String name){
        item.setName(Utils.capitalize(name));
        try {
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
}
