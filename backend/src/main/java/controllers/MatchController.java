package controllers;

import automata.Matcher;
import compiler.RegexDFAPattern;
import compiler.RegexNFAPattern;
import compiler.RegexPattern;

public class MatchController {
    private final RegexPattern regexPattern;

    public MatchController(String regexString, boolean compileDFA){
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
