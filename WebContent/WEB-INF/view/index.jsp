<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html style="width:100%;height:100%">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>バルスってつぶやいた人</title>
<style type="text/css">
.arrow_box {
	display: none;
	position: absolute;
	padding: 16px;
	-webkit-border-radius: 8px;
	-moz-border-radius: 8px;
	border-radius: 8px;
	background: #333;
	color: #fff;
	z-index: 99
}
</style>
</head>
<script type="text/javascript"
	src="${f:url('scripts/jquery-1.9.1.min.js') }"></script>
<script type="text/javascript">
	var startIndex = 0;
	var area;
	function loadBaros() {
		var jxr = $.getJSON("${f:url('/listen')}", {
			startIndex : startIndex
		}, function(data) {
			for ( var i in data) {
				var status = data[i];
				var img = document.createElement("img");
				img.src = status.imageUrl;
				img.alt = status.text;
				img.style.width = "30px";
				img.style.height = "30px";
				img.style.top = Math.floor(Math.random() * 96) + "%";
				img.style.left = Math.floor(Math.random() * 96) + "%";
				img.style.position = "absolute";
				var p = document.createElement("p");
				p.innerHTML = status.text;
				p.className = "arrow_box";
				area.appendChild(img);
				area.appendChild(p);
				$(img).hover(function() {
					$(this).next('p').show();
				}, function() {
					$(this).next('p').hide();
				});
				if (startIndex < status.id) {
					statusIndex = status.id;
				}
			}
		});
		jxr.complete(loadBaros);
	}
	$(function() {
		area = document.getElementById("area");
		loadBaros();
	});
</script>

<body style="width: 100%; height: 100%">

	<div id="area" style="width: 100%; height: 100%; position: relative;">
	</div>
</body>
</html>