<%@ page import="vanity.crawler.spider.CrawlerExecutionSynchronizer;" %>
<html>
<head>
    <title></title>
    <meta name="layout" content="crawler"/>
    <r:require module="manage"/>
</head>

<body>
<g:if test="${flash}">
    <div class="alert alert-success">
        <g:message code="${flash.message}"/>
    </div>
</g:if>

<table class="table table-striped">
    <tr>
        <th>
            #
        </th>
        <th>
            <g:message code="crawler.source.name"/>
        </th>
        <th style="width: 150px;">
        </th>
    </tr>
    <g:each in="${crawlers}" var="crawler" status="i">
        <tr>
            <td>
                <%=i + 1%>
            </td>
            <td>
                ${crawler.source.target}
            </td>
            <td class="text-right">
                <g:if test="${crawler.source.disabled}">
                    <g:link action="enableCrawler" id="${crawler.source.id}" class="btn btn-success btn-small">
                        <g:message code="crawler.enable"/>
                    </g:link>

                    <button class="btn btn-warning btn-small" disabled="disabled">
                        <g:message code="crawler.start"/>
                    </button>
                </g:if>
                <g:else>
                    <g:link action="disableCrawler" id="${crawler.source.id}" class="btn btn-danger btn-small">
                        <g:message code="crawler.disable"/>
                    </g:link>
                    <g:if test="${crawler.status == CrawlerExecutionSynchronizer.Status.WORKING}">
                        <g:link action="stopJob" id="${crawler.source.id}" class="btn btn-danger btn-small">
                            <g:message code="crawler.stop"/>
                        </g:link>
                    </g:if>
                    <g:elseif test="${crawler.status == CrawlerExecutionSynchronizer.Status.STOPPING}">
                        <button class="btn btn-warning btn-small" disabled="disabled">
                            <g:message code="crawler.stopping"/>
                        </button>
                    </g:elseif>
                    <g:else>
                        <g:link action="startJob" id="${crawler.source.id}" class="btn btn-success btn-small">
                            <g:message code="crawler.start"/>
                        </g:link>
                    </g:else>
                </g:else>
            </td>
        </tr>
    </g:each>
</table>
</body>
</html>