/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.services.rs.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.reporting.ReportGeneratorPlugin;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportTypeContainer;
import org.miloss.fgsms.test.WebServiceBaseTests;

/**
 *
 * @author alex.oree
 */
public class GenerateDocumentationTest extends WebServiceBaseTests{
    
    
    public GenerateDocumentationTest() throws Exception {
         super();
        url = "http://localhost/jUnitTestRS";
        Init();
    }
    
    @Test
    public void generateMarkDown() throws Exception {
        File file = new File("../../src/site/markdown");
        Assert.assertTrue(file.exists() && file.isDirectory());
        file = new File(file, "reportTypes.md");
        
        FileOutputStream fos = new FileOutputStream(file);
        Writer out = new BufferedWriter(new OutputStreamWriter(fos, Constants.CHARSET));
        
        
        
        
         List<ReportTypeContainer> array = new ArrayList<ReportTypeContainer>();
        Connection configurationDBConnection = Utility.getConfigurationDBConnection();
        PreparedStatement prepareStatement = configurationDBConnection.prepareStatement("select classname from plugins where appliesto='REPORTING'");
        ResultSet executeQuery = prepareStatement.executeQuery();
        while (executeQuery.next()) {
            ReportTypeContainer x= new ReportTypeContainer();
            x.setType(executeQuery.getString(1));
            array.add(x);
        }
        executeQuery.close();
        prepareStatement.close();
        configurationDBConnection.close();
        
        out.write("### Report Types\n\n");
        
        out.write("This list represents the out of the box report types provided by FGSMS. It is a plugin system and this list is actually generated during the software build"
                + " of FGSMS. \n\n");
        out.write("| Report Type | Applies to (Policy Type) | Description | \n");
        out.write("| ----------- | ------------------------ | ----------- | \n");
        
        List<String> displayNames = new ArrayList<String>();
        
        Map<String,ReportGeneratorPlugin> map = new HashMap<String, ReportGeneratorPlugin>();
        
        for (int i=0; i < array.size(); i++){
            String clazz = array.get(i).getType();
            ReportGeneratorPlugin plugin = (ReportGeneratorPlugin) Class.forName(clazz).newInstance();
            map.put(plugin.GetDisplayName(), plugin);
            displayNames.add(plugin.GetDisplayName());
        }
        
        Collections.sort(displayNames);
        
        
        for (int i=0; i < displayNames.size(); i++){
            ReportGeneratorPlugin plugin = map.get(displayNames.get(i));
            out.write("| ");
            out.write(plugin.GetDisplayName());
            //out.write(" | ");
            //out.write(plugin.getClass().getCanonicalName());
            out.write(" | ");
            out.write(formatList(plugin.GetAppliesTo()));
            out.write (" | ");
            out.write(plugin.GetHtmlFormattedHelp());
            
            out.write("\n");            
        }
        
        out.write("\n");
        out.close();
        fos.close();
    }

    private static String formatList(List<PolicyType> list) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < list.size(); i++){
            sb.append(list.get(i).name()).append("<br>");
        }
        sb.setLength(sb.length()-4);
        return sb.toString();
    }
    
}
