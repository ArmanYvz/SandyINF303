//Adapter Class that controls and operates elements inside created recycleView component in notification_recycler_view.xml

package sandy.android.assistant.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;

import com.github.irshulx.Editor;

import java.util.ArrayList;

import sandy.android.assistant.Controller.NotificationEditorActivity;
import sandy.android.assistant.Controller.MainActivity;
import sandy.android.assistant.Model.CalendarSync;
import sandy.android.assistant.Model.DatabaseManagement;
import sandy.android.assistant.Model.Note;
import sandy.android.assistant.Model.Notification;
import sandy.android.assistant.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    DatabaseManagement db;
    ArrayList<Note> mNoteWithNotificationList;
    LayoutInflater inflater;
    Activity activity;
    CalendarSync calendarSync = new CalendarSync();
    MainActivity mainActivity = new MainActivity();

    public NotificationAdapter(Context context, ArrayList<Note> notesWithNotification, DatabaseManagement db, MainActivity ma) {
        inflater = LayoutInflater.from(context);
        this.mNoteWithNotificationList = notesWithNotification;
        this.db = db;
        this.activity = (Activity) context;
        this.mainActivity = ma;
    }

    public NotificationAdapter(Context context, ArrayList<Note> notesWithNotification, DatabaseManagement db) {
        inflater = LayoutInflater.from(context);
        this.mNoteWithNotificationList = notesWithNotification;
        this.db = db;
        this.activity = (Activity) context;
    }

    //action method that handles adapter logic when viewholder is created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_notification_cardview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    //action method that handles adapter logic when viewholder is bound
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note selectedNote = mNoteWithNotificationList.get(position);

        if (selectedNote != null)
            holder.setData(selectedNote, position);
    }

    @Override
    public int getItemCount() {
        return mNoteWithNotificationList.size();
    }

    //viewholder class that is handled by adapter
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView notificationTitle, notificationDescription;
        ImageView deleteNotification;
        LinearLayout notificationSelectionLinearLayout;

        public MyViewHolder(View itemView) {        //viewholder constructor method
            super(itemView);

            notificationTitle = (TextView) itemView.findViewById(R.id.notificationTitle);
            notificationDescription = (TextView) itemView.findViewById(R.id.notificationDescription);

            notificationSelectionLinearLayout = (LinearLayout) itemView.findViewById(R.id.notificationSelectionLinearLayout);
            notificationSelectionLinearLayout.setOnClickListener(this);

            deleteNotification = (ImageView) itemView.findViewById(R.id.deleteNotification);
            deleteNotification.setOnClickListener(this);

        }

        //method that sets viewholder text information
        public void setData(Note selectedNote, int position) {


            this.notificationTitle.setText(selectedNote.getTitle());
            this.notificationDescription.setText(selectedNote.getNotification().getDate());

        }


        //method that handles click actions on viewholder
        @Override
        public void onClick(View v) {
            if (v == deleteNotification) {
                deleteNotification(getLayoutPosition());
            } else if (v == notificationSelectionLinearLayout) {
                openNotification(getLayoutPosition());
            }

        }

        //method that handles delete notification operations
        private void deleteNotification(int position) {
            Notification notificationToDelete = mNoteWithNotificationList.get(position).getNotification();

            Boolean returnVal = db.deleteNotification(notificationToDelete);

            ArrayList<Note> notes = db.getAllNotes();

            if (returnVal) {
                notifyRemoved(position);
                int calendarReadPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR);
                int calendarWritePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR);

                if (calendarReadPermission == PermissionChecker.PERMISSION_GRANTED && calendarWritePermission == PermissionChecker.PERMISSION_GRANTED) {
                    int deletedRows = 0;
                    deletedRows = calendarSync.deleteCalendarEntry(activity.getApplicationContext(), notificationToDelete.getId());
                    if (deletedRows > 0) {
                        Toast.makeText(activity, activity.getResources().getString(R.string.calendar_event_deleted), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(activity, activity.getResources().getString(R.string.notification_delete_error), Toast.LENGTH_LONG);
            }

            refresh();
        }

        //method that applies open notification operations
        private void openNotification(int position) {
            Notification notificationToOpen = mNoteWithNotificationList.get(position).getNotification();

            try {
                db.getNotificationFromNotificationID(notificationToOpen.getId());
                Intent intent = new Intent(activity.getApplicationContext(), NotificationEditorActivity.class);
                intent.putExtra("NOTIFICATION_ID", notificationToOpen.getId());
                activity.startActivityForResult(intent, 0);
            } catch (Exception e) {
                Toast.makeText(activity, activity.getResources().getString(R.string.notification_open_error), Toast.LENGTH_LONG);
            }

            refresh();
        }

        //method that refreshes viewholder components
        private void refresh() {
            ArrayList<Note> notes = db.getAllNotes();
            ArrayList<Note> notesWithNotification = new ArrayList<Note>();

            for (int index = 0; index < notes.size(); index++) {
                if (notes.get(index).getNotification() != null) {
                    notesWithNotification.add(notes.get(index));
                }
            }

            mNoteWithNotificationList = notesWithNotification;
        }

        //notifies when an item is removed from viewholder
        public void notifyRemoved(int position) {
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mNoteWithNotificationList.size());
        }

        //notifies when an item is inserted into viewholder
        public void notifyInserted(int position) {
            notifyItemInserted(position);
            notifyItemRangeChanged(position, mNoteWithNotificationList.size());
        }

        //notifies when dataset is changed
        public void notifyChanged() {
            notifyDataSetChanged();
        }


    }


}
