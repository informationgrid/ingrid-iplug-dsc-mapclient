<%@ include file="/WEB-INF/jsp/base/include.jsp" %>
<c:choose>
    <c:when test="${plugdescriptionExists == 'false'}">
        <li
        <c:if test="${active == 'extras'}">
            class="active"
        </c:if>
        >Weitere Einstellungen</li>
    </c:when>
    <c:when test="${active != 'extras'}">
        <li><a href="../base/extras.html">Weitere Einstellungen</a></li>
    </c:when>
    <c:otherwise>
        <li class="active">Weitere Einstellungen</li>
    </c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${plugdescriptionExists == 'false'}">
        <li
        <c:if test="${active == 'wmsConfigs'}">
            class="active"
        </c:if>
        >WMS Einstellungen</li>
    </c:when>
    <c:when test="${active != 'wmsConfigs'}">
        <li><a href="../iplug-pages/wmsConfigs.html">WMS Einstellungen</a></li>
    </c:when>
    <c:otherwise>
        <li class="active">WMS Einstellungen</li>
    </c:otherwise>
</c:choose>



