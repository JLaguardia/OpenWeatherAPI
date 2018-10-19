package com.prismsoftworks.openweatherapitest;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.prismsoftworks.openweatherapitest.fragments.CityDetailFragment;
import com.prismsoftworks.openweatherapitest.fragments.MapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.prismsoftworks.openweatherapitest.model.city.UnitType;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.model.list.ListItemState;
import com.prismsoftworks.openweatherapitest.object.CityViewHolder;
import com.prismsoftworks.openweatherapitest.service.CityListService;
import com.prismsoftworks.openweatherapitest.service.RecyclerTouchHelper;
import com.prismsoftworks.openweatherapitest.task.PullTask;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends FragmentActivity implements RecyclerTouchHelper.RecyclerItemTouchHelperListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String CITIES_KEY = "storedCities";
    public static final String UNITS_KEY = "storedUnit";
    public static final String MAP_FRAGMENT_TAG = "map";
    public static final String DETAIL_FRAGMENT_TAG = "detail";
    public static final String HELPER_FRAGMENT_TAG = "help";
    private final String DETAIL_FRAG_KEY = "detailFrag";
    private final String FRAGTAG_KEY = "activeFragId";
    private Set<CityListItem> savedCities = new HashSet<>();
    private FragmentManager mFragMan = null;
    private MapFragment mMapFragment;
    private CityDetailFragment mCityDetailFragment;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;
    private FloatingActionButton mSettingsBtn;
    private boolean appInit = false;
    private String activeFragTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragMan = getSupportFragmentManager();
        if(savedInstanceState != null){
            mCityDetailFragment = (CityDetailFragment) mFragMan.getFragment(savedInstanceState, DETAIL_FRAG_KEY);
            activeFragTag = savedInstanceState.getString(FRAGTAG_KEY);
            CityListService.getInstance().registerContext(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        CityListService.getInstance().clearContext();
        setContentView(R.layout.activity_main);
        CityListService.getInstance().registerContext(this);
        ((LinearLayout)findViewById(R.id.mainContainer)).setOrientation(getResources()
                                                                .getConfiguration().orientation);
        mFragMan = getSupportFragmentManager();
        mDrawerLayout = findViewById(R.id.drawer);
        mNavView = findViewById(R.id.navView);
        init();
    }

    private void init(){
        SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
        final UnitType preferedUnit = UnitType.valueOf(pref.getString(UNITS_KEY, UnitType.IMPERIAL.name()));

        MenuItem unitItem =  mNavView.getMenu().getItem(1);
        Switch sw = unitItem.getActionView().findViewById(R.id.swUnit);

        sw.setChecked(preferedUnit == UnitType.IMPERIAL);
        unitItem.setChecked(sw.isChecked());

        int unitId = (sw.isChecked() ? R.string.imperial_measurement : R.string.metric_measurement);
        ((TextView)((ViewGroup)sw.getParent()).findViewById(R.id.lblUnitChoose)).setText(unitId);

        CityListService.getInstance().setUnits(preferedUnit);
        final String citiesCsv = pref.getString(CITIES_KEY, "");
        if(!citiesCsv.equals("")){ // "Orlando,123.44,-100;Atlanta,345.11,-80"
            for(String city : citiesCsv.split(";")){
                String[] info = city.split(",");
                LatLng coor = new LatLng(Double.parseDouble(info[1]),
                        Double.parseDouble(info[2]));
                CityListItem saved = new CityListItem(info[0], coor);
                savedCities.add(saved);
            }
        }

        final RecyclerView savedRec = findViewById(R.id.markerRecycler);
        CityListItem[] arr = new CityListItem[savedCities.size()];
        arr = savedCities.toArray(arr);
        if(arr.length == 0){
            arr = new CityListItem[1];
            arr[0] = null;
        }

        mSettingsBtn = findViewById(R.id.btnSettings);
        //add map frag
        mMapFragment = new MapFragment();//.setCities(savedCities);

        if(isCurrentFragment(mCityDetailFragment)) {
            setBarWeight(0);
            mCityDetailFragment = new CityDetailFragment().setCityItemFromListItem(
                    mCityDetailFragment.getCityItem());
            replaceFrag(mCityDetailFragment, DETAIL_FRAGMENT_TAG);
        } else {
            setBarWeight(1);
            replaceFrag(mMapFragment, MAP_FRAGMENT_TAG);
        }

        CityListService.getInstance().addItems(arr);
        savedRec.setLayoutManager(new LinearLayoutManager(this));


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerTouchHelper(0, ItemTouchHelper.LEFT , this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(savedRec);
        savedRec.setAdapter(CityListService.getInstance().getAdapter());

        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                    mDrawerLayout.closeDrawers();
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        mNavView.setNavigationItemSelectedListener(this);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean b) {
                int unitId = (b ? R.string.imperial_measurement : R.string.metric_measurement);
                ((TextView)((ViewGroup)btn.getParent()).findViewById(R.id.lblUnitChoose)).setText(unitId);
                CityListService.getInstance().setUnits(b ? UnitType.IMPERIAL : UnitType.METRIC);
            }
        });

//        mSearchview = findViewById(R.id.svCities);
//        mSearchview.setListener(new SearchEdit() {
//            @Override
//            public void onQueryTextChanged(String query) {
//                CityListService.getInstance().getAdapter().filterQuery(query);
//                savedRec.scrollToPosition(0);
//            }
//        });
//        mSearchview.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean b) {
//                if(b){
//                    setBarWeight(9);
//                    Log.e("sv", "setting barweight 9");
//                } else {
//                    Log.e("sv", "setting barweight 1");
//                    setBarWeight(1);
//                }
//            }
//        });

//        mSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                CityListService.getInstance().getAdapter().filterQuery(query);
//                savedRec.scrollToPosition(0);
//                return true;
//            }
//        });
    }

    private boolean isCurrentFragment(Fragment fragment){
        return(fragment != null && fragment.isAdded() && fragment.getTag().equals(activeFragTag));
    }

    @Override
    protected void onPause() {
        super.onPause();
        CityListService.getInstance().clearContext();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PullTask.getInstance().stopTasks();
        outState.putString(FRAGTAG_KEY, activeFragTag);
        if(mCityDetailFragment != null && mCityDetailFragment.isAdded()){
            mFragMan.putFragment(outState, DETAIL_FRAG_KEY, mCityDetailFragment);
        }
    }

    private void replaceFrag(Fragment frag, String tag){
        mSettingsBtn.setVisibility(frag instanceof MapFragment ? View.VISIBLE : View.INVISIBLE);
        FragmentTransaction ft = mFragMan.beginTransaction();
        ft.replace(R.id.testFragContainer, frag, tag);
        if(appInit) {
            ft.addToBackStack(null);
        }

        ft.commit();
        appInit = true;
        activeFragTag = frag.getTag();
    }

    public void setBarWeight(int weight){
        AppBarLayout bar = findViewById(R.id.main_appbar);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bar.getLayoutParams();
        lp.weight = weight;
        bar.setLayoutParams(lp);
    }

    @Override
    public void onBackPressed() {
        PullTask.getInstance().stopTasks();
        if(mFragMan.getBackStackEntryCount() > 0) {
            if(mMapFragment.isAdded()){
                setBarWeight(0);
            } else {
                setBarWeight(1);
            }

            mFragMan.popBackStackImmediate();
            activeFragTag = mFragMan.findFragmentById(R.id.testFragContainer).getTag();
            mSettingsBtn.setVisibility(activeFragTag.equals("map") ? View.VISIBLE : View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    public void showCityDetails(CityListItem city){
        //TODO find a way to optimize this... we shouldnt have to set this to null and then start again
        mCityDetailFragment = null;
        mCityDetailFragment = new CityDetailFragment().setCityItemFromListItem(city);
        setBarWeight(0);
        replaceFrag(mCityDetailFragment, DETAIL_FRAGMENT_TAG);
    }

//    public void handleSearchFocus(){
//        mSearchview.clearFocus();
//        findViewById(R.id.contentFrame).requestFocus();
//        setBarWeight(1);
//    }

    public void clearRecycler(){
        RecyclerView rec = findViewById(R.id.markerRecycler);
        rec.removeAllViewsInLayout();
        rec.setAdapter(null);
        rec.setAdapter(CityListService.getInstance().getAdapter());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        final CityListItem deletedItem = CityListService.getInstance().getList().get(((CityViewHolder) viewHolder).itemIndex);
        String itemName = deletedItem.getName() + " ";

        deletedItem.setState(ListItemState.DELETED);
        CityListService.getInstance().addItems(deletedItem);
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.mainContainer), itemName + getResources().getText(R.string.deleted), Snackbar.LENGTH_LONG);
        snackbar.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletedItem.setState(ListItemState.INSERTED);
                CityListService.getInstance().addItems(deletedItem);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.miDelete:
                CityListService.getInstance().clearBookmarks();
                break;
            case R.id.miUnitToggle:
                item.setChecked(!item.isChecked());
                ((Switch)item.getActionView().findViewById(R.id.swUnit)).setChecked(item.isChecked());
                return true;
        }
        Snackbar.make(mDrawerLayout, getResources().getString(R.string.all_deleted),
                Snackbar.LENGTH_SHORT).show();
        mDrawerLayout.closeDrawers();
        return true;
    }

    public void displayNetworkError(){
        Snackbar.make(mDrawerLayout, getResources().getText(R.string.network_error),
                Snackbar.LENGTH_SHORT).show();
    }
}

    /**
     * sample:
     // https://samples.openweathermap.org/data/2.5/weather?lat=28.67&lon=-81.42&appid=b6907d289e10d714a6e88b30761fae22

     {
         "coord": {
             "lon": 139.01,
             "lat": 35.02
     },
         "weather": [
         {
             "id": 800,
             "main": "Clear",
             "description": "clear sky",
             "icon": "01n"
         }
     ],
         "base": "stations",
         "main": {
             "temp": 285.514,
             "pressure": 1013.75,
             "humidity": 100,
             "temp_min": 285.514,
             "temp_max": 285.514,
             "sea_level": 1023.22,
             "grnd_level": 1013.75
             },
         "wind": {
             "speed": 5.52,
             "deg": 311
             },
     "clouds": {
     "all": 0
     },
     "dt": 1485792967,
     "sys": {
     "message": 0.0025,
     "country": "JP",
     "sunrise": 1485726240,
     "sunset": 1485763863
     },
     "id": 1907296,
     "name": "Tawarano",
     "cod": 200
     }
     */

