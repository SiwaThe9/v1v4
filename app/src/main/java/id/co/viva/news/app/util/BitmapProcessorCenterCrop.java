package id.co.viva.news.app.util;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class BitmapProcessorCenterCrop implements BitmapProcessor {

	@Override
	public Bitmap process(Bitmap source) {
		Bitmap target = null;
		if (source.getWidth() == source.getHeight()) {
			return source;
		} else {
			int size = source.getWidth() > source.getHeight() ? source.getHeight() : source.getWidth(); 
			int offsetX = (source.getWidth() - size) / 2;
			int offsetY = (source.getHeight() - size) / 2;
			target = Bitmap.createBitmap(source, offsetX, offsetY, size, size);
			source.recycle();
			
			return target;
		}
	}

}
