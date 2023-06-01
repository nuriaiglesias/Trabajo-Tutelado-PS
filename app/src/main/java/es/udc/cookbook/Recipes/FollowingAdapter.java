package es.udc.cookbook.Recipes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.udc.cookbook.R;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.MyViewHolder> {
    private final List<String> followedUsers;
    Button unfollowButton;
    private OnUnfollowClickListener unfollowClickListener;

    public FollowingAdapter(List<String> followedUsers) {
        this.followedUsers = followedUsers;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.following_user, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String user = followedUsers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return followedUsers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            unfollowButton = itemView.findViewById(R.id.unfollowButton);

            unfollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (unfollowClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            unfollowClickListener.onUnfollowClick(position);
                        }
                    }
                }
            });
        }

        public void bind(String user) {
            usernameTextView.setText(user);
        }
    }
    public interface OnUnfollowClickListener {
        void onUnfollowClick(int position);
    }
    public void setOnUnfollowClickListener(OnUnfollowClickListener listener) {
        this.unfollowClickListener = listener;
    }

}
