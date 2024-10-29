package com.example.condiplant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.List;

public class BoundingBoxView extends View {
    private List<Rect> boundingBoxes;
    private Paint paint;

    public BoundingBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
    }

    public void setBoundingBoxes(List<Rect> boundingBoxes) {
        this.boundingBoxes = boundingBoxes;
        invalidate(); // Redraw the view
    }

    public void clearBoundingBoxes(){
        this.boundingBoxes = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (boundingBoxes != null) {
            for (Rect rect : boundingBoxes) {
                // If the image is flipped, the left will be translated to right, and the right to left.
                float x0 = (rect.left);
                float x1 = (rect.right);
                rect.left = (int) Math.min(x0, x1);
                rect.right = (int) Math.max(x0, x1);
                rect.top = (rect.top);
                rect.bottom = (rect.bottom);
                canvas.drawRect(rect, paint);
            }
        } else {
            canvas.drawRect(0,0,0, 0, paint);
        }
    }
}
