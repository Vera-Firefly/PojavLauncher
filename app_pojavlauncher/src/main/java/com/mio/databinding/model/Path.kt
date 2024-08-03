package com.mio.databinding.model

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField

data class Path(var name: ObservableField<String>, var path: ObservableField<String>, var selected: ObservableBoolean)