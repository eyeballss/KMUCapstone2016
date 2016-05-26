package helper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by kesl on 2016-05-04.
 */
public class DataSaver {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public DataSaver(){
        pref = PreferenceManager.getDefaultSharedPreferences(StaticManager.applicationContext);
        editor = pref.edit();
    }

    public void setData(String key, String val){
        editor.putString(key, val);
    }
    public void setData(String key, int val){
        editor.putInt(key, val);
    }
    public void setData(String key, boolean val){
        editor.putBoolean(key, val);
    }
    public void setData(String key, long val){
        editor.putLong(key, val);
    }

    public String getData(String key, String def){
        return pref.getString(key, def);
    }
    public int getData(String key, int def){
        return pref.getInt(key, def);
    }
    public boolean getData(String key, boolean def){
        return pref.getBoolean(key, def);
    }
    public long getData(String key, long def){
        return pref.getLong(key, def);
    }


    public void commit(){
        editor.commit();
    }


}
