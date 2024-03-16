package fanshawe.example.week10

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SmsReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        for (sms in messages) {
            val messageBody = sms.messageBody
            // Now messageBody contains the text of the received SMS
            // You can then compare this with your EditText's content
            val sharedPref = context?.getSharedPreferences("mySettings", Context.MODE_PRIVATE)
            val textToMatch = sharedPref?.getString("textToMatch", "")
            if (sms.messageBody.contains(textToMatch.toString())) {
                // The incoming SMS contains the text we're looking for
                val builder = NotificationCompat.Builder(context!!, "MySMSReceiver")
                    .setSmallIcon(R.drawable.baseline_outgoing_mail_24)
                    .setContentTitle("Fanshawe Alarm System")
                    .setContentText("You have received a SMS wth your desired text!")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.notify(123, builder.build())
            }
        }
    }
}