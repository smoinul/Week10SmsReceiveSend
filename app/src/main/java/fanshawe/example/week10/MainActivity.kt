package fanshawe.example.week10

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView
    private val SEND_SMS_PERMISSION_REQUEST_CODE = 101
    lateinit var smsSentReceiver: SmsSentReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if the SEND_SMS permission is already available.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // If not, request permission from the user.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS), SEND_SMS_PERMISSION_REQUEST_CODE)
        }

        createNotificationChannel()


        // Initialize the BroadcastReceiver inside your function or class scope
        smsSentReceiver = SmsSentReceiver()

        bottomNavigationView = findViewById(R.id.bottomNavView)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.send -> {
                    changeFragment(SendFragment())
                    true
                }
                R.id.receive -> {
                    changeFragment(ReceiveFragment())
                    true
                }
                R.id.info -> {
                    changeFragment(InfoFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    private fun changeFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()

    }

    fun onClickSend(view: View) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as SendFragment

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            fragment.view?.let { fragmentView ->
                // Define the phone number and the message
                val phoneNumberEditText =
                    fragmentView.findViewById<EditText>(R.id.editTextPhoneNumber)
                val messageEditText = fragmentView.findViewById<EditText>(R.id.editTextMessage)
                val phoneNumber = phoneNumberEditText.text.toString()
                val myMessage = messageEditText.text.toString()

                // Intent to broadcast when the SMS has been sent
                val smsSentIntent = Intent(this, smsSentReceiver::class.java)
                //pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

                val sentIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    smsSentIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                // Get the default instance of SmsManager
                val smsManager = SmsManager.getDefault()

                // Send the text message
                smsManager.sendTextMessage(phoneNumber, null, myMessage, sentIntent, null)
            }
        }
        else {
            // Request permission from the user
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_REQUEST_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        registerReceiver(smsSentReceiver, IntentFilter(SmsSentReceiver.ACTION_SMS_SENT),  RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsSentReceiver)
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name: CharSequence = "MyAlarm"
            val description = "From MyAlarm App"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("MySMSReceiver", name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SEND_SMS_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, you can send SMS
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
        } else {
            // Permission denied, show a message to the user explaining that the feature is unavailable without permission
            Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun onClickReceived(view: View) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as ReceiveFragment

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            fragment.view?.let { fragmentView ->
                val stringToMatchSMS =
                    fragmentView.findViewById<EditText>(R.id.editTextReceivedSMS)
                val prefsEditor = getSharedPreferences("mySettings", Context.MODE_PRIVATE).edit()
                prefsEditor.putString("textToMatch", stringToMatchSMS.text.toString())
                prefsEditor.apply()
                Toast.makeText(this, "Will look for \"${stringToMatchSMS.text.toString()}\" in the incoming SMS ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onClickOpenSMS(view: View) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as InfoFragment

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            fragment.view?.let { fragmentView ->
                val phoneNumber = fragmentView.findViewById<EditText>(R.id.editTextPhone)
                val myMessage = fragmentView.findViewById<EditText>(R.id.editTextMessageSMS)
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:")  // This ensures only SMS apps respond
                    putExtra("address", phoneNumber.text.toString())
                    putExtra("sms_body", myMessage.text.toString())
                }
                startActivity(Intent.createChooser(intent, "SMS"))
            }
        }
    }
}