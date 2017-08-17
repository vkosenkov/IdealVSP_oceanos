package ru.idealplm.vsp.oceanos.core;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.dialogs.ExportConfiguredNXAssemblyDialog;
import com.teamcenter.rac.pse.plugin.Activator;

import ru.idealplm.vsp.oceanos.core.Report.ReportType;
import ru.idealplm.vsp.oceanos.excel.ExcelReportBuilder;
import ru.idealplm.vsp.oceanos.handlers.VSPHandler;
import ru.idealplm.vsp.oceanos.xml.PDFReportBuilder;
import ru.idealplm.vsp.oceanos.xml.PDFReportBuilderConfiguration;
import ru.idealplm.vsp.oceanos.xml.XmlBuilder;
import ru.idealplm.vsp.oceanos.xml.XmlBuilderConfiguration;

public class VSP
{
	public static final String CLIENT_ID = UUID.randomUUID().toString();
	public static TCComponentBOMLine topBOMLine;
	public static TCComponentItem topBOMLineI;
	public static TCComponentItemRevision topBOMLineIR;
	public static TCComponentItemRevision vspIR;
	public static TCComponent generalNoteForm;
	public static String topItemId;
	
	public static ErrorList errorList;

	public ProgressMonitorDialog progressMonitor;
	public StampData stampData;
	public Report report;
	private DataReader dataReader;
	
	public static TCPreferenceService preferenceService = VSPHandler.session.getPreferenceService();
	
	public VSP()
	{
		stampData = new StampData();
		report = new Report();
		errorList = new ErrorList();
		report.type = ReportType.PDF;
	}
	
	public void init()
	{
		try{
			VSPSettings.reset();
			topBOMLine = Activator.getPSEService().getTopBOMLine();
			topBOMLineI = topBOMLine.getItem();
			topBOMLineIR = topBOMLine.getItemRevision();
			topItemId = topBOMLineI.getProperty("item_id");
			dataReader = new DataReader(this);
			
			String[] emptyValues = {};
			VSPSettings.nonbreakableWords = preferenceService.getStringArray(preferenceService.TC_preference_site, "Oc9_Spec_NonbreakableWords", emptyValues);
		} catch (TCException ex) {
			ex.printStackTrace();
			throw new RuntimeException("Error while initializing");
		}
	}
	
	public void readExistingData()
	{
		dataReader.readExistingData();
	}
	
	public void readData()
	{
		dataReader.readData();
	}

	public void buildXmlFile()
	{
		XmlBuilderConfiguration A4xmlBuilderConfiguration = new XmlBuilderConfiguration(26, 32);
		A4xmlBuilderConfiguration.MaxWidthGlobalRemark = 474;

		XmlBuilder xmlBuilder = new XmlBuilder(A4xmlBuilderConfiguration, this);
		File data = xmlBuilder.buildXml();

		report.data = data;
	}

	public void buildReportFile()
	{
		if(report.type == ReportType.PDF){
			buildXmlFile();
			InputStream template = VSP.class.getResourceAsStream("/pdf/OceanosVSPPDFTemplate.xsl");
			InputStream config = VSP.class.getResourceAsStream("/pdf/OceanosVSPUserconfig.xml");
			PDFReportBuilderConfiguration A3pdfBuilderconfiguration = new PDFReportBuilderConfiguration(template, config);
			System.out.println("SIZEEEEE"+report.linesList.getSortedList().size());
	
			report.configuration = A3pdfBuilderconfiguration;
	
			PDFReportBuilder reportBuilder = new PDFReportBuilder(report);
			reportBuilder.buildReportStatic();
		} else {
			ExcelReportBuilder reportBuilder = new ExcelReportBuilder(report);
		}
	}
	
	public void uploadReportFile()
	{
		ReportUploader uploader = new ReportUploader(this);
		uploader.addToTeamcenter();
	}
}
