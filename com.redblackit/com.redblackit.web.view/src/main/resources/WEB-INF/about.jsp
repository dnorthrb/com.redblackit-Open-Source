<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<h1><fmt:message key="about.title" /></h1>
<h2><fmt:message key="about.caption" /></h2>
<table>
	<tbody>
		<c:if test="${not empty versionInfo.versionMap}">
			<c:forEach var="versionMap" items="${versionInfo.versionMap}">
				<tr>
					<td class="name">${versionMap.key}</td>
					<td class="value">${versionMap.value}</td>
				</tr>
			</c:forEach>
		</c:if>
	</tbody>
</table>


