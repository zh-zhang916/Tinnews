package com.laioffer.tinnews.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.laioffer.tinnews.model.Article;

// exportSchema: 需不需要放在folder里面：不需要.
@Database(entities = {Article.class}, version = 1, exportSchema = false)
public abstract class TinNewsDatabase extends RoomDatabase {
    public abstract ArticleDao articleDao(); // 用Dao这个class去拿我的数据.
}