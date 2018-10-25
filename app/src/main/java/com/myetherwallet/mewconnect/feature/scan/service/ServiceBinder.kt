package com.myetherwallet.mewconnect.feature.scan.service

import android.app.Service
import android.os.Binder

/**
 * Created by BArtWell on 17.07.2018.
 */

class ServiceBinder<T : Service>(val service: T) : Binder()