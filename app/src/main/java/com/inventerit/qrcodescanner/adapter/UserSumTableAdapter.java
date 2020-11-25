package com.inventerit.qrcodescanner.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.internal.LockOnGetVariable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inventerit.qrcodescanner.Model.Codes;
import com.inventerit.qrcodescanner.Model.HistoryModel;
import com.inventerit.qrcodescanner.Model.UserSumTableModel;
import com.inventerit.qrcodescanner.QRUtils;
import com.inventerit.qrcodescanner.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class UserSumTableAdapter  extends RecyclerView.Adapter<UserSumTableAdapter.MyViewHolder> {

    private Context context;
    private List<HistoryModel> modelList;
    public static boolean isAdded = true;

    public UserSumTableAdapter(Context context, List<HistoryModel> modelList){
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=null;
        if(viewType == 0){
            v = LayoutInflater.from(context)
                    .inflate(R.layout.head_plan_table, parent, false);
            return new MyViewHolder(v);
        }else{
            v = LayoutInflater.from(context)
                    .inflate(R.layout.item_plan_table, parent, false);
            return new MyViewHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {

        if(i==0){
            viewHolder.secSumTitle.setText(context.getString(R.string.secsum));
            viewHolder.nrTitle.setText(context.getString(R.string.nr));
            viewHolder.valTitle.setText(context.getString(R.string.val));
            viewHolder.typeTitle.setText(context.getString(R.string.type));
        }

        if(i != 0){
            HistoryModel model = modelList.get(i-1);
            viewHolder.mTypeView.setText(model.getType());
            viewHolder.mNRView.setText(model.getNr());
            viewHolder.mVALView.setText(model.getVal());
            viewHolder.msecsum.setText(calculateSecSum(i-1)+"");
            viewHolder.closeLine.setVisibility(View.VISIBLE);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("scanneddoc").child(model.getId());
            ref.child("val").setValue(model.getVal());
            ref.child("type").setValue(model.getType());

        }

    }

    @Override
    public int getItemCount() {
        return modelList.size()+1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTypeView;
        private TextView mNRView;
        private TextView mVALView;
        private TextView msecsum;
        private View closeLine;
        private TextView nrTitle, typeTitle, valTitle, secSumTitle;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTypeView = itemView.findViewById(R.id.typetv);
            mNRView = itemView.findViewById(R.id.nrtv);
            mVALView = itemView.findViewById(R.id.valtv);
            msecsum = itemView.findViewById(R.id.secsumtv);
            closeLine = itemView.findViewById(R.id.closeLine);

            nrTitle = itemView.findViewById(R.id.nr_textView);
            typeTitle = itemView.findViewById(R.id.type_textView);
            valTitle = itemView.findViewById(R.id.val_textView);
            secSumTitle = itemView.findViewById(R.id.secsum_textView);

        }
    }

    /**
     * Get a diff between two dates
     *
     * @param oldDate the old date
     * @param newDate the new date
     * @return the diff value, in the days
     */
    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int calculateSecSum(int i){
        Date date = new Date(System.currentTimeMillis());

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String[] today_date = dateFormat1.format(date).split("/");

        int secsum = 0;


        int dateDifference = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), modelList.get(i).getDate(), dateFormat1.format(date));
        System.out.println("dateDifference: " + dateDifference);

            String[] history_date = modelList.get(i).getDate().split("/");
            int diff = Integer.parseInt(String.valueOf(today_date[0])) - Integer.parseInt(String.valueOf(history_date[0]));
            Log.i("diff",String.valueOf(diff));

            if(dateDifference==1){
                secsum = Integer.parseInt(String.valueOf(modelList.get(i).getVal()));
            }
            else if(dateDifference==2){
                secsum = Integer.parseInt(String.valueOf(modelList.get(i).getVal()))*dateDifference;
            }
            else if(dateDifference==3){
                secsum = Integer.parseInt(String.valueOf(modelList.get(i).getVal()))*dateDifference;
            }
            else if(dateDifference==4){
                secsum = Integer.parseInt(String.valueOf(modelList.get(i).getVal()))*dateDifference;
            }
            else if(dateDifference==5){
                secsum = Integer.parseInt(String.valueOf(modelList.get(i).getVal()))*dateDifference;
            }
            else if(dateDifference==6){
                secsum = Integer.parseInt(String.valueOf(modelList.get(i).getVal()))*dateDifference;
            }
            else if(dateDifference==7){
                secsum = Integer.parseInt(String.valueOf(modelList.get(i).getVal()))*dateDifference;
            }
            else if(dateDifference==8){
                secsum = Integer.parseInt(String.valueOf(modelList.get(i).getVal()))*dateDifference;
            }
            else if(dateDifference==9){
                secsum = Integer.parseInt(String.valueOf(modelList.get(i).getVal()))*dateDifference;
            }
            else if(dateDifference==10){
                secsum = Integer.parseInt(String.valueOf(modelList.get(i).getVal()))*dateDifference;
            }
            else if(dateDifference>10){
                secsum = Integer.parseInt(String.valueOf(modelList.get(i).getVal()))*10;
            }
            if(secsum!=0){
//                if(isAdded){
//                    isAdded = false;
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("scanneddoc")
                            .child(modelList.get(i).getId()).child("secsum");

                    reference.setValue(secsum);
//                }
            }

        return secsum;
    }
}
