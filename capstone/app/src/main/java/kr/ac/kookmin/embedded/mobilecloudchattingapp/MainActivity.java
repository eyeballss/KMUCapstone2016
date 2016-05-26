package kr.ac.kookmin.embedded.mobilecloudchattingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import helper.HttpConnection;
import helper.LocationService;
import helper.StaticManager;

public class MainActivity extends AppCompatActivity {

    //gps를 사용합니당.
    private LocationService locationService;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀바 없애기
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //프로필을 변경하는 버튼을 누르면
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent in = new Intent(MainActivity.this, EditProfileActivity.class);
                in.putExtra("path", "editProfile");
                startActivity(in);
            }
        });

        //플래그를 조절하는 버튼을 누르면
        FloatingActionButton flagMe = (FloatingActionButton) findViewById(R.id.flagForFindingMe);
        flagMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GPS를 기록하자.
                recodeGPS();
            }
        });

        locationService = new LocationService();
        locationService.startLocationService();
    }

    private void recodeGPS() {
        //gps 받아서 레코딩 함.
        LocationManager locationManager = (LocationManager) getSystemService(StaticManager.applicationContext.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gps) {
            StaticManager.gps = true;
            StaticManager.testToastMsg("Turn on the gps.\nOther people can see you now.");
        } else {
            StaticManager.gps = false;
            updateMyGPS("0 0"); //0으로 내 gps를 업데이트 함.
            StaticManager.testToastMsg("Turn off the gps.\nOther people can't see you now.");

        }
    }

    //로컬 브로드캐스트 해서 받아온 것. 바로 위의 recodeGPS에서 방송 한 거임.
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // db_login.php로 보낸 결과값을 여기서 받음.
            final String message = intent.getStringExtra("gps data");

            updateMyGPS(message); //계속 업데이트 함
//            Intent in;
//            if(message.equals("false")) { //로그인에 실패하면 바로 가입을 위해 EidtProfileActivity로 이동
//                in = new Intent(LoginActivity.this, EditProfileActivity.class);
//                in.putExtra("path", "loginFail");
//                startActivityForResult(in, 1);
//
//
//            }else{ //로그인에 성공하면 MainActivity로 이동.
//                in = new Intent(LoginActivity.this, MainActivity.class);
//                saveProfileToStaticManager(message); //로그인 성공이므로 profile 데이터를 핸드폰에 저장함.
//                startActivity(in);
//                finish(); //로그인 하고 나면 로그인창은 닫습니다.
//            }


            Log.d("MainActivity", "gps data get from broadcast");
        }
    };

    private void updateMyGPS(String data) {
        Log.d("MainActivity", "updateMyGPS data: "+data);
        if(data==null || data.length()<=2) return;
        Log.d("MainActivity", "update gps call");
        String latitude = data.substring(0, data.indexOf(" ")).trim().toString();
        String longitude = data.substring(data.indexOf(" ") + 1).trim().toString();

        String[] key = {"ID", "X", "Y"};
        String[] val = {
                StaticManager.uniqeNum,
                latitude,
                longitude
        };


        //db_updateGPS.php에 업데이트 해달라고 요청함.
        Log.d("MainActivity", val[0] + " " + val[1] + " " + val[2] + " are sent");
        HttpConnection httpConnection = new HttpConnection();
        httpConnection.connect("http://" + StaticManager.ipAddress + "/setCurrentPos.php", "setCurrentPos", key, val);

        Log.d("MainActivity", "send http msg to setCurrentPos");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PEOPLE";
//                case 1:
//                    return "SECTION 2";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView;
            PeopleListTab1Activity tab1 = new PeopleListTab1Activity(getContext());
//            ChattingTab2Activity tab2 = new ChattingTab2Activity(getContext());

//            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                container.addView(tab1);
                rootView = tab1.getView();
//                rootView = inflater.inflate(R.layout.fragment_main_tab1, container, false);
//            }
//            else {
//                container.addView(tab2);
//                rootView = tab2.getView();
//                rootView = inflater.inflate(R.layout.fragment_main_tab2, container, false);
//            }

//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

//            View rootView = inflater.inflate(R.layout.fragment_main_tab1, container, false);

            return rootView;
        }
    }


    public void onResume() {
        super.onResume();
        //이것도 나중에 스태틱으로 바꿔주자. 여기서 특별히 다르게 처리해야 할 것은 없으니까.
        // Register mMessageReceiver to receive messages. 브로드캐스트 리시버 등록
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalBroadcastReceiver, new IntentFilter("localBroadCast"));
    }

    protected void onPause() {
        //이것도 나중에 스태틱으로 바꿔주자. 여기서 특별히 다르게 처리해야 할 것은 없으니까.
        // Unregister since the activity is not visible 브로드캐스트 리비서 해제
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalBroadcastReceiver);
        super.onPause();
    }
}
