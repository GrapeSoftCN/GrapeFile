package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

public class fileconvert {
	private FileConvertModel model = new FileConvertModel();

	/**
	 * office文件转换成pdf
	 * 
	 * @param sourceFile
	 *          源文件地址
	 * @param destFile
	 *          目标文件地址
	 * @throws IOException
	 */
	public void office2pdf(String sourceFile, String destFile) throws IOException {
		File inputFile = new File(sourceFile);
		File outputFile = new File(destFile);
		if (!inputFile.exists()) {
			return;
		}
		if (!outputFile.exists()) {
			outputFile.mkdir();
		}
		outputFile = new File(destFile + "\\" + (int) System.currentTimeMillis() / 1000
				+ ".pdf");
		OpenOfficeConnection connection = model.execOpenOffice();
		DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
		converter.convert(inputFile, outputFile);
		model.close(connection);
	}

	/**
	 * office文件转换成html文件
	 * 
	 * @param sourceFile
	 *          源文件路径
	 * @param destFile
	 *          目标文件所在目录
	 */
	public String office2html(String sourceFile, String destFile) {
		File inputFile = new File(sourceFile);
		File outputFile = new File(destFile);
		if (!inputFile.exists()) {
			return "";
		}
		if (!outputFile.exists()) {
			outputFile.mkdir();
		}
		outputFile = new File(destFile + "\\" + "testfileconvert"
				+ ".html");
		OpenOfficeConnection connection = model.execOpenOffice();
		DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
		converter.convert(new File(sourceFile), outputFile);
		model.close(connection);
		return outputFile.toString();
	}

	/**
	 * office转换成html格式，并获取html文件内容
	 * 
	 * @param sourceFile  源文件目录
	 * @param destFile    目标文件目录
	 * @return
	 */
	public String office2htmlString(String sourceFile, String destFile) {
		File htmlFile = new File(office2html(sourceFile, destFile));
		// 获取html文件流
		StringBuffer html = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
					htmlFile), Charset.forName("gb2312")));
			while (br.ready()) {
				html.append(br.readLine());
			}
			br.close();
			// 删除临时文件
			htmlFile.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return html.toString();
		// HTML文件字符串
//		String htmlStr = html.toString();
		// 返回经过清洁的html文本
//		 return clearFormat(html.toString(), destFile);
	}

	public String clearFormat(String htmlStr, String filepath) {
		// 获取body内容的正则
		String bodyReg = "<BODY .*</BODY>";
		Pattern bodyPattern = Pattern.compile(bodyReg);
		Matcher bodyMatcher = bodyPattern.matcher(htmlStr);
		if (bodyMatcher.find()) {
			// 获取BODY内容，并转化BODY标签为DIV
			htmlStr = bodyMatcher.group().replaceFirst("<BODY", "<DIV").replaceAll("</BODY>",
					"</DIV>");
		}

		// 调整图片地址
		htmlStr = htmlStr.replaceAll("<IMG SRC=\"", "<IMG SRC=\"" + filepath + "/");
		// 把<P></P>转换成</div></div>保留样式
		// content = content.replaceAll("(<P)([^>]*>.*?)(<\\/P>)",
		// "<div$2</div>");
		// 把<P></P>转换成</div></div>并删除样式
		htmlStr = htmlStr.replaceAll("(<P)([^>]*)(>.*?)(<\\/P>)", "<p$3</p>");
		// 删除不需要的标签
		htmlStr = htmlStr.replaceAll(
				"<[/]?(font|FONT|span|SPAN|xml|XML|del|DEL|ins|INS|meta|META|[ovwxpOVWXP]:\\w+)[^>]*?>",
				"");
		// 删除不需要的属性
		htmlStr = htmlStr.replaceAll(
				"<([^>]*)(?:lang|LANG|class|CLASS|style|STYLE|size|SIZE|face|FACE|[ovwxpOVWXP]:\\w+)=(?:'[^']*'|\"\"[^\"\"]*\"\"|[^>]+)([^>]*)>",
				"<$1$2>");

		return StringEscapeUtils.unescapeJava(htmlStr);
	}
}
