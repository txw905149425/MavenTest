http://wenshu.court.gov.cn/List/List?sorttype=1&conditions=searchWord+2+AJLX++%E6%A1%88%E4%BB%B6%E7%B1%BB%E5%9E%8B:%E6%B0%91%E4%BA%8B%E6%A1%88%E4%BB%B6
	//处理URL(排序:sorttype,关键字:searchWord,案由:reason,文书类型:caseType,法院:court,裁判年份:trialYear,裁判日期:trialDate,审理程序:trialRound,文书性质:judgeType)
	//type:null(点击刑事民事等进入), search(点击搜索按钮)，treesearch(分类树点击),tagsearch(点击标签删除一个标签),maxsearch(高级检索),sorttype(排序1:相关性,2:法院层级,3:审理程序,4:裁判日期)
	//arr :date[0] = "type"; date[1] = "key";date[2] = "value";;date[3] = "show";

	//var surl = "http://localhost:8524/List/List?sorttype=1&conditions=caseType+1++民事+案件类型:民事&trialYear+1++裁判年份+裁判年份:2014-01-10";
	//var ens = encodeURI(surl);
	function Param(type, arr) {
	    var host = window.location.host;
	    var hostname = window.location.hostname;
	    var search = window.location.search;
	    var pathname = window.location.pathname;
	    search = decodeURI(search);
	    var params = new Array();
	    if (typeof type != "undefined" && (type == "remove" || type == "treesearch" || type == "removeContent")) {
	        params = getParamsFromUrl(search);
	    }

	    params = Lawyee.Tools.MergeArray(
	                            { arr1: params,
	                                arr2: arr,
	                                compare: function (i, j) {
	                                    if (typeof this.arr1[i].condition != "undefined") {
	                                        if (this.arr1[i].condition.split(":")[0] == this.arr2[j].condition.split(":")[0] && this.arr1[i].condition.split(":")[0] != "关键词") {
	                                            this.arr1[i].condition = this.arr2[j].condition;
	                                        }
	                                        return this.arr1[i].condition == this.arr2[j].condition;
	                                    }
	                                    else {
	                                        return true;
	                                    }
	                                }
	                            });
	    if (typeof type != "undefined" && type == "remove") {
	        for (var i = 0; i < params.length; i++) {
	            for (var j = 0; j < arr.length; j++) {
	                var parmsValue = params[i].condition.split(":");
	                var removeValue = arr[j].condition.split(":");
	                //edit by zhs 151205 对关键词特殊处理，键和值都相同时，将条件去除
	                if (removeValue[0] == "关键词") {
	                    if (removeValue[0] == parmsValue[0] && removeValue[1] == parmsValue[1]) {
	                        params.splice(i, 1);
	                    }
	                } else {
	                    if (parmsValue[0] == removeValue[0]) {
	                        params.splice(i, 1);
	                    }
	                }
	            }
	        }
	    }
	    var condition = "";
	    for (var k = 0; k < params.length; k++) {
	        var val = params[k];
	        if (val.condition.split(':')[1] != "") {
	            condition += "&conditions=" + val.type + "+" + val.value + "+" + val.sign + "+" + val.pid + "+" + val.condition;
	        }
	    }
	    var url = "http://" + host + "/list/list/";
	    var sortType = Lawyee.Tools.QueryString("sorttype");
	    if (sortType != "" && sortType != null) {
	        url += "?sorttype=" + sortType;
	    }
	    else {
	        url += "?sorttype=1";
	    }
	    url += condition;
	    window.location.href = url;
	}

//从URL当中获取参数，无其他意义，仅为了代码可读性改造
function getParamsFromUrl(url) {
    return manageUrl(url);
}