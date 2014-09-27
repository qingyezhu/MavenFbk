package com.wangzhu.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.gwssi.service.IndicatorService;
import com.gwssi.util.IndicatorUtil;

//表示继承了SpringJUnit4ClassRunner类
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml",
		"classpath:spring-mybatis.xml" })
public class IndicatorServiceTest {

	private static Logger logger = Logger.getLogger(IndicatorServiceTest.class);
	@Resource
	private IndicatorService indicatorService;

	// @Before
	// public void before() {
	// final ApplicationContext context = new ClassPathXmlApplicationContext(
	// new String[] { "classpath:spring.xml",
	// "classpath:spring-mybatis.xml" });
	// this.indicatorService = (IndicatorService) context
	// .getBean("indicatorServiceImpl");
	// }

	@Test
	public void testIndicatorService() {
		// String sql =
		// "SELECT DISTINCT SJ.SJ_Z,SJ.BGQ_DM,SJ.SJ_BGQMC,SJ.XZQY_NM,SJ.SJ_DQMC,ZB.ZB_CN_QC ZB_MC,JLDW.JLDW_CN_MC SJ_DWMC,SJ.FHCC FROM FBK_ZBCC_SJ01 SJ LEFT JOIN FBK_ZB ZB ON ZB.ZB_ID = SJ.SJ_ID LEFT JOIN FBK_JLDW JLDW ON JLDW.JLDW_DM = SJ.SJ_DW WHERE 1 = 1 AND SJ.FHCC = '0' AND (SJ.BGQ_DM='20070000' OR SJ.BGQ_DM='20080000' OR SJ.BGQ_DM='20090000' OR SJ.BGQ_DM='20100000') AND SJ.XZQY_NM = '530100' AND SJ.SJ_ID = '010110200000000' ORDER BY SJ.XZQY_NM, SJ.BGQ_DM DESC";
		String sql = "select zbfzx.zbfzglid,zdxdygx.dycclm,fzfx.fz_id,fzfx.fzx_id,fzfx.fzx_cn_qc from fbk_fzfx fzfx,fbk_zbfzx zbfzx left join fbk_zdxdygx zdxdygx on (zdxdygx.dyfzid = zbfzx.fzid) where zbfzx.fzxid=fzfx.fzx_id and zbfzx.fzid = fzfx.fz_id and zdxdygx.sjbm = 'FBK_ZBCC_SJ03' and ( zbfzx.zbfzglid='100000001237') order by zbfzx.zbfzglid,fzfx.fz_id,fzfx.fzx_id";
		List<Map<String, Object>> list = this.indicatorService
				.getIndicatorSj(sql);
		IndicatorServiceTest.logger.debug(JSON.toJSONString(list));

		IndicatorServiceTest.logger.debug(JSON.toJSONString(IndicatorUtil
				.mergeList(list, "FZ_ID",
						new String[] { "FZX_ID", "FZX_CN_QC" })));

	}

}
