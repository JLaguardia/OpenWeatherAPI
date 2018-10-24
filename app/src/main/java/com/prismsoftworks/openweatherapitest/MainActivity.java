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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prismsoftworks.openweatherapitest.fragments.CityDetailFragment;
import com.prismsoftworks.openweatherapitest.fragments.HelpFragment;
import com.prismsoftworks.openweatherapitest.fragments.MapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.prismsoftworks.openweatherapitest.model.city.CityItem;
import com.prismsoftworks.openweatherapitest.model.city.UnitType;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.model.list.ListItemState;
import com.prismsoftworks.openweatherapitest.object.CityViewHolder;
import com.prismsoftworks.openweatherapitest.service.CityListService;
import com.prismsoftworks.openweatherapitest.service.RecyclerTouchHelper;
import com.prismsoftworks.openweatherapitest.task.PullTask;
import com.prismsoftworks.openweatherapitest.task.TaskCallback;

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
    private final String HELP_FRAG_KEY = "helpFrag";
    private final String FRAGTAG_KEY = "activeFragId";
    private Set<CityListItem> savedCities = new HashSet<>();
    private FragmentManager mFragMan = null;
    private MapFragment mMapFragment;
    private CityDetailFragment mCityDetailFragment;
    private HelpFragment mHelpFragment;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;
    private FloatingActionButton mSettingsBtn;
    private boolean appInit = false;
    private String activeFragTag = "";
    private String weatherApiKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragMan = getSupportFragmentManager();
        if(savedInstanceState != null){
            mCityDetailFragment = (CityDetailFragment) mFragMan.getFragment(savedInstanceState,
                    DETAIL_FRAG_KEY);
            mHelpFragment = (HelpFragment) mFragMan.getFragment(savedInstanceState, HELP_FRAG_KEY);
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
        weatherApiKey = getString(R.string.open_weather_key);
        if(!weatherApiKey.equals("")){
            CityListService.getInstance().setWeatherApiKey(weatherApiKey);
        }
        ((LinearLayout)findViewById(R.id.mainContainer)).setOrientation(getResources()
                                                                .getConfiguration().orientation);
        mFragMan = getSupportFragmentManager();
        mDrawerLayout = findViewById(R.id.drawer);
        mNavView = findViewById(R.id.navView);
        init();
    }

    private CityListItem populateCity(final CityListItem item, UnitType preferredUnit){
        Set<LatLng> set = new HashSet<>();
        set.add(item.getCoordinates());
        PullTask.getInstance().getWeatherCityJson(set, preferredUnit, callback(item));
        return item;
    }

    private TaskCallback callback(final CityListItem item){
        return new TaskCallback() {
            @Override
            public void callback(Bundle response) {
                String json = response.getString(PullTask.JSON_KEY);
                Gson gson = new GsonBuilder().create();
                CityItem cityItem = gson.fromJson(json, CityItem.class);
                item.setCityItem(cityItem);
            }
        };
    }

    private void init(){
        SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
        final UnitType preferedUnit = UnitType.valueOf(pref.getString(UNITS_KEY,
                UnitType.IMPERIAL.name()));

        MenuItem unitItem =  mNavView.getMenu().getItem(1);
        Switch sw = unitItem.getActionView().findViewById(R.id.swUnit);

        sw.setChecked(preferedUnit == UnitType.IMPERIAL);
        unitItem.setChecked(sw.isChecked());

        int unitId = (sw.isChecked() ? R.string.imperial_measurement : R.string.metric_measurement);
        ((TextView)((ViewGroup)sw.getParent()).findViewById(R.id.lblUnitChoose)).setText(unitId);

        CityListService.getInstance().setUnits(preferedUnit);
        final String citiesCsv = pref.getString(CITIES_KEY, "");
        if(!citiesCsv.equals("")){
            for(String city : citiesCsv.split("\\;")){
                String[] info = city.split(",");
                LatLng coor = new LatLng(Double.parseDouble(info[1]),
                        Double.parseDouble(info[2]));
                CityListItem saved = new CityListItem(info[0], coor);
                savedCities.add(populateCity(saved, preferedUnit));
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
        mMapFragment = new MapFragment();

        if(isCurrentFragment(mCityDetailFragment)) {
            setBarWeight(0);
            mCityDetailFragment = new CityDetailFragment().setCityItemFromListItem(
                    mCityDetailFragment.getCityItem());
            replaceFrag(mCityDetailFragment, DETAIL_FRAGMENT_TAG);
        } else if(isCurrentFragment(mHelpFragment)) {
            setBarWeight(0);
            mHelpFragment = new HelpFragment();
            replaceFrag(mHelpFragment, HELPER_FRAGMENT_TAG);
        } else {
            setBarWeight(1);
            replaceFrag(mMapFragment, MAP_FRAGMENT_TAG);
        }

        CityListService.getInstance().addItems(arr);
        savedRec.setLayoutManager(new LinearLayoutManager(this));


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerTouchHelper(0, ItemTouchHelper.LEFT , this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(savedRec);
//        savedRec.setAdapter(CityListService.getInstance().getAdapter());

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
                ((TextView)((ViewGroup)btn.getParent()).findViewById(R.id.lblUnitChoose))
                        .setText(unitId);
                CityListService.getInstance().setUnits(b ? UnitType.IMPERIAL : UnitType.METRIC);
            }
        });
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
        if(mHelpFragment != null && mHelpFragment.isAdded()){
            mFragMan.putFragment(outState, HELP_FRAG_KEY, mHelpFragment);
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

    public void clearRecycler(){
        RecyclerView rec = findViewById(R.id.markerRecycler);
        rec.removeAllViewsInLayout();
        rec.setAdapter(null);
        rec.setAdapter(CityListService.getInstance().getAdapter());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        final CityListItem deletedItem = CityListService.getInstance().getList()
                .get(((CityViewHolder) viewHolder).itemIndex);
        String itemName = deletedItem.getName() + " ";

        deletedItem.setState(ListItemState.DELETED);
        CityListService.getInstance().addItems(deletedItem);
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.mainContainer), itemName + getResources().getText(R.string.deleted),
                        Snackbar.LENGTH_LONG);
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
            case R.id.miHelp:
                mHelpFragment = new HelpFragment();
                setBarWeight(0);
                replaceFrag(mHelpFragment, HELPER_FRAGMENT_TAG);
                mDrawerLayout.closeDrawers();
                return true;
            case R.id.miUnitToggle:
                item.setChecked(!item.isChecked());
                ((Switch)item.getActionView().findViewById(R.id.swUnit)).setChecked(item.isChecked());
                return true;
            case R.id.miDelete:
                CityListService.getInstance().clearBookmarks();
                Snackbar.make(mDrawerLayout, getResources().getString(R.string.all_deleted),
                        Snackbar.LENGTH_SHORT).show();
                mDrawerLayout.closeDrawers();
                break;
        }
        return true;
    }

    public void displayNetworkError(CityListItem item){
        String msg = getResources().getText(R.string.network_error).toString();
        boolean errMsg = false;
        if(item.getErrorMessage() != null && !item.getErrorMessage().equals("")){
            errMsg = true;
            msg += " Details of error: \n" + item.getErrorMessage();
        }
        Snackbar snack = Snackbar.make(mDrawerLayout, msg, Snackbar.LENGTH_LONG);

        if(errMsg){
            ((TextView)snack.getView().findViewById(android.support.design.R.id.snackbar_text))
                    .setMaxLines(10);
        }
        snack.show();
    }
}
