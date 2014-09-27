package com.gwssi.service;

import java.util.List;
import java.util.Map;

import com.gwssi.model.TreeNode;

public interface IndicatorService {

	public List<TreeNode> getFbzd(String bgqbDm, String fbzdFjd);

	public List<TreeNode> getIndicator(String bgqbDm, String kind, String id);

	public List<Map<String, Object>> getTablesByIndicator(String[] zbIdArr);

	public List<String> getZbfzglByIndicator(String zbId);

	public List<Map<String, Object>> getFzFzxZdyx(String sjbm,
			List<String> zbfzgls);

	public List<Map<String, Object>> getIndicatorSj(String sql);
}
