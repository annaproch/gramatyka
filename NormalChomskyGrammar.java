package gramatyka;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class NormalChomskyGrammar extends ContextFreeGrammar {

    NormalChomskyGrammar(ContextFreeGrammar grammar) throws ConstructorException {
        super(grammar, false);
        if (!this.ifChomsky()) {
            throw new ConstructorException("Grammar not in Chomsky normal form");
        }
    }

    @Override
    public String typeToString() {
        if (isRegular()) {
            return "Gramatyka: regularna/Chomskiego\n";
        } else {
            return "Gramatyka: bezkontekstowa/Chomskiego\n";
        }
    }

    public NormalGreibachGrammar toGreibach() throws ConstructorException {
        ChomskyToGreibachConverter converter = new ChomskyToGreibachConverter();
        return converter.convertChomskyToGreibach();
    }

    private class ChomskyToGreibachConverter {

        private final String terminals = getTerminals();
        private String nonterminals = getNonterminals();
        private Set<Character> necessaryNonterminals;
        private Dictionary<Character,List<String>> newRules = new Hashtable<>();
        private String transformedNonterminals = "";
        private String extraNonterminals = "";
        private Set<Character> visited = new HashSet<>();
        private int lastFreeNonterminal;
        private Queue<Character> nonterminalsToAnalyse;

        /**
         * * Nonterminals names are transformed to consecutive letters, starting
         * from the end of an alphabet. Thanks to transformation new
         * nonterminals can be easily added.
         */
        private void transformNonterminalsNames() {
            Dictionary<Character,Character> newNonterminalValue = new Hashtable<>();

            for (int i = 0; i < nonterminals.length(); i++) {
                char newNonterminal = (char) ((int) 'Z' - nonterminals.length() + 1 + i);
                newNonterminalValue.put(nonterminals.charAt(i), newNonterminal);
                transformedNonterminals += newNonterminal;
            }

            for (int i = 0; i < getRules().length; i++) {
                String[] products = getRules()[i];
                List<String> newProducts = new ArrayList<>();
                for (String product : products) {
                    String newProduct = "";
                    for (int j = 0; j < product.length(); j++) {
                        if (product.charAt(j) >= 'A' && product.charAt(j) <= 'Z') {
                            newProduct += newNonterminalValue.get(product.charAt(j));
                        } else {
                            newProduct += product.charAt(j);
                        }
                    }
                    newProducts.add(newProduct);
                }
                newRules.put(transformedNonterminals.charAt(i), newProducts);
            }
        }

        /* Transforms products for a given nonterminal such that every production
         * starts with a terminal or a nonterminal which is not before given one
         * on the list of nonterminals. */
        private List<String> eliminateProductsStartingWithLessNonterminal(Character nonterminal) {
            List<String> products = newRules.get(nonterminal);
            boolean changedLastly = true;
            while (changedLastly) {
                changedLastly = false;
                List<String> newProducts = new ArrayList<>();
                List<String> productsToRemove = new ArrayList<>();
                for (String product : products) {
                    Character firstCharacter = product.charAt(0);
                    if (visited.contains(firstCharacter)) {
                        changedLastly = true;
                        productsToRemove.add(product);
                        List<String> firstCharProducts = newRules.get(firstCharacter);

                        for (String firstCharProduct : firstCharProducts) {
                            newProducts.add(firstCharProduct + product.substring(1));
                        }
                    }
                }
                products.removeAll(productsToRemove);
                products.addAll(newProducts);
            }
            return products;
        }

        /* Transforms products for a given nonterminal such that every production
         * doesn't start with that nonterminal.
         * Assumes that every production starts with a not less nonterminal or terminal. */
        private List<String> eliminateProductsStartingWithEqualNonterminal(Character nonterminal) {
            List<String> products = newRules.get(nonterminal);
            List<String> equalProducts = new ArrayList<>();
            for (int j = 0; j < products.size(); j++) {
                String product = products.get(j);
                Character firstCharacter = product.charAt(0);
                if (Objects.equals(firstCharacter, nonterminal)) {
                    equalProducts.add(product.substring(1));
                    products.remove(product);
                    j--;
                }
            }
            if (!equalProducts.isEmpty()) {
                Character newNonterminal = (char) lastFreeNonterminal--;
                extraNonterminals += newNonterminal;

                int productsSize = products.size();
                for (int j = 0; j < productsSize; j++) {
                    products.add(products.get(j) + newNonterminal);
                }

                int equalsSize = equalProducts.size();
                for (int j = 0; j < equalsSize; j++) {
                    equalProducts.add(equalProducts.get(j) + newNonterminal);
                }
                newRules.put(newNonterminal, equalProducts);
            }
            return products;
        }

        /* Transform rules such as every rule is either single terminal or
         * starts with a nonterminal. 
         * After transformation last nonterminal has all productions starting
         * with a terminal. */
        private void transformToTemporaryForm() {
            lastFreeNonterminal = (int) 'Z' - nonterminals.length();
            List<String> products;

            for (int i = 0; i < transformedNonterminals.length(); i++) {
                Character nonterminal = transformedNonterminals.charAt(i);
                products = eliminateProductsStartingWithLessNonterminal(nonterminal);
                newRules.put(nonterminal, products);
                products = eliminateProductsStartingWithEqualNonterminal(nonterminal);
                newRules.put(nonterminal, products);
                visited.add(nonterminal);
            }
        }

        /* For a given nonterminal eliminate productions not starting with a terminal.
         * Assumes that every production starts with a terminal or a greater nonterminal
         * and greater nonterminals' productions are in Greibach form. */
        private List<String> eliminateRulesStartingWithNonterminal(Character nonterminal) {
            List<String> products = newRules.get(nonterminal);
            List<String> productsToRemove = new ArrayList<>();
            List<String> productsToAdd = new ArrayList<>();
            for (String product : products) {
                if (product.charAt(0) >= 'A' && product.charAt(0) <= 'Z') {
                    productsToRemove.add(product);
                    Character firstCharacter = product.charAt(0);
                    List<String> firstCharProducts = newRules.get(firstCharacter);
                    for (String firstCharProduct : firstCharProducts) {
                        productsToAdd.add(firstCharProduct + product.substring(1));
                    }
                }
            }
            products.addAll(productsToAdd);
            products.removeAll(productsToRemove);
            return products;
        }

        private void transformToLastForm(String charactersToTransform) {
            for (int i = charactersToTransform.length() - 1; i >= 0; i--) {
                Character nonterminal = charactersToTransform.charAt(i);
                List<String> products = eliminateRulesStartingWithNonterminal(nonterminal);
                newRules.put(nonterminal, products);
            }
        }

        /* Transforms every productions to ones in Greibach normal form.
         * Assumes that productions are in temporary forms. */
        private void transformToLastForm() {
            transformToLastForm(transformedNonterminals);
            transformToLastForm(extraNonterminals);
            nonterminals = transformedNonterminals + extraNonterminals;
        }

        private void searchNecessaryNonterminals(Character nonterminal) {
            List<String> products = newRules.get(nonterminal);
            for (String product : products) {
                for (int i = 0; i < product.length(); i++) {
                    Character letter = product.charAt(i);
                    if (letter >= 'A' && letter <= 'Z') {
                        if (!necessaryNonterminals.contains(letter)) {
                            necessaryNonterminals.add(letter);
                            nonterminalsToAnalyse.add(letter);
                        }
                    }
                }
            }
        }

        private void removeUselessProductions() {
            necessaryNonterminals = new HashSet<>();
            nonterminalsToAnalyse = new LinkedList<>();
            nonterminalsToAnalyse.add(nonterminals.charAt(0));
            necessaryNonterminals.add(nonterminals.charAt(0));
            while (!nonterminalsToAnalyse.isEmpty()) {
                searchNecessaryNonterminals(nonterminalsToAnalyse.poll());
            }
            for (int i = 0; i < nonterminals.length(); i++) {
                if (!necessaryNonterminals.contains(nonterminals.charAt(i))) {
                    StringBuilder sb = new StringBuilder(nonterminals);
                    sb.deleteCharAt(i);
                    i--;
                    nonterminals = sb.toString();
                }
            }
        }

        private String[][] castRulesToTwoDimensionalArray() {
            String[][] rulesArray = new String[nonterminals.length()][];
            for (int i = 0; i < nonterminals.length(); i++) {
                Character nonterminal = nonterminals.charAt(i);
                List<String> products = newRules.get(nonterminal);
                Set<String> productsSet = new HashSet<>();
                productsSet.addAll(products);
                rulesArray[i] = productsSet.toArray(new String[0]);
            }
            return rulesArray;
        }

        public NormalGreibachGrammar convertChomskyToGreibach() throws ConstructorException {
            transformNonterminalsNames();
            transformToTemporaryForm();
            transformToLastForm();
            removeUselessProductions();

            return new NormalGreibachGrammar(terminals, nonterminals, castRulesToTwoDimensionalArray());
        }
    }

}
