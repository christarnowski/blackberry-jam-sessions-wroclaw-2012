package pl.itraff.camera.utils;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;

/**
 * class from barcode scanner with aditional function that allows to obtain
 * color picture from yuv preview format
 * 
 * @author qba
 * 
 */
public final class PlanarYUVLuminanceSource extends LuminanceSource {
	private final byte[] yuvData;
	private final int dataWidth;
	private final int dataHeight;
	private final int left;
	private final int top;

	public PlanarYUVLuminanceSource(byte[] yuvData, int dataWidth,
			int dataHeight, int left, int top, int width, int height) {
		super(width, height);

		if (left + width > dataWidth || top + height > dataHeight) {
			throw new IllegalArgumentException(
					"Crop rectangle does not fit within image data.");
		}

		this.yuvData = yuvData;
		this.dataWidth = dataWidth;
		this.dataHeight = dataHeight;
		this.left = left;
		this.top = top;
	}

	@Override
	public byte[] getRow(int y, byte[] row) {
		if (y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException(
					"Requested row is outside the image: " + y);
		}
		int width = getWidth();
		if (row == null || row.length < width) {
			row = new byte[width];
		}
		int offset = (y + top) * dataWidth + left;
		System.arraycopy(yuvData, offset, row, 0, width);
		return row;
	}

	@Override
	public byte[] getMatrix() {
		int width = getWidth();
		int height = getHeight();

		// If the caller asks for the entire underlying image, save the copy and
		// give them the
		// original data. The docs specifically warn that result.length must be
		// ignored.
		if (width == dataWidth && height == dataHeight) {
			return yuvData;
		}

		int area = width * height;
		byte[] matrix = new byte[area];
		int inputOffset = top * dataWidth + left;

		// If the width matches the full width of the underlying data, perform a
		// single copy.
		if (width == dataWidth) {
			System.arraycopy(yuvData, inputOffset, matrix, 0, area);
			return matrix;
		}

		// Otherwise copy one cropped row at a time.
		byte[] yuv = yuvData;
		for (int y = 0; y < height; y++) {
			int outputOffset = y * width;
			System.arraycopy(yuv, inputOffset, matrix, outputOffset, width);
			inputOffset += dataWidth;
		}
		return matrix;
	}

	@Override
	public boolean isCropSupported() {
		return true;
	}

	public int getDataWidth() {
		return dataWidth;
	}

	public int getDataHeight() {
		return dataHeight;
	}

	public Bitmap renderCroppedGreyscaleBitmap() {
		int width = getWidth();
		int height = getHeight();
		int[] pixels = new int[width * height];
		byte[] yuv = yuvData;
		int inputOffset = top * dataWidth + left;

		for (int y = 0; y < height; y++) {
			int outputOffset = y * width;
			for (int x = 0; x < width; x++) {
				int grey = yuv[inputOffset + x] & 0xff;
				pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
			}
			inputOffset += dataWidth;
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * decode rgb picture from yuv
	 * 
	 * @param rgb
	 *            output array that will be filled with pixel color values
	 */
	public void decodeYUV420SP(int[] rgb) {
		final int frameSize = dataWidth * dataHeight;
		for (int j = 0, yp = 0; j < dataHeight; j++) {
			int uvp = frameSize + (j >> 1) * dataWidth, u = 0, v = 0;
			for (int i = 0; i < dataWidth; i++, yp++) {
				int y = (0xff & ((int) yuvData[yp])) - 16;
				if (y < 0) {
					y = 0;
				}
				if ((i & 1) == 0) {
					v = (0xff & yuvData[uvp++]) - 128;
					u = (0xff & yuvData[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0) {
					r = 0;
				} else if (r > 262143) {
					r = 262143;
				}
				if (g < 0) {
					g = 0;
				} else if (g > 262143) {
					g = 262143;
				}
				if (b < 0) {
					b = 0;
				} else if (b > 262143) {
					b = 262143;
				}

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}

}