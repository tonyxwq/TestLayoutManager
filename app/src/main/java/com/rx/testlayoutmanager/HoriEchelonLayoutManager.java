package com.rx.testlayoutmanager;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Author:XWQ
 * Time   2018/10/25
 * Descrition: this is HoriEchelonLayoutManager
 */

public class HoriEchelonLayoutManager extends RecyclerView.LayoutManager
{
    private int mScrollOffset = Integer.MAX_VALUE;
    private float mScale = 0.9f;
    private int mViewWidth;
    private int mViewHeight;

    public HoriEchelonLayoutManager()
    {
        mViewWidth = (int) (getHeight() * 1.5);
        mViewHeight = (int) (mViewWidth * 0.8);
    }

    @Override
    public boolean canScrollHorizontally()
    {
        return true;
    }


    /**
     * 得到RecyclerView的高度
     *
     * @return
     */
    private int getVertical()
    {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }


    /**
     * 得到RecyclerView的显示宽度
     *
     * @return
     */
    private int getLevel()
    {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams()
    {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
    {
        if (getItemCount() == 0)
        {
            return;
        }

        if (state.isPreLayout())
        {
            return;
        }
        removeAndRecycleAllViews(recycler);
        mViewWidth = (int) (getLevel() * 1.46f);
        mViewHeight = (int) (getVertical() * 0.87f);
        mScrollOffset = Math.min(Math.max(mViewWidth, mScrollOffset), getItemCount() * mViewWidth);
        layoutChild(recycler);
    }


    public int calculateDistanceToPosition(int targetPos)
    {
        int pendingScrollOffset = mViewWidth * (convert2LayoutPosition(targetPos) + 1);
        return pendingScrollOffset - mScrollOffset;
    }



    public int convert2LayoutPosition(int adapterPostion)
    {
        return getItemCount() - 1 - adapterPostion;
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state)
    {
        mScrollOffset = Math.min(Math.max(mViewWidth, mScrollOffset + dx), getItemCount() * mViewWidth);
        layoutChild(recycler);
        return dx;
    }

    private void layoutChild(RecyclerView.Recycler recycler)
    {
        detachAndScrapAttachedViews(recycler);
        ArrayList<ItemViewInfo> layoutInfo = new ArrayList<>();
        int bottomItemPosition = (int) Math.floor(mScrollOffset / mViewWidth);
        int remainSpace = getLevel() - mViewWidth;
        int bottomItemVisibleHeight = mScrollOffset % mViewWidth;
        Log.d("data", "=====mScrollOffset=======" + mScrollOffset);
        final float offsetPercentRelativeToItemView = bottomItemVisibleHeight * 1.0f / mViewWidth;
        for (int i = bottomItemPosition - 1, j = 1; i >= 0; i--, j++)
        {
            double maxOffset = (getLevel() - mViewWidth) / 2 * Math.pow(0.8, j);
            int start = (int) (remainSpace - offsetPercentRelativeToItemView * maxOffset);
            float scaleXY = (float) (Math.pow(mScale, j - 1) * (1 - offsetPercentRelativeToItemView * (1 - mScale)));
            ItemViewInfo info = new ItemViewInfo(-start, scaleXY);
            layoutInfo.add(0, info);
            remainSpace = (int) (remainSpace - maxOffset);
        }

        int layoutCount = layoutInfo.size();
        for (int i = 0; i < layoutCount; i++)
        {
            View view = recycler.getViewForPosition(i);
            ItemViewInfo info = layoutInfo.get(i);
            addView(view);
            measureChildWithExactlySize(view);
            int left = (getVertical() - mViewHeight) / 2;
            //layoutDecoratedWithMargins(view, left, layoutInfo.getTop(), left + mItemViewWidth, layoutInfo.getTop() + mItemViewHeight);
            layoutDecoratedWithMargins(view, info.getTop() / 2, left, info.getTop() + mViewWidth, mViewHeight);
            //Log.d("data","======-info.getTop()/2========="+(-info.getTop()/2)+"=============="+(-info.getTop() + mViewWidth));
            view.setPivotX(0);
            view.setPivotY(view.getHeight() / 2);
            view.setScaleX(info.getScaleXY());
            view.setScaleY(info.getScaleXY());
        }

    }


    /**
     * 测量itemview的确切大小
     */
    private void measureChildWithExactlySize(View child)
    {
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(mViewWidth, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(mViewHeight, View.MeasureSpec.EXACTLY);
        child.measure(widthSpec, heightSpec);
    }


}
