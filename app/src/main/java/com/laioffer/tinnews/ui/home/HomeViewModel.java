package com.laioffer.tinnews.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.repository.NewsRepository;

public class HomeViewModel extends ViewModel {
    private final NewsRepository repository;
    private final MutableLiveData<String> countryInput = new MutableLiveData<>();

    public HomeViewModel(NewsRepository newsRepository) {
        this.repository = newsRepository;
    }

    public void setCountryInput(String country) {
        // 可以开pump，可以关pump。
        // 一个按键，显示cn，us，rus，euro的新闻。
        countryInput.setValue(country);
    }

    public LiveData<NewsResponse> getTopHeadlines() {
        // 输出新闻列表。把CountryInput丢给 repository::getTopHeadlines这个method.
        // repository::getTopHeadlines 是一个method reference ==> lambda function.
        // 只有特别时候 lambda可以用method reference替代.
        // countryInput输入管道插入到getTopHeadlines这个输出管道, 使得getTopHeadlines吐数据出来.
        return Transformations.switchMap(countryInput, repository::getTopHeadlines);
    }

    public void setFavoriteArticleInput(Article article) {
        // 不需要transform，因为我们HomeFragment不需要也不想observe回来的成功/失败.
        // 不想Database告诉我return结果是什么，因为在另一个地方看得到.
        repository.favoriteArticle(article);
    }
}
