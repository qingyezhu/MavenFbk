package com.gwssi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gwssi.model.TreeNode;

public interface IndicatorDao {

	public List<TreeNode> getFbzd(@Param(value = "bgqbDm") String bgqbDm,
			@Param(value = "fbzdFjd") String fbzdFjd);;

	public List<TreeNode> getIndicator(@Param(value = "bgqbDm") String bgqbDm,
			@Param(value = "kind") String kind, @Param(value = "id") String id);

	public List<Map<String, Object>> getTablesByIndicator(
			@Param(value = "zbIdArr") String[] zbIdArr);

	public List<String> getZbfzglByIndicator(@Param(value = "zbId") String zbId);

	public List<Map<String, Object>> getFzFzxZdyx(
			@Param(value = "sjbm") String sjbm,
			@Param(value = "zbfzgls") List<String> zbfzgls);

	public List<Map<String, Object>> getIndicatorSj(
			@Param(value = "sql") String sql);
}
