package com.prismsoftworks.openweatherapitest.service;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.prismsoftworks.openweatherapitest.R;
import com.prismsoftworks.openweatherapitest.object.CityViewHolder;

public class RecyclerTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;

    public RecyclerTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    private boolean listHasItems(){
        return (CityListService.getInstance().getList().get(0) != null);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null && listHasItems()) {
            final View foregroundView = ((CityViewHolder) viewHolder).itemFg;
            foregroundView.setBackgroundColor(foregroundView.getContext().getResources().getColor(R.color.colorPrimaryDark));
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        if(listHasItems()) {
            final View foregroundView = ((CityViewHolder) viewHolder).itemFg;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if(listHasItems()) {
            final View foregroundView = ((CityViewHolder) viewHolder).itemFg;
            foregroundView.setBackgroundColor(foregroundView.getContext().getResources().getColor(R.color.white));
            getDefaultUIUtil().clearView(foregroundView);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        if(listHasItems()) {
            final View foregroundView = ((CityViewHolder) viewHolder).itemFg;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if(listHasItems()) {
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if(listHasItems()) {
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        } else {
            return -1;
        }
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}

