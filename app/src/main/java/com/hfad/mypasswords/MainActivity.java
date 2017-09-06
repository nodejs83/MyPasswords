package com.hfad.mypasswords;


import android.content.Intent;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.hfad.mypasswords.data.Item;;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;


public class MainActivity extends AbstractListActivity {


    public List<Item> getItems(){
        try{
            return query(getQueryBuilder().where().isNull(Utils.GROUPITEM_COLUMN).prepare());
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    public void removeItem(int position){
        try{
            final Item item = getArrayAdapter().getItem(position);
            getHelper().getItemDao().callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    if(item.isGroup()){
                        query(getDeleteBuilder().where().eq(Utils.GROUPITEM_COLUMN, item.getId()).prepare());
                    }
                    getHelper().getItemDao().deleteById(item.getId());
                    return null;
                }
            });
            

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(this, AddItemActivity.class);
                startActivity(intent);
        }
        return true;
    }

    public AdapterView.OnItemClickListener getItemClickListener(){
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item selected = (Item) parent.getAdapter().getItem(position);
                Intent intent = null;
                if(selected.isGroup()){
                    intent = new Intent(MainActivity.this, GroupContentItemsActivity.class);
                }else{
                    intent = new Intent(MainActivity.this, CredentialDetailsActivity.class);
                }
                intent.putExtra(Utils.ITEM_ID, (int) selected.getId());
                startActivity(intent);
            }
        };
        return itemClickListener;
    }

    public int getListViewId(){
        return  R.id.list_items;
    }

    public  int getMenuId(){
        return R.menu.menu_main;
    }

    public int getActivityLayoutId(){
        return R.layout.activity_main;
    }

    public void setGroupId(){
        setGroupId(null);
    }
}
