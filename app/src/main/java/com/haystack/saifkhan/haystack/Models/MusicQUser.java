package com.haystack.saifkhan.haystack.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by saifkhan on 14-11-23.
 */
public class MusicQUser {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("password")
    public String password;

    @SerializedName("password_confirmation")
    public String passwordConfirmation;

    @SerializedName("authentication_token")
    public String authToken;
}
