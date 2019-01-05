<html>
<body>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%> 
<%@ page contentType="text/html;charset=utf-8"%> 

<h2>Hello World!</h2>

<h2>spring mvc 文件上传</h2>
<form name="form_upload" action="/mmall/manage/product/uploadFile.do" method="post" enctype="multipart/form-data">
	<input type="file" name="upload_file"/>
	<input type="submit" value="spring mvc 文件上传"/>
</form>

<h2>富文本文件上传</h2>
<form name="form_upload" action="/mmall/manage/product/uploadRichTextFile.do" method="post" enctype="multipart/form-data">
	<input type="file" name="upload_file"/>
	<input type="submit" value="富文本文件上传"/>
</form>

</body>
</html>
