package org.janelia.alignment.spec;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.janelia.alignment.filter.InputFilter;
import org.janelia.alignment.filter.InputFilterList;

import java.util.*;

/**
 * Modified version of {@link TransformSpec}.
 *
 * NOTE: Annotations on the {@link FilterSpec} implementation handle
 * polymorphic deserialization for this class.
 */
public class ListFilterSpec extends FilterSpec {

    public static final String TYPE = "list";

    private final List<FilterSpec> specList;

    public ListFilterSpec() {
        this(null, null);
    }

    public ListFilterSpec(final String id,
                          final FilterSpecMetaData metaData) {
        super(id, metaData);
        this.specList = new ArrayList<>();
    }

    public FilterSpec getSpec(final int index) {
        return specList.get(index);
    }

    public FilterSpec getLastSpec() {
        final FilterSpec lastSpec;
        if ((specList.size() > 0)) {
            lastSpec = specList.get(specList.size() - 1);
        } else {
            lastSpec = null;
        }
        return lastSpec;
    }

    public void addSpec(final FilterSpec spec) {
        specList.add(spec);
    }

    public void removeLastSpec() {
        if (specList.size() > 0) {
            specList.remove(specList.size() - 1);
        }
    }

    public void addAllSpecs(final List<FilterSpec> specs) {
        this.specList.addAll(specs);
    }

    public int size() {
        return specList.size();
    }

    public void removeNullSpecs() {
        FilterSpec spec;
        for (final Iterator<FilterSpec> i = specList.iterator(); i.hasNext();) {
            spec = i.next();
            if (spec == null) {
                i.remove();
            }
        }
    }

    @Override
    public boolean isFullyResolved()
            throws IllegalStateException {
        boolean allSpecsResolved = true;
        for (final FilterSpec spec : specList) {
            if (spec == null) {
                throw new IllegalStateException("A null spec is part of the filter spec list with id '" + getId() +
                                                "'.  Check for an extraneous comma at the end of the list.");
            }
            if (! spec.isFullyResolved()) {
                allSpecsResolved = false;
                break;
            }
        }
        return allSpecsResolved;
    }

    @Override
    public void addUnresolvedIds(final Set<String> unresolvedIds) {
        for (final FilterSpec spec : specList) {
            spec.addUnresolvedIds(unresolvedIds);
        }
    }

    @Override
    public void resolveReferences(final Map<String, FilterSpec> idToSpecMap) {
        for (final FilterSpec spec : specList) {
            spec.resolveReferences(idToSpecMap);
        }
    }

    @Override
    public void flatten(final ListFilterSpec flattenedList) throws IllegalStateException {
        for (final FilterSpec spec : specList) {
            spec.flatten(flattenedList);
        }
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore
    public InputFilterList<InputFilter> getNewInstanceAsList()
            throws IllegalArgumentException {
        return (InputFilterList<InputFilter>) super.getNewInstance();
    }

    @Override
    protected InputFilter buildInstance()
            throws IllegalArgumentException {
        final InputFilterList<InputFilter> ctList = new InputFilterList<>();
        for (final FilterSpec spec : specList) {
            ctList.add(spec.buildInstance());
        }
        return ctList;
    }

}
