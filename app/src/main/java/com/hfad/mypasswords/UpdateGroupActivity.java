package com.hfad.mypasswords;

import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.hfad.mypasswords.data.Item;

/**
 * Created by a602256 on 29/09/2017.
 */

public class UpdateGroupActivity extends AddItemActivity {

    private int groupId;
    private Item item;



    @Override
    protected void onStart() {
        super.onStart();
        try{
            groupId = (Integer)getIntent().getExtras().get(Utils.ITEM_ID);
            item = (Item)getItemQueryBuilder().where().eq("id", groupId).queryForFirst();
            setEditText(R.id.add_group_name ,item.getName() );
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
                boolean quit = addGroupItem();
                if(quit){
                    this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
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
}
