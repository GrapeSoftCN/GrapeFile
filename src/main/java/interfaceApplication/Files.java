package interfaceApplication;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import esayhelper.JSONHelper;
import model.FileModel;
import model.fileconvert;

@SuppressWarnings("unchecked")
public class Files {
	private FileModel fileModel = new FileModel();
	private JSONObject _obj = new JSONObject();

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
		_obj.put("records", JSONHelper.string2json(fileModel.add(object)));
		return fileModel.resultmsg(0, _obj.toString());
	}

	// 重命名文件或文件夹,返回重命名后的数据信息
	public String FileUpdate(String fid, String fileInfo) {
		_obj.put("records", JSONHelper.string2json(fileModel.update(fid, JSONHelper.string2json(fileInfo))));
		return fileModel.resultmsg(0, _obj.toString());
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
		_obj.put("records", fileModel.page(idx, pageSize, JSONHelper.string2json(fileInfo)));
		return fileModel.resultmsg(0, _obj.toString());
	}

	public String FindFile(String fileInfo) {
		_obj.put("records", fileModel.find(JSONHelper.string2json(fileInfo)));
		return fileModel.resultmsg(0, _obj.toString());
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
	 *            非回收站文件[{"_id":文件id;"size":文件大小},{"_id":文件id;"size":文件大小},{
	 *            "_id":文件id;"size":文件大小}]
	 *            回收站文件[{"_id":文件id;"size":文件大小,"isdelete":1},{"_id":文件id;"size"
	 *            :文件大小,"isdelete":1},{"_id":文件id;"size":文件大小,"isdelete":1}]
	 * @return
	 */
	public String BatchDelete(String FileInfo) {
		JSONArray array = (JSONArray) JSONValue.parse(FileInfo);
		return fileModel.resultmsg(fileModel.batch(array), "操作成功");
	}

	public String getWord(String fid) {
		JSONObject object = fileModel.find(fid);
		String words = new fileconvert().office2htmlString(object.get("filepath").toString(), "e://test");
		// String words="putao520";
		_obj.put("records", words);
		return fileModel.resultmsg(0, _obj.toString());
	}
	//获取文件路径
	public String geturl(String fid) {
		JSONObject object = fileModel.find(fid);
		String url = object.get("filepath").toString();
		url ="http://123.57.214.226:8080"+url.split("webapps")[1];
		return url+"\\"+object.get("fileoldname").toString();
	}
}
