package helper;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by kesl on 2016-05-01.
 */
//static 변수들이나 static 메소드들을 갖고 있음.
public class StaticManager {

    private static Handler handler;
    private static boolean singleton=false;
    public static String uniqeNum;
    public static String id;
    public static String pw;
    public static String nickname;
    public static String comment;
    public static boolean sex; //F면 여자 T이면 남자
    public static boolean checkIfSMHasProfile=false; //개인 정보를 staticManager가 갖고 있다면 true 아니면 false
    public static String ipAddress="52.79.106.222"; //IP 주소
    public static boolean gps=false;


    public static Context applicationContext;

    public static LocationManager locationManager;


//    //로컬 브로드캐스트 생각해보니 intentName이 하나로 고정되어 있어도 괜찮을 듯. key 값으로 구별하면 되니까.
//    public static void sendBroadcast(String intentName, String key, String data) {
//        Intent intent = new Intent(intentName);
//        intent.putExtra(key, data);
//        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
//    }
    //로컬 브로드캐스트
    public static void sendBroadcast(String key, String data) {
        Intent intent = new Intent("localBroadCast");
        intent.putExtra(key, data);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }

    //테스트용 토스트 메세지
    public static void testToastMsg(final String string){
        if(!singleton){
            handler = new Handler(Looper.getMainLooper()); //핸들러는 하나만 있어도 되므로 싱글톤을 사용하여 하나만 생성하도록, 이렇게 해보자.
            singleton=true;
            Log.d("Static Manager", "new handler and singleton is "+singleton);
        }
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(applicationContext, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static List sortByValue(final Map map){
        List<String> list = new ArrayList();
        list.addAll(map.keySet());

        Collections.sort(list, new Comparator() {

            public int compare(Object o1, Object o2) {
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);

                return ((Comparable) v1).compareTo(v2);
            }

        });
//        Collections.reverse(list); // 주석시 오름차순
        return list;
    }

}
