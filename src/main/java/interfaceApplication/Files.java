package interfaceApplication;

import httpClient.request;
import java.io.FileInputStream;
import java.util.Properties;
import json.JSONHelper;
import model.FileModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import JGrapeSystem.jGrapeFW_Message;

public class Files {
	private FileModel fileModel;
	private String thumailPath = "\\File\\upload\\icon\\folder.ico";

	public Files() {
		this.fileModel = new FileModel();
	}

	public String AddFolder(String fileInfo) {
		JSONObject object = JSONHelper.string2json(fileInfo);
		object.put("filetype", Integer.valueOf(0));
		object.put("isdelete", Integer.valueOf(0));
		object.put("ThumbnailImage", this.thumailPath);
		if (!object.containsKey("fatherid"))
			object.put("fatherid", Integer.valueOf(0));
		else {
			object.put("fatherid", Integer.valueOf(Integer.parseInt(String.valueOf(object.get("fatherid")))));
		}
		return this.fileModel.resultMessage(JSONHelper.string2json(this.fileModel.add(object)));
	}

	public String FileUpdate(String fid, String fileInfo) {
		return this.fileModel
				.resultMessage(JSONHelper.string2json(this.fileModel.update(fid, JSONHelper.string2json(fileInfo))));
	}

	public String RecyCle(String fid) {
		String fileInfo = "{\"isdelete\":1}";
		return this.fileModel.resultmsg(this.fileModel.RecyBatch(fid, JSONHelper.string2json(fileInfo)), "存入回收站成功");
	}

	public String Restore(String fid) {
		String fileInfo = "{\"isdelete\":0}";
		return this.fileModel.resultmsg(this.fileModel.RecyBatch(fid, JSONHelper.string2json(fileInfo)), "从回收站还原文件成功");
	}

	public String FileUpdateBatch(String fids, String folderid) {
		String FileInfo = "{\"fatherid\":\"" + folderid + "\"" + "}";
		return this.fileModel.resultmsg(this.fileModel.updates(fids, JSONHelper.string2json(FileInfo)), "文件移动到文件夹成功");
	}

	public String PageBy(int idx, int pageSize, String fileInfo) {
		return this.fileModel.resultMessage(this.fileModel.page(idx, pageSize, JSONHelper.string2json(fileInfo)));
	}

	public String FindFile(String fileInfo) {
		return this.fileModel.resultMessage(this.fileModel.find(JSONHelper.string2json(fileInfo)));
	}

	public String Delete(String FileInfo) {
		JSONObject object = JSONHelper.string2json(FileInfo);
		return this.fileModel.resultmsg(this.fileModel.delete(object), "操作成功");
	}

	public String BatchDelete(String FileInfo) {
		JSONArray array = JSONHelper.string2array(FileInfo);
		return this.fileModel.resultmsg(this.fileModel.batch(array), "操作成功");
	}

	public String getWord(String fid) {
		JSONObject object = this.fileModel.find(fid);
		String message = "";
		if (object != null) {
			try {
				String hoString = "http://" + getFileIp("file", 0);
				String filepath = object.get("filepath").toString();
				filepath = filepath.replace("\\", "@t");
				message = request.Get(hoString + "/File/FileConvert?sourceFile=" + filepath + "&type=2");
				message = message.replace("gb2312", "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
				message = "";
			}
		} else {
			message = jGrapeFW_Message.netMSG(5, "该文档不存在");
		}
		return message;
	}

	public String getFile(String fid) {
		JSONObject object = this.fileModel.find(fid);
		return this.fileModel.resultmsg(0, object != null ? object.toString() : "");
	}

	public String getFiles(String fid) {
		JSONObject object = this.fileModel.GetFile(fid);
		return object.toString();
	}

	public String geturl(String fid) {
		String url = "";
		JSONObject object = this.fileModel.find(fid);
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
			if ((sign == 0) || (sign == 1))
				value = getAppIp(key).split("/")[sign];
		} catch (Exception e) {
			value = "";
		}
		return value;
	}
}