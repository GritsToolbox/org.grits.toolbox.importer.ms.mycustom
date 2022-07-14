package org.grits.toolbox.entry.ms.mycustom.property.io;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.grits.toolbox.core.datamodel.UnsupportedTypeException;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.entry.ms.property.MassSpecProperty;
import org.grits.toolbox.entry.ms.property.io.MassSpecReaderVersion1_3;
import org.jdom.Element;

/**
 * 
 * @author Brent Weatherly
 *
 */
public class MyMassSpecReaderVersion1_0 {
	private static final Logger logger = Logger.getLogger(MyMassSpecReaderVersion1_0.class);

	public static Property read(Element propertyElement, MassSpecProperty msProperty) throws IOException, UnsupportedVersionException, UnsupportedTypeException {

		return MassSpecReaderVersion1_3.read(propertyElement, msProperty);
	}

}
