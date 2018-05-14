package org.vu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Configure {

	private static final String GITHUB_OFFICIAL_URL = "https://github.com/";
	Properties properties = new Properties();

	public void modifyConfigFile(String gitURL) {
		String filepath = "config.xml";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);
			Node projectUrl = doc.getElementsByTagName("projectUrl").item(0);
			projectUrl.setTextContent(gitURL);

			Node url = doc.getElementsByTagName("url").item(0);
			url.setTextContent(gitURL.substring(0, gitURL.length() - 1) + ".git");

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filepath));
			transformer.transform(source, result);

			System.out.println("Done");

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

	public void setup() {
		properties = loadPropertyFile();
		List<String> users = Arrays.asList(properties.getProperty("github.users").split(","));
		String classroomName = properties.getProperty("github.classroom.name");
		String assignmentName = properties.getProperty("github.assignment.name");
		String gitURL = GITHUB_OFFICIAL_URL + classroomName + "/" + assignmentName + "-";
		for (String user : users) {
			System.out.println(user);
			modifyConfigFile(gitURL + user + "/");
			createJenkinsJob(assignmentName+"-"+user);
		}
	}

	private void createJenkinsJob(String jenkinsJobName) {
		File src = new File("config.xml");
		File dest = new File(properties.getProperty("jenkins.job.path") + jenkinsJobName);
		try {
			if (!dest.exists()) {
				Files.createDirectory(dest.toPath());
			}
		dest = new File(dest.toString()+"/config.xml");
		Files.copy(src.toPath(), dest.toPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private Properties loadPropertyFile() {
		try (InputStream in = new FileInputStream("application.properties")) {
			properties.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

}
