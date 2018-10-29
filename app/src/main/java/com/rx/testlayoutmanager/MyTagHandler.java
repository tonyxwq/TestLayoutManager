package com.rx.testlayoutmanager;

/**
 * Author:XWQ
 * Time   2018/10/20
 * Descrition: this is MyTagHandler
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;


import org.xml.sax.XMLReader;

import java.util.Locale;

public class MyTagHandler implements Html.TagHandler {

    private Context mContext;
    private PopupWindow popupWindow;
    //需要放大的图片
    //private SubsamplingScaleImageView tecent_chat_image;

    public MyTagHandler() {
        super();
    }

    public MyTagHandler(Context context) {
        mContext = context;
        /*mContext = context.getApplicationContext();
        View popView = LayoutInflater.from(context).inflate(R.layout.image_scale, null);
        tecent_chat_image = (SubsamplingScaleImageView) popView.findViewById(R.id.image_scale_image);
        popView.findViewById(R.id.image_scale_rll).setOnClickListener(onClickListener);
        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);// 设置允许在外点击消失
        ColorDrawable dw = new ColorDrawable(0x50000000);
        popupWindow.setBackgroundDrawable(dw);*/
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(popupWindow!=null && popupWindow.isShowing()){
                popupWindow.dismiss();
            }
        }
    };

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        // 处理标签<img>
        if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
            // 获取长度
            int len = output.length();
            // 获取图片地址
            ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
            String imgURL = images[0].getSource();
            // 使图片可点击并监听点击事件
            output.setSpan(new ClickableImage(mContext, imgURL), len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private class ClickableImage extends ClickableSpan {

        private String url;
        private Context context;
        public ClickableImage(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        @Override
        public void onClick(View widget) {
            Log.e("点击图片的路径地址", url);
           /* Bundle bundle = new Bundle();
            bundle.putString("filePath", url);
            Intent intent = new Intent(context, ImageZoomActivity.class);
            intent.putExtras(bundle);
            context.startActivity(intent);*/

           Log.d("data","================="+url);

            /*// 进行图片点击之后的处理
            Log.e("ytp", "点击了图片:url:" + url);
            popupWindow.showAtLocation(widget, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            //Bitmap bitmap = BitmapFactory.decodeFile(url);
            //tecent_chat_image.setImageBitmap(bitmap);
            tecent_chat_image.setImage(ImageSource.uri(url));
            tecent_chat_image.setBackground(mContext.getResources().getDrawable(R.color.colorAccent));*/

        }
    }
}
