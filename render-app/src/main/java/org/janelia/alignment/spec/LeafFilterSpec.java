/**
 * License: GPL
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.janelia.alignment.spec;

import org.janelia.alignment.filter.InputFilter;

import java.util.Map;
import java.util.Set;

/**
 * Modified version of {@link LeafTransformSpec}.
 *
 * Specifies a {@link org.janelia.alignment.filter.InputFilter} implementation
 * along with it's initialization properties.
 */
public class LeafFilterSpec extends FilterSpec {

    public static final String TYPE = "leaf";

    private final String className;
    private final Map<String, String> params;

    private transient Class clazz;

    // no-arg constructor needed for JSON deserialization
    @SuppressWarnings("unused")
    private LeafFilterSpec() {
        super(null, null);
        this.className = null;
        this.params = null;
    }

    /**
     * "Legacy" constructor that supports simple specs without ids or metadata.
     *
     * @param  className   name of filter implementation (java) class.
     * @param  params      parameters with which filter implementation should be initialized.
     */
    public LeafFilterSpec(final String className,
                          final Map<String, String> params) {
        super(null, null);
        this.className = className;
        this.params = params;
    }

    /**
     * Full constructor.
     *
     * @param  id          identifier for this specification.
     * @param  metaData    meta data about the specification.
     * @param  className   name of filter implementation (java) class.
     * @param  params      parameters with which filter implementation should be initialized.
     */
    public LeafFilterSpec(final String id,
                          final FilterSpecMetaData metaData,
                          final String className,
                          final Map<String, String> params) {
        super(id, metaData);
        this.className = className;
        this.params = params;
    }

    public String getClassName() {
        return className;
    }

    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public boolean isFullyResolved() {
        return true;
    }

    @Override
    public void addUnresolvedIds(final Set<String> unresolvedIds) {
        // nothing to do
    }

    @Override
    public void resolveReferences(final Map<String, FilterSpec> idToSpecMap) {
        // nothing to do
    }

    @Override
    public void flatten(final ListFilterSpec flattenedList) throws IllegalStateException {
        flattenedList.addSpec(this);
    }

    protected InputFilter buildInstance()
            throws IllegalArgumentException {

        final InputFilter ct = newInstance();
        if (params == null) {
            throw new IllegalArgumentException("no params defined for leaf filter spec with id '" +
                                               getId() + "'");
        }
        ct.init(params);
        return ct;
    }

    private Class getClazz() throws IllegalArgumentException {
        if (clazz == null) {
            if (className == null) {
                throw new IllegalArgumentException("no className defined for leaf filter spec with id '" +
                                                   getId() + "'");
            }
            try {
                clazz = Class.forName(className);
            } catch (final ClassNotFoundException e) {
                throw new IllegalArgumentException("filter class '" + className + "' cannot be found", e);
            }
        }
        return clazz;
    }

    private InputFilter newInstance()
            throws IllegalArgumentException {

        final Class clazz = getClazz();
        final Object instance;
        try {
            instance = clazz.newInstance();
        } catch (final Exception e) {
            throw new IllegalArgumentException("failed to create instance of filter class '" + className + "'", e);
        }

        final InputFilter filter;
        if (instance instanceof InputFilter) {
            filter = (InputFilter) instance;
        } else {
            throw new IllegalArgumentException("Filter '" + className + "' does not implement the '" +
                    InputFilter.class + "' interface");
        }

        return filter;
    }

}
