/****************************************************************************
 *
 * Copyright (c) 2005-2012, Linagora
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.SchemaException;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractElementImpl;
import org.ow2.easywsdl.tooling.xsd2xml.XSD2XML;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.InterfaceType;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.petals.client.model.RequestMessageBean;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;

/**
 * @author Christophe HAMERLING - Linagora
 * @author Vincent Zurczak - Linagora
 */
public class Utils {

	/**
	 * Logs an information.
	 * @param msg a message (can be null)
	 * @param t a throwable (can be null)
	 * <p>
	 * The stack trace is logged with the FINEST level.
	 * </p>
	 *
	 * @param level the log level for the message
	 * @param logger the logger (not null)
	 */
	public static void log( String msg, Throwable t, Level level, Logger logger ) {

		if( logger != null ) {
			String realMsg = msg != null ? msg : t.getMessage() != null ? t.getMessage() : "An error occurred.";
			logger.log( level, realMsg );

			if( t != null && logger.isLoggable( Level.FINEST ))
				logger.log( Level.FINEST, Utils.extractStackTrace( t ));
		}
	}


    /**
     * Creates a string from a source.
     * @param source the source (not null)
     * @return a string
     * @throws Exception
     */
    public static String createString( Source source ) throws Exception {

		String ret = null;
		if(source instanceof StreamSource) {

		    ByteArrayOutputStream os = new ByteArrayOutputStream();
			InputStream is = ((StreamSource)source).getInputStream();
			copyStream( is, os );
			closeStreamQuietly( is );

			ret = os.toString();

		} else {
			ret = XMLHelper.createStringFromDOMDocument(((DOMSource)source).getNode());
		}

		return ret;
    }


	/**
     * Creates a source from a string representing a XML document.
     * @param msg the string that represents a XML document
     * @return a source
     * @throws Exception
     */
    public static Source createSource( String msg ) throws Exception {

        StreamSource source = new StreamSource();
        source.setInputStream( new ByteArrayInputStream( msg.getBytes( "UTF-8" )));
        return source;
    }


    /**
	 * Writes a document as a string.
	 * @param doc the document
	 * @param omitXmlDeclaration true to omit the XML declaration
	 * @return the written document, or null if the conversion failed
	 * @throws Exception
	 */
	public static String writeDocument( Document doc, boolean omitXmlDeclaration ) throws Exception {

		String result = null;
		DOMSource domSource = new DOMSource( doc );
		StringWriter writer = new StringWriter();
		StreamResult streamResult = new StreamResult( writer );

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
		transformer.setOutputProperty( OutputKeys.STANDALONE, "yes" );
		if( omitXmlDeclaration )
			transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );

		transformer.transform( domSource, streamResult );
		result = writer.toString();
		return result;
	}


    /**
     * Parses a WSDL description.
     * @param wsdlDocument a WSDL document
     * @return a WSDL description
     * @throws Exception in case of problem
     */
    public static Description parseWsdlDescription( Document wsdlDocument ) throws Exception {
        return WSDLFactory.newInstance().newWSDLReader().read( wsdlDocument );
    }


    /**
     * Generates a XML skeleton for an WSDL operation.
     * @param wsdlDescription the WSDL description (not null)
     * @param interfaceName the interface name (not null)
     * @param operationName the operation name (not null)
     * @param removeExtraDeclarations true to remove extra declarations (XSI type and XSI instance)
     * @param generateOptionalElements true to generate optional elements
     * @return a XML instance that matches the XML schema of the operation (can be null)
     * @throws URISyntaxException
     * @throws SchemaException
     */
    public static String generateXmlSkeleton(
            Description wsdlDescription,
            QName interfaceName,
            QName operationName,
            boolean generateOptionalElements )
    throws URISyntaxException, SchemaException {

        InterfaceType itfType = wsdlDescription.getInterface( interfaceName );
        Operation wsdlOperation = null;
        if( itfType != null )
            wsdlOperation = itfType.getOperation( operationName );

        String result = null;
        if( wsdlOperation != null
                && wsdlOperation.getInput() != null
                && wsdlOperation.getInput().getElement() != null ) {

            if( generateOptionalElements ) {
                Element elt = wsdlOperation.getInput().getElement();
                result = XSD2XML.newInstance().printXml( wsdlOperation.getInput().getElement(), XSD2XML.createDefaultMap( "" ), true, true );
                final org.jdom.Element eOut = XSD2XML.newInstance().generateElement(
                		elt,
                		XSD2XML.createDefaultMap( "" ),
                		((AbstractElementImpl) elt).getSchema().getElementFormDefault(),
                		1,
                		true,
                		true );

                result = XSD2XML.newInstance().printXml( eOut );

            } else {
                result = XSD2XML.newInstance().printXml( wsdlOperation.getInput().getElement(), "?" );
            }
        }

        return result;
    }


	/**
	 * Deletes files recursively.
	 * @param files the files to delete
	 * @throws IOException if a file could not be deleted
	 */
	public static void deleteFilesRecursively( File... files ) throws IOException {

		if( files == null )
			return;

		for( File file : files ) {
			if( file.exists()) {
				if( file.isDirectory())
					deleteFilesRecursively( file.listFiles());

				if( ! file.delete())
					throw new IOException( file.getAbsolutePath() + " could not be deleted." );
			}
		}
	}


	/**
	 * Copies the content from in into os.
	 * <p>
	 * Neither <i>in</i> nor <i>os</i> are closed by this method.<br />
	 * They must be explicitly closed after this method is called.
	 * </p>
	 *
	 * @param in
	 * @param os
	 * @throws IOException
	 */
	public static void copyStream( InputStream in, OutputStream os ) throws IOException {

		byte[] buf = new byte[ 1024 ];
		int len;
		while((len = in.read( buf )) > 0) {
			os.write( buf, 0, len );
		}
	}


	/**
	 * Copies the content from inputFile into outputFile.
	 *
	 * @param inputFile (must exist and be a file, not a directory)
	 * @param outputFile will be created if it does not exist
	 * @throws IOException
	 */
	public static void copyFile( File inputFile, File outputFile ) throws IOException {

		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream( inputFile );
			os = new FileOutputStream( outputFile );
			copyStream( is, os );

		} finally {
			try {
				if( is != null )
					is.close();

			} finally {
				if( os != null )
					os.close();
			}
		}
	}


	/**
	 * Loads a resource from the class loader and returns its content as a string.
	 * @param resourceLocation the resource location
	 * @return a string, never null
	 * @throws IOException
	 */
	public static String loadResource( String resourceLocation )
	throws IOException {

		String result = null;
		InputStream in = null;
		try {
			in = Utils.class.getResourceAsStream( resourceLocation );
			if( in != null ) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				copyStream( in, os );
				result = os.toString( "UTF-8" );
			}

		} finally {
			closeStreamQuietly( in );
		}

		return result != null ? result : "";
	}


	/**
     * Builds a document from a string.
     * @param text the text to parse as a XML document
     * @return the document or null if it could not be loaded
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Document buildDocument( String text )
    throws SAXException, IOException, ParserConfigurationException {

        DocumentBuilderFactory db = DocumentBuilderFactory.newInstance();
        db.setNamespaceAware( true );
        return db.newDocumentBuilder().parse( new ByteArrayInputStream( text.getBytes()));
    }


	/**
	 * Closes a stream quietly.
	 * @param stream the stream to close (not null)
	 */
	public static void closeStreamQuietly( InputStream stream ) {

		try {
			if( stream != null )
				stream.close();
		} catch( IOException e ) {
			// nothing
		}
	}


	/**
	 * Clears history files.
	 * @param olderThan the number of days above which files must be removed.
	 * <p>
	 * -1 to remove all the history files.
	 * </p>
	 */
	public static void clearHistory( int olderThan ) {

		File dir = PreferencesManager.INSTANCE.getHistoryDirectory();
		if( dir != null
				&& dir.exists()
				&& dir.isDirectory()) {

			long currentDate = new GregorianCalendar().getTimeInMillis();
			final long limit = olderThan == -1 ? 0 : currentDate - olderThan * 3600 * 24 * 1000L;
			File[] files = dir.listFiles( new FileFilter() {
				@Override
				public boolean accept( File pathname ) {
					return pathname.getName().toLowerCase().endsWith( ".txt" )
							&& pathname.lastModified() > limit;
				}
			});

			if( files != null ) {
				for( File f : files ) {
					if( ! f.delete())
						f.deleteOnExit();
				}
			}
		}
	}


	/**
	 * @param s a string (can be null)
	 * @return true if it is null or only made up of white spaces
	 */
	public static boolean isEmptyString( String s ) {
		return s == null || s.trim().length() == 0;
	}


	/**
	 * Saves a request.
	 * @param target a file (will be overwritten if it already exists)
	 * <p>
	 * If attachments do not have to be saved, this parameter represents the properties file for the request fields.
	 * If attachments do have t be saved, this parameter represents a directory into which the request and the attachments
	 * will be saved.
	 * </p>
	 *
	 * @param req an instance of {@link RequestMessageBean} (not null)
	 * @param withAttachments true to save attachments with the request, false otherwise
	 * @return the saved request, potentially modified (true if attachments were saved)
	 * @throws IOException
	 */
	public static RequestMessageBean saveRequest( File target, RequestMessageBean req, boolean withAttachments )
	throws IOException {

		// Modify the input element if attachments must be saved
		if( withAttachments ) {
			if( target.exists()) {
				File[] files = target.listFiles();
				if( files != null )
					deleteFilesRecursively( files );

			} else if( ! target.exists() && ! target.mkdir()) {
				throw new IOException( "Could not create " + target );
			}

			Set<File> attachments = req.getAttachments();
			if( attachments != null ) {
				Set<File> newAttachments = new LinkedHashSet<File> ();
				for( File f : attachments ) {

					// FIXME: check there are not files with a same name
					File targetAtt = new File( target, f.getName());
					newAttachments.add( targetAtt );
					copyFile( f, targetAtt );
				}

				req.setAttachments( newAttachments );
			}
		}

		// Serialize the element
		Properties props = RequestMessageBeanUtils.write( req );

		// Write it
		FileOutputStream fos = null;
		File targetFile = withAttachments ? new File( target, target.getName() + ".txt" ) : target;
		if( ! targetFile.getParentFile().exists()
				&& ! targetFile.getParentFile().mkdirs())
			throw new IOException( "Could not create the history directory: " + targetFile.getParentFile());

		fos = new FileOutputStream( targetFile );
		try {
			props.store( fos, "Saved from the Petals Client Component" );

		} finally {
			if( fos != null )
				fos.close();
		}

		return req;
	}


	/**
	 * Gets a new file to save a request in the history.
	 * @param req the request to save (file name)
	 * @return a file (the file should not already exist, but this is not guaranteed)
	 */
	public static File getNewHistoryFile( RequestMessageBean req ) {

		String name = "i=" + req.getInterfaceName().getLocalPart();
		if( req.getServiceName() != null
				&& ! Utils.isEmptyString( req.getServiceName().getLocalPart()))
			name += "==s=" + req.getServiceName().getLocalPart();

		if( ! Utils.isEmptyString( req.getEndpointName()))
			name += "==e=" + req.getEndpointName();

		name += "--" + new SimpleDateFormat( "yyyy-MM-dd--HH-mm-ss'.txt'" ).format( new GregorianCalendar().getTime());
		return new File( PreferencesManager.INSTANCE.getHistoryDirectory(), name );
	}


	/**
     * Returns the stack trace as a string.
     * @param throwable a throwable (not null)
     * @return a non-null string
     */
    public static final String extractStackTrace( Throwable throwable ) {

        final StringWriter sw = new StringWriter();
        throwable.printStackTrace( new PrintWriter( sw ));
        sw.flush();
        return sw.toString();
    }
}
