package com.laioffer.tinnews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.network.NewsApi;
import com.laioffer.tinnews.network.RetrofitClient;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // could be implemented using binding for good practise.
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        // connect navView to navController.
        NavigationUI.setupWithNavController(navView, navController);
        // show action bar.
        //NavigationUI.setupActionBarWithNavController(this, navController);

//        // 返回一个retrofit instance
//        Retrofit retrofit = RetrofitClient.newInstance();
//        // NewsApi的实例化
//        NewsApi api = retrofit.create(NewsApi.class);
//        // Use an additional thread to run this.
//        // do something if success, do something if fail.
//        api.getTopHeadlines("US").enqueue(new Callback<NewsResponse>() {
//            @Override
//            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
//                if (response.isSuccessful()) {
//                    // body只有在成功了的时候有body.
//                    Log.d("getTopHeadlines", response.body().toString());
//                } else {
//                    Log.d("getTopHeadlines", response.toString());
//                }
//            }
//        // 网断了或者Gson没map成功
//            @Override
//            public void onFailure(Call<NewsResponse> call, Throwable t) {
//                Log.d("getTopHeadlines", t.toString());
//            }
//        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // 返回箭头的功能...
        return navController.navigateUp();
    }
}
