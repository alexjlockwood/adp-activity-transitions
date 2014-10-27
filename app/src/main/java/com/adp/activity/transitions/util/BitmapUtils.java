package com.adp.activity.transitions.util;
//
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.util.SparseArray;
//
//import com.adp.activity.transitions.R;
//
//import java.util.ArrayList;
//
//public class BitmapUtils {
//
//    private static final int[] PHOTOS = {
//            R.drawable.p1,
//            R.drawable.p2,
//            R.drawable.p3,
//            R.drawable.p4,
//    };
//
//    private static final String[] CAPTIONS = {
//            "Picture #1",
//            "Picture #2",
//            "Picture #3",
//            "Picture #4",
//    };
//
//    private static SparseArray<Bitmap> sBitmapResourceMap = new SparseArray<Bitmap>();
//
//    /**
//     * Load pictures and descriptions. A real app wouldn't do it this way, but that's
//     * not the point of this animation demo. Loading asynchronously is a better way to go
//     * for what can be time-consuming operations.
//     */
//    public ArrayList<PictureData> loadPhotos(Resources resources) {
//        ArrayList<PictureData> pictures = new ArrayList<PictureData>();
//        for (int i = 0; i < PHOTOS.length; i++) {
//            int resourceId = PHOTOS[(int) (Math.random() * PHOTOS.length)];
//            Bitmap bitmap = getBitmap(resources, resourceId);
//            Bitmap thumbnail = getThumbnail(bitmap, 200);
//            String description = CAPTIONS[(int) (Math.random() * CAPTIONS.length)];
//            pictures.add(new PictureData(resourceId, description, thumbnail));
//        }
//        return pictures;
//    }
//
//    /**
//     * Utility method to get bitmap from cache or, if not there, load it
//     * from its resource.
//     */
//    static Bitmap getBitmap(Resources resources, int resourceId) {
//        Bitmap bitmap = sBitmapResourceMap.get(resourceId);
//        if (bitmap == null) {
//            bitmap = BitmapFactory.decodeResource(resources, resourceId);
//            sBitmapResourceMap.put(resourceId, bitmap);
//        }
//        return bitmap;
//    }
//
//    /**
//     * Create and return a thumbnail image given the original source bitmap and a max
//     * dimension (width or height).
//     */
//    private Bitmap getThumbnail(Bitmap original, int maxDimension) {
//        int width = original.getWidth();
//        int height = original.getHeight();
//        int scaledWidth, scaledHeight;
//        if (width >= height) {
//            float scaleFactor = (float) maxDimension / width;
//            scaledWidth = 200;
//            scaledHeight = (int) (scaleFactor * height);
//        } else {
//            float scaleFactor = (float) maxDimension / height;
//            scaledWidth = (int) (scaleFactor * width);
//            scaledHeight = 200;
//        }
//        return Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true);
//    }
//
//}
//
