package com.laioffer.tinnews.repository;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.laioffer.tinnews.TinNewsApplication;
import com.laioffer.tinnews.database.TinNewsDatabase;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.network.NewsApi;
import com.laioffer.tinnews.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {

    private final NewsApi newsApi;
    private final TinNewsDatabase database;

    // 原来是传context进来，newRepository(context)...
    public NewsRepository() {
        // newsApi被实例化了,这个create去实现了NewsApi的方法.
        // retrofit's instance is actually the implementation of the api interface.
        newsApi = RetrofitClient.newInstance().create(NewsApi.class);
        database = TinNewsApplication.getDatabase();
    }

    public LiveData<NewsResponse> getTopHeadlines(String country) {
        // 这个有点像listener，高级的listener.
        MutableLiveData<NewsResponse> topHeadlinesLiveData = new MutableLiveData<>();
        // 这个是异步的function，多一个thread去操作.
        newsApi.getTopHeadlines(country)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        if (response.isSuccessful()) {
                            topHeadlinesLiveData.setValue(response.body());
                        } else {
                            topHeadlinesLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        topHeadlinesLiveData.setValue(null);
                    }
                });

        // 随着api calls的返回结果改变而改变.
        return topHeadlinesLiveData;
    }

    public LiveData<NewsResponse> searchNews(String query) {
        MutableLiveData<NewsResponse> everyThingLiveData = new MutableLiveData<>();
        newsApi.getEverything(query, 40)
                .enqueue(
                        new Callback<NewsResponse>() {
                            @Override
                            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                                if (response.isSuccessful()) {
                                    everyThingLiveData.setValue(response.body());
                                } else {
                                    everyThingLiveData.setValue(null);
                                }
                            }

                            @Override
                            public void onFailure(Call<NewsResponse> call, Throwable t) {
                                everyThingLiveData.setValue(null);
                            }
                        });
        return everyThingLiveData;
    }

    public LiveData<List<Article>> getAllSavedArticles() {
        // 因为用LiveData放回UI thread上面.
        // 自动开个background thread去读.
        // 然后帮你做extra step，把data从background thread挪回到UI thread上面.
        return database.articleDao().getAllArticles();
    }

    public void deleteSavedArticle(Article article) {
        // 这个还是一个Async function.
        // 虽然格式像是一个call back function，但是实际上是一个object.
        // 其实不是call back function, 是一个internal runnable object.
        // doInBackground()里面做runnable的事情.
        // runnable.run() ========v
        AsyncTask.execute(() -> database.articleDao().deleteArticle(article));
    }

    public LiveData<Boolean> favoriteArticle(Article article) {
        // 创建一个空的LiveData
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        // 先用空的resultLiveData去set database，等尝试update database结束后，把success还是failure update进
        // LiveData，返回出去.
        new FavoriteAsyncTask(database, resultLiveData).execute(article);
        return resultLiveData;
    }

    // create an async task to save a article do db.
    // 这个background thread运行的task传入一个Article，并不需要publish任何progress units(void), 结束后返回一个boolean
    private static class FavoriteAsyncTask extends AsyncTask<Article, Void, Boolean> {

        private final TinNewsDatabase database;
        private final MutableLiveData<Boolean> liveData;

        private FavoriteAsyncTask(TinNewsDatabase database, MutableLiveData<Boolean> liveData) {
            this.database = database;
            this.liveData = liveData;
        }

        // 看笔记...
        @Override
        // 开一个新的thread做这个事情.
        protected Boolean doInBackground(Article... articles) {
            Article article = articles[0];
            try {
                database.articleDao().saveArticle(article);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        // 回到UI thread的时候做这个事情.
        protected void onPostExecute(Boolean success) {
            liveData.setValue(success);
        }
    }
}