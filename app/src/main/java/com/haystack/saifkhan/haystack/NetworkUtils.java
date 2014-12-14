package com.haystack.saifkhan.haystack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haystack.saifkhan.haystack.Models.MusicQLoginCall;
import com.haystack.saifkhan.haystack.Models.MusicQUser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by saifkhan on 14-11-23.
 */
public class NetworkUtils {

    private static final String baseURL = "https://musicqtwo.herokuapp.com";
    private static final String registerationsPath = "/registrations";
    private static final String loginPath = "/sessions";

    public abstract static class CreateAccountListener {
        public abstract void didCreateUser(MusicQUser user);

        public abstract void didFailWithMessage(String message);

    }

    public abstract static class NetworkCallListener {
        public abstract void didSucceed();

        public abstract void didFailWithMessage(String message);

    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static void createAccountCall(final MusicQUser user, final CreateAccountListener listener) throws IOException {
        new CreateAccountTask(user, listener).execute("");
    }

    public static void loginCall(final MusicQLoginCall loginCall, NetworkCallListener listener, Context context) {
        new LoginTask(loginCall, listener, context).execute("");
    }

    public static class LoginTask extends AsyncTask<String, Void, String> {


        private final MusicQLoginCall loginCall;
        private final NetworkCallListener listener;
        private final SharedPreferences mSharedPrefs;

        public LoginTask(MusicQLoginCall loginCall, NetworkCallListener listener, Context context) {
            this.loginCall = loginCall;
            this.listener = listener;
            mSharedPrefs = context.getSharedPreferences(
                    context.getPackageName(), Context.MODE_PRIVATE);
        }

        protected String doInBackground(String... urls) {
            try {

                OkHttpClient client = new OkHttpClient();
                Gson gson = new Gson();
                JSONObject userJSON = new JSONObject();
                userJSON.put("user",  new JSONObject(gson.toJson(loginCall)));
                RequestBody body = RequestBody.create(JSON, userJSON.toString());
                Request request = new Request.Builder()
                        .url(baseURL.concat(loginPath))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();

                if(response.code() >= 200 && response.code() < 300) {
                    JSONObject bodyJSON = new JSONObject(response.body().string());
                    if(bodyJSON.has("data")) {
                        if(bodyJSON.getJSONObject("data").has("auth_token") && bodyJSON.getJSONObject("data").has("name")) {
                            String authToken = bodyJSON.getJSONObject("data").getString("auth_token");
                            String name = bodyJSON.getJSONObject("data").getString("name");
                            if (name != null && authToken != null) {
                                mSharedPrefs.edit().putString("auth_token", authToken);
                                mSharedPrefs.edit().putString("name", name).apply();
                                listener.didSucceed();
                                return null;
                            }
                        }
                    }
                    listener.didFailWithMessage("Unknown error. Please try again");
                    return null;
                }

                switch (response.code()) {
                    case 500:
                        listener.didFailWithMessage("Unknown error. Please try again");
                        break;
                    case 401:
                        JSONObject bodyJSON = new JSONObject(response.body().string());
                        if(bodyJSON.has("error")) {
                            listener.didFailWithMessage(bodyJSON.getString("error"));
                        } else {
                            listener.didFailWithMessage("Unknown error. Please try again");
                        }
                        break;
                    default:
                        listener.didFailWithMessage("Unknown error. Please try again");
                        break;
                }


            } catch (IOException e) {
                e.printStackTrace();
                listener.didFailWithMessage("Unknown error. Please try again");
            } catch (JSONException e) {
                e.printStackTrace();
                listener.didFailWithMessage("Unknown error. Please try again");
            }
            return null;
        }

        protected void onPostExecute(String feed) {
        }
    }



    public static class CreateAccountTask extends AsyncTask<String, Void, String> {

        private final CreateAccountListener listener;
        private final MusicQUser user;

        public CreateAccountTask(MusicQUser user, CreateAccountListener listener) {
            this.user = user;
            this.listener = listener;
        }

        protected String doInBackground(String... urls) {
            try {

                OkHttpClient client = new OkHttpClient();
                Gson gson = new Gson();
                JSONObject userJSON = new JSONObject();
                userJSON.put("user",  new JSONObject(gson.toJson(user)));
                RequestBody body = RequestBody.create(JSON, userJSON.toString());
                Request request = new Request.Builder()
                        .url(baseURL.concat(registerationsPath))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();

                if(response.code() >= 200 && response.code() < 300) {
                    JSONObject bodyJSON = new JSONObject(response.body().string());

                    MusicQUser user = gson.fromJson(bodyJSON.getJSONObject("data").getJSONObject("user").toString(), MusicQUser.class);
                    listener.didCreateUser(user);
                    return null;
                }

                switch (response.code()) {
                    case 500:
                        listener.didFailWithMessage("Unknown error. Please try again");
                        break;
                    case 422:
                        JSONObject responseJSON = new JSONObject(response.body().string());
                        JSONObject info = responseJSON.getJSONObject("info");
                        if (info.has("email")) {
                            listener.didFailWithMessage("Email " + info.getJSONArray("email").get(0).toString());
                        } else if (info.has("name")) {
                            listener.didFailWithMessage("Name " + info.getJSONArray("name").get(0).toString());
                        } else if (info.has("password_confirmation")) {
                            listener.didFailWithMessage("Password Mismatch");
                        } else {
                            listener.didFailWithMessage("Unknown error. Please try again");
                        }
                        break;
                    default:
                        listener.didFailWithMessage("Unknown error. Please try again");
                        break;
                }


            } catch (IOException e) {
                e.printStackTrace();
                listener.didFailWithMessage("Unknown error. Please try again");
            } catch (JSONException e) {
                e.printStackTrace();
                listener.didFailWithMessage("Unknown error. Please try again");
            }
            return null;
        }

        protected void onPostExecute(String feed) {
        }
    }
}
