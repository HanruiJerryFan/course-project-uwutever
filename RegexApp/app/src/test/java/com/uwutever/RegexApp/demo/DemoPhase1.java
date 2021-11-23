package demo;

import com.uwutever.RegexApp.utils.automata.nfa.*;
import com.uwutever.RegexApp.utils.compiler.*;
import com.uwutever.RegexApp.utils.parser.ast.ASTNode;

/** Demo for back-end for Phase 1
 * @author Arkaprava Choudhury
 */
public class DemoPhase1 {
    public static void main(String[] args) {
        // Input your regex and test string here. We will integrate this with the frontend in phase 2.
        // Valid operators:
        // - Union |
        // - Zero or more *
        // - One ore more +

        demo("(a|b)cd*", "acd");
    }

    public static void demo(String regex, String demo){
        ASTNode parsedAST = new RegextoASTCompiler(regex).compile();
        System.out.print(parsedAST.prettyPrintAST());

        NFA nfa = new ASTtoNFACompiler(parsedAST).compile();
        nfa.prettyPrint();

        try {
            System.out.println(nfa.accept(demo));
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
