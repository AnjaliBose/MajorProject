package trainedge.sample_proj;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by CISE on 03/04/2017.
 */

public class TaskHolder extends RecyclerView.ViewHolder {

    TextView tvCommentTaskname;
    TextView tvCommentAddress;
    RelativeLayout rlContainer;
    ProgressBar pbStatus;

    public TaskHolder(View itemView) {
        super(itemView);
        tvCommentTaskname = (TextView) itemView.findViewById(R.id.tvCommentTaskname);
        tvCommentAddress = (TextView) itemView.findViewById(R.id.tvCommentAddress);
        rlContainer = (RelativeLayout) itemView.findViewById(R.id.rlContainer);
        pbStatus = (ProgressBar) itemView.findViewById(R.id.pbStatus);
        pbStatus.setVisibility(View.GONE);
    }
}
