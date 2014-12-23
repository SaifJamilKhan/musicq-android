package com.haystack.saifkhan.haystack.Utils;

import android.text.TextUtils;

import com.haystack.saifkhan.haystack.Models.MusicQBase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by saifkhan on 14-12-14.
 */
public class DatabaseManager {

    private static DatabaseManager mDatabaseManager;
    private HashMap<String, HashMap<String, MusicQBase> > mHashmaps;

    public static DatabaseManager getDatabaseManager(){
        if(mDatabaseManager == null) {
            mDatabaseManager = new DatabaseManager();

        }
        return mDatabaseManager;
    }

    public HashMap getHashmapForClass(Class objectClass) {
       if(mHashmaps == null) {
           mHashmaps = new HashMap<String, HashMap<String, MusicQBase> >();
       }
       return getObjectHashmap(objectClass);
    }

    public void addObject(MusicQBase object) {
        if(mHashmaps == null) {
            mHashmaps = new HashMap<String, HashMap<String, MusicQBase> >();
        }
        HashMap<String, MusicQBase> objectHashMap = getObjectHashmap(object.getClass());
        if(!TextUtils.isEmpty(object.id)) {
            objectHashMap.put(object.id, object);
        }
    }

    private HashMap<String, MusicQBase> getObjectHashmap(Class classOfObject) {
        HashMap<String, MusicQBase> objectHashMap = mHashmaps.get(classOfObject.toString());

        if(objectHashMap == null) {
            objectHashMap = new HashMap<String, MusicQBase> ();
            mHashmaps.put(classOfObject.toString(), objectHashMap);
        }
        return objectHashMap;
    }


}
