package com.hfad.mypasswords;


import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hfad.mypasswords.data.Item;
import com.hfad.mypasswords.data.Password;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.opencsv.CSVWriter;;


import java.io.File;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;


public class MainActivity extends AbstractListActivity {


    private boolean running;
    private AlertDialog alertDialog;
    private boolean backup;
    private boolean export;

    public List<Item> getItems(){
        try{
            return queryItems(getItemQueryBuilder().orderBy(Utils.NAME, true).where().isNull(Utils.GROUPITEM_COLUMN).prepare());
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
                return true;
            case R.id.send_me:
                createAlertDialog(Utils.BACKUP);
                return true;
            case R.id.export:
                createAlertDialog(Utils.EXPORT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void createAlertDialog(String operation){
        alertDialog = Utils.getAlertDialog(R.layout.dialog_fragment, this, getOkListener(), getCancelListener(), true);
        alertDialog.show();
        alertDialog.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        if(Utils.BACKUP.equals(operation)){
            backup = true;
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(getOKDialogOnClickListener(Utils.BACKUP));
        }else{
            export = true;
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(getOKDialogOnClickListener(Utils.EXPORT));
        }
    }

    private void send(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(Utils.TEXT_PLAIN);
        intent.putExtra(Intent.EXTRA_TEXT, getData().toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.backup));
        String chooserTitle = getString(R.string.backup);
        Intent chosenIntent = Intent.createChooser(intent, chooserTitle);
        startActivity(chosenIntent);
    }


    private void export(){
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = baseDir + File.separator + Utils.PASSKEYS + File.separator + Utils.EXPORT_FILE;
        File f = new File(filePath );
        CSVWriter writer;
        try{
            if(f.exists() && !f.isDirectory()){
                writer = new CSVWriter(new FileWriter(filePath , true));
            }
            else {
                writer = new CSVWriter(new FileWriter(filePath));
            }

            List<Item> items =  queryItems(getItemQueryBuilder().orderBy(Utils.NAME, true).prepare());
            if(items != null && !items.isEmpty()) {
                for (Item item : items) {
                    String[] data = {String.valueOf(item.getId()),item.getName(), formatString(item.getLogin()),
                            formatString(EncUtil.decryptData(item.getPassword())), String.valueOf(item.isGroup),
                            item.getGroupItem() != null ?  String.valueOf(item.getGroupItem().getId()) : Utils.EMPTY};
                    writer.writeNext(data);
                }
            }
            writer.close();
            Toast.makeText(this, R.string.file_exported, Toast.LENGTH_LONG).show();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String formatString(String value){
        if(Utils.hasText(value)){
            return value;
        }
        return Utils.EMPTY;
    }

    public StringBuffer getData(){
        StringBuffer buffer = null;
        try{
            Password object = (Password) getPasswordQueryBuilder().queryForFirst();
            buffer = new StringBuffer();
            buffer.append(Utils.FIRST + getString(R.string.application_pwd) + Utils.DOTS + EncUtil.decryptData(object.getPassword()));
            buffer.append(Utils.RETURN + Utils.RETURN);
            List<Item> items =  queryItems(getItemQueryBuilder().orderBy(Utils.NAME, true).where()
                    .eq(Utils.ISGROUP_COLUMN, false).prepare());
            if(items != null && !items.isEmpty()){
                int i = 2;
                for(Item item : items){
                    if(item.getGroupItem() == null){
                        buffer.append(i + Utils.MINUS + item.getName() + Utils.DOTS + Utils.RETURN);
                    }else{
                        buffer.append(i + Utils.MINUS + item.getGroupItem().getName() + "[" + item.getName() + "]"  + Utils.DOTS + Utils.RETURN);
                    }


                    if(Utils.hasText(item.getLogin())){
                        buffer.append(getString(R.string.login) + Utils.DOTS);
                        buffer.append(item.getLogin() + Utils.RETURN);
                    }
                    if(Utils.hasText(item.getPassword())){
                        buffer.append(getString(R.string.pwd) + Utils.DOTS);
                        buffer.append(EncUtil.decryptData(item.getPassword()) + Utils.RETURN);
                    }
                    buffer.append(Utils.RETURN);
                    i++;
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return buffer;
    }


    public View.OnClickListener getOKDialogOnClickListener(final String operation){
        return new View.OnClickListener() {
            public void onClick(View v) {
                String appPass = ((EditText) alertDialog.findViewById(R.id.dialog_password)).getText().toString();
                try {
                    if(Utils.hasText(appPass)){
                        Password object = (Password) getPasswordQueryBuilder().queryForFirst();
                        if (EncUtil.decryptData(object.getPassword()).equals(appPass)) {
                            if(Utils.BACKUP.equals(operation)){
                                send();
                                backup = false;
                            }else{
                                export();
                                export = false;
                            }

                            alertDialog.dismiss();
                        }else{
                            TextView textView = ((TextView)alertDialog.findViewById(R.id.message));
                            textView.setText(getString(R.string.pwd_msg));
                            textView.setVisibility(View.VISIBLE);
                        }
                    }else{
                        TextView textView = ((TextView)alertDialog.findViewById(R.id.message));
                        textView.setText(getString(R.string.mandat_pwd_msg));
                        textView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }



    private DialogInterface.OnClickListener getCancelListener(){
        return new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                backup = false;
                export = false;
                dialog.dismiss();
            }
        };
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



    public void checkApplicationPassword(){
        try{
            Password password = (Password) getPasswordQueryBuilder().queryForFirst();
            if(password == null || !Utils.hasText(password.getPassword())){
                alertDialog = Utils.getAlertDialog(R.layout.dialog_layout, this, getOkListener(), null, false);
                alertDialog.show();
                alertDialog.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(getDialogOnClickListener());
                running = true;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private DialogInterface.OnClickListener getOkListener(){
        return new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {

            }
        };
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
                        textView.setText(getString(R.string.mandat_pwd_msg));
                        textView.setVisibility(View.VISIBLE);
                    }else{
                        TextView textView = ((TextView)alertDialog.findViewById(R.id.error_message));
                        textView.setText(R.string.match_pwd_msg);
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
          outState.putBoolean(Utils.RUNNING, running);
          outState.putBoolean(Utils.ISBACKUP, backup);
          outState.putBoolean(Utils.ISEXPORT, export);
    }

    public void setBackup(boolean backup) {
        this.backup = backup;
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
