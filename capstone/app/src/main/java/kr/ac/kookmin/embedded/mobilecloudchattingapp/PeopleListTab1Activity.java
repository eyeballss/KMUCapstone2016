package kr.ac.kookmin.embedded.mobilecloudchattingapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import helper.HttpConnection;
import helper.Message;
import helper.StaticManager;
import listViewAdapter.ListViewAdapter_MainTab1;

/**
 * Created by kesl on 2016-05-05.
 */
public class PeopleListTab1Activity extends LinearLayout {

    static boolean singleton = false;
    View rootView;
    ListView listView;
//    Button listViewRefreshBtn;
    ListViewAdapter_MainTab1 listViewAdapterMainTab1;
    String data = ""; //하드코딩한 녀석
    ArrayList<String> name; //리스트에 들어가는 데이터
    ArrayList<String> distance; //리스트에 들어가는 데이터
    HttpConnection httpConnection;
    HashMap<String, String> commentMap;
    Context peopleListTabContext;

    //여기서 이 레이아웃이 할 일을 지정함.
    private void work(final Context context) {
        peopleListTabContext = context;

        name = new ArrayList<String>();
        distance = new ArrayList<String>();
        httpConnection = new HttpConnection();
        commentMap = new HashMap<String, String>();

//        listViewRefreshBtn = (Button)findViewById(R.id.listViewRefreshBtn);

        String[] key = {"ID"};
        String[] val = {
                StaticManager.uniqeNum
        };
        //db_login.php에 로그인 요청을 보냄. 결과는 브로드캐스트 리비서에서 받을 것임.
        LocalBroadcastManager.getInstance(context).registerReceiver(mLocalBroadcastReceiver, new IntentFilter("localBroadCast"));
        httpConnection.connect("http://" + StaticManager.ipAddress + "/getClosePeople.php", "getClosePeople.php", key, val);



//        listViewRefreshBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listViewSetting();
//            }
//        });

    }//work


    //서버에서 가져온 값을 알려주는 브로드캐스트 리시버
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("PeopleList Activity", "mLocalBroadcastReceiver call");

            final String message = intent.getStringExtra("getClosePeople.php");
            if (message != null) {
                data = message;


                dataParser();


                //아래는 리스트뷰 세팅---------------
                listView = (ListView) findViewById(R.id.listViewMainTab1);
                //이렇게 어댑터를 생성하고 나면 리스트 다루는 일은 어댑터가 도맡아 한다.
                listViewAdapterMainTab1 = new ListViewAdapter_MainTab1((Activity) peopleListTabContext, name, distance, commentMap);
                //리스트뷰는 단지 보여주는 역할만 할 뿐.
                listView.setAdapter(listViewAdapterMainTab1);
                //리스트뷰의 아이템을 클릭하면..!!!!
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String msg =
                                "name : \n" + name.get(position) + "\n" +
                                        "distance : \n" + distance.get(position) + "km\n" +
                                        "comment : \n" + commentMap.get(name.get(position).trim()) + "\n\n" +
                                        "do you want to chat?\n";
                        Message.yesNoMsgShow(msg, "chatting_dialog", name.get(position), "no", peopleListTabContext);
                    }
                });
            }
            final String message2 = intent.getStringExtra("chatting_dialog");
            if (message2 != null) {
                if (message2.equals("no")) {
                    StaticManager.testToastMsg("싫구나..");
                } else {
                    StaticManager.testToastMsg(message2 + "랑 대화하자!");

                    Intent in = new Intent(context, ChattingActivity.class);
                    in.putExtra("oppoNickname", message2);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(in); //이쪽으로 가서 채팅.

                    LocalBroadcastManager.getInstance(context).unregisterReceiver(mLocalBroadcastReceiver);
                }
            }


            Log.d("LoginActivity", "local broadcast receiver works");
        }
    };


    private void listViewSetting(){
        listViewAdapterMainTab1.notifyDataSetChanged();
    }

    //데이터를 리스트에 정렬해서 넣어주는 녀석
    private void dataParser() {
        HashMap<String, Double> map = new HashMap<String, Double>();
        StringTokenizer token = new StringTokenizer(data, "*");
        Log.d("PeopleList Activity", data + "를 파싱합니다.");
        while (token.hasMoreTokens()) {
            String name = token.nextToken();
            double dis = Double.valueOf(token.nextToken());
            String com = token.nextToken();

            Log.d("PeopleList Activity", name + "과 " + dis + "과 " + com);
            commentMap.put(name, com);
            map.put(name, dis);

        }//while

        Iterator it = StaticManager.sortByValue(map).iterator();
        while (it.hasNext()) {
            String temp = (String) it.next();
            name.add(temp + "\t");
            distance.add(String.valueOf(map.get(temp)));

            Log.d("PeopleList Activity", "파싱 정렬 후 name의 크기 : " + name.size() + " dis의 크기 : " + distance.size());
//            System.out.println(temp + " = " + map.get(temp));
        }

    }


    //아래는 세팅하는 부분

    public PeopleListTab1Activity(Context context) { //생성자
        super(context);
        init(context);
    }

    public PeopleListTab1Activity(Context context, AttributeSet attrs) { //생성자
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        if (singleton) {
            singleton = false;
        } else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //첫번째 : xml 파일, 두번째: 가서 붙을 곳, 세번째 : t면 바로 붙고 f면 필요할 때 붙음.
            rootView = inflater.inflate(R.layout.fragment_main_tab1, this, true);
            work(context);
            Log.d("PeopleTab1", "init call");
            singleton = true;
        }
    }

    public View getView() { //inflated View를 돌려줌.
        return rootView;
    }
}