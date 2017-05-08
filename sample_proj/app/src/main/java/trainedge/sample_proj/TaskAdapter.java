package trainedge.sample_proj;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by CISE on 03/04/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {

    //we also need a constructor for this example
    List<TaskModel> commentList;
    //alt insert to add constructor


    public TaskAdapter(List<TaskModel> commentList) {
        this.commentList = commentList;
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = ((LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.simple_comment_item, parent, false);
        return new TaskHolder(row);
    }

    @Override
    public void onBindViewHolder(final TaskHolder holder, int position) {
        //databinding
        final TaskModel model = commentList.get(position);
        holder.tvCommentTaskname.setText(model.taskName);
        holder.tvCommentAddress.setText(model.address);
        holder.rlContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Context context = holder.rlContainer.getContext();
                AlertDialog dialog = new AlertDialog.Builder(context).setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new String[]{"Complete", "Delete",}), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, which + "", Toast.LENGTH_SHORT).show();
                        switch (which) {
                            case 0:
                                updateTask(model, holder);
                                break;
                            case 1:
                                removeTask(model, holder);
                                break;
                        }
                    }
                }).setTitle("options").create();
                dialog.show();
                return true;
            }
        });

    }

    private void removeTask(TaskModel model, final TaskHolder holder) {
        int position = holder.getAdapterPosition();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference georef2 = FirebaseDatabase.getInstance().getReference("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("geofire");
        holder.pbStatus.setVisibility(View.VISIBLE);

        ref.child(model.getKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(holder.rlContainer.getContext(), "Removed", Toast.LENGTH_SHORT).show();
                    holder.pbStatus.setVisibility(View.GONE);
                    removeGeofence();
                }
            }
        });
        georef2.child(model.getKey()).removeValue();
    }

    private void removeGeofence() {

    }

    private void updateTask(TaskModel model, final TaskHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tasks").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        holder.pbStatus.setVisibility(View.VISIBLE);
        ref.child(model.getKey()).child("status").setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    holder.pbStatus.setVisibility(View.GONE);
                } else {
                    Toast.makeText(holder.rlContainer.getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
