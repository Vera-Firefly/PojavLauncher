package com.mio.databinding.model

import android.database.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField

class Path(var name: ObservableField<String>, var path: ObservableField<String>, var selected: ObservableBoolean) {
}