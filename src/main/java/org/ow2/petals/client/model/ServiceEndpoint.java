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

package org.ow2.petals.client.model;

import javax.xml.namespace.QName;

/**
 * @author Vincent Zurczak - Linagora
 */
public class ServiceEndpoint {

	private QName interfaceName, serviceName;
	private String endpointName;


	/**
	 * Constructor.
	 */
	public ServiceEndpoint() {
		// nothing
	}


	/**
	 * Constructor.
	 * @param interfaceName
	 * @param serviceName
	 * @param endpointName
	 */
	public ServiceEndpoint( QName interfaceName, QName serviceName, String endpointName ) {
		this.interfaceName = interfaceName;
		this.serviceName = serviceName;
		this.endpointName = endpointName;
	}

	/**
	 * @return the interfaceName
	 */
	public QName getInterfaceName() {
		return this.interfaceName;
	}

	/**
	 * @param interfaceName the interfaceName to set
	 */
	public void setInterfaceName(QName interfaceName) {
		this.interfaceName = interfaceName;
	}

	/**
	 * @return the serviceName
	 */
	public QName getServiceName() {
		return this.serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(QName serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the endpointName
	 */
	public String getEndpointName() {
		return this.endpointName;
	}

	/**
	 * @param endpointName the endpointName to set
	 */
	public void setEndpointName(String endpointName) {
		this.endpointName = endpointName;
	}
}
