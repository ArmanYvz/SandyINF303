//Adapter Class that controls and operates elements inside created recycleView component in activity_main.xml

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

import java.util.ArrayList;

import sandy.android.assistant.Controller.MainActivity;
import sandy.android.assistant.Model.CalendarSync;
import sandy.android.assistant.Model.DatabaseManagement;
import sandy.android.assistant.Model.Note;
import sandy.android.assistant.Controller.NoteEditorActivity;
import sandy.android.assistant.R;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    DatabaseManagement db;
    ArrayList<Note> mNoteList;
    LayoutInflater inflater;
    Activity activity;
    CalendarSync calendarSync = new CalendarSync();
    MainActivity mainActivity = new MainActivity();

    public NoteAdapter(Context context, ArrayList<Note> notes, DatabaseManagement db, MainActivity ma) {
        inflater = LayoutInflater.from(context);
        this.mNoteList = notes;
        this.db = db;
        this.activity = (Activity) context;
        this.mainActivity = ma;
    }

    public NoteAdapter(Context context, ArrayList<Note> notes, DatabaseManagement db) {
        inflater = LayoutInflater.from(context);
        this.mNoteList = notes;
        this.db = db;
        this.activity = (Activity) context;
    }

    //action method that handles adapter logic when viewholder is created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_note_cardview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    //action method that handles adapter logic when viewholder is bound
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note selectedNote = mNoteList.get(position);
        holder.setData(selectedNote, position);

    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }


    //viewholder class that is handled by adapter
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView noteTitle, noteDescription;
        ImageView deleteNote;
        LinearLayout noteSelectionLinearLayout;

        public MyViewHolder(View itemView) {        //viewholder constructor method
            super(itemView);

            noteTitle = (TextView) itemView.findViewById(R.id.noteTitle);
            noteDescription = (TextView) itemView.findViewById(R.id.noteDescription);

            noteSelectionLinearLayout = (LinearLayout) itemView.findViewById(R.id.noteSelectionLinearLayout);
            noteSelectionLinearLayout.setOnClickListener(this);

            deleteNote = (ImageView) itemView.findViewById(R.id.deleteNote);
            deleteNote.setOnClickListener(this);

        }

        //method that sets viewholder text information
        public void setData(Note selectedNote, int position) {

            this.noteTitle.setText(selectedNote.getTitle());
            this.noteDescription.setText(selectedNote.getSaveDate());

        }


        //method that handles click actions on viewholder
        @Override
        public void onClick(View v) {
            if (v == deleteNote) {
                deleteNote(getLayoutPosition());
            } else if (v == noteSelectionLinearLayout) {
                openNote(getLayoutPosition());
            }

        }

        //method that applies note delete operation
        private void deleteNote(int position) {
            Boolean returnVal = false;
            Note noteToDelete = mNoteList.get(position);
            returnVal = db.deleteNote(noteToDelete);
            if (returnVal) {
                mNoteList.remove(position);
                if (noteToDelete.getNotification() != null) {
                    int calendarReadPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR);
                    int calendarWritePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR);

                    if (calendarReadPermission == PermissionChecker.PERMISSION_GRANTED && calendarWritePermission == PermissionChecker.PERMISSION_GRANTED) {
                        int deletedRows = 0;
                        deletedRows = calendarSync.deleteCalendarEntry(activity.getApplicationContext(), noteToDelete.getNotification().getId());
                        if (deletedRows > 0) {
                            Toast.makeText(activity, activity.getResources().getString(R.string.calendar_event_deleted), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                mNoteList = db.getAllNotes();
                notifyRemoved(position);
            } else {
                Toast.makeText(activity, activity.getResources().getString(R.string.note_delete_error), Toast.LENGTH_LONG);
            }
        }

        //method that applies note opening operations
        private void openNote(int position) {
            Note noteToOpen = mNoteList.get(position);

            try {
                db.getNoteFromNoteId(noteToOpen.getId());
                Intent intent = new Intent(mainActivity, NoteEditorActivity.class);
                intent.putExtra("NOTE_ID", noteToOpen.getId());
                mainActivity.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(activity, activity.getResources().getString(R.string.note_not_found_error), Toast.LENGTH_LONG);
            }
        }

        //notifies when an item is removed from viewholder
        public void notifyRemoved(int position) {
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mNoteList.size());
        }

        //notifies when an item is inserted into viewholder
        public void notifyInserted(int position) {
            notifyItemInserted(position);
            notifyItemRangeChanged(position, mNoteList.size());
        }

        //notifies when dataset is changed
        public void notifyChanged() {
            notifyDataSetChanged();
        }

    }
}
