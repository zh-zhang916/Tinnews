package com.laioffer.tinnews.ui.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.FragmentSearchBinding;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;


public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    // 根据xml layout自动生成
    private FragmentSearchBinding binding;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 你在这里是要给各种views render到屏幕上.
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_search, container, false);
        // 外面那层container，其实就是FragmentContainerView, 在activity_main.xml.
        // 我拿着打气筒把我这个xml生成的java class吹满整个container,并且以一个tree的结构实现出来.
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        // 就像dom tree一样，先拿最上层节点，然后自动找.
        return binding.getRoot(); // return这个fragment subtree.
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set up binding.recyclerView
        SearchNewsAdapter newsAdapter = new SearchNewsAdapter();
        // given by Android, (getContext and 2 columns).
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        binding.newsResultsRecyclerView.setLayoutManager(gridLayoutManager);
        binding.newsResultsRecyclerView.setAdapter(newsAdapter);

        binding.newsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 你按enter了就进入这里.
                if (!query.isEmpty()) {
                    // 用callback to hook up viewModel.setSearchInput("google").
                    // 这是一个异步操作，你在这里的时候viewModel已经创建完了,
                    // 请参照fragment life cycle那张图.
                    viewModel.setSearchInput(query);
                }
                // 没有就会被call两次.
                binding.newsSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 每敲一个字母run一次，连起来的.
                // 改变输入return false ==> 不处理该情况
                return false;
            }
        });

        //用这个repository创建SearchViewModel.
        NewsRepository repository = new NewsRepository();
        //viewModel = new SearchViewModel(repository);
        // retain them in a store of the given {@code ViewModelStoreOwner which is this}
        // Provider好处就是帮你保留states.
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository))
                .get(SearchViewModel.class);

        //viewModel.setSearchInput("google"); move up to connect with binding.
        viewModel
                .searchNews() // 返回一个liveData.
                .observe(
                        // LiveData自己 manage life cycle,在哪里打开就会在对应的event里面关掉.
                        getViewLifecycleOwner(),
                        // 一开始是空的,observe后有新数据了会再次call这个lambda function.
                        // 自动化了很多东西, 这个就相当于一个listen function.
                        newsResponse -> {
                            if (newsResponse != null) {
                                Log.d("SearchFragment", newsResponse.toString());
                                // viewModel拿到结果后，传进Adapter里面, 然后notifyDataSetChanged();
                                newsAdapter.setArticles(newsResponse.articles);
                            }
                        });

        newsAdapter.setItemCallback(article -> {
            SearchFragmentDirections.ActionNavigationSearchToNavigationDetails
                    direction = SearchFragmentDirections.actionNavigationSearchToNavigationDetails(article);
            NavHostFragment.findNavController(SearchFragment.this).navigate(direction);
        });
    }
}