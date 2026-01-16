package com.grupok.watertrack.scripts.apiCRUD;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.grupok.watertrack.R;
import com.grupok.watertrack.database.entities.EnterpriseEntity;
import com.grupok.watertrack.database.entities.MeterReadingEntity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.MeterTypeEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;
import com.grupok.watertrack.fragments.mainactivityfrags.readingscontadorview.MainACReadingsContadorFrag;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIMethods {
    //-------------------------------------------------------------------------------------------
    // <editor-fold desc="GET USERS">
    private GetUsersResponse getUsersResponse;
    public interface GetUsersResponse{
        void onGetUsersResponse(int responseType, List<UserInfosEntity> users);
    }
    public void setGetUsersResponse(GetUsersResponse listenner){
        this.getUsersResponse = listenner;
    }
    public void getUsers(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://172.22.21.222/watertrack/backend/web/api/users";
        List<UserInfosEntity> users = new ArrayList<>();
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject hit = response.getJSONObject(i);

                            /*UserInfosEntity user = new UserInfosEntity(
                                    hit.getString("username"),
                                    hit.getString("email"),
                                    hit.getString("password_hash"),
                                    "",
                                    1,
                                    "",
                                    ""
                            );*/

                            //users.add(user);
                        }

                        getUsersResponse.onGetUsersResponse(1, users);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        getUsersResponse.onGetUsersResponse(0, users);
                    }
                },
                error -> {
                    Toast.makeText(context, "NETWORK ERROR", Toast.LENGTH_SHORT).show();
                    getUsersResponse.onGetUsersResponse(0, users);
                }
        );

        // Add the request to the RequestQueue.
        queue.add(request);
    }
    // </editor-fold>
    //-------------------------------------------------------------------------------------------
    // <editor-fold desc="GET USER ROLE">
    private GetUserRoleResponse getUserRoleResponse;
    public interface GetUserRoleResponse{
        void onGetUserRoleResponse(boolean response, String responseText, String role);
    }
    public void setGetUserRoleResponse(GetUserRoleResponse listenner){
        this.getUserRoleResponse = listenner;
    }
    public void getUserRole(Context context, UserInfosEntity user){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://172.22.21.222/watertrack/backend/web/api/users/getrole/"+user.userId;

        SharedPreferences prefs = context.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String pass = prefs.getString(user.email, "");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        getUserRoleResponse.onGetUserRoleResponse(true, "", response.getString("role")
                        );
                    } catch (JSONException e) {
                        getUserRoleResponse.onGetUserRoleResponse(false, context.getString(R.string.apiMethods_JsonParseError), "");
                    }
                },
                error -> {
                    getUserRoleResponse.onGetUserRoleResponse(false, context.getString(R.string.apiMethods_VolleyError), "");
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                String credentials = user.username + ":" + pass;
                String auth = "Basic " + Base64.encodeToString(
                        credentials.getBytes(StandardCharsets.UTF_8),
                        Base64.NO_WRAP
                );

                headers.put("Authorization", auth);
                headers.put("Accept", "application/json");

                headers.put("Host", "172.22.21.222");

                return headers;
            }
        };

        queue.add(request);
    }
    // </editor-fold>
    //-------------------------------------------------------------------------------------------
    // <editor-fold desc="LOGIN">
    private LoginResponse loginResponse;
    public interface LoginResponse{
        void onLoginResponse(boolean response, UserInfosEntity user, String message);
    }
    public void setLoginResponse(LoginResponse listenner){
        this.loginResponse = listenner;
    }
    public void login(Context context, String username, String password) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/auth/login";

        JSONObject json = new JSONObject();
        try{
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, json,
                response -> {
                    // login success
                    try{
                        switch (response.getInt("success")){
                            case 0:
                                JSONObject userObject = response.getJSONObject("user");
                                UserInfosEntity user = new UserInfosEntity(userObject.getInt("userId"),
                                        userObject.getString("username"),
                                        userObject.getString("email"),
                                        userObject.getInt("status"));

                                try{
                                    user.setProfileInfo(userObject.getString("birthDate"), userObject.getString("address"));
                                } catch (Exception e) {
                                    Log.i("API_Login", "Profile Info not found.");
                                }
                                try{
                                    user.setTechInfo(userObject.getInt("enterpriseID"), userObject.getString("certificationNumber"));
                                } catch (Exception e) {
                                    Log.i("API_Login", "Technician Info not found.");
                                }

                                SharedPreferences prefs = context.getSharedPreferences("Perf_User", MODE_PRIVATE);
                                prefs.edit().putString(user.email, password).apply();

                                loginResponse.onLoginResponse(true, user, "");
                                break;
                            case 2: // Username and password required
                            case 3: // User not found
                            case 4: // Incorrect password
                                loginResponse.onLoginResponse(false, null, response.getString("message"));
                                break;
                        }
                    } catch (JSONException e) {
                        loginResponse.onLoginResponse(false, null, context.getString(R.string.apiMethods_JsonParseError));
                    }
                },
                error -> {
                    loginResponse.onLoginResponse(false, null, context.getString(R.string.apiMethods_VolleyError));
                }
        );

        queue.add(request);
    }
    // </editor-fold>
    //-------------------------------------------------------------------------------------------
    // <editor-fold desc="SIGN UP">
    private SignUpResponse signUpResponse;
    public interface SignUpResponse{
        void onSignUpResponse(boolean response, String message);
    }
    public void setSignUpResponse(SignUpResponse listenner){
        this.signUpResponse = listenner;
    }
    public void signup(Context context, String Email, String password){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/auth/signup";

        JSONObject json = new JSONObject();
        try{
            json.put("email", Email);
            json.put("password", password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, json,
                response -> {
                    // login success
                    try{
                        switch (response.getInt("success")){
                            case 1:
                                signUpResponse.onSignUpResponse(true, "");
                                break;
                            case 0: // ERROR
                                signUpResponse.onSignUpResponse(false, response.getString("message"));
                                break;
                        }
                    } catch (JSONException e) {
                        signUpResponse.onSignUpResponse(false, context.getString(R.string.apiMethods_JsonParseError));
                    }
                },
                error -> {
                    signUpResponse.onSignUpResponse(false, context.getString(R.string.apiMethods_VolleyError));
                }
        );

        queue.add(request);
    }
    // </editor-fold>
    //-------------------------------------------------------------------------------------------
    // <editor-fold desc="GET METERS">
    private GetMetersResponse getMetersResponse;
    public interface GetMetersResponse{
        void onGetMetersResponse(boolean response, String message, List<MeterEntity> list);
    }
    public void setGetMetersResponse(GetMetersResponse listenner){
        this.getMetersResponse = listenner;
    }
    public void getMeters(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/meters";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    // login success
                    try {
                        List<MeterEntity> list = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject meter = response.getJSONObject(i);
                            MeterEntity contador = new MeterEntity(
                                    meter.getString("address"),
                                    meter.getInt("userID"),
                                    meter.getInt("meterTypeID"),
                                    meter.getInt("enterpriseID"),
                                    meter.getString("class"),
                                    meter.getString("instalationDate"),
                                    meter.getString("shutdownDate"),
                                    meter.getString("maxCapacity"),
                                    meter.getString("measureUnity"),
                                    meter.getString("supportedTemperature"),
                                    meter.getInt("state"));
                            contador.setId(meter.getInt("id"));
                            list.add(contador);
                        }
                        Log.d("erros", "size by meter: " + list.size());
                        getMetersResponse.onGetMetersResponse(true, "", list);
                    } catch (JSONException e) {
                        Log.d("erros", "getMeters: jsonERROR: error:"+ e.getMessage());
                        getMetersResponse.onGetMetersResponse(false, context.getString(R.string.apiMethods_JsonParseError), null);
                    }
                },
                error -> {
                    Log.d("erros", "getMeters: VolleyError: error:"+ error.getMessage());
                    getMetersResponse.onGetMetersResponse(false, context.getString(R.string.apiMethods_VolleyError), null);
                }
        );

        queue.add(request);
    }
    // </editor-fold>
    //-------------------------------------------------------------------------------------------
    // <editor-fold desc="GET METERS BY USER ID">
    private GetMetersByUserIdResponse getMetersByUserIdResponse;

    public interface GetMetersByUserIdResponse {
        void onGetMetersByUserIdResponse(boolean response, String message, List<MeterEntity> list);
    }

    public void setGetMetersByUserIdResponse(GetMetersByUserIdResponse listenner) {
        this.getMetersByUserIdResponse = listenner;
    }

    public void getMetersByUserId(Context context, int userId, UserInfosEntity user, String pass) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/meters/fromuser/" + userId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        List<MeterEntity> list = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject meter = response.getJSONObject(i);
                            MeterEntity contador = new MeterEntity(
                                    meter.getString("address"),
                                    meter.getInt("userID"),
                                    meter.getInt("meterTypeID"),
                                    meter.getInt("enterpriseID"),
                                    meter.getString("class"),
                                    meter.getString("instalationDate"),
                                    meter.getString("shutdownDate"),
                                    meter.getString("maxCapacity"),
                                    meter.getString("measureUnity"),
                                    meter.getString("supportedTemperature"),
                                    meter.getInt("state")
                            );
                            contador.setId(meter.getInt("id"));
                            list.add(contador);
                        }
                        Log.d("erros", "size by userid: " + list.size());
                        if (getMetersByUserIdResponse != null)
                            getMetersByUserIdResponse.onGetMetersByUserIdResponse(true, "", list);
                    } catch (JSONException e) {
                        Log.d("erros", "getMetersByUserId: jsonERROR: error:"+ e.getMessage());
                        if (getMetersByUserIdResponse != null)
                            getMetersByUserIdResponse.onGetMetersByUserIdResponse(
                                    false,
                                    context.getString(R.string.apiMethods_JsonParseError),
                                    null
                            );
                    }
                },
                error -> {
                    Log.d("erros", "getMetersByUserId: VolleyError: error:"+ error.getMessage());
                    if (getMetersByUserIdResponse != null)
                        getMetersByUserIdResponse.onGetMetersByUserIdResponse(
                                false,
                                context.getString(R.string.apiMethods_VolleyError),
                                null
                        );
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = user.username + ":" + pass;
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);

                headers.put("Authorization", auth);
                headers.put("Accept", "application/json");
                headers.put("Host", "172.22.21.222");

                return headers;
            }
        };

        queue.add(request);
    }
    // </editor-fold>
    //-------------------------------------------------------------------------------------------
    // <editor-fold desc="GET METERS BY ENTERPRISE ID">
    private GetMetersByEnterpriseResponse getMetersByEnterpriseResponse;

    public interface GetMetersByEnterpriseResponse {
        void onGetMetersByEnterpriseResponse(boolean response, String message, List<MeterEntity> list);
    }

    public void setGetMetersByEnterpriseResponse(GetMetersByEnterpriseResponse listener) {
        this.getMetersByEnterpriseResponse = listener;
    }

    public void getMetersByEnterprise(Context context, int enterpriseId) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/meters/fromenterprise/" + enterpriseId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        List<MeterEntity> list = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject meter = response.getJSONObject(i);
                            MeterEntity contador = new MeterEntity(
                                    meter.getString("address"),
                                    meter.getInt("userID"),
                                    meter.getInt("meterTypeID"),
                                    meter.getInt("enterpriseID"),
                                    meter.getString("class"),
                                    meter.getString("instalationDate"),
                                    meter.getString("shutdownDate"),
                                    meter.getString("maxCapacity"),
                                    meter.getString("measureUnity"),
                                    meter.getString("supportedTemperature"),
                                    meter.getInt("state")
                            );
                            contador.setId(meter.getInt("id"));
                            list.add(contador);
                        }
                        Log.d("erros", "size by enterpriseid: " + list.size());
                        if (getMetersByEnterpriseResponse != null)
                            getMetersByEnterpriseResponse.onGetMetersByEnterpriseResponse(true, "", list);
                    } catch (JSONException e) {
                        Log.d("erros", "getMetersByEnterprise: jsonERROR: error:"+ e.getMessage());
                        if (getMetersByEnterpriseResponse != null)
                            getMetersByEnterpriseResponse.onGetMetersByEnterpriseResponse(
                                    false,
                                    context.getString(R.string.apiMethods_JsonParseError),
                                    null
                            );
                    }
                },
                error -> {
                    Log.d("erros", "getMetersByEnterprise: VolleyError: error:"+ error.getMessage());
                    if (getMetersByEnterpriseResponse != null)
                        getMetersByEnterpriseResponse.onGetMetersByEnterpriseResponse(
                                false,
                                context.getString(R.string.apiMethods_VolleyError),
                                null
                        );
                }
        );

        queue.add(request);
    }
    // </editor-fold>
    //-------------------------------------------------------------------------------------------
    // <editor-fold desc="GET ENTERPRISE BY ID">
    private GetEnterpriseByIdResponse getEnterpriseByIdResponse;
    public interface GetEnterpriseByIdResponse{
        void onGetEnterpriseByIdResponse(boolean response, String message, EnterpriseEntity enterprise);
    }
    public void setGetEnterpriseByIdResponse(GetEnterpriseByIdResponse listenner){
        this.getEnterpriseByIdResponse = listenner;
    }
    public void getEnterpriseById(Context context, int id){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/enterprises/"+id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try{
                        EnterpriseEntity enterprise = new EnterpriseEntity(response.getString("name"),
                                response.getString("address"),
                                response.getString("contactNumber"),
                                response.getString("contactEmail"),
                                response.getString("website"));
                        enterprise.setId(id);
                        getEnterpriseByIdResponse.onGetEnterpriseByIdResponse(true, "", enterprise);
                    } catch (JSONException e) {
                        getEnterpriseByIdResponse.onGetEnterpriseByIdResponse(false, context.getString(R.string.apiMethods_JsonParseError), null);
                    }
                },
                error -> {
                    getEnterpriseByIdResponse.onGetEnterpriseByIdResponse(false, context.getString(R.string.apiMethods_VolleyError), null);
                }
        );

        queue.add(request);
    }
    // </editor-fold>
    //-------------------------------------------------------------------------------------------
    // <editor-fold desc="GET USER BY ID">
    private GetUserByIdResponse getUserByIdResponse;
    public interface GetUserByIdResponse{
        void onGetUserByIdResponse(boolean response, String message, UserInfosEntity user);
    }
    public void setGetUserByIdResponse(GetUserByIdResponse listenner){
        this.getUserByIdResponse = listenner;
    }
    public void getUserById(Context context, int id){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/users/"+id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try{
                        UserInfosEntity user = new UserInfosEntity(response.getInt("id"),
                                response.getString("username"),
                                response.getString("email"),
                                response.getInt("status"));
                        getUserByIdResponse.onGetUserByIdResponse(true, "", user);
                    } catch (JSONException e) {
                        getUserByIdResponse.onGetUserByIdResponse(false, context.getString(R.string.apiMethods_JsonParseError), null);
                    }
                },
                error -> {
                    getUserByIdResponse.onGetUserByIdResponse(false, context.getString(R.string.apiMethods_VolleyError), null);
                }
        );

        queue.add(request);
    }
    // </editor-fold>
    //-------------------------------------------------------------------------------------------
    // <editor-fold desc="GET METER TYPE BY ID">
    private GetMeterTypeByIdResponse getMeterTypeByIdResponse;
    public interface GetMeterTypeByIdResponse{
        void onGetMeterTypeByIdResponse(boolean response, String message, MeterTypeEntity type);
    }
    public void setGetMeterTypeByIdResponse(GetMeterTypeByIdResponse listenner){
        this.getMeterTypeByIdResponse = listenner;
    }
    public void getMeterTypeById(Context context, int id){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/meter-types/"+id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try{
                        MeterTypeEntity meterType = new MeterTypeEntity(response.getString("description"));
                        meterType.setId(id);
                        getMeterTypeByIdResponse.onGetMeterTypeByIdResponse(true, "", meterType);
                    } catch (JSONException e) {
                        getMeterTypeByIdResponse.onGetMeterTypeByIdResponse(false, context.getString(R.string.apiMethods_JsonParseError), null);
                    }
                },
                error -> {
                    getMeterTypeByIdResponse.onGetMeterTypeByIdResponse(false, context.getString(R.string.apiMethods_VolleyError), null);
                }
        );

        queue.add(request);
    }
    // </editor-fold>
    //-------------------------------------------------------------------------------------------
    // <editor-fold desc="GET READINGS BY METER ID">
    private GetReadingsByMeterIdResponse getReadingsByMeterIdResponse;
    private MainACReadingsContadorFrag fragGetreadings;
    public interface GetReadingsByMeterIdResponse{
        void onGetReadingsByMeterIdResponse(boolean response, String message, List<MeterReadingEntity> list, MainACReadingsContadorFrag frag);
    }
    public void setGetReadingsByMeterIdResponse(GetReadingsByMeterIdResponse listenner, MainACReadingsContadorFrag frag){
        this.getReadingsByMeterIdResponse = listenner;
        this.fragGetreadings = frag;
    }
    public void getReadingsByMeterId(Context context, int id){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/meter-readings/frommeter/"+id;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    // login success
                    try {
                        List<MeterReadingEntity> list = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject reading = response.getJSONObject(i);
                            MeterReadingEntity leituras = new MeterReadingEntity(
                                    reading.getInt("tecnicoID"),
                                    reading.getInt("meterID"),
                                    reading.getString("reading"),
                                    reading.getString("accumulatedConsumption"),
                                    reading.getString("date"),
                                    reading.getString("waterPressure")
                            );
                            leituras.setId(reading.getInt("id"));
                            list.add(leituras);
                        }
                        Log.d("erros", "getReadingsByMeterId: list size: " + list.size());
                        getReadingsByMeterIdResponse.onGetReadingsByMeterIdResponse(true, "", list, fragGetreadings);
                    } catch (JSONException e) {
                        Log.d("erros", "jsonERROR: error:"+ e.getMessage());
                        getReadingsByMeterIdResponse.onGetReadingsByMeterIdResponse(false, context.getString(R.string.apiMethods_JsonParseError), null, fragGetreadings);
                    }
                },
                error -> {
                    getReadingsByMeterIdResponse.onGetReadingsByMeterIdResponse(false, context.getString(R.string.apiMethods_VolleyError), null, fragGetreadings);
                }
        );

        queue.add(request);
    }
    // </editor-fold>


}
