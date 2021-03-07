//Adapter Class that controls and operates elements inside created recycleView component in activity_main.xml

package sandy.android.assistant.Adapter;

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

import androidx.recyclerview.widget.RecyclerView;

import com.github.irshulx.Editor;

import java.util.ArrayList;

import sandy.android.assistant.Model.DatabaseManagement;
import sandy.android.assistant.Model.Notebook;
import sandy.android.assistant.Controller.NotebookActivity;
import sandy.android.assistant.R;

public class MainActivityNavigationDrawerAdapter extends RecyclerView.Adapter<MainActivityNavigationDrawerAdapter.MyViewHolder> {

    DatabaseManagement db;
    ArrayList<Notebook> notebookArrayList;
    LayoutInflater inflater;
    Activity activity;

    //adapter constructor method
    public MainActivityNavigationDrawerAdapter(Context context, ArrayList<Notebook> notebookArrayList, DatabaseManagement db) {
        inflater = LayoutInflater.from(context);
        this.notebookArrayList = notebookArrayList;
        this.db = db;
        this.activity = (Activity) context;
    }

    //action method that handles adapter logic when viewholder is created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_notebook_cardview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    //action method that handles adapter logic when viewholder is bound
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Notebook selectedNotebook = notebookArrayList.get(position);

        holder.setData(selectedNotebook, position);
    }

    @Override
    public int getItemCount() {
        return notebookArrayList.size();
    }

    //viewholder class that is handled by adapter
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView notebookTitle;
        ImageView deleteNotebook;
        LinearLayout notebookSelectionLinearLayout;

        public MyViewHolder(View itemView) {        //viewholder constructor method
            super(itemView);

            notebookTitle = (TextView) itemView.findViewById(R.id.notebookTitle);

            notebookSelectionLinearLayout = (LinearLayout) itemView.findViewById(R.id.notebookSelectionLinearLayout);
            notebookSelectionLinearLayout.setOnClickListener(this);

            deleteNotebook = (ImageView) itemView.findViewById(R.id.deleteNotebook);
            deleteNotebook.setOnClickListener(this);

        }

        public void setData(Notebook selectedNotebook, int position) {      //method that sets viewholder text information


            this.notebookTitle.setText(selectedNotebook.getTitle());

        }


        @Override
        public void onClick(View v) {       //method that handles click actions on viewholder
            if (v == deleteNotebook) {
                deleteNotebook(getLayoutPosition());
            } else if (v == notebookSelectionLinearLayout) {
                openNotebook(getLayoutPosition());
            }

        }

        private void deleteNotebook(int position) {         //method that appies notebook delete operation
            Notebook notebookToDelete = notebookArrayList.get(position);

            Boolean returnVal = db.deleteNotebook(notebookToDelete);

            if (returnVal) {
                notifyRemoved(position);
            } else {
                Toast.makeText(activity, activity.getResources().getString(R.string.notebook_delete_error), Toast.LENGTH_LONG);
            }

            refresh();
        }

        private void openNotebook(int position) {       //method that opens up selected notebook
            Notebook notebookToOpen = notebookArrayList.get(position);

            Intent intent = new Intent(activity.getApplicationContext(), NotebookActivity.class);
            intent.putExtra("NOTEBOOK_ID", notebookToOpen.getId());
            activity.startActivityForResult(intent, 0);

            refresh();
        }

        private void refresh() {         //method that refreshes viewholder components
            ArrayList<Notebook> notebooks = db.getAllNotebooks();

            notebookArrayList = notebooks;
        }

        public void notifyRemoved(int position) {       //notifies when an item is removed from viewholder
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notebookArrayList.size());
        }

        public void notifyInserted(int position) {          //notifies when an item is inserted into viewholder
            notifyItemInserted(position);
            notifyItemRangeChanged(position, notebookArrayList.size());
        }

        //notifies when dataset is changed
        public void notifyChanged() {
            notifyDataSetChanged();
        }


    }
}
