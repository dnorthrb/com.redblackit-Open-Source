<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<style type="text/css" media="screen">
	    @import url("<c:url value="/styles/css-framework/css/tools.css" />");
	    @import url("<c:url value="/styles/css-framework/css/typo.css" />");
	    @import url("<c:url value="/styles/css-framework/css/layout-navleft-1col.css" />");
	    @import url("<c:url value="/styles/css-framework/css/layout.css" />");
	    @import url("<c:url value="/styles/css-framework/css/nav-vertical.css" />");
	    @import url("<c:url value="/styles/redblack.css" />");
	</style>
	<title><fmt:message><tiles:insertAttribute name="title"/></fmt:message></title>
</head>
<body>
	<div id="page">
		<div id="header" class="clearfix">
			<div id="branding">
				<img src="<c:url value="/images/logo_redblack_780x89_purered.gif"/>" />
			</div>
		</div>
		<div id="content" class="clearfix">
			<div id="main">
				<tiles:insertAttribute name="main" />
			</div>
			<div id="nav">
				<tiles:importAttribute name="navigationTab" />
				<ul class="clearfix">
					<c:if test="${navigationTab != 'login'}">
						<li>
							<c:if test="${navigationTab eq 'home'}">
								<strong>
									<a href="<c:url value="/"/>">
										<fmt:message key="navigate.home"/>
									</a>
								</strong>
							</c:if>
							<c:if test="${navigationTab != 'home'}">
								<a href="<c:url value="/"/>">
									<fmt:message key="navigate.home"/>
								</a>
							</c:if>
						</li>
						<li>
							<c:if test="${navigationTab eq 'about'}">
								<strong>
									<a href="<c:url value="/about"/>">
										<fmt:message key="navigate.about"/>
									</a>
								</strong>
							</c:if>
							<c:if test="${navigationTab != 'about'}">
								<a href="<c:url value="/about"/>">
									<fmt:message key="navigate.about"/>
								</a>
							</c:if>
						</li>
			        </c:if>
			        <security:authorize access="!isAnonymous()">
						<li>
							<a href="<c:url value="/j_spring_security_logout"/>">Logout <security:authentication property="principal.username" /></a>
						</li>
			        </security:authorize>
				</ul>
			</div>
		</div>
		<div id="footer" class="clearfix">
			<ul><li class="first"><a href="<fmt:message key="footer.url"/>"><fmt:message key="footer.url"/></a></li><li class="last"><fmt:message key="footer.message"/></li></ul>
		</div>
	</div>
</body>
</html>