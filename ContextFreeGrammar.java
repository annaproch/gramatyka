package gramatyka;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ContextFreeGrammar {

    private final String terminals;
    private final String nonterminals;
    private final String[][] rules;
    private final boolean isRegular;

    private ContextFreeGrammar(String terminals, String nonterminals, String[][] rules, boolean isRegular) throws ConstructorException {
        this.terminals = terminals;
        this.nonterminals = nonterminals;
        this.rules = rules;
        this.isRegular = isRegular;
        checkIfContextFree();
    }

    protected ContextFreeGrammar(ContextFreeGrammar grammar, boolean isRegular) throws ConstructorException {
        this(grammar.getTerminals(), grammar.getNonterminals(), grammar.getRules(), isRegular);
    }

    ContextFreeGrammar(String terminals, String nonterminals, String[][] rules) throws ConstructorException {
        this(terminals, nonterminals, rules, false);
    }

    public String descriptionToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Terminale: ").append(terminals).append("\n");
        sb.append("Nieterminale: ").append(nonterminals).append("\n");
        sb.append("Produkcje\n");
        for (int i = 0; i < nonterminals.length(); i++) {
            for (String product : rules[i]) {
                if (product.equals("")) {
                    product = "&";
                }
                sb.append(nonterminals.charAt(i)).append(" -> ").append(product).append("\n");
            }
        }
        return sb.toString();
    }

    public String typeToString() {
        if (isRegular) {
            return "Gramatyka: regularna/&\n";
        } else {
            return "Gramatyka: bezkontekstowa/&\n";
        }
    }

    @Override
    public String toString() {
        return typeToString() + descriptionToString();
    }

    /*
     * * Constructor of regular grammar from a context-free grammar. **
     */
    public static ContextFreeGrammar regularGrammar(ContextFreeGrammar grammar) throws ConstructorException {
        if (!grammar.ifRegular()) {
            throw new ConstructorException("Grammar not regular");
        }
        return new ContextFreeGrammar(grammar, true);
    }

    public String getTerminals() {
        return terminals;
    }

    public String getNonterminals() {
        return nonterminals;
    }

    public String[][] getRules() {
        String[][] newArray = new String[rules.length][];
        for (int i = 0; i < rules.length; i++) {
            newArray[i] = Arrays.copyOf(rules[i], rules[i].length);
        }
        return newArray;
    }

    public boolean isRegular() {
        return isRegular;
    }

    public boolean isTerminal(Character c) {
        return terminals.contains("" + c);
    }

    public boolean isNonterminal(Character c) {
        return nonterminals.contains("" + c);
    }

    public boolean isInAlphabet(Character c) {
        return isTerminal(c) || isNonterminal(c);
    }

    /**
     * * Throws ConstructorException if parameters describe a grammar with a
     * useless nonterminal.
     *
     * Nonterminal is not useless if there exists a word in grammar created
     * with a rule containing this nonterminal.
     *
     * Starting with a set of all nonterminals, not useless ones are removed
     * from set. **
     */
    private void checkUselessNonterminal(String nonterminals, String[][] rules) throws ConstructorException {
        boolean checkAgain = true;
        Set<Character> uselessNonterminals = new HashSet<>();
        Set<Integer> uselessNonterminalsIndexes = new HashSet<>();

        for (int i = 0; i < nonterminals.length(); i++) {
            uselessNonterminalsIndexes.add(i);
            uselessNonterminals.add(nonterminals.charAt(i));
        }
        while (checkAgain) {
            int indexToRemove = -1;
            checkAgain = false;
            Iterator iterator = uselessNonterminalsIndexes.iterator();
            while (iterator.hasNext() && !checkAgain) {
                int index = (int) iterator.next();
                /* If there exists a rule for a nonterminal not containing useless
                 nonterminal it is not useless. */
                for (String product : rules[index]) {
                    if (!StringUtils.stringContainsCharFromSet(product, uselessNonterminals)) {
                        checkAgain = true;
                        indexToRemove = index;
                    }
                }
            }
            if (indexToRemove != -1) {
                uselessNonterminals.remove(nonterminals.charAt(indexToRemove));
                uselessNonterminalsIndexes.remove(indexToRemove);
            }
        }
        if (!uselessNonterminals.isEmpty()) {
            throw new ConstructorException("Useless nonterminal");
        }
    }

    /*
     * * Throws ConstructorException if parameters don't describe correct
     * context-free grammar. **
     */
    public void checkIfContextFree() throws ConstructorException {
        if (!StringUtils.allCharactersLowerCase(terminals) || !StringUtils.allCharactersUnique(terminals)) {
            throw new ConstructorException("Wrong terminals");
        }
        if (!StringUtils.allCharactersUpperCase(nonterminals) || !StringUtils.allCharactersUnique(nonterminals)) {
            throw new ConstructorException("Wrong nonterminals");
        }
        if (rules.length != nonterminals.length()) {
            throw new ConstructorException("Wrong number of productions");
        }

        for (String[] products : rules) {
            if (products.length == 0) {
                throw new ConstructorException("Useless nonterminal");
            }
            for (String product : products) {
                for (int i = 0; i < product.length(); i++) {
                    if (!isInAlphabet(product.charAt(i))) {
                        throw new ConstructorException("Non-existing character in a production");
                    }
                }
            }
        }
        checkUselessNonterminal(nonterminals, rules);
    }

    /**
     * * Type of regular grammar: not set, left linear, right linear. **
     */
    private enum RegularType {

        NONE, LEFT, RIGHT
    }

    public boolean ifRegular() {
        RegularType type = RegularType.NONE;
        for (String[] products : rules) {
            for (String product : products) {
                if (product.length() > 2)
                    return false;
                if (product.length() == 1 && !isTerminal(product.charAt(0)))
                    return false;
                if (product.length() == 2) {
                    if (isTerminal(product.charAt(0)) && isNonterminal(product.charAt(1))) {
                        if (type == RegularType.LEFT) return false;
                        type = RegularType.RIGHT;
                    }
                    if (isTerminal(product.charAt(1)) && isNonterminal(product.charAt(0))) {
                        if (type == RegularType.RIGHT) return false;
                        type = RegularType.LEFT;
                    }
                }
            }
        }
        return true;
    }

    public boolean ifChomsky() {
        for (String[] products : rules) {
            for (String product : products) {
                if (product.length() == 0 || product.length() > 2)
                    return false;
                if (product.length() == 1 && !isTerminal(product.charAt(0)))
                    return false;
                if (product.length() == 2 && (!isNonterminal(product.charAt(0)) || !isNonterminal(product.charAt(1))))
                    return false;
            }
        }
        return true;
    }

    public boolean ifGreibach() {
        for (String[] products : rules) {
            for (String product : products) {
                if (product.length() == 1 && isTerminal(product.charAt(0))) {
                    continue;
                }
                if (product.length() > 1 && isTerminal(product.charAt(0))
                        && StringUtils.allCharactersUpperCase(product.substring(1))) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

}
