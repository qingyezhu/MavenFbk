package com.gwssi.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gwssi.model.TreeNode;
import com.gwssi.service.IndicatorService;
import com.gwssi.util.IndicatorUtil;

@Controller
@RequestMapping("/Indicator")
public class IndicatorController {

	private IndicatorService indicatorService;

	public IndicatorService getIndicatorService() {
		return this.indicatorService;
	}

	@Resource
	public void setIndicatorService(IndicatorService indicatorService) {
		this.indicatorService = indicatorService;
	}

	@RequestMapping("/indicator")
	public ModelAndView toIndicator() {
		return new ModelAndView("indicator");
	}

	@RequestMapping("/getTree")
	@ResponseBody
	public List<TreeNode> getTree(String lv, String kind, String bgqbDm,
			String id) {
		List<TreeNode> treeNodes = null;
		if ((null == lv) || "0".equals(lv)) {
			treeNodes = this.indicatorService.getFbzd(bgqbDm, id);
		} else {
			treeNodes = this.indicatorService.getIndicator(bgqbDm, kind, id);
		}
		return treeNodes;
	}

	@RequestMapping("/getIndicatorSj")
	@ResponseBody
	public Map<String, Object> getIndicatorSj(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		this.getIndicatorSj(request, dataList);
		result.put("recordsTotal", dataList.size());
		result.put("recordsFiltered", dataList.size());
		result.put("data", dataList);
		return result;
	}

	private void getIndicatorSj(HttpServletRequest request,
			List<Map<String, Object>> dataList) {
		String zbIds = request.getParameter("zbIds");
		if ((zbIds != null) && !"".equals(zbIds)) {
			String[] zbIdArr = zbIds.split(",");
			String bgqbDm = request.getParameter("bgqbDm");
			String[] bgqArr = this.getBgqs(bgqbDm);
			this.getIndicatorSj(zbIdArr, bgqArr, dataList);
		}
	}

	private void getIndicatorSj(String[] zbIdArr, String[] bgqArr,
			List<Map<String, Object>> dataList) {

		List<Map<String, Object>> zbTables = this.indicatorService
				.getTablesByIndicator(zbIdArr);
		System.out.println(zbTables);
		Map<String, Object> zbTableMap = this.listToMap(zbTables);
		System.out.println(zbTableMap);
		String xzqyNm = ResourceBundle.getBundle("app").getString("xzqy");
		for (String zbId : zbIdArr) {
			String sjbm = String.valueOf(zbTableMap.get(zbId));
			this.getIndicatorSj(zbId, sjbm, bgqArr, xzqyNm, dataList);
		}
	}

	private void getIndicatorSj(String zbId, String sjbm, String[] bgqArr,
			String xzqyNm, List<Map<String, Object>> dataList) {
		Map<String, Object> fzxMap = new HashMap<String, Object>();
		HashMap<String, HashSet<HashMap<String, HashSet<HashMap<String, Object>>>>> zbfzglMap = new HashMap<String, HashSet<HashMap<String, HashSet<HashMap<String, Object>>>>>();
		this.handlerFzFzxZdyx(zbId, sjbm, fzxMap, zbfzglMap);

		StringBuffer sqlSb = new StringBuffer();

		StringBuffer selectSql = new StringBuffer();
		selectSql
				.append("SELECT DISTINCT SJ.SJ_Z,SJ.BGQ_DM,SJ.SJ_BGQMC,SJ.XZQY_NM,");
		selectSql
				.append("SJ.SJ_DQMC,ZB.ZB_CN_QC ZB_MC,JLDW.JLDW_CN_MC SJ_DWMC,SJ.FHCC");

		StringBuffer fromSql = new StringBuffer();
		fromSql.append("FROM ").append(sjbm).append(" SJ");
		fromSql.append(" LEFT JOIN FBK_ZB ZB ON ZB.ZB_ID = SJ.SJ_ID");
		fromSql.append(" LEFT JOIN FBK_JLDW JLDW ON JLDW.JLDW_DM = SJ.SJ_DW");

		StringBuffer whereSql = new StringBuffer();
		whereSql.append(IndicatorUtil.getInSql(bgqArr, "SJ.BGQ_DM"));
		whereSql.append(" AND SJ.XZQY_NM = '").append(xzqyNm).append("'");
		whereSql.append(" AND SJ.SJ_ID = '").append(zbId).append("'");

		this.spellSql(zbfzglMap, selectSql, fromSql, whereSql, sqlSb);
		String[] sqlArr = null;
		if (sqlSb.length() == 0) {
			sqlSb.append(selectSql).append(" ").append(fromSql)
					.append(" WHERE 1 = 1 AND SJ.FHCC = '0'").append(whereSql)
					.append(" ORDER BY SJ.XZQY_NM, SJ.BGQ_DM DESC");
		}
		sqlArr = sqlSb.toString().split("UNION ALL");

		List<Map<String, Object>> sjList = new ArrayList<Map<String, Object>>();
		this.getIndicatorSj(sqlArr, sjList);

		List<String> zdyxList = new ArrayList<String>();
		Map<String, Object> mcMap = new HashMap<String, Object>();
		Map<String, Object> sjMap = new HashMap<String, Object>();

		this.handlerIndicatorSj(sjList, fzxMap, zdyxList, mcMap, sjMap);

		this.transformJSON(zdyxList, mcMap, sjMap, bgqArr, xzqyNm, dataList);

	}

	private void transformJSON(List<String> zdyxList,
			Map<String, Object> mcMap, Map<String, Object> sjMap,
			String[] bgqArr, String xzqyNm, List<Map<String, Object>> dataList) {
		for (int i = 0, size = zdyxList.size(); i < size; i++) {
			String zdyx = String.valueOf(zdyxList.get(i));
			StringBuffer mcKeySb = new StringBuffer();
			mcKeySb.append(xzqyNm).append("_").append(zdyx);
			String mc = String.valueOf(mcMap.get(mcKeySb.toString()));
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("ZB_MC", mc);
			for (String bgqDm : bgqArr) {
				StringBuffer sjKeySb = new StringBuffer();
				sjKeySb.append(xzqyNm).append("_").append(zdyx).append("_")
						.append(bgqDm);
				String sjZ = null;
				if (sjMap.containsKey(sjKeySb.toString())) {
					sjZ = String.valueOf(sjMap.get(sjKeySb.toString()));
				} else {
					sjZ = "";
				}
				dataMap.put(bgqDm, sjZ);
			}
			dataList.add(dataMap);
		}
	}

	private void handlerIndicatorSj(List<Map<String, Object>> sjList,
			Map<String, Object> fzxMap, List<String> zdyxList,
			Map<String, Object> mcMap, Map<String, Object> sjMap) {
		String tmpZdyx = null;

		for (int i = 0, size = sjList.size(); i < size; i++) {
			sjList.get(i);
			String zbMc = IndicatorUtil.objectToString(IndicatorUtil
					.getValueByKey(sjList, i, "ZB_MC"));
			String bgqDm = IndicatorUtil.objectToString(IndicatorUtil
					.getValueByKey(sjList, i, "BGQ_DM"));
			String xzqyNm = IndicatorUtil.objectToString(IndicatorUtil
					.getValueByKey(sjList, i, "XZQY_NM"));
			String sjdwMc = IndicatorUtil.objectToString(IndicatorUtil
					.getValueByKey(sjList, i, "SJ_DWMC"));
			String zdyxStr = IndicatorUtil.objectToString(IndicatorUtil
					.getValueByKey(sjList, i, "ZDYX"));
			String sjZ = IndicatorUtil.objectToString(IndicatorUtil
					.getValueByKey(sjList, i, "SJ_Z"));

			StringBuffer zdyxSb = new StringBuffer();
			if (null != zdyxStr) {
				String[] zdyxArr = zdyxStr.split(",");
				for (String element : zdyxArr) {
					zdyxSb.append("_").append(
							String.valueOf(fzxMap.get(element)));
				}
			}
			String zdyx = zdyxSb.toString();
			if (!zdyx.equals(tmpZdyx)) {
				zdyxList.add(zdyx);
				tmpZdyx = zdyx;
			}
			StringBuffer mcSb = new StringBuffer();
			mcSb.append(zbMc).append(zdyx).append("(").append(sjdwMc)
					.append(")");

			StringBuffer mcKeySb = new StringBuffer();
			mcKeySb.append(xzqyNm).append("_").append(tmpZdyx);
			mcMap.put(mcKeySb.toString(), mcSb.toString());

			StringBuffer sjKeySb = new StringBuffer();
			sjKeySb.append(xzqyNm).append("_").append(tmpZdyx).append("_")
					.append(bgqDm);
			sjMap.put(sjKeySb.toString(), sjZ);
		}
	}

	private void getIndicatorSj(String[] sqlArr,
			List<Map<String, Object>> sjList) {
		for (String sql : sqlArr) {
			List<Map<String, Object>> list = this.indicatorService
					.getIndicatorSj(sql);
			if ((list != null) && (list.size() != 0)) {
				sjList.addAll(list);
			}
		}
	}

	private void spellSql(
			HashMap<String, HashSet<HashMap<String, HashSet<HashMap<String, Object>>>>> zbfzglMap,
			StringBuffer selectSql, StringBuffer fromSql,
			StringBuffer whereSql, StringBuffer sqlSb) {
		int iIndex = 0;
		for (String zbfzglId : zbfzglMap.keySet()) {
			HashSet<HashMap<String, HashSet<HashMap<String, Object>>>> zbfzglSet = zbfzglMap
					.get(zbfzglId);
			StringBuffer tmpSelectSb = new StringBuffer();
			tmpSelectSb.append(selectSql);
			StringBuffer tmpFromSb = new StringBuffer();
			tmpFromSb.append(fromSql);
			StringBuffer tmpWhereSb = new StringBuffer();
			tmpWhereSb.append(" WHERE 1 = 1");
			int jIndex = 0, jSize = zbfzglSet.size();
			for (HashMap<String, HashSet<HashMap<String, Object>>> fzMap : zbfzglSet) {
				for (String fzId : fzMap.keySet()) {
					HashSet<HashMap<String, Object>> itemSet = fzMap.get(fzId);
					int kIndex = 0, kSize = itemSet.size();
					String zdyx = null;
					for (HashMap<String, Object> itemMap : itemSet) {
						String fzxId = String.valueOf(itemMap.get("fzx_id"));
						zdyx = String.valueOf(itemMap.get("dycclm"));
						if (kIndex == 0) {
							tmpWhereSb.append(" AND (");
						} else {
							tmpWhereSb.append(" OR ");
						}
						tmpWhereSb.append(" SJ.").append(zdyx).append("='")
								.append(fzxId).append("'");
						if (kIndex == (kSize - 1)) {
							tmpWhereSb.append(")");
						}
						kIndex++;
					}

					if (jIndex == 0) {
						tmpSelectSb.append(",");
					} else {
						tmpSelectSb.append("||','||");
					}
					tmpSelectSb.append("").append(zdyx);
					if (jIndex == (jSize - 1)) {
						tmpSelectSb.append(" ZDYX ");
					}
					jIndex++;
				}
			}
			tmpWhereSb.append(" AND SJ.FHCC = ").append(jSize).append(whereSql);
			if (iIndex > 0) {
				sqlSb.append(" UNION ALL ");
			}
			sqlSb.append(tmpSelectSb).append(tmpFromSb).append(tmpWhereSb);
			sqlSb.append(" ORDER BY SJ.FHCC, ZDYX, SJ.XZQY_NM, SJ.BGQ_DM DESC ");
			iIndex++;
		}
	}

	private void handlerFzFzxZdyx(
			String zbId,
			String sjbm,
			Map<String, Object> fzxMap,
			HashMap<String, HashSet<HashMap<String, HashSet<HashMap<String, Object>>>>> zbfzglMap) {
		List<String> zbfzgls = this.indicatorService.getZbfzglByIndicator(zbId);
		List<Map<String, Object>> fzfzxzdyxList = null;
		if (zbfzgls.size() < -1) {
			fzfzxzdyxList = this.indicatorService.getFzFzxZdyx(sjbm, zbfzgls);
		} else {
			fzfzxzdyxList = Collections.emptyList();
		}
		this.spellFzFzxZdyx(fzfzxzdyxList, zbfzglMap, fzxMap);
	}

	private void spellFzFzxZdyx(
			List<Map<String, Object>> fzfzxzdyxList,
			Map<String, HashSet<HashMap<String, HashSet<HashMap<String, Object>>>>> result,
			Map<String, Object> fzxMap) {
		HashSet<HashMap<String, HashSet<HashMap<String, Object>>>> zbfzglSet = new HashSet<HashMap<String, HashSet<HashMap<String, Object>>>>();
		HashMap<String, HashSet<HashMap<String, Object>>> fzMap = new HashMap<String, HashSet<HashMap<String, Object>>>();
		HashSet<HashMap<String, Object>> itemSet = new HashSet<HashMap<String, Object>>();
		String tmpFzId = null;
		for (int i = 0, size = fzfzxzdyxList.size(); i < size; i++) {
			fzfzxzdyxList.get(i);
			String zbfzglId = IndicatorUtil.objectToString(IndicatorUtil
					.getValueByKey(fzfzxzdyxList, i, "zbfzglid"));
			String fzId = IndicatorUtil.objectToString(IndicatorUtil
					.getValueByKey(fzfzxzdyxList, i, "fz_id"));
			String fzxId = IndicatorUtil.objectToString(IndicatorUtil
					.getValueByKey(fzfzxzdyxList, i, "fzx_id"));
			String fzxMc = IndicatorUtil.objectToString(IndicatorUtil
					.getValueByKey(fzfzxzdyxList, i, "fzx_cn_qc"));
			String zdyx = IndicatorUtil.objectToString(IndicatorUtil
					.getValueByKey(fzfzxzdyxList, i, "dycclm"));
			HashMap<String, Object> itemMap = new HashMap<String, Object>();
			itemMap.put("fzx_id", fzxId);
			itemMap.put("fzx_mc", fzxMc);
			itemMap.put("dycclm", zdyx);

			if (result.containsKey(zbfzglId)) {
				if (!fzId.equals(tmpFzId)) {
					fzMap = new HashMap<String, HashSet<HashMap<String, Object>>>();
					itemSet = new HashSet<HashMap<String, Object>>();
					zbfzglSet.add(fzMap);
				}
			} else {
				zbfzglSet = new HashSet<HashMap<String, HashSet<HashMap<String, Object>>>>();
				fzMap = new HashMap<String, HashSet<HashMap<String, Object>>>();
				itemSet = new HashSet<HashMap<String, Object>>();
				zbfzglSet.add(fzMap);
			}
			itemSet.add(itemMap);
			fzMap.put(fzId, itemSet);
			result.put(zbfzglId, zbfzglSet);
			tmpFzId = fzId;

			if (!fzxMap.containsKey(fzxId)) {
				fzxMap.put(fzxId, fzxMc);
			}

		}
	}

	private Map<String, Object> listToMap(List<Map<String, Object>> list) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (int i = 0, len = list.size(); i < len; i++) {
			Map<String, Object> map = list.get(i);
			result.put(String.valueOf(map.get("zb_id")), map.get("fbzd_htccb"));
		}
		return result;
	}

	private String[] getBgqs(String bgqbDm) {
		String[] bgqArr = null;
		if ("1".equals(bgqbDm)) {
			bgqArr = new String[] { "20070000", "20080000", "20090000",
					"20100000" };
		} else if ("3".equals(bgqbDm)) {
			bgqArr = new String[] { "20111000", "20112000", "20113000",
					"20114000" };
		} else if ("4".equals(bgqbDm)) {
			bgqArr = new String[] { "20120040", "20120030", "20120020",
					"20120010" };
		}
		return bgqArr;
	}

}
