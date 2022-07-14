package org.grits.toolbox.importer.ms.mycustom.wizard;

import org.apache.log4j.Logger;
import org.grits.toolbox.entry.ms.mycustom.property.datamodel.MyCustomMassSpecProperty;
import org.grits.toolbox.entry.ms.property.MassSpecProperty;
import org.grits.toolbox.entry.ms.property.datamodel.MassSpecMetaData;
import org.grits.toolbox.importer.ms.wizard.NewMSWizard;
import org.grits.toolbox.importer.ms.wizard.PageOne;

public class MyNewMSWizard extends NewMSWizard {
	//log4J Logger
	private static final Logger logger = Logger.getLogger(MyNewMSWizard.class);

	public MyNewMSWizard() {
		super();
		NewMSWizard.preferences.getAllExperimentTypes().add(MyPageOne.MS_MY_TYPE_LABEL);
	}
	
	@Override
	protected PageOne getNewPageOne() {
		return new MyPageOne();
	}

	@Override
	protected MassSpecProperty getNewMSProperty() {
		if( getOne().getMsExperimentType().equals(MyPageOne.MS_MY_TYPE) ) {
			MassSpecProperty property = new MyCustomMassSpecProperty();
			property.setVersion(MyCustomMassSpecProperty.CURRENT_VERSION);
			return property;
		}
		return super.getNewMSProperty();
	}

	@Override
	protected MassSpecMetaData getNewMSMetaData() {
		if( getOne().getMsExperimentType().equals(MyPageOne.MS_MY_TYPE) ) {
			MassSpecMetaData model = new MassSpecMetaData();
			model.setVersion(MassSpecMetaData.CURRENT_VERSION);	
			return model;
		}
		return super.getNewMSMetaData();
	}

}
