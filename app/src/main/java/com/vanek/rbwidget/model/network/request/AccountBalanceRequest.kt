package com.vanek.rbwidget.model.network.request

data class AccountBalanceRequest (
    val accountId: String,
    val balance: Double,
    val currency: String,
    val availableBalance: Double? = null,
    val lastUpdated: Long? = null
)