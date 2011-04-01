<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${not empty login_error}">
	<div id="errors" class="error">
		<p><fmt:message key="${login_error}"/>: ${SPRING_SECURITY_LAST_EXCEPTION.message}</p>
	</div>	
</c:if>

<form action="<c:url value='/j_spring_security_check'/>" method="post">
	<fieldset>
		<legend><fmt:message key="login.hint"/></legend>
		<ol>
			<li>
				<label for="j_username"><fmt:message key="login.user"/></label>
				<input type='text' id='j_username' name='j_username' value='<c:out value="${user}"/>'/>
			</li>
			<li>
				<label for="j_password"><fmt:message key="login.password"/></label>
				<input type='password' id='j_password' name='j_password'/>
			</li>
		</ol>
		<button type="submit"><fmt:message key="login.submit"/></button>
	</fieldset>
</form>
