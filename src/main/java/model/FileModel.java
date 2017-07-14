package model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import JGrapeSystem.jGrapeFW_Message;
import apps.appsProxy;
import check.formHelper;
import check.formHelper.formdef;
import database.DBHelper;
import database.db;
import json.JSONHelper;
import string.StringHelper;

public class FileModel {
	private static DBHelper file;
	private static formHelper form;
	private JSONObject _obj = new JSONObject();

	static {
		file = new DBHelper(appsProxy.configValue().get("db").toString(),
				"file", "_id");
		form = file.getChecker();
	}

	private db bind() {
		return file.bind(String.valueOf(appsProxy.appid()));
	}

	public FileModel() {
		form.putRule("fileoldname", formdef.notNull);
	}

	// 新增文件夹
	public String add(JSONObject files) {
		if (!form.checkRuleEx(files)) {
			return resultmsg(1, "必填项为空");
		}
		String info = bind().data(files).insertOnce().toString();
		return find(info).toString();
	}

	// 修改文件信息
	public String update(String fid, JSONObject FileInfo) {
		int code = bind().eq("_id", new ObjectId(fid)).data(FileInfo)
				.update() != null ? 0 : 99;
		if (code != 0) {
			return resultmsg(code, "操作失败");
		}
		return find(fid).toString();
	}

	// 整合单个文件修改及批量修改
	public int updates(String fids, JSONObject FileInfo) {
		if (fids.contains(",")) {
			bind().or();
			String[] value = fids.split(",");
			for (int i = 0, len = value.length; i < len; i++) {
				bind().eq("_id", new ObjectId(value[i]));
			}
		} else {
			bind().eq("_id", new ObjectId(fids));
		}
		return bind().data(FileInfo).updateAll() != 0 ? 0 : 99;
	}

	// id查询文件或文件夹信息
	public JSONObject find(String fid) {
		return bind().eq("_id", new ObjectId(fid)).find();
	}

	@SuppressWarnings("unchecked")
	public JSONObject GetFile(String fid) {
		String[] value = fid.split(",");
		db db = bind().or();
		for (String tempid : value) {
			if (tempid.equals("")) {
				continue;
			}
			db.eq("_id", new ObjectId(tempid));
		}
		JSONArray array = db.select();
		JSONObject object;
		JSONObject objId;
		JSONObject rObject = new JSONObject();
		if (array != null && array.size() != 0) {
			for (Object object2 : array) {
				object = (JSONObject) object2;
				objId = (JSONObject) object.get("_id");
				rObject.put( objId.getString("$oid") , object);
			}
		}
		return rObject;
	}
	// 获取某个文件夹下所有文件的大小
	public int getSize(JSONArray array) {
		int size = 0;
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject object = (JSONObject) array.get(i);
			size += Integer.parseInt(object.get("size").toString());
		}
		return size;
	}

	// json条件查询文件或文件夹信息
	public JSONArray find(JSONObject fileInfo) {
		if (fileInfo.containsKey("isdelete")) {
			bind().eq("isdelete", 0);
		}
		for (Object object2 : fileInfo.keySet()) {
			bind().like(object2.toString(), fileInfo.get(object2.toString()));
		}
		return bind().limit(20).select();
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int ids, int pageSize, JSONObject fileInfo) {
		if (!fileInfo.containsKey("isdelete")) {
			bind().eq("isdelete", 0);
		}
		for (Object object2 : fileInfo.keySet()) {
			bind().eq(object2.toString(), fileInfo.get(object2.toString()));
		}
		JSONArray array = bind().dirty().page(ids, pageSize);
		long count = bind().dirty().count();
		JSONObject object = new JSONObject();
		object.put("totalSize",
				(int) Math.ceil((double) bind().count() / pageSize));
		bind().clear();
		object.put("currentPage", ids);
		object.put("pageSize", pageSize);
		object.put("total", count);
		object.put("data", array);
		return object;
	}

	// 存入回收站，从回收站还原
	public int RecyBatch(String fid, JSONObject FileInfo) {
		if (!fid.contains(",")) {
			if (isfile(fid) == 0) {
				// 判断该文件夹下是否有文件
				fid = getfid(fid);
			}
		} else {
			fid = Batch(fid.split(","));
		}
		return updates(fid, FileInfo);
	}

	// 多个数据操作
	private String Batch(String[] fids) {
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0, len = fids.length; i < len; i++) {
			if (isfile(fids[i]) == 0) {
				// 判断该文件夹下是否有文件
				list.add(getfid(fids[i]));
			} else {
				list.add(fids[i]);
			}
		}
		return StringHelper.join(list);
	}

	// 判断是否为文件
	private int isfile(String fid) {
		int ckcode = 0;
		String type = find(fid).get("filetype").toString();
		if ("0".equals(type)) {
			ckcode = 0; // 文件夹
		} else {
			ckcode = 1; // 文件
		}
		return ckcode;
	}

	// 判断该文件夹下是否有文件，返回所有的id，包含文件夹id
	private String getfid(String fid) {
		ArrayList<String> list = new ArrayList<>();
		String cond = "{\"fatherid\":\"" + fid + "\"" + "}";
		JSONArray array = find(JSONHelper.string2json(cond));
		if (array.size() != 0) { // 判断文件夹是否包含文件
			for (int i = 0, lens = array.size(); i < lens; i++) {
				JSONObject object = (JSONObject) array.get(i);
				JSONObject object2 = (JSONObject) object.get("_id");
				list.add(object2.get("$oid").toString());
			}
		}
		list.add(fid);
		return StringHelper.join(list);
	}

	// 删除文件[包含批量删除]
	private int delete(String fid) {
		if (fid.contains(",")) {
			bind().or();
			String[] value = fid.split(",");
			for (int i = 0, len = value.length; i < len; i++) {
				bind().eq("_id", new ObjectId(value[i]));
			}
		} else {
			bind().eq("_id", new ObjectId(fid));
		}
		return bind().deleteAll() != 0 ? 0 : 99;
	}

	public int ckDelete(String fid) {
		if (!fid.contains(",")) {
			if (isfile(fid) == 0) {
				deleteall(fid);
			}
		} else {
			String[] value = fid.split(",");
			ArrayList<String> list = new ArrayList<>();
			for (int i = 0, len = value.length; i < len; i++) {
				if (isfile(value[i]) == 0) {
					// 判断该文件夹下是否有文件
					list.add(value[i]);
				}
			}
			if (list.size() != 0) {
				deleteall(StringHelper.join(list));
			}
		}
		return delete(fid);
	}

	public int delete(JSONObject object) {
		int code = 0;
		long size = (long) object.get("size");
		if (object.containsKey("isdelete")) {
			code = ckDelete(object.get("_id").toString());
		}
		if (size > 4 * 1024 * 1024 * 1024) {
			code = ckDelete(object.get("_id").toString());
		} else {
			String infos = "{\"isdelete\":1}";
			code = RecyBatch(object.get("_id").toString(),
					JSONHelper.string2json(infos));
		}
		return code;
	}

	public int batch(JSONArray array) {
		int code = 0;
		boolean flag = false;
		List<String> list = new ArrayList<>();
		List<String> lists = new ArrayList<>();
		long FIXSIZE = new Long((long) 4 * 1024 * 1024 * 1024);
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject object = (JSONObject) array.get(i);
			if (object.containsKey("isdelete")) {
				flag = true;
				list.add(object.get("_id").toString());
			} else {
				if (object.get("size").toString() == null) {
					lists.add(object.get("_id").toString());
				} else {
					if ((long) object.get("size") > FIXSIZE) {
						flag = true;
						list.add(object.get("_id").toString());
					} else {
						lists.add(object.get("_id").toString());
					}
				}
			}
		}
		if (flag) {
			code = ckDelete(StringHelper.join(list));
		}
		if (lists.size() != 0) {
			String infos = "{\"isdelete\":1}";
			code = RecyBatch(StringHelper.join(lists),
					JSONHelper.string2json(infos));
		}
		return code;
	}

	private void deleteall(String fid) {
		if (fid.contains(",")) {
			bind().or();
			String[] value = fid.split(",");
			for (int i = 0, len = value.length; i < len; i++) {
				bind().eq("fatherid", value[i]);
			}
		} else {
			bind().eq("fatherid", fid);
		}
		bind().deleteAll();
	}

	@SuppressWarnings("unchecked")
	public String resultMessage(JSONObject object) {
		_obj.put("records", object);
		return resultmsg(0, _obj.toString());
	}

	@SuppressWarnings("unchecked")
	public String resultMessage(JSONArray array) {
		_obj.put("records", array);
		return resultmsg(0, _obj.toString());
	}

	public String resultmsg(int num, String mString) {
		String msg = "";
		switch (num) {
		case 0:
			msg = mString;
			break;
		case 1:
			msg = "必填项为空";
			break;
		case 2:
			msg = "没有创建数据权限，请联系管理员进行权限调整";
			break;
		case 3:
			msg = "没有修改数据权限，请联系管理员进行权限调整";
			break;
		case 4:
			msg = "没有删除数据权限，请联系管理员进行权限调整";
			break;
		default:
			msg = "其他异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}
