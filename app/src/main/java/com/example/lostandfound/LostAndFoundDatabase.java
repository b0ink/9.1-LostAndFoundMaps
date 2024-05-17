package com.example.lostandfound;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {LostItem.class}, version = 2)
public abstract class LostAndFoundDatabase extends RoomDatabase {
    public abstract LostItemDao lostItemDao();
}
