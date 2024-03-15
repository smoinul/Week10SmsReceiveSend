package fanshawe.example.week10

import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.widget.Toast

class SmsSentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (resultCode) {
            RESULT_OK -> Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show()
            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(context, "Generic failure error", Toast.LENGTH_SHORT).show()
            SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(context, "No service available", Toast.LENGTH_SHORT).show()
            SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT).show()
            SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(context, "Radio is off", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val ACTION_SMS_SENT = "SMS_SENT"
    }
}
