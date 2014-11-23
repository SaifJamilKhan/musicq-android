package com.haystack.saifkhan.haystack;

        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;
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

    public abstract static class CreateAccountListener {
        public abstract void didCreateUser(MusicQUser user);
        public abstract void didFailWithMessage(String message);

    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static void createAccountCall(final MusicQUser user, final CreateAccountListener listener) throws IOException {
        Thread thread = new Thread()
        {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    Gson gson = new Gson();

                    RequestBody body = RequestBody.create(JSON, gson.toJson(user));
                    Request request = new Request.Builder()
                            .url(baseURL.concat(registerationsPath))
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    switch (response.code()) {
                        case 500:
                            listener.didFailWithMessage("Unknown error. Please try again");
                        case 422:
                            JSONObject responseJSON = new JSONObject(response.body().string());
                            JSONObject info = responseJSON.getJSONObject("info");
                            if(info.has("email")) {
                                listener.didFailWithMessage("Email Taken!");
                            } else if(info.has("name")) {
                                listener.didFailWithMessage("Name Taken!");
                            } else if(info.has("password_confirmation")) {
                                listener.didFailWithMessage("Password Mismatch");
                            } else {
                                listener.didFailWithMessage("Unknown error. Please try again");
                            }
                         default:
                             listener.didFailWithMessage("Unknown error. Please try again");
                    }
                    MusicQUser user = gson.fromJson(response.body().string(), MusicQUser.class);
                    listener.didCreateUser(user);

                } catch (IOException e) {
                    e.printStackTrace();
                    listener.didFailWithMessage("Unknown error. Please try again");
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.didFailWithMessage("Unknown error. Please try again");
                }
            }
        };
        thread.run();

//        return response.body().string();
    }
}
