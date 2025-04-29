/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

/**
 *
 * @author benja
 */
public class ISBNValidator {
    
    public static boolean isValidISBN(String isbn) {
        String cleanISBN = cleanISBN(isbn);
        return isValidISBN10(cleanISBN) || isValidISBN13(cleanISBN);
    }

    public static String cleanISBN(String isbn) {
        return isbn.replaceAll("[\\-\\s]", "").toUpperCase();
    }

    private static boolean isValidISBN10(String isbn) {
        if (isbn.length() != 10) return false;
        
        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(isbn.charAt(i)) * (10 - i);
            }
            
            char lastChar = isbn.charAt(9);
            sum += (lastChar == 'X') ? 10 : Character.getNumericValue(lastChar);
            
            return sum % 11 == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isValidISBN13(String isbn) {
        if (isbn.length() != 13) return false;
        
        try {
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                sum += Character.getNumericValue(isbn.charAt(i)) * ((i % 2 == 0) ? 1 : 3);
            }
            
            int checkDigit = Character.getNumericValue(isbn.charAt(12));
            return checkDigit == (10 - (sum % 10)) % 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
     public static String getCleanISBN(String isbn) {
        return isbn.replaceAll("[\\-\\s]", "").toUpperCase();
    }
}
