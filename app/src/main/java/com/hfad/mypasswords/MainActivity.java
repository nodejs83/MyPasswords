package com.hfad.mypasswords;


import android.content.Intent;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.hfad.mypasswords.data.Item;
import com.hfad.mypasswords.data.Password;
import com.j256.ormlite.stmt.DeleteBuilder;;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;


public class MainActivity extends AbstractListActivity implements DialogFragment.DialogListener{


    public List<Item> getItems(){
        try{
            return queryItems(getItemQueryBuilder().where().isNull(Utils.GROUPITEM_COLUMN).prepare());
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
                        DeleteBuilder deleteBuilder = getItemDeleteBuilder();
                        deleteBuilder.where().eq(Utils.GROUPITEM_COLUMN, item.getId());
                        deleteItems(deleteBuilder.prepare());
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

    public void checkApplicationPassword(){
        try{
            Password password = (Password) getPasswordQueryBuilder().queryForFirst();
            if(password == null || !Utils.hasText(password.getPassword())){
                DialogFragment dialogFragment = new DialogFragment();
                dialogFragment.setListener(this);
                dialogFragment.show(getFragmentManager(), "DialogFragment");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void onDialogPositiveClick(DialogFragment dialog){
        String password = ((EditText)dialog.getDialog().findViewById(R.id.dialog_password)).getText().toString();
        if(Utils.hasText(password)){
            try{
                Password object = new Password();
                object.setPassword(EncUtil.encryptData(password));
                getHelper().getPasswordDao().create(object);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void onDialogNegativeClick(DialogFragment dialog){

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
