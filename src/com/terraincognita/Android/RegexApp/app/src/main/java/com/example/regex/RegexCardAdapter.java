package com.example.regex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.regex.db.RegexObj;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class RegexCardAdapter extends RecyclerView.Adapter<RegexCardAdapter.RegexViewHolder> {

    private final LayoutInflater mInflater;
    private List<RegexObj> mRegexObjs;

    RegexCardAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @NonNull
    @Override
    public RegexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_regex, parent, false);
        return new RegexViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RegexViewHolder holder, int position) {
        if (mRegexObjs != null) {
            RegexObj currentRegex = mRegexObjs.get(position);
            holder.RegexItemView.setText(currentRegex.getRegex());
        } else {
            holder.RegexItemView.setText("No Regex Now");
        }
    }

    void setRegexObjs(List<RegexObj> RegexObjs){
        mRegexObjs = RegexObjs;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mRegexObjs != null)
            return mRegexObjs.size();
        else return 0;
    }

    class RegexViewHolder extends RecyclerView.ViewHolder {
        private final TextView RegexItemView;

        private RegexViewHolder(View itemView) {
            super(itemView);
            RegexItemView = itemView.findViewById(R.id.RegexCard);
        }
    }
}