package com.grupok.watertrack.scripts.apiCRUD;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.grupok.watertrack.R;
import com.grupok.watertrack.database.entities.EnterpriseEntity;
import com.grupok.watertrack.database.entities.MeterEntity;
import com.grupok.watertrack.database.entities.MeterTypeEntity;
import com.grupok.watertrack.database.entities.UserInfosEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class APIMethods {
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
    //-------------------------------------------------------------------------------------------

    //------------------------------------------LOGIN--------------------------------------------
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
    //-------------------------------------------------------------------------------------------

    //-----------------------------------------SIGN UP-------------------------------------------
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
    //-------------------------------------------------------------------------------------------

    //-----------------------------------------GET METERS-------------------------------------------
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
                            MeterEntity contador = new MeterEntity("N/A",
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
                        getMetersResponse.onGetMetersResponse(true, "", list);
                    } catch (JSONException e) {
                        getMetersResponse.onGetMetersResponse(false, context.getString(R.string.apiMethods_JsonParseError), null);
                    }
                },
                error -> {
                    getMetersResponse.onGetMetersResponse(false, context.getString(R.string.apiMethods_VolleyError), null);
                }
        );

        queue.add(request);
    }
    //-------------------------------------------------------------------------------------------

    //-----------------------------------------GET ENTERPRISE BY ID-------------------------------------------
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
    //-------------------------------------------------------------------------------------------

    //-----------------------------------------GET USER BY ID-------------------------------------------
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
    //-------------------------------------------------------------------------------------------

    //-----------------------------------------GET METER TYPE BY ID-------------------------------------------
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
    //-------------------------------------------------------------------------------------------
}
