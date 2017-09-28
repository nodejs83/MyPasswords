package com.hfad.mypasswords;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import com.hfad.mypasswords.data.Item;;

/**
 * Created by Khaled Jamal on 29/08/2017.
 */

public abstract class AbstractListActivity extends BaseActivity {



    private ArrayAdapter<Item> arrayAdapter;
    private Integer groupId;
    private List<Item> items = new ArrayList<Item>();
    private ListView listView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGroupId();
        setContentView(getActivityLayoutId());
        items = getItems();
        listView = (ListView) findViewById(getListViewId());
        setAdapter();
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(getItemClickListener());
        registerForContextMenu(listView);
        listView.setOnCreateContextMenuListener(getCreateContextMenuListener());
        checkApplicationPassword();
    }

    private void setAdapter(){
        arrayAdapter = new ArrayAdapter<Item>(AbstractListActivity.this, android.R.layout.simple_list_item_1,getItems());
        listView.setAdapter(arrayAdapter);
    }

    public abstract void checkApplicationPassword();

    public abstract List<Item> getItems();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(getMenuId(), menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(getOnQueryTextListener());
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setAdapter();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private SearchView.OnQueryTextListener getOnQueryTextListener(){
        return new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                arrayAdapter.getFilter().filter(newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }
        };
    }

    private View.OnCreateContextMenuListener getCreateContextMenuListener(){
        View.OnCreateContextMenuListener createContextMenuListener = new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId()==getListViewId()) {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                    menu.setHeaderTitle(R.string.actions);
                    menu.add(Menu.NONE, 0, 0, R.string.modify_label);
                    menu.add(Menu.NONE, 1, 1, R.string.delete_label);
                    menu.add(Menu.NONE, 2, 2, R.string.cancel_label);
                }
            }
        };
        return createContextMenuListener;
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getItemId() == 1){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.alert_label);
            alert.setMessage(R.string.alert_title_label);
            alert.setPositiveButton(R.string.alert_yes_button, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeItem(info.position);
                    refreshAdapter();
                    dialog.dismiss();

                }
            });
            alert.setNegativeButton(R.string.alert_no_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
        }else if (item.getItemId() == 0){
            Item currentItem =  getItemByPosition(info.position);
            Intent intent = null;
            if(currentItem.isGroup()){
                intent = new Intent(this, GroupContentItemsActivity.class);
            }else{
                intent = new Intent(this, UpdateCredentialActivity.class);
            }
            intent.putExtra(Utils.ITEM_ID, (int) currentItem.getId());
            intent.putExtra(Utils.MODE, Utils.CREDENTIAL);
            intent.putExtra(Utils.GROUPID, getGroupId());
            startActivity(intent);
        }
        return super.onContextItemSelected(item);
    }

    public Item getItemByPosition(int position){
        return getArrayAdapter().getItem(position);
    }


    public Integer getGroupId(){
        return groupId;
    }

    public abstract void removeItem(int position);

    @Override
    protected void onResume() {
        super.onResume();
        refreshAdapter();
    }


    private void refreshAdapter(){
        items.clear();
        if(searchView != null && Utils.hasText(searchView.getQuery().toString())){
            arrayAdapter.clear();
            arrayAdapter.addAll(getItems());
            arrayAdapter.getFilter().filter(searchView.getQuery());
        }else{
            setAdapter();
        }
        arrayAdapter.notifyDataSetChanged();
    }

    public ArrayAdapter<Item> getArrayAdapter() {
        return arrayAdapter;
    }

    public void setArrayAdapter(ArrayAdapter<Item> arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public abstract int getListViewId();

    public abstract int getActivityLayoutId();

    public abstract int getMenuId();

    public abstract AdapterView.OnItemClickListener getItemClickListener();

    public abstract void setGroupId();
}
