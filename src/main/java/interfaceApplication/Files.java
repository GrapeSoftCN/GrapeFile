package interfaceApplication;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import model.FileModel;
import model.fileconvert;
@SuppressWarnings("unchecked")
public class Files {
	private FileModel fileModel = new FileModel();
	private JSONObject _obj = new JSONObject();
	//新建文件夹
	public String AddFolder(String fileInfo) {
		JSONObject object = JSONHelper.string2json(fileInfo);
		object.put("filetype", 0);
		if (!object.containsKey("fatherid")) {
			object.put("fatherid", 0);
		}else{
			object.put("fatherid", Integer.parseInt(String.valueOf(object.get("fatherid"))));
		}
		_obj.put("records", StringEscapeUtils.unescapeJava(fileModel.addfile(object)));
		return fileModel.resultmsg(0,_obj.toString());
	}
//	public String Rename(String fid,String fileInfo) {
//		return fileModel.resultmsg(fileModel.Rename(fid,fileInfo), "文件重命名成功！");
//	}
	public String FindFile(String fileInfo) {
		_obj.put("records", fileModel.find(JSONHelper.string2json(fileInfo)));
		return fileModel.resultmsg(0, _obj.toString());
	}
	public String ShowFileByType(String typeid) {
		_obj.put("records", fileModel.show(typeid));
		return fileModel.resultmsg(0, _obj.toString());
	}
	public String Delete(String fileId) {
		return fileModel.resultmsg(fileModel.delete(fileId), "删除成功");
	}
	public String BatchDelete(String fileId) {
		return fileModel.resultmsg(fileModel.delete(fileId.split(",")), "删除成功");
	}
	public String Page(int idx,int pageSize) {
		_obj.put("records", fileModel.page(idx, pageSize));
		return fileModel.resultmsg(0, _obj.toString());
	}
	
	public String PageBy(int idx,int pageSize,String fileInfo) {
		_obj.put("records", fileModel.page(idx, pageSize, fileInfo));
		return fileModel.resultmsg(0, _obj.toString());
	}
	public String FileUpdate(String id,String fileInfo){
		return fileModel.resultmsg(fileModel.update(id, fileInfo), "文件或文件夹修改成功");
	}
	public String FileUpdateBatch(String fids,String folderid){
		if (fids.contains(",")) {
			return fileModel.resultmsg(fileModel.update(fids.split(","), folderid), "移动到文件夹成功");
		}else{
			return fileModel.resultmsg(fileModel.updates(fids, folderid), "移动到文件夹成功");
		}
	}
	public String getWord(String fid){
		JSONObject object = fileModel.find(fid);
		new fileconvert().office2html(object.get("filepath").toString(), "e://test");
		String words = new fileconvert().office2htmlString(object.get("filepath").toString(), "e://test");
//		String words="putao520";
		_obj.put("records", words);
		return fileModel.resultmsg(0, _obj.toString());
		
//		return null;
	}
}
