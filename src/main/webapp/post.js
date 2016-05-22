function load()
{
	var data = new FormData();
	data.append("file", $("#file")[0].files[0]);
	data.append("documentName", $("#documentName")[0].value)
	data.append("link", $("#link")[0].value)

	$.ajax
	(
			{
				type: "POST",
				url: "/uploadAudioFile",
				datatype: "json",
				cache: false,
			    contentType: false,
			    processData: false,
			    enctype: "multipart/form-data",
			    data: data,
				success: function(data)
				{
					alert(data);
					
				},
				error: function()
				{
					alert("error");
					
				},
			}
	);
	
}

