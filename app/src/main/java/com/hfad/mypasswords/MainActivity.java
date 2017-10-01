package com.hfad.mypasswords;


import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.hfad.mypasswords.data.Item;
import com.hfad.mypasswords.data.Password;
import com.j256.ormlite.stmt.DeleteBuilder;;



import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;


public class MainActivity extends AbstractListActivity {


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
            final Item item = getCustomArrayAdapter().getItem(position);
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
                intent.putExtra(Utils.GROUP_NAME, selected.getName());
                startActivity(intent);
            }
        };
        return itemClickListener;
    }

    private boolean running;
    private AlertDialog alertDialog;

    public void checkApplicationPassword(){
        try{
            Password password = (Password) getPasswordQueryBuilder().queryForFirst();
            if(password == null || !Utils.hasText(password.getPassword())){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                LayoutInflater inflater = getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.dialog_layout, null));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(getDialogOnClickListener());
                running = true;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public View.OnClickListener getDialogOnClickListener(){
        return new View.OnClickListener(){
            public void onClick(View v){
                String password = getPasswordValue();
                String confirmed = getConfirmPasswordValue();
                try{
                    if(Utils.hasText(password) && Utils.hasText(confirmed)
                            && password.equals(confirmed)){
                        Password object = new Password();
                        object.setPassword(EncUtil.encryptData(password));
                        getHelper().getPasswordDao().create(object);
                        alertDialog.dismiss();
                        running = false;
                    }else if(!Utils.hasText(password) && !Utils.hasText(confirmed)){
                        TextView textView = ((TextView)alertDialog.findViewById(R.id.error_message));
                        textView.setText("The password is mandatory");
                        textView.setVisibility(View.VISIBLE);
                    }else{
                        TextView textView = ((TextView)alertDialog.findViewById(R.id.error_message));
                        textView.setText("The passwords do not match");
                        textView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
    }

    private String getPasswordValue(){
        return ((EditText)alertDialog.findViewById(R.id.dialog_password)).getText().toString();
    }

    private String getConfirmPasswordValue(){
        return ((EditText)alertDialog.findViewById(R.id.confirm_dialog_password)).getText().toString();
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
          outState.putBoolean("running", running);
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
