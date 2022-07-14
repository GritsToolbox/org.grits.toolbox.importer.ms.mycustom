package org.grits.toolbox.entry.ms.mycustom.property.io;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.grits.toolbox.core.datamodel.UnsupportedTypeException;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.datamodel.io.PropertyReader;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.entry.ms.mycustom.property.datamodel.MyCustomMassSpecProperty;
import org.grits.toolbox.entry.ms.property.io.MassSpecPropertyReader;
import org.jdom.Element;

/**
 * Reader for sample entry. Should check for empty values
 * @author Brent Weatherly
 *
 */
public class MyMassSpecPropertyReader extends MassSpecPropertyReader
{
	private static final Logger logger = Logger.getLogger(MyMassSpecPropertyReader.class);

	@Override
	public Property read(Element propertyElement) throws IOException, UnsupportedVersionException
	{
		MyCustomMassSpecProperty property = new MyCustomMassSpecProperty();

		PropertyReader.addGenericInfo(propertyElement, property);

		if(property.getVersion().equals("1.0")) {
			try {
				MyMassSpecReaderVersion1_0.read(propertyElement, property);
				updateMassSpecMetaData(property, propertyElement);
				adjustFilePathsForLockFile (propertyElement, property);
				PropertyReader.UPDATE_PROJECT_XML = true;
			} catch (UnsupportedTypeException e) {
				throw new IOException(e.getMessage(), e);
			}
		}
		else 
			throw new UnsupportedVersionException("This version is currently not supported.", property.getVersion());


		return property;
	}


}
