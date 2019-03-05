package orsc.util;

import orsc.graphics.gui.MenuItem;

public final class ArrayUtil {
	public static void putZero(int[] data, int offset, int count) {
		for (count = offset + count - 7; offset < count; ) {
			data[offset++] = 0;
			data[offset++] = 0;
			data[offset++] = 0;
			data[offset++] = 0;
			data[offset++] = 0;
			data[offset++] = 0;
			data[offset++] = 0;
			data[offset++] = 0;
		}

		for (count += 7; offset < count; ) data[offset++] = 0;
	}

	public static void alignedCopy(byte[] src, int sI, byte[] dest, int dI, int count) {
		if (src == dest) {
			if (sI == dI) return;

			if (dI > sI && dI < sI + count) {
				--count;
				sI += count;
				dI += count;
				count = sI - count;

				for (count += 7; sI >= count; dest[dI--] = src[sI--]) {
					dest[dI--] = src[sI--];
					dest[dI--] = src[sI--];
					dest[dI--] = src[sI--];
					dest[dI--] = src[sI--];
					dest[dI--] = src[sI--];
					dest[dI--] = src[sI--];
					dest[dI--] = src[sI--];
				}

				for (count -= 7; sI >= count; dest[dI--] = src[sI--]) ;

				return;
			}
		}

		count += sI;

		for (count -= 7; sI < count; dest[dI++] = src[sI++]) {
			dest[dI++] = src[sI++];
			dest[dI++] = src[sI++];
			dest[dI++] = src[sI++];
			dest[dI++] = src[sI++];
			dest[dI++] = src[sI++];
			dest[dI++] = src[sI++];
			dest[dI++] = src[sI++];
		}

		for (count += 7; sI < count; dest[dI++] = src[sI++]) ;

	}

	private static void quickSort(MenuItem[] items, int[] priority, int left, int right) {
		try {
			if (right > left) {
				int mid = (right + left) / 2;
				int pivot = left;
				int midPriority = priority[mid];
				priority[mid] = priority[right];
				priority[right] = midPriority;
				MenuItem var8 = items[mid];
				items[mid] = items[right];
				items[right] = var8;
				int var9 = midPriority == Integer.MAX_VALUE ? 0 : 1;

				for (int i = left; i < right; ++i)
					if ((i & var9) + midPriority > priority[i]) {
						int myPrior = priority[i];
						priority[i] = priority[pivot];
						priority[pivot] = myPrior;
						MenuItem var12 = items[i];
						items[i] = items[pivot];
						items[pivot++] = var12;
					}

				priority[right] = priority[pivot];
				priority[pivot] = midPriority;
				items[right] = items[pivot];
				items[pivot] = var8;
				quickSort(items, priority, left, pivot - 1);
				quickSort(items, priority, 1 + pivot, right);
			}

		} catch (RuntimeException var13) {
			throw GenUtil.makeThrowable(var13, "ub.A(" + "{...}" + ',' + "dummy" + ',' + left + ',' + right + ',' + "{...}" + ')');
		}
	}

	public static void quickSort(MenuItem[] items, int[] priority) {
		try {
			quickSort(items, priority, 0, priority.length - 1);
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "cb.D(" + "dummy" + ',' + (items != null ? "{...}" : "null") + ',' + "{...}" + ')');
		}
	}
}