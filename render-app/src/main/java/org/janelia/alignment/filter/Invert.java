package org.janelia.alignment.filter;

import java.util.Collections;
import java.util.Map;

import ij.process.ImageProcessor;

/**
 * A very simple filter class to invert the contents of an image.
 *
 * @author Eric Perlman
 */
public class Invert implements Filter, InputFilter {
    @Override
    public ImageProcessor process(ImageProcessor ip, final double scale) {
        ip.invert();
        return ip;
    }

    @Override
    public void init(Map<String, String> params) {
    }

    @Override
    public Map<String, String> getParams() {
        return Collections.emptyMap();
    }
}
