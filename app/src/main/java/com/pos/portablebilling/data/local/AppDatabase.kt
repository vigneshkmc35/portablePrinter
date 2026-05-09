package com.pos.portablebilling.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pos.portablebilling.data.local.dao.ItemDao
import com.pos.portablebilling.data.local.dao.TransactionDao
import com.pos.portablebilling.data.local.entity.ItemEntity
import com.pos.portablebilling.data.local.entity.TransactionEntity

@Database(entities = [ItemEntity::class, TransactionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "portable_billing_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
