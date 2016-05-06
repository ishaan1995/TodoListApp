package com.ishaan.todolists;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    Realm realm;
    private static final String TAG = "MainActivity";
    RealmConfiguration config;
    String userEmail=null;
    Intent intent;

    RecyclerView recyclerView;

    ItemAdapter itemAdapter;
    RealmResults<Item> itemRealmResults;
    ArrayList<Item> data = new ArrayList<>();

    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.itemrecyc);



        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Get a Realm instance for this thread


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent =new Intent(MainActivity.this,AddToList.class);
                startActivity(intent);


            }
        });

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onStart() {
        super.onStart();
        config = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(config);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()){
            GoogleSignInResult result = opr.get();
            userEmail = result.getSignInAccount().getEmail();
            itemRealmResults = realm.where(Item.class).equalTo("email",userEmail).findAll();
            //Toast.makeText(MainActivity.this,"Db size"+itemRealmResults.size(), Toast.LENGTH_SHORT).show();
            itemsdata();
        }
        else {
            RealmResults<User> realmResults = realm.where(User.class).findAll();
            if (realmResults.size()==0){
                Toast.makeText(MainActivity.this, "Error while retrieving email from local db!", Toast.LENGTH_SHORT).show();
            }else {
                userEmail=realmResults.get(0).getEmail();
                itemsdata();
            }

        }

    }

    public void itemsdata(){
        data = new ArrayList<>();
        for (int i=0;i<itemRealmResults.size();i++){
            data.add(itemRealmResults.get(i));
            Log.d(TAG,"Email : "+itemRealmResults.get(i).getEmail());
        }
        itemAdapter = new ItemAdapter(this,data);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);
        finish();
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        if (status.isSuccess()){
                            RealmResults<User> realmResults = realm.where(User.class).findAll();
                            realmResults.get(0).deleteFromRealm();
                            Log.d(TAG,"Deleted from local DB as well");
                            Toast.makeText(MainActivity.this, "Signout successfull", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,Login.class));
                            finish();
                        }
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {

            signOut();
        }

        return super.onOptionsItemSelected(item);
    }
}
