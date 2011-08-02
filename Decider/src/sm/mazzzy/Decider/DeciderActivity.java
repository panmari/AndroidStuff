package sm.mazzzy.Decider;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class DeciderActivity extends ListActivity {
    
    private NotesDbAdapter mDbHelper;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();
        
        final ListView listView = getListView();

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
	
    private void fillData() {
        // Get all of the rows from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
            new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice, 
            		notesCursor, from, to);
        setListAdapter(notes);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_addLoc);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void createNote() {
        Intent i = new Intent(this, addLocation.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}