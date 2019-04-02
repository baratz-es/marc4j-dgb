/**
 * Copyright (C) 2019 DIGIBÍS S.L.
 *
 * This file is part of MARC4J
 *
 * MARC4J is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * MARC4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with MARC4J; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.marc4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Utility class to access to resouces files on the classpath
 */
public class ResourcesUtil
{
    private static final Logger log = Logger.getLogger(ResourcesUtil.class);

    private static final String RESOURCE_PATH_SEPARATOR = "/";

    private ResourcesUtil()
    {
    }

    /**
     * Devuelve la URL del recurso dado. A partir de esa URL se puede abrir
     * un InputStream ó obtener al File que lleva debajo, si es que lo hay
     * (esto permitiría, por ejemplo, hacer un chequeo de ficheros modificados
     * a partir de su fecha).
     * 
     * @param resourceName Nombre "classpath" del recurso, separado por / o por \\
     * @return URL del recurso
     */
    public static URL getURL(String resourceName)
    {
        String normalizedResourceName = ResourcesUtil.normalizeResourceName(resourceName);

        // Primero se prueba con el ClassLoader de la clase
        URL url = ResourcesUtil.class.getResource(normalizedResourceName);

        // Si no funciona empezando con / se prueba sin la / al principio
        if (url == null) {
            url = ResourcesUtil.class.getResource(normalizedResourceName.substring(1));
        }

        // Si no se encuentra, se vuelve a probar, pero con el ClassLoader de contexto del thread
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(normalizedResourceName);
        }
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(normalizedResourceName.substring(1));
        }

        return url;
    }

    /**
     * Normaliza el nombre de recurso, limpiando separadores "\"
     * y poniendo un / al principio si no lo había.
     */
    public static String normalizeResourceName(String resourceName)
    {
        String res = resourceName.replaceAll("\\\\", RESOURCE_PATH_SEPARATOR);
        if (!res.startsWith(RESOURCE_PATH_SEPARATOR)) {
            res = RESOURCE_PATH_SEPARATOR + res;
        }
        return res;
    }

    /**
     * Devuelve el stream del recurso dado.
     * 
     * @param resourceName Nombre "classpath" del recurso, separado por / o por \\
     * @return InputStream
     * @throws IOException
     */
    public static InputStream getStream(String resourceName)
        throws IOException
    {
        log.debug("Cargando recurso: " + resourceName);

        // Si el nombre del recurso contiene la cadena ".." está intentando acceder
        // a un path relativo y eso no está permitido para acceder al classpath
        if (StringUtils.contains(resourceName, "..")) {
            return null;
        }

        URL url = ResourcesUtil.getURL(resourceName);
        if (url == null) {
            log.debug("Recurso de classpath no encontrado: " + resourceName);
            return null;

        }
        return url.openStream();
    }
}
