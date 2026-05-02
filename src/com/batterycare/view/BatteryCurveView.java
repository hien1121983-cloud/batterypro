package com.batterycare.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class BatteryCurveView extends View {
    private final Paint referencePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint actualPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<Float> curveData = new ArrayList<>();

    public BatteryCurveView(Context context) {
        this(context, null);
    }

    public BatteryCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    private void initPaints() {
        gridPaint.setColor(0x33FFFFFF);
        gridPaint.setStrokeWidth(1f);
        referencePaint.setColor(0x88CCCCCC);
        referencePaint.setStyle(Paint.Style.STROKE);
        referencePaint.setStrokeWidth(3f);
        referencePaint.setPathEffect(new DashPathEffect(new float[]{10f, 10f}, 0f));
        actualPaint.setColor(0xFFFFFFFF);
        actualPaint.setStyle(Paint.Style.STROKE);
        actualPaint.setStrokeWidth(5f);
    }

    public void setCurveData(List<Float> data) {
        if (data == null) {
            this.curveData = new ArrayList<>();
        } else {
            this.curveData = new ArrayList<>(data);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float padding = 32f;
        drawGrid(canvas, width, height, padding);
        drawReferenceCurve(canvas, width, height, padding);
        drawActualCurve(canvas, width, height, padding);
    }

    private void drawGrid(Canvas canvas, float width, float height, float padding) {
        for (int i = 1; i < 5; i++) {
            float y = padding + (height - padding * 2) * i / 5f;
            canvas.drawLine(padding, y, width - padding, y, gridPaint);
        }
    }

    private void drawReferenceCurve(Canvas canvas, float width, float height, float padding) {
        Path path = new Path();
        float maxWidth = width - padding * 2;
        float maxHeight = height - padding * 2;
        int steps = 20;
        for (int i = 0; i <= steps; i++) {
            float x = padding + maxWidth * i / steps;
            float y = padding + maxHeight * (1f - (i / (float) steps));
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        canvas.drawPath(path, referencePaint);
    }

    private void drawActualCurve(Canvas canvas, float width, float height, float padding) {
        if (curveData.isEmpty()) {
            return;
        }
        Path path = new Path();
        float maxWidth = width - padding * 2;
        float maxHeight = height - padding * 2;
        int size = curveData.size();
        for (int i = 0; i < size; i++) {
            float proportion = curveData.get(i) / 100f;
            float x = padding + maxWidth * i / Math.max(1, size - 1);
            float y = padding + maxHeight * (1f - proportion);
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        canvas.drawPath(path, actualPaint);
    }
}
