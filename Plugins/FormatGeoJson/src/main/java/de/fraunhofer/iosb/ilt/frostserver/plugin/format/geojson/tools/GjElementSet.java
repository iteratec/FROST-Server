/*
 * Copyright (C) 2020 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.frostserver.plugin.format.geojson.tools;

import de.fraunhofer.iosb.ilt.frostserver.model.EntityType;
import de.fraunhofer.iosb.ilt.frostserver.model.core.Entity;
import de.fraunhofer.iosb.ilt.frostserver.model.core.EntitySet;
import de.fraunhofer.iosb.ilt.frostserver.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostserver.property.NavigationProperty;
import de.fraunhofer.iosb.ilt.frostserver.property.Property;
import de.fraunhofer.iosb.ilt.frostserver.query.Expand;
import de.fraunhofer.iosb.ilt.frostserver.query.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author scf
 */
public class GjElementSet {

    /**
     * The name of this EntitySet.
     */
    private final String name;

    /**
     * The elements to output for each Entity in the set.
     */
    private final List<GjEntityEntry> elements = new ArrayList<>();

    /**
     * Should the collection be flushed after each entity.
     */
    private final boolean flush;

    /**
     * The serviceRootUrl for the current request.
     */
    private final String serviceRootUrl;

    public GjElementSet(String serviceRootUrl, String name, boolean flush) {
        this.serviceRootUrl = serviceRootUrl;
        this.name = name;
        this.flush = flush;
    }

    public void initFrom(EntityType type, Query query) {
        if (query == null || query.getSelect().isEmpty()) {
            initFrom(type.getPropertySet(), query);
        } else {
            initFrom(query.getSelect(), query);
        }
    }

    public void initFrom(Set<Property> properties, Query query) {
        initProperties(properties);

        if (query == null) {
            return;
        }

        for (Expand expand : query.getExpand()) {
            NavigationProperty path = expand.getPath();
            initFrom(path, expand.getSubQuery());
        }
    }

    private void initProperties(Set<Property> properties) {
        for (Property property : properties) {
            if (property == EntityPropertyMain.SELFLINK) {
                elements.add(new GjSelfLinkProperty(serviceRootUrl, EntityPropertyMain.SELFLINK.entitiyName));
            }
            if (property == EntityPropertyMain.UNITOFMEASUREMENT) {
                elements.add(new GjUnitOfMeasurementProperty(EntityPropertyMain.UNITOFMEASUREMENT.entitiyName));
            } else if (property instanceof EntityPropertyMain) {
                elements.add(new GjEntityProperty(((EntityPropertyMain) property).entitiyName, property));
            }
        }
    }

    public void initFrom(NavigationProperty property, Query query) {
        GjEntityExpand element = new GjEntityExpand(
                serviceRootUrl,
                property.getName() + "/",
                property,
                query);
        elements.add(element);
    }

    public void writeData(GjRowCollector collector, Object obj, String namePrefix) {
        if (obj instanceof Entity) {
            writeData(collector, (Entity) obj, namePrefix + name);
        } else if (obj instanceof EntitySet) {
            writeData(collector, (EntitySet) obj, namePrefix + name);
        }
    }

    public void writeData(GjRowCollector collector, Entity<?> entity, String namePrefix) {
        if (entity == null) {
            return;
        }
        collectElements(collector, entity, namePrefix);
        if (flush) {
            collector.flush();
        }
    }

    public void writeData(GjRowCollector collector, EntitySet<?> entitySet, String namePrefix) {
        if (entitySet == null) {
            return;
        }
        List<? extends Entity> list = entitySet.asList();
        int idx = 0;
        for (Entity entity : list) {
            String localName = flush ? namePrefix : namePrefix + idx + "/";
            collectElements(collector, entity, localName);
            if (flush) {
                collector.flush();
            }
            idx++;
        }
    }

    private void collectElements(GjRowCollector collector, Entity<?> entity, String namePrefix) {
        for (GjEntityEntry element : elements) {
            element.writeData(collector, entity, namePrefix);
        }
    }

}
