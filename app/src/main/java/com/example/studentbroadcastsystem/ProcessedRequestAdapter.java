package com.example.studentbroadcastsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProcessedRequestAdapter extends RecyclerView.Adapter<ProcessedRequestAdapter.ViewHolder> {

    private Context context;
    private List<MessageModel> messageList;

    public ProcessedRequestAdapter(Context context, List<MessageModel> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    public void updateList(List<MessageModel> newList) {
        this.messageList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_processed_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel msg = messageList.get(position);

        holder.tvFaculty.setText("Faculty: " + msg.getSenderEmail());

        if (msg.isIndividual()) {
            holder.tvTarget.setText("Individual: " + msg.getIndividualEmail());
        } else {
            holder.tvTarget.setText("Branch: " + msg.getBranch() + " | Sem: " + msg.getSemester());
        }

        holder.tvContent.setText(msg.getContent());

        if (msg.getTimestamp() > 0) {
            String dateString = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(msg.getTimestamp()));
            holder.tvDate.setText("Date: " + dateString);
            holder.tvDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvDate.setVisibility(View.GONE);
        }

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
        TextView tvStatusIcon, tvFaculty, tvTarget, tvDate, tvContent, tvRejectionReason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatusIcon = itemView.findViewById(R.id.tvStatusIcon);
            tvFaculty = itemView.findViewById(R.id.tvFaculty);
            tvTarget = itemView.findViewById(R.id.tvTarget);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvRejectionReason = itemView.findViewById(R.id.tvRejectionReason);
        }
    }
}
