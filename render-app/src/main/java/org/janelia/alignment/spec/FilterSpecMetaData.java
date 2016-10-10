package org.janelia.alignment.spec;

import java.io.Serializable;

/**
 * A modified copy of {@link TransformSpecMetaData}.
 *
 * Any information about a {@link FilterSpec} that is NOT directly needed for rendering
 * (not directly needed for implementation of the transformation).
 *
 * It's not clear whether this will be needed, but the basic infrastructure is in place
 * to handle it just in case.  The initially specified "group" attribute is just a place holder.
 * More attributes can be added later if needed.
 */
public class FilterSpecMetaData implements Serializable {

    private String group;

    public FilterSpecMetaData() {
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(final String group) {
        this.group = group;
    }

}
