package com.ishaan.todolists;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.Calendar;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class AddToList extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    private int mYear, mMonth, mDay, mHour, mMinute,mSecond;
    TextView date,time,place;
    int mhour,mminute;
    Boolean timeerror=false;
    ImageButton addTodo,placePicker,dateSet,timeSet;
    Button redBack,purpleBack,greenBack,whiteBack;
    EditText todoItem;
    String color="#ffffff";
    Random random = new Random();

    Realm realm;
    RealmConfiguration config;

    private static final String TAG = "AddToListActivity";
    RelativeLayout relativeLayout; //to give a preview to user of selected color
    Calendar c; // to get current system date-time

    //Place picker variable
    int PLACE_PICKER_REQUEST = 1;
    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;
    String userEmail=null;
    private PendingIntent pendingIntent;

    //Set calender to start pending intent for notification
    Calendar calendar = Calendar.getInstance();
    Calendar setUser = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        redBack = (Button) findViewById(R.id.redback);
        purpleBack = (Button) findViewById(R.id.purpleback);
        greenBack = (Button) findViewById(R.id.greenback);
        whiteBack = (Button) findViewById(R.id.whiteback);
        todoItem = (EditText) findViewById(R.id.todo_item);
        date = (TextView) findViewById(R.id.duedate);
        time= (TextView) findViewById(R.id.duetime);
        place = (TextView) findViewById(R.id.place);
        addTodo = (ImageButton) findViewById(R.id.done);
        placePicker = (ImageButton) findViewById(R.id.place_picker);
        dateSet = (ImageButton) findViewById(R.id.dateset);
        timeSet = (ImageButton) findViewById(R.id.timeset);


        config = new RealmConfiguration.Builder(this).build();

        // Get a Realm instance for this thread
        realm = Realm.getInstance(config);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Google api client builder for place picker

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        //start place picker intent
        placePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    placePicker();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
            }
        });

        //check for character count of todoitem which is 128 and see if its empty
        todoItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                //Log.d(TAG,"Here");
                if (s.toString().endsWith("\n")){
                    todoItem.setText(todoItem.getText().toString().trim());
                    Toast.makeText(AddToList.this, "Cannot add blank new line!", Toast.LENGTH_SHORT).show();

                }




            }
        });

        //addtodo item to local realm db

        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (todoItem.getText().toString().isEmpty()){
                    Toast.makeText(AddToList.this, "Todo Item cannot be empty!", Toast.LENGTH_SHORT).show();
                }
                else if (userEmail==null){
                    Toast.makeText(AddToList.this, "Could not get user email!Try again later", Toast.LENGTH_SHORT).show();
                }
                else{

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            Item item = bgRealm.createObject(Item.class);
                            item.setEmail(userEmail);
                            item.setColor(color);
                            item.setListItem(todoItem.getText().toString());
                            item.setPlace(place.getText().toString());
                            item.setDateTime(date.getText().toString()+" "+time.getText().toString());
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            // Transaction was a success.
                            if (!timeerror) {


                                int m = random.nextInt(9999 - 1000) + 1000;
                                Toast.makeText(AddToList.this, "Added successfully!", Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(AddToList.this, TodoService.class);
                                //AddToList.this.startService(myIntent);
                                Bundle b = new Bundle();
                                b.putString("todoitem", todoItem.getText().toString());
                                b.putString("place", place.getText().toString());
                                myIntent.putExtras(b);
                                pendingIntent = PendingIntent.getService(AddToList.this, m, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                //setUser.setTimeInMillis(System.currentTimeMillis());
                                setUser.set(Calendar.YEAR, mYear);
                                setUser.set(Calendar.MONTH, mMonth);
                                setUser.set(Calendar.DAY_OF_MONTH, mDay - 1);
                                setUser.set(Calendar.MINUTE, mMinute);
                                setUser.set(Calendar.HOUR, mHour + 12);
                                //setUser.setTimeInMillis(System.currentTimeMillis());
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                Log.d(TAG, "cal time:" + calendar.getTimeInMillis());

                                //use if current time is selected by user or time is less than 1 min
                                if (calendar.getTimeInMillis() == setUser.getTimeInMillis()
                                        || Math.abs(calendar.getTimeInMillis() - setUser.getTimeInMillis()) <= 60000) {
                                    Toast.makeText(AddToList.this, "here after 10 sec!", Toast.LENGTH_SHORT).show();
                                    calendar.add(Calendar.SECOND, 10);
                                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                } else {
                                    Log.d(TAG, "usertime :" + setUser.getTimeInMillis());
                                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    alarmManager.set(AlarmManager.RTC_WAKEUP, setUser.getTimeInMillis(), pendingIntent);
                                }
                                Intent intent = new Intent(AddToList.this, MainActivity.class);

                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(AddToList.this, "Choose time from timepicker correctly to proceed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            // Transaction failed and was automatically canceled.
                            Toast.makeText(AddToList.this,"Saving TodoList to db failed!Try later on", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        //onclick listner to change color of todoitem as a preview
        //using color filter to multiply white color with my custom color selection
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                color=null;
                switch (v.getId()){
                    case R.id.redback:
                        color="#F44336";
                        break;
                    case R.id.purpleback:
                        color="#9C27B0";
                        break;
                    case R.id.greenback:
                        color="#4CAF50";
                        break;
                    case R.id.whiteback:
                        color="#ffffff";
                        break;
                }
                if (!color.equals("#ffffff")){
                    relativeLayout.getBackground().setColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
                }else {
                    relativeLayout.setBackground(ContextCompat.getDrawable(AddToList.this,R.drawable.box));
                    relativeLayout.getBackground().clearColorFilter();
                }


            }
        };

        //assign findview to color buttons
        redBack.setOnClickListener(onClickListener);
        purpleBack.setOnClickListener(onClickListener);
        greenBack.setOnClickListener(onClickListener);
        whiteBack.setOnClickListener(onClickListener);

        /*
            Set current date and time to display in date and time textview
            if user does not select a custom date or time,
            notification is received after 30 secs with later 30 mins snooze time
        */
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);

        mMinute = c.get(Calendar.MINUTE);
        mSecond = c.get(Calendar.SECOND);
        // Set min date in datepicker dialog,subtracted extra 1000 to support for android versions below 4.4.2
        final long Mindate = c.getTimeInMillis()-1000;

        int correctMonth=mMonth+1;

        date.setText(mDay+"-"+correctMonth+"-"+mYear);

        time.setText(mHour+":"+mMinute);

        //Date picker dialog
        final DatePickerDialog datepickerdialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                mYear=year;
                mMonth=monthOfYear;
                mDay=dayOfMonth;
                int correctMonth = monthOfYear+1;  //to correct month correction as system stats January as month 0
                date.setText(year+"-"+correctMonth+"-"+dayOfMonth);

            }
        },mYear,mMonth,mDay);

        //Time Picker Dialog
        final TimePickerDialog timepickerdialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                if (hourOfDay<mHour){
                    timeerror=true;
                    Toast.makeText(AddToList.this, "Error in hour set!", Toast.LENGTH_SHORT).show();

                }else if (minute<mMinute && hourOfDay==mHour){
                    timeerror=true;
                    Toast.makeText(AddToList.this, "Error in min set!", Toast.LENGTH_SHORT).show();

                }
                else {
                    timeerror=false;

                }
                time.setText(hourOfDay+":"+minute);
                mhour=hourOfDay;
                mminute=minute;




            }
        },mHour,mMinute,true); //true to set 24 hr view


        //Datepicker dialog show ImageButton

        dateSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepickerdialog.getDatePicker().setMinDate(Mindate);
                datepickerdialog.show();
            }
        });

        //Time picker show Imagebutton

        timeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                timepickerdialog.show();
                if (!timeerror){
                    mHour=mhour;
                    mMinute=mminute;
                }
            }
        });



    }

    //get user signedin email for db storage

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()){
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            GoogleSignInAccount acct = result.getSignInAccount();
            userEmail= acct.getEmail();
        }else {
            RealmResults<User> realmResults = realm.where(User.class).findAll();
            Log.d(TAG,"using local db userEmail");
            userEmail=realmResults.get(0).getEmail();

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    //Start Place picker intent
    public void placePicker() throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
    }

    //Get place selected from user and set to Place TextView
    //By default place will be set to HOME
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place Place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", Place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                place.setText(Place.getName());
            }
        }
    }


}



