package com.yhpgi.openwrtmonitor.ui.viewModel

import android.app.Application
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.yhpgi.openwrtmonitor.domain.helper.repository.DataStoreRepository
import com.yhpgi.openwrtmonitor.domain.helper.repository.MainRepository
import com.yhpgi.openwrtmonitor.domain.model.ApiResponse
import com.yhpgi.openwrtmonitor.domain.model.ResultExec
import com.yhpgi.openwrtmonitor.network.AndroidCookieJar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class MainViewModel(application: Application) : AndroidViewModel(application = application) {

    private val repository = DataStoreRepository(application)

    val savedThemeString by mutableStateOf(repository.getThemeString.asLiveData())
    val savedIpString by mutableStateOf(repository.getIPString.asLiveData())
    val savedTokenString by mutableStateOf(repository.getTokenString.asLiveData())
    val savedLuciPathString by mutableStateOf(repository.getLuciPathString.asLiveData())
    val savedClashString by mutableStateOf(repository.getClashString.asLiveData())
    var luciConfigChanged by mutableStateOf(false)
    var clashConfigChanged by mutableStateOf(false)

    fun setLuciFalse() {
        luciConfigChanged = false
    }

    fun setClashFalse() {
        luciConfigChanged = false
    }

    fun saveTheme(newThemeString: String) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveThemeString(newThemeString)
        }

    private fun saveIPString(newIpAddress: String) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveIPString(newIpAddress)
            luciConfigChanged = true
            clashConfigChanged = true
        }

    private fun saveTokenString(newToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveTokenString(newToken)
            luciConfigChanged = true
        }

    private fun saveLuciPathString(newLuciPathString: String) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveLuciString(newLuciPathString)
            luciConfigChanged = true
        }

    private fun saveOpenClashString(newClashString: String) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveClashString(newClashString)
            clashConfigChanged = true
        }

    var openEditTextDialog by mutableStateOf(false)
    var openRadioDialog by mutableStateOf(false)

    var title by mutableIntStateOf(MainRepository.INT_DEFAULT_VALUE)
    var description by mutableIntStateOf(MainRepository.INT_DEFAULT_VALUE)
    var placeholder by mutableStateOf(MainRepository.STRING_BLANK)
    private var keyDialog by mutableIntStateOf(MainRepository.INT_DEFAULT_VALUE)

    var hostname: String by mutableStateOf(MainRepository.STRING_LOADING)
    var model: String by mutableStateOf(MainRepository.STRING_LOADING)
    var firmwareVersion: String by mutableStateOf(MainRepository.STRING_LOADING)
    var kernelVersion: String by mutableStateOf(MainRepository.STRING_LOADING)

    fun getSystemInformation() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val savedIpAddress = savedIpString.value
                val savedToken = savedTokenString.value
                val httpClient = OkHttpClient.Builder().cookieJar(AndroidCookieJar()).build()

                val json = """{"method":"exec", "params":["ubus call system board"]}""".trimIndent()

                val requestBody = json.toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("http://$savedIpAddress/cgi-bin/luci/rpc/sys")
                    .post(requestBody)
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseString = response.body.string()
                    val gson = Gson()
                    val apiResponse = gson.fromJson(responseString, ApiResponse::class.java)

                    val result = gson.fromJson(apiResponse.result, ResultExec::class.java)
                    hostname = result?.hostname.toString()
                    model = result?.model.toString()
                    firmwareVersion = result?.release?.version ?: "Unknown"
                    kernelVersion = result?.kernel.toString()
                } else {
                    hostname = MainRepository.STRING_DATA_NOT_FOUND
                    model = MainRepository.STRING_DATA_NOT_FOUND
                    firmwareVersion = MainRepository.STRING_DATA_NOT_FOUND
                    kernelVersion = MainRepository.STRING_DATA_NOT_FOUND
                }
            } catch (e: Exception) {
                hostname = MainRepository.STRING_ERROR
                model = MainRepository.STRING_ERROR
                firmwareVersion = MainRepository.STRING_ERROR
                kernelVersion = MainRepository.STRING_ERROR
            }
        }
    }

    fun openEditTextDialog(
        @StringRes title: Int,
        @StringRes description: Int,
        defaultValue: String,
        key: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            openEditTextDialog = true
            this@MainViewModel.title = title
            this@MainViewModel.description = description
            this@MainViewModel.placeholder = defaultValue
            keyDialog = key
        }
    }

    fun closeEditTextDialog(
        isSave: Boolean,
        newKeyValue: String = "value"
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            openEditTextDialog = false
            if (isSave) {
                when (keyDialog) {
                    0 -> saveIPString(newKeyValue)
                    1 -> saveLuciPathString(newKeyValue)
                    2 -> saveOpenClashString(newKeyValue)
                    3 -> saveTokenString(newKeyValue)
                }
            }
        }
    }

    fun openRadioDialog(title: Int, description: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            openRadioDialog = true
            this@MainViewModel.title = title
            this@MainViewModel.description = description
        }
    }

    fun closeRadioDialog() {
        openRadioDialog = false
    }
}