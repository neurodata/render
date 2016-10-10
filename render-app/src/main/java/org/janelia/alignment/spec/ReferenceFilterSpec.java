package org.janelia.alignment.spec;

import org.janelia.alignment.filter.InputFilter;

import java.util.Map;
import java.util.Set;

/**
 * Modified version of {@link ReferenceTransformSpec}.
 *
 * A reference to another {@link FilterSpec} instance.
 *
 * References do not have their own id, they simply use the id of the spec they reference.
 *
 * @author Eric Trautman
 */
public class ReferenceFilterSpec extends FilterSpec {

    public static final String TYPE = "ref";

    private final String refId;

    /**
     * The effective id being referenced.
     * This starts out the same as the original reference id, but can change if
     * resolution of the original (or subsequent) id locates another reference.
     */
    private transient String effectiveRefId;
    private transient FilterSpec resolvedInstance;

    // no-arg constructor needed for JSON deserialization
    @SuppressWarnings("unused")
    private ReferenceFilterSpec() {
        super(null, null);
        this.refId = null;
        this.effectiveRefId = null;
    }

    /**
     * @param  refId  the id this specification references.
     */
    public ReferenceFilterSpec(final String refId) {
        super(null, null);
        this.refId = refId;
        this.effectiveRefId = refId;
    }

    public String getRefId() {
        return refId;
    }

    public String getEffectiveRefId() {
        if (effectiveRefId == null) {
            effectiveRefId = refId;
        }
        return effectiveRefId;
    }

    @Override
    public boolean isFullyResolved()
            throws IllegalStateException {
        return ((resolvedInstance != null) && (resolvedInstance.isFullyResolved()));
    }

    @Override
    public void addUnresolvedIds(final Set<String> unresolvedIds) {
        if (resolvedInstance == null) {
            unresolvedIds.add(getEffectiveRefId());
        } else {
            resolvedInstance.addUnresolvedIds(unresolvedIds);
        }
    }

    @Override
    public void resolveReferences(final Map<String, FilterSpec> idToSpecMap) {
        if (resolvedInstance == null) {
            final FilterSpec spec = idToSpecMap.get(getEffectiveRefId());
            if (spec != null) {
                if (spec instanceof ReferenceFilterSpec) {
                    effectiveRefId = ((ReferenceFilterSpec) spec).getEffectiveRefId();
                } else {
                    resolvedInstance = spec;
                }
            }
        }
    }

    @Override
    public void flatten(final ListFilterSpec flattenedList) throws IllegalStateException {
        if (! isFullyResolved()) {
            throw new IllegalStateException("cannot flatten unresolved reference to " + getEffectiveRefId());
        }
        resolvedInstance.flatten(flattenedList);
    }

    @Override
    protected InputFilter buildInstance()
            throws IllegalArgumentException {
        if (resolvedInstance == null) {
            throw new IllegalArgumentException("spec reference to id '" + refId + "' has not been resolved");
        }
        return resolvedInstance.buildInstance();
    }

}
