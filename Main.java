/***
 * Author: Anna Prochowska
 * Date: May 2015
 * 
 * Representation of context free grammar. 
 * Enables checking if grammar is regular and if is in normal Chomsky or Greibach 
 * form and converting from Chomsky to Greibach form.
 */

package gramatyka;

public class Main {
    public static void main(String args[]) throws ConstructorException, Exception {
        String[][] tab =  {{"P","Q","R"},{"a","aP","aPb"},{"b", "Qb", "aQb"},{""}};
        ContextFreeGrammar grammar = new ContextFreeGrammar(
                "ab",
                "SPQR",
                tab);
        System.out.println(grammar.toString());
        
        String[][] tab1 =  {{"a"},{"aA","b"},{"bC"},{"", "cC"}};
        ContextFreeGrammar grammar1 = new ContextFreeGrammar(
                "abc",
                "SABC",
                tab1);
        assert(grammar1.ifRegular() == true);
        ContextFreeGrammar regular = ContextFreeGrammar.regularGrammar(grammar1);
        System.out.println(regular.toString());
        
        String[][] tab2 =  {{"a"},{"aA","b"},{"bC"},{"", "Cc"}};
        ContextFreeGrammar grammar2 = new ContextFreeGrammar(
                "abc",
                "SABC",
                tab2);
        assert(grammar2.ifRegular() == false);
  
        String[][] tab3 =  {{"AB"},{"AB", "CB", "a"},{"AB", "b"},{"AC", "c"}};
        ContextFreeGrammar grammar3 = new ContextFreeGrammar(
                "abc",
                "SABC",
                tab3);
        assert(grammar3.ifChomsky() == true);
        NormalChomskyGrammar chomskyGram = new NormalChomskyGrammar(grammar3);
        ContextFreeGrammar greibachGram = chomskyGram.toGreibach();
        assert(greibachGram.ifGreibach());
        System.out.println(greibachGram.toString());
        
        String[][] tab4 =  {{"BC"},{"CA", "b"},{"AB", "a"}};
        ContextFreeGrammar ChToG2 = new ContextFreeGrammar(
                "ab",
                "ABC",
                tab4);
        assert(ChToG2.ifChomsky());
        NormalChomskyGrammar chomskyG2 = new NormalChomskyGrammar(ChToG2);
        ContextFreeGrammar greibach2 = chomskyG2.toGreibach();
        assert(greibach2.ifGreibach());
        System.out.println(greibach2.toString());
        
        String[][] tab5 =  {{"BC"},{"DA", "a"}, {"b"}, {"a"}};
        ContextFreeGrammar ChToG3 = new ContextFreeGrammar(
                "ab",
                "ABCD",
                tab5);
        NormalChomskyGrammar chomskyG3 = new NormalChomskyGrammar(ChToG3);
        ContextFreeGrammar greibach3 = chomskyG3.toGreibach();
        assert(greibach3.ifGreibach());
        System.out.println(greibach3.toString());   
        String[][] tab6 =  {{"a","AA"}};
        ContextFreeGrammar grammar4 = new ContextFreeGrammar(
                "a",
                "A",
                tab6);
        NormalChomskyGrammar chomskyG4 = new NormalChomskyGrammar(grammar4);
        ContextFreeGrammar greibach4 = chomskyG4.toGreibach();
        System.out.println(greibach4.toString());
    }  
}

