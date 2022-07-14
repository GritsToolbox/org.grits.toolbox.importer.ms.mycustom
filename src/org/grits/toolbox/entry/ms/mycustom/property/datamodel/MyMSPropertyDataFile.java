package org.grits.toolbox.entry.ms.mycustom.property.datamodel;

import java.util.List;

import org.apache.log4j.Logger;
import org.grits.toolbox.entry.ms.property.datamodel.MSPropertyDataFile;
import org.grits.toolbox.importer.ms.mycustom.MyCustomFileInfo;
import org.grits.toolbox.ms.file.FileCategory;
import org.grits.toolbox.ms.file.MSFile;
import org.grits.toolbox.ms.file.reader.IMSFileReader;
import org.grits.toolbox.ms.mycustom.file.reader.impl.MyCustomFileReader;

public class MyMSPropertyDataFile extends MSPropertyDataFile {
	private static final Logger logger = Logger.getLogger(MyMSPropertyDataFile.class);

	// required by JAXB
	public MyMSPropertyDataFile() {
	}
	
	/**
	 * MSPropertyDataFile constructor
	 * 
	 * @param name
	 * @param version
	 * @param type
	 * @param category
	 * @param msFileType
	 * @param originalFile
	 * @param purpose 
	 */
	public MyMSPropertyDataFile(String name, String version, String type, FileCategory category, String msFileType, String originalFile, List<String> purpose) {
		super(name, version, type, category, msFileType, originalFile, purpose, false);
	}

	/**
	 * MSPropertyDataFile constructor
	 * 
	 * @param name
	 * @param version
	 * @param type
	 * @param category
	 * @param msFileType
	 * @param originalFile
	 * @param purpose
	 * @param isParent whether it is a parent or child entry
	 */
	public MyMSPropertyDataFile(String name, String version, String type, FileCategory category, String msFileType, String originalFile, List<String> purpose, boolean isParent) {
		super(name, version, type, category, msFileType, originalFile, purpose, isParent);
	}
	
	/**
	 * return the reader based on the file properties.
	 * IMPORTANT: when new file types are supported this method needs to be updated
	 * @param file MS file to be read
	 * @return reader appropriate for reading the provided file
	 */
	public IMSFileReader getReaderForFile () {
		IMSFileReader reader = super.getReaderForFile();
		if( reader != null ) {
			return reader;
		}
				
		if (getType().equals(MyCustomFileInfo.MSFORMAT_CUSTOM_TYPE) ) {
			return new MyCustomFileReader();
		}
		return null;
	}
	
//	@Override
//	public MSFile getMSFileWithReader(String msPath, String msExperimentType, boolean displayOnly) {
//		MSFile msFile = getMSFileWithReader (msPath, msExperimentType);
//		if (getType().equals(MyCustomFileInfo.MSFORMAT_CUSTOM_TYPE) ) {
//			msFile.setReader(new MyCustomFileReader());
//		}
//		return msFile;
//	}
	
	@Override
	public boolean isValidMSFile() {
		boolean isMzXml = super.isValidMSFile(); 
		
		return (isMzXml ||  getType().equals(MyCustomFileInfo.MSFORMAT_CUSTOM_TYPE));
	}
	
	@Override
	public Object clone() {
		return super.clone();
	}
	
	@Override
	protected MSPropertyDataFile getNewMSPropertyDataFile(){
		MSPropertyDataFile clonePdf = new MyMSPropertyDataFile();
		return clonePdf;
	}
	
}
