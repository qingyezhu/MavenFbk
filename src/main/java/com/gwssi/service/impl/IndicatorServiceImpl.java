package com.gwssi.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gwssi.dao.IndicatorDao;
import com.gwssi.model.TreeNode;
import com.gwssi.service.IndicatorService;

@Service
public class IndicatorServiceImpl implements IndicatorService {

	@Autowired
	private IndicatorDao indicatorDao;

	@Override
	public List<TreeNode> getFbzd(String bgqbDm, String fbzdFjd) {
		return this.indicatorDao.getFbzd(bgqbDm, fbzdFjd);
	}

	@Override
	public List<TreeNode> getIndicator(String bgqbDm, String kind, String id) {
		return this.indicatorDao.getIndicator(bgqbDm, kind, id);
	}

	@Override
	public List<Map<String, Object>> getTablesByIndicator(String[] zbIdArr) {
		return this.indicatorDao.getTablesByIndicator(zbIdArr);
	}

	@Override
	public List<String> getZbfzglByIndicator(String zbId) {
		return this.indicatorDao.getZbfzglByIndicator(zbId);
	}

	@Override
	public List<Map<String, Object>> getFzFzxZdyx(String sjbm,
			List<String> zbfzgls) {
		return this.indicatorDao.getFzFzxZdyx(sjbm, zbfzgls);
	}

	@Override
	public List<Map<String, Object>> getIndicatorSj(String sql) {
		return this.indicatorDao.getIndicatorSj(sql);
	}

}
