package com.rx.testlayoutmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import java.util.ArrayList;

/**
 * Author:XWQ
 * Time   2018/10/18
 * Descrition: this is EchelonLayoutManager
 */

public class EchelonLayoutManager extends RecyclerView.LayoutManager
{
    private int mItemViewWidth;
    private int mItemViewHeight;
    private float mScale = 0.9f;
    private int mScrollOffset = Integer.MAX_VALUE;
    private RecyclerView recyclerView;
    private RecyclerView.Recycler recyclers;

    public EchelonLayoutManager(RecyclerView recyclerView)
    {
        mItemViewWidth = (int) (getHorizontalSpace() * 0.87f);//item的宽
        mItemViewHeight = (int) (mItemViewWidth * 1.46f);//item的高
        this.recyclerView = recyclerView;
        //recyclerView.setOnFlingListener(onFlingListener);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
    {
        //如果没有item，直接返回
        if (getItemCount() <= 0) return;
        // 跳过preLayout，preLayout主要用于支持动画
        if (state.isPreLayout())
        {
            return;
        }
        removeAndRecycleAllViews(recycler);
        mItemViewWidth = (int) (getHorizontalSpace() * 0.87f);
        mItemViewHeight = (int) (mItemViewWidth * 1.46f);
        mScrollOffset = Math.min(Math.max(mItemViewHeight, mScrollOffset), getItemCount() * mItemViewHeight);
        //Log.d("data","========mItemViewHeight========"+mItemViewHeight);
        layoutChild(recycler, 0);
        recyclers = recycler;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view)
    {
        super.onAttachedToWindow(view);
        //check when raise finger and settle to the appropriate item
        view.setOnTouchListener(mTouchListener);
        view.setOnFlingListener(mOnFlingListener);
    }

    float starty = 0, endy = 0;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    starty = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    endy = event.getY();
                    if ((endy - starty) > 0 && (endy - starty) > 100)//向下滑动
                    {
                        int scrow = (int) (mItemViewHeight - (endy - starty));
                        recyclerView.scrollBy(0, -(scrow));

                    } else if ((endy - starty) < 0 && (endy - starty) < 100)
                    {
                        int scrow = (int) (mItemViewHeight + (endy - starty));
                        recyclerView.scrollBy(0, scrow);
                    }
                    break;
            }
            return false;
        }
    };

    private RecyclerView.OnFlingListener mOnFlingListener = new RecyclerView.OnFlingListener()
    {
        @Override
        public boolean onFling(int velocityX, int velocityY)
        {
            return true;
        }
    };

    private void brewAndStartAnimator()
    {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(recyclerView, "translationY", 0f, 200f);
//      设置移动时间
        objectAnimator.setDuration(1000);
//      开始动画
        objectAnimator.start();

    }

    private void layoutChild(RecyclerView.Recycler recycler, int dy)
    {
        if (getItemCount() == 0) return;
        int bottomItemPosition = (int) Math.floor((mScrollOffset) / mItemViewHeight);
        //Log.d("data","========bottomItemPosition========"+bottomItemPosition);

        int remainSpace = getVerticalSpace() - mItemViewHeight;

        int bottomItemVisibleHeight = mScrollOffset % mItemViewHeight;
        final float offsetPercentRelativeToItemView = bottomItemVisibleHeight * 1.0f / mItemViewHeight;

        ArrayList<ItemViewInfo> layoutInfos = new ArrayList<>();
        for (int i = bottomItemPosition - 1, j = 1; i >= 0; i--, j++)
        {
            double maxOffset = (getVerticalSpace() - mItemViewHeight) / 2 * Math.pow(0.8, j);
            int start = (int) (remainSpace - offsetPercentRelativeToItemView * maxOffset);
            float scaleXY = (float) (Math.pow(mScale, j - 1) * (1 - offsetPercentRelativeToItemView * (1 - mScale)));
            ItemViewInfo info = new ItemViewInfo(start, scaleXY);
            layoutInfos.add(0, info);
            remainSpace = (int) (remainSpace - maxOffset);
            if (remainSpace <= 0)
            {
                info.setTop((int) (remainSpace + maxOffset));
                info.setPositionOffset(0);
                info.setLayoutPercent(info.getTop() / getVerticalSpace());
                info.setScaleXY((float) Math.pow(mScale, j - 1));
                break;
            }
        }
        if (bottomItemPosition < getItemCount())
        {
            final int start = getVerticalSpace() - bottomItemVisibleHeight;
            layoutInfos.add(new ItemViewInfo(start, 1.0f)
                    .setIsBottom());
            mScrollOffset = mScrollOffset;

            //Log.d("data","============="+dy);

        } else
        {
            bottomItemPosition = bottomItemPosition - 1;//99
        }

        int layoutCount = layoutInfos.size();
        final int startPos = bottomItemPosition - (layoutCount - 1);
        final int endPos = bottomItemPosition;
        final int childCount = getChildCount();
        //Log.d("data","=========layoutCount===layoutCount========="+layoutCount);
        for (int i = childCount - 1; i >= 0; i--)
        {
            View childView = getChildAt(i);
            int pos = getPosition(childView);
            if (pos > endPos || pos < startPos)
            {
                removeAndRecycleView(childView, recycler);
            }
        }

        detachAndScrapAttachedViews(recycler);

        for (int i = 0; i < layoutCount; i++)
        {
            View view = recycler.getViewForPosition(startPos + i);
            ItemViewInfo layoutInfo = layoutInfos.get(i);
            addView(view);
            measureChildWithExactlySize(view);
            int left = (getHorizontalSpace() - mItemViewWidth) / 2;
            layoutDecoratedWithMargins(view, left, layoutInfo.getTop(), left + mItemViewWidth, layoutInfo.getTop() + mItemViewHeight);
            view.setPivotX(view.getWidth() / 2);
            view.setPivotY(0);
            view.setScaleX(layoutInfo.getScaleXY());
            view.setScaleY(layoutInfo.getScaleXY());
        }
    }


    /**
     * 测量itemview的确切大小
     */
    private void measureChildWithExactlySize(View child)
    {
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(mItemViewWidth, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(mItemViewHeight, View.MeasureSpec.EXACTLY);
        child.measure(widthSpec, heightSpec);
    }


    @Override
    public boolean canScrollVertically()
    {
        return true;
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state)
    {
        mScrollOffset = Math.min(Math.max(mItemViewHeight, mScrollOffset + dy), getItemCount() * mItemViewHeight);
        //Log.d("data","=====mScrollOffset======"+(mScrollOffset + dy));
        layoutChild(recycler, 0);
        return dy;
    }


   /* @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state)
    {
        //实际要滑动的距离
        int travel = dy;

        //如果滑动到最顶部
        if (verticalScrollOffset + dy < 0)
        {
            travel = -verticalScrollOffset;

        } else if (verticalScrollOffset + dy > getTotalHeight() - getVerticalSpace())
        {//如果滑动到最底部
            travel = getTotalHeight() - getVerticalSpace() - verticalScrollOffset;
        }

        //将竖直方向的偏移量+travel
        verticalScrollOffset += travel;

        // 平移容器内的item
        offsetChildrenVertical(-travel);
        return travel;
    }
*/

    /**
     * 获取RecyclerView的显示高度
     */
    public int getVerticalSpace()
    {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    /**
     * 获取RecyclerView的显示宽度
     */
    public int getHorizontalSpace()
    {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }


    public int getTotalHeight()
    {
        return getItemCount() * getHeight();
    }


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams()
    {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }


    /**
     * 目标项是否在最后一个可见项之后
     */
    private boolean mShouldScroll;
    /**
     * 记录目标项位置
     */
    private int mToPosition;

    /**
     * 滑动到指定位置
     *
     * @param mRecyclerView
     * @param position
     */
    public void smoothMoveToPosition(RecyclerView mRecyclerView, final int position)
    {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));

        if (position < firstItem)
        {
            // 如果跳转位置在第一个可见位置之前，就smoothScrollToPosition可以直接跳转
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem)
        {
            // 跳转位置在第一个可见项之后，最后一个可见项之前
            // smoothScrollToPosition根本不会动，此时调用smoothScrollBy来滑动到指定位置
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount())
            {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else
        {
            // 如果要跳转的位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }


}
