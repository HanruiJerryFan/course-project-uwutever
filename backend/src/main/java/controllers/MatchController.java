package controllers;

import automata.Matcher;
import compiler.RegexDFAPattern;
import compiler.RegexNFAPattern;
import compiler.RegexPattern;
import errors.RegexException;

/**
 * This class construct a regexPattern
 * and perform matching with a given test string
 */
public class MatchController {
    private final RegexPattern regexPattern;

    public MatchController(String regexString, boolean compileDFA) throws RegexException {
        if(compileDFA){
            this.regexPattern = new RegexDFAPattern(regexString);
        } else{
            this.regexPattern = new RegexNFAPattern(regexString);
        }
    }

    public boolean match(String text){
        return Matcher.match(this.regexPattern.getFSA(), text);
    }
}
