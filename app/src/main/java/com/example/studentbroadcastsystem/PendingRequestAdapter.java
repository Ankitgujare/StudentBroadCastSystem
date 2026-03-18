package com.example.studentbroadcastsystem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.ViewHolder> {

    private Context context;
    private List<MessageModel> requestList;

    public PendingRequestAdapter(Context context, List<MessageModel> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel model = requestList.get(position);
        holder.tvFaculty.setText("Faculty: " + model.getSenderEmail());
        holder.tvBranchSem.setText(model.getBranch() + " - Semester " + model.getSemester());
        holder.tvMessageSnippet.setText(model.getContent());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RequestDetailActivity.class);
            intent.putExtra("MESSAGE_ID", model.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFaculty, tvBranchSem, tvMessageSnippet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFaculty = itemView.findViewById(R.id.tvFaculty);
            tvBranchSem = itemView.findViewById(R.id.tvBranchSem);
            tvMessageSnippet = itemView.findViewById(R.id.tvMessageSnippet);
        }
    }
}
