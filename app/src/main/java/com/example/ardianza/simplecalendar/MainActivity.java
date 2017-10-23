package com.example.ardianza.simplecalendar;

import android.Manifest;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    long calID = 0;
    long startMillis = 0;
    long endMillis = 0;
    private static final int PERMISSION_CALENDAR_READ = 1;
    private static final int PERMISSION_CALENDAR_WRITE = 2;
    AsyncQueryHandler queryHandler;
    ContentValues values;
    Activity mActivity;

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    Uri uri;
    String selection;
    String[] selectionArgs;
    String userEmail;

    ArrayList<String> emails;
    ArrayList<Long> ids;

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActivity = this;

        emails = new ArrayList<>();
        ids = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryHandler = new AsyncQueryHandler(getContentResolver()) {
                    @Override
                    protected void onInsertComplete(int token, Object cookie, Uri uri) {
                        super.onInsertComplete(token, cookie, uri);
                        Toast.makeText(getApplicationContext(), "sukses", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                        if (cursor == null) {
                            // Some providers return null if an error occurs whereas others throw an exception
                            Log.e("result", "null");
                        }
                        else if (cursor.getCount() < 1) {
                            // No matches found
                            Log.e("result", "no matches found");

                        }
                        else {
                            // Use the cursor to step through the returned records
                            while (cursor.moveToNext()) {
                                // Get the field values
                                calID = cursor.getLong(PROJECTION_ID_INDEX);

                                userEmail = cursor.getString(1);

                                ids.add(calID);
                                emails.add(userEmail);
                                //Log.e("id", String.valueOf(calID));
                                //Log.e("e-mail", String.valueOf(emails));
                            }

                            String[] haha = new String[emails.size()];
                            emails.toArray(haha);

                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            builder.setTitle("Pilih Kalender").setItems(haha, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Toast.makeText(getApplicationContext(), String.valueOf(ids.get(i)), Toast.LENGTH_LONG).show();
                                    createEvent(ids.get(i));
                                    emails.clear();
                                    ids.clear();
                                }
                            });

                            AlertDialog dialog = builder.create();

                            dialog.show();

                        }
                    }
                };

                //For querying calendar
                uri = CalendarContract.Calendars.CONTENT_URI;
                selection = CalendarContract.Calendars.ACCOUNT_NAME + " = " + CalendarContract.Calendars.OWNER_ACCOUNT;
                selectionArgs = new String[] {CalendarContract.Calendars.OWNER_ACCOUNT};

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(mActivity, new String[] { Manifest.permission.READ_CALENDAR }, PERMISSION_CALENDAR_READ);

                } else {
                    queryHandler.startQuery(1, null, uri, EVENT_PROJECTION, selection, null, null);
                }

                //For writing to calendar
                /*Calendar beginTime = Calendar.getInstance();
                beginTime.set(2017, 9, 5, 10, 30);
                startMillis = beginTime.getTimeInMillis();
                Calendar endTime = Calendar.getInstance();
                endTime.set(2017, 9, 5, 12, 00);
                endMillis = endTime.getTimeInMillis();

                values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, startMillis);
                values.put(CalendarContract.Events.DTEND, endMillis);
                values.put(CalendarContract.Events.TITLE, "Jazzercise");
                values.put(CalendarContract.Events.DESCRIPTION, "Group workout");
                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Jakarta");

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(mActivity, new String[] { Manifest.permission.WRITE_CALENDAR }, PERMISSION_CALENDAR);

                } else {
                    queryHandler.startInsert(1, null, CalendarContract.Events.CONTENT_URI, values);
                }*/


            }
        });
    }

    private void createEvent(long calID) {
        //For writing to calendar
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2017, 9, 6, 12, 30);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2017, 9, 6, 13, 00);
        endMillis = endTime.getTimeInMillis();

        Log.e("time", String.valueOf(startMillis));

        values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Rapat Payload");
        values.put(CalendarContract.Events.DESCRIPTION, "Rapat Yeah!");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Jakarta");

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[] { Manifest.permission.WRITE_CALENDAR }, PERMISSION_CALENDAR_WRITE);
        } else {
            queryHandler.startInsert(2, null, CalendarContract.Events.CONTENT_URI, values);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CALENDAR_READ:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //For querying calendar
                    queryHandler.startQuery(1, null, uri, EVENT_PROJECTION, selection, selectionArgs, null);
                }
                break;
            case PERMISSION_CALENDAR_WRITE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //For writing to calendar
                    queryHandler.startInsert(2, null, CalendarContract.Events.CONTENT_URI, values);
                }
                break;
        }
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
}
