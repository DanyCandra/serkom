package com.serkom.site.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

public class AbstractExporter {

	public void setResponseHeader(HttpServletResponse response, String contentType, String extention) throws IOException {

		// set tanggal dan nama file
		DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timeStamp = dateFormater.format(new Date());
		String fileName = "users_" + timeStamp + extention;

		// set browser untuk download file berupa content(text/csv/dll)
		response.setContentType(contentType);

		// set respone agar browser download content/file yang telah dibuat
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName;
		response.setHeader(headerKey, headerValue);



	}

}
