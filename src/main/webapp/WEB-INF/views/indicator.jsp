<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String base = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="<%=base %>/js/datatable/css/jquery.dataTables.min.css"/>
<link rel="stylesheet" href="<%=base %>/js/ztree/css/zTreeStyle/zTreeStyle.css" />
<script type="text/javascript" src="<%=base %>/js/datatable/js/jquery.js"></script>
<script type="text/javascript" src="<%=base %>/js/datatable/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<%=base %>/js/ztree/js/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="<%=base %>/js/highcharts/highcharts.js"></script>
<script type="text/javascript">
var html = [];
html.push("<th>2010年</th><th>2009年</th><th>2008年</th><th>2007年</th>");
html.push("<th>2011年第4季度</th><th>2011年第3季度</th><th>2011年第2季度</th><th>2011年第1季度</th>");
html.push("<th>2004年12月</th><th>2004年11月</th><th>2004年10月</th><th>2004年09月</th>");

var url = "getIndicatorSj.do?zbIds=";
var dataTable;
var options = {
	"bPaginate" : false,
	"bJQueryUI" : true,
	"processing" : true,
	"serverSide" : true,
	"bDestroy" : true,
	"bAutoWidth" : true,
	"sAjaxSource" : url,
	"columns" : [ {
		"title":'指标名称',
		"data" : "ZB_MC",
		"width" : "30%"
	}, {
		"title":'2010年',
		"data" : "20100000"
	}, {
		"title":'2009年',
		"data" : "20090000"
	}, {
		"title":'2008年',
		"data" : "20080000"
	}, {
		"title":'2007年',
		"data" : "20070000"
	} ],
	"oLanguage" : {
		"sProcessing" : "正在加载中...",
		"sLengthMenu" : "每页显示_MENU_条记录",
		"sZeroRecords" : "对不起，查询不到相关数据！",
		"sEmptyTable" : "表中无数据存在",
		"sInfo" : "当前显示_START_ ～ _END_条，共_TOTAL_条记录",
		"sInfoEmpty" : "当前显示0 ～ 0条，共0条记录",
		"sInfoFiltered" : "数据表中共为_MAX_条记录",
		"sSearch" : "搜索",
		"oPaginate" : {
			"sFirst" : "首页",
			"sPrevious" : "上一页",
			"sNext" : "下一页",
			"sLast" : "末页"
		}
	}
};
var zTree;
var setting = {
		async : {
			enable : true,
			type : "post",
			url : "getTree.do",
			autoParam : [ "id", "level=lv", "kind" ],
			otherParam:{"bgqbDm":"1"}
		},
		callback : {
			onCheck : function() {

				$('#chartShowId').hide();
				var chkArr = zTree.getCheckedNodes(true);

				var zbIds = [];
				for(var i = 0,len = chkArr.length;i < len;i ++){
					zbIds.push(chkArr[i].id);
				}
				dataTable.fnSettings().sAjaxSource = url + zbIds.join(',') + "&bgqbDm=" + $("#bgqbSelectId").val();
				dataTable.fnDraw();
			}//,
			//onAsyncSuccess:zTreeOnAsyncSuccess
		},
		check : {
			enable : true
		},
		view : {
			showLine : false,
			showIcon : false
		}
	};

function zTreeOnAsyncSuccess(event,treeId,msg){
	var nodes = zTree.getNodes();
	expandNodes(nodes);
}
function expandNodes(nodes){
	if(!nodes){
		return;
	}
	var zTree = $.fn.zTree.getZTreeObj('zTreeId');
	var node = nodes[0];
	zTree.expandNode(node,true);
	if(node.isParent){
		expandNodes(node.children);
	}
	
	if(node.isLastNode){
		zTree.checkNode(node,true,true,true);
	}
}

var highchartsOptions = {
		chart:{
			renderTo:'container'
		},
		title:{
			text:'指标数据'
		},
		tooltip:{
			pointFormat:'{series.name}:<b>{point.y}</b>'
		},
		xAxis:{
			categories:[]
		},
		yAxis:{
			title:{
				text:'数值'
			}
		},
		series:[]
};
$(function(){
	dataTable = $('#example').dataTable(options);
	$('#example tbody').on('click','tr',function(){
		if($(this).hasClass('selected')){
			$(this).removeClass('selected');
			$('#chartShowId').hide();
		}else{
			dataTable.$('tr.selected').removeClass('selected');
			$(this).addClass('selected');
			var dataObj = [];
			var dataArr = [];
			var categories = [];
			for(var i = 1,len = options.aoColumns.length;i < len;i ++){
				var val = dataTable.fnGetData(this)[options.aoColumns[i].data];
				if(val != null && val != ''){
					val = Number(val);
				}else{
					val = null;
				}
				//dataArr.push([options.aoColumns[i].title,val]);
				dataArr.push(val);
				categories.push(options.aoColumns[i].title);
			}
			dataObj.push({
				name:dataTable.fnGetData(this)['ZB_MC'],
				data:dataArr
			});
			
			changeHighchartsOption(categories,dataObj);
		}
		
	});
	$("#bgqbSelectId").change(function() {
		$("#example tr th:not(:eq(0))").remove();
		var aoColumns = [];
		aoColumns.push({
			"data" : "ZB_MC",
			"width" : "40%"
		});
		switch (this.value) {
		case "1":
			$("#example tr th:eq(0)").after(html[0]);
			aoColumns.push({
				"title":'2010年',
				"data" : "20100000"
			});
			aoColumns.push({
				"title":'2009年',
				"data" : "20090000"
			});
			aoColumns.push({
				"title":'2008年',
				"data" : "20080000"
			});
			aoColumns.push({
				"title":'2007年',
				"data" : "20070000"
			});
			break;
		case "3":
			$("#example tr th:eq(0)").after(html[1]);
			aoColumns.push({
				"title":'2011年4季度',
				"data" : "20114000"
			});
			aoColumns.push({
				"title":'2011年3季度',
				"data" : "20113000"
			});
			aoColumns.push({
				"title":'2011年2季度',
				"data" : "20112000"
			});
			aoColumns.push({
				"title":'2011年1季度',
				"data" : "20111000"
			});
			break;
		case "4":
			$("#example tr th:eq(0)").after(html[2]);
			aoColumns.push({
				"title":'2012年04月',
				"data" : "20120040"
			});
			aoColumns.push({
				"title":'2012年03月',
				"data" : "20120030"
			});
			aoColumns.push({
				"title":'2012年02月',
				"data" : "20120020"
			});
			aoColumns.push({
				"title":'2012年01月',
				"data" : "20120010"
			});
			break;
		}
		options.aoColumns = aoColumns;
		dataTable = $('#example').dataTable(options);
		
		setting.async.otherParam.bgqbDm = this.value;
		zTree = $.fn.zTree.init($("#zTreeId"), setting);

		$('#chartShowId').hide();
	});
	zTree = $.fn.zTree.init($("#zTreeId"), setting);
	$("#bgqbSelectId option[value=1]").prop("selected",true);
	
	$('#chartTypeId').change(function(){
		newHighcharts(this.value);
	});

	$('#chartShowId').hide();
	
	
	$('#showChartId').click(function(){
		var data = dataTable.DataTable().rows().data();
		if(data.length == 0){
			alert('没有数据');
			return ;
		}
		var categories = [];
		for(var i = 1,len = options.aoColumns.length;i < len;i ++){
			categories.push(options.aoColumns[i].title);
		}
		
		var dataObj = [];
		for(var i = 0,iLen = data.length;i < iLen;i ++){
			var dataRow = data[i];
			var dataArr = [];
			for(var j = 1,jLen = options.aoColumns.length;j < jLen;j ++){
				var val = dataRow[options.aoColumns[j].data];
				if(val != null && val != ''){
					val = Number(val);
				}else{
					val = null;
				}
				//dataArr.push([options.aoColumns[j].title,val]);
				dataArr.push(val);
			}
			dataObj.push({
				name:dataRow['ZB_MC'],
				data:dataArr
			});
		}
		changeHighchartsOption(categories,dataObj);
	});
	
	/**
	*categories:[]
	*series:[
	*         {
	*			name:''
	*		  	data:[]
	*		  }
	*		]
	*/
	function changeHighchartsOption(categories,series){
		highchartsOptions.xAxis.categories = categories;
		highchartsOptions.series = series;	
		$('#chartTypeId').val('line');
		newHighcharts('line');
	}
	
	function newHighcharts(type){
		$('#chartShowId').show();
		highchartsOptions.chart.type = type;
		new Highcharts.Chart(highchartsOptions);
	}
	
});
</script>
</head>
<body>
	<div>
		<select id="bgqbSelectId">
			<option value="1">年度</option>
			<option value="3">季度</option>
			<option value="4">月度</option>
		</select>
	</div>
	<div id="zTreeId" class="ztree" style="float: left; width: 250px;"></div>
	<div>
		<input id="showChartId" type="button" value="出图">
	</div>
	<div style="float: left; width: 950px;">
		<table id="example" class="display" cellspacing="0" width="100%">
		</table>
	</div>
	<div style="float:right;" id="chartShowId">
		<select id="chartTypeId">
			<option value="line">折线图</option>
			<option value="column">柱状图</option>
		</select>
		<div id="container" style="min-width:800px;height:400px;"></div>
	</div>
</body>
</html>