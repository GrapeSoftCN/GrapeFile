package model;

import java.io.IOException;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;

public class FileConvertModel {
	private Process process = null;
	private OpenOfficeConnection connection = null;

	/**
	 * 启动openoffice并连接到openoffice
	 * 
	 * @return
	 */
	public OpenOfficeConnection execOpenOffice() {
		String command = "E:\\OpenOffice\\OpenOffice4\\program\\soffice.exe -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\"";
		try {
			process = Runtime.getRuntime().exec(command);
			connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
			connection.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * 关闭连接
	 */
	public void close(OpenOfficeConnection connection) {
		connection.disconnect();
		process.destroy();
	}
}
