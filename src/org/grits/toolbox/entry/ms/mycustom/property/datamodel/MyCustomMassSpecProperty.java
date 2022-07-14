package org.grits.toolbox.entry.ms.mycustom.property.datamodel;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.io.PropertyWriter;
import org.grits.toolbox.core.datamodel.property.ProjectProperty;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.core.datamodel.property.PropertyDataFile;
import org.grits.toolbox.core.datamodel.util.DataModelSearch;
import org.grits.toolbox.core.utilShare.DeleteUtils;
import org.grits.toolbox.core.utilShare.XMLUtils;
import org.grits.toolbox.entry.ms.mycustom.property.io.MyMassSpecPropertyWriter;
import org.grits.toolbox.entry.ms.property.MassSpecProperty;
import org.grits.toolbox.entry.ms.property.datamodel.MSPropertyDataFile;
import org.grits.toolbox.entry.ms.property.datamodel.MassSpecMetaData;

/**
 * A Property object specific for MS Entry
 * 
 * @author D Brent Weatherly (dbrentw@uga.edu)
 *
 */
public class MyCustomMassSpecProperty extends MassSpecProperty {
	public static final String CURRENT_VERSION = "1.0";
	private static final Logger logger = Logger.getLogger(MyCustomMassSpecProperty.class);
	public static final String TYPE = "org.grits.toolbox.property.ms.mycustom";
	protected static MyMassSpecPropertyWriter writer = new MyMassSpecPropertyWriter();

	@Inject
	public MyCustomMassSpecProperty() {
		super();
	//	if( ! MassSpecProperty.msMetaDataClasses.contains(MyMassSpecMetaData.class) ) {
	//		MassSpecProperty.msMetaDataClasses.add(MyMassSpecMetaData.class);
	//	}
	}

	@Override
	public MSPropertyDataFile getUpdatePropertyDataFile( MSPropertyDataFile msPDF ) {
		MSPropertyDataFile clonePdf = null;
		if( !( msPDF instanceof MyMSPropertyDataFile) ) {
			clonePdf = new MyMSPropertyDataFile();
			msPDF.clone(clonePdf);
			if ( msPDF.getChildren() != null) {
				clonePdf.getChildren().clear();
				for (MSPropertyDataFile child: msPDF.getChildren()) {
					MSPropertyDataFile cloneChildPdf = new MyMSPropertyDataFile();
					child.clone(cloneChildPdf);
					if( ! clonePdf.getChildren().contains(cloneChildPdf) ) {
						clonePdf.getChildren().add(cloneChildPdf);
					}

				}
			}
		}
		if( clonePdf != null ) {
			return clonePdf;
		}
		return msPDF;
	}
	
	@Override
	public void updateMSPropertyDataFiles( MassSpecMetaData metaData ) {
		boolean bChanged = false;
		List<MSPropertyDataFile> fileList = new ArrayList<>();
		for( MSPropertyDataFile msPDF : metaData.getFileList() ) {
			if( !( msPDF instanceof MyMSPropertyDataFile) ) {
				MSPropertyDataFile clonePdf = getUpdatePropertyDataFile(msPDF);
				fileList.add(clonePdf);
				bChanged = true;				
			} else {
				fileList.add(msPDF);
			}
		}
		if( bChanged ) {
			metaData.setFileList(fileList);
		}
	}

	@Override
	public List<PropertyDataFile> getDataFiles() {
		// TODO Auto-generated method stub
		return super.getDataFiles();
	}

	@Override
	public MassSpecMetaData unmarshallSettingsFile( String sFileName ) {
		MassSpecMetaData metaData = null;
		try {
			metaData = (MassSpecMetaData) XMLUtils.unmarshalObjectXML(sFileName, MassSpecProperty.msMetaDataClasses);
			if( metaData.getFileList() != null ) {
				updateMSPropertyDataFiles(metaData);
			}

		} catch (Exception e ) {
			logger.error(e.getMessage(), e);
		}
		return metaData;
	}

	@Override
	public void marshallSettingsFile( String sFileName, MassSpecMetaData metaData  ) {
		try {
			String xmlString = XMLUtils.marshalObjectXML(metaData, MassSpecProperty.msMetaDataClasses);
			//write the serialized data to the folder
			FileWriter fileWriter = new FileWriter(sFileName);
			fileWriter.write(xmlString);
			fileWriter.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public String getType() {
		return MyCustomMassSpecProperty.TYPE;
	}

	@Override
	public PropertyWriter getWriter() {
		return MyCustomMassSpecProperty.writer;
	}

	public static String getFullyQualifiedFolderName(Entry entry) {
		String workspaceLocation = PropertyHandler.getVariable("workspace_location");
		String projectName = DataModelSearch.findParentByType(entry, ProjectProperty.TYPE).getDisplayName();
		String folderName = workspaceLocation + projectName + File.separator + MyCustomMassSpecProperty.getFoldername();
		return folderName;		
	}

	public String getFullyQualifiedMetaDataFileName(Entry entry) {
		String msSettings = getMSSettingsFile().getName();
		if( msSettings == null ) {
			return null;
		}
		String workspaceLocation = PropertyHandler.getVariable("workspace_location");
		String projectName = DataModelSearch.findParentByType(entry, ProjectProperty.TYPE).getDisplayName();
		String mzXML = workspaceLocation + projectName + File.separator + MyCustomMassSpecProperty.getFoldername() +File.separator + msSettings;
		return mzXML;		
	}

	@Override
	public void delete(Entry entry) throws IOException {
		String workspaceLocation = PropertyHandler.getVariable("workspace_location");
		String projectName = DataModelSearch.findParentByType(entry, ProjectProperty.TYPE).getDisplayName();
		String msPath = workspaceLocation+projectName + File.separator + MyCustomMassSpecProperty.getFoldername();

		String sEntryPath = null; // the folder created for each entry under the ms folder

		// delete the uploaded files (mzXML, raw, peaklist etc.) from MassSpecMetaData
		if( getMassSpecMetaData() != null ) {
			sEntryPath = deleteSettings(entry, msPath);
		}
		// delete the settings file
		Iterator<PropertyDataFile> itr = getDataFiles().iterator();
		while( itr.hasNext() ) {
			PropertyDataFile file = itr.next();
			if( file.getName().trim().equals("") ) {
				continue;
			}
			DeleteUtils.delete(new File( msPath + File.separator + file.getName() ) );
		}

		if( sEntryPath != null ) {
			DeleteUtils.delete(new File(msPath + File.separator + sEntryPath) );
		}
	}


	public static Entry getMSParentEntry( Entry entry ) {
		if ( entry.getProperty() instanceof MyCustomMassSpecProperty ) {
			return entry;
		}
		if ( entry.getParent() != null )
			return MyCustomMassSpecProperty.getMSParentEntry(entry.getParent());
		return null;
	}

	@Override
	public boolean updateMSSettings( MassSpecMetaData metaData, String sSettingsFIle ) {
		try {
			marshallSettingsFile(sSettingsFIle, metaData);
		} catch( Exception e ) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public Object clone() {
		MyCustomMassSpecProperty newProp = new MyCustomMassSpecProperty();	
		if ( getMassSpecMetaData() != null ) {
			MassSpecMetaData settings = (MassSpecMetaData) getMassSpecMetaData().clone();
			newProp.setDataFiles(getDataFiles());
			newProp.setMassSpecMetaData(settings);
		}
		return newProp;
	}

	@Override
	public Property getParentProperty() {
		// TODO Auto-generated method stub
		return null;
	}	
	
	@Override
	public MassSpecProperty getNewMSProperty () {
		MyCustomMassSpecProperty t_property = new MyCustomMassSpecProperty();
		t_property.setVersion(CURRENT_VERSION);
		return t_property;
	}
}
