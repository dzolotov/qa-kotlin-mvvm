package tech.dzolotov.counterappmvvm

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class CoroutineDispatcherOverride

@InstallIn(ActivityRetainedComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    @ActivityRetainedScoped
    abstract fun bindDescription(impl: DescriptionRepository): IDescriptionRepository
}

@InstallIn(SingletonComponent::class)
@Module
object ScopeModule {
    @Provides
    @Singleton
    @CoroutineDispatcherOverride
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
