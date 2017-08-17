package ru.idealplm.vsp.oceanos.data;

import java.util.ArrayList;

import com.teamcenter.rac.kernel.TCComponentBOMLine;

import ru.idealplm.vsp.oceanos.util.LineUtil;

public class ReportLineOccurence
{
	public TCComponentBOMLine bomLine;
	public ReportLine reportLine;
	public ReportLineOccurence parentOccurence;
	public ArrayList<ReportLineOccurence> children;
	public int quantity = 1;
	public int quantityMult = 1;
	public String remark = "";
	public ArrayList<String> remarkLines;
	private int lineHeight = 1;
	
	public ReportLineOccurence(ReportLine reportLine, ReportLineOccurence parentOccurence)
	{
		this.parentOccurence = parentOccurence;
		this.reportLine = reportLine;
		this.children = new ArrayList<ReportLineOccurence>(1);
	}
	
	public int getTotalQuantity()
	{
		return quantity * quantityMult;
	}
	
	public String getLineId()
	{
		return reportLine==null?"":reportLine.id;
	}
	
	public String getParentId()
	{
		return parentOccurence==null?"":parentOccurence.getLineId();
	}
	
	public void addChildOccurence(ReportLineOccurence child)
	{
		this.children.add(child);
	}
	
	public int calcLineHeight(double maxWidth)
	{
		remarkLines = new ArrayList<String>(1);
		remarkLines = LineUtil.getFittedLines(remark, maxWidth);
		lineHeight = remarkLines.size();
		return lineHeight;
	}
}