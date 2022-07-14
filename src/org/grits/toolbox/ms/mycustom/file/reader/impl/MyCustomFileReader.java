package org.grits.toolbox.ms.mycustom.file.reader.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.grits.toolbox.ms.file.MSFile;
import org.grits.toolbox.ms.file.reader.IMSAnnotationFileReader;
import org.grits.toolbox.ms.file.scan.data.ScanView;
import org.grits.toolbox.ms.om.data.Peak;
import org.grits.toolbox.ms.om.data.Scan;
import org.grits.toolbox.widgets.tools.NotifyingProcess;


public class MyCustomFileReader extends NotifyingProcess implements IMSAnnotationFileReader{
	// log4J Logger
	private static final Logger logger = Logger.getLogger(MyCustomFileReader.class);

	@Override
	public boolean isValid(MSFile file) {
		Scanner s = getScannerFile(file.getFileName());
		return (s != null);				
	}

	@Override
	public List<Scan> readMSFile(MSFile file) {
		List<Scan> lScans = getScanFromMyFile(file.getFileName());
		return lScans;
	}

	@Override
	public List<Scan> readMSFile(MSFile file, int scanNumber) {
		List<Scan> scans = readMSFile(file);
		List<Scan> lScans = new ArrayList<>();
		for( Scan scan : scans ) {
			if( scan.getScanNo() == scanNumber ) {
				lScans.add(scan);
			}
		}
		return lScans;
	}

	@Override
	public List<Scan> readMSFile(MSFile file, int msLevel, int parentScanNum, int scanNum) {
		List<Scan> scans = readMSFile(file);
		List<Scan> lScans = new ArrayList<>();
		for( Scan scan : scans ) {
			if(parentScanNum >= 0 && scan.getParentScan() != null && scan.getParentScan() == parentScanNum ) {
				lScans.add(scan);
			}
			if( msLevel > 0 && scan.getMsLevel() == msLevel ) {
				lScans.add(scan);
			}
			if( scanNum >= 0 && scan.getScanNo() == scanNum ) {
				lScans.add(scan);
			}
		}
		return lScans;
	}

	@Override
	public List<Integer> getScanList(MSFile file, int scanNumber) {
		List<Scan> lScans = readMSFile(file);
		List<Integer> lScanNos = new ArrayList<>();
		for( Scan s : lScans ) {
			lScanNos.add(s.getScanNo());
		}
		return lScanNos;
	}

	@Override
	public Integer getMaxScanNumber(MSFile file) {
		List<Scan> lScans = readMSFile(file);
		int iMaxScanNum = Integer.MIN_VALUE;
		for( Scan s : lScans ) {
			if( s.getScanNo() > iMaxScanNum ) {
				iMaxScanNum = s.getScanNo();
			}
		}
		return iMaxScanNum;
	}

	@Override
	public Integer getMinScanNumber(MSFile file) {
		List<Scan> lScans = readMSFile(file);
		int iMinScanNum = Integer.MAX_VALUE;
		for( Scan s : lScans ) {
			if( s.getScanNo() < iMinScanNum ) {
				iMinScanNum = s.getScanNo();
			}
		}
		return iMinScanNum;
	}

	@Override
	public Integer getMinMSLevel(MSFile file) {
		List<Scan> lScans = readMSFile(file);
		int iMinMSLevel = Integer.MAX_VALUE;
		for( Scan s : lScans ) {
			if( s.getScanNo() > 0 && s.getMsLevel() < iMinMSLevel ) {
				iMinMSLevel = s.getMsLevel();
			}
		}
		return iMinMSLevel;
	}

	@Override
	public boolean hasMS1Scan(MSFile file) {
		List<Scan> lScans = readMSFile(file);
		for( Scan s : lScans ) {
			if( s.getScanNo() > 0 && s.getMsLevel() == 1 ) {
				return true;
			}
		}
		return false;
	}

	private int countScansByMSLevel(List<Scan> lScans, int iMSLevel ) {
		int iNumScans = 0;
		if ( lScans != null ) {
			for( Scan s : lScans ) {
				if (s.getScanNo() > 0 && s.getMsLevel() == iMSLevel ) {
					iNumScans++;
				}
			}
		}		
		return iNumScans;
	}

	@Override
	public int getNumMS1Scans(MSFile file) {
		try {
			List<Scan> lScans = readMSFile(file);
			return countScansByMSLevel(lScans, 1);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return 0;
	}

	@Override
	public int getNumMS2Scans(MSFile file) {
		try {
			List<Scan> lScans = readMSFile(file);
			return countScansByMSLevel(lScans, 2);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return 0;
	}

	@Override
	public List<ScanView> readMSFileForView(MSFile file, int msLevel, int parentScanNum, int scanNum) {
		try {			
			updateListeners("Reading XML file", -1);
			
			List<Scan> scans = readMSFile(file, msLevel, parentScanNum, scanNum);
			List<ScanView> scansViewList = new ArrayList<ScanView>();
			for (Scan scan : scans) {
				ScanView scanView = new ScanView();
				scanView.setMsLevel(scan.getMsLevel());
				scanView.setScanNo(scan.getScanNo());
				scanView.setParentScan(scan.getParentScan());
				if (scan.getPrecursor() != null) {
					scanView.setPreCursorIntensity(scan.getPrecursor().getIntensity());
					scanView.setPreCursorMz(scan.getPrecursor().getMz());
				}
				else { 
					scanView.setPreCursorIntensity(1.0);
					scanView.setPreCursorMz(-1d);
				}
				scanView.setRetentionTime(scan.getRetentionTime());
				if (!scan.getSubScans().isEmpty())
					scanView.setSubScans(readMSFileForView(file, -1, scan.getScanNo(), -1));
				scansViewList.add(scanView);
			}
			 
			if( scansViewList != null ) {
				Collections.sort(scansViewList);
			}
			if( scansViewList.isEmpty() ) {
				if (!isCanceled())
					updateErrorListener("Warning: no scan data read from MS file. The file may be invalid or incorrect type.");
			}
			return scansViewList;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	public Scan getFirstMS1Scan(MSFile file) {
		List<Scan> lScans = readMSFile(file);
		for( Scan s : lScans ) {
			if( s.getMsLevel() == 1 ) {
				return s;
			}
		}
		return null;
	}

	protected Scanner getScannerFile( String specFileName ) {
		Scanner sc = null;
		File specFile = new File(specFileName);
		try {
			sc = new Scanner(specFile);
		} catch (FileNotFoundException e) {
			return null;
		}		
		return sc;
	}

	public List<Scan> getScanFromMyFile(String specFilename) {
		// load all data
		Scanner sc = getScannerFile( specFilename );
		if( sc == null ) {
			return null;
		}
		double mPrecursor = -1;
		String mMetal = "";
		String currentLine = sc.nextLine().trim();
		boolean firstLine = true;
		while (currentLine.startsWith("#") || currentLine.isEmpty()) {
			if (currentLine.startsWith("# Metal:")) {
				mMetal = currentLine.substring(9);
				if (mMetal.equals("H")) {
					mMetal = "Proton";
				}
			} 
			else if (currentLine.startsWith("# Precursor:")) {
				// add some line here to get correct mPrecursor
				String[] fields = currentLine.substring(13).split(";");
				String temp = mMetal + "+";
				if (mMetal.equals("Proton")) {
					temp = "H+";
				}
				for (String p : fields) {
					p = p.trim();
					int idx = p.indexOf(temp);
					int num = 1;
					if (idx != -1) {
						mPrecursor = Double.valueOf(p.substring(idx + temp.length() + 1));
						//						if (idx != 0) {
						//							num = Integer.valueOf(p.substring(0, idx));
						//						}
						//						mPrecursor = num * mPrecursor - num * (CMass.getAtomMass(mMetal) - CMass.Electron)
						//								+ CMass.Proton;
					}
				}
			}
			currentLine = sc.nextLine().trim();
		}
		Scan ms1Scan = new Scan();
		ms1Scan.setMsLevel(1);
		ms1Scan.setScanNo(1);
		ms1Scan.setActivationMethode("CID");
		ms1Scan.setRetentionTime(0.0);
		ms1Scan.setTotalNumPeaks(1);
		Peak precursor = new Peak();
		precursor.setCharge(1);
		precursor.setIsPrecursor(true);
		precursor.setPrecursorMz(mPrecursor);
		precursor.setMz(mPrecursor);
		precursor.setIntensity(1.0);
		precursor.setId(1);
		Scan ms2Scan = new Scan();
		ms2Scan.setMsLevel(2);
		ms2Scan.setScanNo(2);
		ms2Scan.setIsCentroided(true);
		ms2Scan.setActivationMethode("CID");
		ms1Scan.getSubScans().add(2);
		ms1Scan.getPeaklist().add(precursor);
		ms2Scan.setPrecursor(precursor);
		List<Peak> peakList = new ArrayList<>();
		int iPeakCnt = 2;
		while (sc.hasNextLine()) {
			Scanner lineSc = new Scanner(sc.nextLine().trim());
			if (!lineSc.hasNext()) {
				continue;
			}
			double rawMZ = lineSc.nextDouble();
			int rawZ = Character.getNumericValue(lineSc.next().charAt(0));
			double intensity = lineSc.nextDouble();
			lineSc.close();
			Peak peak = new Peak();
			peak.setCharge(rawZ);
			peak.setMz(rawMZ);
			peak.setIntensity(intensity);
			peak.setId(iPeakCnt++);
			peakList.add(peak);
		}
		ms2Scan.setPeaklist(peakList);
		ms2Scan.setTotalNumPeaks(peakList.size());
		ms2Scan.setParentScan(1);
		ms2Scan.setPrecursor(precursor);
		sc.close();
		List<Scan> scans = new ArrayList<>();
		scans.add(ms1Scan);
		scans.add(ms2Scan);
		return scans;
	}

}
