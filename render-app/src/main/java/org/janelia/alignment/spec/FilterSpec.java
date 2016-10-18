package org.janelia.alignment.spec;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.janelia.alignment.filter.InputFilter;
import org.janelia.alignment.filter.InputFilterList;
import org.janelia.alignment.json.JsonUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.*;

/**
 * Abstract base for all filter specifications.  Modification of {@link TransformSpec}.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = LeafFilterSpec.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LeafFilterSpec.class, name = LeafFilterSpec.TYPE),
        @JsonSubTypes.Type(value = ListFilterSpec.class, name = ListFilterSpec.TYPE),
        @JsonSubTypes.Type(value = ReferenceFilterSpec.class, name = ReferenceFilterSpec.TYPE) })
public abstract class FilterSpec implements Serializable {

    private final String id;
    private final FilterSpecMetaData metaData;

    protected FilterSpec(final String id,
                         final FilterSpecMetaData metaData) {
        this.id = id;
        this.metaData = metaData;
    }

    public boolean hasId() {
        return (id != null);
    }

    public String getId() {
        return id;
    }

    public FilterSpecMetaData getMetaData() {
        return metaData;
    }

    /**
     * @throws IllegalArgumentException
     *   if a {@link InputFilter} instance cannot be created based upon this specification.
     */
    public void validate()
            throws IllegalArgumentException {
        if (! isFullyResolved()) {
            final Set<String> unresolvedIdList = new HashSet<>();
            addUnresolvedIds(unresolvedIdList);
            throw new IllegalArgumentException("spec '" + id +
                                               "' has the following unresolved references: " + unresolvedIdList);
        }
        buildInstance(); // building instance will force everything to be validated
    }

    /**
     * @return a new (distinct and thread safe) {@link InputFilter} instance built from this specification.
     *
     * @throws IllegalArgumentException
     *   if the instance cannot be created.
     */
    public InputFilter getNewInstance()
            throws IllegalArgumentException {
        return buildInstance();
    }

    /**
     * @return true if all spec references within this spec have been resolved; otherwise false.
     *
     * @throws IllegalStateException
     *   if the spec's current state prevents checking resolution.
     */
    @JsonIgnore
    public abstract boolean isFullyResolved() throws IllegalStateException;

    /**
     * Add the ids for any unresolved spec references to the specified set.
     *
     * @param  unresolvedIds  set to which unresolved ids will be added.
     */
    public abstract void addUnresolvedIds(Set<String> unresolvedIds);

    /**
     * @return the set of unresolved spec references within this spec.
     */
    public Set<String> getUnresolvedIds() {
        final Set<String> unresolvedIds = new HashSet<>();
        addUnresolvedIds(unresolvedIds);
        return unresolvedIds;
    }

    /**
     * Uses the specified map to resolve any spec references within this spec.
     *
     * @param  idToSpecMap  map of filter ids to resolved specs.
     */
    public abstract void resolveReferences(Map<String, FilterSpec> idToSpecMap);

    /**
     * Adds a flattened (fully resolved) version of this spec to the specified list.
     *
     * @param  flattenedList  list to which flattened specs should be appended.
     *
     * @throws IllegalStateException
     *   if any references have not been resolved.
     */
    public abstract void flatten(ListFilterSpec flattenedList) throws IllegalStateException;


    public String toJson() {
        return JSON_HELPER.toJson(this);
    }

    public static FilterSpec fromJson(final String json) {
        return JSON_HELPER.fromJson(json);
    }

    public static List<FilterSpec> fromJsonArray(final String json) {
        // TODO: verify using Arrays.asList optimization is actually faster
        //       http://stackoverflow.com/questions/6349421/how-to-use-jackson-to-deserialise-an-array-of-objects
        // return JSON_HELPER.fromJsonArray(json);
        try {
            return Arrays.asList(JsonUtils.MAPPER.readValue(json, FilterSpec[].class));
        } catch (final IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<FilterSpec> fromJsonArray(final Reader json)
            throws IOException {
        // TODO: verify using Arrays.asList optimization is actually faster
        // return JSON_HELPER.fromJsonArray(json);
        try {
            return Arrays.asList(JsonUtils.MAPPER.readValue(json, FilterSpec[].class));
        } catch (final IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * @return the filter instance built from this spec.
     *
     * @throws IllegalArgumentException
     *   if the instance cannot be created.
     */
    protected abstract InputFilter buildInstance()
            throws IllegalArgumentException;

//    private static final TypeReference<List<TransformSpec>> LIST_TYPE = new TypeReference<List<TransformSpec>>(){};

    /**
     * Create a FilterSpec from a {@link InputFilter}.  The
     * {@link InputFilter} has to be a {@link InputFilterList}List,
     * method returns null.
     */
    static public FilterSpec create(final InputFilter filter) {

        if (InputFilterList.class.isInstance(filter)) {
            final ListFilterSpec listSpec = new ListFilterSpec(UUID.randomUUID().toString(), null);
            @SuppressWarnings({ "rawtypes", "unchecked" })
            final List<InputFilter> filters = ((InputFilterList)filter).getList(null);
            for (final InputFilter t : filters)
                listSpec.addSpec(create(t));
            return listSpec;
        } else if (InputFilter.class.isInstance(filter)) {
            final InputFilter f = (InputFilter)filter;
            return new LeafFilterSpec(UUID.randomUUID().toString(), null, f.getClass().getCanonicalName(), f.getParams());
        } else return null;

    }

    private static final JsonUtils.Helper<FilterSpec> JSON_HELPER =
            new JsonUtils.Helper<>(FilterSpec.class);

}