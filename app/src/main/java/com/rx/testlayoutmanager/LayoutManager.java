package com.rx.testlayoutmanager;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Author:XWQ
 * Time   2018/10/18
 * Descrition: this is LayoutManager
 */

public class LayoutManager extends RecyclerView.LayoutManager
{
    private int temp = 0;
    private float starty = 0, endy = 0;
    private int mItemViewWidth;
    private int mItemViewHeight;
    private float mScale = 0.9f;
    private float mWidthScale = 0.87f;
    private float mHeigthScale = 1.46f;
    private int mScrollOffset = Integer.MAX_VALUE;
    private RecyclerView recyclerView;
    private RecyclerView.Recycler recyclers;

    public LayoutManager(RecyclerView recyclerView)
    {
        mItemViewWidth = (int) (getHorizontalSpace() * mWidthScale);//item的宽
        mItemViewHeight = (int) (mItemViewWidth * mHeigthScale);//item的高
        this.recyclerView = recyclerView;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
    {
        //如果没有item，直接返回
        if (getItemCount() <= 0) return;
        // 跳过preLayout，preLayout主要用于支持动画
        if (state.isPreLayout()) return;
        removeAndRecycleAllViews(recycler);
        mItemViewWidth = (int) (getHorizontalSpace() * mWidthScale);
        mItemViewHeight = (int) (mItemViewWidth * mHeigthScale);
        mScrollOffset = Math.min(Math.max(mItemViewHeight, mScrollOffset), getItemCount() * mItemViewHeight);
        layoutChild(recycler, 0);
        recyclers = recycler;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view)
    {
        super.onAttachedToWindow(view);
        view.setOnTouchListener(mTouchListener);
        view.setOnFlingListener(mOnFlingListener);
    }

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
                    if ((endy - starty) > 0 && (endy - starty) > 1)//向下滑动
                    {
                        int scrow = (int) (mItemViewHeight - (endy - starty));
                        temp = 0;
                        ValueAnimator(scrow);

                    } else if ((endy - starty) < 0 && (endy - starty) < 1)
                    {
                        int scrow = (int) (mItemViewHeight + (endy - starty));
                        temp = 0;
                        ValueAnimator(-scrow);
                    }
                    break;
            }
            return false;
        }
    };


    private void ValueAnimator(int index)
    {
        ValueAnimator anim = ValueAnimator.ofInt(0, index);
        // 设置动画运行的时长
        anim.setDuration(200);
        anim.setRepeatCount(0);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int currentValue = (Integer) animation.getAnimatedValue();
                int currentmove = currentValue - temp;
                recyclerView.scrollBy(0, -(currentmove));
                temp = currentValue;
            }
        });
        anim.start();
        // 启动动画
    }

    private RecyclerView.OnFlingListener mOnFlingListener = new RecyclerView.OnFlingListener()
    {
        @Override
        public boolean onFling(int velocityX, int velocityY)
        {
            return true;
        }
    };

    private void layoutChild(RecyclerView.Recycler recycler, int dy)
    {
        if (getItemCount() == 0) return;
        int bottomItemPosition = (int) Math.floor((mScrollOffset) / mItemViewHeight);
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
        } else
        {
            bottomItemPosition = bottomItemPosition - 1;//99
        }

        int layoutCount = layoutInfos.size();
        final int startPos = bottomItemPosition - (layoutCount - 1);
        final int endPos = bottomItemPosition;
        final int childCount = getChildCount();
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
}
