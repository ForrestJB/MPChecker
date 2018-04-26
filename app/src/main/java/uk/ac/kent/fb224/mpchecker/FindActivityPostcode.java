package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tree1 on 28/03/2018.
 */

public class FindActivityPostcode extends AppCompatActivity{
    private EditText searchTxt;
    private ConstraintLayout ConSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_layout_postcode);

        searchTxt = (EditText) findViewById(R.id.PostSearchEditText);
        ConSearch = findViewById(R.id.TitleAni5);
        searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String Input = searchTxt.getText().toString();
                    Input.replace(" ", "+");
                    PostSearch(Input);
                return true;
            }
        });


    }
    public void PostSearch(String Input){
        final Constituency YourCon = new Constituency();
        String MPName="";
        String URL = "https://www.theyworkforyou.com/api/getMP?key=DvbcgvFHgew2FECNnUCJ7frD&postcode="+Input+"&output=js";
        NetManager NetMgr = NetManager.getInstance(getApplicationContext());
        RequestQueue requestQueue = NetMgr.requestQueue;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String MPName = response.getString("full_name");
                    for(int i=0;i<NetManager.getInstance(getApplicationContext()).conList.size();i++){
                        if(i==121){}
                        else {
                            Constituency Con = NetManager.getInstance(getApplicationContext()).conList.get(i);
                            Log.d("iter", Integer.toString(i));
                            if (Con.MPName.equals(MPName)) {
                                YourCon.MPName = Con.MPName;
                                break;
                            }
                        }
                    }
                    if(YourCon == null){
                        Toast.makeText(getApplicationContext(), "Postcode not found, please try again", Toast.LENGTH_LONG).show();
                    }
                    SharedPreferences sharedPref = getSharedPreferences("Main_Pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putString("User_MP", YourCon.MPName);
                    editor.putBoolean("First_Open", false);
                    editor.apply();
                    Log.d("Your MP", YourCon.MPName);
                    Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Postcode not found, please try again", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Network error, please check your internet connection", Toast.LENGTH_LONG).show();//if there is a reponse error, notify the user
            }
        });
        requestQueue.add(request);
    }

}
