package com.maratangsoft.todaysperformance;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.maratangsoft.todaysperformance.databinding.ItemSearchFragmentBinding;

import java.util.ArrayList;

public class FavoriteFragmentAdapter extends RecyclerView.Adapter<FavoriteFragmentAdapter.FavoriteViewHolder> {

    Context context;
    ArrayList<RecyclerViewItem> itemList;

    public FavoriteFragmentAdapter(Context context, ArrayList<RecyclerViewItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }
    public FavoriteFragmentAdapter() {
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new FavoriteViewHolder(layoutInflater.inflate(R.layout.item_search_fragment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        RecyclerViewItem item = itemList.get(position);
        holder.binding.itemTvPrfnm.setText(item.prfnm);
        holder.binding.itemTvGenrenm.setText(item.genrenm);
        holder.binding.itemTvFcltynm.setText(item.fcltynm);
        holder.binding.itemTvPrfpdfrom.setText(item.prfpdfrom + " ~ ");
        holder.binding.itemTvPrfpdto.setText(item.prfpdto);
        Glide.with(context).load(item.poster).error(R.drawable.error).into(holder.binding.itemIvPoster);
        holder.binding.itemBtnFavorite.setVisibility(ImageView.GONE);

        holder.binding.itemLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("mt20id", item.mt20id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ItemSearchFragmentBinding binding;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSearchFragmentBinding.bind(itemView);
        }
    }
}
