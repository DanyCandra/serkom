package com.serkom.site.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.serkom.site.entity.User;



public class UserCSVExporter extends AbstractExporter{

	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {

		super.setResponseHeader(response, "text/csv",".csv" );

		//menulis csv dari lib super csv
		ICsvBeanWriter csvWriter=new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		//set header file csv
		String[] csvHeader= {"User ID","E-mail","First Name","Last Name"};
		String[] fieldMapping= {"id", "email","firstName","lastName"};

		//tulis file csv
		csvWriter.writeHeader(csvHeader);
		for(User user:listUsers) {
			csvWriter.write(user, fieldMapping);
		}
		csvWriter.close();

	}

}
