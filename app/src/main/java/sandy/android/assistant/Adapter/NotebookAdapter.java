//Adapter Class that controls and operates elements inside created recycleView component in notebook_recycler_view.xml

package sandy.android.assistant.Adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.irshulx.Editor;

import java.util.ArrayList;

import sandy.android.assistant.Model.DatabaseManagement;
import sandy.android.assistant.Model.Note;
import sandy.android.assistant.Model.Notebook;
import sandy.android.assistant.R;

public class NotebookAdapter extends RecyclerView.Adapter<NotebookAdapter.MyViewHolder> {

    DatabaseManagement db;
    Notebook notebook;
    ArrayList<Note> notebookNotes;
    LayoutInflater inflater;
    Activity activity;
    View root;


    public NotebookAdapter(Context context, ArrayList<Note> notebookNotes, Notebook notebook, View view, DatabaseManagement db) {
        inflater = LayoutInflater.from(context);
        this.notebookNotes = notebookNotes;
        this.notebook = notebook;
        this.root = view;
        this.db = db;
        this.activity = (Activity) context;
    }

    //action method that handles adapter logic when viewholder is created
    @Override
    public NotebookAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_notes_of_notebook_cardview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    //action method that handles adapter logic when viewholder is bound
    @Override
    public void onBindViewHolder(NotebookAdapter.MyViewHolder holder, int position) {
        Note selectedNote = notebookNotes.get(position);
        holder.setData(selectedNote, position);
    }

    @Override
    public int getItemCount() {
        return notebookNotes.size();
    }

    //viewholder class that is handled by adapter
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView notebookTitle;
        TextView notebookDescription;
        LinearLayout notesOfNotebookLinearLayout;
        ImageView deleteButton;

        public MyViewHolder(View itemView) {        //viewholder constructor method
            super(itemView);
            notebookTitle = (TextView) itemView.findViewById(R.id.noteOfNotebookTitle);

            notebookDescription = (TextView) itemView.findViewById(R.id.notesOfNotebookDescription);

            notesOfNotebookLinearLayout = (LinearLayout) itemView.findViewById(R.id.notesOfNotebookLinearLayout);
            notesOfNotebookLinearLayout.setOnClickListener(this);

            deleteButton = itemView.findViewById(R.id.notesOfNotebookDelete);
            deleteButton.setOnClickListener(this);

        }

        //method that sets viewholder text information
        public void setData(Note selectedNote, int position) {

            this.notebookTitle.setText(selectedNote.getTitle());
            String noteContent = String.valueOf(Html.fromHtml(selectedNote.getContent()));
            String noteDescription = "";
            if (noteContent.length() > 0) {
                for (int i = 0; i < noteContent.length(); i++) {
                    if (i == 20) {
                        break;
                    } else {
                        noteDescription = noteDescription + noteContent.charAt(i);
                    }
                }
                this.notebookDescription.setText(noteDescription);
            }
        }


        //method that handles click actions on viewholder
        @Override
        public void onClick(View v) {
            if (v == deleteButton) {
                deleteNote(getLayoutPosition());
            }
            if (v == notesOfNotebookLinearLayout) {
                openNote(getLayoutPosition(), root);
            }

        }

        //method that applies notebook opening operations
        private void openNote(int position, View v) {
            Note note = notebookNotes.get(position);

            View popupView = inflater.inflate(R.layout.notebook_note_popup_view, null);

            Editor renderer = popupView.findViewById(R.id.notebookPopupRenderer);
            renderer.render(note.getContent());

            TextView title = popupView.findViewById(R.id.notebookPopupTitle);
            title.setText(note.getTitle());
            title.setEnabled(false);

            ImageView buttonBackToNotebookView = popupView.findViewById(R.id.buttonBackToNotebookView);

            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            buttonBackToNotebookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

        }

        //method that applies note delete operation
        private void deleteNote(int position) {
            try {
                Note note = notebookNotes.get(position);
                db.removeNoteFromNotebook(note);
                notebookNotes.remove(position);
                notifyRemoved(position);
            } catch (Exception e) {

            }
        }

        //method that refreshes viewholder components
        private void refresh() {
            notebookNotes = db.getNotesFromNotebook(notebook);
        }

        //notifies when an item is inserted into viewholder
        public void notifyInserted(int position) {
            notifyItemInserted(position);
            notifyItemRangeChanged(position, notebookNotes.size());
        }

        public void notifyChanged() {
            notifyDataSetChanged();
        }

        //notifies when an item is removed from viewholder
        public void notifyRemoved(int position) {
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notebookNotes.size());
        }


    }
}
