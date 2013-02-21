<%@ page import="vanity.crawler.spider.CrawlerExecutor" %>
<html>
<head>
    <title></title>
    <meta name="layout" content="crawler"/>
    <r:require module="manage"/>
</head>
<body>
    <table class="table table-striped">
        <tr>
            <th>
                #
            </th>
            <th>
                <g:message code="crawler.source.name" />
            </th>
            <th style="width: 100px;">

            </th>
        </tr>
        <g:if test="${flash}" >
            <div class="alert alert-success">
                <g:message code="${flash.message}" />
            </div>
        </g:if>

        <g:each in="${crawlers}" var="crawler" status="i">
            <tr>
                <td>
                    <%= i + 1 %>
                </td>
                <td>
                    <g:message code="${crawler.source.name()}" />
                </td>
                <td class="text-right">
                    <g:if test="${crawler.status == CrawlerExecutor.Status.WORKING}">
                        <g:link action="stopJob" params="${[source:crawler.source]}" class="btn btn-danger btn-small">
                            <g:message code="crawler.stop" />
                        </g:link>
                    </g:if>
                    <g:elseif test="${crawler.status == CrawlerExecutor.Status.STOPPING}">
                        <button class="btn btn-warning btn-small" disabled="disabled">
                            <g:message code="crawler.stopping" />
                        </button>
                    </g:elseif>
                    <g:else>
                        <g:link action="startJob" params="${[source:crawler.source]}" class="btn btn-success btn-small">
                            <g:message code="crawler.start" />
                        </g:link>
                    </g:else>
                </td>
            </tr>
        </g:each>
    </table>
</body>
</html>