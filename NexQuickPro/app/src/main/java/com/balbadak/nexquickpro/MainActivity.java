package com.balbadak.nexquickpro;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.balbadak.nexquickpro.vo.ListViewItem;
import com.balbadak.nexquickpro.vo.OnDelivery;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;


public class
MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    String mainUrl;
    //폰트관련 설정
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    ArrayList<ListViewItem> quickList;
    ArrayList<OnDelivery> list;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SharedPreferences loginInfo;
    private SharedPreferences.Editor editor;
    private String qpName;
    private int qpId;
    private int onWork;
    TextView nav_header_title;
    TextView nav_header_contents;
    Switch onWorkSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainUrl = getResources().getString(R.string.main_url);
        loginInfo = getSharedPreferences("setting", 0);
        editor = loginInfo.edit();

        qpName = loginInfo.getString("qpName", "");
        qpId = loginInfo.getInt("qpId", 0);
        onWork = loginInfo.getInt("onWork", 0);
        quickList = new ArrayList<>();
        list = new ArrayList<>();

        initQuickList();
        initNavi();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
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



    // 내비게이션 메뉴 관련 (인텐트 설정 - 메뉴이름을 바꾸려면 activity_navigation_drawer.xml로 가시오
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.main_menu) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.order_list_before) {
            Intent intent = new Intent(getApplicationContext(), OrderListBeforeActivity.class);
            startActivity(intent);
        } else if (id == R.id.update_info) {
            Intent intent = new Intent(getApplicationContext(), UpdateUserActivity.class);
            startActivity(intent);
        } else if (id == R.id.deviceManager) {
            Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
            startActivity(intent);
        } else if (id == R.id.getOffWork){
            Intent intent = new Intent(getApplicationContext(), GoToWorkActivity.class);
            editor.remove("onWork");
            editor.commit();
            Intent i = new Intent(getApplicationContext(),LocationService.class);
            stopService(i);
            Intent i2 = new Intent(getApplicationContext(), BluetoothService.class);
            stopService(i2);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.logout) {
            editor.remove("qpId");
            editor.remove("qpName");
            editor.remove("qpPhone");
            editor.remove("qpDeposit");
            editor.remove("rememberId");
            editor.remove("rememberPassword");
            editor.remove("onWork");
            editor.commit();
            Intent i = new Intent(getApplicationContext(),LocationService.class);
            stopService(i);
            Intent i2 = new Intent(getApplicationContext(), BluetoothService.class);
            stopService(i2);
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void initQuickList(){

        String url = mainUrl + "list/optimalRoute.do";

        ContentValues values = new ContentValues();
        values.put("qpId", qpId);
        // AsyncTask를 통해 HttpURLConnection 수행.
        GetListTask getListTask = new GetListTask(url, values);
        getListTask.execute();
    }

    public void initNavi(){
        // Adding Toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View nav_header_view = navigationView.getHeaderView(0);


        nav_header_title = (TextView) nav_header_view.findViewById(R.id.nav_header_title);
        nav_header_contents = (TextView) nav_header_view.findViewById(R.id.nav_header_contents);
        nav_header_title.setText(qpName+" 퀵프로님");


        onWorkSwitch = (Switch) nav_header_view.findViewById(R.id.onWorkSwitch);

        if(onWork == 2) {
            //퇴근상태이면 들어갈 각종 디폴트 상황들, oncreat에서만 로딩됨
            onWorkSwitch.setChecked(false);
            nav_header_contents.setText("운행정지");

        } else if (onWork == 1) {
            //출근상태라면 들어갈 각종 디폴트 상황들, oncreat에서만 로딩됨
            onWorkSwitch.setChecked(true);
            nav_header_contents.setText("운행중");

        } else{
            onWorkSwitch.setChecked(false);
            onWorkSwitch.setEnabled(false);
            nav_header_contents.setText("출근 전");
        }

        if (onWork != -1){
            onWorkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                //스위치 토글시 바뀔 내용들
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Intent i = new Intent(getApplicationContext(), LocationService.class);
                    if(isChecked) {
                        editor.putInt("onWork", 1); //프리퍼런스 값 바꿈
                        editor.commit();
                        nav_header_contents.setText("운행중");
                        String url = mainUrl +"qpAccount/changeQPStatus.do";
                        ContentValues values = new ContentValues();
                        values.put("qpId", qpId);
                        values.put("qpStatus", 0);
                        QPStatusTask qpStatusTask = new QPStatusTask(url, values);
                        qpStatusTask.execute();

                    } else {
                        editor.putInt("onWork", 2); //프리퍼런스 값 바꿈
                        editor.commit();
                        nav_header_contents.setText("운행정지");
                        String url = mainUrl +"qpAccount/changeQPStatus.do";
                        ContentValues values = new ContentValues();
                        values.put("qpId", qpId);
                        values.put("qpStatus", 1);
                        QPStatusTask qpStatusTask = new QPStatusTask(url, values);
                        qpStatusTask.execute();
                    }
                }
            });
        }

    }

    public class TabPagerAdapter extends FragmentStatePagerAdapter {


        // Count number of tabs
        private int tabCount;

        public TabPagerAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            this.tabCount = tabCount;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment=null;
            Bundle bundle;

            // Returning the current tabs
            switch (position) {
                case 0:
                    fragment = new fragment_order_list();
                    bundle = new Bundle();
                    bundle.putParcelableArrayList("quickList", quickList);
                    bundle.putParcelableArrayList("list", list);
                    fragment.setArguments(bundle);
                    break;
                case 1:
                    fragment = new fragment_route();
                    bundle = new Bundle();
                    bundle.putParcelableArrayList("quickList", quickList);
                    bundle.putParcelableArrayList("list", list);
                    fragment.setArguments(bundle);
                    break;
                case 2:
                    fragment = new fragment_calculate();
                    break;
                default:

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }




    // 여기부터 AsyncTask 영역
    public class GetListTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public GetListTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            StringBuilder titleSb = new StringBuilder();
            StringBuilder descSb = new StringBuilder();
            super.onPostExecute(s);


            if (s != null) {
                try {
                    JSONArray ja = new JSONArray(s);
                    JSONObject data;
                    OnDelivery order;
                    HashSet<Integer> callNumSet = new HashSet<>();
                    for (int i = 0; i < ja.length(); i++) {
                        order = new OnDelivery();
                        data = ja.getJSONObject(i);
                        ListViewItem item = new ListViewItem();
                        titleSb.setLength(0);
                        descSb.setLength(0);

                        order.setUrgent(data.getInt("urgent"));
                        order.setOrderNum(data.getInt("orderNum"));
                        order.setCallNum(data.getInt("callNum"));
                        order.setCallTime(data.getString("callTime"));
                        order.setOrderPrice(data.getInt("orderPrice"));
                        order.setMemo(data.getString("memo"));
                        order.setDeliveryStatus(data.getInt("deliveryStatus"));
                        order.setFreightList(data.getString("freightList"));

                        if (order.getDeliveryStatus() == 2) {
                            if (order.getUrgent() == 1) {
                                titleSb.append("급/");
                                item.setUrgentStr("급");
                            }

                            order.setSenderName(data.getString("senderName"));
                            order.setSenderPhone(data.getString("senderPhone"));
                            order.setSenderAddress(data.getString("senderAddress"));
                            order.setSenderAddressDetail(data.getString("senderAddressDetail"));
                            order.setLatitude(data.getString("senderLatitude"));
                            order.setLongitude(data.getString("senderLongitude"));


                            if(!callNumSet.contains(order.getCallNum())) {

                                callNumSet.add(order.getCallNum());

                                titleSb.append("픽/");
                                titleSb.append(order.getSenderAddress());

                                if(order.getFreightList()!=null) descSb.append(order.getFreightList());
                                descSb.append("/");
                                descSb.append(order.getOrderPrice());

                                item.setTitleStr(titleSb.toString());
                                item.setDescStr(descSb.toString());
                                item.setCallNum(order.getCallNum());
                                item.setOrderNum(order.getOrderNum());
                                item.setSenderPhone(order.getSenderPhone());
                                item.setQuickType(1);

                                list.add(order);
                                quickList.add(item);
                            }

                        } else if (order.getDeliveryStatus() == 3) {
                            if (order.getUrgent() == 1) {
                                titleSb.append("급/");
                                item.setUrgentStr("급");
                            }
                            order.setReceiverName(data.getString("receiverName"));
                            order.setReceiverPhone(data.getString("receiverPhone"));
                            order.setReceiverAddress(data.getString("receiverAddress"));
                            order.setReceiverAddressDetail(data.getString("receiverAddressDetail"));
                            order.setLatitude(data.getString("receiverLatitude"));
                            order.setLongitude(data.getString("receiverLongitude"));

                            titleSb.append("착/");
                            titleSb.append(order.getReceiverAddress());

                            if(data.getString("freightList")!=null) descSb.append(data.getString("freightList"));
                            descSb.append("/");
                            descSb.append(data.getString("orderPrice"));

                            item.setTitleStr(titleSb.toString());
                            item.setDescStr(descSb.toString());
                            item.setCallNum(order.getCallNum());
                            item.setOrderNum(order.getOrderNum());
                            item.setReceiverPhone(order.getReceiverPhone());
                            item.setQuickType(2);

                            list.add(order);
                            quickList.add(item);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

            }

            // Initializing the TabLayout
            tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            tabLayout.addTab(tabLayout.newTab().setText("퀵리스트").setIcon(R.drawable.tab_apply_detail_black));
            tabLayout.addTab(tabLayout.newTab().setText("경로안내").setIcon(R.drawable.tab_address_detail));
            tabLayout.addTab(tabLayout.newTab().setText("정산하기").setIcon(R.drawable.tab_complete_black));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            // Initializing ViewPager
            viewPager = (ViewPager) findViewById(R.id.pager);

            // Creating TabPagerAdapter adapter
            TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(pagerAdapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            // Set TabSelectedListener
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                    switch(tab.getPosition()) {
                        case 0:
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.colorGold));
                            tab.setIcon(R.drawable.tab_apply_detail_gold);
                            break;
                        case 1:
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.colorGold));
                            tab.setIcon(R.drawable.tab_address_detail_gold);
                            break;
                        case 2:
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.colorGold));
                            tab.setIcon(R.drawable.tab_completed_gold);
                            break;
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    switch(tab.getPosition()) {
                        case 0:
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.colorGold));
                            tab.setIcon(R.drawable.tab_apply_detail_black);
                            break;
                        case 1:
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.colorGold));
                            tab.setIcon(R.drawable.tab_address_detail);
                            break;
                        case 2:
                            tabLayout.setTabTextColors(getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.colorGold));
                            tab.setIcon(R.drawable.tab_complete_black);
                            break;
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            viewPager.setCurrentItem(1); // routeActivity (경로화면)이 디폴트


        }
    }

    public class QPStatusTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public QPStatusTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

        }
    }

}



