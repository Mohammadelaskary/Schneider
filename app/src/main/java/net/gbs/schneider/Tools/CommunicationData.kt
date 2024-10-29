package net.gbs.schneider.Tools

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class CommunicationData {
    companion object {
        private val PROTOCOL_TYPE_NAME = "protocol"
        private val IP_ADDRESS_NAME = "Ip_address"
        private val PORT_NUM_NAME = "port_num"
        fun getProtocol(application: Application): String {
            val sharedPreferences = application.applicationContext.getSharedPreferences(
                application.packageName, Context.MODE_PRIVATE
            )
            return if (sharedPreferences.getString(
                    PROTOCOL_TYPE_NAME,
                    null
                ) != null
            ) sharedPreferences.getString(
                PROTOCOL_TYPE_NAME, null
            )!! else "http"
        }

        fun getIpAddress(application: Application): String {
            val sharedPreferences = application.applicationContext.getSharedPreferences(
                application.packageName, Context.MODE_PRIVATE
            )
            return if (sharedPreferences.getString(
                    IP_ADDRESS_NAME,
                    null
                ) != null
            ) sharedPreferences.getString(
                IP_ADDRESS_NAME, null
            )!! else "41.196.137.3"
        }

        fun getPortNumber(application: Application): String {
            val sharedPreferences = application.applicationContext.getSharedPreferences(
                application.packageName, Context.MODE_PRIVATE
            )
            return if (sharedPreferences.getString(
                    PORT_NUM_NAME,
                    null
                ) != null
            ) sharedPreferences.getString(
                PORT_NUM_NAME, null
            )!! else "2100"
        }

        fun saveProtocol(application: Application, protocol: String?) {
            val sharedPreferences = application.applicationContext.getSharedPreferences(
                application.packageName, Context.MODE_PRIVATE
            )
            sharedPreferences.edit().putString(PROTOCOL_TYPE_NAME, protocol).apply()
        }

        fun saveIPAddress(application: Application, ipAddress: String?) {
            val sharedPreferences = application.applicationContext.getSharedPreferences(
                application.packageName, Context.MODE_PRIVATE
            )
            sharedPreferences.edit().putString(IP_ADDRESS_NAME, ipAddress).apply()
        }

        fun savePortNum(application: Application, portNum: String?) {
            val sharedPreferences = application.applicationContext.getSharedPreferences(
                application.packageName, Context.MODE_PRIVATE
            )
            sharedPreferences.edit().putString(PORT_NUM_NAME, portNum).apply()
        }
    }

}