package com.royalit.garghi

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.text.Html
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.royalit.garghi.Activitys.EnquiryPostActivity
import com.royalit.garghi.Activitys.EnquiryProductActivity
import com.royalit.garghi.Activitys.NotificationActivity
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.lang.System.currentTimeMillis
import java.util.Random


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val channelId = "Disability_channel" // Define a notification channel ID

    companion object {
        lateinit var sharedPreferences: SharedPreferences
        private val TAG = "MyFirebaseToken"
        private lateinit var notificationManager: NotificationManager
        private  var title: String=""
        private  var notification_body: String=""
        private  var userid: String=""
        var token: String? = ""
        var body: String? = ""
        var id: String? = ""
        //var context: Context
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.e("onMessageReceived: ", p0.data.toString())

        Log.e("onMessageReceived: ", p0.notification.toString())

        sendNotification(p0);
        if(true)
        {
            return
        }
        if(p0.notification!=null)
        {
            val title=  p0.notification!!.title
            val body=  p0.notification!!.body
            sendNotification(p0)
        }else
            if (p0.data.isNotEmpty()) {
                try {
                    val params: Map<String?, String?> = p0.data
                    val json = JSONObject(params)

                    val message = json.getString("message")
                    var obj = JSONObject(message)
                    val jsonString = message.substring(1, message.length - 1).replace("\\", "")
                    val jsonObject = JSONTokener(message).nextValue() as JSONObject

                    id = jsonObject.getString("id")
                    title = jsonObject.getString("title")
                    body = jsonObject.getString("body")
//
//                if ((id.equals("1"))){
//                    startActivity(Intent(context,Home_Screen::class.java))
//                }

                    Log.e(TAG, "onMessageReceived: $json")
                    Log.e("jsonString", "" + jsonString)
                    Log.e("id", "" + id)


                    val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

//        Log.e("tilte", "" + title)
                    val intent = Intent(this, NotificationActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                    val pendingIntent: PendingIntent
                    pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    } else {
                        PendingIntent.getActivity(
                            this,
                            0, intent, PendingIntent.FLAG_IMMUTABLE
                        )
                    }
                    Log.e("id", " 13 " + id)

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        checkNotificationChannel("Disability_Properties")
                    }

                    Log.e("id", " 14 " + id)

                    val notification = NotificationCompat.Builder(applicationContext, "Disability_channel")
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val color = ContextCompat.getColor(this, R.color.colorPrimary)
                        notification.setColor(
                            Color.RED

                        )
//            notification.setColor(ContextCompat.getColor(this, R.color.red))


                    } else {
                        val color = ContextCompat.getColor(this, R.color.colorPrimary)
                        notification.setColor(
                            Color.RED


                        )
//            notification.setColor(ContextCompat.getColor(this, R.color.red))
                    }
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Disability")
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(
                            NotificationCompat.MessagingStyle(title!!)
                                .setGroupConversation(false)
                                .addMessage(body, currentTimeMillis(), title)
                        )
                        //.setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSound(defaultSound)
                    Log.e("id", " 15 " + id)

                    val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(1, notification.build())
                    Log.e("id", " 16 " + id)

                } catch (e: JSONException) {
                    Log.e(TAG, "Exception: " + e.message)
                }
            }



    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "FalconNotification"
        val channelName = "New Deals"
        Log.e("remoteMessage", "remoteMessage " + remoteMessage.notification)
        Log.e("remoteMessage", "remoteMessage " + remoteMessage.data.toString())

        try {

            Log.e("INstance","Instnace "+remoteMessage.data)

            val  obj= remoteMessage.data
            Log.e("INstance","Instnace "+remoteMessage.data.get("title")+" "+remoteMessage.data.get("body"))


            //val obj = JSONObject(remoteMessage.data.toString())
            val title = obj.get("title")
            val body = obj.get("body")
            var product_name=""
            var product_id=""
            var type=""
            if(obj.containsKey("product_name")&&!obj.get("product_name").equals("0"))
            {
                product_name= obj.get("product_name").toString();
                product_id=obj.get("product_id").toString();
                type=obj.get("type").toString();
            }
            //Log.e("remoteMessage","remoteMessage "+remoteMessage.getNotification().getTitle());
            //Log.e("remoteMessage","remoteMessage "+remoteMessage.getNotification().getBody());
            val notificationId = Random().nextInt(1000)

            // Log.d("EDITABLE",remoteMessage.);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel(
                    channelId, channelName, importance
                )
                notificationManager.createNotificationChannel(mChannel)
            }

            val inboxStyle = NotificationCompat.BigTextStyle()
            val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setStyle(inboxStyle)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentText(
                    Html
                        .fromHtml("<i>$body</i>")
                )
                .setContentTitle(title)

            val stackBuilder = TaskStackBuilder.create(this)
            var intent = Intent(
                this,
                NotificationActivity::class.java
            )

            if(type!=null&&type.isNotEmpty())
            {
                if(type.equals("ServiceEnquiry"))
                {
                    intent = Intent(
                        this,
                        EnquiryPostActivity::class.java
                    )
                    intent. putExtra("post_id",product_id)
                    intent.  putExtra("post_Name",product_name)
                }else  if(type.equals("ProductEnquiry"))
                {
                    intent = Intent(
                        this,
                        EnquiryProductActivity::class.java
                    )
                    intent. putExtra("post_id",product_id)
                    intent.  putExtra("post_Name",product_name)
                }
            }
            intent.putExtra("isNotification", "1")
            stackBuilder.addNextIntent(intent)
            val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_MUTABLE
            )
            mBuilder.setContentIntent(resultPendingIntent)

            notificationManager.notify(1, mBuilder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /*  private fun sendNotification(remoteMessage: RemoteMessage) {
          val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
          val channelId = "def"
          val channelName = "New Deals"
          val notificationId = Random().nextInt(1000)

          // Log.d("EDITABLE",remoteMessage.);
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              val importance = NotificationManager.IMPORTANCE_HIGH
              val mChannel = NotificationChannel(
                  channelId, channelName, importance
              )
              notificationManager.createNotificationChannel(mChannel)
          }

          val inboxStyle = NotificationCompat.BigTextStyle()
          val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
              .setSmallIcon(R.drawable.ic_notification)
              .setStyle(inboxStyle)
              .setPriority(Notification.PRIORITY_MAX)
              .setAutoCancel(true)
              .setContentText(
                  Html
                      .fromHtml("<i>" + remoteMessage.notification!!.body + "</i>")
              )
              .setContentTitle(remoteMessage.notification!!.title)

          val stackBuilder = TaskStackBuilder.create(this)
          val intent=Intent(this, NotificationActivity::class.java);
          intent.putExtra("isNotification","1");
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


          stackBuilder.addNextIntentWithParentStack(intent);
          val resultPendingIntent = stackBuilder.getPendingIntent(
              0,
              PendingIntent.FLAG_IMMUTABLE
          )
          val pendingIntent = PendingIntent.getActivity(
              this, 0,  *//* Request code *//*intent,
            PendingIntent.FLAG_MUTABLE
        )
        mBuilder.setContentIntent(pendingIntent)

        notificationManager.notify(1, mBuilder.build())
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkNotificationChannel(CHANNEL_ID: String) {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "Disability_channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.description = body
        notificationChannel.enableLights(true)

        notificationChannel.enableVibration(true)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    override fun onNewToken(p0: String) {
        token = p0
        super.onNewToken(p0)
    }

    private fun getProfile() {

    }
}