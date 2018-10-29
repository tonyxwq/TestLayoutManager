package com.rx.testlayoutmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity
{
    private RecyclerView mRecyclerView;
    private Button mButton;
    MyAdapter myAdapter;
    LayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.buttonPanel);
        mRecyclerView = findViewById(R.id.recycler_view);
        manager = new LayoutManager(mRecyclerView);
        mRecyclerView.setLayoutManager(manager);
        myAdapter = new MyAdapter();
        mRecyclerView.setAdapter(myAdapter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
    {
        private String[] images = {"http://g.hiphotos.baidu.com/image/pic/item/5243fbf2b211931376d158d568380cd790238dc1.jpg",
                "http://e.hiphotos.baidu.com/image/pic/item/aec379310a55b3199f70cd0e4ea98226cffc173b.jpg",
                "http://e.hiphotos.baidu.com/image/pic/item/4b90f603738da977f53a9d57bd51f8198618e3b1.jpg",
                "http://b.hiphotos.baidu.com/image/pic/item/5243fbf2b211931369cbb5cb68380cd790238dc5.jpg",
                "http://h.hiphotos.baidu.com/image/pic/item/d043ad4bd11373f0cd65c8faa90f4bfbfbed0478.jpg",
                "http://f.hiphotos.baidu.com/image/pic/item/7acb0a46f21fbe0916db4d7066600c338644adc9.jpg"};

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_echelon, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position)
        {
            Glide.with(MainActivity.this).load(images[position % 4]).into(holder.imageView);
        }

        @Override
        public int getItemCount()
        {
            return 400;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            ImageView imageView;

            public ViewHolder(View itemView)
            {
                super(itemView);
                imageView = itemView.findViewById(R.id.img_bg);
            }
        }
    }

}
