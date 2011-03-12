<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<h1><fmt:message key="about.title" /></h1>
<h2><fmt:message key="about.caption" /></h2>
<table>
	<tbody>
		<c:if test="${not empty versionInfo.versionProperties}">
			<c:forEach var="versionProp" items="${versionInfo.versionProperties}">
				<tr>
					<td class="name">${versionProp.key}</td>
					<td class="value">${versionProp.value}</td>
				</tr>
			</c:forEach>
		</c:if>
	</tbody>
</table>


