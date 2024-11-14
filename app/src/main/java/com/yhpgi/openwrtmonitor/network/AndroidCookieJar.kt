package com.yhpgi.openwrtmonitor.network

import android.webkit.CookieManager
import androidx.annotation.WorkerThread
import androidx.core.util.Predicate
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidCookieJar : CookieJar {

    private val cookieManager = CookieManager.getInstance()

    @WorkerThread
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val rawCookie = cookieManager.getCookie(url.toString()) ?: return emptyList()
        return rawCookie.split(';').mapNotNull {
            Cookie.parse(url, it)
        }
    }

    @WorkerThread
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isEmpty()) {
            return
        }
        val urlString = url.toString()
        for (cookie in cookies) {
            cookieManager.setCookie(urlString, cookie.toString())
        }
    }

    fun removeCookies(url: HttpUrl, predicate: Predicate<Cookie>?) {
        val cookies = loadForRequest(url)
        if (cookies.isEmpty()) {
            return
        }
        val urlString = url.toString()
        for (c in cookies) {
            if (predicate != null && !predicate.test(c)) {
                continue
            }
            val nc = c.newBuilder()
                .expiresAt(System.currentTimeMillis() - 100000)
                .build()
            cookieManager.setCookie(urlString, nc.toString())
        }
    }

    suspend fun clear() = suspendCoroutine<Boolean> { continuation ->
        cookieManager.removeAllCookies(continuation::resume)
    }
}