<!DOCTYPE html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Vanity crawler"/></title>
		<g:layoutHead/>
        <r:require module="crawler"/>
		<r:layoutResources />
	</head>
	<body>
        <div id="wrap">
            <!-- Begin page content -->
            <div class="container">
                <g:layoutBody/>
            </div>
            <div id="push"></div>
        </div>

        <div id="footer">
            <div class="container">
                <p class="muted credit"></p>
            </div>
        </div>
        <r:layoutResources />
    </body>
</html>
