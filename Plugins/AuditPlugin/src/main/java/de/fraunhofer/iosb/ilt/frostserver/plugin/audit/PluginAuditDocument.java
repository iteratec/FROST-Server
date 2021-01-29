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
package de.fraunhofer.iosb.ilt.frostserver.plugin.audit;

import de.fraunhofer.iosb.ilt.frostserver.formatter.ResultFormatter;
import de.fraunhofer.iosb.ilt.frostserver.service.*;
import de.fraunhofer.iosb.ilt.frostserver.settings.ConfigDefaults;
import de.fraunhofer.iosb.ilt.frostserver.settings.CoreSettings;
import de.fraunhofer.iosb.ilt.frostserver.settings.Settings;
import de.fraunhofer.iosb.ilt.frostserver.settings.annotation.DefaultValueBoolean;
import de.fraunhofer.iosb.ilt.frostserver.util.HttpMethod;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * MAYBE useful for Audit
 * - access to request and maybe content (was not tested yet)
 * - missing access to Header (and therefore the user)!
 */
public class PluginAuditDocument implements PluginRootDocument, ConfigDefaults {

    @DefaultValueBoolean(true)
    public static final String TAG_ENABLE = "audit.enable";

    @Override // from Plugin
    public void init(CoreSettings settings) {
        Settings pluginSettings = settings.getPluginSettings();
        boolean enabled = pluginSettings.getBoolean(TAG_ENABLE, getClass());
        if (enabled) {
            settings.getPluginManager().registerPlugin(this);
        }
    }

    /**
     * Add Audit code here
     *
     * ServiceRequest has access to:
     * - requestType
     * - content
     * - contentType
     * - parameterMap
     * - urlPath
     * - urlQuery
     *
     * (@see #Service.executeGetCapabilities())
     */
    @Override // from PluginRootDocument
    public void modifyServiceDocument(ServiceRequest request, Map<String, Object> result) {
    }
}
