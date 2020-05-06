package com.myetherwallet.mewconnect.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myetherwallet.mewconnect.feature.buy.viewmodel.BuyViewModel
import com.myetherwallet.mewconnect.feature.buy.viewmodel.HistoryViewModel
import com.myetherwallet.mewconnect.feature.main.viewmodel.IntroViewModel
import com.myetherwallet.mewconnect.feature.main.viewmodel.WalletViewModel
import com.myetherwallet.mewconnect.feature.register.viewmodel.GeneratingViewModel
import com.myetherwallet.mewconnect.feature.scan.viewmodel.ConfirmTransactionViewModel
import com.myetherwallet.mewconnect.feature.scan.viewmodel.ScanViewModel
import com.myetherwallet.mewconnect.feature.scan.viewmodel.SignMessageViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(WalletViewModel::class)
    abstract fun bindsWalletViewModel(viewModel: WalletViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GeneratingViewModel::class)
    abstract fun bindsGeneratingViewModel(viewModel: GeneratingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ScanViewModel::class)
    abstract fun bindsScanViewModel(viewModel: ScanViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConfirmTransactionViewModel::class)
    abstract fun bindsConfirmTransactionViewModel(viewModel: ConfirmTransactionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignMessageViewModel::class)
    abstract fun bindsSignMessageViewModel(viewModel: SignMessageViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BuyViewModel::class)
    abstract fun bindsBuyViewModel(viewModel: BuyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    abstract fun bindsHistoryViewModel(viewModel: HistoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IntroViewModel::class)
    abstract fun bindsIntroViewModel(viewModel: IntroViewModel): ViewModel
}
