package com.laioffer.tinnews.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.FragmentHomeBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.List;

public class HomeFragment extends Fragment implements CardStackListener {

    private HomeViewModel viewModel;
    private FragmentHomeBinding binding;
    private CardStackLayoutManager layoutManager;
    // 需要知道哪张卡片被点了.
    private List<Article> articles;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false); // xml实例化成java objects.
        return binding.getRoot(); // return这个fragment subtree.
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup CardStackView
        CardSwipeAdapter swipeAdapter = new CardSwipeAdapter();
        layoutManager = new CardStackLayoutManager(requireContext(), this);
        // 出现叠层的效果.
        layoutManager.setStackFrom(StackFrom.Top);
        binding.homeCardStackView.setLayoutManager(layoutManager);
        binding.homeCardStackView.setAdapter(swipeAdapter);

        // Handle like unlike button clicks
        binding.homeLikeButton.setOnClickListener(v -> swipeCard(Direction.Right));
        binding.homeUnlikeButton.setOnClickListener(v -> swipeCard(Direction.Left));

        // 用这个repository创建HomeViewModel.
        NewsRepository repository = new NewsRepository();

        // viewModel = new HomeViewModel(repository);
        // retain them in a store of the given {@code ViewModelStoreOwner which is "this"}
        // Provider好处就是帮你保留states.
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository))
                .get(HomeViewModel.class);

        // 一启动就给输入input.
        viewModel.setCountryInput("us");

        viewModel
                .getTopHeadlines()
                .observe(
                        // LiveData自己 manage life cycle,在哪里打开就会在对应的event里面关掉.
                        getViewLifecycleOwner(),
                        // 一开始是空的,observe后有新数据了会再次call这个lambda function.
                        // 现在就暂时print出来就好了, 自动化了很多东西, 这个就相当于一个listen function.
                        newsResponse -> {
                            if (newsResponse != null) {
                                Log.d("HomeFragment", newsResponse.toString());
                                // 储存一个copy，为了知道我们点的是哪一个卡片, 后面用的到.
                                articles = newsResponse.articles;
                                swipeAdapter.setArticles(articles);
                            }
                        });
    }

    private void swipeCard(Direction direction) {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(direction)
                .setDuration(Duration.Normal.duration)
                .build();
        // 先更新一下滑动的setting往哪里滑.
        layoutManager.setSwipeAnimationSetting(setting);
        // 执行滑动.
        binding.homeCardStackView.swipe();
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (direction == Direction.Left) {
            Log.d("CardStackView", "Unliked " + layoutManager.getTopPosition());
        } else if (direction == Direction.Right) {
            Log.d("CardStackView", "Liked " + layoutManager.getTopPosition());
            // index 从1开始的,需要一个offset -1.
            Article article = articles.get(layoutManager.getTopPosition() - 1);
            // 这个函数返回null.
            viewModel.setFavoriteArticleInput(article);
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }
}