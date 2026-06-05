package com.tgod.training_analytics.adapters.main;

import com.tgod.training_analytics.domain.activities.exception.ActivitiesFetchException;
import com.tgod.training_analytics.domain.activities.exception.InvalidOAuthStateException;
import com.tgod.training_analytics.domain.activities.exception.TokenExchangeException;
import com.tgod.training_analytics.domain.activities.exception.TokenNotFoundException;
import com.tgod.training_analytics.domain.analysis.exception.LLMException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(TokenNotFoundException.class)
    ProblemDetail handleTokenNotFound(TokenNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidOAuthStateException.class)
    ProblemDetail handleInvalidOAuthState(InvalidOAuthStateException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({ActivitiesFetchException.class, TokenExchangeException.class, LLMException.class})
    ProblemDetail handleUpstreamFailure(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }
}
