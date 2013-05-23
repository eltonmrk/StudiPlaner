package com.studiplaner;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import db.Lehrender;
import db.Notizen;
import db.Semester;
import db.Veranstaltung;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityImport extends Activity {
	Veranstaltung veranstaltung;
	Lehrender lehrender;
	Semester semester;
	Notizen notizen;
	Helper helper;
	Activity thisActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		veranstaltung = new Veranstaltung(this);
		lehrender = new Lehrender(this);
		semester = new Semester(this);
		notizen = new Notizen(this);
		helper = new Helper(this);
		thisActivity = this;
	}

	@Override
	public void onStart() {
		super.onStart();
		veranstaltung.openDBConnections();
		lehrender.openDBConnections();
		semester.openDBConnections();
		notizen.openDBConnections();
		helper.openDBConnections();

		buildImport();
	}

	@Override
	public void onStop() {
		super.onStop();
		veranstaltung.closeDBConnections();
		lehrender.closeDBConnections();
		semester.closeDBConnections();
		notizen.closeDBConnections();
		helper.closeDBConnections();
	}

	public void buildImport() {
		setContentView(R.layout.import_data);
		final LinearLayout importLayout = (LinearLayout) findViewById(R.id.import_container);
		TextView url = (TextView) importLayout.findViewById(R.id.import_url);
		String urlTxt = helper.getURL();
		if (urlTxt != null)
			url.setText(urlTxt);
		Button importOK = (Button) importLayout.findViewById(R.id.save);
		importOK.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				EditText urlEditField = (EditText) importLayout.findViewById(R.id.import_url);
				helper.saveURL(urlEditField.getEditableText().toString());
				Element rootElement = null;
				try {
					URL url = new URL(urlEditField.getEditableText().toString());
					DocumentBuilder builder =
							DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document xmldata = builder.parse(url.openConnection().getInputStream());
					rootElement = xmldata.getDocumentElement();
				} catch (SAXException e) {
					Helper.printMessage(thisActivity, thisActivity.getString(R.string.xml_not_valid), SAXException.class.getName(), false);
					return;
				} catch (IOException e) {
					Helper.printMessage(thisActivity, thisActivity.getString(R.string.file_not_found), IOException.class.getName(), false);
					return;
				} catch (ParserConfigurationException e) {
					Helper.printMessage(thisActivity, e.getMessage(), ParserConfigurationException.class.getName(), false);
					return;
				} catch (FactoryConfigurationError e) {
					Helper.printMessage(thisActivity, e.getMessage(), FactoryConfigurationError.class.getName(), false);
					return;
				}

				int semesterAdded = 0;
				int subjectsAdded = 0;
				int lecturerAdded = 0;
				int assignmentAdded = 0;

				if (rootElement.getNodeName().equals("studiplaner"))
				{
					// semesters
					NodeList semesterList = rootElement.getElementsByTagName("semester");
					if (semesterList.getLength() > 0)
					{
						notizen.deleteTable();
						semester.deleteTable();
						veranstaltung.deleteTable();
						lehrender.deleteTable();
					}
					for (int i = 0; i < semesterList.getLength(); i++)
					{
						Element semesterItemInXML = (Element) semesterList.item(i);
						long semesterId;
						semesterId = semester.saveSemester(
									semesterItemInXML.getAttribute("title"),
									semesterItemInXML.getAttribute("begin"),
									semesterItemInXML.getAttribute("end"));
						semesterAdded++;
						// subjects
						NodeList subjectList = semesterItemInXML.getElementsByTagName("subject");
						for (int b = 0; b < subjectList.getLength(); b++)
						{
							Element subjectItemXML = (Element) subjectList.item(b);
							veranstaltung.saveSubject(
										subjectItemXML.getAttribute("lecturerId"),
										subjectItemXML.getAttribute("title"),
										subjectItemXML.getAttribute("begin"),
										subjectItemXML.getAttribute("end"),
										subjectItemXML.getAttribute("day"),
										subjectItemXML.getAttribute("type"),
										subjectItemXML.getAttribute("place"),
										(int) semesterId);
							subjectsAdded++;
						}
					}
					// lecturers
					NodeList lecturerList = rootElement.getElementsByTagName("lecturer");
					for (int i = 0; i < lecturerList.getLength(); i++)
					{
						Element lecturer = (Element) lecturerList.item(i);
						lehrender.saveDozent(
									lecturer.getAttribute("name"),
									lecturer.getAttribute("email"),
									lecturer.getAttribute("place"));
						lecturerAdded++;
					}
					
					// assignments
					NodeList assignmentList = rootElement.getElementsByTagName("assignment");
					for (int i = 0; i < assignmentList.getLength(); i++)
					{
						Element assignment = (Element) assignmentList.item(i);
						notizen.saveNotice(
								assignment.getAttribute("title"), 
								assignment.getAttribute("content"), 
								assignment.getAttribute("tags"), 
								Integer.parseInt(assignment.getAttribute("archived")));
						assignmentAdded++;
					}

					String logTxt = "";
					if (semesterAdded != 0)
						logTxt += getString(R.string.semester_added) + ": " + String.valueOf(semesterAdded) + "\n";
					if (subjectsAdded != 0)
						logTxt += getString(R.string.subjects_added) + ": " + String.valueOf(subjectsAdded) + "\n";
					if (lecturerAdded != 0)
						logTxt += getString(R.string.lecturer_added) + ": " + String.valueOf(lecturerAdded) + "\n";
					if (assignmentAdded != 0)
						logTxt += getString(R.string.notes_added) + ": " + String.valueOf(assignmentAdded) + "\n";
					
					Helper.printMessage(thisActivity, logTxt, getString(R.string.request), true);
				}
			}
		});
	}

	public void finish(View arg0) {
		finish();
	}
}
