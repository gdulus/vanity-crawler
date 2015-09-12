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
<canvas id="stats" style="width: 100%; height: 400px"></canvas>
<v:contentSourceColorLegent/>
<g:javascript>

    var data = {
        labels: [<g:each in="${stats.days}" var="day">'${day.format('dd-MM-YYYY')}',</g:each>],
        datasets: [
    <g:each in="${stats.values}" var="source">
        {
            label: "${source.key}",
                    fillColor: "${v.contentSourceColor(target: source.key)}",
                    strokeColor: "${v.contentSourceColor(target: source.key)}",
                    highlightFill: "${v.contentSourceColor(target: source.key)}",
                    highlightStroke: "${v.contentSourceColor(target: source.key)}",
                    data: [<g:each in="${source.value}" var="count">${count},</g:each>]
                },
    </g:each>
    ]
};

var ctx = document.getElementById("stats").getContext("2d");
new Chart(ctx).Bar(data, {barShowStroke: false});
</g:javascript>
</body>
</html>