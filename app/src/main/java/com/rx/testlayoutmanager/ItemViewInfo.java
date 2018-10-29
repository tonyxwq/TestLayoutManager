package com.rx.testlayoutmanager;

/**
 * Author:XWQ
 * Time   2018/10/22
 * Descrition: this is ItemViewInfo
 */

public class ItemViewInfo
{
    private float mScaleXY;
    private float mLayoutPercent;
    private float mPositionOffset;
    private int mTop;
    private boolean mIsBottom;
    private float mmp;

    public ItemViewInfo(int top, float scaleXY)
    {
        this.mTop = top;
        this.mScaleXY = scaleXY;

    }

    public ItemViewInfo(float scaleXY)
    {
        this.mScaleXY = scaleXY;

    }

    public ItemViewInfo setIsBottom()
    {
        mIsBottom = true;
        return this;
    }

    public float getScaleXY()
    {
        return mScaleXY;
    }

    public void setScaleXY(float mScaleXY)
    {
        this.mScaleXY = mScaleXY;
    }

    public float getLayoutPercent()
    {
        return mLayoutPercent;
    }

    public void setLayoutPercent(float mLayoutPercent)
    {
        this.mLayoutPercent = mLayoutPercent;
    }

    public float getPositionOffset()
    {
        return mPositionOffset;
    }

    public void setPositionOffset(float mPositionOffset)
    {
        this.mPositionOffset = mPositionOffset;
    }

    public int getTop()
    {
        return mTop;
    }

    public void setTop(int mTop)
    {
        this.mTop = mTop;
    }

}
