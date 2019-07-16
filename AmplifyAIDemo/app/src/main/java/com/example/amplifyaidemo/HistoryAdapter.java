package com.example.amplifyaidemo;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.amazonaws.amplify.generated.graphql.ListTranslatedHistorysQuery;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {

    private static String TAG ="Demo";
    private LayoutInflater mInflater;
    private List<ListTranslatedHistorysQuery.Item> mData = new ArrayList<>();


    HistoryAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public HistoryAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.history_item, viewGroup, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.Holder holder, int i) {
        holder.bindData(mData.get(i));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setItems(List<ListTranslatedHistorysQuery.Item> items) {
        mData = items;
    }

    public class Holder extends RecyclerView.ViewHolder{
        private TextView srcTxt, tgtTxt;

        private ImageView iv;

        public Holder(View view){
            super(view);
            iv = view.findViewById(R.id.img);
            srcTxt =view.findViewById(R.id.src_text);
            tgtTxt =view.findViewById(R.id.tgt_text);
        }
        void bindData(final ListTranslatedHistorysQuery.Item item) {

            srcTxt.setText(item.src());
            tgtTxt.setText(item.target());
            final String url = item.link();

            iv.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    TTSPlayer tts = new TTSPlayer();
                    try {
                        tts.doPlay(new URL(url ));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}





