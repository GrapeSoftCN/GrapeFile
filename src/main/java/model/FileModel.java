package model;

import java.util.Set;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import esayhelper.DBHelper;
import esayhelper.JSONHelper;
import esayhelper.jGrapeFW_Message;

public class FileModel {
	private static DBHelper file = (DBHelper) new DBHelper("mongodb", "file");

	public String addfile(JSONObject folder) {
		String info = file.data(folder).insertOnce().toString();
		return find(info).toString();
	}

	// 根据_id查询文件夹信息
	public JSONObject find(String fid) {
		return file.eq("_id", new ObjectId(fid)).find();
	}

	public int Rename(String fid, String fileInfo) {
		JSONObject object = JSONHelper.string2json(fileInfo);
		return file.eq("_id", new ObjectId(fid)).data(object).update() != null ? 0 : 99;
	}

	public int update(String fid, String fileInfo) {
		JSONObject object = JSONHelper.string2json(fileInfo);
		return file.eq("_id", new ObjectId(fid)).data(object).update() != null ? 0 : 99;
	}

	@SuppressWarnings("unchecked")
	public int update(String[] fids, String folderid) {
		file = (DBHelper) file.or();
		for (int i = 0; i < fids.length; i++) {
			file.eq("_id", new ObjectId(fids[i]));
		}
		JSONObject object = new JSONObject();
		object.put("fatherid", folderid);
		return file.data(object).update() != null ? 0 : 99;
	}

	@SuppressWarnings("unchecked")
	public int updates(String fids, String folderid) {
		JSONObject object = new JSONObject();
		object.put("fatherid", folderid);
		return file.eq("_id", new ObjectId(fids)).data(object).update()!=null?0:99;
	}

	// public int update(String jsonarray){
	// JSONObject object;
	// String folderid = "";
	// file = (DBHelper) file.or();
	// JSONArray array = (JSONArray) JSONValue.parse(jsonarray);
	// for (int i = 0; i < array.size(); i++) {
	// object = (JSONObject) array.get(i);
	// if (object.containsKey("_id")) {
	// file.eq("_id", new ObjectId(object.get("_id").toString()));
	// }
	// }
	// return file.eq("_id", new ObjectId(fid)).data(object).update()!=null?0:99;
	// }
	// public JSONArray show() {
	// return file.select();
	// }
	public JSONArray find(JSONObject fileInfo) {
		@SuppressWarnings("unchecked")
		Set<Object> set = fileInfo.keySet();
		for (Object object2 : set) {
			file.eq(object2.toString(), fileInfo.get(object2.toString()));
		}
		return file.select();
	}

	public JSONArray show(String typeid) {
		return file.eq("type", typeid).select();
	}

	public int delete(String fileId) {
		return file.eq("_id", new ObjectId(fileId)).delete() != null ? 0 : 99;
	}

	public int delete(String[] fileId) {
		file = (DBHelper) file.or();
		for (int i = 0; i < fileId.length; i++) {
			file.eq("_id", new ObjectId(fileId[i]));
		}
		return file.delete() != null ? 0 : 99;
	}

	// public int deleteUpdate(String fileId) {
	// String jsonString = "{\"isdelete\":\"1\"}";
	// return file.update(new ObjectId(fileId), jsonString)==true?0:99;
	// }
	public String page(int idx, int pageSize) {
		JSONArray array = file.page(idx, pageSize);
		@SuppressWarnings("unchecked")
		JSONObject object = new JSONObject() {
			private static final long serialVersionUID = 1L;

			{
				put("totalSize", (int) Math.ceil((double) file.count() / pageSize));
				put("currentPage", idx);
				put("pageSize", pageSize);
				put("data", array);

			}
		};
		return object.toString();
	}

	public JSONObject page(int idx, int pageSize, String fileInfo) {
		@SuppressWarnings("unchecked")
		Set<Object> set = JSONHelper.string2json(fileInfo).keySet();
		for (Object object2 : set) {
			file.eq(object2.toString(), JSONHelper.string2json(fileInfo).get(object2
					.toString()));
		}
		JSONArray array = file.page(idx, pageSize);
		@SuppressWarnings("unchecked")
		JSONObject object = new JSONObject() {
			private static final long serialVersionUID = 1L;

			{
				put("totalSize", (int) Math.ceil((double) file.count() / pageSize));
				put("currentPage", idx);
				put("pageSize", pageSize);
				put("data", array);

			}
		};
		return object;
	}

	public String getID() {
		String str = UUID.randomUUID().toString();
		return str.replace("-", "");
	}

	public String resultmsg(int num, String mString) {
		String msg = "";
		switch (num) {
		case 0:
			msg = mString;
			break;

		default:
			msg = "其他异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}
