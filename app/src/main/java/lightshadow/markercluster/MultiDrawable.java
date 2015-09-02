package lightshadow.markercluster;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by lightshadow on 15/9/2.
 */
public class MultiDrawable extends Drawable{
    private final List<Drawable> mDrawables;

    public MultiDrawable(List<Drawable> drawables) {
        mDrawables = drawables;
    }

    @Override
    public void draw(Canvas canvas) {
        if(mDrawables.size() == 1){
            mDrawables.get(0).draw(canvas);
        }
        int width = getBounds().width();
        int height = getBounds().height();

        canvas.save();
        canvas.clipRect(0, 0, width, height);

        if (mDrawables.size() == 2 || mDrawables.size() == 3) {
            // Paint left half
            canvas.save();
            canvas.clipRect(0, 0, width / 2, height);
            canvas.translate(-width / 4, 0);
            mDrawables.get(0).draw(canvas);
            canvas.restore();
        }
        if (mDrawables.size() == 2) {
            // Paint right half
            canvas.save();
            canvas.clipRect(width / 2, 0, width, height);
            canvas.translate(width / 4, 0);
            mDrawables.get(1).draw(canvas);
            canvas.restore();
        } else {
            // Paint top right
            canvas.save();
            canvas.scale(.5f, .5f);
            canvas.translate(width, 0);
            mDrawables.get(1).draw(canvas);

            // Paint bottom right
            canvas.translate(0, height);
            mDrawables.get(2).draw(canvas);
            canvas.restore();
        }

        if (mDrawables.size() >= 4) {
            // Paint top left
            canvas.save();
            canvas.scale(.5f, .5f);
            mDrawables.get(0).draw(canvas);

            // Paint bottom left
            canvas.translate(0, height);
            mDrawables.get(3).draw(canvas);
            canvas.restore();
        }

        canvas.restore();

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

}
