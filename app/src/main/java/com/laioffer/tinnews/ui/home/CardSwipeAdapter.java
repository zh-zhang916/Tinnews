package com.laioffer.tinnews.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.SwipeNewsCardBinding;
import com.laioffer.tinnews.model.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardSwipeAdapter extends RecyclerView.Adapter<CardSwipeAdapter.CardSwipeViewHolder> {
    // 1. Supporting data:
    private List<Article> articles = new ArrayList<>();

    public void setArticles(List<Article> newsList) {
        articles.clear();
        articles.addAll(newsList);
        // 一开始Adaptor是空的.
        // 要告诉Adaptor有新的数据变化了，然后Adaptor告诉CardStackView.
        // 就像refresh一下.
        notifyDataSetChanged();
    }

    // 2. Adapter overrides:
    @NonNull
    @Override
    public CardSwipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 在你的CardStackView里面去生成一个新的view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_news_card, parent, false);
        // 用一个viewHolder装起来, 有多少view就有多少viewHolder.
        return new CardSwipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardSwipeViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.titleTextView.setText(article.title);
        holder.descriptionTextView.setText(article.description);
        if (article.urlToImage != null && article.urlToImage.length() != 0) {
            Picasso.get().load(article.urlToImage).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }


    // 3. CardSwipeViewHolder:
    public static class CardSwipeViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titleTextView;
        TextView descriptionTextView;

        public CardSwipeViewHolder(@NonNull View itemView) {
            super(itemView);
            // findViewById是需要花时间的, 这里做一次就好了.
            SwipeNewsCardBinding binding = SwipeNewsCardBinding.bind(itemView);
            // 记录第一次findViewById的binding信息, 放在holder里面.
            imageView = binding.swipeCardImageView;
            titleTextView = binding.swipeCardTitle;
            descriptionTextView = binding.swipeCardDescription;
        }
    }
}