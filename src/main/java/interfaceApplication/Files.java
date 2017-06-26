package interfaceApplication;

import java.io.FileInputStream;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import httpClient.request;
import model.FileModel;

@SuppressWarnings("unchecked")
public class Files {
	private FileModel fileModel = new FileModel();
	// private String userid;

	// public Files() {
	// userid = execRequest.getChannelValue("Userid").toString();
	// }

	// 新建文件夹
	public String AddFolder(String fileInfo) {
		JSONObject object = JSONHelper.string2json(fileInfo);
		object.put("filetype", 0); // 文件夹的文件类型设置为0
		object.put("isdelete", 0);
		if (!object.containsKey("fatherid")) {
			object.put("fatherid", 0);
		} else {
			object.put("fatherid", Integer.parseInt(String.valueOf(object.get("fatherid"))));
		}
		return fileModel.resultMessage(JSONHelper.string2json(fileModel.add(object)));
	}

	// 重命名文件或文件夹,返回重命名后的数据信息
	public String FileUpdate(String fid, String fileInfo) {
		return fileModel.resultMessage(JSONHelper.string2json(fileModel.update(fid, JSONHelper.string2json(fileInfo))));
	}

	// 文件存入回收站,fileInfo={\"isdelete\":\"1\"}[支持多个文件操作]
	public String RecyCle(String fid) {
		String fileInfo = "{\"isdelete\":1}";
		return fileModel.resultmsg(fileModel.RecyBatch(fid, JSONHelper.string2json(fileInfo)), "存入回收站成功");
	}

	// 文件还原,fileInfo={\"isdelete\":\"0\"}
	public String Restore(String fid) {
		String fileInfo = "{\"isdelete\":0}";
		return fileModel.resultmsg(fileModel.RecyBatch(fid, JSONHelper.string2json(fileInfo)), "从回收站还原文件成功");
	}

	// 文件移动至文件夹[包含批量移动]
	public String FileUpdateBatch(String fids, String folderid) {

		String FileInfo = "{\"fatherid\":\"" + folderid + "\"" + "}";
		return fileModel.resultmsg(fileModel.updates(fids, JSONHelper.string2json(FileInfo)), "文件移动到文件夹成功");
	}

	/**
	 * 条件分页显示
	 * 
	 * @param idx
	 * @param pageSize
	 * @param fileInfo
	 *            {isdelete:0}显示所有文件 {filetype:1}显示图片文件 {filetype:2}显示视频文件
	 *            {filetype:3}显示文档文件 {filetype:4}显示音频文件 {filetype:5}显示其他类型文件
	 *            {isdelete:1}显示回收站文件
	 * @return
	 */
	public String PageBy(int idx, int pageSize, String fileInfo) {
		return fileModel.resultMessage(fileModel.page(idx, pageSize, JSONHelper.string2json(fileInfo)));
	}

	public String FindFile(String fileInfo) {
		return fileModel.resultMessage(fileModel.find(JSONHelper.string2json(fileInfo)));
	}

	/**
	 * 删除单条文件数据
	 * 
	 * @param FileInfo
	 *            非回收站文件{"_id":文件id;"size":文件大小}
	 *            回收站文件{"_id":文件id;"size":文件大小,"isdelete":1}
	 * @return
	 */
	public String Delete(String FileInfo) {
		JSONObject object = JSONHelper.string2json(FileInfo);
		return fileModel.resultmsg(fileModel.delete(object), "操作成功");
	}

	/**
	 * 批量删除数据
	 * 
	 * @param FileInfo
	 *            非回收站文件[{"_id":文件id;"size":文件大小},{"_id":文件id;"size":文件大小},
	 *            {"_id":文件id;"size":文件大小}]
	 *            回收站文件[{"_id":文件id;"size":文件大小,"isdelete":1},
	 *            {"_id":文件id;"size":文件大小,"isdelete":1},
	 *            {"_id":文件id;"size":文件大小,"isdelete":1}]
	 * @return
	 */
	public String BatchDelete(String FileInfo) {
		JSONArray array = JSONHelper.string2array(FileInfo);
		return fileModel.resultmsg(fileModel.batch(array), "操作成功");
	}

	public String getWord(String fid) {
		JSONObject object = fileModel.find(fid);
		String message = "";
		try {
			String hoString = "http://" + getFileIp("file", 0);
//			String hoString = "http://127.0.0.1:8080";
			String filepath = object.get("filepath").toString();
//			String filepath = "\\File\\upload\\2017-06-21\\网格信息20170221.xls";
//			System.out.println(filepath);
			filepath = filepath.replace("\\", "@t");
			message = request
					.Get(hoString + "/File/FileConvert?sourceFile=" + filepath + "&type=2");
			message = message.replace("gb2312", "utf-8");
			
		} catch (Exception e) {
			e.printStackTrace();
			message = "";
		}
		return message;

//		JSONObject object2 = new JSONObject();
//		object2.put("records", words);
//		return fileModel.resultmsg(0, object2.toString());
	}

	// 获取文件对象
	public String getFile(String fid) {
		JSONObject object = fileModel.find(fid);
		return fileModel.resultmsg(0, object != null ? object.toString() : "");
	}

	// 获取文件路径
	public String geturl(String fid) {
		String url = "";
		JSONObject object = fileModel.find(fid);
		if (object == null) {
			return "";
		}
		if (object.containsKey("filepath")) {
			url = object.get("filepath").toString();
			url = getAppIp("file").split("/")[0] + url;
		}
		return url;
	}

	private String getAppIp(String key) {
		String value = "";
		try {
			Properties pro = new Properties();
			pro.load(new FileInputStream("URLConfig.properties"));
			value = pro.getProperty(key);
		} catch (Exception e) {
			value = "";
		}
		return value;
	}

	private String getFileIp(String key, int sign) {
		String value = "";
		try {
			if (sign == 0 || sign == 1) {
				value = getAppIp(key).split("/")[sign];
			}
		} catch (Exception e) {
			value = "";
		}
		return value;
	}
}
