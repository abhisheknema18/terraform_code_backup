package com.amtsybex.fieldreach.fdm.web.jsf.util;

import java.util.Map;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;
import org.primefaces.application.exceptionhandler.PrimeExceptionHandler;
import org.primefaces.application.exceptionhandler.PrimeExceptionHandlerFactory;

public class ExtendedPrimeExceptionHandlerFactory extends PrimeExceptionHandlerFactory {
    private static final String ERROR_PAGE = "../errors/error.xhtml";

    public ExtendedPrimeExceptionHandlerFactory(final ExceptionHandlerFactory wrapped) {
        super(wrapped);
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new ExtendedPrimeExceptionHandler(getWrapped().getExceptionHandler());
    }

    private static class ExtendedPrimeExceptionHandler extends PrimeExceptionHandler {

        public ExtendedPrimeExceptionHandler(ExceptionHandler wrapped) {
            super(wrapped);
        }

        @Override
        protected String evaluateErrorPage(Map<String, String> errorPages, Throwable rootCause) {
            return ERROR_PAGE;
        }

    }

}