package com.gwssi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndicatorUtil {

	/**
	 * 获取List列表中第index个Map映射中key所对应的值
	 * 
	 * @param list
	 *            列表
	 * @param index
	 *            第几个
	 * @param key
	 *            关键字
	 * @return
	 */
	public static Object getValueByKey(List<Map<String, Object>> list,
			int index, String key) {
		Object value = null;
		if ((list != null) && (list.size() > index)) {
			Map<String, Object> map = list.get(index);
			if (map.containsKey(key)) {
				value = map.get(key);
			}
		}
		return value;
	}

	/**
	 * 根据提供的关键属性，对其他属性进行映射，如下所示：<br/>
	 * &nbsp;&nbsp;pA1<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;(pB11,pC11,pD11)<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;(pB12,pC12,pD12)<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;(pB13,pC13,pD13)<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;(pB14,pC14,pD14)<br/>
	 * &nbsp;&nbsp;pA2<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;(pB21,pC21,pD21)<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;(pB22,pC22,pD22)<br/>
	 * 
	 * @param list
	 * @param propertyA
	 * @param propertyBArr
	 * @return
	 */
	public static Map<String, List<Map<String, Object>>> mergeList(
			List<Map<String, Object>> list, String propertyA,
			String[] propertyBArr) {
		List<Map<String, Object>> itemList = null;

		Object tmpPropertyAValue = null;
		Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();

		for (int i = 0, size = list.size(); i < size; i++) {
			Object propertyAValue = IndicatorUtil.getValueByKey(list, i,
					propertyA);
			if (!propertyAValue.equals(tmpPropertyAValue)) {
				itemList = new ArrayList<Map<String, Object>>();
				resultMap.put(String.valueOf(propertyAValue), itemList);
				tmpPropertyAValue = propertyAValue;
			}
			Map<String, Object> dataMap = new HashMap<String, Object>();
			for (String element : propertyBArr) {
				dataMap.put(element,
						IndicatorUtil.getValueByKey(list, i, element));
			}
			itemList.add(dataMap);
		}
		return resultMap;
	}

	/**
	 * 将Object对象转化为String对象
	 * 
	 * @param object
	 * @return
	 */
	public static String objectToString(Object object) {
		String result = null;
		if (object != null) {
			result = String.valueOf(object);
		}
		return result;
	}

	/**
	 * 获取类似In的Sql语句 如： property:zb.zb_id and (zb.zb_id = '000001' or zb.zb_id =
	 * '000002')
	 * 
	 * @param arr
	 *            数据数组
	 * @param property
	 *            指定的字符串
	 * @return
	 */
	public static StringBuffer getInSql(String[] arr, String property) {
		StringBuffer sqlSb = new StringBuffer();
		if (arr != null) {
			for (int i = 0, len = arr.length; i < len; i++) {
				if (i == 0) {
					sqlSb.append(" AND (");
				} else {
					sqlSb.append(" OR ");
				}
				sqlSb.append(property).append(" = '").append(arr[i])
						.append("'");
				if (i == (len - 1)) {
					sqlSb.append(")");
				}
			}
		}
		return sqlSb;
	}
}
