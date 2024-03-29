/****************************************************************************
 *
 * Copyright (c) 2012, Linagora
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *****************************************************************************/

package org.ow2.petals.client.swt.syntaxhighlighting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.ow2.petals.client.misc.PreferencesManager;
import org.ow2.petals.client.swt.syntaxhighlighting.XmlRegion.XmlRegionType;

/**
 * A class that manages color resources.
 * @author Vincent Zurczak - Linagora
 */
public class ColorCacheManager {

	private final Map<XmlRegionType,Color> regionTypeTocolor;
	private final Color orangeColor;


	/**
	 * Constructor.
	 */
	public ColorCacheManager() {

		this.orangeColor = new Color( Display.getCurrent(), 222, 159, 38 );
		this.regionTypeTocolor = new ConcurrentHashMap<XmlRegion.XmlRegionType,Color> ();
		for( XmlRegionType xr : XmlRegionType.values())
			updateColor( xr );
	}


	/**
	 * Updates a color in the cache from the preferences.
	 */
	public void updateColor( XmlRegionType xr ) {

		RGB rgb = PreferencesManager.INSTANCE.getXmlRegionColor( xr );
		if( rgb != null ) {
			Color newColor = new Color( Display.getDefault(), rgb );
			Color previousColor = this.regionTypeTocolor.put( xr, newColor );
			if( previousColor != null )
				previousColor.dispose();
		}
	}


	/**
	 * Disposes all the resources.
	 */
	public void dispose() {

		for( Color c : this.regionTypeTocolor.values()) {
			if( ! c.isDisposed())
				c.dispose();
		}

		this.regionTypeTocolor.clear();
		if( this.orangeColor != null && ! this.orangeColor.isDisposed())
			this.orangeColor.dispose();
	}


	/**
	 * @param xrt a XML region type
	 * @return a color (can be null)
	 */
	public Color getColor( XmlRegionType xrt ) {
		return this.regionTypeTocolor.get( xrt );
	}


	/**
	 * @return the orangeColor
	 */
	public Color getOrangeColor() {
		return this.orangeColor;
	}
}
