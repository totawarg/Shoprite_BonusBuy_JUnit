package za.co.invictus.testtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class XSLTest {
	
	private static String xslMapping = "C:/Users/Martin/workspace/XSLMappings/resources/SHOPRITE-584-BonusBuys/BonusBuys.xsl";
	
	private static String xmlSourceDir = "D:/Projects/Shoprite/COR3/POS Outbound/584 Bonus Buys/Input/";
	private static String xmlTargetDir = "D:/Projects/Shoprite/COR3/POS Outbound/584 Bonus Buys/Output/";
	private static String xmlReferenceDir = "D:/Projects/Shoprite/COR3/POS Outbound/584 Bonus Buys/Reference Output/";
	
	private String inputFile;
	
	@Parameters(name="{0}")
    public static Collection<String> data() {
		//System.out.println("@Parameters - data()");
		
    	//Read list of source files...
    	File xslSrcDir;
    	ArrayList<String> files = new ArrayList<String>();
    	
        xslSrcDir = new File(xmlSourceDir);
        
        //TODO: implement FileNameFilter...
        if (xslSrcDir.isDirectory()) {
        	
        	for (String filename : xslSrcDir.list()) {
        		if (filename.matches(".*xml|.*XML")) files.add(filename); 
        		
        	}
        }
    	
        return files;
    }
	
	public XSLTest(String inputFile) {
		this.inputFile = inputFile;
		
	}
	
	@Test
	public void checkInputFile() {
		File file = new File(xmlSourceDir + inputFile);
		assertNotEquals("Checking input file is readable: " + inputFile, false, file.canRead());
	}
	
	@Test
	public void checkReferenceFile() {
		File refFile = new File(xmlReferenceDir + inputFile.replaceAll("\\.XML|\\.xml", ".OUTPUT.XML"));
		assertNotEquals("Checking reference file is readable: " + refFile, false, refFile.canRead());
	}
	
	@Test
	public void xslOutputMatchesReference() {
		//System.out.println("@Test - xslOutputMatchesReference");
		
		//Check file exist
		File file = new File(xmlSourceDir + inputFile);
		//assertNotEquals("Checking input file is readable: " + inputFile, false, file.canRead());
		
		String output = performMapping(file);
		
		File outFile = new File(xmlTargetDir + inputFile.replaceAll("\\.XML|\\.xml", ".OUTPUT.XML"));
		try {
			FileWriter writer = new FileWriter(outFile);
			writer.write(output);
			writer.flush();
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File refFile = new File(xmlReferenceDir + inputFile.replaceAll("\\.XML|\\.xml", ".OUTPUT.XML"));
		assertNotEquals("Checking reference file is readable: " + refFile, false, refFile.canRead());

		String reference = readFile(refFile);
		
		assertEquals("Checking output " + inputFile + " matches reference failed", output.hashCode(), reference.hashCode());
		
		//System.out.println("XSL mapping output matches reference file. " + inputFile);
		
	}
	
	private String performMapping(File file) {
		
		TransformerFactory factory = TransformerFactory.newInstance();
		
		// Add a URI resolver so that the XSL docs may import other XSL docs
		// using relative paths
//		factory.setURIResolver(new URIResolver() {
//			
//			public Source resolve(String href, String base) throws TransformerException
//			{
//				
//				//String url = base.substring(0, base.lastIndexOf("/") + 1) + href;
//				String url = "/" + href;
//				
//				System.out.println(url);
//				
//				InputStream is= XSLTest.class.getResourceAsStream(url);
//				return new StreamSource(is);
//				
//			}
//		});
		
        Transformer transformer;
        
        Source inputFile = new StreamSource(file);
		
		File xslFile = new File(xslMapping);
		
		Source xsl = new StreamSource(xslFile);
        
		try {
			transformer = factory.newTransformer(xsl);
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			StreamResult output = new StreamResult(outputStream);
			transformer.transform(inputFile, output);

			byte[] bResult = outputStream.toByteArray();
			
			return (new String(bResult)).replaceAll("<\\!--.*-->", "");
			
		} catch (TransformerConfigurationException tce) {
			// TODO Auto-generated catch block
			tce.printStackTrace();
		} catch (TransformerException te) {
			// TODO Auto-generated catch block
			te.printStackTrace();
		}
		
		return "";
		
	}
	
	private String readFile(File file) {
		
		byte[] content;
		
		try {
			content = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

			//System.out.println(new String(content));
			
			return (new String(content)).replaceAll("<\\!--.*-->", "");
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
}
