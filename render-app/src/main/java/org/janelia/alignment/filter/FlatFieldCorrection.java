package org.janelia.alignment.filter;

import ij.IJ;
import ij.process.*;

import java.util.*;

/**
 * Java implementation of Flat Field Correction python code from Sharmi (flatfield_correct.py).
 *
 * TODO: Implement a small LRU cache or hook into render's imageProcessorCache.
 *
 * @author Eric Perlman
 */
public class FlatFieldCorrection implements InputFilter {

    private String flatFieldImagePath;

    @Override
    public ImageProcessor process(ImageProcessor ip, final double scale) {
        ImageProcessor ff = IJ.openImage(flatFieldImagePath).getProcessor();
        if (scale > 0) {
            ff = ff.resize(ip.getWidth(), ip.getHeight());
        }

        if (ip.getHeight() != ff.getHeight() || ip.getWidth() != ff.getWidth()) {
            throw new IllegalArgumentException("Image sizes differ between original image and flat field correction.\n");
        }

        // Crude copy of the python code using slow java arrays.
        // TODO: Replace with far more efficient ImageJ routines.

        // num = np.ones((ff.shape[0],ff.shape[1]))
        // fac = np.divide(num* np.amax(ff),ff+0.0001)
        float [][] fac = new float[ip.getHeight()][ip.getWidth()];
        float ffmax = (float)ff.getStatistics().max;
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                fac[y][x] = ffmax / ((float) ff.get(x, y) + 0.0001f);
            }
        }

        // result = np.multiply(img,fac)
        float [][] result = new float[ip.getHeight()][ip.getWidth()];
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                result[y][x] = (float) ip.get(x, y) * fac[y][x];
            }
        }

        // result = np.multiply(result,np.mean(img)/np.mean(result)
        float imgmean = (float)ip.getStatistics().mean;
        float resultMean = 0f;
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                resultMean += result[y][x];
            }
        }
        resultMean = resultMean / (ip.getHeight() * ip.getWidth());
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                result[y][x] = result[y][x] *  (imgmean / resultMean);
            }
        }

        // result_int = np.uint16(result)
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                ip.putPixel(x, y, (int)result[y][x]);
            }
        }

        return ip;
    }

    /* Invert does not take or return any parameters */
    @Override
    public void init(Map<String, String> params) {
        if (params.containsKey("flatfieldimage"))
            this.flatFieldImagePath = params.get("flatfieldimage");
        else
            throw new IllegalArgumentException("Flat field image required.");
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("flatfieldimage", this.flatFieldImagePath);
        return params;
    }
}
