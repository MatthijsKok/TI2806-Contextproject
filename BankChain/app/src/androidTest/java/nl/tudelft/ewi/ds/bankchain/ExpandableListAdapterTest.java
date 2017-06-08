package nl.tudelft.ewi.ds.bankchain;


import android.support.test.runner.AndroidJUnit4;
import org.junit.Ignore;
import android.widget.ExpandableListView;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.tudelft.ewi.ds.bankchain.activities.ExpandableListAdapter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Isha Dijcks
 */
@RunWith(AndroidJUnit4.class)
//TRAVIS @Ignore
public class ExpandableListAdapterTest {

    private ExpandableListAdapter expandableListAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    @Test
    public void hasStableIdsTest() {
        expandableListAdapter = new ExpandableListAdapter(null, null, null);
        assertFalse(expandableListAdapter.hasStableIds());
    }

    @Test
    public void isChildSelectableTest() {
        expandableListAdapter = new ExpandableListAdapter(null, null, null);
        assertTrue(expandableListAdapter.isChildSelectable(0, 0));
        assertTrue(expandableListAdapter.isChildSelectable(-1, 1));
    }

    @Test
    public void getChildIdTest() {
        expandableListAdapter = new ExpandableListAdapter(null, null, null);
        assertEquals(0, expandableListAdapter.getChildId(0, 0));
        assertEquals(-1, expandableListAdapter.getChildId(1, -1));
        assertEquals(200, expandableListAdapter.getChildId(-10, 200));
    }

    @Test
    public void fullExpandableListTest() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader.add("Header");
        List<String> details = new ArrayList<>();
        details.add("Entry 1");
        details.add("Entry 2");
        listDataChild.put("Header", details);
        expandableListAdapter = new ExpandableListAdapter(null, listDataHeader, listDataChild);
        assertEquals("Entry 1", expandableListAdapter.getChild(0, 0));
    }

    @Test
    public void getChildrenCountTest() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader.add("Header");
        List<String> details = new ArrayList<>();
        details.add("Entry 1");
        details.add("Entry 2");
        listDataChild.put("Header", details);
        expandableListAdapter = new ExpandableListAdapter(null, listDataHeader, listDataChild);
        assertEquals(2, expandableListAdapter.getChildrenCount(0));
    }
}
