package gramatyka;

public class NormalGreibachGrammar extends ContextFreeGrammar {
    
    NormalGreibachGrammar(ContextFreeGrammar grammar) throws ConstructorException {
        super(grammar, grammar.isRegular());
        if (!this.ifGreibach())
            throw new ConstructorException("Grammar not in Greibach normal form");
    }
    
    NormalGreibachGrammar(String terminals, String nonterminals, String[][] rules) throws ConstructorException {
        super(terminals, nonterminals, rules);
        if (!this.ifGreibach())
            throw new ConstructorException("Grammar not in Greibach normal form");
    }
    
    @Override
    public String typeToString() {
        if (isRegular())
            return "Gramatyka: regularna/Greibach\n";
        else
            return "Gramatyka: bezkontekstowa/Greibach\n";
    }

}
