package com.example.studentbroadcastsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FacultyRequestAdapter extends RecyclerView.Adapter<FacultyRequestAdapter.ViewHolder> {

    private Context context;
    private List<MessageModel> messageList;

    public FacultyRequestAdapter(Context context, List<MessageModel> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_faculty_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel msg = messageList.get(position);

        if (msg.isIndividual()) {
            holder.tvTarget.setText("Individual: " + msg.getIndividualEmail());
        } else {
            holder.tvTarget.setText("Branch: " + msg.getBranch() + " | Sem: " + msg.getSemester());
        }

        holder.tvContent.setText(msg.getContent());

        if ("approved".equalsIgnoreCase(msg.getStatus())) {
            holder.tvStatusIcon.setText("✅");
            holder.tvRejectionReason.setVisibility(View.GONE);
        } else if ("rejected".equalsIgnoreCase(msg.getStatus())) {
            holder.tvStatusIcon.setText("❌");
            holder.tvRejectionReason.setVisibility(View.VISIBLE);
            holder.tvRejectionReason.setText("Reason: " + msg.getRejectionReason());
        } else {
            holder.tvStatusIcon.setText("⏳");
            holder.tvRejectionReason.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatusIcon, tvTarget, tvContent, tvRejectionReason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatusIcon = itemView.findViewById(R.id.tvStatusIcon);
            tvTarget = itemView.findViewById(R.id.tvTarget);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvRejectionReason = itemView.findViewById(R.id.tvRejectionReason);
        }
    }
}
