package com.hfad.mypasswords;


import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;


import com.hfad.mypasswords.data.Item;

import java.sql.SQLException;




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
        setTitle(Utils.EMPTY);
        String value = null;
        if(savedInstanceState == null){
            Utils.setEditTextValue(this,R.id.update_group_name,item.getName());
            value = item.getName();
        }else{
            Utils.setEditTextValue(this,R.id.update_group_name,savedInstanceState.getString(Utils.NAME));
            value=savedInstanceState.getString(Utils.NAME);
        }
        ((EditText)findViewById(R.id.update_group_name)).setSelection(value.length());
        getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.NAME, Utils.getEditTextValue(this,R.id.update_group_name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean updateGroupItem(){
        String name = Utils.getEditTextValue(this,R.id.update_group_name);
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
}
