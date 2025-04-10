package com.parithidb.parithiseekho.data.di

import android.content.Context
import com.parithidb.parithiseekho.data.database.AppDatabase
import com.parithidb.parithiseekho.data.repository.AnimeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityModule {
    @Provides
    @ActivityRetainedScoped
    fun provideUserRepository(
        @ApplicationContext context: Context,
        database: AppDatabase
    ): AnimeRepository {
        return AnimeRepository(context, database)
    }
}