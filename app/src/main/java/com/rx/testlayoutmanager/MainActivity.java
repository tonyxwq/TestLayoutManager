package com.rx.testlayoutmanager;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidessence.pinchzoomtextview.PinchZoomTextView;
import com.bumptech.glide.Glide;

import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private RecyclerView mRecyclerView;
    private Button mButton;
    Handler handler = new Handler();
    MyAdapter myAdapter;
    EchelonLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.buttonPanel);
        mRecyclerView = findViewById(R.id.recycler_view);
        manager = new EchelonLayoutManager(mRecyclerView);
        mRecyclerView.setLayoutManager(manager);
        myAdapter = new MyAdapter();
        mRecyclerView.setAdapter(myAdapter);
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //manager.smoothMoveToPosition(mRecyclerView, 2);
                handler.postDelayed(runnable, 50);
            }
        });
    }

    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
            //handler.postDelayed(this, 50);
        }

    };

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
    {
        private int[] icons = {R.mipmap.header_icon_1, R.mipmap.header_icon_2, R.mipmap.header_icon_3, R.mipmap.header_icon_4};
        private int[] bgs = {R.mipmap.bg_1, R.mipmap.bg_2, R.mipmap.bg_3, R.mipmap.bg_4};

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_echelon, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position)
        {
            holder.icon.setImageResource(icons[position % 4]);
            //holder.nickName.setText(nickNames[position % 4]);
            //holder.desc.setText(descs[position % 5]);
            holder.bg.setImageResource(bgs[position % 4]);
        }

        @Override
        public int getItemCount()
        {
            return 400;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView icon;
            ImageView bg;
            TextView nickName;
            TextView desc;

            public ViewHolder(View itemView)
            {
                super(itemView);
                icon = itemView.findViewById(R.id.img_icon);
                bg = itemView.findViewById(R.id.img_bg);
                nickName = itemView.findViewById(R.id.tv_nickname);
                desc = itemView.findViewById(R.id.tv_desc);

            }
        }
    }

}
