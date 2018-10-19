package com.prismsoftworks.openweatherapitest;

import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.prismsoftworks.openweatherapitest.model.city.UnitType;
import com.prismsoftworks.openweatherapitest.model.list.CityListItem;
import com.prismsoftworks.openweatherapitest.model.list.ListItemState;
import com.prismsoftworks.openweatherapitest.object.CityViewHolder;
import com.prismsoftworks.openweatherapitest.service.CityListService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CityListServiceInstrumentationTest {

    private CityListService service;
    private RecyclerView.Adapter<CityViewHolder> cityViewHolderAdapter;
    private List<CityListItem> items;
    private CityListItem item;


    @Before
    public void setup() {
        service = CityListService.getInstance();
        cityViewHolderAdapter = service.getAdapter();
        item = new CityListItem();
        item.setName("TestItem");
        item.setCoordinates(new LatLng(200, 200));
        item.setChosenUnitType(UnitType.METRIC);
        item.setState(ListItemState.INSERTED);
    }

    @Test
    public void verifyListExists() {
        assertNotNull(service.getList());
    }

    @Test
    public void verifyListIsEmpty() {
        assertEquals(0, service.getList().size());
    }

    @Test
    public void verifyAdapterExists() {
        assertNotNull(cityViewHolderAdapter);
    }

    @Test
    public void verifyItemCountIsZero() {
        assertEquals(0, cityViewHolderAdapter.getItemCount());
    }

    @Test
    public void verifyItemName() {
        assertNotNull(item.getName());
        assertEquals("TestItem", item.getName());
    }

    @Test
    public void verifyItemCoordinates() {
        assertNotNull(item.getCoordinates());
        assertEquals(new LatLng(200, 200), item.getCoordinates());
    }

    @Test
    public void verifyItemUnitType() {
        assertNotNull(item.getChosenUnitType());
        assertEquals(UnitType.METRIC, item.getChosenUnitType());
    }

    @Test
    public void verifyItemState() {
        assertNotNull(item.getState());
        assertEquals(ListItemState.INSERTED, item.getState());
    }

    @Test
    public void verifyCityItemExists() {
        assertNotNull(item.getCityItem());
    }

    @Test
    public void verifyCitListServiceExists() {
        assertNotNull(service);
    }

    @Test
    public void verifyMetricUnits() {
        assertEquals("50mm", service.getLengthMeasureString(item, "50"));
    }

    @Test
    public void verifyTemperature(){
        double temperature = item.getCityItem().getTemperature().getTemperature();
        assertEquals( temperature + "°C", service.getTemperatureString(item));
    }

}
