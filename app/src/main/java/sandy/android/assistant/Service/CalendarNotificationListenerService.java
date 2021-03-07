//this notification service is worked after user gives permission to SANDY Personal Assistant to inspect other notifications.
// If requested permissions are given, this service catches calendar notifications in order to send push notifications.
//This is not the best solution to implement push notifications, but it's the only effective way because of
//new Android restrictions in SDK's after 24. For now, background tasks are not provided by Android.
//So we came up with this solution in order to push notifications. This service is connected to calendar application
//in the phone. Because of this, this service will only work if calendar notification is triggered.
//But there is always a possibility that service might not work, because Android background services
//policy is strict in new SDK versions.

package sandy.android.assistant.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.Html;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

import sandy.android.assistant.Model.DatabaseManagement;
import sandy.android.assistant.Model.Note;
import sandy.android.assistant.Controller.NotePreviewActivity;
import sandy.android.assistant.R;


public class CalendarNotificationListenerService extends NotificationListenerService {
    NotificationChannel channel;
    NotificationManager notificationManager;

    @Override
    public void onCreate() {
        System.out.println("notification listener service created.");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {       //when a notification is posted, service detects it.
        final String packageName = sbn.getPackageName();
        if (!TextUtils.isEmpty(packageName) && packageName.equals("com.android.calendar")) {        //if a push notification came from calendar app,
            createNotificationChannel();        //create notification channel to trigger push notifications

            Bundle extras = sbn.getNotification().extras;       //get extras from calendar notification
            String noteTitle = extras.get(Notification.EXTRA_TITLE).toString();
            System.out.println("note title:" + noteTitle);
            DatabaseManagement db;
            db = new DatabaseManagement(this);      //initialize database access object
            ArrayList<Note> dbNotes = db.getAllNotes();     //get all notes from database.
            if (dbNotes.size() > 0) {
                for (int i = 0; i < dbNotes.size(); i++) {
                    if (dbNotes.get(i).getTitle().equals(noteTitle)) {      //if notification title is same as note title at database,
                        //create a new note preview activity. this is a pending intent and will open up NotePreviewActivity if user clicks on notification.
                        Intent intent = new Intent(getApplicationContext(), NotePreviewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("calendar_notification_note_title", dbNotes.get(i).getTitle());     //put note title extra
                        intent.putExtra("calendar_notification_note_content", dbNotes.get(i).getContent());     //put note content extra
                        intent.setData((Uri.parse("custom://" + System.currentTimeMillis())));        //create a unique intent by parsing system millis as intent data
                        //prepare pending intent that will be triggered when user clicks on notification.
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), dbNotes.get(i).getNotification().getId(), intent, 0);

                        android.app.Notification.Builder builder = null;

                        //notification builder will be handled in this way because
                        //notification builder is instantiated with different parameters in versions after SDK 26

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            builder = new android.app.Notification.Builder(this, channel.getId());
                        } else {
                            builder = new android.app.Notification.Builder(this);
                        }
                        builder.setContentTitle(dbNotes.get(i).getTitle());     //fills notification builder options
                        builder.setContentText(dbNotes.get(i).getTitle());
                        builder.setSmallIcon(R.drawable.circle_clock);
                        builder.setStyle(new android.app.Notification.BigTextStyle().bigText(Html.fromHtml(dbNotes.get(i).getContent())));
                        builder.setContentIntent(pendingIntent);
                        builder.setAutoCancel(true);

                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(dbNotes.get(i).getNotification().getId(), builder.build());      //push created notification to screen
                    }
                }
            }
            System.out.println("calendar event.");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //simply do nothing
    }

    // this method creates a new NotificationChannel, but only works on API 26 and higher because
    // the NotificationChannel class is new and not in the support library

    public void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {       //if SDK version is 26 and higher
            CharSequence name = getString(R.string.channel_name);       //set channel name and description
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;        //set channel importance
            channel = new NotificationChannel(getString(R.string.channel_name), name, importance);      //initialize notificationChannel
            channel.setDescription(description);    //set other options of notification
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);        //set lock screen visibility to public

            //registers channel with notificationManager and creates it.
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            System.out.println("notification channel name: " + notificationManager.getNotificationChannel(channel.getId()).getId());
        }
    }

}
