package com.hecorat.dragndrop;

import android.content.ClipData;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    ImageView imageView1, imageView2, shadow1, shadow2, selectedImage;
    RelativeLayout layout1, layout2;
    RelativeLayout.LayoutParams imageParams1, imageParams2, shadowParams1, shadowParams2, selectedImageParams;
    LinearLayout layoutTop, layoutBottom;
    ImageHolder imageHolder1, imageHolder2, selectedHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        imageView1 = (ImageView) findViewById(R.id.image_1);
        imageView2 = (ImageView) findViewById(R.id.image_2);
        layout1 = (RelativeLayout) findViewById(R.id.layout1);
        layout2 = (RelativeLayout) findViewById(R.id.layout2);
        layoutTop = (LinearLayout) findViewById(R.id.layout_top);
        layoutBottom = (LinearLayout) findViewById(R.id.layout_bottom);
        layoutTop.setOnDragListener(onDragListener);
        layoutBottom.setOnDragListener(onDragListener);
        layout2.setOnDragListener(onDragListener);
        layout1.setOnDragListener(onDragListener);

        imageView1.setOnLongClickListener(onImageLongClick);
        imageView2.setOnLongClickListener(onImageLongClick);
        shadow1 = (ImageView) findViewById(R.id.shadow_1);
        shadow2 = (ImageView) findViewById(R.id.shadow_2);

        imageParams1 = (RelativeLayout.LayoutParams) imageView1.getLayoutParams();
        imageParams2 = (RelativeLayout.LayoutParams) imageView2.getLayoutParams();
        shadowParams1 = (RelativeLayout.LayoutParams) shadow1.getLayoutParams();
        shadowParams2 = (RelativeLayout.LayoutParams) shadow2.getLayoutParams();

        imageHolder1 = new ImageHolder(imageView1, true);
        imageHolder2 = new ImageHolder(imageView2, true);
        imageView1.setTag(imageHolder1);
        imageView2.setTag(imageHolder2);
    }

    View.OnLongClickListener onImageLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            selectedHolder = (ImageHolder) view.getTag();
            selectedImage = (ImageView) view;
            selectedImageParams = (RelativeLayout.LayoutParams) selectedImage.getLayoutParams();
            selectedImage.setImageResource(R.drawable.image_highline);
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(clipData, shadowBuilder, view, 0);
            } else {
                view.startDrag(clipData, shadowBuilder, view, 0);
            }
            if (selectedHolder.inLayout1) {
                shadow1.setVisibility(View.VISIBLE);
                shadowParams1.leftMargin = selectedImageParams.leftMargin;
                shadow1.setLayoutParams(shadowParams1);
            } else {
                shadow2.setVisibility(View.VISIBLE);
                shadowParams2.leftMargin = selectedImageParams.leftMargin;
                shadow2.setLayoutParams(shadowParams2);
            }
            return false;
        }
    };

    private void setShadowVisiable(boolean shadow1Visiable) {
        int visiable1 = shadow1Visiable ? View.VISIBLE : View.INVISIBLE;
        int visiable2 = shadow1Visiable ? View.INVISIBLE : View.VISIBLE;
        shadow1.setVisibility(visiable1);
        shadow2.setVisibility(visiable2);
    }

    View.OnDragListener onDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            int x = (int) dragEvent.getX();

            if (view.equals(layout1) || view.equals(layoutTop)) {
                if (x != 0) {
                    shadowParams1.leftMargin = (int) dragEvent.getX();
                    shadow1.setLayoutParams(shadowParams1);
                    selectedImage.setImageResource(R.drawable.image_normal);
                }
                setShadowVisiable(true);
            }
            if (view.equals(layout2) || view.equals(layoutBottom)) {
                if (x != 0) {
                    shadowParams2.leftMargin = (int) dragEvent.getX();
                    shadow2.setLayoutParams(shadowParams2);
                    selectedImage.setImageResource(R.drawable.image_normal);
                }
                setShadowVisiable(false);
            }

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    selectedImageParams.leftMargin = (int) dragEvent.getX();
                    selectedImage.setLayoutParams(selectedImageParams);
                    if (view.equals(layout1) || view.equals(layoutTop)) {
                        if (!selectedHolder.inLayout1) {
                            layout2.removeView(selectedImage);
                            layout1.addView(selectedImage);
                        }
                        selectedHolder.inLayout1 = true;
                    }

                    if (view.equals(layout2) || view.equals(layoutBottom)) {
                        if (selectedHolder.inLayout1) {
                            layout1.removeView(selectedImage);
                            layout2.addView(selectedImage);
                        }
                        selectedHolder.inLayout1 = false;
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    shadow1.setVisibility(View.INVISIBLE);
                    shadow2.setVisibility(View.INVISIBLE);
                    break;
            }
            return true;
        }
    };

    private void log(String msg) {
        Log.e("Drag and drop", msg);
    }

    class ImageHolder {
        ImageView imageView;
        boolean inLayout1;

        ImageHolder(ImageView image, boolean inLayout){
            imageView = image;
            inLayout1 = inLayout;
        }
    }
}
