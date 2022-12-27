package com.maratangsoft.todaysperformance;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.maratangsoft.todaysperformance.databinding.ItemSearchFragmentBinding;

import java.util.ArrayList;

public class SearchFragmentAdapter extends RecyclerView.Adapter<SearchFragmentAdapter.ItemViewHolder> {

    Context context;
    ArrayList<RecyclerViewItem> itemList;
    FavoriteDB favoriteDB;

    public SearchFragmentAdapter(Context context, ArrayList<RecyclerViewItem> itemList) {
        this.context = context;
        this.itemList = itemList;
        favoriteDB = new FavoriteDB(context);
    }
    public SearchFragmentAdapter() {
    }

    @NonNull
    @Override
    public SearchFragmentAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        return new ItemViewHolder(inflater.inflate(R.layout.item_search_fragment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFragmentAdapter.ItemViewHolder holder, int position) {

        RecyclerViewItem item = itemList.get(position);
        holder.binding.itemTvPrfnm.setText(item.prfnm);
        holder.binding.itemTvGenrenm.setText(item.genrenm);
        holder.binding.itemTvFcltynm.setText(item.fcltynm);
        holder.binding.itemTvPrfpdfrom.setText(item.prfpdfrom + " ~ ");
        holder.binding.itemTvPrfpdto.setText(item.prfpdto);
        Glide.with(context).load(item.poster).error(R.drawable.error).into(holder.binding.itemIvPoster);

        //즐겨찾기 아이콘 표시
        if(favoriteDB.isFavorited(item.mt20id)) {
            holder.binding.itemBtnFavorite.setImageResource(R.drawable.ic_favorite_filled_24);
        }
        else {
            holder.binding.itemBtnFavorite.setImageResource(R.drawable.ic_favorite_unfilled_24);
        }

        // 아이템 레이아웃 클릭하면 상세정보 액티비티 보여주기
        holder.binding.itemLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("mt20id", item.mt20id);
            context.startActivity(intent);
        });

        //하트모양 이미지뷰 클릭하면 즐겨찾기DB에 추가/삭제하기
        holder.binding.itemBtnFavorite.setOnClickListener(view -> {
            if (favoriteDB.isFavorited(item.mt20id)){
                favoriteDB.deleteFavorite(item.mt20id);
                holder.binding.itemBtnFavorite.setImageResource(R.drawable.ic_favorite_unfilled_24);
                Toast.makeText(context, "북마크를 취소합니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                favoriteDB.insertFavorite(item.mt20id, item.prfnm, item.genrenm, item.prfpdfrom, item.prfpdto, item.poster, item.fcltynm);
                holder.binding.itemBtnFavorite.setImageResource(R.drawable.ic_favorite_filled_24);
                Toast.makeText(context, "해당 공연을 북마크합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ItemSearchFragmentBinding binding;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSearchFragmentBinding.bind(itemView);
        }
    }

}
