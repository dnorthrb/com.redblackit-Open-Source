package com.redblackit.web.server.mvc;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;

/**
 * Class handling UrlChildLocation. Normally, this return type will cause the LOCATION header in the response to be set
 * to its location URL. However, this is overridden if the return type is annotated with either
 * <code>@ResponseBody</code> or <code>@ModelAttribute</code>. In these cases, the return object is left as it is, for
 * processing by the return value handler corresponding to the annotation.
 *
 * @author Dominic North
 */
public class UrlChildLocationReturnValueHandler implements HandlerMethodReturnValueHandler {

    /**
     * Check not @ResponseBody and not @ModelAttribute, then check if return type is UrlChildLocation.
     *
     * @param returnTypeMethodParameter
     * @return true or false
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnTypeMethodParameter) {
        return (returnTypeMethodParameter.getMethodAnnotation(ResponseBody.class) == null &&
                returnTypeMethodParameter.getMethodAnnotation(ModelAttribute.class) == null &&
                UrlChildLocation.class.isAssignableFrom(returnTypeMethodParameter.getParameterType()));
    }

    /**
     * Having established return value is not null, and we do have a UrlChildLocation object, set response location
     * header from its child URL.
     *
     * @param returnValue
     * @param returnTypeMethodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @throws Exception
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnTypeMethodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {

        if (supportsReturnType(returnTypeMethodParameter) && returnValue != null) {
            UrlChildLocation urlChildLocation = (UrlChildLocation) returnValue;
            HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
            response.setHeader("LOCATION", urlChildLocation.getLocationUrl());
            modelAndViewContainer.setRequestHandled(true);
        }
    }
}
