package com.example.myapplication.data.network;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;
import com.example.myapplication.data.model.User;

public interface ApiService {
    @GET("users")
    Call<List<User>> getUsers();
}
