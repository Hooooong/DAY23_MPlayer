package com.hooooong.mplayer.view.main.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hooooong.mplayer.R;
import com.hooooong.mplayer.data.model.Music;
import com.hooooong.mplayer.view.main.MusicFragment;

import java.util.List;


public class MyMusicRecyclerViewAdapter extends RecyclerView.Adapter<MyMusicRecyclerViewAdapter.ViewHolder> {

    private List<Music.Item> mValues;
    private MusicFragment.OnListFragmentInteractionListener mListener;

    public MyMusicRecyclerViewAdapter(MusicFragment.OnListFragmentInteractionListener listener) {
        mValues = listener.getList();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).title);
        holder.position = position;

        // onBindViewHolder 에서 setOnClickListener 를 설정해주면
        // List 를 Scroll 할 때마다 Listener 가 설정되므로
        // 좋지않은 코드이다.
        /*
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.getList(holder.mItem);

                    *//*Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(textId, "textId");
                    pairs[1] = new Pair<View, String>(textName, "textName");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) view.getContext(), pairs);
                    //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) view.getContext(), textName, "textName");

                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    intent.putExtra("textId", textId.getText());
                    intent.putExtra("textName", textName.getText());
                    options.toBundle();*//*

                    mListener.openPlayer(position);
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        int position;
        TextView mIdView;
        TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.content);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.openPlayer(position);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
