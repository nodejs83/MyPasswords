package com.hfad.mypasswords.data;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by a602256 on 04/09/2017.
 */

public class DataBaseUtils {

    private static final List<Item> items = new ArrayList<Item>();
    private static int index = 1;


    public static final String GROUPID = "GroupId";
    public static final String ITEM_ID = "itemId";
    public static final String MODE = "mode";

    static {
        Item item1 = new Item();
        Item item2 = new Item();
        Item item3 = new Item();


        Item item4 = new Item();
        Item item5 = new Item();
        Item item6 = new Item();

        Item item7 = new Item();
        Item item8 = new Item();
        Item item9 = new Item();


        item1.setName("Facebook");
        item1.setLogin("jamsn83@gmail.com");
        item1.setPassword("12345687");


        item2.setName("Gmail");
        item2.setLogin("jamalbayi@gmail.com");
        item2.setPassword("12345687");

        item3.setName("Amazon");
        item3.setLogin("jamsn83@gmail.com");
        item3.setPassword("12345687");

        item4.setName("Visa");
        item4.setLogin("Card");
        item4.setPassword("12345687");

        item5.setName("Lulu");
        item5.setLogin("jamalbayi@gmail.com");
        item5.setPassword("12345687");

        item6.setName("Smashwords");
        item6.setLogin("jamalbayi@gmail.com");
        item6.setPassword("12345687");

        item7.setName("Windows Pro");
        item7.setLogin("A602256");
        item7.setPassword("12345687");

        item8.setName("Windows Home");
        item8.setLogin("Jamal");
        item8.setPassword("12345687");


        item9.setName("Adwords");
        item9.setLogin("jamalbayi@gmail.com");
        item9.setPassword("12345687");

        items.add(item1);
        items.add(item2);
        items.add(item3);

        items.add(item4);
        items.add(item5);
        items.add(item6);

        items.add(item7);
//        items.add(item8);
//        items.add(item9);
    }


    public static List<Item> getItems(){
        return items ;
    }

//    public static List<Item> getSubItemsByGroupId(int groupId){
//        for(Item item : getItems()){
//            if(item.getId() == groupId && item.isGroup()){
//                return item.getSubItems();
//            }
//        }
//        return null;
//    }
//
//    public static Item getSubItemByIds(int groupId, int itemId){
//        List<Item> subItems = getSubItemsByGroupId(groupId);
//        for(Item item : subItems){
//            if(item.getId() == itemId){
//                return item;
//            }
//        }
//        return null;
//    }
//
//    public static void addItem(String name, String login, String password, boolean isGroup){
//        Item item = new Item();
//        item.setId(index++);
//        item.setName(name);
//        item.setLogin(login);
//        item.setPassword(password);
//        item.setGroup(isGroup);
//        getItems().add(item);
//    }
//
//
//    public static void addSubItem(int groupId, String name, String login, String password){
//        for(Item item : getItems()){
//            if(item.getId() == groupId && item.isGroup()){
//                Item subItem = new Item();
//                subItem.setId(index++);
//                subItem.setName(name);
//                subItem.setLogin(login);
//                subItem.setPassword(password);
//                item.getSubItems().add(subItem);
//                break;
//            }
//        }
//    }
//
//
//    public static Item getItemById(int id){
//        for(Item item : getItems()){
//            if(item.getId() == id){
//                return item;
//            }
//        }
//        return null;
//    }


    public static boolean hasText(String value){
        if(value != null && !value.isEmpty()){
            return true;
        }
        return false;
    }


}
