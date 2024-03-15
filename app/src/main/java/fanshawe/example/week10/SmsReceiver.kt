package fanshawe.example.week10

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
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
                Toast.makeText(context, "Match found in SMS: ${sms.messageBody}", Toast.LENGTH_LONG).show()
            }
        }
    }
}