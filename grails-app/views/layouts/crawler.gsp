<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title><g:layoutTitle default="${g.message(code: 'vanity.crawler.title')}"/></title>
        <g:layoutHead/>
        <r:layoutResources />
    </head>
    <body>
        <div class="navbar navbar-inverse navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container-fluid">
                    <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
            </button>
                    <a class="brand" href="#"><g:message code="vanity.crawler.title"/></a>

                    <div class="nav-collapse collapse">
                <p class="navbar-text pull-right">
                    <g:link controller="logout" class="btn btn-small btn-danger btn-logout"><i class="icon-off icon-white"></i></g:link>
                </p>
                <ul class="nav">
                    <li class="active"><a href="#"><g:message code="vanity.crawler.crawlers"/></a></li>
                </ul>
            </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>

    <div class="container-fluid">
        <div class="row-fluid">
            <div class="span3">
                <div class="well sidebar-nav">
                    <ul class="nav nav-list">
                        <li class="nav-header"><g:message code="vanity.crawler.crawlers"/></li>
                        <li class="active"><a href="#"><g:message code="vanity.crawler.list" /></a></li>
                </ul>
            </div><!--/.well -->
        </div><!--/span-->
        <div class="span9">
            <g:layoutBody/>
        </div><!--/span-->
        </div><!--/row-->
    </div><!--/.fluid-container-->

    <r:layoutResources />
    </body>
</html>

