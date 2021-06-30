package model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.ChatActivity;
import com.example.letschat.MainActivity;
import com.example.letschat.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context mainActivity;
    ArrayList<Users> userArrayList;
    public UserAdapter(MainActivity mainActivity, ArrayList<Users> userArrayList) {
        this.mainActivity = mainActivity;
        this.userArrayList = userArrayList;

    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_users_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = userArrayList.get(position);
            holder.userName.setText(users.fname + " " + users.lname);
            holder.userStatus.setText(users.status);


            // onclick on the user chat start\
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chatIntent = new Intent(mainActivity, ChatActivity.class);
                    chatIntent.putExtra("name", users.getFname());
                    chatIntent.putExtra("uid", users.getUid());
                    mainActivity.startActivity(chatIntent);
                }
            });
            //  onclick on the user chat end
        }




    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder {
        CircleImageView userProfile;
        TextView userName, userStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.user_name);
            userStatus = itemView.findViewById(R.id.user_status);
        }
    }
}
