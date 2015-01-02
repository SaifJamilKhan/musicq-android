package com.haystack.saifkhan.haystack.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.haystack.saifkhan.haystack.Models.MusicQLoginCall;
import com.haystack.saifkhan.haystack.Models.MusicQPlayList;
import com.haystack.saifkhan.haystack.Models.MusicQSong;
import com.haystack.saifkhan.haystack.Models.MusicQUser;
import com.haystack.saifkhan.haystack.R;
import com.squareup.mimecraft.FormEncoding;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

/**
 * Created by saifkhan on 14-11-23.
 */
public class NetworkUtils {

    private static final String baseURL = "https://musicqtwo.herokuapp.com";
    private static final String registerationsPath = "/registrations";
    private static final String loginPath = "/sessions";
    private static final String playlistPath = "/playlists";
    private static final String videoPath = "/videos";

    public abstract static class CreateAccountListener {
        public abstract void didCreateUser(MusicQUser user);

        public abstract void didFailWithMessage(String message);

    }

    public abstract static class NetworkCallListener {
        public abstract void didSucceed();
        public abstract void didSucceedWithJson(JSONObject body);
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

    public static void createPlaylist(final MusicQPlayList playlistRequest, NetworkCallListener listener, Context context) {
        new CreateObjectTask(playlistRequest, listener, playlistPath, context).execute("");
    }

    public static void createVideo(final MusicQSong songRequest, NetworkCallListener listener, Context context) {
        new CreateObjectTask(songRequest, listener, videoPath, context).execute("");
    }

    public static void showPlaylist(String id, final NetworkCallListener listener, Context context) {
        new ShowObjectTask(id, listener, playlistPath, context).execute("");
    }

    public static void getAllPlaylists(final NetworkCallListener listener, final Context context) {
        new GetListTask(new NetworkCallListener() {
            @Override
            public void didSucceed() {
                listener.didFailWithMessage(context.getString(R.string.error));
            }

            @Override
            public void didSucceedWithJson(JSONObject body) {
                Gson gson = new Gson();
                try {
                    JSONArray playlistJSONArray = body.getJSONArray("playlists");
                    SharedPreferences.Editor sharedPrefEditor = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE ).edit();
                    sharedPrefEditor.putString( "mostRecentPlaylists", playlistJSONArray.toString()).apply();

                    for (int x = 0; x < playlistJSONArray.length(); x++) {
                        JSONObject object = playlistJSONArray.getJSONObject(x);

                        final MusicQPlayList playlist = gson.fromJson(object.toString(), MusicQPlayList.class);
                        if (!TextUtils.isEmpty(playlist.id)) {
                            DatabaseManager.getDatabaseManager().addObject(playlist);
                        } else {
                            Timber.v("");

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.didSucceed();
            }

            @Override
            public void didFailWithMessage(String message) {
                listener.didFailWithMessage(message);
            }
        }, playlistPath, context).execute("");
    }

    public static class GetListTask extends AsyncTask<String, Void, String> {

        private final NetworkCallListener listener;
        private final String path;
        private final SharedPreferences sharedPreference;

        public GetListTask(NetworkCallListener listener, String path, Context context) {
            this.listener = listener;
            this.path = path;
            this.sharedPreference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        }

        protected String doInBackground(String... urls) {
            try {

                OkHttpClient client = new OkHttpClient();
                List<NameValuePair> params = new LinkedList<NameValuePair>();

                params.add(new BasicNameValuePair("user_token", sharedPreference.getString("auth_token", "")));
                params.add(new BasicNameValuePair("user_email", sharedPreference.getString("email", "")));

                String paramString = URLEncodedUtils.format(params, "utf-8");

                Request request = new Request.Builder()
                        .url(baseURL.concat(path + "?" + paramString))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();
                Response response;
                try {
                    response = client.newCall(request).execute();
                } catch (Exception e) {
                    listener.didFailWithMessage("No internet?");
                    return null;
                }
                if(response.code() >= 200 && response.code() < 300) {
                    JSONObject bodyJSON = new JSONObject(response.body().string());
                    listener.didSucceedWithJson(bodyJSON);
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

    public static class CreateObjectTask extends AsyncTask<String, Void, String> {

        private final Object requestObject;
        private final NetworkCallListener listener;
        private final String path;
        private final SharedPreferences sharedPreference;

        public CreateObjectTask(Object requestObject, NetworkCallListener listener, String path, Context context) {
            this.requestObject = requestObject;
            this.listener = listener;
            this.path = path;
            //?user_token=TteKVCjYX-_bxvNo1HBt
            this.sharedPreference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        }

        protected String doInBackground(String... urls) {
            try {

                OkHttpClient client = new OkHttpClient();
                List<NameValuePair> params = new LinkedList<NameValuePair>();

                params.add(new BasicNameValuePair("user_token", sharedPreference.getString("auth_token", "")));
                params.add(new BasicNameValuePair("user_email", sharedPreference.getString("email", "")));

                String paramString = URLEncodedUtils.format(params, "utf-8");

                Gson gson = new Gson();
                RequestBody body = RequestBody.create(JSON, gson.toJson(requestObject));
                Request request = new Request.Builder()
                        .url(baseURL.concat(path + "?" + paramString))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .post(body)
                        .build();
                Response response;
                try {
                    response = client.newCall(request).execute();
                } catch (Exception e) {
                    listener.didFailWithMessage("No internet?");
                    return null;
                }
                if(response.code() >= 200 && response.code() < 300) {
                    JSONObject bodyJSON = new JSONObject(response.body().string());
                    listener.didSucceedWithJson(bodyJSON);
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

    public static class ShowObjectTask extends AsyncTask<String, Void, String> {

        private final String requestID;
        private final NetworkCallListener listener;
        private final String path;
        private final SharedPreferences sharedPreference;

        public ShowObjectTask(String requestID, NetworkCallListener listener, String path, Context context) {
            this.requestID = requestID;
            this.listener = listener;
            this.path = path;
            //?user_token=TteKVCjYX-_bxvNo1HBt
            this.sharedPreference = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        }

        protected String doInBackground(String... urls) {
            try {

                OkHttpClient client = new OkHttpClient();
                List<NameValuePair> params = new LinkedList<NameValuePair>();

                params.add(new BasicNameValuePair("user_token", sharedPreference.getString("auth_token", "")));
                params.add(new BasicNameValuePair("user_email", sharedPreference.getString("email", "")));

                String paramString = URLEncodedUtils.format(params, "utf-8");

                Request request = new Request.Builder()
                        .url(baseURL.concat(path + "/" + requestID + "?" + paramString))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();
                Response response;
                try {
                    response = client.newCall(request).execute();
                } catch (Exception e) {
                    listener.didFailWithMessage("No internet?");
                    return null;
                }

                if(response.code() >= 200 && response.code() < 300) {
                    JSONObject bodyJSON = new JSONObject(response.body().string());
                    listener.didSucceedWithJson(bodyJSON);
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


    public static class LoginTask extends AsyncTask<String, Void, String> {


        private final Object requestObject;
        private final NetworkCallListener listener;
        private final SharedPreferences mSharedPrefs;

        public LoginTask(Object requestObject, NetworkCallListener listener, Context context) {
            this.requestObject = requestObject;
            this.listener = listener;
            mSharedPrefs = context.getSharedPreferences(
                    context.getPackageName(), Context.MODE_PRIVATE);
        }

        protected String doInBackground(String... urls) {
            try {

                OkHttpClient client = new OkHttpClient();
                Gson gson = new Gson();
                JSONObject userJSON = new JSONObject();
                userJSON.put("user",  new JSONObject(gson.toJson(requestObject)));
                RequestBody body = RequestBody.create(JSON, userJSON.toString());
                Request request = new Request.Builder()
                        .url(baseURL.concat(loginPath))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .post(body)
                        .build();
                Response response;
                try {
                    response = client.newCall(request).execute();
                } catch (Exception e) {
                    listener.didFailWithMessage("No internet?");
                    return null;
                }
                if(response.code() >= 200 && response.code() < 300) {
                    JSONObject bodyJSON = new JSONObject(response.body().string());
                    if(bodyJSON.has("data")) {
                        if(bodyJSON.getJSONObject("data").has("auth_token") && bodyJSON.getJSONObject("data").has("name")) {
                            String authToken = bodyJSON.getJSONObject("data").getString("auth_token");
                            String name = bodyJSON.getJSONObject("data").getString("name");
                            String email = bodyJSON.getJSONObject("data").getString("email");
                            if (name != null && authToken != null && email != null) {
                                mSharedPrefs.edit().putString("auth_token", authToken).apply();
                                mSharedPrefs.edit().putString("name", name).apply();
                                mSharedPrefs.edit().putString("email", email).apply();
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
                Response response;
                try {
                    response = client.newCall(request).execute();
                } catch (Exception e) {
                    listener.didFailWithMessage("No internet?");
                    return null;
                }
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
