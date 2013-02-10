<html>
<head>
    <title></title>
    <meta name="layout" content="crawler"/>
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
            <th>

            </th>
        </tr>
        <g:if test="${flash}" >
            <div class="alert alert-success">
                <g:message code="${flash.message}" />
            </div>
        </g:if>

        <g:each in="${sources}" var="source" status="i">
            <tr>
                <td>
                    <%= i + 1 %>
                </td>
                <td>
                    <g:message code="${source.name()}" />
                </td>
                <td>
                    <g:link action="startJob" params="${[source:sources]}" class="btn btn-success">
                        <g:message code="crawler.start" />
                    </g:link>
                </td>
            </tr>
        </g:each>
    </table>
</body>
</html>