package org.ifaco.smsexporter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.ifaco.smsexporter.data.Contact
import org.ifaco.smsexporter.data.SMS

class Model : ViewModel() {
    val threads: MutableLiveData<List<SMS.Thread>?> by lazy { MutableLiveData<List<SMS.Thread>?>() }
    val viewThread: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }
    val contacts: MutableLiveData<List<Contact>?> by lazy { MutableLiveData<List<Contact>?>() }


    @Suppress("UNCHECKED_CAST")
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(Model::class.java)) {
                val key = "Model"
                return if (hashMapViewModel.containsKey(key)) getViewModel(key) as T
                else {
                    addViewModel(key, Model())
                    getViewModel(key) as T
                }
            }
            throw IllegalArgumentException("Unknown Model class")
        }

        companion object {
            val hashMapViewModel = HashMap<String, ViewModel>()

            fun addViewModel(key: String, viewModel: ViewModel) =
                hashMapViewModel.put(key, viewModel)

            fun getViewModel(key: String): ViewModel? = hashMapViewModel[key]
        }
    }
}
