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
import com.grupok.watertrack.database.entities.ReportsEntity;
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
        void onGetUsersResponse(boolean response, String responseText, List<UserInfosEntity> users);
    }
    public void setGetUsersResponse(GetUsersResponse listenner){
        this.getUsersResponse = listenner;
    }
    public void getUsers(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://172.22.21.222/watertrack/backend/web/api/users";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<UserInfosEntity> list = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject userObject = response.getJSONObject(i);

                            UserInfosEntity user = new UserInfosEntity(userObject.getInt("id"),
                                    userObject.getString("username"),
                                    userObject.getString("email"),
                                    userObject.getInt("status"));

                            list.add(user);
                        }

                        getUsersResponse.onGetUsersResponse(true, "", list);

                    } catch (JSONException e) {
                        getUsersResponse.onGetUsersResponse(false, context.getString(R.string.apiMethods_JsonParseError), null);
                    }
                },
                error -> {
                    getUsersResponse.onGetUsersResponse(false, context.getString(R.string.apiMethods_VolleyError), null);
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
                                    user.setProfileInfo(userObject.getString("birthDate"), userObject.getString("address"), userObject.getInt("profileID"));
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

    public void getMetersByUserId(Context context, UserInfosEntity user, String pass) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/meters/fromuser/" + user.userId;

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

    public void getMetersByEnterprise(Context context, UserInfosEntity user, String pass) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/meters/fromenterprise/" + user.enterpriseID;

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
                        Log.d("erros", "getMetersByEnterprise: url: " + url);
                        Log.d("erros", "getMetersByEnterprise: JSON: error:"+ e.getMessage());
                        if (getMetersByEnterpriseResponse != null)
                            getMetersByEnterpriseResponse.onGetMetersByEnterpriseResponse(
                                    false,
                                    context.getString(R.string.apiMethods_JsonParseError),
                                    null
                            );
                    }
                },
                error -> {
                    Log.d("erros", "getMetersByEnterprise: url: " + url);
                    Log.d("erros", "getMetersByEnterprise: VolleyError: error:"+ error.getMessage());
                    if (getMetersByEnterpriseResponse != null)
                        getMetersByEnterpriseResponse.onGetMetersByEnterpriseResponse(
                                false,
                                context.getString(R.string.apiMethods_VolleyError),
                                null
                        );
                }
        ){
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
    // <editor-fold desc="GET ENTERPRISES">
    private GetEnterpriseResponse getEnterpriseResponse;
    public interface GetEnterpriseResponse{
        void onGetEnterpriseResponse(boolean response, String message, List<EnterpriseEntity> enterprise);
    }
    public void setGetEnterpriseResponse(GetEnterpriseResponse listenner){
        this.getEnterpriseResponse = listenner;
    }
    public void getEnterprises(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/enterprises";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        List<EnterpriseEntity> list = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject meter = response.getJSONObject(i);
                            EnterpriseEntity enterprise = new EnterpriseEntity(
                                    meter.getString("name"),
                                    meter.getString("address"),
                                    meter.getString("contactNumber"),
                                    meter.getString("contactEmail"),
                                    meter.getString("website"));
                            enterprise.setId(meter.getInt("id"));
                            list.add(enterprise);
                        }
                        Log.d("erros", "size by meter: " + list.size());
                        getEnterpriseResponse.onGetEnterpriseResponse(true, "", list);
                    } catch (JSONException e) {
                        getEnterpriseResponse.onGetEnterpriseResponse(false, context.getString(R.string.apiMethods_JsonParseError), null);
                    }
                },
                error -> {
                    getEnterpriseResponse.onGetEnterpriseResponse(false, context.getString(R.string.apiMethods_VolleyError), null);
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
    // <editor-fold desc="GET METERS TYPES">
    private GetMeterTypesResponse getMeterTypesResponse;
    public interface GetMeterTypesResponse{
        void onGetMeterTypesResponse(boolean response, String message, List<MeterTypeEntity> list);
    }
    public void setGetMeterTypesResponse(GetMeterTypesResponse listenner){
        this.getMeterTypesResponse = listenner;
    }
    public void getMeterTypes(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/meter-types";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        List<MeterTypeEntity> list = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject meter = response.getJSONObject(i);
                            MeterTypeEntity type = new MeterTypeEntity(
                                    meter.getString("description"));
                            type.setId(meter.getInt("id"));
                            list.add(type);
                        }
                        Log.d("erros", "size by meter: " + list.size());
                        getMeterTypesResponse.onGetMeterTypesResponse(true, "", list);
                    } catch (JSONException e) {
                        getMeterTypesResponse.onGetMeterTypesResponse(false, context.getString(R.string.apiMethods_JsonParseError), null);
                    }
                },
                error -> {
                    getMeterTypesResponse.onGetMeterTypesResponse(false, context.getString(R.string.apiMethods_VolleyError), null);
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
    public void getReadingsByMeterId(Context context, int id, UserInfosEntity user){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/meter-readings/frommeter/"+id;

        SharedPreferences prefs = context.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String pass = prefs.getString(user.email, "");

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
        ){
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
    // <editor-fold desc="GET REPORTS">
    private GetReportsResponse getReportsResponse;
    public interface GetReportsResponse{
        void onGetReportsResponse(boolean response, String responseText, List<ReportsEntity> reportsEntities);
    }
    public void setGetReportsResponse(GetReportsResponse listenner){
        this.getReportsResponse = listenner;
    }
    public void getReports(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://172.22.21.222/watertrack/backend/web/api/meter-problems";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<ReportsEntity> reportsEntities = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject object = response.getJSONObject(i);

                            int tecnicoID = 0;
                            if(!object.isNull("tecnicoID")){
                                tecnicoID = object.getInt("tecnicoID");
                            }

                            ReportsEntity report = new ReportsEntity(
                                    object.getInt("meterID"),
                                    object.getInt("userID"),
                                    tecnicoID,
                                    object.getInt("problemState"),
                                    object.getString("description"));
                            report.setId(object.getInt("id"));
                            reportsEntities.add(report);
                        }

                        getReportsResponse.onGetReportsResponse(true, "", reportsEntities);
                    } catch (JSONException e) {
                        getReportsResponse.onGetReportsResponse(false, context.getString(R.string.apiMethods_JsonParseError), null);
                    }
                },
                error -> {
                    getReportsResponse.onGetReportsResponse(false, context.getString(R.string.apiMethods_VolleyError), null);
                }
        );

        queue.add(request);
    }
    // </editor-fold>
    //-------------------------------------------------------------------------------------------
    // <editor-fold desc="GET REPORTS BY USER ID">
    private GetReportsByUserIDResponse getReportsByUserIDResponse;
    public interface GetReportsByUserIDResponse{
        void onGetReportsByUserIDResponse(boolean response, String responseText, List<ReportsEntity> reportsEntities);
    }
    public void setGetReportsByUserIDResponse(GetReportsByUserIDResponse listenner){
        this.getReportsByUserIDResponse = listenner;
    }
    public void getReportsByUserID(Context context, UserInfosEntity user){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://172.22.21.222/watertrack/backend/web/api/meter-problems/fromuser/"+user.userId;

        SharedPreferences prefs = context.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String pass = prefs.getString(user.email, "");

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<ReportsEntity> reportsEntities = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject object = response.getJSONObject(i);

                            int tecnicoID = 0;
                            if(!object.isNull("tecnicoID")){
                                tecnicoID = object.getInt("tecnicoID");
                            }

                            ReportsEntity report = new ReportsEntity(
                                    object.getInt("meterID"),
                                    object.getInt("userID"),
                                    tecnicoID,
                                    object.getInt("problemState"),
                                    object.getString("description"));
                            report.setId(object.getInt("id"));
                            reportsEntities.add(report);
                        }

                        getReportsByUserIDResponse.onGetReportsByUserIDResponse(true, "", reportsEntities);
                    } catch (JSONException e) {
                        getReportsByUserIDResponse.onGetReportsByUserIDResponse(false, context.getString(R.string.apiMethods_JsonParseError), null);
                    }
                },
                error -> {
                    getReportsByUserIDResponse.onGetReportsByUserIDResponse(false, context.getString(R.string.apiMethods_VolleyError), null);
                }
        ){
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
    // <editor-fold desc="GET REPORTS BY METER ID">
    private GetReportsByMeterIDResponse getReportsByMeterIDResponse;
    public interface GetReportsByMeterIDResponse{
        void onGetReportsByMeterIDResponse(boolean response, String responseText, List<ReportsEntity> reportsEntities);
    }
    public void setGetReportsByMeterIDResponse(GetReportsByMeterIDResponse listenner){
        this.getReportsByMeterIDResponse = listenner;
    }
    public void getReportsByMeterID(Context context, UserInfosEntity user, int meterID){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://172.22.21.222/watertrack/backend/web/api/meter-problems/frommeter/"+meterID;

        SharedPreferences prefs = context.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String pass = prefs.getString(user.email, "");

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<ReportsEntity> reportsEntities = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject object = response.getJSONObject(i);

                            int tecnicoID = 0;
                            if(!object.isNull("tecnicoID")){
                                tecnicoID = object.getInt("tecnicoID");
                            }

                            ReportsEntity report = new ReportsEntity(
                                    object.getInt("meterID"),
                                    object.getInt("userID"),
                                    tecnicoID,
                                    object.getInt("problemState"),
                                    object.getString("description"));
                            report.setId(object.getInt("id"));
                            reportsEntities.add(report);
                        }

                        getReportsByMeterIDResponse.onGetReportsByMeterIDResponse(true, "", reportsEntities);
                    } catch (JSONException e) {
                        getReportsByMeterIDResponse.onGetReportsByMeterIDResponse(false, context.getString(R.string.apiMethods_JsonParseError), null);
                    }
                },
                error -> {
                    getReportsByMeterIDResponse.onGetReportsByMeterIDResponse(false, context.getString(R.string.apiMethods_VolleyError), null);
                }
        ){
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
    // <editor-fold desc="POST REPORT">
    private PostReportResponse postReportResponse;
    public interface PostReportResponse{
        void onPostReportResponse(boolean response, String message);
    }
    public void setPostReportResponse(PostReportResponse listenner){
        this.postReportResponse = listenner;
    }
    public void postReport(Context context, UserInfosEntity user, int meterID, String descripiton) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/meter-problems";

        SharedPreferences prefs = context.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String pass = prefs.getString(user.email, "");

        JSONObject json = new JSONObject();
        try{
            json.put("meterID", meterID);
            json.put("userID", user.userId);
            json.put("tecnicoID", null);
            json.put("problemState", 2);
            json.put("description", descripiton);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, json,
                response -> {
                    postReportResponse.onPostReportResponse(true, "");
                },
                error -> {
                    postReportResponse.onPostReportResponse(false, context.getString(R.string.apiMethods_VolleyError));
                }
        ){
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
    // <editor-fold desc="PATCH USER PROFILE">
    private PatchUserProfileResponse patchUserProfileResponse;
    public interface PatchUserProfileResponse{
        void onPatchUserProfileResponse(boolean response, String message);
    }
    public void setPatchUserProfileResponse(PatchUserProfileResponse listenner){
        this.patchUserProfileResponse = listenner;
    }
    public void patchUserProfile(Context context, UserInfosEntity user, String address, String bDate) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/user-profiles/"+user.profileID;

        SharedPreferences prefs = context.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String pass = prefs.getString(user.email, "");

        JSONObject json = new JSONObject();
        try{
            json.put("birthDate", bDate);
            json.put("address", address);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PATCH, url, json,
                response -> {
                    patchUserProfileResponse.onPatchUserProfileResponse(true, "");
                },
                error -> {
                    patchUserProfileResponse.onPatchUserProfileResponse(false, context.getString(R.string.apiMethods_VolleyError));
                }
        ){
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
    // <editor-fold desc="PATCH USER">
    private PatchUserResponse patchUserResponse;
    public interface PatchUserResponse{
        void onPatchUserResponse(boolean response, String message, Context context);
    }
    public void setPatchUserResponse(PatchUserResponse listenner){
        this.patchUserResponse = listenner;
    }
    public void patchUser(Context context, UserInfosEntity user, String username, String email) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/users/"+user.userId;

        SharedPreferences prefs = context.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String pass = prefs.getString(user.email, "");

        JSONObject json = new JSONObject();
        try{
            json.put("username", username);
            json.put("email", email);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PATCH, url, json,
                response -> {
                    patchUserResponse.onPatchUserResponse(true, "", context);
                },
                error -> {
                    patchUserResponse.onPatchUserResponse(false, context.getString(R.string.apiMethods_VolleyError), context);
                }
        ){
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
    // <editor-fold desc="POST METER">
    private PostMeterResponse postMeterResponse;
    public interface PostMeterResponse{
        void onPostMeterResponse(boolean response, String message);
    }
    public void setPostMeterResponse(PostMeterResponse listenner){
        this.postMeterResponse = listenner;
    }
    public void postMeter(Context context, UserInfosEntity user, MeterEntity meter) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://172.22.21.222/watertrack/backend/web/api/meters";

        SharedPreferences prefs = context.getSharedPreferences("Perf_User", MODE_PRIVATE);
        String pass = prefs.getString(user.email, "");

        JSONObject json = new JSONObject();
        try{
            json.put("address", meter.address);
            json.put("userID", user.userId);
            json.put("meterTypeID", meter.meterTypeID);
            json.put("enterpriseID", meter.enterpriseID);
            json.put("class", meter.classe);
            json.put("instalationDate", meter.instalationDate);
            json.put("shutdownDate", null);
            json.put("maxCapacity", meter.maxCapacity);
            json.put("measureUnity", meter.measureUnity);
            json.put("supportedTemperature", meter.supportedTemperature);
            json.put("state", meter.state);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, json,
                response -> {
                    postMeterResponse.onPostMeterResponse(true, "");
                },
                error -> {
                    postMeterResponse.onPostMeterResponse(false, context.getString(R.string.apiMethods_VolleyError));
                }
        ){
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
}
