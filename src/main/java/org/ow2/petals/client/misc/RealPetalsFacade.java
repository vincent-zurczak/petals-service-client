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

package org.ow2.petals.client.misc;

import java.util.List;

import javax.xml.namespace.QName;

import org.ow2.petals.client.PetalsFacade;
import org.ow2.petals.client.model.RequestMessageBean;
import org.ow2.petals.client.model.ServiceEndpoint;
import org.w3c.dom.Document;

/**
 * An implementation that interacts with a real Petals server.
 * @author Vincent Zurczak - Linagora
 */
public class RealPetalsFacade extends PetalsFacade {

	@Override
	public void send(RequestMessageBean request) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Document findWsdlDescriptionAsDocument(ServiceEndpoint se) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceEndpoint resolveServiceEndpoint(QName itfName, QName srvName, String edptName)
	throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ServiceEndpoint> getAllServiceEndpoints() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
