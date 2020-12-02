package gov.dot.fhwa.saxton.crossingrequest;


import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class FlashingView extends View {
    private ValueAnimator flashAnimator;
    private static String FLASH_STATE = "FLASH_STATE";
    private static final int flashRateHz = 4;
    private int canvasColor = R.color.flashStateA;
    private Paint diamondPaint = new Paint();
    private static final double DIAMOND_WEIGHT = 0.5;


    public FlashingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        flashAnimator = new ValueAnimator();
        PropertyValuesHolder flashState = PropertyValuesHolder.ofFloat(FLASH_STATE, 0.0f, 1.0f);
        flashAnimator.setValues(flashState);
        flashAnimator.setDuration(1000 / flashRateHz);
        flashAnimator.setInterpolator(new LinearInterpolator());
        flashAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if ((float) valueAnimator.getAnimatedValue(FLASH_STATE) < 0.5f) {
                    canvasColor = R.color.flashStateA;
                } else {
                    canvasColor = R.color.flashStateB;
                }

                invalidate();
            }
        });
        flashAnimator.setRepeatCount(ValueAnimator.INFINITE);
        flashAnimator.start();
    }

    public FlashingView(Context context) {
        super(context);
        flashAnimator = new ValueAnimator();
        PropertyValuesHolder flashState = PropertyValuesHolder.ofFloat(FLASH_STATE, 0.0f, 1.0f);
        flashAnimator.setValues(flashState);
        flashAnimator.setDuration(1000 / flashRateHz);
        flashAnimator.setInterpolator(new LinearInterpolator());
        flashAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if ((float) valueAnimator.getAnimatedValue(FLASH_STATE) < 0.5f) {
                    canvasColor = R.color.flashStateA;
                } else {
                    canvasColor = R.color.flashStateB;
                }

                invalidate();
            }
        });
        flashAnimator.setRepeatCount(ValueAnimator.INFINITE);
        flashAnimator.start();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(ContextCompat.getColor(getContext(), canvasColor));

        // Draw center diamond
        int canvasCenterX = getWidth() / 2;
        int canvasCenterY = getHeight() / 2;
        int diamondSize = (int) (Math.min(getWidth(), getHeight()) * DIAMOND_WEIGHT / 2);

        canvas.rotate(45.0f, canvasCenterX, canvasCenterY);
        diamondPaint.setColor(ContextCompat.getColor(getContext(),
                (canvasColor == R.color.flashStateA ?
                R.color.flashStateB :
                R.color.flashStateA)));

        canvas.drawRect(canvasCenterX - diamondSize,
                        canvasCenterY - diamondSize,
                        canvasCenterX + diamondSize,
                canvasCenterY + diamondSize,
                        diamondPaint);
    }
}
