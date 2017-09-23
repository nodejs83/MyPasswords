package com.hfad.mypasswords;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.sql.SQLException;
import java.util.List;
import com.hfad.mypasswords.data.Item;;

public class GroupContentItemsActivity extends AbstractListActivity {


    public List<Item> getItems(){
        try {
            return queryItems(getItemQueryBuilder().where().eq(Utils.GROUPITEM_COLUMN, getGroupId()).prepare());
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add:
                Intent intent = new Intent(GroupContentItemsActivity.this, AddItemActivity.class);
                intent.putExtra(Utils.MODE, Utils.CREDENTIAL);
                intent.putExtra(Utils.GROUPID, getGroupId());
                startActivity(intent);
        }
        return true;
    }

    public AdapterView.OnItemClickListener getItemClickListener(){
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GroupContentItemsActivity.this, CredentialDetailsActivity.class);
                Item selected = (Item) parent.getAdapter().getItem(position);

                intent.putExtra(Utils.ITEM_ID, (int) selected.getId());
                intent.putExtra(Utils.GROUPID, getGroupId());
                startActivity(intent);
            }
        };
        return itemClickListener;
    }


    public void removeItem(int position){
        try{
            getHelper().getItemDao().deleteById(getArrayAdapter().getItem(position).getId());
        }catch(SQLException e){
            e.printStackTrace();
        }
    }


    public void setGroupId(){
        if(getIntent().getExtras() != null){
            setGroupId((Integer)getIntent().getExtras().get(Utils.ITEM_ID));
        }else{
            setGroupId(null);
        }
    }

    public int getListViewId(){
        return  R.id.list_sub_items;
    }

    public  int getMenuId(){
        return R.menu.menu_group;
    }

    public int getActivityLayoutId(){
        return R.layout.activity_group_list;
    }

    public void checkApplicationPassword(){}
}
