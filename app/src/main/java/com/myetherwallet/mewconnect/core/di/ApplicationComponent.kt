package com.myetherwallet.mewconnect.core.di

import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.core.di.viewmodel.ViewModelModule
import com.myetherwallet.mewconnect.core.persist.database.DatabaseModule
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesModule
import com.myetherwallet.mewconnect.core.ui.view.StaticToolbar
import com.myetherwallet.mewconnect.feature.auth.fragment.*
import com.myetherwallet.mewconnect.feature.backup.fragment.*
import com.myetherwallet.mewconnect.feature.buy.fragment.BuyFragment
import com.myetherwallet.mewconnect.feature.buy.fragment.HistoryFragment
import com.myetherwallet.mewconnect.feature.main.activity.MainActivity
import com.myetherwallet.mewconnect.feature.main.dialog.RateDialog
import com.myetherwallet.mewconnect.feature.main.fragment.*
import com.myetherwallet.mewconnect.feature.main.view.WalletCardView
import com.myetherwallet.mewconnect.feature.register.fragment.GeneratingFragment
import com.myetherwallet.mewconnect.feature.scan.fragment.*
import com.myetherwallet.mewconnect.feature.scan.service.SocketService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationModule::class,
    ViewModelModule::class,
    PreferencesModule::class,
    DatabaseModule::class
])
interface ApplicationComponent {

    fun inject(application: MewApplication)

    fun inject(service: SocketService)

    fun inject(activity: MainActivity)

    fun inject(fragment: WalletFragment)

    fun inject(fragment: AddressFragment)

    fun inject(fragment: InfoFragment)

    fun inject(fragment: ViewRecoveryPhraseFragment)

    fun inject(fragment: IntroFragment)

    fun inject(fragment: GeneratingFragment)

    fun inject(scanFragment: ScanFragment)

    fun inject(fragment: SignMessageFragment)

    fun inject(fragment: MessageSignedFragment)

    fun inject(fragment: ConfirmTransactionFragment)

    fun inject(fragment: TransactionConfirmedFragment)

    fun inject(fragment: TransactionDeclinedFragment)

    fun inject(fragment: AuthFragment)

    fun inject(fragment: ForgotPasswordFragment)

    fun inject(fragment: RestoreExistingWalletFragment)

    fun inject(fragment: DoYouHavePhraseFragment)

    fun inject(fragment: SafetyPriorityFragment)

    fun inject(fragment: EnterRecoveryPhraseFragment)

    fun inject(fragment: WriteTheseFragment)

    fun inject(fragment: DoubleCheckFragment)

    fun inject(fragment: PrepareWriteFragment)

    fun inject(fragment: WalletBackedUpFragment)

    fun inject(fragment: BackUpWalletFragment)

    fun inject(fragment: BuyFragment)

    fun inject(fragment: HistoryFragment)

    fun inject(fragment: FingerprintAvailableFragment)

    fun inject(fragment: FingerprintEnabledFragment)

    fun inject(fragment: WhatsNewFragment)

    fun inject(dialog: RateDialog)

    fun inject(view: WalletCardView)

    fun inject(view: StaticToolbar)
}
