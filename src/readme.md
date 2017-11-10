Some tips for Netbean users to make editing the site easier

Install the netbeans plugin from
https://github.com/madflow/flow-netbeans-markdown/

`Tools->Options->Miscellaneous->Markdown->HTML Export`

Enter this

	<!DOCTYPE html>
	<html>
	<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<title>{%TITLE%}</title>
	</head>
	<body>
	{%CONTENT%}
	<script>
		
		var images = document.getElementsByTagName('img');

		for(i = 0;i < images.length; i++) {
			var image = images[i];

			image.src=image.src.replace('images/', '../resources/images/');
		}
	</script>
