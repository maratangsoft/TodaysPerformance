package com.maratangsoft.todaysperformance;

import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.maratangsoft.todaysperformance.databinding.FragmentFavoriteBinding;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FavoriteFragment extends Fragment {
    FragmentFavoriteBinding binding;

    FavoriteDB db;
    ArrayList<RecyclerViewItem> itemList = new ArrayList<>();
    FavoriteFragmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).setSupportActionBar(binding.toolbar);

        adapter = new FavoriteFragmentAdapter(getActivity(), itemList);
        binding.recycler.setAdapter(adapter);

        db = new FavoriteDB(getActivity());
        loadFavorite();
        
        //스와이프로 DB와 리사이클러뷰에서 제거하기
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                //getBindingAdapterPosition()과 getAbsoluteAdapterPosition() 두 가지가 있음
                int position = viewHolder.getBindingAdapterPosition();
                if (direction == ItemTouchHelper.LEFT){
                    String idWantsToDelete = itemList.get(position).mt20id;
                    itemList.remove(position);
                    adapter.notifyItemRemoved(position);
                    db.deleteFavorite(idWantsToDelete);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                RecyclerViewSwipeDecorator.Builder builder = new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                builder.addSwipeLeftLabel("삭제");
                builder.create().decorate();
            }
        });
        touchHelper.attachToRecyclerView(binding.recycler);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) loadFavorite();
    }

    @Override
    public void onResume() { //TODO 디테일 액티비티 갔다왔을때 목록 갱신시키기
        super.onResume();
        loadFavorite();
    }

    public void loadFavorite(){
        itemList.clear();
        Cursor cursor = db.readAllData();
        if(cursor.getCount() != 0){ //레코드가 비어 있지 않을 때만 작업
            while (cursor.moveToNext()){ //커서가 더이상 moveToNext()를 할 수 없을 때까지
                RecyclerViewItem item = new RecyclerViewItem(cursor.getString(0), cursor.getString(1)
                        , cursor.getString(2), cursor.getString(3)
                        , cursor.getString(4), cursor.getString(5), cursor.getString(6));
                itemList.add(item);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
