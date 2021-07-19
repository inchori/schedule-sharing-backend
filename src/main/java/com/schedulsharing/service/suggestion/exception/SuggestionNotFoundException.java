package com.schedulsharing.service.suggestion.exception;

import com.schedulsharing.excpetion.EntityNotFoundException;

public class SuggestionNotFoundException extends EntityNotFoundException {


    public SuggestionNotFoundException() {
        super("존재하지 않는 Suggestion 입니다.");
    }
}
