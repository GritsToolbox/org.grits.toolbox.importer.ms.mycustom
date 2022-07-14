package org.grits.toolbox.importer.ms.mycustom.wizard;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.grits.toolbox.entry.ms.mycustom.property.datamodel.MyMSPropertyDataFile;
import org.grits.toolbox.entry.ms.property.datamodel.MSPropertyDataFile;
import org.grits.toolbox.importer.ms.mycustom.MyCustomFileInfo;
import org.grits.toolbox.importer.ms.wizard.PageOne;
import org.grits.toolbox.ms.file.FileCategory;
import org.grits.toolbox.ms.file.MSFileInfo;
import org.grits.toolbox.ms.om.data.Method;

public class MyPageOne extends PageOne {
	public static final String MS_MY_TYPE = "BRANDEIS";
	public static final String MS_MY_TYPE_LABEL = "Brandeis MS";

	@Override
	protected int setLeftLCGroup(Group experimentComposite, Set<String> allExperimentTypes, String sDefaultExpType) {
		return super.setLeftLCGroup(experimentComposite, allExperimentTypes, sDefaultExpType);
	}
	
	@Override
	protected int setRightDIGroup(Group experimentComposite, Set<String> allExperimentTypes, String sDefaultExpType) {
		return super.setRightDIGroup(experimentComposite, allExperimentTypes, sDefaultExpType);
	}
		
	@Override
    protected MSPropertyDataFile getAnnotationDataFile( String sAnnotationFile ) {
		MSPropertyDataFile msAnnotFile = null;
		if( getExperimentSelection().equals(MyPageOne.MS_MY_TYPE) ) {
			msAnnotFile = new MyMSPropertyDataFile(sAnnotationFile, MyCustomFileInfo.MSFORMAT_CUSTOM_CURRENT_VERSION, 
					MyCustomFileInfo.MSFORMAT_CUSTOM_TYPE, 
				FileCategory.ANNOTATION_CATEGORY, MSFileInfo.MS_FILE_TYPE_DATAFILE, sAnnotationFile, 
				Arrays.asList(new String[]{FileCategory.ANNOTATION_CATEGORY.getLabel()}), false);    
		} else {
			msAnnotFile = super.getAnnotationDataFile(sAnnotationFile);
		}
		return msAnnotFile;
    }
	
    /**
     * see which radio button is selected for the experiment type
     * @return the experiment type selected
     */
	@Override
    protected String getExperimentSelection() {
		for (Button button : getExperimentTypeButtons()) {
			if (button.getSelection()) {
				String selectedLabel = button.getText();
				String gritsType = Method.getMsTypeByLabel(selectedLabel);
				if( gritsType != null ) {
					return gritsType;
				}
				if(selectedLabel.equals(MS_MY_TYPE_LABEL) ) {
					return MS_MY_TYPE;
				}
			}
		}
		return null;
	}
	
}
